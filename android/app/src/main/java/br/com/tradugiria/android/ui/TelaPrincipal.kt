package br.com.tradugiria.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.tradugiria.android.dominio.GiriaDetectada
import br.com.tradugiria.android.dominio.NivelDeExplicacao
import br.com.tradugiria.android.dominio.OrigemDaResposta

/**
 * Tela única do aplicativo.
 *
 * Segue as mesmas regras de acessibilidade da versão web: texto grande por
 * padrão, alvos de toque de 48dp e nenhuma informação transmitida só por cor.
 */
@Composable
fun TelaPrincipal(
    estado: EstadoDaTela,
    aoDigitar: (String) -> Unit,
    aoDefinirNivel: (NivelDeExplicacao) -> Unit,
    aoAlternarModoFamilia: () -> Unit,
    aoConsultar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "TraduGíria",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Não entendeu uma palavra ou uma mensagem? Escreva ou cole " +
                "aqui que a gente explica em linguagem simples.",
            style = MaterialTheme.typography.bodyLarge,
        )

        OutlinedTextField(
            value = estado.texto,
            onValueChange = aoDigitar,
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 120.dp),
            label = { Text("Palavra ou mensagem") },
            placeholder = { Text("Exemplo: mano, ele clutchou a round, foi mt pog") },
            textStyle = MaterialTheme.typography.bodyLarge,
        )

        Text(
            text = "O que você escreve aqui não é guardado em nenhum lugar.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = estado.nivel == NivelDeExplicacao.SIMPLES,
                onClick = { aoDefinirNivel(NivelDeExplicacao.SIMPLES) },
                label = { Text("Simples") },
                modifier = Modifier.sizeIn(minHeight = 48.dp),
            )
            FilterChip(
                selected = estado.nivel == NivelDeExplicacao.DETALHADA,
                onClick = { aoDefinirNivel(NivelDeExplicacao.DETALHADA) },
                label = { Text("Detalhada") },
                modifier = Modifier.sizeIn(minHeight = 48.dp),
            )
            FilterChip(
                selected = estado.modoFamilia,
                onClick = { aoAlternarModoFamilia() },
                label = { Text("Modo Família") },
                modifier = Modifier.sizeIn(minHeight = 48.dp),
            )
        }

        Button(
            onClick = aoConsultar,
            enabled = !estado.consultando && estado.texto.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 56.dp),
        ) {
            if (estado.consultando) {
                CircularProgressIndicator(
                    modifier = Modifier.sizeIn(maxHeight = 24.dp, maxWidth = 24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text("Explicar", style = MaterialTheme.typography.titleMedium)
            }
        }

        estado.erro?.let { mensagem ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                ),
            ) {
                Text(
                    text = mensagem,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }

        estado.resultado?.let { resultado ->
            Spacer(Modifier.height(4.dp))
            if (resultado.girias.isEmpty()) {
                Text(
                    "Não encontramos nenhuma gíria conhecida neste texto.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            } else {
                Text(
                    text = if (resultado.girias.size == 1) {
                        "Encontramos 1 gíria"
                    } else {
                        "Encontramos ${resultado.girias.size} gírias"
                    },
                    style = MaterialTheme.typography.titleLarge,
                )
                TextoComDestaques(estado.textoAnalisado, resultado.girias)
                resultado.girias.forEach { giria -> CartaoDaGiria(giria) }
            }
        }
    }
}

/**
 * Reconstrói o texto original com as gírias em destaque.
 *
 * Recorta por `inicio`/`fim` direto, sem ajuste, porque o servidor devolve
 * intervalo semiaberto — a mesma convenção de `substring`. Os testes do
 * tokenizador nas três plataformas garantem que isso continue verdade.
 */
@Composable
private fun TextoComDestaques(texto: String, girias: List<GiriaDetectada>) {
    val anotado = buildAnnotatedString {
        var cursor = 0
        girias.sortedBy { it.inicio }.forEach { giria ->
            // Posição inválida não pode derrubar a tela inteira.
            if (giria.inicio < cursor || giria.fim > texto.length || giria.inicio >= giria.fim) {
                return@forEach
            }
            append(texto.substring(cursor, giria.inicio))
            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    // Negrito e sublinhado além do fundo: quem não distingue
                    // cores precisa enxergar que a palavra está marcada.
                    textDecoration = TextDecoration.Underline,
                    background = if (giria.riscoMenor) {
                        Color(0xFFFDECEA)
                    } else {
                        Color(0xFFFFF3C4)
                    },
                    color = Color(0xFF16202A),
                ),
            ) {
                append(texto.substring(giria.inicio, giria.fim))
            }
            cursor = giria.fim
        }
        if (cursor < texto.length) append(texto.substring(cursor))
    }

    SelectionContainer {
        Text(
            text = anotado,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
        )
    }
}

@Composable
private fun CartaoDaGiria(giria: GiriaDetectada) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = giria.termo,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            // A seção 7.1 do documento exige que toda resposta de IA apareça
            // marcada como não verificada. Sem isso, um palpite do modelo
            // passaria por verbete revisado por pessoa.
            if (giria.origem == OrigemDaResposta.IA) {
                Text(
                    text = "Explicação gerada por inteligência artificial — " +
                        "ainda não conferida por uma pessoa.",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }

            Text(
                text = giria.explicacao ?: "Ainda não temos uma explicação para este termo.",
                style = MaterialTheme.typography.bodyLarge,
            )

            giria.equivalenteFormal?.let {
                Text(
                    text = "Em outras palavras: $it",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (giria.riscoMenor) {
                Text(
                    text = "⚠️ Este termo costuma aparecer em conversas que merecem " +
                        "atenção. Se você acompanha o uso de redes de um adolescente, " +
                        "pode valer uma conversa sobre o assunto.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
