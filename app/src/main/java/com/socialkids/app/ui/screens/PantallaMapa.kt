package com.socialkids.app.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.socialkids.app.R
import com.socialkids.app.data.seed.MundoSeed
import com.socialkids.app.domain.model.ZonaId
import com.socialkids.app.domain.usecase.DesbloqueoEvaluador
import com.socialkids.app.domain.usecase.ProgresoCalculadora
import com.socialkids.app.ui.JuegoViewModel
import com.socialkids.app.ui.art.Avatar
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.art.FondoIsla
import com.socialkids.app.ui.art.SimboloFigura
import com.socialkids.app.ui.components.BarraProgreso
import com.socialkids.app.ui.components.BocadilloNima
import com.socialkids.app.ui.components.BotonGrande
import com.socialkids.app.ui.components.TituloSeccion
import com.socialkids.app.ui.components.etiquetaMecanica
import com.socialkids.app.ui.navigation.Rutas
import com.socialkids.app.ui.theme.LocalAjustes
import com.socialkids.app.ui.theme.Paleta

/**
 * Centro de la experiencia: el mapa de la Isla Conecta.
 * No es una lista de botones: es un mapa con sendero, nodos de zona,
 * progreso visible y la siguiente mision destacada.
 */
@Composable
fun PantallaMapa(
    juegoVM: JuegoViewModel,
    alAbrirZona: (ZonaId) -> Unit,
    alAbrirMision: (String) -> Unit,
    alAbrirRuta: (String) -> Unit
) {
    val estado by juegoVM.estado.collectAsStateWithLifecycle()
    val siguiente by juegoVM.siguienteMision.collectAsStateWithLifecycle()
    val racha by juegoVM.racha.collectAsStateWithLifecycle()
    val perfil = estado.perfil

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // ---------- Cabecera del jugador ----------
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier
                    .padding(start = 14.dp, end = 14.dp, top = 40.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Paleta.tono(perfil?.avatarId ?: 0).copy(alpha = 0.20f))
                        .clickable { alAbrirRuta(Rutas.PERFIL) }
                ) {
                    Avatar(avatarId = perfil?.avatarId ?: 0, modifier = Modifier.fillMaxSize())
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        perfil?.alias ?: "Explorador",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Paleta.Violeta,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "Nivel ${estado.estadisticas.nivel}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        BarraProgreso(
                            valor = ProgresoCalculadora.progresoEnNivel(estado.estadisticas.xp),
                            modifier = Modifier.weight(1f),
                            color = Paleta.Violeta,
                            alto = 10.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "${estado.estadisticas.xp} XP",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Paleta.Naranja)
                    Text(
                        "${racha.first}d",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        // ---------- Mapa ----------
        MapaIslaInteractivo(
            xp = estado.estadisticas.xp,
            progresoZonas = MundoSeed.zonas.associate { zona ->
                zona.id to DesbloqueoEvaluador.porcentajeZona(
                    MundoSeed.misionesDe(zona.id),
                    estado.progreso
                )
            },
            zonaDestacada = siguiente?.zonaId,
            alAbrirZona = alAbrirZona
        )

        // ---------- Siguiente mision ----------
        Column(Modifier.padding(16.dp)) {
            val mision = siguiente
            if (mision != null) {
                val zona = MundoSeed.zona(mision.zonaId)
                val color = Paleta.colorZona(mision.zonaId)
                TituloSeccion("Tu siguiente paso", color = color)
                Spacer(Modifier.height(10.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            zona.nombre.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = color
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(mision.titulo, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            mision.consigna,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(10.dp)) {
                                Text(
                                    etiquetaMecanica(mision.mecanica),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = color,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "+${mision.xp} XP",
                                style = MaterialTheme.typography.labelMedium,
                                color = Paleta.Naranja
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                        BotonGrande(
                            texto = "Empezar mision",
                            modifier = Modifier.fillMaxWidth(),
                            color = color,
                            icono = Icons.Filled.PlayArrow
                        ) { alAbrirMision(mision.id) }
                    }
                }
            } else {
                BocadilloNima(
                    texto = "Has recorrido toda la isla. Prueba el modo Repaso para subir tus estrellas.",
                    estado = EstadoNima.ALEGRE
                )
            }

            Spacer(Modifier.height(20.dp))
            TituloSeccion("Tu mochila", color = Paleta.Turquesa)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AccesoRapido(
                    "Cartas",
                    "${estado.cartas.size}/${com.socialkids.app.data.seed.CartasSeed.cartas.size}",
                    Icons.Filled.Style,
                    Paleta.Violeta,
                    Modifier.weight(1f)
                ) { alAbrirRuta(Rutas.COLECCION) }
                AccesoRapido(
                    "Insignias",
                    "${estado.insignias.size}/${com.socialkids.app.domain.usecase.InsigniaEvaluador.reglas.size}",
                    Icons.Filled.Star,
                    Paleta.Naranja,
                    Modifier.weight(1f)
                ) { alAbrirRuta(Rutas.INSIGNIAS) }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AccesoRapido(
                    "Diario",
                    "${estado.estadisticas.registrosAnimo} notas",
                    Icons.Filled.Book,
                    Paleta.Menta,
                    Modifier.weight(1f)
                ) { alAbrirRuta(Rutas.DIARIO) }
                AccesoRapido(
                    "Calma",
                    "Respirar",
                    Icons.Filled.Favorite,
                    Paleta.Cielo,
                    Modifier.weight(1f)
                ) { alAbrirRuta(Rutas.CALMA) }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AccesoRapido(
                    "Repaso",
                    "Subir estrellas",
                    Icons.Filled.Refresh,
                    Paleta.Coral,
                    Modifier.weight(1f)
                ) { alAbrirRuta(Rutas.REPASO) }
                AccesoRapido(
                    "Ajustes",
                    "Sonido y texto",
                    Icons.Filled.Settings,
                    Paleta.Bloqueado,
                    Modifier.weight(1f)
                ) { alAbrirRuta(Rutas.AJUSTES) }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

/** El mapa en si: fondo, sendero punteado y nodos de zona colocados por coordenadas. */
@Composable
private fun MapaIslaInteractivo(
    xp: Int,
    progresoZonas: Map<ZonaId, Float>,
    zonaDestacada: ZonaId?,
    alAbrirZona: (ZonaId) -> Unit
) {
    val animaciones = LocalAjustes.current.animaciones
    val transicion = rememberInfiniteTransition(label = "mapa")
    val latido by transicion.animateFloat(
        initialValue = 1f,
        targetValue = if (animaciones) 1.12f else 1f,
        animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse),
        label = "latido"
    )

    val descripcionMapa = stringResource(R.string.desc_mapa)
    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .height(420.dp)
            .semantics { contentDescription = descripcionMapa }
    ) {
        val anchoPx = constraints.maxWidth.toFloat()
        val altoPx = constraints.maxHeight.toFloat()

        FondoIsla(Modifier.fillMaxSize())

        // Sendero punteado que une las zonas en orden
        Canvas(Modifier.fillMaxSize()) {
            val puntos = MundoSeed.zonas.sortedBy { it.orden }
                .map { Offset(it.mapaX * anchoPx, it.mapaY * altoPx) }
            for (i in 0 until puntos.size - 1) {
                drawLine(
                    color = Color.White.copy(alpha = 0.75f),
                    start = puntos[i],
                    end = puntos[i + 1],
                    strokeWidth = 7f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 16f))
                )
            }
            // Marco decorativo del mapa
            drawRoundRect(
                color = Color.White.copy(alpha = 0.35f),
                style = Stroke(width = 6f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(28f)
            )
        }

        MundoSeed.zonas.forEach { zona ->
            val desbloqueada = DesbloqueoEvaluador.zonaDesbloqueada(zona, xp)
            val destacada = zona.id == zonaDestacada
            val color = Paleta.colorZona(zona.id)
            val porcentaje = progresoZonas[zona.id] ?: 0f

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(
                        x = with(androidx.compose.ui.platform.LocalDensity.current) {
                            (zona.mapaX * anchoPx).toDp() - 46.dp
                        },
                        y = with(androidx.compose.ui.platform.LocalDensity.current) {
                            (zona.mapaY * altoPx).toDp() - 46.dp
                        }
                    )
                    .width(92.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .scale(if (destacada) latido else 1f)
                        .clip(CircleShape)
                        .background(
                            if (desbloqueada) color else Paleta.Bloqueado.copy(alpha = 0.75f)
                        )
                        .clickable(enabled = desbloqueada) { alAbrirZona(zona.id) },
                    contentAlignment = Alignment.Center
                ) {
                    if (desbloqueada) {
                        SimboloFigura(
                            figura = figuraDeZona(zona.id),
                            color = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    } else {
                        Icon(Icons.Filled.Lock, contentDescription = "Zona bloqueada", tint = Color.White)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(9.dp)
                ) {
                    Column(
                        Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            zona.nombre.substringAfter("de ").substringAfter("que ")
                                .replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall,
                            color = Paleta.TextoOscuro,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                        if (desbloqueada) {
                            Text(
                                "${(porcentaje * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = color
                            )
                        } else {
                            Text(
                                "${zona.xpNecesaria} XP",
                                style = MaterialTheme.typography.labelSmall,
                                color = Paleta.Bloqueado
                            )
                        }
                    }
                }
            }
        }
    }
}

fun figuraDeZona(zona: ZonaId) = when (zona) {
    ZonaId.FARO -> com.socialkids.app.domain.model.Figura.FARO
    ZonaId.BOSQUE -> com.socialkids.app.domain.model.Figura.HOJA
    ZonaId.PUENTE -> com.socialkids.app.domain.model.Figura.PUENTE
    ZonaId.PLAZA -> com.socialkids.app.domain.model.Figura.BURBUJA
    ZonaId.TALLER -> com.socialkids.app.domain.model.Figura.LLAVE
    ZonaId.MIRADOR -> com.socialkids.app.domain.model.Figura.ESTRELLA
}

@Composable
private fun AccesoRapido(
    titulo: String,
    detalle: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    alPulsar: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(74.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { alPulsar() },
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 3.dp
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = color)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(titulo, style = MaterialTheme.typography.titleSmall)
                Text(
                    detalle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
