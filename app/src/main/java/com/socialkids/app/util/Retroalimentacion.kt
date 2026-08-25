package com.socialkids.app.util

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.socialkids.app.ui.theme.LocalAjustes

/** Tipos de aviso que puede emitir la aplicacion. */
enum class Aviso { TOQUE, ACIERTO, FALLO, RECOMPENSA, DESBLOQUEO }

/**
 * Sonido y vibracion cortos, siempre opcionales.
 * El sonido se genera con el tono del sistema: no hace falta ningun archivo de audio,
 * asi que la app sigue funcionando sin conexion y sin recursos pesados.
 */
class Retroalimentacion(
    private val haptica: HapticFeedback,
    private val sonidoActivo: Boolean,
    private val vibracionActiva: Boolean,
    private val generador: ToneGenerator?
) {
    fun emitir(aviso: Aviso) {
        if (vibracionActiva) {
            val tipo = when (aviso) {
                Aviso.TOQUE -> HapticFeedbackType.TextHandleMove
                else -> HapticFeedbackType.LongPress
            }
            runCatching { haptica.performHapticFeedback(tipo) }
        }
        if (sonidoActivo && generador != null) {
            val (tono, duracion) = when (aviso) {
                Aviso.TOQUE -> ToneGenerator.TONE_PROP_BEEP to 60
                Aviso.ACIERTO -> ToneGenerator.TONE_PROP_ACK to 140
                Aviso.FALLO -> ToneGenerator.TONE_PROP_NACK to 160
                Aviso.RECOMPENSA -> ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD to 220
                Aviso.DESBLOQUEO -> ToneGenerator.TONE_CDMA_CONFIRM to 260
            }
            runCatching { generador?.startTone(tono, duracion) }
        }
    }
}

@Composable
fun recordarRetroalimentacion(): Retroalimentacion {
    val ajustes = LocalAjustes.current
    val haptica = LocalHapticFeedback.current
    val generador = remember {
        runCatching { ToneGenerator(AudioManager.STREAM_MUSIC, 55) }.getOrNull()
    }
    DisposableEffect(Unit) {
        onDispose { runCatching { generador?.release() } }
    }
    return remember(ajustes.sonido, ajustes.vibracion, generador) {
        Retroalimentacion(haptica, ajustes.sonido, ajustes.vibracion, generador)
    }
}
