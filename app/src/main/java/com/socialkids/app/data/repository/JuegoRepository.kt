package com.socialkids.app.data.repository

import com.socialkids.app.data.local.dao.AnimoDao
import com.socialkids.app.data.local.dao.ColeccionDao
import com.socialkids.app.data.local.dao.IntentoDao
import com.socialkids.app.data.local.dao.PerfilDao
import com.socialkids.app.data.local.dao.ProgresoDao
import com.socialkids.app.data.local.dao.VisitaDao
import com.socialkids.app.data.local.entity.AnimoEntity
import com.socialkids.app.data.local.entity.CartaEntity
import com.socialkids.app.data.local.entity.InsigniaEntity
import com.socialkids.app.data.local.entity.IntentoEntity
import com.socialkids.app.data.local.entity.PerfilEntity
import com.socialkids.app.data.local.entity.ProgresoMisionEntity
import com.socialkids.app.data.local.entity.VisitaEntity
import com.socialkids.app.data.seed.CartasSeed
import com.socialkids.app.data.seed.MundoSeed
import com.socialkids.app.domain.model.EstadoJuego
import com.socialkids.app.domain.model.Mecanica
import com.socialkids.app.domain.model.PerfilJugador
import com.socialkids.app.domain.model.Recompensa
import com.socialkids.app.domain.model.ResultadoActividad
import com.socialkids.app.domain.model.Zona
import com.socialkids.app.domain.usecase.DesbloqueoEvaluador
import com.socialkids.app.domain.usecase.EstadisticasJugador
import com.socialkids.app.domain.usecase.InsigniaEvaluador
import com.socialkids.app.domain.usecase.ProgresoCalculadora
import com.socialkids.app.domain.usecase.ProgresoMision
import com.socialkids.app.domain.usecase.RachaCalculadora
import com.socialkids.app.domain.usecase.RegistroAnimo
import com.socialkids.app.util.Reloj
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Unica puerta de entrada a los datos del juego.
 * Toda la interfaz lee de aqui y todos los cambios pasan por aqui.
 */
class JuegoRepository(
    private val perfilDao: PerfilDao,
    private val progresoDao: ProgresoDao,
    private val intentoDao: IntentoDao,
    private val coleccionDao: ColeccionDao,
    private val animoDao: AnimoDao,
    private val visitaDao: VisitaDao,
    private val reloj: Reloj
) {

    private data class Hitos(
        val rostros: Int = 0,
        val puentes: Int = 0,
        val mensajes: Int = 0,
        val conflictos: Int = 0
    )

    private data class Extras(
        val animos: Int,
        val racha: Int,
        val hitos: Hitos
    )

    private val flujoHitos: Flow<Hitos> = combine(
        intentoDao.contarHitos(Mecanica.ROSTROS.name),
        intentoDao.contarHitos(Mecanica.PUENTE.name),
        intentoDao.contarHitos(Mecanica.MENSAJE.name),
        intentoDao.contarHitos(Mecanica.CONFLICTO.name)
    ) { r, p, m, c -> Hitos(r, p, m, c) }

    private val flujoExtras: Flow<Extras> = combine(
        animoDao.observarTodo().map { it.size },
        visitaDao.observarDias(),
        flujoHitos
    ) { animos, dias, hitos ->
        Extras(animos, RachaCalculadora.rachaActual(dias, reloj.hoyEpochDay()), hitos)
    }

    val estado: Flow<EstadoJuego> = combine(
        perfilDao.observar(),
        progresoDao.observarTodo(),
        coleccionDao.observarCartas(),
        coleccionDao.observarInsignias(),
        flujoExtras
    ) { perfil, progresos, cartas, insignias, extras ->
        val mapaProgreso = progresos.associate { it.misionId to it.aDominio() }
        val xp = perfil?.xp ?: 0
        EstadoJuego(
            perfil = perfil?.let {
                PerfilJugador(it.alias, it.avatarId, it.xp, it.onboardingHecho)
            },
            progreso = mapaProgreso,
            cartas = cartas.map { it.cartaId }.toSet(),
            insignias = insignias.map { it.insigniaId }.toSet(),
            estadisticas = EstadisticasJugador(
                xp = xp,
                nivel = ProgresoCalculadora.nivelDeXp(xp),
                misionesCompletadas = mapaProgreso.values.count { it.completada },
                misionesDominadas = mapaProgreso.values.count { it.mejoresEstrellas >= 3 },
                zonasCompletadas = contarZonasCompletadas(mapaProgreso),
                cartasDesbloqueadas = cartas.size,
                registrosAnimo = extras.animos,
                rachaActual = extras.racha,
                mensajesAsertivosPerfectos = extras.hitos.mensajes,
                conflictosResueltosConCalma = extras.hitos.conflictos,
                puentesFirmes = extras.hitos.puentes,
                rostrosClavados = extras.hitos.rostros
            ),
            cargando = false
        )
    }

    val animos: Flow<List<RegistroAnimo>> = animoDao.observarTodo().map { lista ->
        lista.map { RegistroAnimo(it.id, it.diaEpoch, it.emocion, it.intensidad, it.nota) }
    }

    val ultimosIntentos: Flow<List<IntentoEntity>> = intentoDao.observarUltimos(30)

    val diasVisitados: Flow<List<Long>> = visitaDao.observarDias()

    // ---------------------------------------------------------------- perfil

    suspend fun crearPerfil(alias: String, avatarId: Int) {
        val limpio = alias.trim().take(16).ifBlank { "Explorador" }
        perfilDao.guardar(
            PerfilEntity(
                id = 1,
                alias = limpio,
                avatarId = avatarId,
                xp = 0,
                creadoEn = reloj.ahora(),
                onboardingHecho = true
            )
        )
        coleccionDao.desbloquearCarta(CartaEntity(CartasSeed.CARTA_BIENVENIDA, reloj.ahora()))
        registrarVisita()
    }

    suspend fun actualizarIdentidad(alias: String, avatarId: Int) {
        val limpio = alias.trim().take(16).ifBlank { "Explorador" }
        perfilDao.actualizarIdentidad(limpio, avatarId)
    }

    suspend fun registrarVisita() {
        visitaDao.registrar(VisitaEntity(reloj.hoyEpochDay()))
    }

    // ------------------------------------------------------------ actividad

    /**
     * Guarda el resultado de una mision y devuelve todo lo que el jugador gana.
     * Es el corazon del bucle de juego: progreso, XP, carta, insignias y zona.
     */
    suspend fun registrarResultado(
        misionId: String,
        resultado: ResultadoActividad,
        hito: Boolean
    ): Recompensa? {
        val mision = MundoSeed.mision(misionId) ?: return null
        val perfil = perfilDao.obtener() ?: return null

        val antes = estadisticasAhora(perfil.xp)
        val previo = progresoDao.obtener(misionId)
        val yaCompletada = previo?.completada == true
        val completaAhora = resultado.estrellas >= 1

        val xpGanada = ProgresoCalculadora.xpGanada(mision, resultado.estrellas, yaCompletada)

        progresoDao.guardar(
            ProgresoMisionEntity(
                misionId = misionId,
                zonaId = mision.zonaId.name,
                mejoresEstrellas = maxOf(previo?.mejoresEstrellas ?: 0, resultado.estrellas),
                mejorPuntaje = maxOf(previo?.mejorPuntaje ?: 0, resultado.puntaje),
                intentos = (previo?.intentos ?: 0) + 1,
                completada = yaCompletada || completaAhora,
                actualizadoEn = reloj.ahora()
            )
        )

        intentoDao.insertar(
            IntentoEntity(
                misionId = misionId,
                mecanica = mision.mecanica.name,
                puntaje = resultado.puntaje,
                estrellas = resultado.estrellas,
                hito = hito,
                diaEpoch = reloj.hoyEpochDay(),
                creadoEn = reloj.ahora()
            )
        )

        perfilDao.sumarXp(xpGanada)
        registrarVisita()

        var cartaNueva: com.socialkids.app.domain.model.Carta? = null
        if (completaAhora && mision.cartaId != null) {
            val yaTenia = coleccionDao.idsCartas().contains(mision.cartaId)
            if (!yaTenia) {
                coleccionDao.desbloquearCarta(CartaEntity(mision.cartaId, reloj.ahora()))
                cartaNueva = CartasSeed.carta(mision.cartaId)
            }
        }

        val xpNueva = perfil.xp + xpGanada
        val despues = estadisticasAhora(xpNueva)
        val nuevas = InsigniaEvaluador.nuevas(antes, despues)
        nuevas.forEach { coleccionDao.conseguirInsignia(InsigniaEntity(it.id, reloj.ahora())) }

        val zonaCompletada: Zona? = zonaRecienCompletada(mision.zonaId.name)

        return Recompensa(
            resultado = resultado,
            xpGanada = xpGanada,
            nivelAnterior = ProgresoCalculadora.nivelDeXp(perfil.xp),
            nivelNuevo = ProgresoCalculadora.nivelDeXp(xpNueva),
            cartaNueva = cartaNueva,
            insigniasNuevas = nuevas,
            zonaCompletada = zonaCompletada
        )
    }

    private suspend fun zonaRecienCompletada(zonaId: String): Zona? {
        val progresos = progresoDao.todos().associate { it.misionId to it.aDominio() }
        val zona = MundoSeed.zonas.firstOrNull { it.id.name == zonaId } ?: return null
        val misiones = MundoSeed.misionesDe(zona.id)
        return if (DesbloqueoEvaluador.zonaCompletada(misiones, progresos)) zona else null
    }

    private suspend fun estadisticasAhora(xp: Int): EstadisticasJugador {
        val progresos = progresoDao.todos().associate { it.misionId to it.aDominio() }
        return EstadisticasJugador(
            xp = xp,
            nivel = ProgresoCalculadora.nivelDeXp(xp),
            misionesCompletadas = progresos.values.count { it.completada },
            misionesDominadas = progresos.values.count { it.mejoresEstrellas >= 3 },
            zonasCompletadas = contarZonasCompletadas(progresos),
            cartasDesbloqueadas = coleccionDao.totalCartas(),
            registrosAnimo = animoDao.total(),
            rachaActual = RachaCalculadora.rachaActual(visitaDao.dias(), reloj.hoyEpochDay()),
            mensajesAsertivosPerfectos = intentoDao.contarHitosAhora(Mecanica.MENSAJE.name),
            conflictosResueltosConCalma = intentoDao.contarHitosAhora(Mecanica.CONFLICTO.name),
            puentesFirmes = intentoDao.contarHitosAhora(Mecanica.PUENTE.name),
            rostrosClavados = intentoDao.contarHitosAhora(Mecanica.ROSTROS.name)
        )
    }

    private fun contarZonasCompletadas(progresos: Map<String, ProgresoMision>): Int =
        MundoSeed.zonas.count { zona ->
            DesbloqueoEvaluador.zonaCompletada(MundoSeed.misionesDe(zona.id), progresos)
        }

    // ---------------------------------------------------------------- animo

    suspend fun guardarAnimo(emocion: String, intensidad: Int, nota: String) {
        animoDao.insertar(
            AnimoEntity(
                diaEpoch = reloj.hoyEpochDay(),
                emocion = emocion,
                intensidad = intensidad.coerceIn(1, 10),
                nota = nota.trim().take(140),
                creadoEn = reloj.ahora()
            )
        )
        registrarVisita()
        revisarInsignias()
    }

    suspend fun borrarAnimo(id: Long) = animoDao.borrarUno(id)

    /** Reevalua insignias fuera del bucle de misiones (por ejemplo tras anotar el animo). */
    suspend fun revisarInsignias() {
        val perfil = perfilDao.obtener() ?: return
        val stats = estadisticasAhora(perfil.xp)
        val yaTiene = coleccionDao.idsInsignias().toSet()
        InsigniaEvaluador.conseguidas(stats)
            .filter { it.id !in yaTiene }
            .forEach { coleccionDao.conseguirInsignia(InsigniaEntity(it.id, reloj.ahora())) }
    }

    // -------------------------------------------------------------- borrado

    suspend fun reiniciarProgreso() {
        progresoDao.borrar()
        intentoDao.borrar()
        coleccionDao.borrarCartas()
        coleccionDao.borrarInsignias()
        animoDao.borrar()
        visitaDao.borrar()
        perfilDao.borrar()
    }
}

internal fun ProgresoMisionEntity.aDominio(): ProgresoMision = ProgresoMision(
    misionId = misionId,
    mejoresEstrellas = mejoresEstrellas,
    intentos = intentos,
    completada = completada
)
