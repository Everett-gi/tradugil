package br.com.tradugil.dicionario;

import br.com.tradugil.dicionario.PacoteDtos.MetadadosDoPacote;
import br.com.tradugil.dicionario.PacoteDtos.Pacote;
import br.com.tradugil.dicionario.PacoteDtos.VariacaoDoPacote;
import br.com.tradugil.dicionario.PacoteDtos.VerbeteDoPacote;
import br.com.tradugil.traducao.Normalizador;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Monta o dicionário completo para os clientes offline baixarem.
 *
 * <p>É o que faz o aplicativo <b>nascer útil</b> em vez de precisar aprender.
 * Sem isso, o dicionário do aparelho só cresce a partir das consultas que já
 * deram certo — ou seja, só funciona offline depois de já ter funcionado
 * online, que é exatamente ao contrário do que a Marlene precisa.</p>
 */
@Service
public class ServicoDePacote {

    private final RepositorioDeGiria repositorio;

    public ServicoDePacote(RepositorioDeGiria repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Metadados, para o cliente decidir se vale baixar.
     *
     * <p>Uma requisição de poucos bytes que evita um download de centenas de
     * kilobytes quando nada mudou — a diferença importa para quem tem plano
     * de dados limitado, que é parte do público.</p>
     */
    @Transactional(readOnly = true)
    public MetadadosDoPacote metadados() {
        Pacote pacote = pacoteCompleto();
        return new MetadadosDoPacote(pacote.versao(), pacote.geradoEm(), pacote.qtdVerbetes());
    }

    /**
     * O dicionário inteiro.
     *
     * <p>Em cache: o conteúdo é o mesmo para todo mundo e só muda quando a
     * curadoria mexe. Montar isso por requisição faria cada instalação nova
     * do aplicativo varrer o dicionário completo no banco.</p>
     */
    @Cacheable(cacheNames = "verbetes", key = "'pacote-completo'")
    @Transactional(readOnly = true)
    public Pacote pacoteCompleto() {
        List<Giria> girias = repositorio.findAll();

        List<VerbeteDoPacote> verbetes = girias.stream()
                .map(this::converter)
                .filter(v -> v != null)
                .sorted((a, b) -> a.termoNormalizado().compareTo(b.termoNormalizado()))
                .toList();

        // A versão é derivada do conteúdo, e não de um contador: assim ela
        // muda exatamente quando algo que o cliente guardou mudou, sem
        // ninguém precisar lembrar de incrementar nada ao editar um verbete.
        OffsetDateTime maisRecente = girias.stream()
                .flatMap(g -> g.getDefinicoes().stream())
                .map(Definicao::getAtualizadoEm)
                .filter(d -> d != null)
                .max(OffsetDateTime::compareTo)
                .orElse(OffsetDateTime.now(ZoneOffset.UTC));

        String versao = verbetes.size() + "-" + maisRecente.toInstant().toEpochMilli();

        return new Pacote(versao, maisRecente, verbetes.size(), verbetes);
    }

    /** Devolve {@code null} para verbete sem definição aprovada: não explica nada. */
    private VerbeteDoPacote converter(Giria giria) {
        Definicao definicao = giria.definicoesAprovadas().stream().findFirst().orElse(null);
        if (definicao == null) {
            return null;
        }

        List<VariacaoDoPacote> variacoes = giria.getVariacoes().stream()
                .map(v -> new VariacaoDoPacote(
                        v.getVariacaoNormalizada(), v.getVariacaoColapsada()))
                .toList();

        return new VerbeteDoPacote(
                giria.getTermo(),
                giria.getTermoNormalizado(),
                // Recalculado em Java, e não lido da coluna: se um dia a
                // regra de colapso mudar e a migração ficar para trás, o
                // pacote leva a forma certa em vez de espalhar a errada por
                // todos os aparelhos.
                Normalizador.colapsarRepeticoes(giria.getTermoNormalizado()),
                giria.getIdioma().getCodigo(),
                definicao.getExplicacaoSimples(),
                definicao.getExplicacaoDetalhada(),
                definicao.getEquivalenteFormal(),
                giria.isNsfw(),
                giria.isRiscoMenor(),
                variacoes);
    }
}
