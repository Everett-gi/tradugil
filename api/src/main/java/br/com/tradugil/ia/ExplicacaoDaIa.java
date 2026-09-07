package br.com.tradugil.ia;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * O que a IA tem permissão de devolver.
 *
 * <p>Este record é o esquema de saída estruturada: a API do modelo garante
 * que a resposta tenha exatamente estes campos, com estes tipos. Texto livre
 * não tem por onde sair.</p>
 *
 * <p><b>Esquema válido não é conteúdo confiável.</b> Uma instrução injetada
 * no texto do usuário pode, no pior caso, produzir uma resposta perfeitamente
 * conforme ao esquema e com conteúdo escolhido pelo atacante. Por isso
 * {@link #ehUtilizavel()} confere o que o esquema não confere (tamanho,
 * faixa de confiança, coerência entre campos) e a interface sempre exibe o
 * resultado com rótulo de não verificado.</p>
 *
 * @param eGiria             se o termo é gíria no contexto dado
 * @param explicacaoSimples  uma ou duas frases, sem jargão
 * @param explicacaoDetalhada origem e nuance, opcional
 * @param equivalenteFormal  como se diria a mesma coisa formalmente
 * @param nsfw               conteúdo impróprio, ocultado no Modo Família
 * @param riscoMenor         associado a comportamento de risco para menores
 * @param confianca          0.0 a 1.0
 */
public record ExplicacaoDaIa(        @JsonPropertyDescription("true se o termo for gíria no contexto dado")
        boolean eGiria,

        @JsonPropertyDescription(                "Explicação em uma ou duas frases curtas, sem jargão, "
                        + "compreensível por uma pessoa idosa sem familiaridade com internet")
        String explicacaoSimples,

        @JsonPropertyDescription("Origem e nuance do termo, para quem quiser ir além")
        String explicacaoDetalhada,

        @JsonPropertyDescription("Como se diria a mesma coisa em linguagem formal")
        String equivalenteFormal,

        @JsonPropertyDescription("true se o termo for de conteúdo impróprio")
        boolean nsfw,

        @JsonPropertyDescription(                "true se o termo estiver associado a comportamento de risco para adolescentes")
        boolean riscoMenor,

        @JsonPropertyDescription("Confiança na resposta, de 0.0 a 1.0")
        double confianca
) {

    /**
     * Explicação mais longa que isto não é explicação: é texto derramado.
     * O limite também impede que uma injeção bem-sucedida use o campo como
     * canal para despejar conteúdo arbitrário na tela do usuário.
     */
    private static final int TAMANHO_MAXIMO_SIMPLES = 400;

    private static final int TAMANHO_MAXIMO_DETALHADA = 1_200;

    private static final int TAMANHO_MAXIMO_FORMAL = 160;

    /**
     * Confiança abaixo disso não vale exibir nem gastar linha na fila de
     * curadoria: é o modelo dizendo que não sabe.
     */
    private static final double CONFIANCA_MINIMA = 0.4;

    /**
     * Confere o que o esquema não confere.
     *
     * <p>Uma resposta reprovada aqui é descartada em silêncio e o termo segue
     * para a fila de desconhecidos, como se a IA não tivesse sido chamada. Do
     * ponto de vista do usuário, não há diferença entre "a IA não soube" e "a
     * IA respondeu algo que não passou na validação", e é melhor assim: a
     * alternativa seria exibir o que não passou.</p>
     */
    public boolean ehUtilizavel() {
        if (!eGiria) {
            // Resposta negativa é legítima e útil ("67" numa conta de
            // matemática não é gíria), mas nada dela vai para a tela.
            return false;
        }
        if (explicacaoSimples == null || explicacaoSimples.isBlank()) {
            return false;
        }
        if (confianca < CONFIANCA_MINIMA || confianca > 1.0) {
            return false;
        }
        return explicacaoSimples.length() <= TAMANHO_MAXIMO_SIMPLES
                && (explicacaoDetalhada == null
                        || explicacaoDetalhada.length() <= TAMANHO_MAXIMO_DETALHADA)
                && (equivalenteFormal == null
                        || equivalenteFormal.length() <= TAMANHO_MAXIMO_FORMAL);
    }
}
