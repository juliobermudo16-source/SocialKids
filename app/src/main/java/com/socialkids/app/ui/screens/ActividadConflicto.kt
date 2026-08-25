package com.socialkids.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.socialkids.app.domain.engine.ConflictoEngine
import com.socialkids.app.ui.ActividadViewModel
import com.socialkids.app.ui.art.Avatar
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.components.BocadilloNima
import com.socialkids.app.ui.components.Medidor
import com.socialkids.app.ui.theme.Paleta
import com.socialkids.app.util.Aviso
import com.socialkids.app.util.recordarRetroalimentacion

/**
 * Simulador de Conflicto: cada eleccion mueve calma, confianza y acuerdo.
 * El final no esta escrito, lo decide el estado en el que termina la charla.
 */
@Composable
fun ActividadConflicto(vm: ActividadViewModel, color: Color) {
    val reto = vm.retoConflicto
    val estado = vm.estadoConflicto
    val nodo = reto.nodo(estado.nodoId)
    val retro = recordarRetroalimentacion()

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
                Text("Escena con ${reto.personaje}", style = MaterialTheme.typography.labelMedium, color = color)
                Spacer(Modifier.height(4.dp))
                Text(reto.escena, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Panel de medidores: el estado de la conversacion siempre visible
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Como va la conversacion", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Turno ${estado.turno}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Medidor("Calma", estado.calma, Paleta.Cielo)
                Medidor("Confianza", estado.confianza, Paleta.Menta)
                Medidor("Acuerdo", estado.acuerdo, Paleta.Sol)
            }
        }

        Spacer(Modifier.height(14.dp))

        // Bitacora de la conversacion
        if (vm.bitacora.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                vm.bitacora.takeLast(4).forEachIndexed { i, linea ->
                    val mio = linea.startsWith("Tu:")
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = if (mio) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            color = if (mio) color.copy(alpha = 0.18f)
                            else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                linea.removePrefix("Tu: "),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        if (nodo != null && nodo.opciones.isNotEmpty() && estado.calma > 0) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.18f))
                ) {
                    Avatar(avatarId = reto.personaje.hashCode().mod(8), modifier = Modifier.fillMaxSize())
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        nodo.narrador,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(
                            topStart = 4.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 18.dp
                        ),
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            nodo.dialogo,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            Text("Que respondes?", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                nodo.opciones.forEach { opcion ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable {
                                vm.elegirOpcionConflicto(opcion)
                                retro.emitir(
                                    if (opcion.dCalma >= 0) Aviso.ACIERTO else Aviso.FALLO
                                )
                            },
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 2.dp
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(opcion.texto, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                opcion.etiqueta,
                                style = MaterialTheme.typography.labelSmall,
                                color = color
                            )
                        }
                    }
                }
            }
        } else {
            AnimatedVisibility(visible = true, enter = fadeIn()) {
                Column {
                    val desenlace = ConflictoEngine.desenlace(estado)
                    Surface(
                        color = color.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(desenlace.titulo, style = MaterialTheme.typography.titleMedium, color = color)
                            Spacer(Modifier.height(6.dp))
                            Text(desenlace.relato, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    BocadilloNima(
                        texto = "Pulsa Terminar para ver como te ha ido y por que.",
                        estado = EstadoNima.ALEGRE,
                        tamanioNima = 64.dp
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}
