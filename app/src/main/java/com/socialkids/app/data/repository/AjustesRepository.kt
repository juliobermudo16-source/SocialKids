package com.socialkids.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.almacenAjustes: DataStore<Preferences> by preferencesDataStore(name = "ajustes_socialkids")

/** Preferencias de accesibilidad y confort. Todas se pueden desactivar. */
data class Ajustes(
    val sonido: Boolean = true,
    val vibracion: Boolean = true,
    val animaciones: Boolean = true,
    val textoGrande: Boolean = false,
    val altoContraste: Boolean = false
)

class AjustesRepository(private val context: Context) {

    private object Claves {
        val SONIDO = booleanPreferencesKey("sonido")
        val VIBRACION = booleanPreferencesKey("vibracion")
        val ANIMACIONES = booleanPreferencesKey("animaciones")
        val TEXTO_GRANDE = booleanPreferencesKey("texto_grande")
        val ALTO_CONTRASTE = booleanPreferencesKey("alto_contraste")
    }

    val ajustes: Flow<Ajustes> = context.almacenAjustes.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { p ->
            Ajustes(
                sonido = p[Claves.SONIDO] ?: true,
                vibracion = p[Claves.VIBRACION] ?: true,
                animaciones = p[Claves.ANIMACIONES] ?: true,
                textoGrande = p[Claves.TEXTO_GRANDE] ?: false,
                altoContraste = p[Claves.ALTO_CONTRASTE] ?: false
            )
        }

    suspend fun cambiarSonido(valor: Boolean) = guardar(Claves.SONIDO, valor)
    suspend fun cambiarVibracion(valor: Boolean) = guardar(Claves.VIBRACION, valor)
    suspend fun cambiarAnimaciones(valor: Boolean) = guardar(Claves.ANIMACIONES, valor)
    suspend fun cambiarTextoGrande(valor: Boolean) = guardar(Claves.TEXTO_GRANDE, valor)
    suspend fun cambiarAltoContraste(valor: Boolean) = guardar(Claves.ALTO_CONTRASTE, valor)

    private suspend fun guardar(clave: Preferences.Key<Boolean>, valor: Boolean) {
        context.almacenAjustes.edit { it[clave] = valor }
    }
}
