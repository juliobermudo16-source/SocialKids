package com.socialkids.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.socialkids.app.domain.model.Mecanica
import com.socialkids.app.ui.ActividadViewModel
import com.socialkids.app.ui.JuegoViewModel
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.art.Nima
import com.socialkids.app.ui.art.SimboloFigura
import com.socialkids.app.ui.components.BotonGrande
import com.socialkids.app.ui.components.BotonSuave
import com.socialkids.app.ui.components.Cabecera
import com.socialkids.app.ui.components.Estrellas
import com.socialkids.app.ui.components.PanelExplicacion
import com.socialkids.app.ui.components.etiquetaMecanica
import com.socialkids.app.ui.theme.Paleta
import com.socialkids.app.util.Aviso
import com.socialkids.app.util.recordarRetroalimentacion

/**
 * Pantalla contenedora de una mision. Elige la mecanica adecuada,
 * gestiona el boton de terminar y muestra la pantalla de recompensa.
 */
@Composable
fun PantallaMision(
    misionId: String,
    juegoVM: JuegoViewModel,
    alSalir: () -> Unit,
    alIrAColeccion: () -> Unit
) {
    val vm: ActividadViewModel = viewModel(
        key = "actividad_$misionId",
        factory = ActividadViewModel.factory(misionId)
    )
    val recompensa by juegoVM.ultimaRecompensa.collectAsStateWithLifecycle()
    val retro = recordarRetroalimentacion()
    val mision = vm.mision
    val color = Paleta.colorZona(mision.zonaId)
    val resultado = vm.resultado

    DisposableEffect(misionId) {
        onDispose { juegoVM.limpiarRecompensa() }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(Modifier.fillMaxSize()) {
            Cabecera(
                titulo = mision.titulo,
                subtitulo = etiquetaMecanica(mision.mecanica),
                alVolver = alSalir,
                modifier = Modifier.padding(top = 28.dp)
            )

            Box(Modifier.weight(1f)) {
                when (mision.mecanica) {
                    Mecanica.ROSTROS -> ActividadRostros(vm, color)
                    Mecanica.ESCUCHA -> ActividadEscucha(vm, color)
                    Mecanica.PUENTE -> ActividadPuente(vm, color)
                    Mecanica.MENSAJE -> ActividadMensaje(vm, color)
                    Mecanica.CONFLICTO -> ActividadConflicto(vm, color)
                    Mecanica.TERMOMETRO -> ActividadTermometro(vm, color)
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 10.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BotonSuave(texto = "Reiniciar", icono = Icons.Filled.Refresh) {
                        vm.reiniciar()
                        retro.emitir(Aviso.TOQUE)
                    }
                    Spacer(Modifier.width(10.dp))
                    BotonGrande(
                        texto = "Terminar",
                        modifier = Modifier.weight(1f),
                        color = color,
                        icono = Icons.Filled.Check,
                        habilitado = vm.listoParaEvaluar()
                    ) {
                        val res = vm.evaluar()
                        juegoVM.guardarResultado(misionId, res, vm.hito)
                        retro.emitir(if (res.estrellas >= 2) Aviso.ACIERTO else Aviso.FALLO)
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = resultado != null,
            enter = fadeIn() + slideInVertically { it / 3 }
        ) {
            if (resultado != null) {
                PantallaResultado(
                    resultado = resultado,
                    recompensa = recompensa,
                    color = color,
                    alRepetir = {
                        vm.reiniciar()
                        juegoVM.limpiarRecompensa()
                    },
                    alSalir = alSalir,
                    alVerColeccion = alIrAColeccion
                )
            }
        }
    }
}

/** Pantalla de recompensa: estrellas, explicacion educativa y lo desbloqueado. */
@Composable
private fun PantallaResultado(
    resultado: com.socialkids.app.domain.model.ResultadoActividad,
    recompensa: com.socialkids.app.domain.model.Recompensa?,
    color: Color,
    alRepetir: () -> Unit,
    alSalir: () -> Unit,
    alVerColeccion: () -> Unit
) {
    val retro = recordarRetroalimentacion()
    DisposableEffect(recompensa?.cartaNueva?.id, recompensa?.insigniasNuevas?.size) {
        if (recompensa?.cartaNueva != null || (recompensa?.insigniasNuevas?.isNotEmpty() == true)) {
            retro.emitir(Aviso.DESBLOQUEO)
        }
        onDispose { }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.97f))
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))
            Nima(
                estado = when {
                    resultado.estrellas >= 3 -> EstadoNima.ANIMANDO
                    resultado.estrellas >= 1 -> EstadoNima.ALEGRE
                    else -> EstadoNima.PENSATIVA
                },
                tamanio = 120.dp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                resultado.titulo,
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            Estrellas(resultado.estrellas, tamanio = 42.dp)
            Spacer(Modifier.height(6.dp))
            Text(
                "${resultado.puntaje} puntos",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))
            PanelExplicacion(
                titulo = "Por que",
                explicacion = resultado.explicacion,
                consejo = resultado.consejo,
                color = color
            )

            if (resultado.detalles.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        resultado.detalles.forEach {
                            Text("- $it", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            if (recompensa != null) {
                Spacer(Modifier.height(16.dp))
                Surface(
                    color = Paleta.Sol.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Has ganado", style = MaterialTheme.typography.titleSmall, color = Paleta.Naranja)
                        Spacer(Modifier.height(6.dp))
                        Text("+${recompensa.xpGanada} XP", style = MaterialTheme.typography.headlineSmall)
                        if (recompensa.subioNivel) {
                            Text(
                                "Subes al nivel ${recompensa.nivelNuevo}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Paleta.Violeta
                            )
                        }
                        val carta = recompensa.cartaNueva
                        if (carta != null) {
                            Spacer(Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Paleta.tono(carta.tono)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SimboloFigura(carta.figura, Color.White, modifier = Modifier.size(30.dp))
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text("Carta nueva: ${carta.nombre}", style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        carta.dato,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        recompensa.insigniasNuevas.forEach { insignia ->
                            Spacer(Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Paleta.tono(insignia.tono)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SimboloFigura(insignia.figura, Color.White, modifier = Modifier.size(26.dp))
                                }
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    "Insignia: ${insignia.nombre}",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }
                        val zona = recompensa.zonaCompletada
                        if (zona != null) {
                            Spacer(Modifier.height(10.dp))
                            Text(
                                "Zona completada: ${zona.nombre}",
                                style = MaterialTheme.typography.titleSmall,
                                color = Paleta.Exito
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            BotonGrande(
                texto = "Volver al mapa",
                modifier = Modifier.fillMaxWidth(),
                color = color,
                icono = Icons.Filled.Check,
                alPulsar = alSalir
            )
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BotonSuave(
                    texto = "Practicar otra vez",
                    icono = Icons.Filled.Refresh,
                    modifier = Modifier.weight(1f),
                    alPulsar = alRepetir
                )
                if (recompensa?.cartaNueva != null) {
                    BotonSuave(
                        texto = "Ver coleccion",
                        modifier = Modifier.weight(1f),
                        alPulsar = alVerColeccion
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

/** Icono auxiliar de comprobacion usado por varias actividades. */
@Composable
fun MarcaCorrecta(modifier: Modifier = Modifier, color: Color = Paleta.Exito) {
    Box(
        modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Filled.Check,
            contentDescription = "Correcto",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
    }
}
