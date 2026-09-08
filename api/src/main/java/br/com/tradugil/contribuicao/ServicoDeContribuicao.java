package br.com.tradugil.contribuicao;

import br.com.tradugil.comum.erro.RecursoNaoEncontradoException;
import br.com.tradugil.comum.erro.RegraDeNegocioException;
import br.com.tradugil.contribuicao.Contribuicao.StatusDeContribuicao;
import br.com.tradugil.contribuicao.ContribuicaoDtos.ContribuicaoResposta;
import br.com.tradugil.contribuicao.ContribuicaoDtos.DecisaoDeModeracao;
import br.com.tradugil.contribuicao.ContribuicaoDtos.ItemDaFila;
import br.com.tradugil.contribuicao.ContribuicaoDtos.NovaContribuicao;
import br.com.tradugil.dicionario.Categoria;
import br.com.tradugil.dicionario.Definicao;
import br.com.tradugil.dicionario.Fonte;
import br.com.tradugil.dicionario.Fonte.TipoDeFonte;
import br.com.tradugil.dicionario.Giria;
import br.com.tradugil.dicionario.Idioma;
import br.com.tradugil.dicionario.RepositorioDeCategoria;
import br.com.tradugil.dicionario.RepositorioDeDefinicao;
import br.com.tradugil.dicionario.RepositorioDeFonte;
import br.com.tradugil.dicionario.RepositorioDeGiria;
import br.com.tradugil.dicionario.RepositorioDeIdioma;
import br.com.tradugil.identidade.RepositorioDeUsuario;
import br.com.tradugil.identidade.Usuario;
import br.com.tradugil.traducao.Normalizador;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ServicoDeContribuicao {

    /**
     * Quantas propostas pendentes uma mesma pessoa pode ter em aberto.
     *
     * <p>Sem teto, uma conta só enche a fila de moderação e afoga as
     * contribuições de todo mundo. O limite é por pendências, não por total:
     * quem contribui bem e tem tudo aprovado nunca esbarra nele.</p>
     */
    private static final int MAXIMO_DE_PENDENTES_POR_USUARIO = 10;

    private final RepositorioDeContribuicao repositorio;
    private final RepositorioDeAuditoria auditoria;
    private final RepositorioDeUsuario repositorioDeUsuario;
    private final RepositorioDeIdioma repositorioDeIdioma;
    private final RepositorioDeGiria repositorioDeGiria;
    private final RepositorioDeDefinicao repositorioDeDefinicao;
    private final RepositorioDeFonte repositorioDeFonte;
    private final RepositorioDeCategoria repositorioDeCategoria;
    private final org.springframework.cache.CacheManager gerenciadorDeCache;

    public ServicoDeContribuicao(RepositorioDeContribuicao repositorio,
                                 RepositorioDeAuditoria auditoria,
                                 RepositorioDeUsuario repositorioDeUsuario,
                                 RepositorioDeIdioma repositorioDeIdioma,
                                 RepositorioDeGiria repositorioDeGiria,
                                 RepositorioDeDefinicao repositorioDeDefinicao,
                                 RepositorioDeFonte repositorioDeFonte,
                                 RepositorioDeCategoria repositorioDeCategoria,
                                 org.springframework.cache.CacheManager gerenciadorDeCache) {
        this.repositorio = repositorio;
        this.auditoria = auditoria;
        this.repositorioDeUsuario = repositorioDeUsuario;
        this.repositorioDeIdioma = repositorioDeIdioma;
        this.repositorioDeGiria = repositorioDeGiria;
        this.repositorioDeDefinicao = repositorioDeDefinicao;
        this.repositorioDeFonte = repositorioDeFonte;
        this.repositorioDeCategoria = repositorioDeCategoria;
        this.gerenciadorDeCache = gerenciadorDeCache;
    }

    @Transactional
    public ContribuicaoResposta propor(Long usuarioId, NovaContribuicao nova) {
        Usuario autor = repositorioDeUsuario.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada."));

        long pendentes = repositorio.countByUsuarioIdAndStatus(
                usuarioId, StatusDeContribuicao.PENDENTE);
        if (pendentes >= MAXIMO_DE_PENDENTES_POR_USUARIO) {
            throw new RegraDeNegocioException("MUITAS_PENDENTES",
                    "Você já tem " + MAXIMO_DE_PENDENTES_POR_USUARIO + " propostas "
                            + "aguardando revisão. Espere a moderação antes de enviar mais.");
        }

        Idioma idioma = repositorioDeIdioma.findByCodigo(nova.idioma())
                .orElseThrow(() -> new RegraDeNegocioException("IDIOMA_INVALIDO",
                        "Idioma não reconhecido."));

        String termo = nova.termo().trim();
        if (Normalizador.normalizar(termo).isBlank()) {
            // Um termo só de pontuação normaliza para vazio e viraria um
            // verbete que nunca poderia ser encontrado por ninguém.
            throw new RegraDeNegocioException("TERMO_INVALIDO",
                    "O termo precisa ter ao menos uma letra ou número.");
        }

        Contribuicao salva = repositorio.save(
                new Contribuicao(autor, termo, idioma, nova.explicacaoProposta().trim()));
        return ContribuicaoResposta.de(salva);
    }

    @Transactional(readOnly = true)
    public List<ContribuicaoResposta> minhasPropostas(Long usuarioId) {
        return repositorio.findByUsuarioIdOrderByCriadoEmDesc(usuarioId).stream()
                .map(ContribuicaoResposta::de)
                .toList();
    }

    /**
     * A fila, com o que o dicionário já diz sobre cada termo proposto.
     *
     * <h2>Uma consulta, e não uma por linha</h2>
     *
     * <p>Cada item precisa saber se o termo já existe. Buscar um a um seriam
     * até cem idas ao banco para desenhar uma tela, que é a mesma forma de
     * N+1 que já custou 1,97 s numa página de catálogo. Os termos vão todos
     * num {@code IN} e o casamento acontece em memória.</p>
     *
     * <p>O casamento é por termo normalizado <b>e</b> idioma, e os dois
     * importam: "feed" existe nos dois idiomas com sentidos diferentes, e
     * casar só pelo termo mostraria ao moderador o verbete errado, que é pior
     * do que não mostrar nada.</p>
     */
    @Transactional(readOnly = true)
    public List<ItemDaFila> fila(StatusDeContribuicao status, int pagina, int tamanho) {
        StatusDeContribuicao filtro = status == null ? StatusDeContribuicao.PENDENTE : status;
        List<Contribuicao> pendentes = repositorio
                .findByStatusOrderByCriadoEm(filtro, PageRequest.of(
                        Math.max(pagina, 0), Math.min(Math.max(tamanho, 1), 100)));

        Set<String> normalizados = pendentes.stream()
                .map(c -> Normalizador.normalizar(c.getTermo()))
                .filter(t -> !t.isBlank())
                .collect(Collectors.toSet());

        Map<String, Giria> existentes = normalizados.isEmpty()
                ? Map.of()
                : repositorioDeGiria.findByTermoNormalizadoIn(normalizados).stream()
                        .collect(Collectors.toMap(
                                g -> chave(g.getTermoNormalizado(), g.getIdioma().getCodigo()),
                                Function.identity(),
                                // Termo repetido no mesmo idioma não deveria
                                // existir, mas se existir a fila não é o lugar
                                // de estourar: mostra o primeiro.
                                (primeiro, segundo) -> primeiro));

        return pendentes.stream()
                .map(c -> ItemDaFila.de(c, existentes.get(chave(
                        Normalizador.normalizar(c.getTermo()),
                        c.getIdioma().getCodigo()))))
                .toList();
    }

    private static String chave(String termoNormalizado, String codigoDoIdioma) {
        return termoNormalizado + "|" + codigoDoIdioma;
    }

    /**
     * Aprova ou rejeita uma proposta.
     *
     * <p>A decisão e a trilha de auditoria são gravadas na mesma transação, e
     * isso não é detalhe: sair daqui com a contribuição aprovada e o registro
     * de auditoria ausente produziria exatamente o estado que a mitigação de
     * Repudiation existe para impedir: conteúdo publicado sem ninguém
     * respondendo por ele.</p>
     */
    @Transactional
    public ContribuicaoResposta decidir(Long moderadorId, Long contribuicaoId,
                                        DecisaoDeModeracao decisao) {
        Usuario moderador = repositorioDeUsuario.findById(moderadorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada."));

        Contribuicao contribuicao = repositorio.findById(contribuicaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Contribuição não encontrada."));

        if (!contribuicao.estaPendente()) {
            // Sem esta checagem, duas abas abertas na fila fariam dois
            // moderadores decidirem a mesma proposta, e a segunda decisão
            // sobrescreveria a primeira em silêncio.
            throw new RegraDeNegocioException("JA_MODERADA",
                    "Esta proposta já foi revisada por alguém.");
        }

        if (decisao.aprovar()) {
            // A publicação vem ANTES de marcar como aprovada, de propósito.
            // Se a categoria for inválida ou faltar, a exceção sai daqui com a
            // contribuição ainda pendente e o moderador pode corrigir. Na
            // ordem inversa a proposta ficaria aprovada e não publicada, que é
            // o pior dos dois estados: sairia da fila sem virar verbete.
            publicar(contribuicao, decisao.categoria());
            contribuicao.aprovar(moderador);
        } else {
            if (decisao.motivo() == null || decisao.motivo().isBlank()) {
                // Rejeição sem motivo não ensina nada a quem contribuiu, e a
                // pessoa reenviaria a mesma proposta.
                throw new RegraDeNegocioException("MOTIVO_OBRIGATORIO",
                        "Explique o motivo da rejeição.");
            }
            contribuicao.rejeitar(moderador, decisao.motivo().trim());
        }

        auditoria.save(new AuditoriaDeModeracao(
                contribuicao,
                moderador,
                contribuicao.getStatus(),
                contribuicao.getMotivoRejeicao()));

        return ContribuicaoResposta.de(contribuicao);
    }

    /**
     * Leva a proposta aprovada para o dicionário.
     *
     * <h2>Termo que já existe ganha um sentido, não um verbete novo</h2>
     *
     * <p>É a mesma regra do gerador de migrações: dois verbetes para a mesma
     * palavra fazem a resposta depender da ordem das linhas no banco. Se o
     * termo já está lá, a explicação aprovada entra como mais um sentido
     * dele.</p>
     *
     * <p>E não entra se já existir uma igual. Uma proposta que repete o que o
     * dicionário já diz deveria ter sido recusada pela moderação; aprovada
     * por engano, o pior resultado possível é o verbete mostrar a mesma frase
     * duas vezes, que é exatamente o defeito que as migrações V29 e V33
     * limparam.</p>
     *
     * <h2>A prateleira deixou de ser opcional</h2>
     *
     * <p>Antes o verbete da comunidade nascia sem categoria. O efeito era
     * silencioso e ruim: ele era encontrado pela busca e pelo
     * {@code /traduzir} e <b>nunca aparecia no catálogo</b>, que é organizado
     * por prateleira e é justamente por onde chega quem não sabe o que
     * procurar. Verbete invisível para esse público é meio verbete.</p>
     *
     * <p>Adivinhar a categoria pelo texto seria classificar em nome de quem
     * contribuiu, e prateleira errada é pior que prateleira nenhuma: manda a
     * pessoa procurar no lugar errado. Quem escolhe é a moderação, ao aprovar,
     * que é o único momento em que alguém está lendo a proposta com atenção.</p>
     *
     * <p>Só é exigida quando faz falta: um sentido novo para um termo que já
     * está em duas prateleiras não precisa de uma terceira. A regra olha o
     * resultado, e não o formulário: nenhum verbete sai daqui fora do
     * catálogo.</p>
     */
    private void publicar(Contribuicao contribuicao, String slugDaCategoria) {
        Idioma idioma = contribuicao.getIdioma();
        String normalizado = Normalizador.normalizar(contribuicao.getTermo());

        Giria giria = repositorioDeGiria
                .findByTermoNormalizadoAndIdiomaCodigo(normalizado, idioma.getCodigo())
                .orElseGet(() -> repositorioDeGiria.save(
                        Giria.daComunidade(contribuicao.getTermo(), idioma)));

        aplicarPrateleira(giria, slugDaCategoria);

        String explicacao = contribuicao.getExplicacaoProposta().trim();
        boolean jaExiste = giria.getDefinicoes().stream()
                .anyMatch(d -> explicacao.equals(d.getExplicacaoSimples()));
        if (jaExiste) {
            return;
        }

        Fonte fonte = repositorioDeFonte.findFirstByTipo(TipoDeFonte.COMUNIDADE)
                .orElseThrow(() -> new IllegalStateException(
                        "Fonte COMUNIDADE ausente. Ela vem da V2 e o banco está incompleto."));

        repositorioDeDefinicao.save(Definicao.daComunidade(giria, explicacao, fonte));

        /*
         * O verbete em cache é o anterior. Sem limpar, a explicação recém
         * publicada só apareceria quando o cache vencesse, em até uma hora, e
         * quem aprovou veria a própria decisão não fazer efeito nenhum.
         */
        limpar("verbetes");
    }

    /**
     * Põe o verbete na prateleira escolhida, e recusa a aprovação se o
     * resultado ficaria fora do catálogo.
     */
    private void aplicarPrateleira(Giria giria, String slug) {
        if (slug == null || slug.isBlank()) {
            if (giria.getCategorias().isEmpty()) {
                throw new RegraDeNegocioException("CATEGORIA_OBRIGATORIA",
                        "Escolha em qual prateleira do catálogo este verbete entra. "
                                + "Sem ela, ele existe na busca e não aparece no catálogo.");
            }
            return;
        }

        Categoria categoria = repositorioDeCategoria.findBySlug(slug.trim())
                .orElseThrow(() -> new RegraDeNegocioException("CATEGORIA_INVALIDA",
                        "Essa prateleira não existe."));
        giria.entrarNaPrateleira(categoria);
        repositorioDeGiria.save(giria);

        /*
         * A contagem por categoria também está em cache, e é ela que o
         * catálogo mostra ao lado do nome da prateleira. Sem limpar, a
         * prateleira anunciaria 40 e abriria com 41.
         */
        limpar("categorias");
    }

    private void limpar(String nomeDoCache) {
        var cache = gerenciadorDeCache.getCache(nomeDoCache);
        if (cache != null) {
            cache.clear();
        }
    }
}
