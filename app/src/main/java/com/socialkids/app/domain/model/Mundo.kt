package com.socialkids.app.domain.model

/** Las seis zonas de la Isla Conecta. El orden define el recorrido del mapa. */
enum class ZonaId {
    FARO,      // Faro de las Emociones
    BOSQUE,    // Bosque que Escucha
    PUENTE,    // Puente de la Empatia
    PLAZA,     // Plaza de las Palabras
    TALLER,    // Taller de Acuerdos
    MIRADOR    // Mirador de la Amistad
}

/** Mecanica interactiva principal de cada mision. */
enum class Mecanica {
    ROSTROS,     // Estudio de rostros: construir una cara con controles
    ESCUCHA,     // Detective de escucha: rescatar los datos reales del relato
    PUENTE,      // Puente de la empatia: colocar piezas siente/piensa/necesita
    MENSAJE,     // Constructor de mensajes-yo con fichas arrastrables
    CONFLICTO,   // Simulador de conflicto con variables vivas
    TERMOMETRO   // Termometro de intensidad y estrategia proporcional
}

enum class EstadoMision { BLOQUEADA, DISPONIBLE, INICIADA, COMPLETADA, DOMINADA }

data class Zona(
    val id: ZonaId,
    val nombre: String,
    val lema: String,
    val descripcion: String,
    val orden: Int,
    val xpNecesaria: Int,
    val mapaX: Float,
    val mapaY: Float
)

data class Mision(
    val id: String,
    val zonaId: ZonaId,
    val orden: Int,
    val titulo: String,
    val consigna: String,
    val mecanica: Mecanica,
    val dificultad: Int,
    val xp: Int,
    val cartaId: String?
)

data class Carta(
    val id: String,
    val nombre: String,
    val categoria: CategoriaCarta,
    val rareza: Rareza,
    val lema: String,
    val dato: String,
    val figura: Figura,
    val tono: Int
)

enum class CategoriaCarta { EMOCION, HABILIDAD, PERSONAJE, LUGAR }
enum class Rareza { COMUN, RARA, LEGENDARIA }

/** Familias de ilustracion vectorial dibujadas con Compose Canvas. */
enum class Figura {
    CORAZON, CHISPA, OLA, HOJA, ESTRELLA, NUBE, LLAVE, PUENTE,
    BURBUJA, FARO, BRUJULA, SEMILLA
}

data class Insignia(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val pista: String,
    val figura: Figura,
    val tono: Int
)

data class AvatarSpec(
    val id: Int,
    val nombre: String,
    val tono: Int,
    val accesorio: Accesorio
)

enum class Accesorio { GORRA, GAFAS, AURICULARES, BUFANDA, FLOR, ANTENA, CAPUCHA, DIADEMA }

/** Resultado normalizado que devuelve cualquier mecanica al terminar. */
data class ResultadoActividad(
    val puntaje: Int,
    val estrellas: Int,
    val titulo: String,
    val explicacion: String,
    val consejo: String,
    val detalles: List<String> = emptyList()
)

object Estrellas {
    /** Regla unica de conversion puntaje -> estrellas para todas las mecanicas. */
    fun de(puntaje: Int): Int = when {
        puntaje >= 88 -> 3
        puntaje >= 70 -> 2
        puntaje >= 45 -> 1
        else -> 0
    }
}
