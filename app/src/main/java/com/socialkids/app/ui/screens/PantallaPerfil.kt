package com.socialkids.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.socialkids.app.data.seed.CartasSeed
import com.socialkids.app.data.seed.MundoSeed
import com.socialkids.app.domain.usecase.InsigniaEvaluador
import com.socialkids.app.domain.usecase.ProgresoCalculadora
import com.socialkids.app.ui.JuegoViewModel
import com.socialkids.app.ui.art.Avatar
import com.socialkids.app.ui.art.SimboloFigura
import com.socialkids.app.ui.components.BarraProgreso
import com.socialkids.app.ui.components.BotonSuave
import com.socialkids.app.ui.components.Cabecera
import com.socialkids.app.ui.components.TituloSeccion
import com.socialkids.app.ui.navigation.Rutas
import com.socialkids.app.ui.theme.Paleta

/** Perfil del jugador: identidad, nivel y accesos a todo lo conseguido. */
@Composable
fun PantallaPerfil(
    juegoVM: JuegoViewModel,
    alVolver: () -> Unit,
    alEditar: () -> Unit,
    alAbrirRuta: (String) -> Unit
) {
    val estado by juegoVM.estado.collectAsStateWithLifecycle()
    val racha by juegoVM.racha.collectAsStateWithLifecycle()
    val perfil = estado.perfil
    val stats = estado.estadisticas
    val avatarSpec = CartasSeed.avatar(perfil?.avatarId ?: 0)
    val color = Paleta.tono(avatarSpec.tono)

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Cabecera(
            titulo = "Tu explorador",
            alVolver = alVolver,
            modifier = Modifier.padding(top = 28.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(
                    color = color.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(color.copy(alpha = 0.22f))
                        ) {
                            Avatar(avatarId = perfil?.avatarId ?: 0, modifier = Modifier.fillMaxSize())
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                perfil?.alias ?: "Explorador",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                "Avatar ${avatarSpec.nombre}",
                                style = MaterialTheme.typography.labelMedium,
                                color = color
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Nivel ${stats.nivel} - ${stats.xp} XP",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            BarraProgreso(
                                ProgresoCalculadora.progresoEnNivel(stats.xp),
                                Modifier.fillMaxWidth(),
                                color,
                                10.dp
                            )
                        }
                    }
                }
            }

            item {
                BotonSuave(
                    texto = "Cambiar apodo y avatar",
                    icono = Icons.Filled.Edit,
                    modifier = Modifier.fillMaxWidth(),
                    alPulsar = alEditar
                )
            }

            item { TituloSeccion("Lo que llevas conseguido", color = Paleta.Turquesa) }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilaLogro(
                        "Misiones completadas",
                        "${stats.misionesCompletadas} de ${MundoSeed.misiones.size}",
                        stats.misionesCompletadas.toFloat() / MundoSeed.misiones.size,
                        Paleta.Menta
                    )
                    FilaLogro(
                        "Cartas de la isla",
                        "${stats.cartasDesbloqueadas} de ${CartasSeed.cartas.size}",
                        stats.cartasDesbloqueadas.toFloat() / CartasSeed.cartas.size,
                        Paleta.Violeta
                    )
                    FilaLogro(
                        "Insignias",
                        "${estado.insignias.size} de ${InsigniaEvaluador.reglas.size}",
                        estado.insignias.size.toFloat() / InsigniaEvaluador.reglas.size,
                        Paleta.Naranja
                    )
                    FilaLogro(
                        "Zonas completadas",
                        "${stats.zonasCompletadas} de ${MundoSeed.zonas.size}",
                        stats.zonasCompletadas.toFloat() / MundoSeed.zonas.size,
                        Paleta.Coral
                    )
                }
            }

            item {
                Surface(
                    color = Paleta.Sol.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Paleta.Naranja)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Racha actual: ${racha.first} dias", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "Tu mejor racha fue de ${racha.second} dias. Si se corta no pasa nada: se empieza otra.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item { TituloSeccion("Ir a", color = Paleta.Violeta) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AccesoPerfil("Cartas", Icons.Filled.Style, Paleta.Violeta, Modifier.weight(1f)) {
                        alAbrirRuta(Rutas.COLECCION)
                    }
                    AccesoPerfil("Insignias", Icons.Filled.Star, Paleta.Naranja, Modifier.weight(1f)) {
                        alAbrirRuta(Rutas.INSIGNIAS)
                    }
                    AccesoPerfil("Numeros", Icons.Filled.Insights, Paleta.Turquesa, Modifier.weight(1f)) {
                        alAbrirRuta(Rutas.ESTADISTICAS)
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CartasSeed.cartas.take(8).forEach { carta ->
                        val tiene = estado.cartas.contains(carta.id)
                        Box(
                            Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (tiene) Paleta.tono(carta.tono).copy(alpha = 0.85f)
                                    else Paleta.Bloqueado.copy(alpha = 0.18f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            SimboloFigura(
                                carta.figura,
                                Color.White,
                                apagado = !tiene,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaLogro(titulo: String, detalle: String, progreso: Float, color: Color) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(titulo, style = MaterialTheme.typography.titleSmall)
                Text(detalle, style = MaterialTheme.typography.labelMedium, color = color)
            }
            Spacer(Modifier.height(8.dp))
            BarraProgreso(progreso, Modifier.fillMaxWidth(), color, 10.dp)
        }
    }
}

@Composable
private fun AccesoPerfil(
    titulo: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    alPulsar: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(84.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { alPulsar() },
        color = color.copy(alpha = 0.14f)
    ) {
        Column(
            Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icono, contentDescription = null, tint = color)
            Spacer(Modifier.height(6.dp))
            Text(titulo, style = MaterialTheme.typography.labelMedium)
        }
    }
}
