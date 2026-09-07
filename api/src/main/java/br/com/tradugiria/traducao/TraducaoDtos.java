package br.com.tradugiria.traducao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public final class TraducaoDtos {

    private TraducaoDtos() {
    }

    /**
     * Pedido de tradução.
     *
     * @param texto       trecho a analisar. Nunca é persistido: percorre a
     *                    requisição em memória e some com ela (seção 8 do
     *                    documento). O limite de tamanho é o que impede que
     *                    alguém use o endpoint público como triturador de
     *                    texto para gerar custo de IA.
     * @param idioma      'pt-BR', 'en' ou 'auto'
     * @param nivel       SIMPLES ou DETALHADA
     * @param contexto    pista opcional de onde o texto apareceu ("chat de
     *                    jogo"). Só é usada pela camada de IA, para desambiguar
     *                    termos como "67"
     * @param modoFamilia oculta termos impróprios e sinaliza os de risco
     */
    public record PedidoDeTraducao(
            @NotBlank(message = "Informe o texto a traduzir.")
            @Size(max = 5000, message = "O texto pode ter no máximo 5000 caracteres.")
            String texto,

            String idioma,
            NivelDeExplicacao nivel,

            @Size(max = 120, message = "O contexto pode ter no máximo 120 caracteres.")
            String contexto,

            Boolean modoFamilia
    ) {
        /**
         * O Modo Família liga por padrão. A escolha protege o caso em que
         * errar custa caro — uma criança ou a Marlene recebendo conteúdo
         * impróprio — em vez do caso em que errar só incomoda.
         */
        public boolean familiaLigado() {
            return modoFamilia == null || modoFamilia;
        }

        public NivelDeExplicacao nivelOuPadrao() {
            return nivel == null ? NivelDeExplicacao.SIMPLES : nivel;
        }

        public String idiomaOuNulo() {
            return idioma == null || idioma.isBlank() || "auto".equalsIgnoreCase(idioma)
                    ? null : idioma;
        }
    }

    public enum NivelDeExplicacao {
        SIMPLES, DETALHADA
    }

    /** De qual nível da cascata a resposta veio. Sobe até a interface. */
    public enum OrigemDaResposta {
        DICIONARIO, EXTERNA, IA
    }

    public record RespostaDeTraducao(
            String idiomaDetectado,
            List<GiriaDetectada> girias,
            boolean geradoPorIa,
            List<String> naoResolvidos
    ) {
    }

    /**
     * @param posicao par [início, fim) sobre o texto enviado, na mesma
     *                convenção de {@code String.substring}: o cliente marca o
     *                trecho com {@code texto.substring(inicio, fim)} sem
     *                somar nem subtrair nada
     */
    public record GiriaDetectada(
            String termo,
            List<Integer> posicao,
            String explicacao,
            String equivalenteFormal,
            boolean nsfw,
            boolean riscoMenor,
            double confianca,
            OrigemDaResposta origem
    ) {
    }
}
