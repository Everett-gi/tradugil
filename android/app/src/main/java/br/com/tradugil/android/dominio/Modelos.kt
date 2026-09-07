package br.com.tradugil.android.dominio

/** Espelha `TraducaoDtos` no servidor e `tipos.ts` nos clientes web. */

enum class NivelDeExplicacao { SIMPLES, DETALHADA }

/** De qual nível da cascata veio a resposta. A interface é obrigada a exibir. */
enum class OrigemDaResposta { DICIONARIO, EXTERNA, IA }

data class GiriaDetectada(
    val termo: String,
    /** Início inclusivo, fim exclusivo: pronto para `substring`. */
    val inicio: Int,
    val fim: Int,
    val explicacao: String?,
    val equivalenteFormal: String?,
    val nsfw: Boolean,
    val riscoMenor: Boolean,
    val confianca: Double,
    val origem: OrigemDaResposta,
)

data class RespostaDeTraducao(
    val idiomaDetectado: String?,
    val girias: List<GiriaDetectada>,
    val geradoPorIa: Boolean,
    val naoResolvidos: List<String>,
)

data class PedidoDeTraducao(
    val texto: String,
    val nivel: NivelDeExplicacao = NivelDeExplicacao.SIMPLES,
    val contexto: String? = null,
    val modoFamilia: Boolean = true,
)

/** Falha ao falar com o servidor. Separada para a interface decidir se cai no local. */
class ErroDeRede(causa: Throwable?) : Exception("Não foi possível falar com o servidor.", causa)
