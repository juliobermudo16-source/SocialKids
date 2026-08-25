package com.socialkids.app.domain.usecase

import kotlin.math.roundToInt

/** Un registro del Diario de Animo, tal como se guarda en la base de datos. */
data class RegistroAnimo(
    val id: Long,
    val diaEpoch: Long,
    val emocion: String,
    val intensidad: Int,
    val nota: String
)

data class ResumenAnimo(
    val total: Int,
    val intensidadMedia: Double,
    val emocionFrecuente: String?,
    val conteoPorEmocion: Map<String, Int>,
    val ultimosSieteDias: List<Int>,
    val diasRegistrados: Int
)

/**
 * Calculadora de estadisticas del diario. Todo sale de los registros persistidos:
 * ningun numero de la pantalla de estadisticas esta escrito a mano.
 */
object EstadisticasCalculadora {

    fun resumen(registros: List<RegistroAnimo>, hoyEpoch: Long): ResumenAnimo {
        if (registros.isEmpty()) {
            return ResumenAnimo(0, 0.0, null, emptyMap(), List(7) { 0 }, 0)
        }
        val conteo = registros.groupingBy { it.emocion }.eachCount()
        val media = registros.sumOf { it.intensidad }.toDouble() / registros.size
        val serie = (6 downTo 0).map { atras ->
            val dia = hoyEpoch - atras
            val delDia = registros.filter { it.diaEpoch == dia }
            if (delDia.isEmpty()) 0 else (delDia.sumOf { it.intensidad }.toDouble() / delDia.size).roundToInt()
        }
        return ResumenAnimo(
            total = registros.size,
            intensidadMedia = (media * 10).roundToInt() / 10.0,
            emocionFrecuente = conteo.maxByOrNull { it.value }?.key,
            conteoPorEmocion = conteo,
            ultimosSieteDias = serie,
            diasRegistrados = registros.map { it.diaEpoch }.distinct().size
        )
    }
}

/** Racha de dias con actividad. Nunca castiga: solo informa. */
object RachaCalculadora {

    fun rachaActual(dias: Collection<Long>, hoyEpoch: Long): Int {
        if (dias.isEmpty()) return 0
        val set = dias.toSet()
        val inicio = when {
            set.contains(hoyEpoch) -> hoyEpoch
            set.contains(hoyEpoch - 1) -> hoyEpoch - 1
            else -> return 0
        }
        var racha = 0
        var dia = inicio
        while (set.contains(dia)) {
            racha++
            dia--
        }
        return racha
    }

    fun mejorRacha(dias: Collection<Long>): Int {
        if (dias.isEmpty()) return 0
        val ordenados = dias.toSortedSet().toList()
        var mejor = 1
        var actual = 1
        for (i in 1 until ordenados.size) {
            actual = if (ordenados[i] == ordenados[i - 1] + 1) actual + 1 else 1
            if (actual > mejor) mejor = actual
        }
        return mejor
    }
}
