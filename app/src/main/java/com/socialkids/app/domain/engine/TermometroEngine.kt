package com.socialkids.app.domain.engine

import com.socialkids.app.domain.model.Estrellas
import com.socialkids.app.domain.model.ResultadoActividad
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Estrategia de regulacion. Cada una funciona bien en una franja de intensidad:
 * respirar sirve cuando estas muy encendido, hablarlo sirve cuando ya bajaste.
 */
data class Estrategia(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val minIntensidad: Int,
    val maxIntensidad: Int
) {
    fun sirvePara(intensidad: Int): Boolean = intensidad in minIntensidad..maxIntensidad
}

data class RetoTermometro(
    val id: String,
    val situacion: String,
    val emocion: String,
    val intensidadEsperada: Int,
    val margen: Int,
    val estrategias: List<Estrategia>,
    val explicacion: String
)

/**
 * Motor del Termometro Emocional.
 * Evalua dos decisiones: medir la intensidad de forma razonable
 * y elegir una estrategia proporcional a esa intensidad.
 */
object TermometroEngine {

    fun puntajeIntensidad(elegida: Int, esperada: Int, margen: Int): Int {
        val d = abs(elegida - esperada)
        if (d <= margen) return 100
        val exceso = (d - margen).toDouble()
        return ((1.0 - exceso / 5.0).coerceIn(0.0, 1.0) * 100).roundToInt()
    }

    fun estrategiaAdecuada(estrategia: Estrategia, intensidad: Int): Boolean = estrategia.sirvePara(intensidad)

    fun puntaje(intensidad: Int, estrategia: Estrategia, reto: RetoTermometro): Int {
        val pi = puntajeIntensidad(intensidad, reto.intensidadEsperada, reto.margen)
        // La estrategia se juzga contra la intensidad REAL de la situacion,
        // no contra la que el jugador creyo ver.
        val pe = if (estrategia.sirvePara(reto.intensidadEsperada)) 100 else 40
        return (pi * 0.5 + pe * 0.5).roundToInt().coerceIn(0, 100)
    }

    fun evaluar(intensidad: Int, estrategia: Estrategia, reto: RetoTermometro): ResultadoActividad {
        val p = puntaje(intensidad, estrategia, reto)
        val ok = estrategia.sirvePara(reto.intensidadEsperada)
        val dif = intensidad - reto.intensidadEsperada
        val titulo = when {
            p >= 88 -> "Medida justa"
            !ok -> "Estrategia desajustada"
            dif > 0 -> "Lo mediste mas alto de lo que era"
            else -> "Lo mediste mas bajo de lo que era"
        }
        val consejo = if (ok) {
            "${estrategia.nombre} encaja con una intensidad de ${reto.intensidadEsperada}/10."
        } else {
            "Con intensidad ${reto.intensidadEsperada}/10, ${estrategia.nombre.lowercase()} se queda corto o se pasa. Busca una estrategia de esa franja."
        }
        return ResultadoActividad(
            puntaje = p,
            estrellas = Estrellas.de(p),
            titulo = titulo,
            explicacion = reto.explicacion,
            consejo = consejo,
            detalles = listOf(
                "Tu medida: $intensidad/10",
                "Medida util: ${reto.intensidadEsperada}/10",
                "Estrategia: ${estrategia.nombre}"
            )
        )
    }
}
