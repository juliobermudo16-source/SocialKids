package com.socialkids.app.data.seed

import com.socialkids.app.domain.model.Mecanica
import com.socialkids.app.domain.model.Mision
import com.socialkids.app.domain.model.Zona
import com.socialkids.app.domain.model.ZonaId

/**
 * La Isla Conecta: seis zonas y veinticuatro misiones.
 * Este catalogo es contenido fijo del juego; el progreso del jugador vive en Room.
 */
object MundoSeed {

    val zonas: List<Zona> = listOf(
        Zona(
            id = ZonaId.FARO,
            nombre = "Faro de las Emociones",
            lema = "Aqui se aprende a ponerle nombre a lo que pasa por dentro",
            descripcion = "El faro dejo de girar cuando la isla olvido como se llaman las emociones. Vuelve a encenderlo.",
            orden = 1, xpNecesaria = 0, mapaX = 0.20f, mapaY = 0.78f
        ),
        Zona(
            id = ZonaId.BOSQUE,
            nombre = "Bosque que Escucha",
            lema = "Los arboles solo crecen si alguien escucha de verdad",
            descripcion = "En este bosque las hojas repiten lo ultimo que oyeron. Si escuchas bien, el camino se abre.",
            orden = 2, xpNecesaria = 60, mapaX = 0.62f, mapaY = 0.70f
        ),
        Zona(
            id = ZonaId.PUENTE,
            nombre = "Puente de la Empatia",
            lema = "Tres tablones: siente, piensa, necesita",
            descripcion = "El puente se cayo. Solo se sostiene cuando alguien entiende de verdad a la otra persona.",
            orden = 3, xpNecesaria = 150, mapaX = 0.36f, mapaY = 0.52f
        ),
        Zona(
            id = ZonaId.PLAZA,
            nombre = "Plaza de las Palabras",
            lema = "Decir lo que sientes sin romper nada",
            descripcion = "En la plaza las palabras se vuelven objetos. Las agresivas rompen cosas; las asertivas construyen.",
            orden = 4, xpNecesaria = 260, mapaX = 0.72f, mapaY = 0.40f
        ),
        Zona(
            id = ZonaId.TALLER,
            nombre = "Taller de Acuerdos",
            lema = "Aqui se reparan las discusiones",
            descripcion = "Un taller lleno de maquinas que solo arrancan cuando dos personas encuentran una salida buena para las dos.",
            orden = 5, xpNecesaria = 390, mapaX = 0.30f, mapaY = 0.26f
        ),
        Zona(
            id = ZonaId.MIRADOR,
            nombre = "Mirador de la Amistad",
            lema = "Nadie se queda fuera del mirador",
            descripcion = "Desde arriba se ve la isla entera y tambien quien se ha quedado solo abajo. Tu decides si bajas a buscarle.",
            orden = 6, xpNecesaria = 540, mapaX = 0.66f, mapaY = 0.14f
        )
    )

    val misiones: List<Mision> = listOf(
        // ---------- FARO DE LAS EMOCIONES ----------
        Mision(
            "m_faro_1", ZonaId.FARO, 1, "La primera luz",
            "Nima ha olvidado como es la cara de la alegria. Construyela en el espejo del faro.",
            Mecanica.ROSTROS, 1, 25, "c_alegria"
        ),
        Mision(
            "m_faro_2", ZonaId.FARO, 2, "El termometro roto",
            "Mide cuanta emocion hay de verdad en esta escena y elige que hacer con ella.",
            Mecanica.TERMOMETRO, 1, 25, "c_termometro"
        ),
        Mision(
            "m_faro_3", ZonaId.FARO, 3, "Caras que se parecen",
            "El miedo y la sorpresa se confunden. Ajusta el rostro hasta que solo se lea uno.",
            Mecanica.ROSTROS, 2, 30, "c_miedo"
        ),
        Mision(
            "m_faro_4", ZonaId.FARO, 4, "Enfado a las siete",
            "Una escena tipica de casa. Que intensidad tiene de verdad y que estrategia le sirve?",
            Mecanica.TERMOMETRO, 2, 30, "c_calma"
        ),

        // ---------- BOSQUE QUE ESCUCHA ----------
        Mision(
            "m_bosque_1", ZonaId.BOSQUE, 1, "Lo que dijo Ada",
            "Ada te cuenta algo importante. Rescata solo lo que dijo de verdad.",
            Mecanica.ESCUCHA, 1, 30, "c_escucha"
        ),
        Mision(
            "m_bosque_2", ZonaId.BOSQUE, 2, "El ruido de fondo",
            "Bruno habla mientras suena todo a la vez. Que se queda y que se pierde?",
            Mecanica.ESCUCHA, 2, 35, "c_atencion"
        ),
        Mision(
            "m_bosque_3", ZonaId.BOSQUE, 3, "Debajo de las palabras",
            "Lo que se dice y lo que se siente no siempre coinciden. Levanta el puente.",
            Mecanica.PUENTE, 2, 35, "c_pistas"
        ),
        Mision(
            "m_bosque_4", ZonaId.BOSQUE, 4, "Sin interrumpir",
            "Zoe cuenta un problema largo. Aguanta hasta el final y responde bien.",
            Mecanica.ESCUCHA, 3, 40, "c_paciencia"
        ),

        // ---------- PUENTE DE LA EMPATIA ----------
        Mision(
            "m_puente_1", ZonaId.PUENTE, 1, "El primer tablon",
            "Teo se quedo sin equipo en gimnasia. Que siente, que piensa y que necesita?",
            Mecanica.PUENTE, 1, 30, "c_empatia"
        ),
        Mision(
            "m_puente_2", ZonaId.PUENTE, 2, "La nueva de clase",
            "Lena lleva tres dias en el colegio y come sola. Cruza hasta ella.",
            Mecanica.PUENTE, 2, 35, "c_inclusion"
        ),
        Mision(
            "m_puente_3", ZonaId.PUENTE, 3, "La cara de despues",
            "Reconstruye el rostro de alguien que acaba de recibir una noticia triste.",
            Mecanica.ROSTROS, 3, 40, "c_tristeza"
        ),
        Mision(
            "m_puente_4", ZonaId.PUENTE, 4, "Dos versiones",
            "Dos amigos cuentan la misma pelea de forma distinta. Construye el puente del otro lado.",
            Mecanica.PUENTE, 3, 45, "c_perspectiva"
        ),

        // ---------- PLAZA DE LAS PALABRAS ----------
        Mision(
            "m_plaza_1", ZonaId.PLAZA, 1, "Mi turno tambien cuenta",
            "Siempre eligen el juego sin contar contigo. Arma el mensaje que lo dice sin pelear.",
            Mecanica.MENSAJE, 1, 35, "c_mensaje_yo"
        ),
        Mision(
            "m_plaza_2", ZonaId.PLAZA, 2, "El mote",
            "Te llaman de una forma que no te gusta. Pon el limite en modo asertivo.",
            Mecanica.MENSAJE, 2, 40, "c_limite"
        ),
        Mision(
            "m_plaza_3", ZonaId.PLAZA, 3, "Saber decir no",
            "Te piden copiar los deberes. Di que no sin perder al amigo.",
            Mecanica.MENSAJE, 3, 45, "c_no"
        ),
        Mision(
            "m_plaza_4", ZonaId.PLAZA, 4, "Antes de contestar",
            "Escucha entera la queja de Iker antes de responder.",
            Mecanica.ESCUCHA, 3, 40, "c_pausa"
        ),

        // ---------- TALLER DE ACUERDOS ----------
        Mision(
            "m_taller_1", ZonaId.TALLER, 1, "El mando roto",
            "Discusion en casa por un mando de consola. Manten viva la conversacion.",
            Mecanica.CONFLICTO, 2, 40, "c_acuerdo"
        ),
        Mision(
            "m_taller_2", ZonaId.TALLER, 2, "Reparar despues de gritar",
            "Ya gritaste. Ahora arma el mensaje que repara.",
            Mecanica.MENSAJE, 3, 45, "c_reparar"
        ),
        Mision(
            "m_taller_3", ZonaId.TALLER, 3, "El trabajo en grupo",
            "Nadie hace su parte y la entrega es maniana. Negocia.",
            Mecanica.CONFLICTO, 3, 45, "c_equipo"
        ),
        Mision(
            "m_taller_4", ZonaId.TALLER, 4, "La pausa que salva",
            "La discusion sube de tono. Aprende a pedir tiempo muerto.",
            Mecanica.CONFLICTO, 3, 50, "c_pausa_larga"
        ),

        // ---------- MIRADOR DE LA AMISTAD ----------
        Mision(
            "m_mirador_1", ZonaId.MIRADOR, 1, "El que mira desde lejos",
            "Hay alguien que nunca entra al grupo. Entiende por que antes de invitarle.",
            Mecanica.PUENTE, 2, 40, "c_amistad"
        ),
        Mision(
            "m_mirador_2", ZonaId.MIRADOR, 2, "Cuando el grupo se pasa",
            "Se rien de alguien y esperan que te rias tu tambien. Que haces?",
            Mecanica.CONFLICTO, 3, 50, "c_valentia"
        ),
        Mision(
            "m_mirador_3", ZonaId.MIRADOR, 3, "Pedir perdon de verdad",
            "Un perdon de verdad tiene partes. Armalo pieza a pieza.",
            Mecanica.MENSAJE, 3, 50, "c_perdon"
        ),
        Mision(
            "m_mirador_4", ZonaId.MIRADOR, 4, "La luz del mirador",
            "Ultimo reto de la isla: mide una emocion fuerte y elige bien.",
            Mecanica.TERMOMETRO, 3, 55, "c_isla"
        )
    )

    fun misionesDe(zonaId: ZonaId): List<Mision> =
        misiones.filter { it.zonaId == zonaId }.sortedBy { it.orden }

    fun mision(id: String): Mision? = misiones.firstOrNull { it.id == id }

    fun zona(id: ZonaId): Zona = zonas.first { it.id == id }

    /** XP total posible si se domina todo. Se usa para el porcentaje global del mapa. */
    val xpTotalPosible: Int = misiones.sumOf { it.xp }
}
