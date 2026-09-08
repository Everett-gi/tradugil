package br.com.tradugil.android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.tradugil.android.dados.RepositorioDeTraducao
import br.com.tradugil.android.dominio.NivelDeExplicacao
import br.com.tradugil.android.dominio.PedidoDeTraducao
import br.com.tradugil.android.dominio.RespostaDeTraducao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EstadoDaTela(
    val texto: String = "",
    val consultando: Boolean = false,
    val nivel: NivelDeExplicacao = NivelDeExplicacao.SIMPLES,
    /**
     * Ligado por padrão. A escolha protege o caso em que errar custa caro
     * (uma criança recebendo conteúdo impróprio) em vez do caso em que errar
     * apenas incomoda.
     */
    val modoFamilia: Boolean = true,
    val resultado: RespostaDeTraducao? = null,
    /** Texto que gerou o resultado, para o destaque casar com as posições. */
    val textoAnalisado: String = "",
    val erro: String? = null,
    val semServidor: Boolean = false,
)

class PrincipalViewModel(private val repositorio: RepositorioDeTraducao) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoDaTela())
    val estado: StateFlow<EstadoDaTela> = _estado.asStateFlow()

    fun aoDigitar(texto: String) = _estado.update { it.copy(texto = texto) }

    fun definirNivel(nivel: NivelDeExplicacao) = _estado.update { it.copy(nivel = nivel) }

    fun alternarModoFamilia() = _estado.update { it.copy(modoFamilia = !it.modoFamilia) }

    /**
     * Texto que chegou de outro aplicativo: compartilhado, ou selecionado na
     * tela e enviado pelo menu do sistema.
     *
     * Consulta na hora, sem esperar toque nenhum. Quem chegou por este
     * caminho ja disse o que queria ao escolher o Tradugil no menu; pedir
     * mais um toque seria repetir a pergunta que a pessoa acabou de
     * responder.
     */
    fun receberDeFora(texto: String) {
        _estado.update { it.copy(texto = texto) }
        consultar()
    }

    fun consultar() {
        val consulta = _estado.value.texto.trim()
        if (consulta.isEmpty() || _estado.value.consultando) return

        _estado.update { it.copy(consultando = true, erro = null, semServidor = false) }

        viewModelScope.launch {
            try {
                val resultado = repositorio.traduzir(
                    PedidoDeTraducao(
                        texto = consulta,
                        nivel = _estado.value.nivel,
                        modoFamilia = _estado.value.modoFamilia,
                    ),
                )
                _estado.update {
                    it.copy(
                        consultando = false,
                        resultado = resultado.resposta,
                        textoAnalisado = consulta,
                        semServidor = resultado.offline,
                    )
                }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(
                        consultando = false,
                        erro = "Não foi possível consultar agora. " +
                            "Tente novamente em instantes.",
                    )
                }
            }
        }
    }
}
