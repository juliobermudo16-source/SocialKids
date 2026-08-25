package com.socialkids.app

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.socialkids.app.data.local.SocialKidsDatabase
import com.socialkids.app.data.local.entity.AnimoEntity
import com.socialkids.app.data.local.entity.CartaEntity
import com.socialkids.app.data.local.entity.IntentoEntity
import com.socialkids.app.data.local.entity.PerfilEntity
import com.socialkids.app.data.local.entity.ProgresoMisionEntity
import com.socialkids.app.data.local.entity.VisitaEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas instrumentadas de persistencia real con Room.
 * Se ejecutan en un dispositivo o emulador con: ./gradlew connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class BaseDeDatosTest {

    private lateinit var base: SocialKidsDatabase

    @Before
    fun crear() {
        base = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SocialKidsDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun cerrar() {
        base.close()
    }

    @Test
    fun perfil_se_guarda_y_se_lee() = runBlocking {
        base.perfilDao().guardar(PerfilEntity(1, "Ada", 2, 0, 100L, true))
        val perfil = base.perfilDao().obtener()
        assertNotNull(perfil)
        assertEquals("Ada", perfil!!.alias)
        base.perfilDao().sumarXp(40)
        assertEquals(40, base.perfilDao().obtener()!!.xp)
    }

    @Test
    fun progreso_se_actualiza_sin_duplicar_filas() = runBlocking {
        val dao = base.progresoDao()
        dao.guardar(ProgresoMisionEntity("m_faro_1", "FARO", 1, 50, 1, true, 0L))
        dao.guardar(ProgresoMisionEntity("m_faro_1", "FARO", 3, 95, 2, true, 1L))
        assertEquals(1, dao.todos().size)
        assertEquals(3, dao.obtener("m_faro_1")!!.mejoresEstrellas)
        assertEquals(1, dao.completadas())
    }

    @Test
    fun los_hitos_se_cuentan_por_mision_distinta() = runBlocking {
        val dao = base.intentoDao()
        dao.insertar(IntentoEntity(0, "m_faro_1", "ROSTROS", 95, 3, true, 1L, 1L))
        dao.insertar(IntentoEntity(0, "m_faro_1", "ROSTROS", 95, 3, true, 1L, 2L))
        dao.insertar(IntentoEntity(0, "m_faro_3", "ROSTROS", 90, 3, true, 1L, 3L))
        dao.insertar(IntentoEntity(0, "m_puente_1", "PUENTE", 90, 3, false, 1L, 4L))
        assertEquals(2, dao.contarHitosAhora("ROSTROS"))
        assertEquals(0, dao.contarHitosAhora("PUENTE"))
        assertEquals(4, dao.total())
    }

    @Test
    fun las_cartas_no_se_duplican() = runBlocking {
        val dao = base.coleccionDao()
        dao.desbloquearCarta(CartaEntity("c_alegria", 1L))
        dao.desbloquearCarta(CartaEntity("c_alegria", 2L))
        assertEquals(1, dao.totalCartas())
        assertEquals(listOf("c_alegria"), dao.idsCartas())
    }

    @Test
    fun las_visitas_del_mismo_dia_solo_cuentan_una_vez() = runBlocking {
        val dao = base.visitaDao()
        dao.registrar(VisitaEntity(100L))
        dao.registrar(VisitaEntity(100L))
        dao.registrar(VisitaEntity(101L))
        assertEquals(2, dao.dias().size)
    }

    @Test
    fun el_diario_ordena_por_dia_descendente_y_permite_borrar() = runBlocking {
        val dao = base.animoDao()
        val id1 = dao.insertar(AnimoEntity(0, 100L, "Alegria", 8, "hoy", 1L))
        dao.insertar(AnimoEntity(0, 99L, "Calma", 5, "ayer", 2L))
        val lista = dao.observarTodo().first()
        assertEquals(100L, lista.first().diaEpoch)
        dao.borrarUno(id1)
        assertEquals(1, dao.total())
    }

    @Test
    fun borrar_todo_deja_la_base_vacia() = runBlocking {
        base.perfilDao().guardar(PerfilEntity(1, "Ada", 0, 10, 0L, true))
        base.animoDao().insertar(AnimoEntity(0, 1L, "Miedo", 4, "", 0L))
        base.perfilDao().borrar()
        base.animoDao().borrar()
        assertNull(base.perfilDao().obtener())
        assertEquals(0, base.animoDao().total())
    }
}
