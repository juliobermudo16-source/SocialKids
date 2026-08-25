package com.socialkids.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.socialkids.app.domain.usecase.InsigniaEvaluador
import com.socialkids.app.ui.JuegoViewModel
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.components.BarraProgreso
import com.socialkids.app.ui.components.BocadilloNima
import com.socialkids.app.ui.components.Cabecera
import com.socialkids.app.ui.components.TituloSeccion
import com.socialkids.app.ui.components.VistaInsignia
import com.socialkids.app.ui.theme.Paleta

/** Insignias: cada una con su regla real y su progreso calculado. */
@Composable
fun PantallaInsignias(juegoVM: JuegoViewModel, alVolver: () -> Unit) {
    val estado by juegoVM.estado.collectAsStateWithLifecycle()
    val stats = estado.estadisticas
    val reglas = InsigniaEvaluador.reglas
    val conseguidas = reglas.count { it.conseguida(stats) }
    val objetivo = InsigniaEvaluador.siguienteObjetivo(stats)

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Cabecera(
            titulo = "Insignias",
            subtitulo = "$conseguidas de ${reglas.size} conseguidas",
            alVolver = alVolver,
            modifier = Modifier.padding(top = 28.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                BarraProgreso(
                    valor = conseguidas.toFloat() / reglas.size,
                    modifier = Modifier.fillMaxWidth(),
                    color = Paleta.Naranja
                )
            }

            if (objetivo != null) {
                item {
                    Spacer(Modifier.height(4.dp))
                    BocadilloNima(
                        texto = "La que tienes mas cerca es \"${objetivo.insignia.nombre}\": ${objetivo.medida(stats)} de ${objetivo.objetivo}.",
                        estado = EstadoNima.ANIMANDO
                    )
                }
            }

            item { TituloSeccion("Conseguidas", color = Paleta.Exito) }
            val logradas = reglas.filter { it.conseguida(stats) }
            if (logradas.isEmpty()) {
                item {
                    Text(
                        "Todavia ninguna. La primera llega al terminar tu primera mision.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(logradas, key = { it.insignia.id }) { regla ->
                VistaInsignia(regla.insignia, conseguida = true, progreso = 1f)
            }

            item {
                Spacer(Modifier.height(6.dp))
                TituloSeccion("Por conseguir", color = Paleta.Bloqueado)
            }
            items(reglas.filter { !it.conseguida(stats) }, key = { it.insignia.id }) { regla ->
                VistaInsignia(regla.insignia, conseguida = false, progreso = regla.progreso(stats))
            }
        }
    }
}
