package com.socialkids.app.domain.usecase

import com.socialkids.app.domain.model.Figura
import com.socialkids.app.domain.model.Insignia

/**
 * Fotografia del jugador en un instante. Todos los campos se calculan
 * desde la base de datos; el evaluador de insignias solo lee esta foto.
 */
data class EstadisticasJugador(
    val xp: Int = 0,
    val nivel: Int = 1,
    val misionesCompletadas: Int = 0,
    val misionesDominadas: Int = 0,
    val zonasCompletadas: Int = 0,
    val cartasDesbloqueadas: Int = 0,
    val registrosAnimo: Int = 0,
    val rachaActual: Int = 0,
    val mensajesAsertivosPerfectos: Int = 0,
    val conflictosResueltosConCalma: Int = 0,
    val puentesFirmes: Int = 0,
    val rostrosClavados: Int = 0
)

data class ReglaInsignia(
    val insignia: Insignia,
    val objetivo: Int,
    val medida: (EstadisticasJugador) -> Int
) {
    fun progreso(e: EstadisticasJugador): Float = (medida(e).toFloat() / objetivo).coerceIn(0f, 1f)
    fun conseguida(e: EstadisticasJugador): Boolean = medida(e) >= objetivo
}

/**
 * Catalogo de insignias y su evaluacion. Una insignia solo se otorga
 * si la accion real ocurrio y quedo registrada.
 */
object InsigniaEvaluador {

    val reglas: List<ReglaInsignia> = listOf(
        ReglaInsignia(
            Insignia("ins_primer_paso", "Primer paso", "Completa tu primera mision en la isla.", "Entra en el Faro y termina una mision.", Figura.SEMILLA, 0),
            1
        ) { it.misionesCompletadas },
        ReglaInsignia(
            Insignia("ins_explorador", "Exploradora o explorador", "Completa 5 misiones.", "Sigue el camino del mapa.", Figura.BRUJULA, 1),
            5
        ) { it.misionesCompletadas },
        ReglaInsignia(
            Insignia("ins_lector_caras", "Lee-caras", "Consigue 3 estrellas en 3 rostros.", "Ajusta cejas y boca con cuidado.", Figura.CHISPA, 2),
            3
        ) { it.rostrosClavados },
        ReglaInsignia(
            Insignia("ins_oido_fino", "Oido fino", "Domina 4 misiones cualesquiera.", "Tres estrellas cuentan como dominio.", Figura.OLA, 3),
            4
        ) { it.misionesDominadas },
        ReglaInsignia(
            Insignia("ins_constructor", "Constructor de puentes", "Levanta 3 puentes firmes.", "Los tres tablones tienen que encajar.", Figura.PUENTE, 4),
            3
        ) { it.puentesFirmes },
        ReglaInsignia(
            Insignia("ins_voz_clara", "Voz clara", "Arma 3 mensajes asertivos perfectos.", "Estructura completa y tono asertivo.", Figura.BURBUJA, 5),
            3
        ) { it.mensajesAsertivosPerfectos },
        ReglaInsignia(
            Insignia("ins_mediador", "Mediador o mediadora", "Cierra 2 conflictos con acuerdo y calma alta.", "Baja la tension antes de proponer.", Figura.LLAVE, 6),
            2
        ) { it.conflictosResueltosConCalma },
        ReglaInsignia(
            Insignia("ins_diario", "Diario constante", "Anota tu animo 10 veces.", "Una anotacion al dia basta.", Figura.HOJA, 7),
            10
        ) { it.registrosAnimo },
        ReglaInsignia(
            Insignia("ins_racha", "Tres dias seguidos", "Vuelve a la isla 3 dias seguidos.", "La racha no se pierde por castigo, solo se reinicia.", Figura.ESTRELLA, 8),
            3
        ) { it.rachaActual },
        ReglaInsignia(
            Insignia("ins_coleccion", "Coleccionista", "Reune 12 cartas de la isla.", "Cada mision nueva trae una carta.", Figura.CORAZON, 9),
            12
        ) { it.cartasDesbloqueadas },
        ReglaInsignia(
            Insignia("ins_faro", "Guardian del faro", "Completa 3 zonas enteras.", "Termina todas las misiones de una zona.", Figura.FARO, 10),
            3
        ) { it.zonasCompletadas },
        ReglaInsignia(
            Insignia("ins_isla", "Voz de la isla", "Completa las 6 zonas.", "El mapa entero, de punta a punta.", Figura.NUBE, 11),
            6
        ) { it.zonasCompletadas }
    )

    fun todas(): List<Insignia> = reglas.map { it.insignia }

    fun conseguidas(e: EstadisticasJugador): List<Insignia> =
        reglas.filter { it.conseguida(e) }.map { it.insignia }

    /** Insignias que se acaban de ganar comparando la foto anterior con la nueva. */
    fun nuevas(antes: EstadisticasJugador, ahora: EstadisticasJugador): List<Insignia> =
        reglas.filter { !it.conseguida(antes) && it.conseguida(ahora) }.map { it.insignia }

    fun regla(id: String): ReglaInsignia? = reglas.firstOrNull { it.insignia.id == id }

    /** La insignia mas cercana a conseguirse, para mostrarla como objetivo vivo. */
    fun siguienteObjetivo(e: EstadisticasJugador): ReglaInsignia? =
        reglas.filter { !it.conseguida(e) }.maxByOrNull { it.progreso(e) }
}
