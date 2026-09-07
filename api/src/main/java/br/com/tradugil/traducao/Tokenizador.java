package br.com.tradugil.traducao;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Quebra o texto em palavras e monta os candidatos a gíria.
 *
 * <p>Como {@code Normalizador}, tem gêmeas em {@code packages/core-ts} e no
 * módulo Android: o destaque das gírias precisa cair exatamente nas mesmas
 * posições no site, na extensão e no aplicativo.</p>
 */
public final class Tokenizador {

    /**
     * Sequências de letras ou dígitos. A pontuação fica de fora de propósito:
     * ela é ruído para a busca, e o intervalo devolvido continua apontando
     * para a posição correta no texto original mesmo assim.
     */
    private static final Pattern PALAVRA = Pattern.compile("[\\p{IsAlphabetic}\\p{IsDigit}]+");

    /**
     * Maior verbete de várias palavras que vale procurar ("no cap", "dar
     * ruim"). Três é o teto porque a cada palavra a mais o número de
     * candidatos cresce junto com o custo da consulta, e verbetes de quatro
     * palavras são raros o bastante para não pagarem esse preço.
     */
    public static final int MAXIMO_DE_PALAVRAS = 3;

    private Tokenizador() {
    }

    /**
     * Um trecho do texto original e sua forma de busca.
     *
     * @param inicio índice do primeiro caractere, inclusivo
     * @param fim    índice logo após o último caractere, exclusivo — mesma
     *               convenção de {@code String.substring}, para o cliente
     *               poder destacar o trecho sem ajustar nada
     */
    public record Trecho(String original, String normalizado, int inicio, int fim) {
    }

    public static List<Trecho> tokenizar(String texto) {
        List<Trecho> palavras = new ArrayList<>();
        if (texto == null || texto.isBlank()) {
            return palavras;
        }
        Matcher m = PALAVRA.matcher(texto);
        while (m.find()) {
            String bruto = m.group();
            palavras.add(new Trecho(bruto, Normalizador.normalizar(bruto), m.start(), m.end()));
        }
        return palavras;
    }

    /**
     * Monta os candidatos, das sequências mais longas para as mais curtas.
     *
     * <p>A ordem é o que garante o casamento guloso mais adiante: quando o
     * texto tem "dar ruim", o candidato de duas palavras aparece antes de
     * "dar" e de "ruim" sozinhos, e vence. Sem isso, o usuário veria "ruim"
     * explicado como adjetivo comum em vez da expressão que ele leu.</p>
     */
    public static List<Trecho> candidatos(List<Trecho> palavras) {
        List<Trecho> candidatos = new ArrayList<>();
        for (int tamanho = MAXIMO_DE_PALAVRAS; tamanho >= 1; tamanho--) {
            for (int i = 0; i + tamanho <= palavras.size(); i++) {
                List<Trecho> janela = palavras.subList(i, i + tamanho);
                String normalizado = janela.stream()
                        .map(Trecho::normalizado)
                        .reduce((a, b) -> a + " " + b)
                        .orElse("");
                if (normalizado.isBlank()) {
                    continue;
                }
                int inicio = janela.get(0).inicio();
                int fim = janela.get(janela.size() - 1).fim();
                candidatos.add(new Trecho(normalizado, normalizado, inicio, fim));
            }
        }
        return candidatos;
    }
}
