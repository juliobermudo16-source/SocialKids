package com.socialkids.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.socialkids.app.ui.ActividadViewModel
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.components.BocadilloNima
import com.socialkids.app.ui.theme.Paleta
import com.socialkids.app.util.Aviso
import com.socialkids.app.util.recordarRetroalimentacion

/**
 * Termometro emocional: medir la intensidad de una escena y elegir
 * una estrategia proporcional a esa intensidad.
 */
@Composable
fun ActividadTermometro(vm: ActividadViewModel, color: Color) {
    val reto = vm.retoTermometro
    val retro = recordarRetroalimentacion()
    val nivel by animateFloatAsState(vm.intensidad / 10f, tween(320), label = "mercurio")

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Surface(
            color = color.copy(alpha = 0.12f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(14.dp)) {
                Text("La escena", style = MaterialTheme.typography.labelMedium, color = color)
                Spacer(Modifier.height(4.dp))
                Text(reto.situacion, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Termometro dibujado
            Box(
                Modifier
                    .width(80.dp)
                    .height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val anchoTubo = w * 0.34f
                    val x = (w - anchoTubo) / 2f
                    val bulboR = w * 0.28f
                    val topeTubo = h * 0.08f
                    val baseTubo = h - bulboR * 2.1f

                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(x, topeTubo),
                        size = Size(anchoTubo, baseTubo - topeTubo + bulboR),
                        cornerRadius = CornerRadius(anchoTubo / 2f)
                    )
                    val alturaMercurio = (baseTubo - topeTubo) * nivel
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            listOf(Paleta.Coral, Paleta.Sol)
                        ),
                        topLeft = Offset(x + anchoTubo * 0.18f, baseTubo - alturaMercurio),
                        size = Size(anchoTubo * 0.64f, alturaMercurio + bulboR),
                        cornerRadius = CornerRadius(anchoTubo / 3f)
                    )
                    drawCircle(Color.White, bulboR, Offset(w / 2f, h - bulboR))
                    drawCircle(Paleta.Coral, bulboR * 0.74f, Offset(w / 2f, h - bulboR))

                    // Marcas de la escala
                    repeat(11) { i ->
                        val y = baseTubo - (baseTubo - topeTubo) * (i / 10f)
                        drawLine(
                            Paleta.Bloqueado.copy(alpha = 0.75f),
                            Offset(x + anchoTubo + w * 0.03f, y),
                            Offset(x + anchoTubo + w * (if (i % 5 == 0) 0.20f else 0.12f), y),
                            strokeWidth = 3f
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    "Cuanta ${reto.emocion.lowercase()} hay aqui?",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "${vm.intensidad}/10",
                    style = MaterialTheme.typography.displayMedium,
                    color = color
                )
                Slider(
                    value = vm.intensidad.toFloat(),
                    onValueChange = { vm.cambiarIntensidad(it.toInt()) },
                    valueRange = 0f..10f,
                    steps = 9,
                    colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color)
                )
                Text(
                    when (vm.intensidad) {
                        in 0..2 -> "Apenas me afecta"
                        in 3..5 -> "Me molesta, pero puedo hablarlo"
                        in 6..8 -> "Estoy bastante encendido"
                        else -> "Me esta desbordando"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(18.dp))
        Text("Que haces con eso?", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            reto.estrategias.forEach { estrategia ->
                val elegida = vm.estrategiaElegida?.id == estrategia.id
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable {
                            vm.elegirEstrategia(estrategia)
                            retro.emitir(Aviso.TOQUE)
                        }
                        .border(
                            width = if (elegida) 2.dp else 0.dp,
                            color = if (elegida) color else Color.Transparent,
                            shape = RoundedCornerShape(18.dp)
                        ),
                    color = if (elegida) color.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface,
                    shadowElevation = if (elegida) 4.dp else 1.dp
                ) {
                    Row(
                        Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (elegida) color else Paleta.Bloqueado.copy(alpha = 0.5f))
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(estrategia.nombre, style = MaterialTheme.typography.titleSmall)
                            Text(
                                estrategia.descripcion,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        BocadilloNima(
            texto = "No hay una respuesta unica: lo importante es que la estrategia encaje con lo encendido que estas.",
            estado = EstadoNima.PENSATIVA,
            tamanioNima = 64.dp
        )
        Spacer(Modifier.height(16.dp))
    }
}
