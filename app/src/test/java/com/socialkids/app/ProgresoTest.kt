package com.socialkids.app

import com.socialkids.app.data.seed.CartasSeed
import com.socialkids.app.data.seed.MundoSeed
import com.socialkids.app.data.seed.RetosConflicto
import com.socialkids.app.data.seed.RetosEscucha
import com.socialkids.app.data.seed.RetosMensaje
import com.socialkids.app.data.seed.RetosPuente
import com.socialkids.app.data.seed.RetosRostro
import com.socialkids.app.data.seed.RetosTermometro
import com.socialkids.app.domain.model.EstadoMision
import com.socialkids.app.domain.model.Mecanica
import com.socialkids.app.domain.usecase.DesbloqueoEvaluador
import com.socialkids.app.domain.usecase.EstadisticasCalculadora
import com.socialkids.app.domain.usecase.EstadisticasJugador
import com.socialkids.app.domain.usecase.InsigniaEvaluador
import com.socialkids.app.domain.usecase.ProgresoCalculadora
import com.socialkids.app.domain.usecase.ProgresoMision
import com.socialkids.app.domain.usecase.RachaCalculadora
import com.socialkids.app.domain.usecase.RegistroAnimo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgresoCalculadoraTest {

    @Test
    fun `el nivel 1 empieza en cero xp`() {
        assertEquals(1, ProgresoCalculadora.nivelDeXp(0))
        assertEquals(0, ProgresoCalculadora.xpAcumuladaParaNivel(1))
    }

    @Test
    fun `subir de nivel cuesta cada vez un poco mas`() {
        assertEquals(100, ProgresoCalculadora.xpParaSubir(1))
        assertEquals(150, ProgresoCalculadora.xpParaSubir(2))
        assertEquals(200, ProgresoCalculadora.xpParaSubir(3))
    }

    @Test
    fun `la xp acumulada corresponde con los saltos de nivel`() {
        assertEquals(100, ProgresoCalculadora.xpAcumuladaParaNivel(2))
        assertEquals(250, ProgresoCalculadora.xpAcumuladaParaNivel(3))
        assertEquals(2, ProgresoCalculadora.nivelDeXp(100))
        assertEquals(1, ProgresoCalculadora.nivelDeXp(99))
        assertEquals(3, ProgresoCalculadora.nivelDeXp(250))
    }

    @Test
    fun `el progreso dentro del nivel va de 0 a 1`() {
        assertEquals(0f, ProgresoCalculadora.progresoEnNivel(0))
        assertEquals(0.5f, ProgresoCalculadora.progresoEnNivel(50))
        assertEquals(0f, ProgresoCalculadora.progresoEnNivel(100))
    }

    @Test
    fun `una xp negativa no rompe el calculo`() {
        assertEquals(1, ProgresoCalculadora.nivelDeXp(-500))
        assertTrue(ProgresoCalculadora.progresoEnNivel(-500) in 0f..1f)
    }

    @Test
    fun `el nivel maximo no se supera`() {
        assertEquals(ProgresoCalculadora.NIVEL_MAXIMO, ProgresoCalculadora.nivelDeXp(1_000_000))
        assertEquals(0, ProgresoCalculadora.xpRestanteParaSiguienteNivel(1_000_000))
    }

    @Test
    fun `repetir una mision da menos xp que la primera vez`() {
        val mision = MundoSeed.misiones.first()
        val primera = ProgresoCalculadora.xpGanada(mision, 3, yaCompletada = false)
        val repetida = ProgresoCalculadora.xpGanada(mision, 3, yaCompletada = true)
        assertEquals(mision.xp, primera)
        assertTrue(repetida < primera)
        assertTrue(repetida >= 3)
    }

    @Test
    fun `cero estrellas sigue dando algo de xp`() {
        val mision = MundoSeed.misiones.first()
        assertTrue(ProgresoCalculadora.xpGanada(mision, 0, false) > 0)
    }
}

class DesbloqueoEvaluadorTest {

    private val faro = MundoSeed.misionesDe(com.socialkids.app.domain.model.ZonaId.FARO)

    @Test
    fun `la primera zona esta abierta desde el principio`() {
        assertTrue(DesbloqueoEvaluador.zonaDesbloqueada(MundoSeed.zonas.first(), 0))
        assertFalse(DesbloqueoEvaluador.zonaDesbloqueada(MundoSeed.zonas.last(), 0))
    }

    @Test
    fun `la primera mision de una zona abierta esta disponible`() {
        val estado = DesbloqueoEvaluador.estadoMision(faro[0], true, emptyMap(), faro)
        assertEquals(EstadoMision.DISPONIBLE, estado)
    }

    @Test
    fun `la segunda mision esta bloqueada hasta completar la primera`() {
        assertEquals(
            EstadoMision.BLOQUEADA,
            DesbloqueoEvaluador.estadoMision(faro[1], true, emptyMap(), faro)
        )
        val progreso = mapOf(faro[0].id to ProgresoMision(faro[0].id, 2, 1, true))
        assertEquals(
            EstadoMision.DISPONIBLE,
            DesbloqueoEvaluador.estadoMision(faro[1], true, progreso, faro)
        )
    }

    @Test
    fun `tres estrellas marcan la mision como dominada`() {
        val progreso = mapOf(faro[0].id to ProgresoMision(faro[0].id, 3, 2, true))
        assertEquals(
            EstadoMision.DOMINADA,
            DesbloqueoEvaluador.estadoMision(faro[0], true, progreso, faro)
        )
    }

    @Test
    fun `una mision intentada sin completar queda como iniciada`() {
        val progreso = mapOf(faro[0].id to ProgresoMision(faro[0].id, 0, 1, false))
        assertEquals(
            EstadoMision.INICIADA,
            DesbloqueoEvaluador.estadoMision(faro[0], true, progreso, faro)
        )
    }

    @Test
    fun `el porcentaje de zona refleja las misiones completadas`() {
        val progreso = faro.take(2).associate { it.id to ProgresoMision(it.id, 2, 1, true) }
        assertEquals(0.5f, DesbloqueoEvaluador.porcentajeZona(faro, progreso))
        assertFalse(DesbloqueoEvaluador.zonaCompletada(faro, progreso))
    }

    @Test
    fun `la siguiente mision sugerida es la primera disponible`() {
        val sugerida = DesbloqueoEvaluador.siguienteMision(
            MundoSeed.zonas, MundoSeed.misiones, emptyMap(), 0
        )
        assertEquals(faro[0].id, sugerida?.id)
    }

    @Test
    fun `el repaso solo propone misiones completadas con menos de tres estrellas`() {
        val progreso = mapOf(
            faro[0].id to ProgresoMision(faro[0].id, 3, 1, true),
            faro[1].id to ProgresoMision(faro[1].id, 1, 1, true),
            faro[2].id to ProgresoMision(faro[2].id, 0, 2, false)
        )
        val repaso = DesbloqueoEvaluador.misionesDeRepaso(MundoSeed.misiones, progreso)
        assertEquals(1, repaso.size)
        assertEquals(faro[1].id, repaso.first().id)
    }
}

class InsigniaEvaluadorTest {

    @Test
    fun `sin actividad no hay ninguna insignia`() {
        assertTrue(InsigniaEvaluador.conseguidas(EstadisticasJugador()).isEmpty())
    }

    @Test
    fun `la primera mision otorga la insignia de primer paso`() {
        val stats = EstadisticasJugador(misionesCompletadas = 1)
        val ids = InsigniaEvaluador.conseguidas(stats).map { it.id }
        assertTrue(ids.contains("ins_primer_paso"))
    }

    @Test
    fun `las insignias nuevas se calculan comparando dos fotos`() {
        val antes = EstadisticasJugador(misionesCompletadas = 4)
        val ahora = EstadisticasJugador(misionesCompletadas = 5)
        val nuevas = InsigniaEvaluador.nuevas(antes, ahora).map { it.id }
        assertTrue(nuevas.contains("ins_explorador"))
        assertFalse(nuevas.contains("ins_primer_paso"))
    }

    @Test
    fun `el progreso de una insignia esta acotado entre 0 y 1`() {
        val regla = InsigniaEvaluador.regla("ins_coleccion")!!
        assertEquals(0f, regla.progreso(EstadisticasJugador()))
        assertEquals(1f, regla.progreso(EstadisticasJugador(cartasDesbloqueadas = 99)))
        assertEquals(0.5f, regla.progreso(EstadisticasJugador(cartasDesbloqueadas = 6)))
    }

    @Test
    fun `el siguiente objetivo es la insignia mas cercana`() {
        val stats = EstadisticasJugador(misionesCompletadas = 4, cartasDesbloqueadas = 1)
        val objetivo = InsigniaEvaluador.siguienteObjetivo(stats)
        assertNotNull(objetivo)
        assertEquals("ins_explorador", objetivo!!.insignia.id)
    }

    @Test
    fun `todas las insignias tienen id unico y pista`() {
        val ids = InsigniaEvaluador.todas().map { it.id }
        assertEquals(ids.size, ids.toSet().size)
        InsigniaEvaluador.todas().forEach {
            assertTrue(it.pista.isNotBlank())
            assertTrue(it.descripcion.isNotBlank())
        }
    }

    @Test
    fun `completar la isla entera otorga la insignia final`() {
        val stats = EstadisticasJugador(zonasCompletadas = 6)
        assertTrue(InsigniaEvaluador.conseguidas(stats).map { it.id }.contains("ins_isla"))
    }
}

class EstadisticasTest {

    @Test
    fun `un diario vacio devuelve un resumen en cero`() {
        val r = EstadisticasCalculadora.resumen(emptyList(), 100)
        assertEquals(0, r.total)
        assertNull(r.emocionFrecuente)
        assertEquals(7, r.ultimosSieteDias.size)
        assertTrue(r.ultimosSieteDias.all { it == 0 })
    }

    @Test
    fun `la media de intensidad se calcula con un decimal`() {
        val registros = listOf(
            RegistroAnimo(1, 100, "Alegria", 8, ""),
            RegistroAnimo(2, 100, "Enfado", 5, "")
        )
        assertEquals(6.5, EstadisticasCalculadora.resumen(registros, 100).intensidadMedia, 0.001)
    }

    @Test
    fun `la emocion mas frecuente se detecta bien`() {
        val registros = listOf(
            RegistroAnimo(1, 100, "Alegria", 8, ""),
            RegistroAnimo(2, 99, "Alegria", 6, ""),
            RegistroAnimo(3, 98, "Enfado", 5, "")
        )
        val r = EstadisticasCalculadora.resumen(registros, 100)
        assertEquals("Alegria", r.emocionFrecuente)
        assertEquals(2, r.conteoPorEmocion["Alegria"])
        assertEquals(3, r.diasRegistrados)
    }

    @Test
    fun `la serie de siete dias coloca cada registro en su dia`() {
        val registros = listOf(RegistroAnimo(1, 100, "Calma", 6, ""))
        val serie = EstadisticasCalculadora.resumen(registros, 100).ultimosSieteDias
        assertEquals(6, serie.last())
        assertEquals(0, serie.first())
    }

    @Test
    fun `varios registros el mismo dia se promedian`() {
        val registros = listOf(
            RegistroAnimo(1, 100, "Calma", 4, ""),
            RegistroAnimo(2, 100, "Calma", 8, "")
        )
        assertEquals(6, EstadisticasCalculadora.resumen(registros, 100).ultimosSieteDias.last())
    }

    @Test
    fun `la racha cuenta dias consecutivos hasta hoy`() {
        assertEquals(3, RachaCalculadora.rachaActual(listOf(100L, 99L, 98L), 100))
        assertEquals(0, RachaCalculadora.rachaActual(listOf(97L, 96L), 100))
        assertEquals(2, RachaCalculadora.rachaActual(listOf(99L, 98L), 100))
    }

    @Test
    fun `la mejor racha encuentra el tramo mas largo`() {
        assertEquals(3, RachaCalculadora.mejorRacha(listOf(1L, 2L, 3L, 10L, 11L)))
        assertEquals(0, RachaCalculadora.mejorRacha(emptyList()))
        assertEquals(1, RachaCalculadora.mejorRacha(listOf(5L)))
    }
}

class ContenidoTest {

    @Test
    fun `hay seis zonas y veinticuatro misiones`() {
        assertEquals(6, MundoSeed.zonas.size)
        assertEquals(24, MundoSeed.misiones.size)
    }

    @Test
    fun `cada zona tiene cuatro misiones ordenadas`() {
        MundoSeed.zonas.forEach { zona ->
            val misiones = MundoSeed.misionesDe(zona.id)
            assertEquals("Zona ${zona.id}", 4, misiones.size)
            assertEquals(listOf(1, 2, 3, 4), misiones.map { it.orden })
        }
    }

    @Test
    fun `los identificadores de mision y carta son unicos`() {
        val idsMision = MundoSeed.misiones.map { it.id }
        assertEquals(idsMision.size, idsMision.toSet().size)
        val idsCarta = CartasSeed.cartas.map { it.id }
        assertEquals(idsCarta.size, idsCarta.toSet().size)
    }

    @Test
    fun `cada mision apunta a una carta que existe`() {
        MundoSeed.misiones.forEach { mision ->
            val cartaId = mision.cartaId
            assertNotNull("Mision ${mision.id} sin carta", cartaId)
            assertNotNull("Carta $cartaId inexistente", CartasSeed.carta(cartaId!!))
        }
    }

    @Test
    fun `las zonas se desbloquean en orden creciente de xp`() {
        val ordenadas = MundoSeed.zonas.sortedBy { it.orden }
        ordenadas.zipWithNext().forEach { (a, b) ->
            assertTrue("${a.id} deberia costar menos que ${b.id}", a.xpNecesaria < b.xpNecesaria)
        }
        assertEquals(0, ordenadas.first().xpNecesaria)
    }

    @Test
    fun `la xp de la isla alcanza para abrir todas las zonas`() {
        assertTrue(MundoSeed.xpTotalPosible > MundoSeed.zonas.maxOf { it.xpNecesaria })
    }

    @Test
    fun `las opciones multiples no superan la mitad de las mecanicas`() {
        val total = MundoSeed.misiones.size
        val manipulativas = MundoSeed.misiones.count {
            it.mecanica in listOf(
                Mecanica.ROSTROS, Mecanica.PUENTE, Mecanica.MENSAJE, Mecanica.TERMOMETRO
            )
        }
        assertTrue("Deben predominar las mecanicas manipulativas", manipulativas > total / 2)
    }

    @Test
    fun `cada mision tiene contenido cargado para su mecanica`() {
        MundoSeed.misiones.forEach { mision ->
            when (mision.mecanica) {
                Mecanica.ROSTROS -> assertTrue(RetosRostro.objetivo(mision.id).emocion.isNotBlank())
                Mecanica.ESCUCHA -> assertTrue(RetosEscucha.reto(mision.id).detalles.size >= 5)
                Mecanica.PUENTE -> assertTrue(RetosPuente.reto(mision.id).piezas.size >= 6)
                Mecanica.MENSAJE -> assertEquals(12, RetosMensaje.reto(mision.id).fichas.size)
                Mecanica.CONFLICTO -> assertTrue(RetosConflicto.reto(mision.id).nodos.size >= 4)
                Mecanica.TERMOMETRO -> assertTrue(RetosTermometro.reto(mision.id).estrategias.size >= 4)
            }
        }
    }

    @Test
    fun `hay ocho avatares distintos`() {
        assertEquals(8, CartasSeed.avatares.size)
        assertEquals(8, CartasSeed.avatares.map { it.accesorio }.toSet().size)
    }

    @Test
    fun `todos los textos de mision estan en espaniol y son breves`() {
        MundoSeed.misiones.forEach {
            assertTrue("Titulo largo en ${it.id}", it.titulo.length <= 40)
            assertTrue("Consigna vacia en ${it.id}", it.consigna.isNotBlank())
            assertTrue("Consigna larga en ${it.id}", it.consigna.length <= 160)
        }
    }
}
