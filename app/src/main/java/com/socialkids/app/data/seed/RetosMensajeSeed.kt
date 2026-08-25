package com.socialkids.app.data.seed

import com.socialkids.app.domain.engine.Estilo
import com.socialkids.app.domain.engine.Ficha
import com.socialkids.app.domain.engine.Ranura
import com.socialkids.app.domain.engine.RetoMensaje

/**
 * Contenido del Constructor de Mensajes.
 * Para cada hueco del mensaje-yo hay tres fichas: una asertiva, una agresiva y una pasiva.
 * El motor clasifica la frase resultante segun las fichas que el jugador arrastro.
 */
object RetosMensaje {

    private fun trio(
        prefijo: String,
        ranura: Ranura,
        asertiva: String,
        agresiva: String,
        pasiva: String
    ): List<Ficha> = listOf(
        Ficha("${prefijo}_as", asertiva, ranura, Estilo.ASERTIVO),
        Ficha("${prefijo}_ag", agresiva, ranura, Estilo.AGRESIVO),
        Ficha("${prefijo}_pa", pasiva, ranura, Estilo.PASIVO)
    )

    private val retos: Map<String, RetoMensaje> = mapOf(
        "m_plaza_1" to RetoMensaje(
            id = "ms_plaza_1",
            personaje = "Tu grupo del recreo",
            situacion = "Cada recreo eligen el mismo juego y nunca te preguntan. Hoy has decidido decirlo.",
            fichas = trio(
                "s1", Ranura.SENTIMIENTO,
                "dejada de lado", "harta de vosotros", "un poco tonta por decir esto"
            ) + trio(
                "s2", Ranura.SITUACION,
                "eligiais el juego sin preguntarme", "hacéis siempre lo que os da la gana", "no pasa nada, de verdad"
            ) + trio(
                "s3", Ranura.MOTIVO,
                "a mi tambien me apetece proponer algo", "sois unos egoistas", "seguro que soy yo que me rallo"
            ) + trio(
                "s4", Ranura.PETICION,
                "que un dia elijamos por turnos", "que dejeis de mandar", "bueno, da igual, olvidadlo"
            ),
            explicacion = "El mensaje-yo tiene cuatro partes: como te sientes, que ocurrio, por que te importa y que pides. Si falta la peticion, el otro sabe que estas mal pero no sabe que hacer."
        ),
        "m_plaza_2" to RetoMensaje(
            id = "ms_plaza_2",
            personaje = "Dani",
            situacion = "Dani te ha puesto un mote que a ti no te hace gracia. Los demas ya lo repiten.",
            fichas = trio(
                "s1", Ranura.SENTIMIENTO,
                "incomodo", "asqueado contigo", "raro por quejarme"
            ) + trio(
                "s2", Ranura.SITUACION,
                "me llamas asi delante de la clase", "te crees muy gracioso", "me llamas asi, aunque no es grave"
            ) + trio(
                "s3", Ranura.MOTIVO,
                "ese mote no es mi nombre", "eres un idiota", "supongo que soy demasiado sensible"
            ) + trio(
                "s4", Ranura.PETICION,
                "que me llames por mi nombre", "que cierres la boca de una vez", "que hagas lo que quieras"
            ),
            explicacion = "Poner un limite funciona mejor cuando describes el hecho concreto y pides algo posible. Llamar idiota al otro le da una excusa para no cambiar nada."
        ),
        "m_plaza_3" to RetoMensaje(
            id = "ms_plaza_3",
            personaje = "Alex",
            situacion = "Alex te pide copiar los deberes otra vez. Es tu amigo y no quieres hacerlo.",
            fichas = trio(
                "s1", Ranura.SENTIMIENTO,
                "en un apuro", "usado por ti", "fatal por decirte que no"
            ) + trio(
                "s2", Ranura.SITUACION,
                "me pides copiar los deberes", "vas de listo y no haces nada", "me pides ayuda, que no es nada malo"
            ) + trio(
                "s3", Ranura.MOTIVO,
                "si copiamos los dos nos la jugamos", "eres un vago", "no se, quiza deberia dejartelos"
            ) + trio(
                "s4", Ranura.PETICION,
                "que los hagamos juntos en el recreo", "que te busques la vida", "bueno, esta vez vale"
            ),
            explicacion = "Un no asertivo casi siempre incluye una alternativa. Decir no puedo dejartelos, pero los hacemos juntos mantiene el limite y la amistad al mismo tiempo."
        ),
        "m_taller_2" to RetoMensaje(
            id = "ms_taller_2",
            personaje = "Tu hermana",
            situacion = "Le gritaste a tu hermana delante de sus amigas. Ya pasaron dos horas y quieres arreglarlo.",
            fichas = trio(
                "s1", Ranura.SENTIMIENTO,
                "mal por como te hable", "todavia enfadado contigo", "un poco culpable, pero tu empezaste"
            ) + trio(
                "s2", Ranura.SITUACION,
                "te grite delante de tus amigas", "me sacaste de quicio otra vez", "discutimos, como siempre"
            ) + trio(
                "s3", Ranura.MOTIVO,
                "gritarte no tiene nada que ver con lo que discutiamos", "tu tambien me gritas", "seguro que ya ni te acuerdas"
            ) + trio(
                "s4", Ranura.PETICION,
                "pedirte perdon y hablarlo cuando estemos tranquilos", "que la proxima vez no me provoques", "que hagas como si no hubiera pasado"
            ),
            explicacion = "Reparar es separar dos cosas: el motivo de la discusion y la forma en que se llevo. Se puede pedir perdon por gritar aunque el motivo siga sin resolverse."
        ),
        "m_mirador_3" to RetoMensaje(
            id = "ms_mirador_3",
            personaje = "Noa",
            situacion = "Contaste a otras personas algo que Noa te habia pedido guardar. Ella lo sabe.",
            fichas = trio(
                "s1", Ranura.SENTIMIENTO,
                "avergonzado por lo que hice", "atacado por como me lo dijiste", "regular, aunque no fue para tanto"
            ) + trio(
                "s2", Ranura.SITUACION,
                "conte algo que me pediste guardar", "se me escapo porque me presionaron", "se entero todo el mundo, no se como"
            ) + trio(
                "s3", Ranura.MOTIVO,
                "rompi algo que confiaste en mi", "tampoco era un secreto tan gordo", "supongo que ya da igual"
            ) + trio(
                "s4", Ranura.PETICION,
                "arreglarlo y no volver a contar nada tuyo", "que no te enfades mas", "que me perdones ya"
            ),
            explicacion = "Un perdon completo reconoce el hecho, nombra el danio y ofrece un cambio. Si aparece la palabra pero, el perdon se convierte en excusa."
        )
    )

    fun reto(misionId: String): RetoMensaje = retos[misionId] ?: retos.values.first()

    fun fichasDe(reto: RetoMensaje, ranura: Ranura): List<Ficha> =
        reto.fichas.filter { it.ranura == ranura }.sortedBy { (it.id + reto.id).hashCode() }
}
