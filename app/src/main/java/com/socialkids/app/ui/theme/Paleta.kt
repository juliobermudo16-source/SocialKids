package com.socialkids.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.socialkids.app.domain.model.ZonaId

/**
 * Paleta propia de SocialKids. Doce tonos con nombre que se usan
 * en cartas, insignias, avatares y escenarios.
 */
object Paleta {

    val Coral = Color(0xFFFF6B5A)
    val Turquesa = Color(0xFF12B5B0)
    val Sol = Color(0xFFFFC24B)
    val Violeta = Color(0xFF7C5CFF)
    val Menta = Color(0xFF3FD08D)
    val Rosa = Color(0xFFFF87B2)
    val Azul = Color(0xFF3AA0FF)
    val Naranja = Color(0xFFFF9A3C)
    val Lima = Color(0xFFB7E04A)
    val Lavanda = Color(0xFFB08CFF)
    val Cielo = Color(0xFF63D2FF)
    val Arena = Color(0xFFFFD9A0)

    val tonos = listOf(
        Coral, Turquesa, Sol, Violeta, Menta, Rosa,
        Azul, Naranja, Lima, Lavanda, Cielo, Arena
    )

    fun tono(indice: Int): Color = tonos[Math.floorMod(indice, tonos.size)]

    // Neutros de la isla
    val NocheProfunda = Color(0xFF0E1B2B)
    val Noche = Color(0xFF16263A)
    val NocheSuave = Color(0xFF22374F)
    val Papel = Color(0xFFFFF7EC)
    val PapelSuave = Color(0xFFFFEFDA)
    val Blanco = Color(0xFFFFFFFF)
    val TextoClaro = Color(0xFFEAF2FB)
    val TextoOscuro = Color(0xFF1B2B3F)

    // Estados
    val Exito = Color(0xFF2FBF71)
    val Aviso = Color(0xFFFFB020)
    val Error = Color(0xFFEF5B5B)
    val Bloqueado = Color(0xFF9AAAC0)

    /** Color principal de cada zona de la isla. */
    fun colorZona(zona: ZonaId): Color = when (zona) {
        ZonaId.FARO -> Turquesa
        ZonaId.BOSQUE -> Menta
        ZonaId.PUENTE -> Violeta
        ZonaId.PLAZA -> Coral
        ZonaId.TALLER -> Sol
        ZonaId.MIRADOR -> Rosa
    }

    /** Color de apoyo de cada zona, para degradados y fondos. */
    fun colorZonaSuave(zona: ZonaId): Color = when (zona) {
        ZonaId.FARO -> Cielo
        ZonaId.BOSQUE -> Lima
        ZonaId.PUENTE -> Lavanda
        ZonaId.PLAZA -> Naranja
        ZonaId.TALLER -> Arena
        ZonaId.MIRADOR -> Violeta
    }

    fun degradadoZona(zona: ZonaId): Brush =
        Brush.linearGradient(listOf(colorZona(zona), colorZonaSuave(zona)))

    val degradadoCielo = Brush.verticalGradient(
        listOf(Color(0xFF5FD3F0), Color(0xFF9BE7C4), Color(0xFFFFE2A8))
    )

    val degradadoNoche = Brush.verticalGradient(
        listOf(Color(0xFF10243A), Color(0xFF1B3B57), Color(0xFF2A5170))
    )

    fun degradadoEstrella(activo: Boolean): Brush = if (activo) {
        Brush.linearGradient(listOf(Sol, Naranja))
    } else {
        Brush.linearGradient(listOf(Bloqueado.copy(alpha = 0.4f), Bloqueado.copy(alpha = 0.25f)))
    }
}
