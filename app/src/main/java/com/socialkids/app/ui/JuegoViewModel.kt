package com.socialkids.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.socialkids.app.SocialKidsApp
import com.socialkids.app.data.repository.Ajustes
import com.socialkids.app.data.repository.AjustesRepository
import com.socialkids.app.data.repository.JuegoRepository
import com.socialkids.app.data.seed.MundoSeed
import com.socialkids.app.domain.model.EstadoJuego
import com.socialkids.app.domain.model.Mision
import com.socialkids.app.domain.model.Recompensa
import com.socialkids.app.domain.model.ResultadoActividad
import com.socialkids.app.domain.usecase.DesbloqueoEvaluador
import com.socialkids.app.domain.usecase.EstadisticasCalculadora
import com.socialkids.app.domain.usecase.RachaCalculadora
import com.socialkids.app.domain.usecase.RegistroAnimo
import com.socialkids.app.domain.usecase.ResumenAnimo
import com.socialkids.app.util.Reloj
import com.socialkids.app.util.RelojSistema
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel principal. Mantiene el estado del juego y expone las acciones
 * que modifican datos persistidos.
 */
class JuegoViewModel(
    private val repositorio: JuegoRepository,
    private val ajustesRepositorio: AjustesRepository,
    private val reloj: Reloj = RelojSistema
) : ViewModel() {

    val estado: StateFlow<EstadoJuego> = repositorio.estado
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EstadoJuego())

    val ajustes: StateFlow<Ajustes> = ajustesRepositorio.ajustes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Ajustes())

    val animos: StateFlow<List<RegistroAnimo>> = repositorio.animos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val resumenAnimo: StateFlow<ResumenAnimo> = repositorio.animos
        .map { EstadisticasCalculadora.resumen(it, reloj.hoyEpochDay()) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            EstadisticasCalculadora.resumen(emptyList(), reloj.hoyEpochDay())
        )

    val racha: StateFlow<Pair<Int, Int>> = repositorio.diasVisitados
        .map { dias ->
            RachaCalculadora.rachaActual(dias, reloj.hoyEpochDay()) to RachaCalculadora.mejorRacha(dias)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0 to 0)

    /** Mision recomendada ahora mismo, calculada desde el progreso real. */
    val siguienteMision: StateFlow<Mision?> = estado
        .map { e ->
            DesbloqueoEvaluador.siguienteMision(
                MundoSeed.zonas, MundoSeed.misiones, e.progreso, e.estadisticas.xp
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val misionesRepaso: StateFlow<List<Mision>> = estado
        .map { DesbloqueoEvaluador.misionesDeRepaso(MundoSeed.misiones, it.progreso) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _ultimaRecompensa = MutableStateFlow<Recompensa?>(null)
    val ultimaRecompensa: StateFlow<Recompensa?> = _ultimaRecompensa.asStateFlow()

    fun crearPerfil(alias: String, avatarId: Int) = viewModelScope.launch {
        repositorio.crearPerfil(alias, avatarId)
    }

    fun actualizarIdentidad(alias: String, avatarId: Int) = viewModelScope.launch {
        repositorio.actualizarIdentidad(alias, avatarId)
    }

    fun registrarVisita() = viewModelScope.launch { repositorio.registrarVisita() }

    fun guardarResultado(misionId: String, resultado: ResultadoActividad, hito: Boolean) =
        viewModelScope.launch {
            _ultimaRecompensa.value = repositorio.registrarResultado(misionId, resultado, hito)
        }

    fun limpiarRecompensa() {
        _ultimaRecompensa.value = null
    }

    fun guardarAnimo(emocion: String, intensidad: Int, nota: String) = viewModelScope.launch {
        repositorio.guardarAnimo(emocion, intensidad, nota)
    }

    fun borrarAnimo(id: Long) = viewModelScope.launch { repositorio.borrarAnimo(id) }

    fun reiniciarProgreso() = viewModelScope.launch { repositorio.reiniciarProgreso() }

    fun cambiarSonido(v: Boolean) = viewModelScope.launch { ajustesRepositorio.cambiarSonido(v) }
    fun cambiarVibracion(v: Boolean) = viewModelScope.launch { ajustesRepositorio.cambiarVibracion(v) }
    fun cambiarAnimaciones(v: Boolean) = viewModelScope.launch { ajustesRepositorio.cambiarAnimaciones(v) }
    fun cambiarTextoGrande(v: Boolean) = viewModelScope.launch { ajustesRepositorio.cambiarTextoGrande(v) }
    fun cambiarAltoContraste(v: Boolean) = viewModelScope.launch { ajustesRepositorio.cambiarAltoContraste(v) }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SocialKidsApp
                JuegoViewModel(app.contenedor.juegoRepository, app.contenedor.ajustesRepository)
            }
        }
    }
}

/** Se mantiene por claridad de imports en pantallas que crean el ViewModel. */
internal fun extrasVacias(): CreationExtras = CreationExtras.Empty

/** Combina dos flujos en pares, util para pantallas que necesitan varios origenes. */
internal fun <A, B> emparejar(
    a: kotlinx.coroutines.flow.Flow<A>,
    b: kotlinx.coroutines.flow.Flow<B>
) = combine(a, b) { x, y -> x to y }
