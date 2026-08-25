package com.socialkids.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.socialkids.app.data.seed.MundoSeed
import com.socialkids.app.data.seed.RetosConflicto
import com.socialkids.app.data.seed.RetosEscucha
import com.socialkids.app.data.seed.RetosMensaje
import com.socialkids.app.data.seed.RetosPuente
import com.socialkids.app.data.seed.RetosRostro
import com.socialkids.app.data.seed.RetosTermometro
import com.socialkids.app.domain.engine.ConflictoEngine
import com.socialkids.app.domain.engine.Desenlace
import com.socialkids.app.domain.engine.EjeRostro
import com.socialkids.app.domain.engine.EscuchaEngine
import com.socialkids.app.domain.engine.Estilo
import com.socialkids.app.domain.engine.EstadoConflicto
import com.socialkids.app.domain.engine.Estrategia
import com.socialkids.app.domain.engine.Ficha
import com.socialkids.app.domain.engine.MensajeEngine
import com.socialkids.app.domain.engine.OpcionConflicto
import com.socialkids.app.domain.engine.OpcionEscucha
import com.socialkids.app.domain.engine.PiezaPuente
import com.socialkids.app.domain.engine.PuenteEngine
import com.socialkids.app.domain.engine.Ranura
import com.socialkids.app.domain.engine.RasgoExtra
import com.socialkids.app.domain.engine.RostroConfig
import com.socialkids.app.domain.engine.RostroEngine
import com.socialkids.app.domain.engine.Tablon
import com.socialkids.app.domain.engine.TermometroEngine
import com.socialkids.app.domain.model.Mecanica
import com.socialkids.app.domain.model.Mision
import com.socialkids.app.domain.model.ResultadoActividad

/** Fases por las que pasa el Detective de Escucha. */
enum class FaseEscucha { RELATO, DETALLES, RESPUESTA }

/**
 * Estado y logica de presentacion de una mision concreta.
 * Los calculos los hacen los motores de dominio; aqui solo vive el estado
 * de la pantalla y la traduccion a resultado.
 */
class ActividadViewModel(val misionId: String) : ViewModel() {

    val mision: Mision = MundoSeed.mision(misionId) ?: MundoSeed.misiones.first()

    // ---- Estudio de rostros
    val objetivoRostro = RetosRostro.objetivo(misionId)
    var rostro by mutableStateOf(RostroConfig())
        private set

    fun moverEje(eje: EjeRostro, valor: Int) {
        rostro = rostro.conEje(eje, valor)
    }

    fun alternarRasgo(rasgo: RasgoExtra) {
        rostro = rostro.alternar(rasgo)
    }

    // ---- Detective de escucha
    val retoEscucha = RetosEscucha.reto(misionId)
    var fase by mutableStateOf(FaseEscucha.RELATO)
        private set
    var seleccionDetalles by mutableStateOf(emptySet<String>())
        private set
    var opcionEscucha by mutableStateOf<OpcionEscucha?>(null)
        private set

    fun avanzarFase() {
        fase = when (fase) {
            FaseEscucha.RELATO -> FaseEscucha.DETALLES
            FaseEscucha.DETALLES -> FaseEscucha.RESPUESTA
            FaseEscucha.RESPUESTA -> FaseEscucha.RESPUESTA
        }
    }

    fun alternarDetalle(id: String) {
        seleccionDetalles = if (seleccionDetalles.contains(id)) {
            seleccionDetalles - id
        } else {
            seleccionDetalles + id
        }
    }

    fun elegirOpcionEscucha(opcion: OpcionEscucha) {
        opcionEscucha = opcion
    }

    // ---- Puente de la empatia
    val retoPuente = RetosPuente.reto(misionId)
    var colocacionPuente by mutableStateOf<Map<Tablon, PiezaPuente?>>(emptyMap())
        private set

    fun colocarPieza(tablon: Tablon, pieza: PiezaPuente?) {
        val nuevo = colocacionPuente.toMutableMap()
        // Una pieza solo puede estar en un tablon a la vez.
        nuevo.entries.filter { it.value?.id == pieza?.id }.forEach { nuevo[it.key] = null }
        nuevo[tablon] = pieza
        colocacionPuente = nuevo
    }

    fun piezasLibres(): List<PiezaPuente> {
        val usadas = colocacionPuente.values.filterNotNull().map { it.id }.toSet()
        return RetosPuente.piezasBarajadas(retoPuente).filter { it.id !in usadas }
    }

    // ---- Constructor de mensajes
    val retoMensaje = RetosMensaje.reto(misionId)
    var colocacionMensaje by mutableStateOf<Map<Ranura, Ficha?>>(emptyMap())
        private set

    fun colocarFicha(ranura: Ranura, ficha: Ficha?) {
        colocacionMensaje = colocacionMensaje.toMutableMap().apply { put(ranura, ficha) }
    }

    val frasePreview: String get() = MensajeEngine.frase(colocacionMensaje)
    val estiloPreview: Estilo get() = MensajeEngine.estilo(colocacionMensaje)

    // ---- Simulador de conflicto
    val retoConflicto = RetosConflicto.reto(misionId)
    var estadoConflicto by mutableStateOf(EstadoConflicto(nodoId = retoConflicto.nodoInicial))
        private set
    var bitacora by mutableStateOf(emptyList<String>())
        private set

    fun elegirOpcionConflicto(opcion: OpcionConflicto) {
        bitacora = bitacora + listOf("Tu: ${opcion.texto}", opcion.replica)
        estadoConflicto = ConflictoEngine.aplicar(estadoConflicto, opcion)
    }

    val conflictoTerminado: Boolean
        get() = ConflictoEngine.terminado(estadoConflicto, retoConflicto)

    // ---- Termometro emocional
    val retoTermometro = RetosTermometro.reto(misionId)
    var intensidad by mutableStateOf(5)
        private set
    var estrategiaElegida by mutableStateOf<Estrategia?>(null)
        private set

    fun cambiarIntensidad(valor: Int) {
        intensidad = valor.coerceIn(0, 10)
    }

    fun elegirEstrategia(estrategia: Estrategia) {
        estrategiaElegida = estrategia
    }

    // ---- Resultado comun
    var resultado by mutableStateOf<ResultadoActividad?>(null)
        private set
    var hito by mutableStateOf(false)
        private set

    /** Indica si ya se puede pulsar el boton de terminar. */
    fun listoParaEvaluar(): Boolean = when (mision.mecanica) {
        Mecanica.ROSTROS -> true
        Mecanica.ESCUCHA -> fase == FaseEscucha.RESPUESTA && opcionEscucha != null
        Mecanica.PUENTE -> PuenteEngine.completo(colocacionPuente)
        Mecanica.MENSAJE -> MensajeEngine.completo(colocacionMensaje)
        Mecanica.CONFLICTO -> conflictoTerminado
        Mecanica.TERMOMETRO -> estrategiaElegida != null
    }

    /** Calcula el resultado con el motor correspondiente y marca el hito de la mecanica. */
    fun evaluar(): ResultadoActividad {
        val (r, h) = when (mision.mecanica) {
            Mecanica.ROSTROS -> {
                val res = RostroEngine.evaluar(rostro, objetivoRostro)
                res to (res.estrellas >= 3)
            }
            Mecanica.ESCUCHA -> {
                val opcion = opcionEscucha ?: retoEscucha.opciones.first()
                val res = EscuchaEngine.evaluar(seleccionDetalles, retoEscucha, opcion)
                res to (res.estrellas >= 3)
            }
            Mecanica.PUENTE -> {
                val res = PuenteEngine.evaluar(colocacionPuente, retoPuente)
                res to (PuenteEngine.tablonesCorrectos(colocacionPuente) == 3)
            }
            Mecanica.MENSAJE -> {
                val res = MensajeEngine.evaluar(colocacionMensaje, retoMensaje)
                val construido = MensajeEngine.construir(colocacionMensaje)
                res to (construido.completo && construido.estilo == Estilo.ASERTIVO && construido.puntaje >= 88)
            }
            Mecanica.CONFLICTO -> {
                val res = ConflictoEngine.evaluar(estadoConflicto, retoConflicto)
                val desenlace = ConflictoEngine.desenlace(estadoConflicto)
                res to (desenlace == Desenlace.ACUERDO && estadoConflicto.calma >= 50)
            }
            Mecanica.TERMOMETRO -> {
                val estrategia = estrategiaElegida ?: retoTermometro.estrategias.first()
                val res = TermometroEngine.evaluar(intensidad, estrategia, retoTermometro)
                res to (res.puntaje >= 88)
            }
        }
        resultado = r
        hito = h
        return r
    }

    /** Vuelve a empezar la misma mision sin salir de la pantalla. */
    fun reiniciar() {
        rostro = RostroConfig()
        fase = FaseEscucha.RELATO
        seleccionDetalles = emptySet()
        opcionEscucha = null
        colocacionPuente = emptyMap()
        colocacionMensaje = emptyMap()
        estadoConflicto = EstadoConflicto(nodoId = retoConflicto.nodoInicial)
        bitacora = emptyList()
        intensidad = 5
        estrategiaElegida = null
        resultado = null
        hito = false
    }

    companion object {
        fun factory(misionId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer { ActividadViewModel(misionId) }
        }
    }
}
