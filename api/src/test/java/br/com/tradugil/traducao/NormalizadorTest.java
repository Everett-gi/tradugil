package br.com.tradugil.traducao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Estes testes são o contrato que as versões TypeScript e Kotlin do
 * normalizador precisam cumprir. Ao portar a classe, porte os casos junto:
 * uma divergência aqui vira uma gíria que funciona online e some offline.
 */
class NormalizadorTest {

    @Test
    @DisplayName("remove acento e caixa, chegando à mesma chave")
    void normalizaAcentoECaixa() {
        assertThat(Normalizador.normalizar("Ranço")).isEqualTo("ranco");
        assertThat(Normalizador.normalizar("RANÇO")).isEqualTo("ranco");
        assertThat(Normalizador.normalizar("ranco")).isEqualTo("ranco");
        assertThat(Normalizador.normalizar("migué")).isEqualTo("migue");
    }

    @Test
    @DisplayName("descarta pontuação sem colar as palavras vizinhas")
    void descartaPontuacao() {
        assertThat(Normalizador.normalizar("pog!")).isEqualTo("pog");
        assertThat(Normalizador.normalizar("...cringe?")).isEqualTo("cringe");
        assertThat(Normalizador.normalizar("dar,ruim")).isEqualTo("dar ruim");
    }

    @Test
    @DisplayName("preserva o espaço interno dos verbetes de mais de uma palavra")
    void preservaEspacoInterno() {
        assertThat(Normalizador.normalizar("  Dar   Ruim  ")).isEqualTo("dar ruim");
        assertThat(Normalizador.normalizar("No Cap")).isEqualTo("no cap");
    }

    @Test
    @DisplayName("mantém dígitos, porque '67' é verbete")
    void mantemDigitos() {
        assertThat(Normalizador.normalizar("67")).isEqualTo("67");
    }

    @Test
    @DisplayName("trata acento pré-composto e decomposto como o mesmo termo")
    void tratamAcentoComposto() {
        // "ç" chega como um caractere só (U+00E7) ou como "c" mais cedilha
        // combinante (U+0327), dependendo do teclado e do sistema: texto
        // vindo de OCR ou do iOS costuma chegar decomposto. São a mesma
        // palavra para quem digitou, e precisam gerar a mesma chave.
        String preComposto = "ranço";
        String decomposto = "ranço";

        assertThat(preComposto).isNotEqualTo(decomposto);
        assertThat(Normalizador.normalizar(preComposto)).isEqualTo("ranco");
        assertThat(Normalizador.normalizar(decomposto)).isEqualTo("ranco");
    }

    @Test
    void entradaVaziaNaoQuebra() {
        assertThat(Normalizador.normalizar(null)).isEmpty();
        assertThat(Normalizador.normalizar("   ")).isEmpty();
        assertThat(Normalizador.normalizar("!!!")).isEmpty();
    }

    @Test
    @DisplayName("colapsa ênfase até duas letras, não até uma")
    void colapsaEnfase() {
        assertThat(Normalizador.colapsarRepeticoes("mdsss")).isEqualTo("mdss");
        assertThat(Normalizador.colapsarRepeticoes("kkkkkkkk")).isEqualTo("kk");
        assertThat(Normalizador.colapsarRepeticoes("aaaa")).isEqualTo("aa");
    }

    @Test
    @DisplayName("não destrói dígrafo legítimo: 'carro' não vira 'caro'")
    void preservaDigrafo() {
        assertThat(Normalizador.colapsarRepeticoes("carro")).isEqualTo("carro");
        assertThat(Normalizador.colapsarRepeticoes("nossa")).isEqualTo("nossa");
        assertThat(Normalizador.colapsarRepeticoes("kkk")).isEqualTo("kk");
    }
}
