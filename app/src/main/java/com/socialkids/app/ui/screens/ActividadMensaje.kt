package com.socialkids.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.socialkids.app.data.seed.RetosMensaje
import com.socialkids.app.domain.engine.Estilo
import com.socialkids.app.domain.engine.Ficha
import com.socialkids.app.domain.engine.Ranura
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
 * Constructor de Mensajes: se arma un mensaje-yo con cuatro piezas.
 * La frase se ve crecer en vivo y el estilo cambia de color segun lo que eliges.
 */
@Composable
fun ActividadMensaje(vm: ActividadViewModel, color: Color) {
    val reto = vm.retoMensaje
    val retro = recordarRetroalimentacion()
    val arrastre = LocalArrastre.current
    var seleccionada by remember { mutableStateOf<Ficha?>(null) }

    val estilo = vm.estiloPreview
    val colorEstilo by animateColorAsState(
        when (estilo) {
            Estilo.ASERTIVO -> Paleta.Exito
            Estilo.AGRESIVO -> Paleta.Error
            Estilo.PASIVO -> Paleta.Bloqueado
        },
        tween(300),
        label = "estilo"
    )

    fun colocar(ranura: Ranura, ficha: Ficha?) {
        vm.colocarFicha(ranura, ficha)
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
                Text(reto.situacion, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Vista previa viva del mensaje
        Surface(
            color = colorEstilo.copy(alpha = 0.12f),
            shape = RoundedCornerShape(
                topStart = 22.dp, topEnd = 22.dp, bottomEnd = 22.dp, bottomStart = 6.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, colorEstilo.copy(alpha = 0.55f), RoundedCornerShape(
                    topStart = 22.dp, topEnd = 22.dp, bottomEnd = 22.dp, bottomStart = 6.dp
                ))
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    vm.frasePreview.ifBlank { "Aqui aparecera tu mensaje..." },
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .width(10.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(colorEstilo)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Tono: ${estilo.etiqueta}",
                        style = MaterialTheme.typography.labelMedium,
                        color = colorEstilo
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Ranura.entries.forEach { ranura ->
            val puesta = vm.colocacionMensaje[ranura]
            Column(Modifier.padding(bottom = 12.dp)) {
                Text(ranura.etiqueta, style = MaterialTheme.typography.labelMedium, color = color)
                Spacer(Modifier.height(6.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .zonaSoltar(ranura, arrastre)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            if (seleccionada != null && seleccionada!!.ranura == ranura) {
                                colocar(ranura, seleccionada)
                            } else {
                                colocar(ranura, null)
                            }
                        }
                        .border(
                            2.dp,
                            if (puesta != null) colorEstiloFicha(puesta) else Paleta.Bloqueado.copy(alpha = 0.45f),
                            RoundedCornerShape(16.dp)
                        ),
                    color = if (puesta != null) colorEstiloFicha(puesta).copy(alpha = 0.12f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ) {
                    Row(
                        Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            puesta?.texto ?: "Elige una ficha",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (puesta != null) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    RetosMensaje.fichasDe(reto, ranura).forEach { ficha ->
                        val elegida = seleccionada?.id == ficha.id
                        val puestaAqui = puesta?.id == ficha.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    if (puestaAqui) {
                                        colocar(ranura, null)
                                    } else {
                                        seleccionada = ficha
                                        colocar(ranura, ficha)
                                    }
                                }
                                .piezaArrastrable(
                                    dato = ficha.id,
                                    vistaPrevia = { FichaFlotante(ficha.texto) },
                                    alSoltar = { zona ->
                                        if (zona is Ranura && zona == ficha.ranura) colocar(zona, ficha)
                                    }
                                ),
                            color = when {
                                puestaAqui -> colorEstiloFicha(ficha).copy(alpha = 0.20f)
                                elegida -> color.copy(alpha = 0.12f)
                                else -> MaterialTheme.colorScheme.surface
                            },
                            shadowElevation = if (puestaAqui) 4.dp else 1.dp
                        ) {
                            Text(
                                ficha.texto,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        BocadilloNima(
            texto = "Las cuatro partes juntas son lo que hace que el otro entienda sin sentirse atacado.",
            estado = EstadoNima.NEUTRAL,
            tamanioNima = 64.dp
        )
        Spacer(Modifier.height(20.dp))
    }
}

/** El color de la ficha no se revela hasta que se coloca: asi no se resuelve por color. */
private fun colorEstiloFicha(ficha: Ficha): Color = when (ficha.estilo) {
    Estilo.ASERTIVO -> Paleta.Exito
    Estilo.AGRESIVO -> Paleta.Error
    Estilo.PASIVO -> Paleta.Bloqueado
}

@Composable
private fun FichaFlotante(texto: String) {
    Surface(
        color = Paleta.Violeta,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 10.dp
    ) {
        Text(
            texto,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
    }
}
