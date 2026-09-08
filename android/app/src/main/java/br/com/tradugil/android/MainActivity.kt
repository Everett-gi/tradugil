package br.com.tradugil.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.runtime.collectAsState
import androidx.room.Room
import br.com.tradugil.android.dados.ClienteDaApi
import br.com.tradugil.android.dados.DicionarioLocal
import br.com.tradugil.android.dados.RepositorioDeTraducao
import br.com.tradugil.android.dados.local.BancoLocal
import br.com.tradugil.android.dominio.TextoRecebido
import br.com.tradugil.android.ui.PrincipalViewModel
import br.com.tradugil.android.ui.TelaPrincipal
import br.com.tradugil.android.ui.TemaDoTradugil

class MainActivity : ComponentActivity() {

    /*
     * Criado antes do setContent, e não com viewModel() lá dentro.
     *
     * onNewIntent pode chegar a qualquer momento depois que a activity
     * existe, e precisa de um modelo pronto para entregar o texto. Buscar o
     * modelo dentro da composição deixaria uma janela em que ele ainda não
     * existe, e o texto que a pessoa acabou de selecionar seria descartado
     * em silêncio. O ViewModelProvider devolve o mesmo objeto que o
     * composable usaria: o escopo é a activity nos dois casos.
     */
    private val modelo: PrincipalViewModel by lazy {
        ViewModelProvider(this, fabrica())[PrincipalViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        /*
         * Aqui, e não dentro do composable.
         *
         * A versão anterior lia o intent no corpo da função de composição.
         * Chamar o ViewModel durante a composição é efeito colateral na fase
         * errada: o Compose pode recompor várias vezes e pode descartar uma
         * composição pela metade. Aquilo só não consultava o servidor duas
         * vezes por giro de tela porque apagava o extra na primeira passagem,
         * ou seja, dependia de um efeito colateral para conter o outro.
         */
        consumirIntent(intent)

        setContent {
            TemaDoTradugil {
                val estado by modelo.estado.collectAsState()

                Surface(color = MaterialTheme.colorScheme.background) {
                    Scaffold { interno ->
                        TelaPrincipal(
                            estado = estado,
                            aoDigitar = modelo::aoDigitar,
                            aoDefinirNivel = modelo::definirNivel,
                            aoAlternarModoFamilia = modelo::alternarModoFamilia,
                            aoConsultar = modelo::consultar,
                            modifier = Modifier
                                .padding(interno)
                                .consumeWindowInsets(WindowInsets.safeDrawing),
                        )
                    }
                }
            }
        }
    }

    /**
     * Texto que chega com o app já aberto.
     *
     * Sem isto, a segunda seleção seguida cairia num `onNewIntent` que
     * ninguém atende, e a tela continuaria mostrando a resposta da primeira.
     * Casa com `launchMode="singleTop"` no manifesto: é ele que faz o sistema
     * reaproveitar esta instância em vez de empilhar outra.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        consumirIntent(intent)
    }

    /**
     * Traduz o `Intent` para valores simples e entrega a decisão a
     * [TextoRecebido], que é onde a regra fica coberta por teste.
     */
    private fun consumirIntent(recebido: Intent?) {
        if (recebido == null) return

        val texto = TextoRecebido.extrair(
            acao = recebido.action,
            compartilhado = recebido.getCharSequenceExtra(Intent.EXTRA_TEXT),
            selecionado = recebido.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT),
        ) ?: return

        // Consome os extras para o mesmo texto não ser consultado de novo
        // quando a activity voltar do fundo com o intent antigo ainda preso.
        recebido.removeExtra(Intent.EXTRA_TEXT)
        recebido.removeExtra(Intent.EXTRA_PROCESS_TEXT)

        modelo.receberDeFora(texto)
    }

    private fun fabrica(): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val banco = Room.databaseBuilder(
                applicationContext,
                BancoLocal::class.java,
                "tradugil",
            ).build()

            PrincipalViewModel(
                RepositorioDeTraducao(
                    api = ClienteDaApi(BuildConfig.URL_DA_API),
                    local = DicionarioLocal(banco.dicionario()),
                ),
            )
        }
    }
}
