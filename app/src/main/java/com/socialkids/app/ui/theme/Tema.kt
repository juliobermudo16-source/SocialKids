package com.socialkids.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.socialkids.app.data.repository.Ajustes

private val Color707E92 = androidx.compose.ui.graphics.Color(0xFF707E92)
private val Color9FB3C8 = androidx.compose.ui.graphics.Color(0xFF9FB3C8)

private val EsquemaClaro = lightColorScheme(
    primary = Paleta.Turquesa,
    onPrimary = Paleta.Blanco,
    primaryContainer = Paleta.Cielo,
    onPrimaryContainer = Paleta.TextoOscuro,
    secondary = Paleta.Coral,
    onSecondary = Paleta.Blanco,
    secondaryContainer = Paleta.Arena,
    onSecondaryContainer = Paleta.TextoOscuro,
    tertiary = Paleta.Violeta,
    onTertiary = Paleta.Blanco,
    background = Paleta.Papel,
    onBackground = Paleta.TextoOscuro,
    surface = Paleta.Blanco,
    onSurface = Paleta.TextoOscuro,
    surfaceVariant = Paleta.PapelSuave,
    onSurfaceVariant = Color707E92,
    outline = Color707E92,
    error = Paleta.Error
)

private val EsquemaOscuro = darkColorScheme(
    primary = Paleta.Cielo,
    onPrimary = Paleta.NocheProfunda,
    primaryContainer = Paleta.Turquesa,
    onPrimaryContainer = Paleta.TextoClaro,
    secondary = Paleta.Coral,
    onSecondary = Paleta.NocheProfunda,
    secondaryContainer = Paleta.NocheSuave,
    onSecondaryContainer = Paleta.TextoClaro,
    tertiary = Paleta.Lavanda,
    onTertiary = Paleta.NocheProfunda,
    background = Paleta.NocheProfunda,
    onBackground = Paleta.TextoClaro,
    surface = Paleta.Noche,
    onSurface = Paleta.TextoClaro,
    surfaceVariant = Paleta.NocheSuave,
    onSurfaceVariant = Color9FB3C8,
    outline = Color9FB3C8,
    error = Paleta.Error
)

private val Redondas = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(36.dp)
)

private fun tipografia(escala: Float): Typography {
    val familia = FontFamily.SansSerif
    fun estilo(tamanio: Float, peso: FontWeight, alto: Float, espaciado: Float = 0f) = TextStyle(
        fontFamily = familia,
        fontWeight = peso,
        fontSize = (tamanio * escala).sp,
        lineHeight = (alto * escala).sp,
        letterSpacing = espaciado.sp
    )
    return Typography(
        displayLarge = estilo(40f, FontWeight.Black, 46f, (-0.5f)),
        displayMedium = estilo(32f, FontWeight.Black, 38f, (-0.3f)),
        headlineLarge = estilo(28f, FontWeight.ExtraBold, 34f),
        headlineMedium = estilo(23f, FontWeight.ExtraBold, 29f),
        headlineSmall = estilo(20f, FontWeight.Bold, 26f),
        titleLarge = estilo(19f, FontWeight.Bold, 25f),
        titleMedium = estilo(17f, FontWeight.Bold, 23f),
        titleSmall = estilo(15f, FontWeight.SemiBold, 20f),
        bodyLarge = estilo(16f, FontWeight.Normal, 24f),
        bodyMedium = estilo(15f, FontWeight.Normal, 22f),
        bodySmall = estilo(13f, FontWeight.Normal, 19f),
        labelLarge = estilo(15f, FontWeight.Bold, 20f, 0.2f),
        labelMedium = estilo(13f, FontWeight.Bold, 17f, 0.2f),
        labelSmall = estilo(11f, FontWeight.SemiBold, 15f, 0.4f)
    )
}

val LocalAjustes = staticCompositionLocalOf { Ajustes() }

@Composable
fun SocialKidsTheme(
    ajustes: Ajustes = Ajustes(),
    oscuro: Boolean = isSystemInDarkTheme(),
    contenido: @Composable () -> Unit
) {
    val esquema = when {
        ajustes.altoContraste && oscuro -> EsquemaOscuro.copy(
            background = androidx.compose.ui.graphics.Color.Black,
            surface = androidx.compose.ui.graphics.Color(0xFF101010),
            onSurface = androidx.compose.ui.graphics.Color.White
        )
        ajustes.altoContraste -> EsquemaClaro.copy(
            background = androidx.compose.ui.graphics.Color.White,
            surface = androidx.compose.ui.graphics.Color.White,
            onSurface = androidx.compose.ui.graphics.Color.Black
        )
        oscuro -> EsquemaOscuro
        else -> EsquemaClaro
    }
    CompositionLocalProvider(LocalAjustes provides ajustes) {
        MaterialTheme(
            colorScheme = esquema,
            typography = tipografia(if (ajustes.textoGrande) 1.18f else 1f),
            shapes = Redondas,
            content = contenido
        )
    }
}
