package br.com.tradugil.android.dominio

import java.text.Normalizer
import java.util.Locale

/**
 * Reduz um termo à forma usada como chave de busca.
 *
 * PORTE de `api/.../traducao/Normalizador.java` e de
 * `packages/core-ts/src/normalizador.ts`. As três implementações precisam
 * devolver exatamente o mesmo resultado para toda entrada: o servidor resolve
 * pelo Postgres e o aplicativo resolve pelo banco local, e uma divergência de
 * um caractere faz o mesmo termo funcionar online e sumir offline — o usuário
 * vê o aplicativo esquecer uma gíria que já sabia.
 *
 * `NormalizadorTest` repete os mesmos casos das outras duas plataformas. Ao
 * mudar uma regra aqui, mude nas outras duas e regenere o pacote offline: os
 * termos já gravados foram normalizados pelas regras antigas.
 */
object Normalizador {

    /** Marcas de acentuação isoladas pela decomposição NFD. */
    private val ACENTOS = Regex("\\p{InCombiningDiacriticalMarks}+")

    /** Tudo que não for letra, dígito ou espaço. */
    private val RUIDO = Regex("[^\\p{IsAlphabetic}\\p{IsDigit} ]")

    private val ESPACOS = Regex("\\s+")

    /** Runs de 3 ou mais caracteres iguais: o "sss" de "mdsss". */
    private val REPETICOES = Regex("(.)\\1{2,}")

    /**
     * Forma canônica: minúsculas, sem acento, sem pontuação, espaços
     * colapsados. "Ranço!" e "RANCO" chegam ambos a "ranco".
     *
     * Espaços internos são preservados de propósito: "dar ruim" e "no cap"
     * são verbetes de duas palavras, e colapsá-los quebraria a chave do
     * dicionário.
     */
    fun normalizar(bruto: String?): String {
        if (bruto.isNullOrBlank()) return ""

        val semAcento = ACENTOS.replace(Normalizer.normalize(bruto, Normalizer.Form.NFD), "")
        // Locale.ROOT, e não o do aparelho: em turco, "I".lowercase() devolve
        // "ı" sem ponto. O servidor rodaria com um locale e o celular com
        // outro, e o mesmo termo geraria chaves diferentes — exatamente a
        // divergência que esta classe existe para impedir.
        val limpo = RUIDO.replace(semAcento.lowercase(Locale.ROOT), " ")
        return ESPACOS.replace(limpo, " ").trim()
    }

    /**
     * Colapsa repetições de ênfase: "mdssss" vira "mdss", "kkkkkkk" vira "kk".
     *
     * Vive separada de [normalizar] porque é uma tentativa, não a chave. O
     * usuário escreve a ênfase com qualquer número de letras, e a tabela de
     * variações não consegue listar todas — mas aplicar o colapso à chave
     * principal quebraria termos legítimos com letra dobrada.
     *
     * O corte é em duas letras, e não em uma, para não destruir dígrafos do
     * português: "carro" não pode virar "caro".
     */
    fun colapsarRepeticoes(normalizado: String?): String {
        if (normalizado.isNullOrEmpty()) return ""
        return REPETICOES.replace(normalizado) { encontro ->
            val letra = encontro.groupValues[1]
            letra + letra
        }
    }
}
