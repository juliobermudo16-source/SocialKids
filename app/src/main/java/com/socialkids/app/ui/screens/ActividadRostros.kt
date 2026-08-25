package com.socialkids.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.socialkids.app.domain.engine.EjeRostro
import com.socialkids.app.domain.engine.RasgoExtra
import com.socialkids.app.ui.ActividadViewModel
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.art.RostroParametrico
import com.socialkids.app.ui.components.BocadilloNima
import com.socialkids.app.ui.theme.LocalAjustes
import com.socialkids.app.ui.theme.Paleta
import com.socialkids.app.util.Aviso
import com.socialkids.app.util.recordarRetroalimentacion

/**
 * Estudio de Rostros: el jugador construye una cara moviendo cuatro ejes.
 * El dibujo se recalcula en tiempo real; no hay caras prefabricadas que elegir.
 */
@Composable
fun ActividadRostros(vm: ActividadViewModel, color: Color) {
    val objetivo = vm.objetivoRostro
    val retro = recordarRetroalimentacion()
    val animar = LocalAjustes.current.animaciones

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
                Text(
                    "Construye la cara de",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    objetivo.emocion.uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = color
                )
                Spacer(Modifier.height(4.dp))
                Text(objetivo.pista, style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(Modifier.height(14.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(color.copy(alpha = 0.22f), color.copy(alpha = 0.05f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            RostroParametrico(
                config = vm.rostro,
                modifier = Modifier.fillMaxSize().padding(10.dp),
                tono = Paleta.Arena,
                animar = animar
            )
            Surface(
                color = Color.White.copy(alpha = 0.85f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            ) {
                Text(
                    "Espejo del faro",
                    style = MaterialTheme.typography.labelSmall,
                    color = Paleta.TextoOscuro,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        EjeRostro.entries.forEach { eje ->
            val valor = when (eje) {
                EjeRostro.CEJAS -> vm.rostro.cejas
                EjeRostro.OJOS -> vm.rostro.ojos
                EjeRostro.BOCA -> vm.rostro.boca
                EjeRostro.ENERGIA -> vm.rostro.energia
            }
            Column(Modifier.padding(vertical = 2.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(eje.etiqueta, style = MaterialTheme.typography.labelLarge)
                    Text(
                        "$valor",
                        style = MaterialTheme.typography.labelMedium,
                        color = color
                    )
                }
                Slider(
                    value = valor.toFloat(),
                    onValueChange = { vm.moverEje(eje, it.toInt()) },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = color,
                        activeTrackColor = color
                    )
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        eje.izquierda,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        eje.derecha,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Text("Detalles extra", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RasgoExtra.entries.forEach { rasgo ->
                val activo = vm.rostro.extras.contains(rasgo)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            vm.alternarRasgo(rasgo)
                            retro.emitir(Aviso.TOQUE)
                        },
                    color = if (activo) color else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        nombreRasgo(rasgo),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (activo) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 10.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        BocadilloNima(
            texto = "Mueve las cejas y la boca primero: son las que mas cambian una emocion.",
            estado = EstadoNima.NEUTRAL,
            tamanioNima = 64.dp
        )
        Spacer(Modifier.height(16.dp))
    }
}

private fun nombreRasgo(rasgo: RasgoExtra): String = when (rasgo) {
    RasgoExtra.LAGRIMA -> "Lagrima"
    RasgoExtra.RUBOR -> "Rubor"
    RasgoExtra.SUDOR -> "Sudor"
    RasgoExtra.BRILLO -> "Brillo"
}
