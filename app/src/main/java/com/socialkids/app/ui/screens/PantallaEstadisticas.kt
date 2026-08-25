package com.socialkids.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.socialkids.app.data.seed.MundoSeed
import com.socialkids.app.domain.usecase.DesbloqueoEvaluador
import com.socialkids.app.domain.usecase.ProgresoCalculadora
import com.socialkids.app.ui.JuegoViewModel
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.components.BarraProgreso
import com.socialkids.app.ui.components.BocadilloNima
import com.socialkids.app.ui.components.Cabecera
import com.socialkids.app.ui.components.TituloSeccion
import com.socialkids.app.ui.theme.Paleta
import com.socialkids.app.util.FechaCorta
import com.socialkids.app.util.RelojSistema

/**
 * Estadisticas reales: todo se calcula desde la base de datos.
 * Ningun valor esta escrito a mano en la pantalla.
 */
@Composable
fun PantallaEstadisticas(juegoVM: JuegoViewModel, alVolver: () -> Unit) {
    val estado by juegoVM.estado.collectAsStateWithLifecycle()
    val resumen by juegoVM.resumenAnimo.collectAsStateWithLifecycle()
    val racha by juegoVM.racha.collectAsStateWithLifecycle()
    val stats = estado.estadisticas

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Cabecera(
            titulo = "Tus numeros",
            subtitulo = "Calculados con lo que has hecho",
            alVolver = alVolver,
            modifier = Modifier.padding(top = 28.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Dato("Nivel", "${stats.nivel}", Paleta.Violeta, Modifier.weight(1f))
                    Dato("XP", "${stats.xp}", Paleta.Turquesa, Modifier.weight(1f))
                    Dato("Racha", "${racha.first} d", Paleta.Naranja, Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Dato("Misiones", "${stats.misionesCompletadas}/${MundoSeed.misiones.size}", Paleta.Menta, Modifier.weight(1f))
                    Dato("Dominadas", "${stats.misionesDominadas}", Paleta.Sol, Modifier.weight(1f))
                    Dato("Cartas", "${stats.cartasDesbloqueadas}", Paleta.Rosa, Modifier.weight(1f))
                }
            }

            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Progreso de nivel", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(8.dp))
                        BarraProgreso(
                            ProgresoCalculadora.progresoEnNivel(stats.xp),
                            Modifier.fillMaxWidth(),
                            Paleta.Violeta
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Te faltan ${ProgresoCalculadora.xpRestanteParaSiguienteNivel(stats.xp)} XP para el nivel ${stats.nivel + 1}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item { TituloSeccion("Animo de los ultimos 7 dias", color = Paleta.Menta) }

            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        if (resumen.total == 0) {
                            BocadilloNima(
                                texto = "Sin anotaciones todavia. Cuando escribas en el diario, aqui saldra tu semana.",
                                estado = EstadoNima.PENSATIVA,
                                tamanioNima = 64.dp
                            )
                        } else {
                            GraficoSemana(resumen.ultimosSieteDias)
                            Spacer(Modifier.height(10.dp))
                            Text(
                                "Intensidad media: ${resumen.intensidadMedia}/10",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            val frecuente = resumen.emocionFrecuente
                            if (frecuente != null) {
                                Text(
                                    "Emocion mas anotada: $frecuente",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (resumen.conteoPorEmocion.isNotEmpty()) {
                item { TituloSeccion("Reparto de emociones", color = Paleta.Rosa) }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val maximo = resumen.conteoPorEmocion.values.max()
                        resumen.conteoPorEmocion.entries
                            .sortedByDescending { it.value }
                            .forEach { (nombre, cantidad) ->
                                val e = EmocionesDiario.de(nombre)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        nombre,
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.width(88.dp)
                                    )
                                    BarraProgreso(
                                        cantidad.toFloat() / maximo,
                                        Modifier.weight(1f),
                                        Paleta.tono(e.tono),
                                        12.dp
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("$cantidad", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                    }
                }
            }

            item { TituloSeccion("Progreso por zona", color = Paleta.Turquesa) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    MundoSeed.zonas.forEach { zona ->
                        val misiones = MundoSeed.misionesDe(zona.id)
                        val pct = DesbloqueoEvaluador.porcentajeZona(misiones, estado.progreso)
                        Column {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(zona.nombre, style = MaterialTheme.typography.labelMedium)
                                Text(
                                    "${(pct * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Paleta.colorZona(zona.id)
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            BarraProgreso(pct, Modifier.fillMaxWidth(), Paleta.colorZona(zona.id), 10.dp)
                        }
                    }
                }
            }

            item { TituloSeccion("Hitos de habilidad", color = Paleta.Sol) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    LineaHito("Rostros clavados", stats.rostrosClavados)
                    LineaHito("Puentes firmes", stats.puentesFirmes)
                    LineaHito("Mensajes asertivos perfectos", stats.mensajesAsertivosPerfectos)
                    LineaHito("Conflictos con acuerdo y calma", stats.conflictosResueltosConCalma)
                    LineaHito("Zonas completadas", stats.zonasCompletadas)
                }
            }
        }
    }
}

@Composable
private fun Dato(titulo: String, valor: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.14f),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(valor, style = MaterialTheme.typography.headlineSmall, color = color)
            Text(
                titulo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LineaHito(titulo: String, valor: Int) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(titulo, style = MaterialTheme.typography.bodySmall)
        Text("$valor", style = MaterialTheme.typography.labelMedium, color = Paleta.Naranja)
    }
}

/** Grafico de barras de la semana, dibujado con datos reales del diario. */
@Composable
private fun GraficoSemana(valores: List<Int>) {
    val hoy = RelojSistema.hoyEpochDay()
    Column {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            val w = size.width
            val h = size.height
            val n = valores.size.coerceAtLeast(1)
            val anchoBarra = w / (n * 1.7f)
            val hueco = (w - anchoBarra * n) / (n + 1)

            // Lineas guia
            listOf(0.25f, 0.5f, 0.75f, 1f).forEach { f ->
                drawLine(
                    color = Paleta.Bloqueado.copy(alpha = 0.20f),
                    start = Offset(0f, h - h * f * 0.9f),
                    end = Offset(w, h - h * f * 0.9f),
                    strokeWidth = 2f
                )
            }

            valores.forEachIndexed { i, valor ->
                val x = hueco + i * (anchoBarra + hueco)
                val alto = (valor / 10f) * h * 0.9f
                if (valor > 0) {
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            listOf(Paleta.Menta, Paleta.Turquesa)
                        ),
                        topLeft = Offset(x, h - alto),
                        size = Size(anchoBarra, alto),
                        cornerRadius = CornerRadius(anchoBarra / 2.4f)
                    )
                } else {
                    drawRoundRect(
                        color = Paleta.Bloqueado.copy(alpha = 0.18f),
                        topLeft = Offset(x, h - 8f),
                        size = Size(anchoBarra, 8f),
                        cornerRadius = CornerRadius(4f)
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            (6 downTo 0).forEach { atras ->
                Text(
                    FechaCorta.inicialDiaSemana(hoy - atras),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
