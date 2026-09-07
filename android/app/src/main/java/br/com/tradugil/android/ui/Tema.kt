package br.com.tradugil.android.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * O tema do aplicativo.
 *
 * <h2>Por que ele existe</h2>
 *
 * Antes disto o app usava o `MaterialTheme` sem argumento nenhum, o que
 * significa a paleta roxa padrão do Material 3. O site é areia quente com
 * tinta verde-azulada e o aplicativo era roxo: a mesma marca com duas caras,
 * e quem baixasse o APK pelo site não reconheceria onde chegou.
 *
 * As cores aqui são exatamente as de `web/src/estilos.css`. Quando uma
 * mudar, as duas mudam juntas, ou volta a divergir.
 *
 * <h2>Por que sem cor dinâmica</h2>
 *
 * O Android 12 em diante oferece Material You, que tira a paleta do papel de
 * parede da pessoa. É bonito e aqui seria errado: o amarelo do marca-texto e
 * o vermelho do termo que pede atenção carregam significado, e um esquema
 * gerado pelo sistema pode aproximá-los a ponto de um virar o outro. O aviso
 * que o Roberto precisa enxergar não pode depender do papel de parede dele.
 *
 * O `Build.VERSION` fica citado no comentário e não no código de propósito:
 * não há ramo condicional a manter, porque a decisão vale para toda versão.
 */

// ------------------------------------------------------------------ claro ---

private val AreiaClara = Color(0xFFFBF6F0)
private val FolhaClara = Color(0xFFFFFFFF)
private val TintaClara = Color(0xFF17282C)
private val TintaSuaveClara = Color(0xFF4D666B)
private val MarcaClara = Color(0xFF0A5560)
private val MarcaTenueClara = Color(0xFFDCEDF0)
private val BordaClara = Color(0xFFE2D6C8)
private val RiscoFundoClaro = Color(0xFFFDECE8)
private val RiscoTextoClaro = Color(0xFF7A1A15)

// ----------------------------------------------------------------- escuro ---

private val AreiaEscura = Color(0xFF0E1719)
private val FolhaEscura = Color(0xFF18272B)
private val TintaEscura = Color(0xFFE9F1F2)
private val TintaSuaveEscura = Color(0xFFA4BCC0)
private val MarcaEscura = Color(0xFF6FD0DD)
private val MarcaTenueEscura = Color(0xFF14343A)
private val BordaEscura = Color(0xFF2B3D41)
private val RiscoFundoEscuro = Color(0xFF3A1613)
private val RiscoTextoEscuro = Color(0xFFFFDCD6)

/**
 * Cores que carregam significado, e por isso não moram no `colorScheme`.
 *
 * O `colorScheme` do Material é sobre hierarquia visual (primário,
 * secundário, superfície). Estas duas são sobre conteúdo: amarelo é "isto é
 * uma gíria" e vermelho é "isto pede atenção". Guardá-las junto com as
 * outras convidaria alguém a usar o amarelo para decorar um botão, e no dia
 * em que isso acontecer o marcador deixa de significar coisa alguma.
 */
object CoresDoConteudo {

    val marcador: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFFD9AB12) else Color(0xFFFFD84D)

    val marcadorTexto: Color
        @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1A1400) else Color(0xFF1A1400)

    val riscoFundo: Color
        @Composable get() = if (isSystemInDarkTheme()) RiscoFundoEscuro else RiscoFundoClaro

    val riscoTexto: Color
        @Composable get() = if (isSystemInDarkTheme()) RiscoTextoEscuro else RiscoTextoClaro
}

private val EsquemaClaro = lightColorScheme(
    primary = MarcaClara,
    onPrimary = Color.White,
    primaryContainer = MarcaTenueClara,
    onPrimaryContainer = TintaClara,
    secondary = MarcaClara,
    onSecondary = Color.White,
    secondaryContainer = MarcaTenueClara,
    onSecondaryContainer = TintaClara,
    tertiary = Color(0xFF5B3A8E),
    onTertiary = Color.White,
    background = AreiaClara,
    onBackground = TintaClara,
    surface = FolhaClara,
    onSurface = TintaClara,
    surfaceVariant = AreiaClara,
    onSurfaceVariant = TintaSuaveClara,
    outline = Color(0xFF75898D),
    outlineVariant = BordaClara,
    error = Color(0xFFA3231C),
    onError = Color.White,
    errorContainer = RiscoFundoClaro,
    onErrorContainer = RiscoTextoClaro,
)

private val EsquemaEscuro = darkColorScheme(
    primary = MarcaEscura,
    onPrimary = Color(0xFF04191D),
    primaryContainer = MarcaTenueEscura,
    onPrimaryContainer = TintaEscura,
    secondary = MarcaEscura,
    onSecondary = Color(0xFF04191D),
    secondaryContainer = MarcaTenueEscura,
    onSecondaryContainer = TintaEscura,
    tertiary = Color(0xFFC3ABE9),
    onTertiary = Color(0xFF241A37),
    background = AreiaEscura,
    onBackground = TintaEscura,
    surface = FolhaEscura,
    onSurface = TintaEscura,
    surfaceVariant = AreiaEscura,
    onSurfaceVariant = TintaSuaveEscura,
    outline = Color(0xFF6F8A8F),
    outlineVariant = BordaEscura,
    error = Color(0xFFF2A99F),
    onError = Color(0xFF3A1613),
    errorContainer = RiscoFundoEscuro,
    onErrorContainer = RiscoTextoEscuro,
)

/**
 * Tipografia com corpo maior que o padrão do Material.
 *
 * O `bodyLarge` padrão é 16sp. Aqui é 18sp, pelo mesmo motivo que o site
 * parte de 18px: o público principal tem 68 anos, e o tamanho confortável
 * precisa ser o padrão, não algo que a pessoa descubra num ajuste.
 *
 * Tudo em sp, nunca dp: sp acompanha o tamanho de fonte configurado no
 * Android, e é isso que faz o aplicativo respeitar quem já aumentou a letra
 * no aparelho inteiro.
 */
private val Tipografia = Typography().let { padrao ->
    padrao.copy(
        headlineMedium = padrao.headlineMedium.copy(
            fontSize = 30.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        titleLarge = padrao.titleLarge.copy(
            fontSize = 24.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        bodyLarge = padrao.bodyLarge.copy(fontSize = 18.sp, lineHeight = 27.sp),
        bodyMedium = padrao.bodyMedium.copy(fontSize = 16.sp, lineHeight = 24.sp),
        titleMedium = padrao.titleMedium.copy(fontSize = 18.sp),
        labelLarge = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold),
    )
}

@Composable
fun TemaDoTradugil(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) EsquemaEscuro else EsquemaClaro,
        typography = Tipografia,
        content = content,
    )
}
