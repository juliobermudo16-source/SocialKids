package com.socialkids.app.data.seed

import com.socialkids.app.domain.model.Accesorio
import com.socialkids.app.domain.model.AvatarSpec
import com.socialkids.app.domain.model.Carta
import com.socialkids.app.domain.model.CategoriaCarta
import com.socialkids.app.domain.model.Figura
import com.socialkids.app.domain.model.Rareza

/**
 * Las Cartas de la Isla: la coleccion del juego.
 * Cada carta se desbloquea al completar la mision que la guarda; la carta de Nima
 * es el regalo de bienvenida al crear el perfil.
 */
object CartasSeed {

    const val CARTA_BIENVENIDA = "c_nima"

    val cartas: List<Carta> = listOf(
        Carta(
            "c_nima", "Nima", CategoriaCarta.PERSONAJE, Rareza.LEGENDARIA,
            "La guia de la isla",
            "Nima no resuelve las misiones por ti: solo enciende la luz para que veas mejor.",
            Figura.CHISPA, 5
        ),
        Carta(
            "c_alegria", "Alegria", CategoriaCarta.EMOCION, Rareza.COMUN,
            "Ligera y contagiosa",
            "La alegria se nota mas en los ojos que en la boca: por eso una sonrisa falsa se descubre facil.",
            Figura.ESTRELLA, 2
        ),
        Carta(
            "c_termometro", "Termometro", CategoriaCarta.HABILIDAD, Rareza.COMUN,
            "Del 0 al 10",
            "Antes de reaccionar, ponerle un numero a lo que sientes baja la intensidad casi sin querer.",
            Figura.LLAVE, 0
        ),
        Carta(
            "c_miedo", "Miedo", CategoriaCarta.EMOCION, Rareza.COMUN,
            "El aviso del cuerpo",
            "El miedo abre mucho los ojos porque el cerebro quiere ver todo lo que pueda antes de decidir.",
            Figura.NUBE, 6
        ),
        Carta(
            "c_calma", "Calma", CategoriaCarta.HABILIDAD, Rareza.RARA,
            "Aire que ordena",
            "Soltar el aire mas despacio de lo que lo tomas le dice al cuerpo que ya no hay peligro.",
            Figura.OLA, 1
        ),
        Carta(
            "c_escucha", "Escucha", CategoriaCarta.HABILIDAD, Rareza.COMUN,
            "Oir no es escuchar",
            "Repetir con tus palabras lo que te contaron es la forma mas rapida de demostrar que escuchaste.",
            Figura.OLA, 3
        ),
        Carta(
            "c_atencion", "Atencion", CategoriaCarta.HABILIDAD, Rareza.COMUN,
            "Una cosa a la vez",
            "El cerebro no escucha bien dos cosas a la vez: elige a que le das el foco.",
            Figura.CHISPA, 4
        ),
        Carta(
            "c_pistas", "Pistas ocultas", CategoriaCarta.HABILIDAD, Rareza.RARA,
            "Lo que no se dice",
            "La voz, la postura y la cara cuentan mas de la mitad del mensaje.",
            Figura.BRUJULA, 7
        ),
        Carta(
            "c_paciencia", "Paciencia", CategoriaCarta.HABILIDAD, Rareza.RARA,
            "Esperar el final",
            "Interrumpir hace que la otra persona empiece de nuevo por dentro: se pierde mas tiempo del que se gana.",
            Figura.HOJA, 8
        ),
        Carta(
            "c_empatia", "Empatia", CategoriaCarta.HABILIDAD, Rareza.LEGENDARIA,
            "Entrar sin invadir",
            "Empatia no es sentir lo mismo: es entender lo que le pasa al otro sin dejar de ser tu.",
            Figura.PUENTE, 9
        ),
        Carta(
            "c_inclusion", "Inclusion", CategoriaCarta.HABILIDAD, Rareza.RARA,
            "Un sitio mas",
            "Invitar a alguien cuesta diez segundos y le puede cambiar la semana entera.",
            Figura.CORAZON, 10
        ),
        Carta(
            "c_tristeza", "Tristeza", CategoriaCarta.EMOCION, Rareza.COMUN,
            "Pide compania",
            "La tristeza baja la energia del cuerpo para que pares y pidas ayuda. No es debilidad.",
            Figura.NUBE, 11
        ),
        Carta(
            "c_perspectiva", "Perspectiva", CategoriaCarta.HABILIDAD, Rareza.RARA,
            "El otro lado",
            "Dos personas pueden contar la misma escena distinta sin que ninguna mienta.",
            Figura.BRUJULA, 0
        ),
        Carta(
            "c_mensaje_yo", "Mensaje-yo", CategoriaCarta.HABILIDAD, Rareza.LEGENDARIA,
            "Yo siento, no tu eres",
            "Hablar de lo que te pasa a ti evita que el otro se ponga a la defensiva.",
            Figura.BURBUJA, 1
        ),
        Carta(
            "c_limite", "Limite", CategoriaCarta.HABILIDAD, Rareza.RARA,
            "Hasta aqui",
            "Poner un limite no rompe una amistad: la hace mas honesta.",
            Figura.LLAVE, 2
        ),
        Carta(
            "c_no", "El no amable", CategoriaCarta.HABILIDAD, Rareza.RARA,
            "Decir no sin herir",
            "Un no claro con un motivo corto se acepta mejor que un no con mil excusas.",
            Figura.SEMILLA, 3
        ),
        Carta(
            "c_pausa", "Pausa", CategoriaCarta.HABILIDAD, Rareza.COMUN,
            "Tres segundos",
            "Esperar tres segundos antes de contestar cambia por completo lo que dices.",
            Figura.OLA, 4
        ),
        Carta(
            "c_acuerdo", "Acuerdo", CategoriaCarta.HABILIDAD, Rareza.RARA,
            "Los dos ganan",
            "Un acuerdo bueno es el que las dos personas pueden cumplir sin enfadarse.",
            Figura.PUENTE, 5
        ),
        Carta(
            "c_reparar", "Reparar", CategoriaCarta.HABILIDAD, Rareza.RARA,
            "Despues del ruido",
            "Casi todas las relaciones se rompen un poco. Lo que las salva es repararlas pronto.",
            Figura.SEMILLA, 6
        ),
        Carta(
            "c_equipo", "Equipo", CategoriaCarta.LUGAR, Rareza.COMUN,
            "Reparto justo",
            "Repartir tareas por escrito evita la mitad de las discusiones de un trabajo en grupo.",
            Figura.ESTRELLA, 7
        ),
        Carta(
            "c_pausa_larga", "Tiempo muerto", CategoriaCarta.HABILIDAD, Rareza.LEGENDARIA,
            "Volvemos luego",
            "Pedir una pausa en una discusion no es huir: es proteger la conversacion.",
            Figura.NUBE, 8
        ),
        Carta(
            "c_amistad", "Amistad", CategoriaCarta.HABILIDAD, Rareza.LEGENDARIA,
            "Se cuida",
            "Las amistades no se mantienen solas: se sostienen con detalles pequenios y repetidos.",
            Figura.CORAZON, 9
        ),
        Carta(
            "c_valentia", "Valentia social", CategoriaCarta.HABILIDAD, Rareza.LEGENDARIA,
            "Parar una burla",
            "Cuando alguien dice basta, casi siempre hay mas gente que pensaba lo mismo en silencio.",
            Figura.FARO, 10
        ),
        Carta(
            "c_perdon", "Perdon", CategoriaCarta.HABILIDAD, Rareza.RARA,
            "Sin peros",
            "Un perdon con la palabra pero detras deja de ser un perdon.",
            Figura.HOJA, 11
        ),
        Carta(
            "c_isla", "Isla Conecta", CategoriaCarta.LUGAR, Rareza.LEGENDARIA,
            "Mapa completo",
            "La isla es una manera de mirar: cada zona es una habilidad que puedes usar hoy mismo.",
            Figura.FARO, 0
        )
    )

    fun carta(id: String): Carta? = cartas.firstOrNull { it.id == id }

    val avatares: List<AvatarSpec> = listOf(
        AvatarSpec(0, "Rumbo", 0, Accesorio.GORRA),
        AvatarSpec(1, "Lupa", 1, Accesorio.GAFAS),
        AvatarSpec(2, "Eco", 2, Accesorio.AURICULARES),
        AvatarSpec(3, "Nube", 3, Accesorio.BUFANDA),
        AvatarSpec(4, "Brote", 4, Accesorio.FLOR),
        AvatarSpec(5, "Chispa", 5, Accesorio.ANTENA),
        AvatarSpec(6, "Sombra", 6, Accesorio.CAPUCHA),
        AvatarSpec(7, "Ronda", 7, Accesorio.DIADEMA)
    )

    fun avatar(id: Int): AvatarSpec = avatares.firstOrNull { it.id == id } ?: avatares.first()
}
