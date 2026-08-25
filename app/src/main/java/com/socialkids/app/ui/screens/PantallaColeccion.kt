package com.socialkids.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.socialkids.app.data.seed.CartasSeed
import com.socialkids.app.domain.model.Carta
import com.socialkids.app.domain.model.CategoriaCarta
import com.socialkids.app.ui.JuegoViewModel
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.art.SimboloFigura
import com.socialkids.app.ui.components.BarraProgreso
import com.socialkids.app.ui.components.BocadilloNima
import com.socialkids.app.ui.components.Cabecera
import com.socialkids.app.ui.components.TarjetaCarta
import com.socialkids.app.ui.theme.Paleta

/** Coleccion de Cartas de la Isla. Se desbloquean completando misiones reales. */
@Composable
fun PantallaColeccion(juegoVM: JuegoViewModel, alVolver: () -> Unit) {
    val estado by juegoVM.estado.collectAsStateWithLifecycle()
    var filtro by remember { mutableStateOf<CategoriaCarta?>(null) }
    var detalle by remember { mutableStateOf<Carta?>(null) }

    val cartas = CartasSeed.cartas.filter { filtro == null || it.categoria == filtro }
    val total = CartasSeed.cartas.size
    val conseguidas = estado.cartas.size

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Cabecera(
            titulo = "Cartas de la Isla",
            subtitulo = "$conseguidas de $total descubiertas",
            alVolver = alVolver,
            modifier = Modifier.padding(top = 28.dp)
        )

        Column(Modifier.padding(horizontal = 16.dp)) {
            BarraProgreso(
                valor = conseguidas.toFloat() / total,
                modifier = Modifier.fillMaxWidth(),
                color = Paleta.Violeta
            )
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FiltroChip("Todas", filtro == null) { filtro = null }
                CategoriaCarta.entries.forEach { cat ->
                    FiltroChip(nombreCategoria(cat), filtro == cat) { filtro = cat }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (conseguidas == 0) {
            BocadilloNima(
                texto = "Todavia no tienes cartas. Completa una mision y la primera aparecera aqui.",
                estado = EstadoNima.PENSATIVA,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(cartas, key = { it.id }) { carta ->
                val tiene = estado.cartas.contains(carta.id)
                TarjetaCarta(carta = carta, desbloqueada = tiene) {
                    if (tiene) detalle = carta
                }
            }
        }
    }

    val cartaDetalle = detalle
    if (cartaDetalle != null) {
        val color = Paleta.tono(cartaDetalle.tono)
        AlertDialog(
            onDismissRequest = { detalle = null },
            confirmButton = {
                TextButton(onClick = { detalle = null }) { Text("Cerrar") }
            },
            icon = {
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    SimboloFigura(cartaDetalle.figura, Color.White, modifier = Modifier.size(38.dp))
                }
            },
            title = { Text(cartaDetalle.nombre) },
            text = {
                Column {
                    Text(cartaDetalle.lema, style = MaterialTheme.typography.titleSmall, color = color)
                    Spacer(Modifier.height(8.dp))
                    Text(cartaDetalle.dato, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${nombreCategoria(cartaDetalle.categoria)} - ${cartaDetalle.rareza.name.lowercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
}

@Composable
fun FiltroChip(texto: String, activo: Boolean, alPulsar: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { alPulsar() },
        color = if (activo) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            texto,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (activo) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun nombreCategoria(cat: CategoriaCarta): String = when (cat) {
    CategoriaCarta.EMOCION -> "Emociones"
    CategoriaCarta.HABILIDAD -> "Habilidades"
    CategoriaCarta.PERSONAJE -> "Personajes"
    CategoriaCarta.LUGAR -> "Lugares"
}
