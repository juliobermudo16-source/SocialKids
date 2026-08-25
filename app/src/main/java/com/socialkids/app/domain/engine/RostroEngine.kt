package com.socialkids.app.domain.engine

import com.socialkids.app.domain.model.Estrellas
import com.socialkids.app.domain.model.ResultadoActividad
import kotlin.math.abs
import kotlin.math.roundToInt

/** Rasgos opcionales que el jugador puede activar sobre el rostro. */
enum class RasgoExtra { LAGRIMA, RUBOR, SUDOR, BRILLO }

/**
 * Configuracion de un rostro. Cada eje va de 0 a 100 y tiene un significado grafico:
 *  - cejas: 0 = muy caidas hacia fuera, 50 = neutras, 100 = muy juntas y bajas
 *  - ojos: 0 = casi cerrados, 100 = muy abiertos
 *  - boca: 0 = comisuras muy hacia abajo, 50 = recta, 100 = sonrisa amplia
 *  - energia: 0 = cuerpo hundido, 100 = cuerpo muy activado
 */
data class RostroConfig(
    val cejas: Int = 50,
    val ojos: Int = 50,
    val boca: Int = 50,
    val energia: Int = 50,
    val extras: Set<RasgoExtra> = emptySet()
) {
    fun conEje(eje: EjeRostro, valor: Int): RostroConfig {
        val v = valor.coerceIn(0, 100)
        return when (eje) {
            EjeRostro.CEJAS -> copy(cejas = v)
            EjeRostro.OJOS -> copy(ojos = v)
            EjeRostro.BOCA -> copy(boca = v)
            EjeRostro.ENERGIA -> copy(energia = v)
        }
    }

    fun alternar(extra: RasgoExtra): RostroConfig =
        copy(extras = if (extras.contains(extra)) extras - extra else extras + extra)
}

enum class EjeRostro(val etiqueta: String, val izquierda: String, val derecha: String) {
    CEJAS("Cejas", "caidas", "juntas"),
    OJOS("Ojos", "cerrados", "abiertos"),
    BOCA("Boca", "hacia abajo", "sonrisa"),
    ENERGIA("Energia", "apagada", "encendida")
}

data class RostroObjetivo(
    val emocion: String,
    val cejas: Int,
    val ojos: Int,
    val boca: Int,
    val energia: Int,
    val extras: Set<RasgoExtra>,
    val pista: String,
    val explicacion: String
)

/**
 * Motor del Estudio de Rostros.
 * Compara la cara construida con la cara objetivo midiendo distancia por eje.
 * No conoce nada de Android ni de Compose: es logica pura y testeable.
 */
object RostroEngine {

    private const val TOLERANCIA = 60.0 // distancia por eje a partir de la cual el eje puntua 0

    fun distanciaEje(valor: Int, objetivo: Int): Int = abs(valor - objetivo)

    fun puntajeEje(valor: Int, objetivo: Int): Int {
        val d = distanciaEje(valor, objetivo).toDouble()
        val p = (1.0 - (d / TOLERANCIA)).coerceIn(0.0, 1.0)
        return (p * 100).roundToInt()
    }

    /** Nombre humano del eje peor resuelto, para dar una pista util. */
    fun ejeMasLejano(config: RostroConfig, objetivo: RostroObjetivo): EjeRostro {
        val pares = listOf(
            EjeRostro.CEJAS to distanciaEje(config.cejas, objetivo.cejas),
            EjeRostro.OJOS to distanciaEje(config.ojos, objetivo.ojos),
            EjeRostro.BOCA to distanciaEje(config.boca, objetivo.boca),
            EjeRostro.ENERGIA to distanciaEje(config.energia, objetivo.energia)
        )
        return pares.maxByOrNull { it.second }!!.first
    }

    /** Parte del puntaje que depende de los rasgos extra, de 0 a 100. */
    fun puntajeExtras(config: RostroConfig, objetivo: RostroObjetivo): Int {
        val acertados = config.extras.intersect(objetivo.extras).size
        val sobrantes = (config.extras - objetivo.extras).size
        if (objetivo.extras.isEmpty()) {
            return (100 - sobrantes * 25).coerceIn(0, 100)
        }
        val bruto = (acertados - sobrantes * 0.5) / objetivo.extras.size
        return (bruto * 100).roundToInt().coerceIn(0, 100)
    }

    fun puntaje(config: RostroConfig, objetivo: RostroObjetivo): Int {
        // La boca y las cejas son las que mas informacion emocional aportan.
        val ejes = (
            puntajeEje(config.boca, objetivo.boca) * 0.32 +
                puntajeEje(config.cejas, objetivo.cejas) * 0.28 +
                puntajeEje(config.ojos, objetivo.ojos) * 0.22 +
                puntajeEje(config.energia, objetivo.energia) * 0.18
            )
        // Los rasgos extra afinan la lectura, pero nunca sustituyen a la geometria.
        return (ejes * 0.85 + puntajeExtras(config, objetivo) * 0.15)
            .roundToInt()
            .coerceIn(0, 100)
    }

    fun evaluar(config: RostroConfig, objetivo: RostroObjetivo): ResultadoActividad {
        val p = puntaje(config, objetivo)
        val estrellas = Estrellas.de(p)
        val peor = ejeMasLejano(config, objetivo)
        val titulo = when (estrellas) {
            3 -> "Rostro clavado"
            2 -> "Muy reconocible"
            1 -> "Se acerca"
            else -> "Todavia no se lee"
        }
        val consejo = if (estrellas >= 3) {
            "Fijate en tu propia cara cuando sientas ${objetivo.emocion.lowercase()}: se parece bastante."
        } else {
            "Prueba a mover ${peor.etiqueta.lowercase()}: ahi es donde tu rostro se aleja mas."
        }
        return ResultadoActividad(
            puntaje = p,
            estrellas = estrellas,
            titulo = titulo,
            explicacion = objetivo.explicacion,
            consejo = consejo,
            detalles = listOf(
                "Cejas ${puntajeEje(config.cejas, objetivo.cejas)}%",
                "Ojos ${puntajeEje(config.ojos, objetivo.ojos)}%",
                "Boca ${puntajeEje(config.boca, objetivo.boca)}%",
                "Energia ${puntajeEje(config.energia, objetivo.energia)}%"
            )
        )
    }
}
