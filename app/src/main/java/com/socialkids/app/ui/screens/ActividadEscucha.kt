package com.socialkids.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.socialkids.app.ui.ActividadViewModel
import com.socialkids.app.ui.FaseEscucha
import com.socialkids.app.ui.art.Avatar
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.components.BocadilloNima
import com.socialkids.app.ui.components.BotonGrande
import com.socialkids.app.ui.theme.LocalAjustes
import com.socialkids.app.ui.theme.Paleta
import com.socialkids.app.util.Aviso
import com.socialkids.app.util.recordarRetroalimentacion

/**
 * Detective de Escucha: primero se escucha el relato entero,
 * despues hay que rescatar los datos reales y elegir como responder.
 */
@Composable
fun ActividadEscucha(vm: ActividadViewModel, color: Color) {
    val reto = vm.retoEscucha
    val retro = recordarRetroalimentacion()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        // Personaje que habla
        Row(verticalAlignment = Alignment.Top) {
            Box(
                Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.18f))
            ) {
                Avatar(avatarId = reto.personaje.hashCode().mod(8), modifier = Modifier.fillMaxSize())
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(reto.personaje, style = MaterialTheme.typography.titleMedium, color = color)
                Spacer(Modifier.height(4.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(
                        topStart = 4.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 20.dp
                    ),
                    shadowElevation = 3.dp
                ) {
                    Text(
                        reto.relato,
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        OndasEscucha(color)
        Spacer(Modifier.height(12.dp))

        when (vm.fase) {
            FaseEscucha.RELATO -> {
                BocadilloNima(
                    texto = "Leelo entero sin prisa. Luego te preguntare que dijo de verdad.",
                    estado = EstadoNima.NEUTRAL,
                    tamanioNima = 64.dp
                )
                Spacer(Modifier.height(12.dp))
                BotonGrande(
                    texto = "Ya lo he escuchado",
                    modifier = Modifier.fillMaxWidth(),
                    color = color,
                    icono = null
                ) {
                    vm.avanzarFase()
                    retro.emitir(Aviso.TOQUE)
                }
            }

            FaseEscucha.DETALLES, FaseEscucha.RESPUESTA -> {
                Text(
                    "Marca solo lo que ${reto.personaje} dijo de verdad",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    reto.detalles.forEach { detalle ->
                        val marcado = vm.seleccionDetalles.contains(detalle.id)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(enabled = vm.fase == FaseEscucha.DETALLES) {
                                    vm.alternarDetalle(detalle.id)
                                    retro.emitir(Aviso.TOQUE)
                                },
                            color = if (marcado) color.copy(alpha = 0.16f)
                            else MaterialTheme.colorScheme.surface,
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier
                                        .size(22.dp)
                                        .clip(RoundedCornerShape(7.dp))
                                        .background(
                                            if (marcado) color
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .border(
                                            1.dp,
                                            if (marcado) color else Paleta.Bloqueado,
                                            RoundedCornerShape(7.dp)
                                        )
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(detalle.texto, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                if (vm.fase == FaseEscucha.DETALLES) {
                    BotonGrande(
                        texto = "Ahora respondo",
                        modifier = Modifier.fillMaxWidth(),
                        color = color,
                        icono = null,
                        habilitado = vm.seleccionDetalles.isNotEmpty()
                    ) {
                        vm.avanzarFase()
                        retro.emitir(Aviso.TOQUE)
                    }
                }

                AnimatedVisibility(visible = vm.fase == FaseEscucha.RESPUESTA, enter = fadeIn()) {
                    Column {
                        Text("Que le dices?", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            reto.opciones.forEach { opcion ->
                                val elegida = vm.opcionEscucha?.id == opcion.id
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(18.dp))
                                        .clickable {
                                            vm.elegirOpcionEscucha(opcion)
                                            retro.emitir(Aviso.TOQUE)
                                        },
                                    color = if (elegida) color.copy(alpha = 0.16f)
                                    else MaterialTheme.colorScheme.surface,
                                    shadowElevation = if (elegida) 4.dp else 1.dp
                                ) {
                                    Row(
                                        Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            opcion.texto,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (elegida) {
                                            Spacer(Modifier.width(8.dp))
                                            MarcaCorrecta(color = color)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

/** Ondas de sonido animadas: refuerzan visualmente que aqui se escucha. */
@Composable
private fun OndasEscucha(color: Color) {
    val animaciones = LocalAjustes.current.animaciones
    val transicion = rememberInfiniteTransition(label = "ondas")
    val fase by transicion.animateFloat(
        initialValue = 0f,
        targetValue = if (animaciones) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Restart),
        label = "faseOndas"
    )
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(46.dp)
    ) {
        val centro = Offset(size.width / 2f, size.height / 2f)
        repeat(4) { i ->
            val progreso = ((fase + i * 0.25f) % 1f)
            val radio = size.height * (0.25f + progreso * 1.1f)
            drawCircle(
                color = color.copy(alpha = (1f - progreso) * 0.45f),
                radius = radio,
                center = centro,
                style = Stroke(width = 4f)
            )
        }
        drawCircle(color, size.height * 0.16f, centro)
        drawRect(
            color = Color.Transparent,
            topLeft = Offset.Zero,
            size = Size(size.width, size.height)
        )
    }
}
