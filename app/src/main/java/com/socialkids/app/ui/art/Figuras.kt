package com.socialkids.app.ui.art

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.socialkids.app.domain.model.Figura
import com.socialkids.app.ui.theme.Paleta

/**
 * Familia de doce simbolos vectoriales propios de SocialKids.
 * Se usan en cartas, insignias e iconos de modulo, siempre dibujados con Canvas.
 */
@Composable
fun SimboloFigura(
    figura: Figura,
    color: Color,
    modifier: Modifier = Modifier,
    apagado: Boolean = false
) {
    Canvas(modifier = modifier) {
        dibujarFigura(figura, if (apagado) Paleta.Bloqueado.copy(alpha = 0.55f) else color)
    }
}

fun DrawScope.dibujarFigura(figura: Figura, color: Color) {
    val w = size.width
    val h = size.height
    val c = Offset(w / 2f, h / 2f)
    val r = minOf(w, h) * 0.36f
    val grosor = minOf(w, h) * 0.075f

    when (figura) {
        Figura.CORAZON -> {
            val p = Path().apply {
                moveTo(c.x, c.y + r * 0.95f)
                cubicTo(c.x - r * 1.5f, c.y + r * 0.05f, c.x - r * 0.85f, c.y - r * 1.15f, c.x, c.y - r * 0.35f)
                cubicTo(c.x + r * 0.85f, c.y - r * 1.15f, c.x + r * 1.5f, c.y + r * 0.05f, c.x, c.y + r * 0.95f)
                close()
            }
            drawPath(p, color)
        }

        Figura.CHISPA -> {
            dibujarChispa(c, r * 1.15f, color)
            dibujarChispa(Offset(c.x + r * 0.95f, c.y - r * 0.85f), r * 0.35f, color.copy(alpha = 0.7f))
        }

        Figura.OLA -> {
            repeat(3) { i ->
                val y = c.y - r * 0.55f + i * r * 0.55f
                drawPath(
                    Path().apply {
                        moveTo(c.x - r * 1.1f, y)
                        quadraticBezierTo(c.x - r * 0.55f, y - r * 0.42f, c.x, y)
                        quadraticBezierTo(c.x + r * 0.55f, y + r * 0.42f, c.x + r * 1.1f, y)
                    },
                    color = color.copy(alpha = 1f - i * 0.22f),
                    style = Stroke(width = grosor * 0.85f)
                )
            }
        }

        Figura.HOJA -> {
            drawPath(
                Path().apply {
                    moveTo(c.x - r * 0.75f, c.y + r * 0.85f)
                    cubicTo(c.x - r * 1.1f, c.y - r * 0.6f, c.x + r * 0.2f, c.y - r * 1.2f, c.x + r * 0.9f, c.y - r * 0.75f)
                    cubicTo(c.x + r * 0.85f, c.y + r * 0.35f, c.x + r * 0.05f, c.y + r * 1.05f, c.x - r * 0.75f, c.y + r * 0.85f)
                    close()
                },
                color
            )
            drawLine(
                Color.White.copy(alpha = 0.7f),
                Offset(c.x - r * 0.60f, c.y + r * 0.70f),
                Offset(c.x + r * 0.72f, c.y - r * 0.62f),
                strokeWidth = grosor * 0.5f
            )
        }

        Figura.ESTRELLA -> {
            val p = Path()
            for (i in 0 until 10) {
                val ang = Math.toRadians((-90 + i * 36).toDouble())
                val rad = if (i % 2 == 0) r * 1.12f else r * 0.48f
                val x = c.x + (Math.cos(ang) * rad).toFloat()
                val y = c.y + (Math.sin(ang) * rad).toFloat()
                if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            p.close()
            drawPath(p, color)
        }

        Figura.NUBE -> {
            drawCircle(color, r * 0.55f, Offset(c.x - r * 0.55f, c.y + r * 0.10f))
            drawCircle(color, r * 0.72f, Offset(c.x, c.y - r * 0.18f))
            drawCircle(color, r * 0.50f, Offset(c.x + r * 0.62f, c.y + r * 0.12f))
            drawRoundRect(
                color,
                topLeft = Offset(c.x - r * 1.1f, c.y + r * 0.05f),
                size = Size(r * 2.2f, r * 0.62f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.31f)
            )
        }

        Figura.LLAVE -> {
            drawCircle(color, r * 0.48f, Offset(c.x - r * 0.55f, c.y), style = Stroke(grosor))
            drawLine(
                color,
                Offset(c.x - r * 0.10f, c.y),
                Offset(c.x + r * 1.05f, c.y),
                strokeWidth = grosor
            )
            drawLine(
                color,
                Offset(c.x + r * 0.55f, c.y),
                Offset(c.x + r * 0.55f, c.y + r * 0.50f),
                strokeWidth = grosor
            )
            drawLine(
                color,
                Offset(c.x + r * 0.95f, c.y),
                Offset(c.x + r * 0.95f, c.y + r * 0.35f),
                strokeWidth = grosor
            )
        }

        Figura.PUENTE -> {
            drawArc(
                color = color,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(c.x - r * 1.05f, c.y - r * 0.55f),
                size = Size(r * 2.1f, r * 1.5f),
                style = Stroke(width = grosor)
            )
            drawLine(color, Offset(c.x - r * 1.15f, c.y + r * 0.75f), Offset(c.x + r * 1.15f, c.y + r * 0.75f), strokeWidth = grosor)
            listOf(-0.6f, 0f, 0.6f).forEach { f ->
                drawLine(
                    color.copy(alpha = 0.75f),
                    Offset(c.x + r * f, c.y + r * 0.75f),
                    Offset(c.x + r * f, c.y + r * 0.75f - r * (0.75f - Math.abs(f) * 0.5f)),
                    strokeWidth = grosor * 0.55f
                )
            }
        }

        Figura.BURBUJA -> {
            drawRoundRect(
                color,
                topLeft = Offset(c.x - r * 1.05f, c.y - r * 0.90f),
                size = Size(r * 2.1f, r * 1.5f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.55f)
            )
            drawPath(
                Path().apply {
                    moveTo(c.x - r * 0.35f, c.y + r * 0.55f)
                    lineTo(c.x - r * 0.55f, c.y + r * 1.10f)
                    lineTo(c.x + r * 0.05f, c.y + r * 0.58f)
                    close()
                },
                color
            )
            listOf(-0.45f, 0f, 0.45f).forEach { f ->
                drawCircle(Color.White.copy(alpha = 0.85f), r * 0.13f, Offset(c.x + r * f, c.y - r * 0.15f))
            }
        }

        Figura.FARO -> {
            drawPath(
                Path().apply {
                    moveTo(c.x - r * 0.48f, c.y + r * 1.05f)
                    lineTo(c.x - r * 0.30f, c.y - r * 0.35f)
                    lineTo(c.x + r * 0.30f, c.y - r * 0.35f)
                    lineTo(c.x + r * 0.48f, c.y + r * 1.05f)
                    close()
                },
                color
            )
            drawRoundRect(
                color,
                topLeft = Offset(c.x - r * 0.42f, c.y - r * 0.72f),
                size = Size(r * 0.84f, r * 0.40f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.12f)
            )
            drawPath(
                Path().apply {
                    moveTo(c.x, c.y - r * 1.25f)
                    lineTo(c.x - r * 0.45f, c.y - r * 0.75f)
                    lineTo(c.x + r * 0.45f, c.y - r * 0.75f)
                    close()
                },
                color
            )
            // Haz de luz
            listOf(-1, 1).forEach { lado ->
                drawPath(
                    Path().apply {
                        moveTo(c.x + lado * r * 0.35f, c.y - r * 0.55f)
                        lineTo(c.x + lado * r * 1.35f, c.y - r * 0.95f)
                        lineTo(c.x + lado * r * 1.35f, c.y - r * 0.15f)
                        close()
                    },
                    color.copy(alpha = 0.35f)
                )
            }
        }

        Figura.BRUJULA -> {
            drawCircle(color, r * 1.02f, c, style = Stroke(grosor))
            drawPath(
                Path().apply {
                    moveTo(c.x + r * 0.55f, c.y - r * 0.55f)
                    lineTo(c.x - r * 0.12f, c.y + r * 0.12f)
                    lineTo(c.x - r * 0.55f, c.y + r * 0.55f)
                    lineTo(c.x + r * 0.12f, c.y - r * 0.12f)
                    close()
                },
                color
            )
            drawCircle(color, r * 0.14f, c)
        }

        Figura.SEMILLA -> {
            drawPath(
                Path().apply {
                    moveTo(c.x, c.y + r * 1.0f)
                    cubicTo(c.x - r * 1.0f, c.y + r * 0.2f, c.x - r * 0.6f, c.y - r * 1.0f, c.x, c.y - r * 1.05f)
                    cubicTo(c.x + r * 0.6f, c.y - r * 1.0f, c.x + r * 1.0f, c.y + r * 0.2f, c.x, c.y + r * 1.0f)
                    close()
                },
                color
            )
            drawLine(
                Color.White.copy(alpha = 0.75f),
                Offset(c.x, c.y + r * 0.75f),
                Offset(c.x, c.y - r * 0.55f),
                strokeWidth = grosor * 0.45f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(grosor * 0.5f, grosor * 0.5f))
            )
        }
    }
}
