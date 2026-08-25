package com.socialkids.app.domain.engine

import com.socialkids.app.domain.model.Estrellas
import com.socialkids.app.domain.model.ResultadoActividad
import kotlin.math.roundToInt

/** Tipos de respuesta posibles ante alguien que nos cuenta algo. */
enum class TipoRespuesta(val etiqueta: String, val calidad: Int, val nota: String) {
    PARAFRASEO("Reflejar", 100, "Repites con tus palabras lo que oiste: la otra persona se siente entendida."),
    PREGUNTA_ABIERTA("Preguntar", 88, "Invitas a seguir contando sin dirigir la conversacion."),
    VALIDACION("Validar", 82, "Nombras la emocion del otro y la aceptas tal como es."),
    CONSEJO_RAPIDO("Aconsejar ya", 45, "Dar soluciones antes de escuchar corta el relato."),
    DESVIO("Hablar de ti", 25, "Llevar el tema a tu propia historia deja al otro a medias."),
    JUICIO("Juzgar", 10, "Evaluar a la persona hace que deje de contarte cosas.")
}

data class OpcionEscucha(
    val id: String,
    val texto: String,
    val tipo: TipoRespuesta
)

data class DetalleRelato(
    val id: String,
    val texto: String,
    val esReal: Boolean
)

data class RetoEscucha(
    val id: String,
    val personaje: String,
    val relato: String,
    val detalles: List<DetalleRelato>,
    val opciones: List<OpcionEscucha>,
    val explicacion: String
) {
    val detallesReales: Set<String> get() = detalles.filter { it.esReal }.map { it.id }.toSet()
}

/**
 * Motor del Detective de Escucha.
 * Mide dos cosas independientes: cuanta informacion real retuvo el jugador
 * (precision y cobertura) y que tipo de respuesta eligio.
 */
object EscuchaEngine {

    fun aciertos(seleccion: Set<String>, reales: Set<String>): Int = seleccion.intersect(reales).size

    fun inventados(seleccion: Set<String>, reales: Set<String>): Int = (seleccion - reales).size

    fun olvidados(seleccion: Set<String>, reales: Set<String>): Int = (reales - seleccion).size

    /** F1 clasico entre lo seleccionado y lo realmente dicho, en escala 0..100. */
    fun puntajeMemoria(seleccion: Set<String>, reales: Set<String>): Int {
        if (reales.isEmpty()) return if (seleccion.isEmpty()) 100 else 0
        if (seleccion.isEmpty()) return 0
        val ok = aciertos(seleccion, reales).toDouble()
        if (ok == 0.0) return 0
        val precision = ok / seleccion.size
        val cobertura = ok / reales.size
        val f1 = 2 * precision * cobertura / (precision + cobertura)
        return (f1 * 100).roundToInt()
    }

    fun puntaje(seleccion: Set<String>, reto: RetoEscucha, opcion: OpcionEscucha): Int {
        val memoria = puntajeMemoria(seleccion, reto.detallesReales)
        return (memoria * 0.6 + opcion.tipo.calidad * 0.4).roundToInt().coerceIn(0, 100)
    }

    fun evaluar(seleccion: Set<String>, reto: RetoEscucha, opcion: OpcionEscucha): ResultadoActividad {
        val p = puntaje(seleccion, reto, opcion)
        val estrellas = Estrellas.de(p)
        val olvidados = olvidados(seleccion, reto.detallesReales)
        val inventados = inventados(seleccion, reto.detallesReales)
        val titulo = when {
            estrellas == 3 -> "Escucha de detective"
            opcion.tipo.calidad < 50 -> "Retuviste datos, fallo la respuesta"
            estrellas == 2 -> "Buena escucha"
            else -> "Se te escaparon cosas"
        }
        val consejo = when {
            inventados > 0 -> "Marcaste $inventados dato(s) que nadie dijo. Escuchar tambien es no rellenar huecos."
            olvidados > 0 -> "Se te fueron $olvidados dato(s). Prueba a repetir por dentro lo que oyes."
            else -> opcion.tipo.nota
        }
        return ResultadoActividad(
            puntaje = p,
            estrellas = estrellas,
            titulo = titulo,
            explicacion = reto.explicacion,
            consejo = consejo,
            detalles = listOf(
                "Datos correctos: ${aciertos(seleccion, reto.detallesReales)}/${reto.detallesReales.size}",
                "Tu respuesta: ${opcion.tipo.etiqueta}",
                opcion.tipo.nota
            )
        )
    }
}
