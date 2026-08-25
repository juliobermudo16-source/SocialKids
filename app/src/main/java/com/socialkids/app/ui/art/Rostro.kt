package com.socialkids.app.ui.art

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.socialkids.app.domain.engine.RasgoExtra
import com.socialkids.app.domain.engine.RostroConfig
import com.socialkids.app.ui.theme.Paleta

/**
 * Rostro parametrico del Estudio de Rostros.
 * Los cuatro ejes del motor se traducen aqui en geometria: no hay imagenes
 * prefabricadas, la cara se dibuja de nuevo con cada cambio del jugador.
 */
@Composable
fun RostroParametrico(
    config: RostroConfig,
    modifier: Modifier = Modifier,
    tono: Color = Paleta.Arena,
    animar: Boolean = true
) {
    val duracion = if (animar) 260 else 0
    val cejas by animateFloatAsState(config.cejas / 100f, tween(duracion), label = "cejas")
    val ojos by animateFloatAsState(config.ojos / 100f, tween(duracion), label = "ojos")
    val boca by animateFloatAsState(config.boca / 100f, tween(duracion), label = "boca")
    val energia by animateFloatAsState(config.energia / 100f, tween(duracion), label = "energia")

    Canvas(modifier = modifier) {
        dibujarRostro(cejas, ojos, boca, energia, config.extras, tono)
    }
}

private fun DrawScope.dibujarRostro(
    cejas: Float,
    ojos: Float,
    boca: Float,
    energia: Float,
    extras: Set<RasgoExtra>,
    tono: Color
) {
    val w = size.width
    val h = size.height
    val radio = minOf(w, h) * 0.34f
    // La energia levanta o hunde la cabeza dentro del lienzo.
    val centro = Offset(w / 2f, h * (0.56f - (energia - 0.5f) * 0.10f))

    // Aura de energia: cuanto mas activada, mas amplia y mas calida.
    drawCircle(
        brush = Brush.radialGradient(
            listOf(
                Paleta.tono(if (energia > 0.5f) 7 else 6).copy(alpha = 0.10f + energia * 0.22f),
                Color.Transparent
            ),
            center = centro,
            radius = radio * (1.6f + energia * 0.8f)
        ),
        radius = radio * (1.6f + energia * 0.8f),
        center = centro
    )

    // Hombros: bajan con la energia baja
    val hombroY = centro.y + radio * (1.55f + (1f - energia) * 0.35f)
    drawPath(
        Path().apply {
            moveTo(centro.x - radio * 1.9f, h)
            quadraticBezierTo(centro.x - radio * 1.1f, hombroY, centro.x, hombroY)
            quadraticBezierTo(centro.x + radio * 1.1f, hombroY, centro.x + radio * 1.9f, h)
            close()
        },
        color = tono.copy(alpha = 0.55f)
    )

    // Cabeza
    drawCircle(
        brush = Brush.linearGradient(
            listOf(tono, tono.copy(alpha = 0.75f)),
            start = Offset(centro.x - radio, centro.y - radio),
            end = Offset(centro.x + radio, centro.y + radio)
        ),
        radius = radio,
        center = centro
    )
    drawCircle(
        color = Paleta.TextoOscuro.copy(alpha = 0.35f),
        radius = radio,
        center = centro,
        style = Stroke(width = w * 0.012f)
    )

    // Pelo simple: casquete superior
    drawArc(
        color = Paleta.TextoOscuro.copy(alpha = 0.80f),
        startAngle = 185f,
        sweepAngle = 170f,
        useCenter = true,
        topLeft = Offset(centro.x - radio, centro.y - radio),
        size = Size(radio * 2, radio * 2)
    )

    val separacion = radio * 0.42f
    val yOjos = centro.y + radio * 0.02f
    val grosor = w * 0.016f

    // Cejas: el eje mueve el extremo interno y el externo en direcciones opuestas.
    // 0 -> caidas hacia fuera (tristeza), 1 -> juntas y bajas por dentro (enfado/miedo).
    listOf(-1, 1).forEach { lado ->
        val xInterna = centro.x + lado * separacion * 0.45f
        val xExterna = centro.x + lado * separacion * 1.55f
        val yInterna = yOjos - radio * (0.52f - (cejas - 0.5f) * 0.30f)
        val yExterna = yOjos - radio * (0.34f + (cejas - 0.5f) * 0.26f)
        drawPath(
            Path().apply {
                moveTo(xInterna, yInterna)
                quadraticBezierTo(
                    (xInterna + xExterna) / 2f,
                    minOf(yInterna, yExterna) - radio * 0.10f,
                    xExterna, yExterna
                )
            },
            color = Paleta.TextoOscuro,
            style = Stroke(width = grosor * 1.6f)
        )
    }

    // Ojos: la apertura controla la altura del ovalo
    val altoOjo = radio * (0.10f + ojos * 0.34f)
    val anchoOjo = radio * 0.42f
    listOf(-1, 1).forEach { lado ->
        val pos = Offset(centro.x + lado * separacion, yOjos)
        drawOval(
            color = Color.White,
            topLeft = Offset(pos.x - anchoOjo / 2, pos.y - altoOjo / 2),
            size = Size(anchoOjo, altoOjo)
        )
        val radioPupila = minOf(anchoOjo, altoOjo) * 0.42f
        drawCircle(Paleta.TextoOscuro, radius = radioPupila, center = pos)
        if (extras.contains(RasgoExtra.BRILLO)) {
            drawCircle(
                Color.White,
                radius = radioPupila * 0.38f,
                center = Offset(pos.x + radioPupila * 0.35f, pos.y - radioPupila * 0.35f)
            )
        }
        drawOval(
            color = Paleta.TextoOscuro.copy(alpha = 0.5f),
            topLeft = Offset(pos.x - anchoOjo / 2, pos.y - altoOjo / 2),
            size = Size(anchoOjo, altoOjo),
            style = Stroke(width = grosor)
        )
    }

    // Boca: curva controlada por el eje. 0 = comisuras abajo, 1 = sonrisa amplia.
    val yBoca = centro.y + radio * 0.52f
    val anchoBoca = radio * (0.55f + boca * 0.35f)
    val curva = (boca - 0.5f) * radio * 1.10f
    drawPath(
        Path().apply {
            moveTo(centro.x - anchoBoca / 2, yBoca)
            quadraticBezierTo(centro.x, yBoca + curva, centro.x + anchoBoca / 2, yBoca)
        },
        color = Paleta.TextoOscuro,
        style = Stroke(width = grosor * 1.8f)
    )
    // Con sonrisa amplia aparece el interior de la boca
    if (boca > 0.78f) {
        drawPath(
            Path().apply {
                moveTo(centro.x - anchoBoca / 2, yBoca)
                quadraticBezierTo(centro.x, yBoca + curva, centro.x + anchoBoca / 2, yBoca)
                quadraticBezierTo(centro.x, yBoca + curva * 0.35f, centro.x - anchoBoca / 2, yBoca)
                close()
            },
            color = Paleta.Coral.copy(alpha = 0.8f)
        )
    }

    if (extras.contains(RasgoExtra.RUBOR)) {
        listOf(-1, 1).forEach { lado ->
            drawCircle(
                Paleta.Rosa.copy(alpha = 0.45f),
                radius = radio * 0.20f,
                center = Offset(centro.x + lado * separacion * 1.6f, yOjos + radio * 0.32f)
            )
        }
    }

    if (extras.contains(RasgoExtra.LAGRIMA)) {
        val origen = Offset(centro.x - separacion, yOjos + altoOjo * 0.5f)
        drawPath(
            Path().apply {
                moveTo(origen.x, origen.y)
                quadraticBezierTo(
                    origen.x - radio * 0.10f, origen.y + radio * 0.30f,
                    origen.x, origen.y + radio * 0.46f
                )
                quadraticBezierTo(
                    origen.x + radio * 0.10f, origen.y + radio * 0.30f,
                    origen.x, origen.y
                )
                close()
            },
            color = Paleta.Cielo
        )
    }

    if (extras.contains(RasgoExtra.SUDOR)) {
        val origen = Offset(centro.x + radio * 0.82f, centro.y - radio * 0.62f)
        drawPath(
            Path().apply {
                moveTo(origen.x, origen.y)
                quadraticBezierTo(
                    origen.x - radio * 0.13f, origen.y + radio * 0.24f,
                    origen.x, origen.y + radio * 0.34f
                )
                quadraticBezierTo(
                    origen.x + radio * 0.13f, origen.y + radio * 0.24f,
                    origen.x, origen.y
                )
                close()
            },
            color = Paleta.Cielo.copy(alpha = 0.9f)
        )
    }
}
