package br.com.tradugil.contribuicao;

import br.com.tradugil.comum.erro.RecursoNaoEncontradoException;
import br.com.tradugil.comum.erro.RegraDeNegocioException;
import br.com.tradugil.contribuicao.Contribuicao.StatusDeContribuicao;
import br.com.tradugil.contribuicao.ContribuicaoDtos.ContribuicaoResposta;
import br.com.tradugil.contribuicao.ContribuicaoDtos.DecisaoDeModeracao;
import br.com.tradugil.contribuicao.ContribuicaoDtos.NovaContribuicao;
import br.com.tradugil.dicionario.Idioma;
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

    public ServicoDeContribuicao(RepositorioDeContribuicao repositorio,
                                 RepositorioDeAuditoria auditoria,
                                 RepositorioDeUsuario repositorioDeUsuario,
                                 RepositorioDeIdioma repositorioDeIdioma) {
        this.repositorio = repositorio;
        this.auditoria = auditoria;
        this.repositorioDeUsuario = repositorioDeUsuario;
        this.repositorioDeIdioma = repositorioDeIdioma;
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
     * Repudiation existe para impedir — conteúdo publicado sem ninguém
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
}
