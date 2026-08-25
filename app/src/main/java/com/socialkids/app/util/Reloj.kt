package com.socialkids.app.util

import java.util.Calendar
import java.util.TimeZone

/**
 * Fuente de tiempo de la aplicacion. Se aisla en una interfaz para poder
 * simular dias en los tests sin depender del reloj real del dispositivo.
 * Se evita java.time porque minSdk es 24.
 */
interface Reloj {
    fun ahora(): Long
    fun hoyEpochDay(): Long
}

object RelojSistema : Reloj {
    override fun ahora(): Long = System.currentTimeMillis()

    override fun hoyEpochDay(): Long {
        val ms = ahora()
        val offset = TimeZone.getDefault().getOffset(ms)
        return Math.floorDiv(ms + offset, 86_400_000L)
    }
}

/** Reloj controlable, usado en las pruebas unitarias. */
class RelojFijo(var instante: Long = 0L, var dia: Long = 0L) : Reloj {
    override fun ahora(): Long = instante
    override fun hoyEpochDay(): Long = dia
    fun avanzarDias(n: Int) {
        dia += n
        instante += n * 86_400_000L
    }
}

/** Formatea un dia epoch como texto corto en espaniol, sin depender de java.time. */
object FechaCorta {
    private val meses = listOf(
        "ene", "feb", "mar", "abr", "may", "jun",
        "jul", "ago", "sep", "oct", "nov", "dic"
    )
    private val diasSemana = listOf("L", "M", "X", "J", "V", "S", "D")

    fun texto(diaEpoch: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = diaEpoch * 86_400_000L
        cal.timeZone = TimeZone.getTimeZone("UTC")
        return "${cal.get(Calendar.DAY_OF_MONTH)} ${meses[cal.get(Calendar.MONTH)]}"
    }

    fun inicialDiaSemana(diaEpoch: Long): String {
        // 1970-01-01 fue jueves -> indice 3 en la lista que empieza en lunes.
        val indice = Math.floorMod(diaEpoch + 3, 7L).toInt()
        return diasSemana[indice]
    }
}
