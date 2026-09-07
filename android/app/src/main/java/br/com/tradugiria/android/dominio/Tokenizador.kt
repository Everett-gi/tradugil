package br.com.tradugiria.android.dominio

/**
 * Quebra o texto em palavras e monta os candidatos a gíria.
 *
 * PORTE de `Tokenizador.java` e `tokenizador.ts`. O destaque das gírias
 * precisa cair exatamente nas mesmas posições no site, na extensão e aqui.
 */
object Tokenizador {

    /**
     * Sequências de letras ou dígitos. A pontuação fica de fora de propósito:
     * é ruído para a busca, e o intervalo devolvido continua apontando para a
     * posição correta no texto original mesmo assim.
     */
    private val PALAVRA = Regex("[\\p{IsAlphabetic}\\p{IsDigit}]+")

    /**
     * Maior verbete de várias palavras que vale procurar ("no cap", "dar
     * ruim"). Três é o teto porque a cada palavra a mais o número de
     * candidatos cresce junto com o custo da consulta, e verbetes de quatro
     * palavras são raros o bastante para não pagarem esse preço.
     */
    const val MAXIMO_DE_PALAVRAS = 3

    /**
     * Um trecho do texto original e sua forma de busca.
     *
     * [fim] é exclusivo — mesma convenção de `substring`, para a interface
     * marcar o trecho sem ajustar nada.
     */
    data class Trecho(
        val original: String,
        val normalizado: String,
        val inicio: Int,
        val fim: Int,
    )

    fun tokenizar(texto: String?): List<Trecho> {
        if (texto.isNullOrBlank()) return emptyList()

        return PALAVRA.findAll(texto).map { encontro ->
            Trecho(
                original = encontro.value,
                normalizado = Normalizador.normalizar(encontro.value),
                inicio = encontro.range.first,
                // `range.last` é inclusivo no Kotlin; somar 1 é o que mantém
                // a mesma convenção semiaberta das outras duas plataformas.
                fim = encontro.range.last + 1,
            )
        }.toList()
    }

    /**
     * Monta os candidatos, das sequências mais longas para as mais curtas.
     *
     * A ordem é o que garante o casamento guloso: quando o texto tem "dar
     * ruim", o candidato de duas palavras aparece antes de "dar" e "ruim"
     * sozinhos, e vence. Sem isso o usuário veria "ruim" explicado como
     * adjetivo comum, em vez da expressão que ele leu.
     */
    fun candidatos(palavras: List<Trecho>): List<Trecho> {
        val lista = mutableListOf<Trecho>()
        for (tamanho in MAXIMO_DE_PALAVRAS downTo 1) {
            for (i in 0..(palavras.size - tamanho)) {
                val janela = palavras.subList(i, i + tamanho)
                val normalizado = janela.joinToString(" ") { it.normalizado }
                if (normalizado.isBlank()) continue

                lista += Trecho(
                    original = normalizado,
                    normalizado = normalizado,
                    inicio = janela.first().inicio,
                    fim = janela.last().fim,
                )
            }
        }
        return lista
    }
}
