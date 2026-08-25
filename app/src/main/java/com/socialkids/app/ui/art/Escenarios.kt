package com.socialkids.app.ui.art

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.socialkids.app.domain.model.ZonaId
import com.socialkids.app.ui.theme.LocalAjustes
import com.socialkids.app.ui.theme.Paleta
import kotlin.math.sin

/**
 * Fondo del mapa: mar, olas, islas flotantes y el sendero que une las zonas.
 * Se dibuja entero con Canvas para que la app funcione sin ningun recurso externo.
 */
@Composable
fun FondoIsla(modifier: Modifier = Modifier, oscuro: Boolean = false) {
    val animaciones = LocalAjustes.current.animaciones
    val transicion = rememberInfiniteTransition(label = "mar")
    val fase by transicion.animateFloat(
        initialValue = 0f,
        targetValue = if (animaciones) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(6000), RepeatMode.Reverse),
        label = "fase"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawRect(brush = if (oscuro) Paleta.degradadoNoche else Paleta.degradadoCielo)

        // Sol o luna
        val astro = Offset(w * 0.82f, h * 0.10f)
        drawCircle(
            brush = Brush.radialGradient(
                listOf(
                    (if (oscuro) Paleta.Cielo else Paleta.Sol).copy(alpha = 0.55f),
                    Color.Transparent
                ),
                center = astro,
                radius = w * 0.22f
            ),
            radius = w * 0.22f,
            center = astro
        )
        drawCircle(if (oscuro) Color(0xFFE9F4FF) else Paleta.Sol, w * 0.075f, astro)

        // Nubes
        listOf(
            Triple(0.18f, 0.12f, 1.0f),
            Triple(0.55f, 0.07f, 0.7f),
            Triple(0.36f, 0.22f, 0.55f)
        ).forEach { (x, y, escala) ->
            val cx = w * (x + fase * 0.03f)
            val cy = h * y
            val r = w * 0.055f * escala
            val blanco = Color.White.copy(alpha = if (oscuro) 0.16f else 0.75f)
            drawCircle(blanco, r, Offset(cx, cy))
            drawCircle(blanco, r * 0.78f, Offset(cx + r * 1.1f, cy + r * 0.18f))
            drawCircle(blanco, r * 0.62f, Offset(cx - r * 1.0f, cy + r * 0.24f))
        }

        // Mar con tres bandas de olas
        val nivelMar = h * 0.30f
        drawRect(
            brush = Brush.verticalGradient(
                listOf(
                    (if (oscuro) Color(0xFF12405E) else Color(0xFF2FB6D8)),
                    (if (oscuro) Color(0xFF0B2237) else Color(0xFF1E7FA8))
                ),
                startY = nivelMar,
                endY = h
            ),
            topLeft = Offset(0f, nivelMar),
            size = Size(w, h - nivelMar)
        )
        repeat(3) { banda ->
            val y = nivelMar + h * (0.06f + banda * 0.13f)
            val amplitud = h * 0.014f * (1f + banda * 0.4f)
            val path = Path().apply {
                moveTo(0f, y)
                var x = 0f
                while (x <= w) {
                    val despl = sin((x / w * 6.28f) + fase * 3f + banda) * amplitud
                    lineTo(x, y + despl)
                    x += w / 40f
                }
            }
            drawPath(
                path,
                color = Color.White.copy(alpha = 0.20f - banda * 0.05f),
                style = Stroke(width = h * 0.006f)
            )
        }

        // Islas flotantes de fondo
        dibujarIslote(Offset(w * 0.15f, h * 0.46f), w * 0.13f, Paleta.Menta, oscuro)
        dibujarIslote(Offset(w * 0.78f, h * 0.58f), w * 0.10f, Paleta.Lima, oscuro)
        dibujarIslote(Offset(w * 0.45f, h * 0.80f), w * 0.16f, Paleta.Arena, oscuro)
    }
}

private fun DrawScope.dibujarIslote(centro: Offset, radio: Float, color: Color, oscuro: Boolean) {
    val alpha = if (oscuro) 0.45f else 0.9f
    drawPath(
        Path().apply {
            moveTo(centro.x - radio, centro.y)
            quadraticBezierTo(centro.x, centro.y - radio * 0.72f, centro.x + radio, centro.y)
            lineTo(centro.x + radio * 0.55f, centro.y + radio * 0.85f)
            lineTo(centro.x - radio * 0.55f, centro.y + radio * 0.85f)
            close()
        },
        color = Color(0xFF8A5A3B).copy(alpha = alpha * 0.85f)
    )
    drawPath(
        Path().apply {
            moveTo(centro.x - radio, centro.y)
            quadraticBezierTo(centro.x, centro.y - radio * 0.72f, centro.x + radio, centro.y)
            quadraticBezierTo(centro.x, centro.y + radio * 0.22f, centro.x - radio, centro.y)
            close()
        },
        color = color.copy(alpha = alpha)
    )
}

/**
 * Escena propia de cada zona. Seis composiciones distintas para que ninguna
 * pantalla de zona se parezca a otra.
 */
@Composable
fun EscenaZona(zona: ZonaId, modifier: Modifier = Modifier) {
    val animaciones = LocalAjustes.current.animaciones
    val transicion = rememberInfiniteTransition(label = "escena")
    val pulso by transicion.animateFloat(
        initialValue = 0f,
        targetValue = if (animaciones) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(2800), RepeatMode.Reverse),
        label = "pulso"
    )
    val fondoSuave = MaterialTheme.colorScheme.surface

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val principal = Paleta.colorZona(zona)
        val apoyo = Paleta.colorZonaSuave(zona)

        drawRoundRect(
            brush = Brush.linearGradient(listOf(apoyo.copy(alpha = 0.55f), principal.copy(alpha = 0.30f))),
            cornerRadius = CornerRadius(w * 0.06f)
        )
        drawRoundRect(
            color = fondoSuave.copy(alpha = 0.10f),
            cornerRadius = CornerRadius(w * 0.06f)
        )

        when (zona) {
            ZonaId.FARO -> {
                // Roca, faro y haz de luz que late
                drawPath(
                    Path().apply {
                        moveTo(w * 0.10f, h)
                        quadraticBezierTo(w * 0.35f, h * 0.66f, w * 0.62f, h)
                        close()
                    },
                    color = Color(0xFF6F5A4B)
                )
                drawRoundRect(
                    Color.White,
                    topLeft = Offset(w * 0.30f, h * 0.28f),
                    size = Size(w * 0.12f, h * 0.50f),
                    cornerRadius = CornerRadius(w * 0.02f)
                )
                repeat(3) { i ->
                    drawRect(
                        principal,
                        topLeft = Offset(w * 0.30f, h * (0.34f + i * 0.14f)),
                        size = Size(w * 0.12f, h * 0.05f)
                    )
                }
                drawCircle(Paleta.Sol, w * 0.045f, Offset(w * 0.36f, h * 0.24f))
                drawPath(
                    Path().apply {
                        moveTo(w * 0.40f, h * 0.20f)
                        lineTo(w * 0.98f, h * (0.02f + pulso * 0.05f))
                        lineTo(w * 0.98f, h * (0.34f + pulso * 0.05f))
                        close()
                    },
                    color = Paleta.Sol.copy(alpha = 0.30f + pulso * 0.20f)
                )
            }

            ZonaId.BOSQUE -> {
                repeat(6) { i ->
                    val x = w * (0.10f + i * 0.145f)
                    val alto = h * (0.42f + (i % 3) * 0.14f)
                    drawRect(
                        Color(0xFF7A4E2E),
                        topLeft = Offset(x - w * 0.014f, h - alto * 0.42f),
                        size = Size(w * 0.028f, alto * 0.42f)
                    )
                    drawCircle(principal.copy(alpha = 0.9f), alto * 0.30f, Offset(x, h - alto * 0.52f))
                    drawCircle(apoyo.copy(alpha = 0.85f), alto * 0.22f, Offset(x + w * 0.02f, h - alto * 0.66f))
                }
                // Ondas de sonido entre los arboles
                repeat(3) { i ->
                    drawArc(
                        color = Color.White.copy(alpha = 0.35f - i * 0.08f),
                        startAngle = 210f, sweepAngle = 120f, useCenter = false,
                        topLeft = Offset(w * 0.36f - w * (0.06f + i * 0.06f), h * 0.30f - w * (0.06f + i * 0.06f)),
                        size = Size(w * (0.12f + i * 0.12f), w * (0.12f + i * 0.12f)),
                        style = Stroke(width = w * 0.010f)
                    )
                }
            }

            ZonaId.PUENTE -> {
                drawRect(
                    Color(0xFF5B4636),
                    topLeft = Offset(0f, h * 0.72f),
                    size = Size(w * 0.22f, h * 0.28f)
                )
                drawRect(
                    Color(0xFF5B4636),
                    topLeft = Offset(w * 0.78f, h * 0.72f),
                    size = Size(w * 0.22f, h * 0.28f)
                )
                drawArc(
                    color = principal,
                    startAngle = 180f, sweepAngle = 180f, useCenter = false,
                    topLeft = Offset(w * 0.16f, h * 0.36f),
                    size = Size(w * 0.68f, h * 0.72f),
                    style = Stroke(width = h * 0.045f)
                )
                repeat(5) { i ->
                    val x = w * (0.24f + i * 0.13f)
                    drawLine(
                        principal.copy(alpha = 0.75f),
                        Offset(x, h * 0.72f),
                        Offset(x, h * (0.72f - 0.18f + Math.abs(i - 2) * 0.03f)),
                        strokeWidth = w * 0.012f
                    )
                }
                drawLine(
                    Color.White.copy(alpha = 0.55f),
                    Offset(0f, h * 0.72f), Offset(w, h * 0.72f),
                    strokeWidth = h * 0.012f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(w * 0.04f, w * 0.03f))
                )
            }

            ZonaId.PLAZA -> {
                drawRect(
                    Paleta.Arena.copy(alpha = 0.75f),
                    topLeft = Offset(0f, h * 0.70f),
                    size = Size(w, h * 0.30f)
                )
                listOf(0.12f, 0.36f, 0.62f, 0.86f).forEachIndexed { i, x ->
                    val alto = h * (0.30f + (i % 2) * 0.12f)
                    drawRoundRect(
                        Paleta.tono(i + 3).copy(alpha = 0.9f),
                        topLeft = Offset(w * x - w * 0.07f, h * 0.70f - alto),
                        size = Size(w * 0.14f, alto),
                        cornerRadius = CornerRadius(w * 0.02f)
                    )
                    drawCircle(Color.White.copy(alpha = 0.8f), w * 0.016f, Offset(w * x, h * 0.70f - alto * 0.55f))
                }
                // Bocadillos flotando
                listOf(Offset(w * 0.25f, h * 0.22f), Offset(w * 0.68f, h * 0.14f)).forEach { pos ->
                    drawRoundRect(
                        Color.White.copy(alpha = 0.9f),
                        topLeft = Offset(pos.x - w * 0.09f, pos.y - h * 0.07f),
                        size = Size(w * 0.18f, h * 0.14f),
                        cornerRadius = CornerRadius(w * 0.045f)
                    )
                    repeat(3) { i ->
                        drawCircle(principal, w * 0.010f, Offset(pos.x - w * 0.04f + i * w * 0.04f, pos.y))
                    }
                }
            }

            ZonaId.TALLER -> {
                drawRect(
                    Color(0xFF4A3B2C).copy(alpha = 0.55f),
                    topLeft = Offset(0f, h * 0.74f),
                    size = Size(w, h * 0.26f)
                )
                // Engranajes
                dibujarEngranaje(Offset(w * 0.32f, h * 0.44f), w * 0.13f, principal, pulso * 30f)
                dibujarEngranaje(Offset(w * 0.56f, h * 0.58f), w * 0.09f, apoyo, -pulso * 40f)
                dibujarEngranaje(Offset(w * 0.74f, h * 0.36f), w * 0.07f, Paleta.Coral, pulso * 50f)
                // Banco de trabajo
                drawRoundRect(
                    Color(0xFF8A5A3B),
                    topLeft = Offset(w * 0.10f, h * 0.72f),
                    size = Size(w * 0.80f, h * 0.06f),
                    cornerRadius = CornerRadius(w * 0.012f)
                )
            }

            ZonaId.MIRADOR -> {
                drawPath(
                    Path().apply {
                        moveTo(0f, h)
                        lineTo(w * 0.28f, h * 0.42f)
                        lineTo(w * 0.52f, h)
                        close()
                    },
                    color = Color(0xFF6F5A7B)
                )
                drawPath(
                    Path().apply {
                        moveTo(w * 0.40f, h)
                        lineTo(w * 0.70f, h * 0.28f)
                        lineTo(w, h)
                        close()
                    },
                    color = principal.copy(alpha = 0.85f)
                )
                // Barandilla del mirador
                drawLine(
                    Color.White.copy(alpha = 0.85f),
                    Offset(w * 0.55f, h * 0.44f), Offset(w * 0.88f, h * 0.44f),
                    strokeWidth = h * 0.014f
                )
                repeat(4) { i ->
                    val x = w * (0.58f + i * 0.10f)
                    drawLine(
                        Color.White.copy(alpha = 0.7f),
                        Offset(x, h * 0.44f), Offset(x, h * 0.56f),
                        strokeWidth = h * 0.008f
                    )
                }
                // Estrellas del atardecer
                listOf(0.15f to 0.16f, 0.35f to 0.10f, 0.80f to 0.14f).forEach { (x, y) ->
                    dibujarChispa(Offset(w * x, h * y), w * (0.016f + pulso * 0.006f), Color.White)
                }
            }
        }
    }
}

private fun DrawScope.dibujarEngranaje(centro: Offset, radio: Float, color: Color, giro: Float) {
    val dientes = 8
    val path = Path()
    for (i in 0 until dientes * 2) {
        val ang = Math.toRadians((giro + i * (360.0 / (dientes * 2))))
        val r = if (i % 2 == 0) radio else radio * 0.76f
        val x = centro.x + (Math.cos(ang) * r).toFloat()
        val y = centro.y + (Math.sin(ang) * r).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
    drawCircle(Color.White.copy(alpha = 0.85f), radio * 0.32f, centro)
}
