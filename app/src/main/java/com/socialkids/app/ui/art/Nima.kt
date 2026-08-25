package com.socialkids.app.ui.art

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.socialkids.app.R
import com.socialkids.app.ui.theme.LocalAjustes
import com.socialkids.app.ui.theme.Paleta
import kotlin.math.sin

/** Estados de animo de Nima, la guia de la Isla Conecta. */
enum class EstadoNima(val tono: Color) {
    NEUTRAL(Paleta.Turquesa),
    ALEGRE(Paleta.Sol),
    PENSATIVA(Paleta.Lavanda),
    SORPRENDIDA(Paleta.Cielo),
    ANIMANDO(Paleta.Coral),
    TRISTE(Paleta.Azul)
}

/**
 * Nima: criatura redonda con orejas largas y una antena que se enciende
 * con el color de la emocion. Dibujada integramente con Canvas, sin imagenes externas.
 */
@Composable
fun Nima(
    estado: EstadoNima = EstadoNima.NEUTRAL,
    tamanio: Dp = 120.dp,
    modifier: Modifier = Modifier
) {
    val animaciones = LocalAjustes.current.animaciones
    val transicion = rememberInfiniteTransition(label = "nima")
    val flote by transicion.animateFloat(
        initialValue = 0f,
        targetValue = if (animaciones) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Reverse),
        label = "flote"
    )
    val parpadeo by transicion.animateFloat(
        initialValue = 0f,
        targetValue = if (animaciones) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(3400), RepeatMode.Restart),
        label = "parpadeo"
    )

    val descripcion = stringResource(R.string.desc_nima)
    Canvas(
        modifier = modifier
            .size(tamanio)
            .semantics { contentDescription = descripcion }
    ) {
        val desplazamiento = (sin(flote * Math.PI) * size.height * 0.035).toFloat()
        translate(top = -desplazamiento) {
            dibujarNima(estado, parpadeo)
        }
    }
}

private fun DrawScope.dibujarNima(estado: EstadoNima, parpadeo: Float) {
    val w = size.width
    val h = size.height
    val centro = Offset(w / 2f, h * 0.60f)
    val radio = w * 0.30f
    val tono = estado.tono

    // Halo de luz
    drawCircle(
        brush = Brush.radialGradient(
            listOf(tono.copy(alpha = 0.35f), Color.Transparent),
            center = centro,
            radius = radio * 2.1f
        ),
        radius = radio * 2.1f,
        center = centro
    )

    // Sombra en el suelo
    drawOval(
        color = Color.Black.copy(alpha = 0.12f),
        topLeft = Offset(centro.x - radio * 0.85f, h * 0.90f),
        size = Size(radio * 1.7f, h * 0.055f)
    )

    // Orejas largas
    val oreja = Path().apply {
        moveTo(centro.x - radio * 0.55f, centro.y - radio * 0.55f)
        cubicTo(
            centro.x - radio * 1.15f, centro.y - radio * 1.5f,
            centro.x - radio * 0.95f, centro.y - radio * 2.3f,
            centro.x - radio * 0.35f, centro.y - radio * 1.75f
        )
        cubicTo(
            centro.x - radio * 0.20f, centro.y - radio * 1.35f,
            centro.x - radio * 0.25f, centro.y - radio * 0.95f,
            centro.x - radio * 0.55f, centro.y - radio * 0.55f
        )
        close()
    }
    drawPath(oreja, color = tono.copy(alpha = 0.9f))

    val orejaDerecha = Path().apply {
        moveTo(centro.x + radio * 0.55f, centro.y - radio * 0.55f)
        cubicTo(
            centro.x + radio * 1.15f, centro.y - radio * 1.5f,
            centro.x + radio * 0.95f, centro.y - radio * 2.3f,
            centro.x + radio * 0.35f, centro.y - radio * 1.75f
        )
        cubicTo(
            centro.x + radio * 0.20f, centro.y - radio * 1.35f,
            centro.x + radio * 0.25f, centro.y - radio * 0.95f,
            centro.x + radio * 0.55f, centro.y - radio * 0.55f
        )
        close()
    }
    drawPath(orejaDerecha, color = tono.copy(alpha = 0.75f))

    // Cuerpo
    drawCircle(
        brush = Brush.linearGradient(
            listOf(Color.White, tono.copy(alpha = 0.30f)),
            start = Offset(centro.x - radio, centro.y - radio),
            end = Offset(centro.x + radio, centro.y + radio)
        ),
        radius = radio,
        center = centro
    )
    drawCircle(color = tono, radius = radio, center = centro, style = Stroke(width = w * 0.030f))

    // Antena con chispa
    val antenaBase = Offset(centro.x, centro.y - radio)
    val antenaPunta = Offset(centro.x + radio * 0.15f, centro.y - radio * 1.75f)
    drawPath(
        Path().apply {
            moveTo(antenaBase.x, antenaBase.y)
            quadraticBezierTo(
                centro.x - radio * 0.25f, centro.y - radio * 1.4f,
                antenaPunta.x, antenaPunta.y
            )
        },
        color = tono,
        style = Stroke(width = w * 0.022f)
    )
    drawCircle(tono.copy(alpha = 0.35f), radius = w * 0.075f, center = antenaPunta)
    drawCircle(tono, radius = w * 0.042f, center = antenaPunta)

    // Ojos
    val separacion = radio * 0.42f
    val alturaOjos = centro.y - radio * 0.10f
    val cerrando = parpadeo > 0.92f
    val radioOjo = w * 0.045f
    listOf(-1, 1).forEach { lado ->
        val pos = Offset(centro.x + lado * separacion, alturaOjos)
        if (cerrando || estado == EstadoNima.PENSATIVA) {
            drawLine(
                color = Paleta.TextoOscuro,
                start = Offset(pos.x - radioOjo, pos.y),
                end = Offset(pos.x + radioOjo, pos.y),
                strokeWidth = w * 0.018f
            )
        } else {
            val escala = if (estado == EstadoNima.SORPRENDIDA) 1.35f else 1f
            drawCircle(Paleta.TextoOscuro, radius = radioOjo * escala, center = pos)
            drawCircle(
                Color.White,
                radius = radioOjo * 0.34f,
                center = Offset(pos.x + radioOjo * 0.3f, pos.y - radioOjo * 0.35f)
            )
        }
    }

    // Rubor
    if (estado == EstadoNima.ALEGRE || estado == EstadoNima.ANIMANDO) {
        listOf(-1, 1).forEach { lado ->
            drawCircle(
                Paleta.Rosa.copy(alpha = 0.45f),
                radius = w * 0.038f,
                center = Offset(centro.x + lado * separacion * 1.6f, alturaOjos + radio * 0.30f)
            )
        }
    }

    // Boca segun el estado
    val bocaCentro = Offset(centro.x, centro.y + radio * 0.32f)
    val anchoBoca = radio * 0.55f
    when (estado) {
        EstadoNima.ALEGRE, EstadoNima.ANIMANDO -> drawArc(
            color = Paleta.TextoOscuro,
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(bocaCentro.x - anchoBoca / 2, bocaCentro.y - anchoBoca / 2),
            size = Size(anchoBoca, anchoBoca * 0.8f),
            style = Stroke(width = w * 0.020f)
        )
        EstadoNima.TRISTE -> drawArc(
            color = Paleta.TextoOscuro,
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(bocaCentro.x - anchoBoca / 2, bocaCentro.y),
            size = Size(anchoBoca, anchoBoca * 0.8f),
            style = Stroke(width = w * 0.020f)
        )
        EstadoNima.SORPRENDIDA -> drawOval(
            color = Paleta.TextoOscuro,
            topLeft = Offset(bocaCentro.x - anchoBoca * 0.22f, bocaCentro.y - anchoBoca * 0.18f),
            size = Size(anchoBoca * 0.44f, anchoBoca * 0.52f)
        )
        else -> drawLine(
            color = Paleta.TextoOscuro,
            start = Offset(bocaCentro.x - anchoBoca * 0.28f, bocaCentro.y),
            end = Offset(bocaCentro.x + anchoBoca * 0.28f, bocaCentro.y),
            strokeWidth = w * 0.020f
        )
    }

    // Chispitas alrededor cuando anima
    if (estado == EstadoNima.ANIMANDO || estado == EstadoNima.ALEGRE) {
        listOf(
            Offset(centro.x - radio * 1.5f, centro.y - radio * 0.6f) to w * 0.022f,
            Offset(centro.x + radio * 1.6f, centro.y - radio * 0.2f) to w * 0.016f,
            Offset(centro.x + radio * 1.2f, centro.y + radio * 0.9f) to w * 0.020f
        ).forEach { (pos, r) ->
            dibujarChispa(pos, r, Paleta.Sol)
        }
    }
}

/** Chispa de cuatro puntas, el simbolo grafico recurrente de la app. */
fun DrawScope.dibujarChispa(centro: Offset, radio: Float, color: Color) {
    val path = Path().apply {
        moveTo(centro.x, centro.y - radio)
        quadraticBezierTo(centro.x + radio * 0.22f, centro.y - radio * 0.22f, centro.x + radio, centro.y)
        quadraticBezierTo(centro.x + radio * 0.22f, centro.y + radio * 0.22f, centro.x, centro.y + radio)
        quadraticBezierTo(centro.x - radio * 0.22f, centro.y + radio * 0.22f, centro.x - radio, centro.y)
        quadraticBezierTo(centro.x - radio * 0.22f, centro.y - radio * 0.22f, centro.x, centro.y - radio)
        close()
    }
    drawPath(path, color)
}

/** Rectangulo auxiliar centrado, usado por varias ilustraciones. */
fun rectCentrado(centro: Offset, ancho: Float, alto: Float): Rect =
    Rect(centro.x - ancho / 2f, centro.y - alto / 2f, centro.x + ancho / 2f, centro.y + alto / 2f)
