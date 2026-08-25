package com.socialkids.app.ui.art

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.socialkids.app.R
import com.socialkids.app.data.seed.CartasSeed
import com.socialkids.app.domain.model.Accesorio
import com.socialkids.app.domain.model.AvatarSpec
import com.socialkids.app.ui.theme.Paleta

/**
 * Ocho avatares dibujados con Canvas. Cada uno combina un tono de la paleta
 * con un accesorio distinto, asi que se reconocen de un vistazo.
 */
@Composable
fun Avatar(
    avatarId: Int,
    modifier: Modifier = Modifier,
    conFondo: Boolean = true
) {
    val spec = CartasSeed.avatar(avatarId)
    val descripcion = stringResource(R.string.desc_avatar) + ": " + spec.nombre
    Canvas(modifier = modifier.semantics { contentDescription = descripcion }) {
        dibujarAvatar(spec, conFondo)
    }
}

fun DrawScope.dibujarAvatar(spec: AvatarSpec, conFondo: Boolean) {
    val w = size.width
    val h = size.height
    val color = Paleta.tono(spec.tono)
    val c = Offset(w / 2f, h * 0.54f)
    val r = minOf(w, h) * 0.30f

    if (conFondo) {
        drawCircle(
            brush = Brush.linearGradient(
                listOf(color.copy(alpha = 0.35f), color.copy(alpha = 0.12f)),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            ),
            radius = minOf(w, h) * 0.48f,
            center = Offset(w / 2f, h / 2f)
        )
    }

    // Cuerpo
    drawPath(
        Path().apply {
            moveTo(c.x - r * 1.35f, h * 0.99f)
            quadraticBezierTo(c.x - r * 1.15f, c.y + r * 1.05f, c.x, c.y + r * 1.02f)
            quadraticBezierTo(c.x + r * 1.15f, c.y + r * 1.05f, c.x + r * 1.35f, h * 0.99f)
            close()
        },
        color = color
    )

    // Cabeza
    drawCircle(color = Paleta.Arena, radius = r, center = c)
    drawCircle(color = Paleta.TextoOscuro.copy(alpha = 0.25f), radius = r, center = c, style = Stroke(w * 0.012f))

    // Ojos y sonrisa
    listOf(-1, 1).forEach { lado ->
        drawCircle(Paleta.TextoOscuro, r * 0.11f, Offset(c.x + lado * r * 0.36f, c.y - r * 0.05f))
    }
    drawArc(
        color = Paleta.TextoOscuro,
        startAngle = 20f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(c.x - r * 0.34f, c.y + r * 0.10f),
        size = Size(r * 0.68f, r * 0.50f),
        style = Stroke(width = w * 0.016f)
    )
    listOf(-1, 1).forEach { lado ->
        drawCircle(Paleta.Rosa.copy(alpha = 0.40f), r * 0.15f, Offset(c.x + lado * r * 0.66f, c.y + r * 0.22f))
    }

    dibujarAccesorio(spec.accesorio, c, r, color, w)
}

private fun DrawScope.dibujarAccesorio(
    accesorio: Accesorio,
    c: Offset,
    r: Float,
    color: Color,
    w: Float
) {
    val oscuro = Paleta.TextoOscuro
    when (accesorio) {
        Accesorio.GORRA -> {
            drawArc(
                color = color,
                startAngle = 180f, sweepAngle = 180f, useCenter = true,
                topLeft = Offset(c.x - r * 1.02f, c.y - r * 1.05f),
                size = Size(r * 2.04f, r * 1.5f)
            )
            drawRoundRect(
                color = color,
                topLeft = Offset(c.x - r * 0.15f, c.y - r * 0.42f),
                size = Size(r * 1.55f, r * 0.24f),
                cornerRadius = CornerRadius(r * 0.12f)
            )
        }
        Accesorio.GAFAS -> {
            listOf(-1, 1).forEach { lado ->
                drawCircle(
                    oscuro, r * 0.30f,
                    Offset(c.x + lado * r * 0.36f, c.y - r * 0.05f),
                    style = Stroke(w * 0.016f)
                )
            }
            drawLine(
                oscuro,
                Offset(c.x - r * 0.07f, c.y - r * 0.05f),
                Offset(c.x + r * 0.07f, c.y - r * 0.05f),
                strokeWidth = w * 0.014f
            )
        }
        Accesorio.AURICULARES -> {
            drawArc(
                color = oscuro,
                startAngle = 190f, sweepAngle = 160f, useCenter = false,
                topLeft = Offset(c.x - r * 1.15f, c.y - r * 1.15f),
                size = Size(r * 2.3f, r * 2.3f),
                style = Stroke(width = w * 0.030f)
            )
            listOf(-1, 1).forEach { lado ->
                drawRoundRect(
                    color = color,
                    topLeft = Offset(c.x + lado * r * 1.02f - r * 0.20f, c.y - r * 0.32f),
                    size = Size(r * 0.40f, r * 0.66f),
                    cornerRadius = CornerRadius(r * 0.18f)
                )
            }
        }
        Accesorio.BUFANDA -> {
            drawRoundRect(
                color = color,
                topLeft = Offset(c.x - r * 0.95f, c.y + r * 0.82f),
                size = Size(r * 1.9f, r * 0.42f),
                cornerRadius = CornerRadius(r * 0.20f)
            )
            drawRoundRect(
                color = color,
                topLeft = Offset(c.x + r * 0.40f, c.y + r * 1.05f),
                size = Size(r * 0.34f, r * 0.85f),
                cornerRadius = CornerRadius(r * 0.16f)
            )
        }
        Accesorio.FLOR -> {
            val centroFlor = Offset(c.x + r * 0.72f, c.y - r * 0.76f)
            repeat(5) { i ->
                val ang = Math.toRadians((i * 72).toDouble())
                drawCircle(
                    Paleta.Rosa, r * 0.17f,
                    Offset(
                        centroFlor.x + (Math.cos(ang) * r * 0.22f).toFloat(),
                        centroFlor.y + (Math.sin(ang) * r * 0.22f).toFloat()
                    )
                )
            }
            drawCircle(Paleta.Sol, r * 0.13f, centroFlor)
        }
        Accesorio.ANTENA -> {
            drawLine(
                oscuro,
                Offset(c.x, c.y - r * 0.98f),
                Offset(c.x + r * 0.20f, c.y - r * 1.60f),
                strokeWidth = w * 0.018f
            )
            dibujarChispa(Offset(c.x + r * 0.20f, c.y - r * 1.60f), r * 0.28f, Paleta.Sol)
        }
        Accesorio.CAPUCHA -> {
            drawArc(
                color = color,
                startAngle = 165f, sweepAngle = 210f, useCenter = false,
                topLeft = Offset(c.x - r * 1.18f, c.y - r * 1.18f),
                size = Size(r * 2.36f, r * 2.36f),
                style = Stroke(width = w * 0.075f)
            )
        }
        Accesorio.DIADEMA -> {
            drawArc(
                color = color,
                startAngle = 195f, sweepAngle = 150f, useCenter = false,
                topLeft = Offset(c.x - r * 1.06f, c.y - r * 1.06f),
                size = Size(r * 2.12f, r * 2.12f),
                style = Stroke(width = w * 0.036f)
            )
            drawCircle(Paleta.Sol, r * 0.16f, Offset(c.x + r * 0.75f, c.y - r * 0.72f))
        }
    }
}
