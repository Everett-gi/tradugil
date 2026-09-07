package br.com.tradugil.ia;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

/**
 * Orquestra o nível 4 da cascata: cache primeiro, modelo depois.
 *
 * <p>É aqui que mora o controle de custo da seção 7.3. A cascata já garante
 * que a IA seja rara; o cache garante que a mesma pergunta nunca seja paga
 * duas vezes; e a promoção de respostas a verbete faz o custo de um termo ser
 * pago uma única vez na vida do produto.</p>
 */
@Service
public class ServicoDeIa {

    /** Hash de "sem contexto", para não colidir com um contexto vazio real. */
    private static final String SEM_CONTEXTO = "sem-contexto";

    private static final int TAMANHO_DA_FILA_DE_CURADORIA = 50;

    private final ClienteDeIa cliente;
    private final RepositorioDeRespostaIa repositorio;

    public ServicoDeIa(ClienteDeIa cliente, RepositorioDeRespostaIa repositorio) {
        this.cliente = cliente;
        this.repositorio = repositorio;
    }

    public boolean estaDisponivel() {
        return cliente.estaDisponivel();
    }

    /**
     * Explica um termo desconhecido, do cache ou do modelo.
     *
     * @param contexto trecho ao redor do termo. <b>Não é persistido</b> — só
     *                 o seu hash entra na chave do cache
     */
    @Transactional
    public Optional<ExplicacaoDaIa> explicar(String termoNormalizado, String contexto) {
        String hash = hashDe(contexto);

        Optional<RespostaIa> guardada =
                repositorio.findByTermoNormalizadoAndHashDoContexto(termoNormalizado, hash);
        if (guardada.isPresent()) {
            // Inclui respostas negativas ("não é gíria"): descobrir que "67"
            // numa conta de matemática não é gíria também custou uma chamada,
            // e perguntar de novo custaria outra. A resposta negativa fica no
            // cache e é filtrada aqui, sem ir ao modelo.
            return guardada.map(RespostaIa::paraExplicacao).filter(ExplicacaoDaIa::ehUtilizavel);
        }

        Optional<ExplicacaoDaIa> doModelo = cliente.explicar(termoNormalizado, contexto);
        doModelo.ifPresent(explicacao -> repositorio.guardar(
                termoNormalizado,
                hash,
                explicacao.eGiria(),
                explicacao.explicacaoSimples(),
                explicacao.explicacaoDetalhada(),
                explicacao.equivalenteFormal(),
                explicacao.nsfw(),
                explicacao.riscoMenor(),
                (float) explicacao.confianca()));
        return doModelo;
    }

    /** Fila da curadoria: o que a IA respondeu e ninguém revisou ainda. */
    @Transactional(readOnly = true)
    public List<RespostaIa> filaDeCuradoria() {
        return repositorio.filaDeCuradoria(PageRequest.of(0, TAMANHO_DA_FILA_DE_CURADORIA));
    }

    private static String hashDe(String contexto) {
        String base = contexto == null || contexto.isBlank() ? SEM_CONTEXTO : contexto;
        try {
            byte[] digerido = MessageDigest.getInstance("SHA-256")
                    .digest(base.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digerido);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 é obrigatório em toda JVM; se faltar, o ambiente está
            // quebrado de um jeito que não vale contornar aqui.
            throw new IllegalStateException("SHA-256 indisponível nesta JVM", e);
        }
    }
}
