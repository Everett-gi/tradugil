package br.com.tradugil.android.dados

import br.com.tradugil.android.dominio.ErroDeRede
import br.com.tradugil.android.dominio.PedidoDeTraducao
import br.com.tradugil.android.dominio.RespostaDeTraducao

/**
 * A cascata do lado do cliente.
 *
 * <p><b>A ordem é servidor primeiro, e não cache primeiro.</b> O dicionário
 * do aparelho pode estar desatualizado, e uma explicação corrigida pela
 * curadoria — especialmente de um termo de risco — precisa chegar. O local é
 * a rede de segurança, não o caminho preferencial.</p>
 */
class RepositorioDeTraducao(
    private val api: ClienteDaApi,
    private val local: DicionarioLocal,
) {

    data class Resultado(
        val resposta: RespostaDeTraducao,
        /** Verdadeiro quando veio do dicionário do próprio aparelho. */
        val offline: Boolean,
    )

    suspend fun traduzir(pedido: PedidoDeTraducao): Resultado {
        return try {
            val resposta = api.traduzir(pedido)
            // Guarda o que veio, para a próxima consulta funcionar sem
            // conexão. Falhar ao gravar o cache não pode derrubar uma
            // consulta que deu certo.
            runCatching { local.memorizar(resposta) }
            Resultado(resposta, offline = false)
        } catch (e: ErroDeRede) {
            Resultado(
                local.traduzir(pedido.texto, pedido.nivel, pedido.modoFamilia),
                offline = true,
            )
        }
    }

    suspend fun verbetesGuardados(): Int = local.quantidadeGuardada()
}
