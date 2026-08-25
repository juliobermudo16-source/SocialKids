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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.socialkids.app.domain.engine.PiezaPuente
import com.socialkids.app.domain.engine.PuenteEngine
import com.socialkids.app.domain.engine.Tablon
import com.socialkids.app.ui.ActividadViewModel
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.components.BocadilloNima
import com.socialkids.app.ui.components.LocalArrastre
import com.socialkids.app.ui.components.piezaArrastrable
import com.socialkids.app.ui.components.zonaSoltar
import com.socialkids.app.ui.theme.Paleta
import com.socialkids.app.util.Aviso
import com.socialkids.app.util.recordarRetroalimentacion

/**
 * Puente de la Empatia: hay que colocar una pieza en cada tablon.
 * Se puede arrastrar (manteniendo pulsado) o tocar la pieza y luego el tablon.
 */
@Composable
fun ActividadPuente(vm: ActividadViewModel, color: Color) {
    val reto = vm.retoPuente
    val retro = recordarRetroalimentacion()
    val arrastre = LocalArrastre.current
    var seleccionada by remember { mutableStateOf<PiezaPuente?>(null) }
    val estabilidad by animateFloatAsState(
        PuenteEngine.estabilidad(vm.colocacionPuente),
        tween(420),
        label = "estabilidad"
    )

    fun colocar(tablon: Tablon, pieza: PiezaPuente?) {
        vm.colocarPieza(tablon, pieza)
        seleccionada = null
        retro.emitir(Aviso.TOQUE)
    }

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
                Text(reto.personaje, style = MaterialTheme.typography.labelMedium, color = color)
                Spacer(Modifier.height(4.dp))
                Text(reto.escena, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(Modifier.height(12.dp))
        DibujoPuente(estabilidad, color)
        Spacer(Modifier.height(12.dp))

        Tablon.entries.forEach { tablon ->
            val pieza = vm.colocacionPuente[tablon]
            val correcta = PuenteEngine.esCorrecta(pieza, tablon)
            Column(Modifier.padding(bottom = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        tablon.etiqueta.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = color
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        tablon.pregunta,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(6.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(66.dp)
                        .zonaSoltar(tablon, arrastre)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(enabled = seleccionada != null || pieza != null) {
                            if (seleccionada != null) colocar(tablon, seleccionada)
                            else colocar(tablon, null)
                        }
                        .border(
                            2.dp,
                            if (pieza != null) color else Paleta.Bloqueado.copy(alpha = 0.5f),
                            RoundedCornerShape(18.dp)
                        ),
                    color = if (pieza != null) color.copy(alpha = 0.14f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        Modifier.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            pieza?.texto ?: "Suelta aqui una pieza",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (pieza != null) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        if (pieza != null && correcta) {
                            MarcaCorrecta(color = Paleta.Exito)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(6.dp))
        Text("Piezas disponibles", style = MaterialTheme.typography.titleSmall)
        Text(
            "Manten pulsada una pieza para arrastrarla, o tocala y luego toca un tablon.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            vm.piezasLibres().forEach { pieza ->
                val elegida = seleccionada?.id == pieza.id
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            seleccionada = if (elegida) null else pieza
                            retro.emitir(Aviso.TOQUE)
                        }
                        .piezaArrastrable(
                            dato = pieza.id,
                            vistaPrevia = { PiezaFlotante(pieza.texto, color) },
                            alSoltar = { zona ->
                                if (zona is Tablon) colocar(zona, pieza)
                            }
                        ),
                    color = if (elegida) color.copy(alpha = 0.20f) else MaterialTheme.colorScheme.surface,
                    shadowElevation = if (elegida) 6.dp else 2.dp
                ) {
                    Row(
                        Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .width(6.dp)
                                .height(26.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (elegida) color else Paleta.Bloqueado.copy(alpha = 0.6f))
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(pieza.texto, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        BocadilloNima(
            texto = "Ojo: hay piezas que son juicios o soluciones. Esas no encajan en ningun tablon.",
            estado = EstadoNima.PENSATIVA,
            tamanioNima = 64.dp
        )
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun PiezaFlotante(texto: String, color: Color) {
    Surface(
        color = color,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 10.dp
    ) {
        Text(
            texto,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}

/** El puente se dibuja mas o menos completo segun cuantos tablones encajen. */
@Composable
private fun DibujoPuente(estabilidad: Float, color: Color) {
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(110.dp)
    ) {
        val w = size.width
        val h = size.height

        // Acantilados
        drawRect(Color(0xFF6B5644), topLeft = Offset(0f, h * 0.55f), size = androidx.compose.ui.geometry.Size(w * 0.16f, h * 0.45f))
        drawRect(Color(0xFF6B5644), topLeft = Offset(w * 0.84f, h * 0.55f), size = androidx.compose.ui.geometry.Size(w * 0.16f, h * 0.45f))
        drawRect(Paleta.Menta, topLeft = Offset(0f, h * 0.52f), size = androidx.compose.ui.geometry.Size(w * 0.16f, h * 0.06f))
        drawRect(Paleta.Menta, topLeft = Offset(w * 0.84f, h * 0.52f), size = androidx.compose.ui.geometry.Size(w * 0.16f, h * 0.06f))

        // Arco guia
        drawPath(
            Path().apply {
                moveTo(w * 0.16f, h * 0.55f)
                quadraticBezierTo(w * 0.5f, h * 0.10f, w * 0.84f, h * 0.55f)
            },
            color = Paleta.Bloqueado.copy(alpha = 0.35f),
            style = Stroke(width = 6f)
        )

        // Tablones colocados
        val total = 3
        repeat(total) { i ->
            val puestos = (estabilidad * total)
            val visible = (puestos - i).coerceIn(0f, 1f)
            if (visible > 0f) {
                val x0 = w * (0.16f + i * 0.2267f)
                val x1 = x0 + w * 0.2267f * visible
                val y0 = alturaArco(x0 / w, h)
                val y1 = alturaArco(x1 / w, h)
                drawLine(
                    color = color,
                    start = Offset(x0, y0),
                    end = Offset(x1, y1),
                    strokeWidth = 14f
                )
            }
        }

        // Figura que quiere cruzar
        drawCircle(Paleta.Coral, h * 0.09f, Offset(w * 0.10f, h * 0.42f))
        drawCircle(Paleta.Violeta, h * 0.09f, Offset(w * 0.90f, h * 0.42f))
    }
}

private fun alturaArco(t: Float, h: Float): Float {
    // Aproximacion de la curva del arco entre 0.16 y 0.84
    val u = ((t - 0.16f) / 0.68f).coerceIn(0f, 1f)
    val curva = 4f * u * (1f - u)
    return h * 0.55f - curva * h * 0.32f
}
