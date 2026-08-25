package com.socialkids.app.domain.engine

import com.socialkids.app.domain.model.Estrellas
import com.socialkids.app.domain.model.ResultadoActividad
import kotlin.math.roundToInt

/**
 * Estado vivo de una conversacion dificil. Las tres barras se mueven en cada turno
 * y determinan como termina la escena.
 */
data class EstadoConflicto(
    val calma: Int = 55,
    val confianza: Int = 50,
    val acuerdo: Int = 20,
    val turno: Int = 0,
    val nodoId: String
) {
    val tension: Int get() = 100 - calma
}

data class OpcionConflicto(
    val id: String,
    val texto: String,
    val dCalma: Int,
    val dConfianza: Int,
    val dAcuerdo: Int,
    val replica: String,
    val siguienteNodo: String?,
    val etiqueta: String
)

data class NodoConflicto(
    val id: String,
    val narrador: String,
    val dialogo: String,
    val opciones: List<OpcionConflicto>
)

data class RetoConflicto(
    val id: String,
    val personaje: String,
    val escena: String,
    val nodoInicial: String,
    val nodos: List<NodoConflicto>,
    val explicacion: String
) {
    fun nodo(id: String): NodoConflicto? = nodos.firstOrNull { it.id == id }
}

enum class Desenlace(val titulo: String, val relato: String) {
    ACUERDO("Acuerdo real", "Los dos salis con algo que os sirve. Nadie tuvo que perder para que el otro ganara."),
    TREGUA("Tregua fria", "La discusion para, pero el tema sigue ahi. Podeis retomarlo con mas calma."),
    RUPTURA("Se rompio la charla", "La conversacion se corto en caliente. Se puede reparar mas tarde, pero cuesta mas.")
}

/**
 * Motor del Simulador de Conflicto: maquina de estados pura.
 * Cada eleccion modifica variables reales y el desenlace se calcula con umbrales,
 * no con un texto prefijado.
 */
object ConflictoEngine {

    fun aplicar(estado: EstadoConflicto, opcion: OpcionConflicto): EstadoConflicto = estado.copy(
        calma = (estado.calma + opcion.dCalma).coerceIn(0, 100),
        confianza = (estado.confianza + opcion.dConfianza).coerceIn(0, 100),
        acuerdo = (estado.acuerdo + opcion.dAcuerdo).coerceIn(0, 100),
        turno = estado.turno + 1,
        nodoId = opcion.siguienteNodo ?: estado.nodoId
    )

    fun terminado(estado: EstadoConflicto, reto: RetoConflicto): Boolean =
        estado.calma <= 0 || reto.nodo(estado.nodoId)?.opciones.isNullOrEmpty()

    fun desenlace(estado: EstadoConflicto): Desenlace = when {
        estado.calma < 30 || estado.confianza < 30 -> Desenlace.RUPTURA
        estado.acuerdo >= 65 && estado.calma >= 50 -> Desenlace.ACUERDO
        else -> Desenlace.TREGUA
    }

    fun puntaje(estado: EstadoConflicto): Int =
        (estado.acuerdo * 0.45 + estado.calma * 0.3 + estado.confianza * 0.25).roundToInt().coerceIn(0, 100)

    fun evaluar(estado: EstadoConflicto, reto: RetoConflicto): ResultadoActividad {
        val d = desenlace(estado)
        val p = puntaje(estado)
        val consejo = when (d) {
            Desenlace.ACUERDO -> "Mantuviste la calma alta mientras buscabas una solucion. Esa combinacion es la que resuelve conflictos."
            Desenlace.TREGUA -> "Evitaste la pelea, pero no llegasteis a una propuesta concreta. Prueba a proponer algo que sirva a los dos."
            Desenlace.RUPTURA -> "La conversacion se rompio cuando la calma bajo demasiado. Pedir una pausa a tiempo tambien es una jugada valida."
        }
        return ResultadoActividad(
            puntaje = p,
            estrellas = Estrellas.de(p),
            titulo = d.titulo,
            explicacion = reto.explicacion,
            consejo = consejo,
            detalles = listOf(
                d.relato,
                "Calma final: ${estado.calma}",
                "Confianza final: ${estado.confianza}",
                "Acuerdo final: ${estado.acuerdo}"
            )
        )
    }
}
