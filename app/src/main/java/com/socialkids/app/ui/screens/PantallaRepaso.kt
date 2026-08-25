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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.socialkids.app.data.seed.MundoSeed
import com.socialkids.app.domain.model.EstadoMision
import com.socialkids.app.ui.JuegoViewModel
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.components.BocadilloNima
import com.socialkids.app.ui.components.Cabecera
import com.socialkids.app.ui.components.EstadoVacio
import com.socialkids.app.ui.components.TarjetaMision
import com.socialkids.app.ui.components.TituloSeccion
import com.socialkids.app.ui.theme.Paleta

/**
 * Modo Repaso: propone volver a las misiones que se completaron
 * con menos de tres estrellas. No es un diagnostico, solo una lista de mejora.
 */
@Composable
fun PantallaRepaso(
    juegoVM: JuegoViewModel,
    alVolver: () -> Unit,
    alAbrirMision: (String) -> Unit
) {
    val estado by juegoVM.estado.collectAsStateWithLifecycle()
    val repaso by juegoVM.misionesRepaso.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Cabecera(
            titulo = "Practicar otra vez",
            subtitulo = "Misiones donde puedes subir estrellas",
            alVolver = alVolver,
            modifier = Modifier.padding(top = 28.dp)
        )

        if (repaso.isEmpty()) {
            EstadoVacio(
                titulo = if (estado.estadisticas.misionesCompletadas == 0) "Aun no hay nada que repasar" else "Todo dominado",
                mensaje = if (estado.estadisticas.misionesCompletadas == 0) {
                    "Completa alguna mision del mapa y aqui apareceran las que puedas mejorar."
                } else {
                    "Tienes tres estrellas en todo lo que has jugado. Sigue avanzando por el mapa."
                },
                estado = EstadoNima.ALEGRE
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    BocadilloNima(
                        texto = "Repetir no es castigo: la segunda vez se entiende mejor por que funciona.",
                        estado = EstadoNima.ANIMANDO
                    )
                }
                item {
                    Surface(
                        color = Paleta.Coral.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "${repaso.size} mision(es) por mejorar de ${MundoSeed.misiones.size} en total.",
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                item {
                    Spacer(Modifier.height(2.dp))
                    TituloSeccion("Empieza por las mas flojas", color = Paleta.Coral)
                }
                items(repaso, key = { it.id }) { mision ->
                    val estrellas = estado.progreso[mision.id]?.mejoresEstrellas ?: 0
                    TarjetaMision(
                        mision = mision,
                        estado = if (estrellas >= 3) EstadoMision.DOMINADA else EstadoMision.COMPLETADA,
                        estrellas = estrellas
                    ) { alAbrirMision(mision.id) }
                }
            }
        }
    }
}
