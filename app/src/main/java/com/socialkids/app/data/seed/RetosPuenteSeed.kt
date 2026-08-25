package com.socialkids.app.data.seed

import com.socialkids.app.domain.engine.PiezaPuente
import com.socialkids.app.domain.engine.RetoPuente
import com.socialkids.app.domain.engine.Tablon

/**
 * Contenido del Puente de la Empatia.
 * En cada reto hay una pieza correcta por tablon y tres piezas trampa:
 * juicios, soluciones rapidas o suposiciones. Distinguirlas es el aprendizaje.
 */
object RetosPuente {

    private val retos: Map<String, RetoPuente> = mapOf(
        "m_bosque_3" to RetoPuente(
            id = "p_bosque_3",
            personaje = "Mateo",
            escena = "Mateo dice riendose: da igual, total nunca me eligen para nada. Y cambia de tema muy rapido.",
            piezas = listOf(
                PiezaPuente("a1", "Verguenza, aunque la tape con una risa", Tablon.SIENTE),
                PiezaPuente("a2", "Si digo que me importa, van a reirse mas", Tablon.PIENSA),
                PiezaPuente("a3", "Que alguien le diga que si cuenta para el grupo", Tablon.NECESITA),
                PiezaPuente("a4", "Es un exagerado", null),
                PiezaPuente("a5", "Que se apunte a otro grupo y ya", null),
                PiezaPuente("a6", "Seguro que lo dice para llamar la atencion", null)
            ),
            explicacion = "Cuando alguien se rie de si mismo muy rapido suele estar tapando algo. Separar lo que siente de lo que piensa te deja ver la necesidad que hay debajo."
        ),
        "m_puente_1" to RetoPuente(
            id = "p_puente_1",
            personaje = "Teo",
            escena = "En gimnasia hacen equipos eligiendo por turnos. Teo se queda el ultimo otra vez y se sienta en el banco mirando el suelo.",
            piezas = listOf(
                PiezaPuente("a1", "Tristeza mezclada con un poco de rabia", Tablon.SIENTE),
                PiezaPuente("a2", "Otra vez soy el que sobra", Tablon.PIENSA),
                PiezaPuente("a3", "Que alguien le elija sin que tenga que pedirlo", Tablon.NECESITA),
                PiezaPuente("a4", "Deberia jugar mejor al futbol", null),
                PiezaPuente("a5", "Que el profe cambie el sistema de equipos", null),
                PiezaPuente("a6", "Le da igual, siempre esta solo", null)
            ),
            explicacion = "Lo que necesita una persona casi nunca es un consejo. Muchas veces es algo pequenio y concreto: que alguien le mire y le llame por su nombre."
        ),
        "m_puente_2" to RetoPuente(
            id = "p_puente_2",
            personaje = "Lena",
            escena = "Lena llego hace tres dias de otra ciudad. En el recreo se sienta en la escalera con el bocadillo y mira el movil sin desbloquearlo.",
            piezas = listOf(
                PiezaPuente("a1", "Miedo a acercarse y que la rechacen", Tablon.SIENTE),
                PiezaPuente("a2", "Si me acerco, van a pensar que soy pesada", Tablon.PIENSA),
                PiezaPuente("a3", "Una invitacion facil de aceptar, sin ser el centro", Tablon.NECESITA),
                PiezaPuente("a4", "Es muy timida, no tiene solucion", null),
                PiezaPuente("a5", "Que hable ella, que para eso es la nueva", null),
                PiezaPuente("a6", "No quiere amigos, se ve", null)
            ),
            explicacion = "Ser nuevo cuesta mas de lo que parece desde fuera. Una invitacion buena es la que se puede aceptar sin quedar en evidencia: sientate aqui si quieres funciona mejor que cuentanos algo de ti."
        ),
        "m_puente_4" to RetoPuente(
            id = "p_puente_4",
            personaje = "Sara",
            escena = "Sara y Nuria discutieron. Nuria dice que Sara la dejo sola en el proyecto. Sara dice que Nuria decidio todo sin preguntarle. Estas en el lado de Nuria; construye el puente hacia Sara.",
            piezas = listOf(
                PiezaPuente("a1", "Frustracion por no haber pintado nada en el proyecto", Tablon.SIENTE),
                PiezaPuente("a2", "Si ya lo decide ella todo, para que voy a proponer", Tablon.PIENSA),
                PiezaPuente("a3", "Que le pregunten su opinion antes de cerrar las cosas", Tablon.NECESITA),
                PiezaPuente("a4", "Es una vaga, no hizo nada", null),
                PiezaPuente("a5", "Que se cambien de grupo las dos", null),
                PiezaPuente("a6", "Nuria tiene razon y punto", null)
            ),
            explicacion = "En una pelea las dos versiones suelen ser verdad a la vez. Cruzar el puente no significa darle la razon al otro: significa entender desde donde lo esta viendo."
        ),
        "m_mirador_1" to RetoPuente(
            id = "p_mirador_1",
            personaje = "Hugo",
            escena = "Hugo siempre dice que no cuando le invitan al parque. El grupo ya casi no le llama. Hoy le has visto mirando desde la ventana cuando saliais.",
            piezas = listOf(
                PiezaPuente("a1", "Ganas de ir y miedo de no encajar al mismo tiempo", Tablon.SIENTE),
                PiezaPuente("a2", "Si voy y no se de que hablan, hare el ridiculo", Tablon.PIENSA),
                PiezaPuente("a3", "Que le insistan una vez mas sin presionarle", Tablon.NECESITA),
                PiezaPuente("a4", "Es un raro que prefiere estar solo", null),
                PiezaPuente("a5", "Que se busque otros amigos", null),
                PiezaPuente("a6", "Ya le hemos invitado bastante", null)
            ),
            explicacion = "Un no repetido no siempre significa no quiero. A veces significa no se como. Insistir una vez mas, sin agobiar, es lo que rompe ese bucle."
        )
    )

    fun reto(misionId: String): RetoPuente = retos[misionId] ?: retos.values.first()

    /** Piezas mezcladas de forma estable para que la posicion no delate la respuesta. */
    fun piezasBarajadas(reto: RetoPuente): List<PiezaPuente> =
        reto.piezas.sortedBy { (it.id + reto.id).hashCode() }
}
