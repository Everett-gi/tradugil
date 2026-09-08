package br.com.tradugil.android

import br.com.tradugil.android.dominio.TextoRecebido
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * As duas portas de entrada de texto vindo de outro aplicativo.
 *
 * <h2>O defeito que estes testes fecham</h2>
 *
 * O manifesto declarava `ACTION_SEND` e `ACTION_PROCESS_TEXT`, e o app só
 * atendia a primeira. Quem selecionava texto em qualquer aplicativo e tocava
 * em "Tradugil" no menu que aparece junto de "Copiar" via o app abrir com o
 * campo vazio: o extra do `PROCESS_TEXT` nunca era lido.
 *
 * Nada acusava. O filtro estava no manifesto, o item aparecia no menu do
 * sistema, o app abria. A falha só se revela usando, e no caminho que é o
 * mais curto que o produto tem.
 */
class TextoRecebidoTest {

    @Test
    fun `texto selecionado na tela chega ao app`() {
        val texto = TextoRecebido.extrair(
            acao = TextoRecebido.ACAO_SELECIONAR,
            compartilhado = null,
            selecionado = "esse mano é muito cringe",
        )

        assertEquals("esse mano é muito cringe", texto)
    }

    @Test
    fun `texto compartilhado continua chegando`() {
        val texto = TextoRecebido.extrair(
            acao = TextoRecebido.ACAO_COMPARTILHAR,
            compartilhado = "vou dar um perrengue",
            selecionado = null,
        )

        assertEquals("vou dar um perrengue", texto)
    }

    @Test
    fun `cada acao le o proprio extra`() {
        /*
         * Ler o extra errado é o defeito original visto de outro ângulo, e
         * atender às duas ações lendo sempre EXTRA_TEXT o reintroduziria de
         * forma mais difícil de enxergar: funcionaria no compartilhamento e
         * devolveria vazio na seleção, exatamente como antes.
         */
        assertEquals(
            "da selecao",
            TextoRecebido.extrair(
                TextoRecebido.ACAO_SELECIONAR, "do compartilhamento", "da selecao",
            ),
        )
        assertEquals(
            "do compartilhamento",
            TextoRecebido.extrair(
                TextoRecebido.ACAO_COMPARTILHAR, "do compartilhamento", "da selecao",
            ),
        )
    }

    @Test
    fun `acao desconhecida nao vira consulta`() {
        // Abrir pelo ícone é ACTION_MAIN, e o campo tem de ficar em branco
        // esperando a pessoa digitar.
        assertNull(TextoRecebido.extrair("android.intent.action.MAIN", "sobra", "sobra"))
        assertNull(TextoRecebido.extrair(null, "sobra", "sobra"))
    }

    @Test
    fun `selecao so de espaco nao vira consulta`() {
        // Devolver "" faria o app abrir e consultar o vazio, gastando uma
        // requisição para responder que não encontrou nada.
        assertNull(TextoRecebido.extrair(TextoRecebido.ACAO_SELECIONAR, null, "   \n  "))
        assertNull(TextoRecebido.extrair(TextoRecebido.ACAO_COMPARTILHAR, "", null))
        assertNull(TextoRecebido.extrair(TextoRecebido.ACAO_COMPARTILHAR, null, null))
    }

    @Test
    fun `o espaco em volta some`() {
        assertEquals(
            "lá ele",
            TextoRecebido.extrair(TextoRecebido.ACAO_SELECIONAR, null, "  lá ele \n"),
        )
    }

    @Test
    fun `selecao gigante e cortada no limite da api`() {
        /*
         * Quem seleciona uma conversa inteira e toca em "Tradugil" veria a
         * requisição ser recusada pela validação do /traduzir, com uma
         * mensagem que não explica nada. Cortado, o app responde sobre o
         * começo do texto, que é uma resposta útil.
         */
        val gigante = "a".repeat(TextoRecebido.MAXIMO_DE_CARACTERES + 500)

        val texto = TextoRecebido.extrair(TextoRecebido.ACAO_SELECIONAR, null, gigante)

        assertEquals(TextoRecebido.MAXIMO_DE_CARACTERES, texto?.length)
    }

    @Test
    fun `CharSequence que nao e String tambem serve`() {
        // O sistema entrega os extras como CharSequence, e nem sempre como
        // String: um texto selecionado com formatação chega como Spanned.
        val comFormatacao: CharSequence = StringBuilder("mano do céu")

        assertEquals(
            "mano do céu",
            TextoRecebido.extrair(TextoRecebido.ACAO_SELECIONAR, null, comFormatacao),
        )
    }
}
