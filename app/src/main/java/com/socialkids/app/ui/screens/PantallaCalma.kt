package com.socialkids.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.art.FondoIsla
import com.socialkids.app.ui.art.Nima
import com.socialkids.app.ui.components.BotonGrande
import com.socialkids.app.ui.components.BotonSuave
import com.socialkids.app.ui.components.Cabecera
import com.socialkids.app.ui.theme.LocalAjustes
import com.socialkids.app.ui.theme.Paleta
import com.socialkids.app.util.Aviso
import com.socialkids.app.util.recordarRetroalimentacion
import kotlinx.coroutines.delay

private enum class FaseRespiracion(val etiqueta: String, val segundos: Int, val escala: Float) {
    INSPIRAR("Toma aire", 4, 1f),
    SOSTENER("Sujeta", 4, 1f),
    SOLTAR("Suelta despacio", 6, 0.55f)
}

/**
 * Rincon de calma: respiracion 4-4-6 guiada con una animacion real,
 * no un texto que cambia. Disponible siempre, sin desbloqueos.
 */
@Composable
fun PantallaCalma(alVolver: () -> Unit) {
    val retro = recordarRetroalimentacion()
    val animaciones = LocalAjustes.current.animaciones
    var enMarcha by remember { mutableStateOf(false) }
    var fase by remember { mutableStateOf(FaseRespiracion.INSPIRAR) }
    var restante by remember { mutableIntStateOf(FaseRespiracion.INSPIRAR.segundos) }
    var ciclos by remember { mutableIntStateOf(0) }

    val escala by animateFloatAsState(
        targetValue = if (!enMarcha) 0.75f else fase.escala,
        animationSpec = tween(if (animaciones) fase.segundos * 1000 else 0),
        label = "respiracion"
    )

    LaunchedEffect(enMarcha) {
        while (enMarcha) {
            delay(1000)
            if (restante > 1) {
                restante--
            } else {
                fase = when (fase) {
                    FaseRespiracion.INSPIRAR -> FaseRespiracion.SOSTENER
                    FaseRespiracion.SOSTENER -> FaseRespiracion.SOLTAR
                    FaseRespiracion.SOLTAR -> {
                        ciclos++
                        retro.emitir(Aviso.TOQUE)
                        FaseRespiracion.INSPIRAR
                    }
                }
                restante = fase.segundos
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        FondoIsla(Modifier.fillMaxSize(), oscuro = true)

        Column(Modifier.fillMaxSize()) {
            Cabecera(
                titulo = "Rincon de calma",
                subtitulo = "Respiracion 4-4-6",
                alVolver = alVolver,
                modifier = Modifier.padding(top = 28.dp)
            )

            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    Modifier.size(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(Modifier.fillMaxSize()) {
                        val centro = Offset(size.width / 2f, size.height / 2f)
                        val radioBase = size.minDimension * 0.42f
                        // Aros guia
                        repeat(3) { i ->
                            drawCircle(
                                color = Color.White.copy(alpha = 0.10f),
                                radius = radioBase * (0.6f + i * 0.22f),
                                center = centro,
                                style = Stroke(width = 3f)
                            )
                        }
                        val radio = radioBase * escala
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Paleta.Cielo.copy(alpha = 0.75f), Paleta.Turquesa.copy(alpha = 0.25f)),
                                center = centro,
                                radius = radio
                            ),
                            radius = radio,
                            center = centro
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.85f),
                            radius = radio,
                            center = centro,
                            style = Stroke(width = 5f)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (enMarcha) fase.etiqueta else "Listo?",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        if (enMarcha) {
                            Text(
                                "$restante",
                                style = MaterialTheme.typography.displayLarge,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Nima(
                    estado = if (enMarcha) EstadoNima.PENSATIVA else EstadoNima.NEUTRAL,
                    tamanio = 90.dp
                )
                Spacer(Modifier.height(10.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "Soltar el aire mas despacio de lo que lo tomas le dice al cuerpo que ya no hay peligro.",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Paleta.TextoOscuro,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "Ciclos completados: $ciclos",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BotonSuave(texto = "Reiniciar", icono = Icons.Filled.Refresh) {
                    enMarcha = false
                    fase = FaseRespiracion.INSPIRAR
                    restante = FaseRespiracion.INSPIRAR.segundos
                    ciclos = 0
                }
                BotonGrande(
                    texto = if (enMarcha) "Pausar" else "Empezar",
                    modifier = Modifier.weight(1f),
                    color = Paleta.Cielo,
                    icono = Icons.Filled.PlayArrow
                ) {
                    enMarcha = !enMarcha
                    retro.emitir(Aviso.TOQUE)
                }
            }
        }
    }
}
