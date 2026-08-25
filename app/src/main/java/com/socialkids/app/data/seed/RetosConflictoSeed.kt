package com.socialkids.app.data.seed

import com.socialkids.app.domain.engine.NodoConflicto
import com.socialkids.app.domain.engine.OpcionConflicto
import com.socialkids.app.domain.engine.RetoConflicto

/**
 * Contenido del Simulador de Conflicto.
 * Cada opcion mueve tres variables vivas (calma, confianza y acuerdo).
 * El desenlace no esta escrito: lo calcula el motor a partir de esas variables.
 */
object RetosConflicto {

    private val retos: Map<String, RetoConflicto> = mapOf(
        "m_taller_1" to RetoConflicto(
            id = "cf_taller_1",
            personaje = "Leo",
            escena = "Tu primo Leo lleva una hora con el mando y te toca a ti. Cuando se lo dices, sube el volumen y no contesta.",
            nodoInicial = "n1",
            nodos = listOf(
                NodoConflicto(
                    "n1", "Leo sigue jugando como si no te oyera.",
                    "Ahora no, estoy en mitad de una partida.",
                    listOf(
                        OpcionConflicto("a", "Vale, termina esa y luego me toca a mi.", 8, 6, 12, "Leo baja un poco el volumen. Vale... esta acaba en cinco minutos.", "n2", "Acuerdo con plazo"),
                        OpcionConflicto("b", "Siempre igual, eres un egoista.", -22, -18, -8, "Leo se gira de golpe. Pues ahora no te lo dejo.", "n2", "Ataque"),
                        OpcionConflicto("c", "Le quitas el mando de las manos.", -30, -25, -10, "Leo se levanta gritando y la cosa se pone fea.", "n2", "Fuerza")
                    )
                ),
                NodoConflicto(
                    "n2", "Pasan los cinco minutos y Leo sigue jugando.",
                    "Es que me queda poco, de verdad.",
                    listOf(
                        OpcionConflicto("a", "Dijimos cinco minutos. Ponemos una alarma?", 6, 10, 18, "Leo resopla, pero acepta la alarma.", "n3", "Regla clara"),
                        OpcionConflicto("b", "Se lo cuento a mama ahora mismo.", -8, -14, 4, "Leo pausa el juego, molesto. Eres un chivato.", "n3", "Autoridad"),
                        OpcionConflicto("c", "Te sientas al lado sin decir nada, esperando.", 3, -4, -6, "Leo sigue jugando. El silencio se hace largo.", "n3", "Aguantar")
                    )
                ),
                NodoConflicto(
                    "n3", "Leo por fin suelta el mando y te mira.",
                    "Es que si paro pierdo lo de hoy.",
                    listOf(
                        OpcionConflicto("a", "Y si hacemos turnos de media hora con alarma?", 10, 12, 25, "Leo lo piensa. Media hora cada uno, vale.", "n4", "Propuesta"),
                        OpcionConflicto("b", "Me da igual lo que pierdas.", -18, -16, -12, "Leo se encierra en su cuarto con el mando.", "n4", "Desprecio"),
                        OpcionConflicto("c", "Dejalo, juega tu, total nunca me toca.", 2, -8, -18, "Leo se queda con el mando y con una cara rara.", "n4", "Rendirse")
                    )
                ),
                NodoConflicto("n4", "Fin de la escena.", "", emptyList())
            ),
            explicacion = "En una discusion no gana quien habla mas fuerte, sino quien consigue que los dos acepten una regla. Una alarma o un turno convierten una pelea en un acuerdo que se puede cumplir."
        ),
        "m_taller_3" to RetoConflicto(
            id = "cf_taller_3",
            personaje = "Tu grupo de clase",
            escena = "El trabajo de ciencias se entrega maniana. Tu has hecho tu parte y otras dos personas no han mandado nada al grupo.",
            nodoInicial = "n1",
            nodos = listOf(
                NodoConflicto(
                    "n1", "Escribes en el chat del grupo.",
                    "Marc: perdon, se me paso. Ines: yo pensaba que lo hacia Marc.",
                    listOf(
                        OpcionConflicto("a", "Vale. Que falta exactamente y quien coge cada cosa?", 8, 10, 20, "Los dos empiezan a listar lo que queda.", "n2", "Ordenar"),
                        OpcionConflicto("b", "Siempre me toca hacerlo todo a mi.", -14, -12, -4, "Marc: no empieces. Ines se sale del chat.", "n2", "Reproche"),
                        OpcionConflicto("c", "Lo hago yo esta noche y ya esta.", 4, -6, -14, "Nadie contesta. Te quedas con todo.", "n2", "Cargar solo")
                    )
                ),
                NodoConflicto(
                    "n2", "Son las siete de la tarde.",
                    "Marc: yo puedo, pero hasta las nueve tengo entreno.",
                    listOf(
                        OpcionConflicto("a", "Perfecto: tu a las nueve la conclusion, Ines las graficas.", 8, 12, 22, "Marc pone un ok. Ines empieza a mandar capturas.", "n3", "Reparto real"),
                        OpcionConflicto("b", "Pues te lo saltas, es tu problema.", -16, -14, -6, "Marc se desconecta del chat.", "n3", "Imponer"),
                        OpcionConflicto("c", "Bueno, no pasa nada, ya vere.", 2, -6, -12, "El chat se queda en silencio.", "n3", "Ceder")
                    )
                ),
                NodoConflicto(
                    "n3", "Falta la ultima parte y ya es tarde.",
                    "Ines: yo he acabado lo mio. Y la portada?",
                    listOf(
                        OpcionConflicto("a", "La hago yo, que es rapido. Manianna lo revisamos los tres.", 8, 10, 20, "Los tres quedan diez minutos antes de clase.", "n4", "Cierre justo"),
                        OpcionConflicto("b", "La portada que la haga quien menos ha hecho.", -10, -8, 2, "Ines contesta con un emoji cortante.", "n4", "Factura"),
                        OpcionConflicto("c", "No contestas y lo entregas sin portada.", -6, -12, -10, "Al dia siguiente nadie sabe que paso.", "n4", "Silencio")
                    )
                ),
                NodoConflicto("n4", "Fin de la escena.", "", emptyList())
            ),
            explicacion = "En un grupo, la frase que mas ayuda no es quien tiene la culpa, sino que falta y quien lo coge. Repartir tareas concretas con hora arregla casi cualquier trabajo en grupo."
        ),
        "m_taller_4" to RetoConflicto(
            id = "cf_taller_4",
            personaje = "Sofia",
            escena = "Discutes con tu amiga Sofia por un plan que se cambio sin avisarte. Notas que estas subiendo el tono.",
            nodoInicial = "n1",
            nodos = listOf(
                NodoConflicto(
                    "n1", "Sofia habla rapido y tu ya tienes la voz alta.",
                    "Es que tu nunca quieres hacer nada de lo que proponemos.",
                    listOf(
                        OpcionConflicto("a", "Estoy subiendo la voz. Paramos un momento?", 18, 8, 6, "Sofia se calla un segundo. Vale.", "n2", "Pausa"),
                        OpcionConflicto("b", "Nunca? Ahora te lo demuestro.", -20, -10, -6, "Sofia sube el tono todavia mas.", "n2", "Contraataque"),
                        OpcionConflicto("c", "Te quedas en silencio con cara seria.", -4, -8, -4, "Sofia: ves? Ni contestas.", "n2", "Bloqueo")
                    )
                ),
                NodoConflicto(
                    "n2", "Silencio incomodo de unos segundos.",
                    "Sofia: entonces que hacemos.",
                    listOf(
                        OpcionConflicto("a", "Dame cinco minutos y seguimos, no quiero decir algo feo.", 16, 12, 8, "Sofia acepta y se sienta.", "n3", "Tiempo muerto"),
                        OpcionConflicto("b", "Nada, dejalo, haz lo que quieras.", -10, -14, -14, "Sofia se marcha.", "n3", "Portazo"),
                        OpcionConflicto("c", "Sigues discutiendo sin parar.", -14, -8, -4, "Los dos repetis lo mismo tres veces.", "n3", "Bucle")
                    )
                ),
                NodoConflicto(
                    "n3", "Pasan cinco minutos. Los dos respirais.",
                    "Sofia: perdona por lo de nunca. No es verdad.",
                    listOf(
                        OpcionConflicto("a", "Yo tambien grite. Me molesto que cambiarais el plan sin decirmelo.", 12, 18, 28, "Sofia asiente. La conversacion cambia de tono.", "n4", "Reparar"),
                        OpcionConflicto("b", "Ya, claro, ahora te disculpas.", -16, -18, -10, "Sofia se cierra otra vez.", "n4", "Rencor"),
                        OpcionConflicto("c", "No pasa nada, olvidalo.", 2, -4, -10, "El tema queda enterrado sin resolver.", "n4", "Tapar")
                    )
                ),
                NodoConflicto("n4", "Fin de la escena.", "", emptyList())
            ),
            explicacion = "Pedir una pausa no es huir de la discusion: es evitar decir la frase de la que te arrepentirias. Volver cinco minutos despues casi siempre da una conversacion distinta."
        ),
        "m_mirador_2" to RetoConflicto(
            id = "cf_mirador_2",
            personaje = "El grupo",
            escena = "En el patio, tu grupo empieza a imitar la forma de andar de Ruben. Todos se rien y te miran esperando que te unas.",
            nodoInicial = "n1",
            nodos = listOf(
                NodoConflicto(
                    "n1", "Ruben esta a diez metros y se ha dado cuenta.",
                    "Un amigo te da un codazo: mira, mira.",
                    listOf(
                        OpcionConflicto("a", "A mi no me hace gracia. Dejadlo.", 6, 14, 22, "Se hace un silencio raro. Alguien deja de reirse.", "n2", "Decir basta"),
                        OpcionConflicto("b", "Te ries con ellos.", -10, -20, -18, "Ruben baja la cabeza y se va.", "n2", "Seguir la corriente"),
                        OpcionConflicto("c", "Miras al suelo sin decir nada.", -2, -8, -8, "La broma sigue un rato mas.", "n2", "Callar")
                    )
                ),
                NodoConflicto(
                    "n2", "Uno del grupo se pone a la defensiva.",
                    "Era una broma, no te pongas asi.",
                    listOf(
                        OpcionConflicto("a", "Ya, pero se ha dado cuenta y no se estaba riendo.", 8, 16, 20, "Dos personas del grupo asienten.", "n3", "Hecho concreto"),
                        OpcionConflicto("b", "Sois todos unos imbeciles.", -20, -16, -8, "El grupo se pone en tu contra.", "n3", "Insulto"),
                        OpcionConflicto("c", "Vale, vale, era broma.", -4, -10, -14, "La imitacion vuelve a empezar.", "n3", "Retirarse")
                    )
                ),
                NodoConflicto(
                    "n3", "Ruben esta solo junto a la valla.",
                    "El grupo ya ha cambiado de tema.",
                    listOf(
                        OpcionConflicto("a", "Vas hacia Ruben y le hablas de otra cosa.", 10, 18, 26, "Ruben tarda en contestar, pero se queda contigo.", "n4", "Acompanar"),
                        OpcionConflicto("b", "Le dices delante de todos que no les haga caso.", -6, -6, 4, "Ruben se pone mas rojo todavia.", "n4", "Publico"),
                        OpcionConflicto("c", "Te quedas con el grupo.", -4, -12, -16, "Ruben se queda en la valla hasta que suena el timbre.", "n4", "No hacer nada")
                    )
                ),
                NodoConflicto("n4", "Fin de la escena.", "", emptyList())
            ),
            explicacion = "En una burla de grupo casi nunca hay una sola persona incomoda. Cuando alguien dice a mi no me hace gracia, suele aparecer gente que pensaba lo mismo y no se atrevia."
        )
    )

    fun reto(misionId: String): RetoConflicto = retos[misionId] ?: retos.values.first()
}
