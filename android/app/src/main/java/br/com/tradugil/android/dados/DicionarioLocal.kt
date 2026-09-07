package br.com.tradugil.android.dados

import br.com.tradugil.android.dados.local.DicionarioDao
import br.com.tradugil.android.dados.local.VariacaoLocal
import br.com.tradugil.android.dados.local.VerbeteLocal
import br.com.tradugil.android.dominio.GiriaDetectada
import br.com.tradugil.android.dominio.NivelDeExplicacao
import br.com.tradugil.android.dominio.Normalizador
import br.com.tradugil.android.dominio.OrigemDaResposta
import br.com.tradugil.android.dominio.RespostaDeTraducao
import br.com.tradugil.android.dominio.Tokenizador

/**
 * Traduz usando apenas o que está no aparelho.
 *
 * <p>Reproduz o casamento guloso do servidor: candidatos mais longos
 * primeiro, e nenhum caractere reclamado duas vezes. Sem isso, "dar ruim"
 * viria acompanhado de "ruim" sozinho e a tela desenharia dois destaques
 * sobrepostos.</p>
 *
 * <p>Usa `Tokenizador` e `Normalizador` do domínio: as mesmas funções que o
 * servidor usa em Java e o site em TypeScript. É o que garante que o texto
 * seja quebrado nas mesmas posições com ou sem internet: se esta classe
 * tivesse a própria lógica, o mesmo texto produziria destaques em lugares
 * diferentes conforme a conexão.</p>
 */
class DicionarioLocal(private val dao: DicionarioDao) {

    private companion object {
        /** Termo curto demais para valer uma linha na fila de curadoria. */
        const val TAMANHO_MINIMO_PARA_FILA = 3
    }

    suspend fun quantidadeGuardada(): Int = dao.quantidade()

    suspend fun traduzir(
        texto: String,
        nivel: NivelDeExplicacao,
        modoFamilia: Boolean,
    ): RespostaDeTraducao {
        val candidatos = Tokenizador.candidatos(Tokenizador.tokenizar(texto))
        if (candidatos.isEmpty()) {
            return RespostaDeTraducao(null, emptyList(), false, emptyList())
        }

        val chaves = candidatos.map { it.normalizado }.distinct()
        val colapsadas = chaves.map { Normalizador.colapsarRepeticoes(it) }.distinct()

        // Indexado pelas quatro formas de alcance, para o laço de detecção
        // encontrar o verbete perguntando pela forma que ele tem em mãos.
        val porChave = mutableMapOf<String, VerbeteLocal>()
        dao.buscarPorCandidatos(chaves, colapsadas).forEach { verbete ->
            porChave[verbete.termoNormalizado] = verbete
            porChave.putIfAbsent(verbete.termoColapsado, verbete)
        }
        candidatos.forEach { candidato ->
            if (!porChave.containsKey(candidato.normalizado)) {
                porChave[Normalizador.colapsarRepeticoes(candidato.normalizado)]?.let {
                    porChave[candidato.normalizado] = it
                }
            }
        }

        val detectadas = mutableListOf<GiriaDetectada>()
        val naoResolvidos = linkedSetOf<String>()
        val ocupado = BooleanArray(texto.length)

        for (candidato in candidatos) {
            if (intervaloOcupado(ocupado, candidato.inicio, candidato.fim)) continue

            val verbete = porChave[candidato.normalizado]
            if (verbete == null) {
                if (!candidato.normalizado.contains(" ") &&
                    candidato.normalizado.length >= TAMANHO_MINIMO_PARA_FILA
                ) {
                    naoResolvidos += candidato.normalizado
                }
                continue
            }

            // Modo Família: o impróprio some, o de risco fica e vem marcado.
            if (modoFamilia && verbete.nsfw) {
                marcarOcupado(ocupado, candidato.inicio, candidato.fim)
                continue
            }

            detectadas += GiriaDetectada(
                termo = verbete.termo,
                inicio = candidato.inicio,
                fim = candidato.fim,
                explicacao = if (nivel == NivelDeExplicacao.DETALHADA &&
                    verbete.explicacaoDetalhada != null
                ) {
                    verbete.explicacaoDetalhada
                } else {
                    verbete.explicacaoSimples
                },
                equivalenteFormal = verbete.equivalenteFormal,
                nsfw = verbete.nsfw,
                riscoMenor = verbete.riscoMenor,
                confianca = 1.0,
                origem = OrigemDaResposta.DICIONARIO,
            )
            marcarOcupado(ocupado, candidato.inicio, candidato.fim)
        }

        return RespostaDeTraducao(
            idiomaDetectado = null,
            girias = detectadas.sortedBy { it.inicio },
            geradoPorIa = false,
            naoResolvidos = naoResolvidos.toList(),
        )
    }

    /**
     * Guarda o que o servidor acabou de resolver.
     *
     * <p>É o que faz o dicionário do aparelho crescer com o uso: os termos
     * que a pessoa realmente consulta ficam disponíveis offline na próxima
     * vez, sem precisar baixar o pacote inteiro.</p>
     *
     * <p>Só guarda o que veio do dicionário curado. Resposta de IA não entra:
     * ela é explicitamente não verificada, e gravá-la aqui a transformaria,
     * na consulta seguinte, num verbete offline indistinguível dos revisados.</p>
     */
    suspend fun memorizar(resposta: RespostaDeTraducao) {
        val verbetes = resposta.girias
            .filter { it.origem == OrigemDaResposta.DICIONARIO && it.explicacao != null }
            .map { giria ->
                val normalizado = Normalizador.normalizar(giria.termo)
                VerbeteLocal(
                    termoNormalizado = normalizado,
                    termoColapsado = Normalizador.colapsarRepeticoes(normalizado),
                    termo = giria.termo,
                    idioma = resposta.idiomaDetectado ?: "pt-BR",
                    explicacaoSimples = giria.explicacao!!,
                    explicacaoDetalhada = null,
                    equivalenteFormal = giria.equivalenteFormal,
                    nsfw = giria.nsfw,
                    riscoMenor = giria.riscoMenor,
                )
            }

        if (verbetes.isNotEmpty()) {
            dao.guardar(verbetes, emptyList<VariacaoLocal>())
        }
    }

    private fun intervaloOcupado(ocupado: BooleanArray, inicio: Int, fim: Int): Boolean {
        for (i in inicio until fim) if (ocupado[i]) return true
        return false
    }

    private fun marcarOcupado(ocupado: BooleanArray, inicio: Int, fim: Int) {
        for (i in inicio until fim) ocupado[i] = true
    }
}
