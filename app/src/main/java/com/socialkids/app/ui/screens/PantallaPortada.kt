package com.socialkids.app.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.socialkids.app.R
import com.socialkids.app.domain.model.EstadoJuego
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.art.FondoIsla
import com.socialkids.app.ui.art.Nima
import com.socialkids.app.ui.art.dibujarChispa
import com.socialkids.app.ui.components.BotonGrande
import com.socialkids.app.ui.theme.LocalAjustes
import com.socialkids.app.ui.theme.Paleta

/**
 * Portada de la isla: primera imagen fuerte de la app.
 * Mar animado, logotipo propio y Nima dando la bienvenida.
 */
@Composable
fun PantallaPortada(
    estado: EstadoJuego,
    alEntrar: () -> Unit
) {
    val animaciones = LocalAjustes.current.animaciones
    val transicion = androidx.compose.animation.core.rememberInfiniteTransition(label = "portada")
    val brillo by transicion.animateFloat(
        initialValue = 0.6f,
        targetValue = if (animaciones) 1f else 0.6f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "brillo"
    )

    Box(Modifier.fillMaxSize()) {
        FondoIsla(Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LogotipoSocialKids(brillo)
            Spacer(Modifier.height(6.dp))
            Surface(
                color = Color.White.copy(alpha = 0.85f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    stringResource(R.string.app_lema),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = Paleta.Turquesa
                )
            }

            Spacer(Modifier.height(18.dp))
            Nima(estado = EstadoNima.ALEGRE, tamanio = 150.dp)
            Spacer(Modifier.height(10.dp))

            Surface(
                color = Color.White.copy(alpha = 0.92f),
                shape = RoundedCornerShape(22.dp),
                shadowElevation = 4.dp
            ) {
                Column(
                    Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (estado.perfil == null) {
                            "Soy Nima. La isla perdio las palabras que unen a la gente."
                        } else {
                            "Hola otra vez, ${estado.perfil.alias}. La isla te estaba esperando."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = Paleta.TextoOscuro
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Empatia, escucha y palabras que no rompen nada.",
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        color = Paleta.Turquesa
                    )
                }
            }

            Spacer(Modifier.height(22.dp))
            BotonGrande(
                texto = if (estado.perfil == null) "Empezar la aventura" else "Continuar",
                modifier = Modifier.fillMaxWidth(0.82f),
                color = Paleta.Coral,
                icono = Icons.Filled.PlayArrow,
                alPulsar = alEntrar
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Sin internet, sin anuncios y sin datos personales",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
        }
    }
}

/** Logotipo textual con chispa dibujada, propio de la app. */
@Composable
fun LogotipoSocialKids(brillo: Float = 1f, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(Paleta.Sol.copy(alpha = 0.001f))
        ) {
            Canvas(Modifier.fillMaxSize()) {
                dibujarChispa(
                    Offset(size.width / 2f, size.height / 2f),
                    size.minDimension * 0.46f * brillo,
                    Paleta.Sol
                )
                dibujarChispa(
                    Offset(size.width * 0.82f, size.height * 0.24f),
                    size.minDimension * 0.14f,
                    Color.White
                )
            }
        }
        Spacer(Modifier.width(6.dp))
        Column {
            Text(
                "Social",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White
            )
            Text(
                "Kids",
                style = MaterialTheme.typography.displayMedium,
                color = Paleta.Sol
            )
        }
    }
}
