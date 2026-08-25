package com.socialkids.app.ui.components

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntOffset

/**
 * Sistema propio de arrastrar y soltar.
 * Las zonas de destino registran sus limites en coordenadas de ventana y
 * el elemento arrastrado se dibuja en una capa superior que sigue al dedo.
 */
class EstadoArrastre {
    var datoActual by mutableStateOf<Any?>(null)
        internal set
    var contenidoActual by mutableStateOf<(@Composable () -> Unit)?>(null)
        internal set
    var posicion by mutableStateOf(Offset.Zero)
        internal set
    var tamanioArrastrado by mutableStateOf(Offset.Zero)
        internal set

    internal val zonas = mutableStateMapOf<Any, Rect>()
    internal var origenCapa: Offset = Offset.Zero

    val activo: Boolean get() = datoActual != null

    fun zonaBajo(punto: Offset): Any? =
        zonas.entries.firstOrNull { it.value.contains(punto) }?.key

    fun registrarZona(clave: Any, limites: Rect) {
        zonas[clave] = limites
    }

    fun olvidarZona(clave: Any) {
        zonas.remove(clave)
    }
}

val LocalArrastre = staticCompositionLocalOf { EstadoArrastre() }

/**
 * Contenedor que habilita el arrastre en su interior y dibuja la pieza flotante.
 */
@Composable
fun CapaArrastre(
    modifier: Modifier = Modifier,
    contenido: @Composable BoxScope.() -> Unit
) {
    val estado = remember { EstadoArrastre() }
    CompositionLocalProvider(LocalArrastre provides estado) {
        Box(
            modifier = modifier.onGloballyPositioned { estado.origenCapa = it.positionInWindow() }
        ) {
            contenido()
            val contenidoFlotante = estado.contenidoActual
            if (contenidoFlotante != null) {
                val local = estado.posicion - estado.origenCapa
                Box(
                    modifier = Modifier.offset {
                        IntOffset(
                            (local.x - estado.tamanioArrastrado.x / 2f).toInt(),
                            (local.y - estado.tamanioArrastrado.y / 2f).toInt()
                        )
                    }
                ) {
                    contenidoFlotante()
                }
            }
        }
    }
}

/** Marca un composable como zona donde se puede soltar una pieza. */
fun Modifier.zonaSoltar(clave: Any, estado: EstadoArrastre): Modifier =
    this.onGloballyPositioned { coords ->
        val pos = coords.positionInWindow()
        estado.registrarZona(
            clave,
            Rect(pos.x, pos.y, pos.x + coords.size.width, pos.y + coords.size.height)
        )
    }

/**
 * Hace que un composable se pueda arrastrar. Se activa con una pulsacion mantenida
 * para no competir con el desplazamiento vertical de la pantalla.
 */
@Composable
fun Modifier.piezaArrastrable(
    dato: Any,
    vistaPrevia: @Composable () -> Unit,
    alSoltar: (zona: Any?) -> Unit
): Modifier {
    val estado = LocalArrastre.current
    var origen by remember { mutableStateOf(Offset.Zero) }
    var tamanio by remember { mutableStateOf(Offset.Zero) }
    return this
        .onGloballyPositioned {
            origen = it.positionInWindow()
            tamanio = Offset(it.size.width.toFloat(), it.size.height.toFloat())
        }
        .pointerInput(dato) {
            detectDragGesturesAfterLongPress(
                onDragStart = { local ->
                    estado.datoActual = dato
                    estado.contenidoActual = vistaPrevia
                    estado.tamanioArrastrado = tamanio
                    estado.posicion = origen + local
                },
                onDrag = { cambio, delta ->
                    cambio.consume()
                    estado.posicion += delta
                },
                onDragEnd = {
                    val zona = estado.zonaBajo(estado.posicion)
                    estado.datoActual = null
                    estado.contenidoActual = null
                    alSoltar(zona)
                },
                onDragCancel = {
                    estado.datoActual = null
                    estado.contenidoActual = null
                    alSoltar(null)
                }
            )
        }
}
