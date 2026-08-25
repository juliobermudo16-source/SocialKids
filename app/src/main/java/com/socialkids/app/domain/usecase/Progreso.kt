package com.socialkids.app.domain.usecase

import com.socialkids.app.domain.model.EstadoMision
import com.socialkids.app.domain.model.Mision
import com.socialkids.app.domain.model.Zona

/**
 * Reglas de nivel y experiencia. Curva suave: subir de nivel cuesta un poco mas cada vez,
 * pero nunca tanto como para bloquear a un jugador de 8 anios.
 */
object ProgresoCalculadora {

    const val NIVEL_MAXIMO = 12

    /** XP necesaria para pasar del nivel indicado al siguiente. */
    fun xpParaSubir(nivel: Int): Int {
        require(nivel >= 1) { "El nivel minimo es 1" }
        return 100 + (nivel - 1) * 50
    }

    /** XP total acumulada necesaria para alcanzar un nivel dado. */
    fun xpAcumuladaParaNivel(nivel: Int): Int {
        require(nivel >= 1) { "El nivel minimo es 1" }
        var total = 0
        for (n in 1 until nivel) total += xpParaSubir(n)
        return total
    }

    fun nivelDeXp(xp: Int): Int {
        val puntos = xp.coerceAtLeast(0)
        var nivel = 1
        while (nivel < NIVEL_MAXIMO && puntos >= xpAcumuladaParaNivel(nivel + 1)) nivel++
        return nivel
    }

    /** Progreso dentro del nivel actual, de 0f a 1f. */
    fun progresoEnNivel(xp: Int): Float {
        val nivel = nivelDeXp(xp)
        if (nivel >= NIVEL_MAXIMO) return 1f
        val base = xpAcumuladaParaNivel(nivel)
        val necesaria = xpParaSubir(nivel)
        return ((xp - base).toFloat() / necesaria).coerceIn(0f, 1f)
    }

    fun xpRestanteParaSiguienteNivel(xp: Int): Int {
        val nivel = nivelDeXp(xp)
        if (nivel >= NIVEL_MAXIMO) return 0
        return (xpAcumuladaParaNivel(nivel + 1) - xp).coerceAtLeast(0)
    }

    /** XP que otorga una mision segun su valor base y las estrellas obtenidas. */
    fun xpGanada(mision: Mision, estrellas: Int, yaCompletada: Boolean): Int {
        val bruto = when (estrellas.coerceIn(0, 3)) {
            3 -> mision.xp
            2 -> (mision.xp * 0.75).toInt()
            1 -> (mision.xp * 0.5).toInt()
            else -> 5
        }
        // Repetir una mision sigue dando algo, pero menos: no se granjea XP repitiendo.
        return if (yaCompletada) (bruto * 0.35).toInt().coerceAtLeast(3) else bruto
    }
}

/** Estado guardado de una mision concreta. */
data class ProgresoMision(
    val misionId: String,
    val mejoresEstrellas: Int,
    val intentos: Int,
    val completada: Boolean
)

/**
 * Decide que esta abierto y que no. Regla: una zona se abre por XP,
 * y dentro de la zona las misiones se abren en cadena.
 */
object DesbloqueoEvaluador {

    fun zonaDesbloqueada(zona: Zona, xp: Int): Boolean = xp >= zona.xpNecesaria

    fun estadoMision(
        mision: Mision,
        zonaDesbloqueada: Boolean,
        progreso: Map<String, ProgresoMision>,
        misionesDeLaZona: List<Mision>
    ): EstadoMision {
        val propio = progreso[mision.id]
        if (propio != null && propio.completada) {
            return if (propio.mejoresEstrellas >= 3) EstadoMision.DOMINADA else EstadoMision.COMPLETADA
        }
        if (!zonaDesbloqueada) return EstadoMision.BLOQUEADA
        val anterior = misionesDeLaZona.filter { it.orden < mision.orden }.maxByOrNull { it.orden }
        val abierta = anterior == null || progreso[anterior.id]?.completada == true
        if (!abierta) return EstadoMision.BLOQUEADA
        return if (propio != null && propio.intentos > 0) EstadoMision.INICIADA else EstadoMision.DISPONIBLE
    }

    fun zonaCompletada(misionesDeLaZona: List<Mision>, progreso: Map<String, ProgresoMision>): Boolean =
        misionesDeLaZona.isNotEmpty() && misionesDeLaZona.all { progreso[it.id]?.completada == true }

    fun porcentajeZona(misionesDeLaZona: List<Mision>, progreso: Map<String, ProgresoMision>): Float {
        if (misionesDeLaZona.isEmpty()) return 0f
        val hechas = misionesDeLaZona.count { progreso[it.id]?.completada == true }
        return hechas.toFloat() / misionesDeLaZona.size
    }

    /** Siguiente mision recomendada: la primera disponible siguiendo el orden del mundo. */
    fun siguienteMision(
        zonas: List<Zona>,
        misiones: List<Mision>,
        progreso: Map<String, ProgresoMision>,
        xp: Int
    ): Mision? {
        zonas.sortedBy { it.orden }.forEach { zona ->
            if (!zonaDesbloqueada(zona, xp)) return@forEach
            val deZona = misiones.filter { it.zonaId == zona.id }.sortedBy { it.orden }
            val candidata = deZona.firstOrNull { m ->
                val e = estadoMision(m, true, progreso, deZona)
                e == EstadoMision.DISPONIBLE || e == EstadoMision.INICIADA
            }
            if (candidata != null) return candidata
        }
        // Todo completado: sugerir repasar la mision con menos estrellas.
        return misiones.minByOrNull { progreso[it.id]?.mejoresEstrellas ?: 0 }
    }

    /** Misiones candidatas para el modo Repaso: completadas con menos de 3 estrellas. */
    fun misionesDeRepaso(misiones: List<Mision>, progreso: Map<String, ProgresoMision>): List<Mision> =
        misiones.filter { m ->
            val p = progreso[m.id]
            p != null && p.completada && p.mejoresEstrellas < 3
        }.sortedBy { progreso[it.id]?.mejoresEstrellas ?: 0 }
}
