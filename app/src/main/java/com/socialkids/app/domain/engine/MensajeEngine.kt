package com.socialkids.app.domain.engine

import com.socialkids.app.domain.model.Estrellas
import com.socialkids.app.domain.model.ResultadoActividad
import kotlin.math.roundToInt

/** Las cuatro piezas de un mensaje-yo. */
enum class Ranura(val etiqueta: String, val plantilla: String) {
    SENTIMIENTO("Yo me siento...", "Yo me siento"),
    SITUACION("...cuando...", "cuando"),
    MOTIVO("...porque...", "porque"),
    PETICION("Me gustaria...", "Me gustaria")
}

enum class Estilo(val etiqueta: String, val descripcion: String) {
    PASIVO("Pasivo", "Te callas o te disculpas de mas, y tu necesidad se queda sin decir."),
    AGRESIVO("Agresivo", "Atacas a la persona en lugar de describir el problema."),
    ASERTIVO("Asertivo", "Dices lo que sientes y lo que necesitas sin herir a nadie.")
}

data class Ficha(
    val id: String,
    val texto: String,
    val ranura: Ranura,
    val estilo: Estilo
)

data class RetoMensaje(
    val id: String,
    val situacion: String,
    val personaje: String,
    val fichas: List<Ficha>,
    val explicacion: String
)

data class ResultadoMensaje(
    val frase: String,
    val estilo: Estilo,
    val puntaje: Int,
    val completo: Boolean,
    val observaciones: List<String>
)

/**
 * Motor del Constructor de Mensajes.
 * Arma la frase con las fichas colocadas, clasifica el estilo resultante
 * y explica por que ese estilo funciona o no.
 */
object MensajeEngine {

    fun frase(colocacion: Map<Ranura, Ficha?>): String {
        val partes = Ranura.entries.mapNotNull { r ->
            colocacion[r]?.let { "${r.plantilla} ${it.texto}" }
        }
        if (partes.isEmpty()) return ""
        val texto = partes.joinToString(" ")
        return texto.replaceFirstChar { it.uppercase() }.trimEnd('.') + "."
    }

    fun completo(colocacion: Map<Ranura, Ficha?>): Boolean =
        Ranura.entries.all { colocacion[it] != null }

    fun estilo(colocacion: Map<Ranura, Ficha?>): Estilo {
        val estilos = colocacion.values.filterNotNull().map { it.estilo }
        if (estilos.isEmpty()) return Estilo.PASIVO
        return when {
            estilos.any { it == Estilo.AGRESIVO } -> Estilo.AGRESIVO
            estilos.count { it == Estilo.PASIVO } >= 2 -> Estilo.PASIVO
            estilos.all { it == Estilo.ASERTIVO } -> Estilo.ASERTIVO
            estilos.count { it == Estilo.ASERTIVO } >= 3 -> Estilo.ASERTIVO
            else -> Estilo.PASIVO
        }
    }

    fun puntaje(colocacion: Map<Ranura, Ficha?>): Int {
        val puestas = Ranura.entries.count { colocacion[it] != null }
        val estructura = puestas / 4.0 * 55
        val asertivas = colocacion.values.filterNotNull().count { it.estilo == Estilo.ASERTIVO }
        val agresivas = colocacion.values.filterNotNull().count { it.estilo == Estilo.AGRESIVO }
        val pasivas = colocacion.values.filterNotNull().count { it.estilo == Estilo.PASIVO }
        val tono = asertivas * 12.0 - agresivas * 14.0 - pasivas * 5.0
        return (estructura + tono).roundToInt().coerceIn(0, 100)
    }

    fun construir(colocacion: Map<Ranura, Ficha?>): ResultadoMensaje {
        val obs = mutableListOf<String>()
        Ranura.entries.forEach { r ->
            val f = colocacion[r]
            when {
                f == null -> obs += "Falta la parte \"${r.etiqueta}\"."
                f.estilo == Estilo.AGRESIVO -> obs += "\"${f.texto}\" suena a ataque, no a informacion."
                f.estilo == Estilo.PASIVO -> obs += "\"${f.texto}\" se disculpa demasiado y tapa lo que necesitas."
            }
        }
        return ResultadoMensaje(
            frase = frase(colocacion),
            estilo = estilo(colocacion),
            puntaje = puntaje(colocacion),
            completo = completo(colocacion),
            observaciones = obs
        )
    }

    fun evaluar(colocacion: Map<Ranura, Ficha?>, reto: RetoMensaje): ResultadoActividad {
        val r = construir(colocacion)
        val titulo = when {
            !r.completo -> "Mensaje incompleto"
            r.estilo == Estilo.ASERTIVO && r.puntaje >= 88 -> "Mensaje asertivo perfecto"
            r.estilo == Estilo.ASERTIVO -> "Mensaje asertivo"
            r.estilo == Estilo.AGRESIVO -> "Mensaje con ataque"
            else -> "Mensaje que se esconde"
        }
        val consejo = when (r.estilo) {
            Estilo.ASERTIVO -> "Describes el hecho, no a la persona. Asi el otro puede cambiar algo sin sentirse acusado."
            Estilo.AGRESIVO -> "Cambia el \"tu eres\" por \"cuando pasa esto, yo me siento\". El problema deja de ser la persona."
            Estilo.PASIVO -> "Pedir lo que necesitas no es molestar. Prueba a decirlo en una frase corta y clara."
        }
        return ResultadoActividad(
            puntaje = r.puntaje,
            estrellas = Estrellas.de(r.puntaje),
            titulo = titulo,
            explicacion = reto.explicacion,
            consejo = consejo,
            detalles = listOf("Tu mensaje: ${r.frase.ifBlank { "(vacio)" }}", "Estilo: ${r.estilo.etiqueta}") + r.observaciones
        )
    }
}
