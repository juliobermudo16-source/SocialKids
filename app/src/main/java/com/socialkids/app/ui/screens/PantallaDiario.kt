package com.socialkids.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.socialkids.app.domain.engine.RasgoExtra
import com.socialkids.app.domain.engine.RostroConfig
import com.socialkids.app.ui.JuegoViewModel
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.art.RostroParametrico
import com.socialkids.app.ui.components.BocadilloNima
import com.socialkids.app.ui.components.BotonGrande
import com.socialkids.app.ui.components.BotonSuave
import com.socialkids.app.ui.components.Cabecera
import com.socialkids.app.ui.components.TituloSeccion
import com.socialkids.app.ui.theme.Paleta
import com.socialkids.app.util.Aviso
import com.socialkids.app.util.FechaCorta
import com.socialkids.app.util.recordarRetroalimentacion

/** Emociones del diario, cada una con su cara parametrica y su color. */
data class EmocionDiario(
    val nombre: String,
    val tono: Int,
    val rostro: RostroConfig
)

object EmocionesDiario {
    val lista = listOf(
        EmocionDiario("Alegria", 2, RostroConfig(45, 62, 92, 78, setOf(RasgoExtra.RUBOR, RasgoExtra.BRILLO))),
        EmocionDiario("Calma", 1, RostroConfig(48, 40, 66, 40)),
        EmocionDiario("Tristeza", 6, RostroConfig(22, 28, 12, 14, setOf(RasgoExtra.LAGRIMA))),
        EmocionDiario("Enfado", 0, RostroConfig(88, 62, 18, 82)),
        EmocionDiario("Miedo", 9, RostroConfig(68, 95, 24, 72, setOf(RasgoExtra.SUDOR))),
        EmocionDiario("Verguenza", 5, RostroConfig(35, 30, 38, 30, setOf(RasgoExtra.RUBOR))),
        EmocionDiario("Sorpresa", 10, RostroConfig(20, 96, 60, 70)),
        EmocionDiario("Ilusion", 3, RostroConfig(40, 72, 84, 88, setOf(RasgoExtra.BRILLO)))
    )

    fun de(nombre: String): EmocionDiario = lista.firstOrNull { it.nombre == nombre } ?: lista.first()
}

/**
 * Diario de Animo: registro real que alimenta las estadisticas.
 * Ningun numero de la app esta escrito a mano: todos salen de aqui.
 */
@Composable
fun PantallaDiario(
    juegoVM: JuegoViewModel,
    alVolver: () -> Unit,
    alIrACalma: () -> Unit
) {
    val registros by juegoVM.animos.collectAsStateWithLifecycle()
    val resumen by juegoVM.resumenAnimo.collectAsStateWithLifecycle()
    val retro = recordarRetroalimentacion()

    var emocion by remember { mutableStateOf(EmocionesDiario.lista.first().nombre) }
    var intensidad by remember { mutableIntStateOf(5) }
    var nota by remember { mutableStateOf("") }

    val seleccionada = EmocionesDiario.de(emocion)
    val color = Paleta.tono(seleccionada.tono)

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Cabecera(
            titulo = "Diario de animo",
            subtitulo = "${resumen.total} anotaciones - ${resumen.diasRegistrados} dias",
            alVolver = alVolver,
            modifier = Modifier.padding(top = 28.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(
                    color = color.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Como estas hoy?", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(10.dp))
                        Row(
                            Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            EmocionesDiario.lista.forEach { e ->
                                val activa = e.nombre == emocion
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .width(72.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(
                                            if (activa) Paleta.tono(e.tono).copy(alpha = 0.22f)
                                            else Color.Transparent
                                        )
                                        .clickable {
                                            emocion = e.nombre
                                            retro.emitir(Aviso.TOQUE)
                                        }
                                        .padding(vertical = 6.dp)
                                ) {
                                    RostroParametrico(
                                        config = e.rostro,
                                        modifier = Modifier.size(56.dp),
                                        tono = Paleta.tono(e.tono),
                                        animar = false
                                    )
                                    Text(
                                        e.nombre,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Text("Cuanta intensidad: $intensidad/10", style = MaterialTheme.typography.labelLarge)
                        Slider(
                            value = intensidad.toFloat(),
                            onValueChange = { intensidad = it.toInt() },
                            valueRange = 1f..10f,
                            steps = 8,
                            colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color)
                        )

                        OutlinedTextField(
                            value = nota,
                            onValueChange = { if (it.length <= 140) nota = it },
                            label = { Text("Que ha pasado? (opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )

                        Spacer(Modifier.height(12.dp))
                        BotonGrande(
                            texto = "Guardar en mi diario",
                            modifier = Modifier.fillMaxWidth(),
                            color = color,
                            icono = Icons.Filled.Favorite
                        ) {
                            juegoVM.guardarAnimo(emocion, intensidad, nota)
                            nota = ""
                            retro.emitir(Aviso.RECOMPENSA)
                        }
                        if (intensidad >= 7) {
                            Spacer(Modifier.height(8.dp))
                            BotonSuave(
                                texto = "Ir al rincon de calma",
                                icono = Icons.Filled.Favorite,
                                modifier = Modifier.fillMaxWidth(),
                                alPulsar = alIrACalma
                            )
                        }
                    }
                }
            }

            if (registros.isEmpty()) {
                item {
                    BocadilloNima(
                        texto = "Aqui no hay notas todavia. Escribir como te sientes ayuda a entenderlo mejor.",
                        estado = EstadoNima.PENSATIVA
                    )
                }
            } else {
                item { TituloSeccion("Tus anotaciones", color = Paleta.Menta) }
            }

            items(registros, key = { it.id }) { registro ->
                val e = EmocionesDiario.de(registro.emocion)
                val c = Paleta.tono(e.tono)
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(18.dp),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(c.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            RostroParametrico(
                                config = e.rostro,
                                modifier = Modifier.size(40.dp),
                                tono = c,
                                animar = false
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "${registro.emocion} - ${registro.intensidad}/10",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                FechaCorta.texto(registro.diaEpoch) +
                                    if (registro.nota.isNotBlank()) " - ${registro.nota}" else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Borrar anotacion",
                            tint = Paleta.Bloqueado,
                            modifier = Modifier.clickable { juegoVM.borrarAnimo(registro.id) }
                        )
                    }
                }
            }
        }
    }
}
