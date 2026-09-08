package br.com.tradugil.android.dominio

/**
 * O texto que chega de outro aplicativo, e de onde ele vem.
 *
 * <h2>Por que isto não recebe um Intent</h2>
 *
 * Porque então não teria teste. `Intent` é classe do Android: num teste de
 * unidade da JVM, todo método dela devolve zero ou explode, e provar esta
 * lógica exigiria subir Robolectric. Aqui entram quatro valores simples, a
 * [MainActivity] faz a tradução mecânica de `Intent` para eles, e a decisão
 * de qual extra vale, com aparo e teto, fica coberta.
 *
 * É o mesmo desenho de [Normalizador] e [Tokenizador]: a regra vive longe do
 * framework, e o framework só entrega dado.
 */
object TextoRecebido {

    /**
     * Teto de caracteres.
     *
     * O mesmo limite que o `/traduzir` da API aceita. Aparar aqui, e não
     * descobrir lá, importa: quem seleciona uma conversa inteira e toca em
     * "Tradugil" veria a requisição ser recusada com erro de validação, o que
     * não explica nada. Cortado, o app responde sobre o começo do texto, que
     * é uma resposta útil.
     */
    const val MAXIMO_DE_CARACTERES: Int = 5_000

    /** Compartilhar de outro app: {@code Intent.ACTION_SEND}. */
    const val ACAO_COMPARTILHAR: String = "android.intent.action.SEND"

    /** Menu de seleção de texto: {@code Intent.ACTION_PROCESS_TEXT}. */
    const val ACAO_SELECIONAR: String = "android.intent.action.PROCESS_TEXT"

    /**
     * O texto útil de um intent de entrada, ou null quando não há nenhum.
     *
     * <h2>O defeito que isto conserta</h2>
     *
     * O manifesto declarava as duas portas, e o app só atendia uma. Quem
     * selecionava texto em qualquer aplicativo e tocava em "Tradugil" no menu
     * que aparece junto de "Copiar" via o app abrir com o campo **vazio**: o
     * extra do `PROCESS_TEXT` nunca era lido. A porta estava anunciada no
     * manifesto, aparecia no menu do sistema, e não levava a lugar nenhum.
     *
     * Esse é o caminho mais curto que o produto tem, e o mais importante para
     * quem ele atende: sem trocar de aplicativo, sem copiar e colar, sem
     * permissão nenhuma. Ler a tela por conta própria exigiria o
     * `AccessibilityService`, que é a permissão que aplicativos maliciosos
     * mais abusam. Aqui quem entrega o texto é o sistema, porque a pessoa
     * mandou, e o app não enxerga mais nada além do que foi selecionado.
     *
     * @param acao          {@code intent.action}
     * @param compartilhado {@code EXTRA_TEXT}, do compartilhamento
     * @param selecionado   {@code EXTRA_PROCESS_TEXT}, da seleção de texto
     */
    fun extrair(
        acao: String?,
        compartilhado: CharSequence?,
        selecionado: CharSequence?,
    ): String? = when (acao) {
        ACAO_COMPARTILHAR -> aparar(compartilhado)
        ACAO_SELECIONAR -> aparar(selecionado)
        else -> null
    }

    private fun aparar(bruto: CharSequence?): String? {
        val texto = bruto?.toString()?.trim().orEmpty()
        if (texto.isEmpty()) {
            // Seleção só de espaço, ou compartilhamento de uma imagem sem
            // legenda. Devolver "" faria o app abrir e consultar o vazio.
            return null
        }
        return texto.take(MAXIMO_DE_CARACTERES)
    }
}
