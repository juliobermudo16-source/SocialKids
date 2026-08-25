package com.socialkids.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.socialkids.app.domain.model.Carta
import com.socialkids.app.domain.model.EstadoMision
import com.socialkids.app.domain.model.Insignia
import com.socialkids.app.domain.model.Mecanica
import com.socialkids.app.domain.model.Mision
import com.socialkids.app.domain.model.Rareza
import com.socialkids.app.domain.model.Zona
import com.socialkids.app.ui.art.SimboloFigura
import com.socialkids.app.ui.theme.Paleta

/** Carta coleccionable. Bloqueada muestra silueta, nunca un hueco vacio. */
@Composable
fun TarjetaCarta(
    carta: Carta,
    desbloqueada: Boolean,
    modifier: Modifier = Modifier,
    alPulsar: () -> Unit = {}
) {
    val color = Paleta.tono(carta.tono)
    val escala by animateFloatAsState(if (desbloqueada) 1f else 0.97f, tween(300), label = "carta")
    Surface(
        modifier = modifier
            .aspectRatio(0.72f)
            .scale(escala)
            .clip(RoundedCornerShape(20.dp))
            .clickable { alPulsar() },
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (desbloqueada) 5.dp else 1.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        if (desbloqueada) {
                            Brush.linearGradient(listOf(color.copy(alpha = 0.85f), color.copy(alpha = 0.40f)))
                        } else {
                            Brush.linearGradient(
                                listOf(
                                    Paleta.Bloqueado.copy(alpha = 0.35f),
                                    Paleta.Bloqueado.copy(alpha = 0.18f)
                                )
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                SimboloFigura(
                    figura = carta.figura,
                    color = Color.White,
                    apagado = !desbloqueada,
                    modifier = Modifier.fillMaxSize().padding(18.dp)
                )
                if (!desbloqueada) {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = "Carta bloqueada",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(20.dp)
                    )
                }
                if (desbloqueada && carta.rareza != Rareza.COMUN) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        color = Color.White.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            if (carta.rareza == Rareza.LEGENDARIA) "LEGENDARIA" else "RARA",
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Column(Modifier.padding(10.dp)) {
                Text(
                    if (desbloqueada) carta.nombre else "???",
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    if (desbloqueada) carta.lema else "Aun por descubrir",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** Insignia con su progreso real hacia el objetivo. */
@Composable
fun VistaInsignia(
    insignia: Insignia,
    conseguida: Boolean,
    progreso: Float,
    modifier: Modifier = Modifier
) {
    val color = Paleta.tono(insignia.tono)
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = if (conseguida) 4.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(
                        if (conseguida) {
                            Brush.linearGradient(listOf(color, color.copy(alpha = 0.55f)))
                        } else {
                            Brush.linearGradient(
                                listOf(
                                    Paleta.Bloqueado.copy(alpha = 0.30f),
                                    Paleta.Bloqueado.copy(alpha = 0.15f)
                                )
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                SimboloFigura(
                    insignia.figura,
                    Color.White,
                    apagado = !conseguida,
                    modifier = Modifier.size(34.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(insignia.nombre, style = MaterialTheme.typography.titleSmall)
                Text(
                    if (conseguida) insignia.descripcion else insignia.pista,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (!conseguida) {
                    Spacer(Modifier.height(6.dp))
                    BarraProgreso(progreso, Modifier.fillMaxWidth(), color, 8.dp)
                }
            }
            if (conseguida) {
                Spacer(Modifier.width(8.dp))
                Text("OK", style = MaterialTheme.typography.labelMedium, color = Paleta.Exito)
            }
        }
    }
}

/** Tarjeta de mision dentro de una zona. */
@Composable
fun TarjetaMision(
    mision: Mision,
    estado: EstadoMision,
    estrellas: Int,
    modifier: Modifier = Modifier,
    alPulsar: () -> Unit
) {
    val color = Paleta.colorZona(mision.zonaId)
    val bloqueada = estado == EstadoMision.BLOQUEADA
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(enabled = !bloqueada) { alPulsar() },
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (bloqueada) 0.dp else 4.dp
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (bloqueada) SolidColor(Paleta.Bloqueado.copy(alpha = 0.20f))
                        else Brush.linearGradient(listOf(color, color.copy(alpha = 0.6f)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (bloqueada) {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = Paleta.Bloqueado)
                } else {
                    Text(
                        "${mision.orden}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(mision.titulo, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Text(
                    etiquetaMecanica(mision.mecanica),
                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ChipEstado(estado)
                    Spacer(Modifier.width(8.dp))
                    Estrellas(estrellas, tamanio = 18.dp)
                }
            }
        }
    }
}

/** Tarjeta grande de zona usada en el mapa y en los accesos rapidos. */
@Composable
fun TarjetaZona(
    zona: Zona,
    desbloqueada: Boolean,
    porcentaje: Float,
    modifier: Modifier = Modifier,
    alPulsar: () -> Unit
) {
    val color = Paleta.colorZona(zona.id)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(enabled = desbloqueada) { alPulsar() },
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (desbloqueada) 5.dp else 0.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .background(Paleta.degradadoZona(zona.id))
            ) {
                com.socialkids.app.ui.art.EscenaZona(
                    zona = zona.id,
                    modifier = Modifier.fillMaxSize()
                )
                if (!desbloqueada) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.45f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Se abre con ${zona.xpNecesaria} XP",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            Column(Modifier.padding(14.dp)) {
                Text(zona.nombre, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(2.dp))
                Text(
                    zona.lema,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BarraProgreso(porcentaje, Modifier.weight(1f), color, 10.dp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "${(porcentaje * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = color
                    )
                }
            }
        }
    }
}

/** Panel del resultado de una actividad: estrellas, explicacion y consejo. */
@Composable
fun PanelExplicacion(
    titulo: String,
    explicacion: String,
    consejo: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = color.copy(alpha = 0.10f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleSmall, color = color)
            Text(explicacion, style = MaterialTheme.typography.bodyMedium)
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    consejo,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

fun etiquetaMecanica(mecanica: Mecanica): String = when (mecanica) {
    Mecanica.ROSTROS -> "Estudio de rostros"
    Mecanica.ESCUCHA -> "Detective de escucha"
    Mecanica.PUENTE -> "Puente de la empatia"
    Mecanica.MENSAJE -> "Constructor de mensajes"
    Mecanica.CONFLICTO -> "Simulador de conflicto"
    Mecanica.TERMOMETRO -> "Termometro emocional"
}

/** Texto centrado auxiliar para estados intermedios. */
@Composable
fun TextoCentrado(texto: String, modifier: Modifier = Modifier) {
    Text(
        texto,
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
