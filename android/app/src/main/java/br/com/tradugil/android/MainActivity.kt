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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.runtime.collectAsState
import androidx.room.Room
import br.com.tradugil.android.dados.ClienteDaApi
import br.com.tradugil.android.dados.DicionarioLocal
import br.com.tradugil.android.dados.RepositorioDeTraducao
import br.com.tradugil.android.dados.local.BancoLocal
import br.com.tradugil.android.ui.PrincipalViewModel
import br.com.tradugil.android.ui.TelaPrincipal

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                val modelo: PrincipalViewModel = viewModel(factory = fabrica())
                val estado by modelo.estado.collectAsState()

                // Texto vindo da folha de compartilhamento de outro app
                // (RF20). É o caminho mais curto do produto: a pessoa
                // seleciona no WhatsApp, compartilha, e já vê a explicação.
                intent?.let { recebido ->
                    if (recebido.action == Intent.ACTION_SEND &&
                        recebido.type == "text/plain"
                    ) {
                        recebido.getStringExtra(Intent.EXTRA_TEXT)?.let { texto ->
                            // Consome o extra para a consulta não se repetir a
                            // cada recomposição e a cada giro de tela.
                            recebido.removeExtra(Intent.EXTRA_TEXT)
                            modelo.receberCompartilhado(texto)
                        }
                    }
                }

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
