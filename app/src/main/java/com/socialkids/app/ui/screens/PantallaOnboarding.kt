package com.socialkids.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.socialkids.app.domain.model.ZonaId
import com.socialkids.app.ui.art.EscenaZona
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.art.Nima
import com.socialkids.app.ui.components.BotonGrande
import com.socialkids.app.ui.components.BotonSuave
import com.socialkids.app.ui.theme.Paleta
import kotlinx.coroutines.launch

private data class PaginaOnboarding(
    val titulo: String,
    val texto: String,
    val zona: ZonaId?,
    val estadoNima: EstadoNima,
    val color: androidx.compose.ui.graphics.Color
)

private val paginas = listOf(
    PaginaOnboarding(
        "La isla se quedo muda",
        "En la Isla Conecta la gente dejo de entenderse. Tu trabajo es devolverle las palabras que unen.",
        null, EstadoNima.PENSATIVA, Paleta.Turquesa
    ),
    PaginaOnboarding(
        "Seis zonas, seis habilidades",
        "Emociones, escucha, empatia, palabras claras, acuerdos y amistad. Cada zona entrena una cosa distinta.",
        ZonaId.PUENTE, EstadoNima.NEUTRAL, Paleta.Violeta
    ),
    PaginaOnboarding(
        "Aqui se toca, no se rellena",
        "Construyes caras, arrastras piezas, negocias y decides. Nada de examenes.",
        ZonaId.TALLER, EstadoNima.SORPRENDIDA, Paleta.Sol
    ),
    PaginaOnboarding(
        "Todo se queda en tu movil",
        "Sin internet, sin cuentas y sin tu nombre real. Solo eliges un apodo y un avatar.",
        ZonaId.MIRADOR, EstadoNima.ALEGRE, Paleta.Coral
    )
)

/** Onboarding de cuatro pantallas. Solo aparece la primera vez. */
@Composable
fun PantallaOnboarding(alTerminar: () -> Unit) {
    val estadoPager = rememberPagerState(pageCount = { paginas.size })
    val ambito = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (estadoPager.currentPage < paginas.size - 1) {
                BotonSuave(texto = "Saltar", alPulsar = alTerminar)
            }
        }

        HorizontalPager(
            state = estadoPager,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { indice ->
            val pagina = paginas[indice]
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (pagina.zona != null) {
                        EscenaZona(
                            zona = pagina.zona,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(28.dp))
                        )
                    } else {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(28.dp))
                                .background(Paleta.degradadoCielo)
                        )
                    }
                    Nima(estado = pagina.estadoNima, tamanio = 130.dp)
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    pagina.titulo,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = pagina.color
                )
                Spacer(Modifier.height(10.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        pagina.texto,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(paginas.size) { i ->
                val activo = estadoPager.currentPage == i
                val ancho by animateFloatAsState(if (activo) 26f else 10f, tween(250), label = "punto$i")
                Box(
                    Modifier
                        .padding(horizontal = 4.dp)
                        .size(width = ancho.dp, height = 10.dp)
                        .clip(CircleShape)
                        .background(if (activo) paginas[i].color else Paleta.Bloqueado.copy(alpha = 0.4f))
                )
            }
        }

        BotonGrande(
            texto = if (estadoPager.currentPage == paginas.size - 1) "Crear mi explorador" else "Siguiente",
            modifier = Modifier.fillMaxWidth(),
            color = paginas[estadoPager.currentPage].color
        ) {
            if (estadoPager.currentPage == paginas.size - 1) {
                alTerminar()
            } else {
                ambito.launch { estadoPager.animateScrollToPage(estadoPager.currentPage + 1) }
            }
        }
    }
}
