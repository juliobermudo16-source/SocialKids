package com.socialkids.app.data.seed

import com.socialkids.app.domain.engine.DetalleRelato
import com.socialkids.app.domain.engine.Estrategia
import com.socialkids.app.domain.engine.OpcionEscucha
import com.socialkids.app.domain.engine.RasgoExtra
import com.socialkids.app.domain.engine.RetoEscucha
import com.socialkids.app.domain.engine.RetoTermometro
import com.socialkids.app.domain.engine.RostroObjetivo
import com.socialkids.app.domain.engine.TipoRespuesta

/**
 * Contenido de las mecanicas de rostro, termometro y escucha.
 * Cada reto esta atado al id de la mision que lo usa.
 */
object RetosRostro {

    private val objetivos: Map<String, RostroObjetivo> = mapOf(
        "m_faro_1" to RostroObjetivo(
            emocion = "Alegria",
            cejas = 45, ojos = 62, boca = 92, energia = 78,
            extras = setOf(RasgoExtra.RUBOR, RasgoExtra.BRILLO),
            pista = "La alegria de verdad tambien se ve en los ojos, no solo en la boca.",
            explicacion = "Una sonrisa autentica arruga un poco los ojos. Cuando solo se mueve la boca, la cara parece una foto: por eso notamos cuando alguien sonrie por compromiso."
        ),
        "m_faro_3" to RostroObjetivo(
            emocion = "Miedo",
            cejas = 68, ojos = 95, boca = 24, energia = 72,
            extras = setOf(RasgoExtra.SUDOR),
            pista = "El miedo abre mucho los ojos y tensa las cejas hacia dentro.",
            explicacion = "El miedo y la sorpresa se parecen porque los dos abren los ojos. La diferencia esta en las cejas: en la sorpresa suben y se separan, en el miedo suben y se juntan."
        ),
        "m_puente_3" to RostroObjetivo(
            emocion = "Tristeza",
            cejas = 22, ojos = 28, boca = 12, energia = 14,
            extras = setOf(RasgoExtra.LAGRIMA),
            pista = "La tristeza apaga el cuerpo entero, no solo la cara.",
            explicacion = "La tristeza baja los parpados, las comisuras y los hombros. Ese apagon del cuerpo es una senial para que los demas se acerquen: por eso ayuda pedir compania cuando aparece."
        )
    )

    fun objetivo(misionId: String): RostroObjetivo = objetivos[misionId] ?: objetivos.values.first()
}

object RetosTermometro {

    private val calmaBasicas = listOf(
        Estrategia("e_respirar", "Respirar 4-4-6", "Tomar aire 4, sostener 4, soltar 6. Baja el cuerpo rapido.", 7, 10),
        Estrategia("e_salir", "Salir un momento", "Cambiar de sitio corta el circuito del enfado.", 6, 10),
        Estrategia("e_hablar", "Hablarlo ahora", "Decir lo que te pasa con palabras claras.", 0, 5),
        Estrategia("e_ignorar", "Dejarlo pasar", "No todo merece respuesta. Sirve cuando la cosa es pequenia.", 0, 3)
    )

    private val retos: Map<String, RetoTermometro> = mapOf(
        "m_faro_2" to RetoTermometro(
            id = "t_faro_2",
            situacion = "Estas jugando y tu hermano pequenio apaga la consola sin avisar. Habias llegado al ultimo nivel.",
            emocion = "Enfado",
            intensidadEsperada = 7,
            margen = 1,
            estrategias = calmaBasicas,
            explicacion = "Un 7 de 10 es enfado alto: el cuerpo ya esta activado. Con esa intensidad hablar sale mal, porque las palabras salen disparadas. Primero baja el cuerpo, despues habla."
        ),
        "m_faro_4" to RetoTermometro(
            id = "t_faro_4",
            situacion = "Te piden recoger la mesa justo cuando ibas a salir al parque. Te fastidia, pero no es grave.",
            emocion = "Fastidio",
            intensidadEsperada = 3,
            margen = 1,
            estrategias = listOf(
                Estrategia("e_hablar2", "Decirlo con calma", "Explicar que ibas a salir y pedir cinco minutos.", 0, 5),
                Estrategia("e_respirar2", "Respiracion larga", "Ejercicio completo de calma.", 7, 10),
                Estrategia("e_ignorar2", "Dejarlo pasar", "Hacerlo rapido y seguir con tu plan.", 0, 4),
                Estrategia("e_gritar", "Protestar a gritos", "Levantar la voz para que se note.", 11, 12)
            ),
            explicacion = "No todo lo que molesta es un 8. Medir bien evita responder con un cohete a un mosquito. Con un 3, hablar tranquilo funciona perfectamente."
        ),
        "m_mirador_4" to RetoTermometro(
            id = "t_mirador_4",
            situacion = "Descubres que tu mejor amiga conto un secreto tuyo al grupo. Estabas delante cuando se rieron.",
            emocion = "Dolor y rabia",
            intensidadEsperada = 9,
            margen = 1,
            estrategias = listOf(
                Estrategia("e_pausa3", "Pedir tiempo muerto", "Decir que hablareis luego y salir de la escena.", 7, 10),
                Estrategia("e_respirar3", "Respirar y esperar a maniana", "Bajar el cuerpo antes de decidir nada.", 7, 10),
                Estrategia("e_encarar", "Encararla delante de todos", "Responder en caliente y en publico.", 11, 12),
                Estrategia("e_callar", "No decir nada nunca", "Tragar y seguir como si nada.", 11, 12)
            ),
            explicacion = "Con intensidad 9 el cuerpo manda mas que la cabeza. Lo unico que funciona a ese nivel es ganar tiempo. Hablar del tema sigue siendo necesario, pero maniana."
        )
    )

    fun reto(misionId: String): RetoTermometro = retos[misionId] ?: retos.values.first()
}

object RetosEscucha {

    private val retos: Map<String, RetoEscucha> = mapOf(
        "m_bosque_1" to RetoEscucha(
            id = "e_bosque_1",
            personaje = "Ada",
            relato = "El sabado tenia el concurso de robotica con mi grupo. Estuvimos un mes montando el brazo mecanico. El viernes por la noche mi padre se puso malo y tuvimos que ir a urgencias, asi que no pude ir. Mi grupo gano el segundo puesto y me mandaron la foto por el movil.",
            detalles = listOf(
                DetalleRelato("d1", "El concurso era de robotica", true),
                DetalleRelato("d2", "Estuvieron un mes montando el brazo", true),
                DetalleRelato("d3", "Su padre se puso malo el viernes", true),
                DetalleRelato("d4", "El grupo quedo segundo", true),
                DetalleRelato("d5", "Ada estaba enfadada con su grupo", false),
                DetalleRelato("d6", "El concurso era de matematicas", false),
                DetalleRelato("d7", "Ada llego tarde al concurso", false)
            ),
            opciones = listOf(
                OpcionEscucha("o1", "Entonces te perdiste el concurso justo cuando ya estaba todo listo.", TipoRespuesta.PARAFRASEO),
                OpcionEscucha("o2", "Y como esta tu padre ahora?", TipoRespuesta.PREGUNTA_ABIERTA),
                OpcionEscucha("o3", "A mi me paso algo parecido el anio pasado, te cuento.", TipoRespuesta.DESVIO),
                OpcionEscucha("o4", "Pues haberte organizado mejor.", TipoRespuesta.JUICIO)
            ),
            explicacion = "Escuchar bien es quedarse con los hechos que la otra persona dijo, sin anadir cosas que suponemos. Cuando repites lo que oiste con tus palabras, la otra persona sabe que estabas ahi de verdad."
        ),
        "m_bosque_2" to RetoEscucha(
            id = "e_bosque_2",
            personaje = "Bruno",
            relato = "Me han cambiado de sitio en clase y ahora estoy al lado de la ventana. Veo bien la pizarra, pero no oigo casi nada porque las obras de la calle no paran. Se lo dije al profe el martes y me dijo que lo pensaria. Llevo dos examenes con notas mas bajas que antes.",
            detalles = listOf(
                DetalleRelato("d1", "Ahora se sienta junto a la ventana", true),
                DetalleRelato("d2", "El ruido viene de unas obras", true),
                DetalleRelato("d3", "Hablo con el profe el martes", true),
                DetalleRelato("d4", "Ha bajado en dos examenes", true),
                DetalleRelato("d5", "No ve la pizarra", false),
                DetalleRelato("d6", "El profe le dijo que no", false),
                DetalleRelato("d7", "Sus companieros se rien de el", false)
            ),
            opciones = listOf(
                OpcionEscucha("o1", "O sea que ves bien pero no oyes, y eso ya te ha costado dos notas.", TipoRespuesta.PARAFRASEO),
                OpcionEscucha("o2", "Que crees que podrias decirle al profe esta vez?", TipoRespuesta.PREGUNTA_ABIERTA),
                OpcionEscucha("o3", "Ponte tapones y ya esta.", TipoRespuesta.CONSEJO_RAPIDO),
                OpcionEscucha("o4", "Seguro que es que no atiendes.", TipoRespuesta.JUICIO)
            ),
            explicacion = "El detalle importante casi nunca es el mas ruidoso. Bruno ve bien, el problema es que no oye. Si te quedas con la parte equivocada, la ayuda que ofreces no sirve."
        ),
        "m_bosque_4" to RetoEscucha(
            id = "e_bosque_4",
            personaje = "Zoe",
            relato = "Llevo tres semanas entrenando natacion todos los dias antes de clase. Me levanto a las seis y llego a casa a las ocho de la tarde. Este finde es la competicion y mi entrenadora me ha dicho que me ve cansada, que a lo mejor no compito. No se lo he contado a nadie en casa porque no quiero que me digan que lo deje.",
            detalles = listOf(
                DetalleRelato("d1", "Entrena todos los dias antes de clase", true),
                DetalleRelato("d2", "La entrenadora la ve cansada", true),
                DetalleRelato("d3", "La competicion es este fin de semana", true),
                DetalleRelato("d4", "No lo ha contado en casa", true),
                DetalleRelato("d5", "Ya la han descartado de la competicion", false),
                DetalleRelato("d6", "Sus padres la obligan a nadar", false),
                DetalleRelato("d7", "Quiere dejar la natacion", false)
            ),
            opciones = listOf(
                OpcionEscucha("o1", "Suena a que estas agotada y ademas lo llevas sola, sin contarlo en casa.", TipoRespuesta.VALIDACION),
                OpcionEscucha("o2", "Que es lo que mas te preocupa: la competicion o lo de casa?", TipoRespuesta.PREGUNTA_ABIERTA),
                OpcionEscucha("o3", "Dejalo, total la natacion no da para nada.", TipoRespuesta.CONSEJO_RAPIDO),
                OpcionEscucha("o4", "Yo entrenaba mucho mas y no me quejaba.", TipoRespuesta.DESVIO)
            ),
            explicacion = "Cuando alguien cuenta algo largo, lo ultimo que dice suele ser lo que mas le pesa. Zoe termina hablando de que no se lo ha contado a nadie: ahi esta el nudo."
        ),
        "m_plaza_4" to RetoEscucha(
            id = "e_plaza_4",
            personaje = "Iker",
            relato = "Ayer te pedi el cuaderno de mates y me dijiste que si, pero luego te fuiste con Marta sin darmelo. Me quede sin poder estudiar el tema tres y hoy he suspendido el control. No te lo digo para que te sientas mal, te lo digo porque me fastidio y prefiero contartelo a que se me quede dentro.",
            detalles = listOf(
                DetalleRelato("d1", "Le prometiste el cuaderno de mates", true),
                DetalleRelato("d2", "Te fuiste con Marta sin darselo", true),
                DetalleRelato("d3", "No pudo estudiar el tema tres", true),
                DetalleRelato("d4", "Te lo cuenta para no guardarselo dentro", true),
                DetalleRelato("d5", "Iker esta enfadado con Marta", false),
                DetalleRelato("d6", "Iker quiere que le pidas perdon delante de todos", false),
                DetalleRelato("d7", "Iker aprobo el control", false)
            ),
            opciones = listOf(
                OpcionEscucha("o1", "Te dije que si y luego te deje colgado, y encima suspendiste. Tienes razon.", TipoRespuesta.PARAFRASEO),
                OpcionEscucha("o2", "Que necesitas ahora, que te pase mis apuntes o algo mas?", TipoRespuesta.PREGUNTA_ABIERTA),
                OpcionEscucha("o3", "Tampoco es para tanto, era un cuaderno.", TipoRespuesta.JUICIO),
                OpcionEscucha("o4", "Es que Marta me llamo y no me acorde, siempre me pasa lo mismo.", TipoRespuesta.DESVIO)
            ),
            explicacion = "Cuando alguien nos hace una critica justa, el impulso es defenderse. Escuchar entero antes de contestar y reconocer lo que es verdad desactiva la mitad del conflicto."
        )
    )

    fun reto(misionId: String): RetoEscucha = retos[misionId] ?: retos.values.first()
}
