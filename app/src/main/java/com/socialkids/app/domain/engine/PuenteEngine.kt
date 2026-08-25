package com.socialkids.app.domain.engine

import com.socialkids.app.domain.model.Estrellas
import com.socialkids.app.domain.model.ResultadoActividad
import kotlin.math.roundToInt

/** Los tres tablones del puente de la empatia. */
enum class Tablon(val etiqueta: String, val pregunta: String) {
    SIENTE("Siente", "Que emocion esta viviendo?"),
    PIENSA("Piensa", "Que se esta diciendo por dentro?"),
    NECESITA("Necesita", "Que le haria falta ahora?")
}

data class PiezaPuente(
    val id: String,
    val texto: String,
    val tablon: Tablon?   // null = pieza distractora, no encaja en ningun tablon
)

data class RetoPuente(
    val id: String,
    val personaje: String,
    val escena: String,
    val piezas: List<PiezaPuente>,
    val explicacion: String
)

/**
 * Motor del Puente de la Empatia.
 * El jugador arrastra una pieza a cada tablon. El puente solo se sostiene
 * si las tres piezas encajan; cada tablon aporta un tercio del puntaje.
 */
object PuenteEngine {

    fun esCorrecta(pieza: PiezaPuente?, tablon: Tablon): Boolean = pieza?.tablon == tablon

    fun tablonesCorrectos(colocacion: Map<Tablon, PiezaPuente?>): Int =
        Tablon.entries.count { esCorrecta(colocacion[it], it) }

    fun completo(colocacion: Map<Tablon, PiezaPuente?>): Boolean =
        Tablon.entries.all { colocacion[it] != null }

    fun puntaje(colocacion: Map<Tablon, PiezaPuente?>): Int {
        val correctos = tablonesCorrectos(colocacion)
        val distractoras = Tablon.entries.count { colocacion[it]?.tablon == null && colocacion[it] != null }
        val base = correctos / 3.0 * 100
        return (base - distractoras * 6).roundToInt().coerceIn(0, 100)
    }

    /** Estabilidad visual del puente: 0f a 1f. Se usa para animar la pasarela. */
    fun estabilidad(colocacion: Map<Tablon, PiezaPuente?>): Float =
        tablonesCorrectos(colocacion) / 3f

    fun evaluar(colocacion: Map<Tablon, PiezaPuente?>, reto: RetoPuente): ResultadoActividad {
        val p = puntaje(colocacion)
        val correctos = tablonesCorrectos(colocacion)
        val fallidos = Tablon.entries.filter { !esCorrecta(colocacion[it], it) }
        val titulo = when (correctos) {
            3 -> "Puente firme"
            2 -> "El puente aguanta"
            1 -> "El puente cruje"
            else -> "El puente no se sostiene"
        }
        val consejo = if (fallidos.isEmpty()) {
            "Has separado lo que ${reto.personaje} siente de lo que piensa y de lo que necesita. Eso es empatia fina."
        } else {
            "Revisa el tablon ${fallidos.first().etiqueta}: ${fallidos.first().pregunta}"
        }
        return ResultadoActividad(
            puntaje = p,
            estrellas = Estrellas.de(p),
            titulo = titulo,
            explicacion = reto.explicacion,
            consejo = consejo,
            detalles = Tablon.entries.map { t ->
                "${t.etiqueta}: " + (colocacion[t]?.texto ?: "vacio") + if (esCorrecta(colocacion[t], t)) " (encaja)" else " (no encaja)"
            }
        )
    }
}
