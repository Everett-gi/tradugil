package br.com.tradugiria.traducao;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Reduz um termo à forma usada como chave de busca.
 *
 * <p><b>Esta classe tem gêmeas.</b> A mesma lógica roda no dicionário offline
 * do Android (Room) e do navegador (IndexedDB), onde não existe Postgres para
 * normalizar. Se as três implementações divergirem em um único caractere, o
 * mesmo termo passa a resolver online e falhar offline — e o usuário vê o
 * aplicativo "esquecer" uma gíria que já sabia. Por isso a normalização é
 * feita aqui, em Java puro, e não por {@code unaccent()} do Postgres: o banco
 * não estaria disponível para os clientes replicarem o comportamento.</p>
 *
 * <p>Qualquer alteração de regra aqui exige alteração igual em
 * {@code packages/core-ts} e no módulo Android, e a regeneração do pacote
 * offline — os termos já gravados foram normalizados pelas regras antigas.</p>
 */
public final class Normalizador {

    /** Marcas de acentuação isoladas pela decomposição NFD. */
    private static final Pattern ACENTOS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    /** Tudo que não for letra, dígito ou espaço interno. */
    private static final Pattern RUIDO = Pattern.compile("[^\\p{IsAlphabetic}\\p{IsDigit} ]");

    private static final Pattern ESPACOS = Pattern.compile("\\s+");

    /** Runs de 3 ou mais caracteres iguais: o "sss" de "mdsss". */
    private static final Pattern REPETICOES = Pattern.compile("(.)\\1{2,}");

    private Normalizador() {
    }

    /**
     * Forma canônica: minúsculas, sem acento, sem pontuação, espaços
     * colapsados. "Ranço!" e "RANCO" chegam ambos a "ranco".
     *
     * <p>Espaços internos são preservados de propósito: "dar ruim" e "no cap"
     * são verbetes de duas palavras, e colapsá-los quebraria a chave única
     * de {@code giria.termo_normalizado}.</p>
     */
    public static String normalizar(String bruto) {
        if (bruto == null || bruto.isBlank()) {
            return "";
        }
        String semAcento = ACENTOS.matcher(
                Normalizer.normalize(bruto, Normalizer.Form.NFD)).replaceAll("");
        // Locale.ROOT, e nao o locale padrao: em turco, "I".toLowerCase()
        // devolve "i" sem ponto. O servidor rodaria com um locale e o celular
        // do usuario com outro, e o mesmo termo geraria chaves diferentes --
        // exatamente a divergencia que esta classe existe para impedir.
        String limpo = RUIDO.matcher(semAcento.toLowerCase(Locale.ROOT)).replaceAll(" ");
        return ESPACOS.matcher(limpo).replaceAll(" ").trim();
    }

    /**
     * Colapsa repetições de ênfase: "mdssss" vira "mdss", "kkkkkkk" vira "kk".
     *
     * <p>Vive separada de {@link #normalizar} porque é uma <i>tentativa</i>,
     * não a chave. O usuário pode digitar a ênfase com qualquer número de
     * letras, e a tabela de variações não consegue listar todas — mas aplicar
     * o colapso à chave principal quebraria termos legítimos com letra dobrada.
     * Então a busca tenta primeiro a forma exata e só depois esta.</p>
     *
     * <p>O corte é em duas letras, e não em uma, para não destruir dígrafos
     * do português: "carro" não pode virar "caro".</p>
     */
    public static String colapsarRepeticoes(String normalizado) {
        if (normalizado == null || normalizado.isEmpty()) {
            return "";
        }
        return REPETICOES.matcher(normalizado).replaceAll("$1$1");
    }
}
