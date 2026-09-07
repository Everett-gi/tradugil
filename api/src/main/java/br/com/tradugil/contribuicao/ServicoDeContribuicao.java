package br.com.tradugil.contribuicao;

import br.com.tradugil.comum.erro.RecursoNaoEncontradoException;
import br.com.tradugil.comum.erro.RegraDeNegocioException;
import br.com.tradugil.contribuicao.Contribuicao.StatusDeContribuicao;
import br.com.tradugil.contribuicao.ContribuicaoDtos.ContribuicaoResposta;
import br.com.tradugil.contribuicao.ContribuicaoDtos.DecisaoDeModeracao;
import br.com.tradugil.contribuicao.ContribuicaoDtos.NovaContribuicao;
import br.com.tradugil.dicionario.Definicao;
import br.com.tradugil.dicionario.Fonte;
import br.com.tradugil.dicionario.Fonte.TipoDeFonte;
import br.com.tradugil.dicionario.Giria;
import br.com.tradugil.dicionario.Idioma;
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
    private final org.springframework.cache.CacheManager gerenciadorDeCache;

    public ServicoDeContribuicao(RepositorioDeContribuicao repositorio,
                                 RepositorioDeAuditoria auditoria,
                                 RepositorioDeUsuario repositorioDeUsuario,
                                 RepositorioDeIdioma repositorioDeIdioma,
                                 RepositorioDeGiria repositorioDeGiria,
                                 RepositorioDeDefinicao repositorioDeDefinicao,
                                 RepositorioDeFonte repositorioDeFonte,
                                 org.springframework.cache.CacheManager gerenciadorDeCache) {
        this.repositorio = repositorio;
        this.auditoria = auditoria;
        this.repositorioDeUsuario = repositorioDeUsuario;
        this.repositorioDeIdioma = repositorioDeIdioma;
        this.repositorioDeGiria = repositorioDeGiria;
        this.repositorioDeDefinicao = repositorioDeDefinicao;
        this.repositorioDeFonte = repositorioDeFonte;
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

    @Transactional(readOnly = true)
    public List<ContribuicaoResposta> fila(StatusDeContribuicao status, int pagina, int tamanho) {
        StatusDeContribuicao filtro = status == null ? StatusDeContribuicao.PENDENTE : status;
        return repositorio
                .findByStatusOrderByCriadoEm(filtro, PageRequest.of(
                        Math.max(pagina, 0), Math.min(Math.max(tamanho, 1), 100)))
                .stream()
                .map(ContribuicaoResposta::de)
                .toList();
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
            contribuicao.aprovar(moderador);
            publicar(contribuicao);
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
     * <h2>Isto faltava, e o efeito era grave</h2>
     *
     * <p>Até aqui, aprovar marcava a contribuição como APROVADA e gravava a
     * auditoria, e parava. O verbete nunca era criado. A pessoa via a própria
     * sugestão aprovada, procurava o termo no dicionário e não encontrava
     * nada, sem nenhuma explicação possível.</p>
     *
     * <p>Pior para o produto: o nível 5 da cascata existe para o dicionário
     * não envelhecer, e a contribuição é o caminho pelo qual termo novo
     * entra. Com a publicação faltando, esse caminho terminava num beco.</p>
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
     * <h2>Limitação conhecida: o verbete nasce sem categoria</h2>
     *
     * <p>O formulário de contribuição pede termo, idioma e explicação, e mais
     * nada. Sem categoria, o verbete <b>é encontrado pela busca e pelo
     * {@code /traduzir}, mas nunca aparece no catálogo</b>, que é organizado
     * por prateleira.</p>
     *
     * <p>Fica assim de propósito, por enquanto. Adivinhar a categoria a partir
     * do texto seria classificar em nome de quem contribuiu, e categoria
     * errada é pior que categoria nenhuma: manda a pessoa procurar na
     * prateleira errada. O caminho certo é a moderação escolher a categoria na
     * hora de aprovar, o que exige um campo a mais na tela de moderação.</p>
     */
    private void publicar(Contribuicao contribuicao) {
        Idioma idioma = contribuicao.getIdioma();
        String normalizado = Normalizador.normalizar(contribuicao.getTermo());

        Giria giria = repositorioDeGiria
                .findByTermoNormalizadoAndIdiomaCodigo(normalizado, idioma.getCodigo())
                .orElseGet(() -> repositorioDeGiria.save(
                        Giria.daComunidade(contribuicao.getTermo(), idioma)));

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
        var cache = gerenciadorDeCache.getCache("verbetes");
        if (cache != null) {
            cache.clear();
        }
    }
}
