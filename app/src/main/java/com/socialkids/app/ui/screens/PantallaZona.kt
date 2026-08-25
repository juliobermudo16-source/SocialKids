package com.socialkids.app.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.socialkids.app.data.seed.CartasSeed
import com.socialkids.app.data.seed.MundoSeed
import com.socialkids.app.domain.model.EstadoMision
import com.socialkids.app.domain.model.ZonaId
import com.socialkids.app.domain.usecase.DesbloqueoEvaluador
import com.socialkids.app.ui.JuegoViewModel
import com.socialkids.app.ui.art.EscenaZona
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.art.SimboloFigura
import com.socialkids.app.ui.components.BarraProgreso
import com.socialkids.app.ui.components.BocadilloNima
import com.socialkids.app.ui.components.Cabecera
import com.socialkids.app.ui.components.TarjetaMision
import com.socialkids.app.ui.components.TituloSeccion
import com.socialkids.app.ui.theme.Paleta

/**
 * Pantalla de zona: escenario propio, misiones en cadena y las cartas
 * que se pueden conseguir aqui.
 */
@Composable
fun PantallaZona(
    zonaId: ZonaId,
    juegoVM: JuegoViewModel,
    alAbrirMision: (String) -> Unit,
    alVolver: () -> Unit
) {
    val estado by juegoVM.estado.collectAsStateWithLifecycle()
    val zona = MundoSeed.zona(zonaId)
    val misiones = MundoSeed.misionesDe(zonaId)
    val color = Paleta.colorZona(zonaId)
    val desbloqueada = DesbloqueoEvaluador.zonaDesbloqueada(zona, estado.estadisticas.xp)
    val porcentaje = DesbloqueoEvaluador.porcentajeZona(misiones, estado.progreso)

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box {
            EscenaZona(
                zona = zonaId,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Cabecera(
                titulo = zona.nombre,
                subtitulo = zona.lema,
                alVolver = alVolver,
                modifier = Modifier.padding(top = 28.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(
                    color = color.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(zona.descripcion, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BarraProgreso(porcentaje, Modifier.weight(1f), color, 12.dp)
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "${misiones.count { estado.progreso[it.id]?.completada == true }}/${misiones.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = color
                            )
                        }
                    }
                }
            }

            if (!desbloqueada) {
                item {
                    BocadilloNima(
                        texto = "Esta zona se abre con ${zona.xpNecesaria} XP. Te faltan ${(zona.xpNecesaria - estado.estadisticas.xp).coerceAtLeast(0)}.",
                        estado = EstadoNima.PENSATIVA
                    )
                }
            }

            item { TituloSeccion("Misiones", color = color) }

            items(misiones, key = { it.id }) { mision ->
                val estadoMision = DesbloqueoEvaluador.estadoMision(
                    mision, desbloqueada, estado.progreso, misiones
                )
                TarjetaMision(
                    mision = mision,
                    estado = estadoMision,
                    estrellas = estado.progreso[mision.id]?.mejoresEstrellas ?: 0
                ) {
                    if (estadoMision != EstadoMision.BLOQUEADA) alAbrirMision(mision.id)
                }
            }

            item {
                Spacer(Modifier.height(6.dp))
                TituloSeccion("Cartas de esta zona", color = color)
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    misiones.mapNotNull { it.cartaId }.forEach { cartaId ->
                        val carta = CartasSeed.carta(cartaId)
                        val tiene = estado.cartas.contains(cartaId)
                        if (carta != null) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    Modifier
                                        .height(60.dp)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            if (tiene) Paleta.tono(carta.tono).copy(alpha = 0.85f)
                                            else Paleta.Bloqueado.copy(alpha = 0.20f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SimboloFigura(
                                        carta.figura,
                                        Color.White,
                                        apagado = !tiene,
                                        modifier = Modifier.height(34.dp).fillMaxWidth()
                                    )
                                }
                                Text(
                                    if (tiene) carta.nombre else "???",
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
