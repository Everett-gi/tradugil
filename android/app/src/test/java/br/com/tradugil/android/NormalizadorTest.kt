package br.com.tradugil.android

import br.com.tradugil.android.dominio.Normalizador
import br.com.tradugil.android.dominio.Tokenizador
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Terceira cópia do mesmo contrato, ao lado de `NormalizadorTest.java` e
 * `normalizador.test.ts`. Os casos e os resultados esperados são idênticos
 * de propósito: é assim que se prova que as três implementações não
 * divergiram.
 */
class NormalizadorTest {

    @Test
    fun `remove acento e caixa, chegando a mesma chave`() {
        assertEquals("ranco", Normalizador.normalizar("Ranço"))
        assertEquals("ranco", Normalizador.normalizar("RANÇO"))
        assertEquals("ranco", Normalizador.normalizar("ranco"))
        assertEquals("migue", Normalizador.normalizar("migué"))
    }

    @Test
    fun `descarta pontuacao sem colar as palavras vizinhas`() {
        assertEquals("pog", Normalizador.normalizar("pog!"))
        assertEquals("cringe", Normalizador.normalizar("...cringe?"))
        assertEquals("dar ruim", Normalizador.normalizar("dar,ruim"))
    }

    @Test
    fun `preserva o espaco interno dos verbetes de mais de uma palavra`() {
        assertEquals("dar ruim", Normalizador.normalizar("  Dar   Ruim  "))
        assertEquals("no cap", Normalizador.normalizar("No Cap"))
    }

    @Test
    fun `mantem digitos, porque 67 e verbete`() {
        assertEquals("67", Normalizador.normalizar("67"))
    }

    @Test
    fun `trata acento pre-composto e decomposto como o mesmo termo`() {
        val preComposto = "ranço"
        val decomposto = "ranço"

        assertNotEquals(preComposto, decomposto)
        assertEquals("ranco", Normalizador.normalizar(preComposto))
        assertEquals("ranco", Normalizador.normalizar(decomposto))
    }

    @Test
    fun `entrada vazia nao quebra`() {
        assertEquals("", Normalizador.normalizar(null))
        assertEquals("", Normalizador.normalizar("   "))
        assertEquals("", Normalizador.normalizar("!!!"))
    }

    @Test
    fun `colapsa enfase ate duas letras, nao ate uma`() {
        assertEquals("mdss", Normalizador.colapsarRepeticoes("mdsss"))
        assertEquals("kk", Normalizador.colapsarRepeticoes("kkkkkkkk"))
        assertEquals("aa", Normalizador.colapsarRepeticoes("aaaa"))
    }

    @Test
    fun `nao destroi digrafo legitimo`() {
        assertEquals("carro", Normalizador.colapsarRepeticoes("carro"))
        assertEquals("nossa", Normalizador.colapsarRepeticoes("nossa"))
        assertEquals("kk", Normalizador.colapsarRepeticoes("kkk"))
    }
}

class TokenizadorTest {

    /** A frase de exemplo da seção 5.2 do documento de especificação. */
    private val frase = "mano ele clutchou a round, foi mt pog, kekw"

    @Test
    fun `as posicoes recortam exatamente a palavra no texto original`() {
        val palavras = Tokenizador.tokenizar(frase)

        // O contrato de que a interface depende: substring(inicio, fim)
        // devolve a palavra. Se isto quebrar, o destaque cai no lugar errado.
        palavras.forEach { palavra ->
            assertEquals(palavra.original, frase.substring(palavra.inicio, palavra.fim))
        }

        assertEquals(
            listOf("mano", "ele", "clutchou", "a", "round", "foi", "mt", "pog", "kekw"),
            palavras.map { it.original },
        )
    }

    @Test
    fun `a virgula colada nao entra no trecho destacado`() {
        val pog = Tokenizador.tokenizar(frase).first { it.original == "pog" }
        assertEquals("pog", frase.substring(pog.inicio, pog.fim))
        assertEquals(',', frase[pog.fim])
    }

    @Test
    fun `candidatos vem do mais longo para o mais curto`() {
        val lista = Tokenizador.candidatos(Tokenizador.tokenizar("deu dar ruim"))
        val posicaoDoPar = lista.indexOfFirst { it.normalizado == "dar ruim" }
        val posicaoDoIsolado = lista.indexOfFirst { it.normalizado == "ruim" }

        assertTrue(posicaoDoPar >= 0)
        // A ordem é o que faz "dar ruim" vencer "ruim" no casamento guloso.
        assertTrue(posicaoDoPar < posicaoDoIsolado)
    }

    @Test
    fun `as posicoes de um candidato composto abrangem as duas palavras`() {
        val texto = "isso vai dar ruim"
        val par = Tokenizador.candidatos(Tokenizador.tokenizar(texto))
            .first { it.normalizado == "dar ruim" }
        assertEquals("dar ruim", texto.substring(par.inicio, par.fim))
    }

    @Test
    fun `texto vazio nao gera token nem excecao`() {
        assertEquals(emptyList<Tokenizador.Trecho>(), Tokenizador.tokenizar(""))
        assertEquals(emptyList<Tokenizador.Trecho>(), Tokenizador.tokenizar(null))
        assertEquals(emptyList<Tokenizador.Trecho>(), Tokenizador.candidatos(emptyList()))
    }
}
