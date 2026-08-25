package com.socialkids.app

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
import com.socialkids.app.data.repository.JuegoRepository
import com.socialkids.app.data.seed.CartasSeed
import com.socialkids.app.data.seed.MundoSeed
import com.socialkids.app.domain.model.ResultadoActividad
import com.socialkids.app.util.RelojFijo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// ---------------------------------------------------------------- dobles de prueba

class PerfilDaoFalso : PerfilDao {
    private val estado = MutableStateFlow<PerfilEntity?>(null)
    override fun observar(): Flow<PerfilEntity?> = estado
    override suspend fun obtener(): PerfilEntity? = estado.value
    override suspend fun guardar(perfil: PerfilEntity) { estado.value = perfil }
    override suspend fun sumarXp(delta: Int) {
        estado.value = estado.value?.let { it.copy(xp = it.xp + delta) }
    }
    override suspend fun actualizarIdentidad(alias: String, avatarId: Int) {
        estado.value = estado.value?.copy(alias = alias, avatarId = avatarId)
    }
    override suspend fun marcarOnboarding() {
        estado.value = estado.value?.copy(onboardingHecho = true)
    }
    override suspend fun borrar() { estado.value = null }
}

class ProgresoDaoFalso : ProgresoDao {
    private val estado = MutableStateFlow<List<ProgresoMisionEntity>>(emptyList())
    override fun observarTodo(): Flow<List<ProgresoMisionEntity>> = estado
    override suspend fun obtener(misionId: String) = estado.value.firstOrNull { it.misionId == misionId }
    override suspend fun guardar(progreso: ProgresoMisionEntity) {
        estado.value = estado.value.filterNot { it.misionId == progreso.misionId } + progreso
    }
    override suspend fun todos(): List<ProgresoMisionEntity> = estado.value
    override suspend fun completadas(): Int = estado.value.count { it.completada }
    override suspend fun borrar() { estado.value = emptyList() }
}

class IntentoDaoFalso : IntentoDao {
    private val estado = MutableStateFlow<List<IntentoEntity>>(emptyList())
    private var siguienteId = 1L
    override suspend fun insertar(intento: IntentoEntity): Long {
        val id = siguienteId++
        estado.value = estado.value + intento.copy(id = id)
        return id
    }
    override fun observarUltimos(limite: Int): Flow<List<IntentoEntity>> =
        estado.map { it.sortedByDescending { i -> i.creadoEn }.take(limite) }
    override fun contarHitos(mecanica: String): Flow<Int> =
        estado.map { lista -> lista.filter { it.mecanica == mecanica && it.hito }.map { it.misionId }.distinct().size }
    override suspend fun contarHitosAhora(mecanica: String): Int =
        estado.value.filter { it.mecanica == mecanica && it.hito }.map { it.misionId }.distinct().size
    override suspend fun total(): Int = estado.value.size
    override fun puntajeMedio(): Flow<Double?> =
        estado.map { lista -> if (lista.isEmpty()) null else lista.sumOf { it.puntaje }.toDouble() / lista.size }
    override fun observarDeMision(misionId: String): Flow<List<IntentoEntity>> =
        estado.map { lista -> lista.filter { it.misionId == misionId } }
    override suspend fun borrar() { estado.value = emptyList() }
}

class ColeccionDaoFalso : ColeccionDao {
    private val cartas = MutableStateFlow<List<CartaEntity>>(emptyList())
    private val insignias = MutableStateFlow<List<InsigniaEntity>>(emptyList())
    override fun observarCartas(): Flow<List<CartaEntity>> = cartas
    override suspend fun idsCartas(): List<String> = cartas.value.map { it.cartaId }
    override suspend fun idsInsignias(): List<String> = insignias.value.map { it.insigniaId }
    override suspend fun desbloquearCarta(carta: CartaEntity): Long {
        if (cartas.value.none { it.cartaId == carta.cartaId }) {
            cartas.value = cartas.value + carta
            return 1L
        }
        return -1L
    }
    override suspend fun totalCartas(): Int = cartas.value.size
    override fun observarInsignias(): Flow<List<InsigniaEntity>> = insignias
    override suspend fun conseguirInsignia(insignia: InsigniaEntity): Long {
        if (insignias.value.none { it.insigniaId == insignia.insigniaId }) {
            insignias.value = insignias.value + insignia
            return 1L
        }
        return -1L
    }
    override suspend fun borrarCartas() { cartas.value = emptyList() }
    override suspend fun borrarInsignias() { insignias.value = emptyList() }
}

class AnimoDaoFalso : AnimoDao {
    private val estado = MutableStateFlow<List<AnimoEntity>>(emptyList())
    private var siguienteId = 1L
    override fun observarTodo(): Flow<List<AnimoEntity>> = estado
    override suspend fun insertar(animo: AnimoEntity): Long {
        val id = siguienteId++
        estado.value = estado.value + animo.copy(id = id)
        return id
    }
    override suspend fun borrarUno(id: Long) { estado.value = estado.value.filterNot { it.id == id } }
    override suspend fun total(): Int = estado.value.size
    override suspend fun borrar() { estado.value = emptyList() }
}

class VisitaDaoFalso : VisitaDao {
    private val estado = MutableStateFlow<List<Long>>(emptyList())
    override suspend fun registrar(visita: VisitaEntity): Long {
        if (!estado.value.contains(visita.diaEpoch)) {
            estado.value = estado.value + visita.diaEpoch
            return 1L
        }
        return -1L
    }
    override fun observarDias(): Flow<List<Long>> = estado
    override suspend fun dias(): List<Long> = estado.value
    override suspend fun borrar() { estado.value = emptyList() }
}

// ---------------------------------------------------------------- pruebas

@OptIn(ExperimentalCoroutinesApi::class)
class JuegoRepositoryTest {

    private lateinit var reloj: RelojFijo
    private lateinit var repo: JuegoRepository
    private lateinit var coleccion: ColeccionDaoFalso
    private lateinit var visitas: VisitaDaoFalso

    private val misionFaro1 = MundoSeed.mision("m_faro_1")!!

    private fun resultado(estrellas: Int, puntaje: Int) = ResultadoActividad(
        puntaje = puntaje,
        estrellas = estrellas,
        titulo = "Prueba",
        explicacion = "Explicacion",
        consejo = "Consejo"
    )

    @Before
    fun preparar() {
        reloj = RelojFijo(instante = 1_000L, dia = 20_000L)
        coleccion = ColeccionDaoFalso()
        visitas = VisitaDaoFalso()
        repo = JuegoRepository(
            perfilDao = PerfilDaoFalso(),
            progresoDao = ProgresoDaoFalso(),
            intentoDao = IntentoDaoFalso(),
            coleccionDao = coleccion,
            animoDao = AnimoDaoFalso(),
            visitaDao = visitas,
            reloj = reloj
        )
    }

    @Test
    fun `crear perfil deja la carta de bienvenida y una visita`() = runTest {
        repo.crearPerfil("Ada", 3)
        val estado = repo.estado.first()
        assertEquals("Ada", estado.perfil?.alias)
        assertEquals(3, estado.perfil?.avatarId)
        assertTrue(estado.cartas.contains(CartasSeed.CARTA_BIENVENIDA))
        assertEquals(listOf(20_000L), visitas.dias())
    }

    @Test
    fun `el alias se recorta y nunca queda vacio`() = runTest {
        repo.crearPerfil("   ", 0)
        assertEquals("Explorador", repo.estado.first().perfil?.alias)
        repo.actualizarIdentidad("UnAliasDemasiadoLargoParaCaber", 1)
        assertEquals(16, repo.estado.first().perfil?.alias?.length)
    }

    @Test
    fun `sin perfil no se puede registrar un resultado`() = runTest {
        assertNull(repo.registrarResultado(misionFaro1.id, resultado(3, 95), true))
    }

    @Test
    fun `completar una mision suma xp, guarda progreso y da la carta`() = runTest {
        repo.crearPerfil("Ada", 0)
        val recompensa = repo.registrarResultado(misionFaro1.id, resultado(3, 95), true)
        assertNotNull(recompensa)
        assertEquals(misionFaro1.xp, recompensa!!.xpGanada)
        assertEquals(misionFaro1.cartaId, recompensa.cartaNueva?.id)

        val estado = repo.estado.first()
        assertEquals(misionFaro1.xp, estado.estadisticas.xp)
        assertEquals(1, estado.estadisticas.misionesCompletadas)
        assertEquals(1, estado.estadisticas.misionesDominadas)
        assertEquals(1, estado.estadisticas.rostrosClavados)
        assertTrue(estado.cartas.contains(misionFaro1.cartaId))
    }

    @Test
    fun `repetir la misma mision no duplica la carta ni conserva las estrellas peores`() = runTest {
        repo.crearPerfil("Ada", 0)
        repo.registrarResultado(misionFaro1.id, resultado(3, 95), true)
        val segunda = repo.registrarResultado(misionFaro1.id, resultado(1, 50), false)

        assertNull("La carta ya estaba desbloqueada", segunda?.cartaNueva)
        val estado = repo.estado.first()
        assertEquals(1, estado.estadisticas.misionesCompletadas)
        assertEquals(3, estado.progreso[misionFaro1.id]?.mejoresEstrellas)
        assertEquals(2, estado.progreso[misionFaro1.id]?.intentos)
    }

    @Test
    fun `un resultado de cero estrellas no completa la mision`() = runTest {
        repo.crearPerfil("Ada", 0)
        repo.registrarResultado(misionFaro1.id, resultado(0, 20), false)
        val estado = repo.estado.first()
        assertEquals(0, estado.estadisticas.misionesCompletadas)
        assertFalse(estado.cartas.contains(misionFaro1.cartaId))
        assertTrue(estado.estadisticas.xp > 0)
    }

    @Test
    fun `la primera mision otorga la insignia de primer paso`() = runTest {
        repo.crearPerfil("Ada", 0)
        val recompensa = repo.registrarResultado(misionFaro1.id, resultado(2, 75), false)
        assertTrue(recompensa!!.insigniasNuevas.map { it.id }.contains("ins_primer_paso"))
        assertTrue(repo.estado.first().insignias.contains("ins_primer_paso"))
    }

    @Test
    fun `completar las cuatro misiones de una zona la marca como completada`() = runTest {
        repo.crearPerfil("Ada", 0)
        val faro = MundoSeed.misionesDe(com.socialkids.app.domain.model.ZonaId.FARO)
        var ultima = repo.registrarResultado(faro[0].id, resultado(3, 95), true)
        assertNull(ultima?.zonaCompletada)
        faro.drop(1).forEach { m ->
            ultima = repo.registrarResultado(m.id, resultado(3, 95), true)
        }
        assertNotNull(ultima?.zonaCompletada)
        assertEquals(1, repo.estado.first().estadisticas.zonasCompletadas)
    }

    @Test
    fun `subir de nivel se detecta en la recompensa`() = runTest {
        repo.crearPerfil("Ada", 0)
        var subio = false
        MundoSeed.misiones.take(6).forEach { m ->
            val r = repo.registrarResultado(m.id, resultado(3, 95), true)
            if (r?.subioNivel == true) subio = true
        }
        assertTrue("Con seis misiones dominadas se deberia subir de nivel", subio)
    }

    @Test
    fun `guardar animo recorta la nota y acota la intensidad`() = runTest {
        repo.crearPerfil("Ada", 0)
        repo.guardarAnimo("Alegria", 50, "x".repeat(300))
        val registros = repo.animos.first()
        assertEquals(1, registros.size)
        assertEquals(10, registros.first().intensidad)
        assertEquals(140, registros.first().nota.length)
    }

    @Test
    fun `la racha crece con dias consecutivos y se corta con un hueco`() = runTest {
        repo.crearPerfil("Ada", 0)
        reloj.avanzarDias(1)
        repo.registrarVisita()
        reloj.avanzarDias(1)
        repo.registrarVisita()
        assertEquals(3, repo.estado.first().estadisticas.rachaActual)

        reloj.avanzarDias(3)
        repo.registrarVisita()
        assertEquals(1, repo.estado.first().estadisticas.rachaActual)
    }

    @Test
    fun `diez anotaciones de animo otorgan la insignia del diario`() = runTest {
        repo.crearPerfil("Ada", 0)
        repeat(10) { repo.guardarAnimo("Calma", 5, "nota $it") }
        assertTrue(repo.estado.first().insignias.contains("ins_diario"))
    }

    @Test
    fun `reiniciar el progreso deja el juego como recien instalado`() = runTest {
        repo.crearPerfil("Ada", 0)
        repo.registrarResultado(misionFaro1.id, resultado(3, 95), true)
        repo.guardarAnimo("Enfado", 7, "nota")
        repo.reiniciarProgreso()

        val estado = repo.estado.first()
        assertNull(estado.perfil)
        assertTrue(estado.progreso.isEmpty())
        assertTrue(estado.cartas.isEmpty())
        assertTrue(estado.insignias.isEmpty())
        assertEquals(0, estado.estadisticas.xp)
        assertEquals(0, estado.estadisticas.registrosAnimo)
    }

    @Test
    fun `una mision inexistente no altera el estado`() = runTest {
        repo.crearPerfil("Ada", 0)
        assertNull(repo.registrarResultado("mision_que_no_existe", resultado(3, 95), true))
        assertEquals(0, repo.estado.first().estadisticas.xp)
    }
}
