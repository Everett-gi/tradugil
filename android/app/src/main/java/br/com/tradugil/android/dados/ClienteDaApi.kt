package br.com.tradugil.android.dados

import br.com.tradugil.android.dominio.ErroDeRede
import br.com.tradugil.android.dominio.GiriaDetectada
import br.com.tradugil.android.dominio.OrigemDaResposta
import br.com.tradugil.android.dominio.PedidoDeTraducao
import br.com.tradugil.android.dominio.RespostaDeTraducao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Cliente HTTP da API.
 *
 * Usa `HttpURLConnection` e `org.json`, ambos da própria plataforma, em vez
 * de OkHttp e um serializador. O app precisa ser leve e o JSON trocado aqui
 * é pequeno e estável — trazer duas bibliotecas para isso aumentaria o APK
 * que o usuário baixa do site sem resolver problema nenhum.
 */
class ClienteDaApi(private val baseUrl: String) {

    private companion object {
        const val TIMEOUT_DE_CONEXAO_MS = 5_000
        const val TIMEOUT_DE_LEITURA_MS = 8_000
    }

    suspend fun traduzir(pedido: PedidoDeTraducao): RespostaDeTraducao =
        withContext(Dispatchers.IO) {
            val corpo = JSONObject().apply {
                put("texto", pedido.texto)
                put("nivel", pedido.nivel.name)
                put("modoFamilia", pedido.modoFamilia)
                pedido.contexto?.let { put("contexto", it) }
            }

            val conexao = abrir("/api/v1/traduzir")
            try {
                conexao.requestMethod = "POST"
                conexao.doOutput = true
                conexao.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                conexao.outputStream.use { it.write(corpo.toString().toByteArray(Charsets.UTF_8)) }

                val codigo = conexao.responseCode
                if (codigo !in 200..299) {
                    // 5xx é servidor fora do ar: vale tentar o dicionário
                    // local. 4xx é pedido inválido, e o local não resolveria
                    // melhor — por isso os dois viram exceções diferentes.
                    if (codigo >= 500) throw ErroDeRede(null)
                    throw IllegalStateException("Resposta $codigo do servidor")
                }

                val texto = conexao.inputStream.bufferedReader(Charsets.UTF_8).readText()
                lerResposta(JSONObject(texto))
            } catch (e: ErroDeRede) {
                throw e
            } catch (e: IllegalStateException) {
                throw e
            } catch (e: Exception) {
                throw ErroDeRede(e)
            } finally {
                conexao.disconnect()
            }
        }

    private fun abrir(caminho: String): HttpURLConnection =
        (URL("$baseUrl$caminho").openConnection() as HttpURLConnection).apply {
            connectTimeout = TIMEOUT_DE_CONEXAO_MS
            readTimeout = TIMEOUT_DE_LEITURA_MS
        }

    private fun lerResposta(json: JSONObject): RespostaDeTraducao {
        val girias = json.optJSONArray("girias")
        val lista = buildList {
            for (i in 0 until (girias?.length() ?: 0)) {
                val item = girias!!.getJSONObject(i)
                val posicao = item.getJSONArray("posicao")
                add(
                    GiriaDetectada(
                        termo = item.getString("termo"),
                        inicio = posicao.getInt(0),
                        fim = posicao.getInt(1),
                        explicacao = item.optStringOuNulo("explicacao"),
                        equivalenteFormal = item.optStringOuNulo("equivalenteFormal"),
                        nsfw = item.optBoolean("nsfw"),
                        riscoMenor = item.optBoolean("riscoMenor"),
                        confianca = item.optDouble("confianca", 1.0),
                        origem = runCatching {
                            OrigemDaResposta.valueOf(item.getString("origem"))
                        }.getOrDefault(OrigemDaResposta.DICIONARIO),
                    ),
                )
            }
        }

        return RespostaDeTraducao(
            idiomaDetectado = json.optStringOuNulo("idiomaDetectado"),
            girias = lista,
            geradoPorIa = json.optBoolean("geradoPorIa"),
            naoResolvidos = json.optJSONArray("naoResolvidos")?.let { array ->
                buildList { for (i in 0 until array.length()) add(array.getString(i)) }
            } ?: emptyList(),
        )
    }
}

/**
 * `optString` do org.json devolve a string "null" quando o campo é nulo no
 * JSON, e não `null`. Sem este cuidado a interface exibiria a palavra "null"
 * ao usuário no lugar da explicação ausente.
 */
private fun JSONObject.optStringOuNulo(chave: String): String? =
    if (isNull(chave)) null else optString(chave).takeIf { it.isNotEmpty() }
