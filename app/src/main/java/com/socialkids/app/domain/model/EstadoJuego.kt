package com.socialkids.app.domain.model

import com.socialkids.app.domain.usecase.EstadisticasJugador
import com.socialkids.app.domain.usecase.ProgresoMision

/** Foto completa del juego que consume la interfaz. */
data class EstadoJuego(
    val perfil: PerfilJugador? = null,
    val progreso: Map<String, ProgresoMision> = emptyMap(),
    val cartas: Set<String> = emptySet(),
    val insignias: Set<String> = emptySet(),
    val estadisticas: EstadisticasJugador = EstadisticasJugador(),
    val cargando: Boolean = true
)

data class PerfilJugador(
    val alias: String,
    val avatarId: Int,
    val xp: Int,
    val onboardingHecho: Boolean
)

/** Lo que el jugador se lleva al terminar una mision. */
data class Recompensa(
    val resultado: ResultadoActividad,
    val xpGanada: Int,
    val nivelAnterior: Int,
    val nivelNuevo: Int,
    val cartaNueva: Carta?,
    val insigniasNuevas: List<Insignia>,
    val zonaCompletada: Zona?
) {
    val subioNivel: Boolean get() = nivelNuevo > nivelAnterior
}
