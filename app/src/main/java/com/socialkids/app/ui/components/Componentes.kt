package com.socialkids.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.socialkids.app.domain.model.EstadoMision
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.art.Nima
import com.socialkids.app.ui.theme.Paleta

/** Boton principal grande, con relieve y color de acento. */
@Composable
fun BotonGrande(
    texto: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    icono: ImageVector? = Icons.Filled.PlayArrow,
    habilitado: Boolean = true,
    alPulsar: () -> Unit
) {
    val colorReal by animateColorAsState(
        if (habilitado) color else Paleta.Bloqueado,
        tween(200),
        label = "colorBoton"
    )
    Surface(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = habilitado) { alPulsar() },
        color = colorReal,
        shadowElevation = if (habilitado) 6.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.28f), Color.Transparent)
                        )
                    )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icono != null) {
                    Icon(icono, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(10.dp))
                }
                Text(
                    texto,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}

/** Boton plano secundario. */
@Composable
fun BotonSuave(
    texto: String,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    alPulsar: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { alPulsar() },
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icono != null) {
                Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(8.dp))
            }
            Text(texto, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/** Estrellas de resultado, con animacion de entrada. */
@Composable
fun Estrellas(
    cantidad: Int,
    modifier: Modifier = Modifier,
    tamanio: Dp = 28.dp,
    total: Int = 3
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(total) { i ->
            val activa = i < cantidad
            val escala by animateFloatAsState(
                if (activa) 1f else 0.82f,
                tween(320 + i * 90),
                label = "estrella$i"
            )
            Box(
                modifier = Modifier
                    .size(tamanio)
                    .scale(escala)
                    .background(Paleta.degradadoEstrella(activa), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = if (activa) "Estrella conseguida" else "Estrella vacia",
                    tint = if (activa) Color.White else Color.White.copy(alpha = 0.55f),
                    modifier = Modifier.size(tamanio * 0.62f)
                )
            }
        }
    }
}

/** Barra de progreso redondeada con animacion. */
@Composable
fun BarraProgreso(
    valor: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    alto: Dp = 14.dp
) {
    val objetivo by animateFloatAsState(valor.coerceIn(0f, 1f), tween(600), label = "progreso")
    Box(
        modifier = modifier
            .height(alto)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(objetivo)
                .background(Brush.horizontalGradient(listOf(color, color.copy(alpha = 0.72f))))
        )
    }
}

/** Medidor con etiqueta y valor numerico, usado en el simulador de conflicto. */
@Composable
fun Medidor(
    nombre: String,
    valor: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(nombre, style = MaterialTheme.typography.labelMedium)
            Text("$valor", style = MaterialTheme.typography.labelMedium, color = color)
        }
        Spacer(Modifier.height(4.dp))
        BarraProgreso(valor / 100f, Modifier.fillMaxWidth(), color, 10.dp)
    }
}

/** Etiqueta de estado de una mision: icono + texto, nunca solo color. */
@Composable
fun ChipEstado(estado: EstadoMision, modifier: Modifier = Modifier) {
    val (texto, icono, color) = when (estado) {
        EstadoMision.BLOQUEADA -> Triple("Bloqueada", Icons.Filled.Lock, Paleta.Bloqueado)
        EstadoMision.DISPONIBLE -> Triple("Disponible", Icons.Filled.PlayArrow, Paleta.Turquesa)
        EstadoMision.INICIADA -> Triple("Empezada", Icons.Filled.PlayArrow, Paleta.Sol)
        EstadoMision.COMPLETADA -> Triple("Completada", Icons.Filled.Check, Paleta.Exito)
        EstadoMision.DOMINADA -> Triple("Dominada", Icons.Filled.Star, Paleta.Naranja)
    }
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.16f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(5.dp))
            Text(texto, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}

/** Cabecera de pantalla con boton de volver y titulo. */
@Composable
fun Cabecera(
    titulo: String,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,
    alVolver: (() -> Unit)? = null,
    accion: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (alVolver != null) {
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable { alVolver() },
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
            Spacer(Modifier.width(12.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.headlineSmall, maxLines = 2)
            if (subtitulo != null) {
                Text(
                    subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (accion != null) {
            Spacer(Modifier.width(8.dp))
            accion()
        }
    }
}

/** Bocadillo de dialogo de Nima. Texto siempre corto. */
@Composable
fun BocadilloNima(
    texto: String,
    modifier: Modifier = Modifier,
    estado: EstadoNima = EstadoNima.NEUTRAL,
    tamanioNima: Dp = 78.dp
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Nima(estado = estado, tamanio = tamanioNima)
        Spacer(Modifier.width(6.dp))
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 6.dp, topEnd = 22.dp, bottomEnd = 22.dp, bottomStart = 22.dp),
            shadowElevation = 3.dp,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                texto,
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/** Titulo de seccion con linea decorativa. */
@Composable
fun TituloSeccion(texto: String, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(width = 6.dp, height = 22.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Spacer(Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.titleMedium)
    }
}

/** Estado vacio ilustrado: nunca una pantalla en blanco. */
@Composable
fun EstadoVacio(
    titulo: String,
    mensaje: String,
    modifier: Modifier = Modifier,
    estado: EstadoNima = EstadoNima.PENSATIVA
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Nima(estado = estado, tamanio = 110.dp)
        Spacer(Modifier.height(8.dp))
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            mensaje,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Contenedor con borde punteado usado como hueco donde soltar piezas. */
@Composable
fun HuecoPunteado(
    activo: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    contenido: @Composable () -> Unit
) {
    val colorBorde by animateColorAsState(
        if (activo) color else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
        tween(200),
        label = "borde"
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(color.copy(alpha = if (activo) 0.12f else 0.05f))
            .border(2.dp, colorBorde, RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center
    ) {
        contenido()
    }
}
