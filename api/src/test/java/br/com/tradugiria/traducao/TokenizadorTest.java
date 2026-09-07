package br.com.tradugiria.traducao;

import br.com.tradugiria.traducao.Tokenizador.Trecho;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TokenizadorTest {

    /** A frase de exemplo da seção 5.2 do documento de especificação. */
    private static final String FRASE = "mano ele clutchou a round, foi mt pog, kekw";

    @Test
    @DisplayName("as posições recortam exatamente a palavra no texto original")
    void posicoesRecortamOTextoOriginal() {
        List<Trecho> palavras = Tokenizador.tokenizar(FRASE);

        // O contrato que a interface depende: substring(inicio, fim) devolve
        // a palavra. Se isto quebrar, o destaque cai no lugar errado.
        for (Trecho palavra : palavras) {
            assertThat(FRASE.substring(palavra.inicio(), palavra.fim()))
                    .isEqualTo(palavra.original());
        }

        assertThat(palavras).extracting(Trecho::original)
                .containsExactly("mano", "ele", "clutchou", "a", "round",
                        "foi", "mt", "pog", "kekw");
    }

    @Test
    @DisplayName("a vírgula colada não entra no trecho destacado")
    void virgulaNaoEntraNoTrecho() {
        List<Trecho> palavras = Tokenizador.tokenizar(FRASE);

        Trecho pog = palavras.stream()
                .filter(p -> p.original().equals("pog")).findFirst().orElseThrow();
        assertThat(FRASE.substring(pog.inicio(), pog.fim())).isEqualTo("pog");
        assertThat(FRASE.charAt(pog.fim())).isEqualTo(',');
    }

    @Test
    @DisplayName("candidatos vêm do mais longo para o mais curto")
    void candidatosVemDoMaisLongo() {
        List<Trecho> candidatos = Tokenizador.candidatos(Tokenizador.tokenizar("deu dar ruim"));

        int posicaoDoPar = indiceDe(candidatos, "dar ruim");
        int posicaoDoIsolado = indiceDe(candidatos, "ruim");

        // A ordem é o que faz "dar ruim" vencer "ruim" no casamento guloso.
        assertThat(posicaoDoPar).isLessThan(posicaoDoIsolado);
    }

    @Test
    @DisplayName("candidatos de várias palavras usam a forma normalizada")
    void candidatosUsamFormaNormalizada() {
        List<Trecho> candidatos = Tokenizador.candidatos(Tokenizador.tokenizar("Deu Dar  Ruim!"));

        assertThat(candidatos).extracting(Trecho::normalizado).contains("dar ruim");
    }

    @Test
    @DisplayName("texto vazio não gera candidato nem exceção")
    void textoVazio() {
        assertThat(Tokenizador.tokenizar("")).isEmpty();
        assertThat(Tokenizador.tokenizar(null)).isEmpty();
        assertThat(Tokenizador.candidatos(List.of())).isEmpty();
    }

    private int indiceDe(List<Trecho> candidatos, String normalizado) {
        for (int i = 0; i < candidatos.size(); i++) {
            if (candidatos.get(i).normalizado().equals(normalizado)) {
                return i;
            }
        }
        throw new AssertionError("candidato ausente: " + normalizado);
    }
}
