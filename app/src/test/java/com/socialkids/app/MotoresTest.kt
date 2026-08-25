package com.socialkids.app

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
import com.socialkids.app.domain.engine.EstadoConflicto
import com.socialkids.app.domain.engine.Estilo
import com.socialkids.app.domain.engine.Estrategia
import com.socialkids.app.domain.engine.Ficha
import com.socialkids.app.domain.engine.MensajeEngine
import com.socialkids.app.domain.engine.OpcionConflicto
import com.socialkids.app.domain.engine.PiezaPuente
import com.socialkids.app.domain.engine.PuenteEngine
import com.socialkids.app.domain.engine.Ranura
import com.socialkids.app.domain.engine.RasgoExtra
import com.socialkids.app.domain.engine.RostroConfig
import com.socialkids.app.domain.engine.RostroEngine
import com.socialkids.app.domain.engine.Tablon
import com.socialkids.app.domain.engine.TermometroEngine
import com.socialkids.app.domain.engine.TipoRespuesta
import com.socialkids.app.domain.model.Estrellas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RostroEngineTest {

    private val objetivo = RetosRostro.objetivo("m_faro_1")

    @Test
    fun `rostro identico al objetivo puntua el maximo`() {
        val config = RostroConfig(
            objetivo.cejas, objetivo.ojos, objetivo.boca, objetivo.energia, objetivo.extras
        )
        assertEquals(100, RostroEngine.puntaje(config, objetivo))
        assertEquals(3, RostroEngine.evaluar(config, objetivo).estrellas)
    }

    @Test
    fun `rostro opuesto no llega a una estrella`() {
        val config = RostroConfig(
            cejas = 100 - objetivo.cejas,
            ojos = 100 - objetivo.ojos,
            boca = 100 - objetivo.boca,
            energia = 100 - objetivo.energia,
            extras = emptySet()
        )
        assertTrue(RostroEngine.puntaje(config, objetivo) < 45)
    }

    @Test
    fun `el puntaje por eje cae a cero fuera de la tolerancia`() {
        assertEquals(100, RostroEngine.puntajeEje(50, 50))
        assertEquals(0, RostroEngine.puntajeEje(0, 100))
        assertTrue(RostroEngine.puntajeEje(50, 70) in 60..70)
    }

    @Test
    fun `los rasgos sobrantes restan puntos`() {
        val base = RostroConfig(objetivo.cejas, objetivo.ojos, objetivo.boca, objetivo.energia, objetivo.extras)
        val conSobrante = base.alternar(RasgoExtra.SUDOR)
        assertTrue(RostroEngine.puntaje(conSobrante, objetivo) < RostroEngine.puntaje(base, objetivo))
    }

    @Test
    fun `el eje mas lejano se identifica correctamente`() {
        val config = RostroConfig(
            cejas = objetivo.cejas,
            ojos = objetivo.ojos,
            boca = 0,
            energia = objetivo.energia
        )
        assertEquals(EjeRostro.BOCA, RostroEngine.ejeMasLejano(config, objetivo))
    }

    @Test
    fun `mover un eje respeta los limites 0 y 100`() {
        val c = RostroConfig().conEje(EjeRostro.OJOS, 500).conEje(EjeRostro.BOCA, -80)
        assertEquals(100, c.ojos)
        assertEquals(0, c.boca)
    }

    @Test
    fun `el puntaje nunca sale del rango 0 a 100`() {
        val extremo = RostroConfig(0, 0, 0, 0, RasgoExtra.entries.toSet())
        val p = RostroEngine.puntaje(extremo, objetivo)
        assertTrue(p in 0..100)
    }
}

class EscuchaEngineTest {

    private val reto = RetosEscucha.reto("m_bosque_1")
    private val buena = reto.opciones.first { it.tipo == TipoRespuesta.PARAFRASEO }
    private val mala = reto.opciones.first { it.tipo == TipoRespuesta.JUICIO }

    @Test
    fun `seleccion perfecta y respuesta reflejo dan tres estrellas`() {
        val r = EscuchaEngine.evaluar(reto.detallesReales, reto, buena)
        assertEquals(3, r.estrellas)
        assertEquals(100, EscuchaEngine.puntajeMemoria(reto.detallesReales, reto.detallesReales))
    }

    @Test
    fun `marcar datos inventados baja la memoria`() {
        val inventado = reto.detalles.first { !it.esReal }.id
        val conRuido = reto.detallesReales + inventado
        assertTrue(
            EscuchaEngine.puntajeMemoria(conRuido, reto.detallesReales) <
                EscuchaEngine.puntajeMemoria(reto.detallesReales, reto.detallesReales)
        )
        assertEquals(1, EscuchaEngine.inventados(conRuido, reto.detallesReales))
    }

    @Test
    fun `no seleccionar nada puntua cero de memoria`() {
        assertEquals(0, EscuchaEngine.puntajeMemoria(emptySet(), reto.detallesReales))
    }

    @Test
    fun `una respuesta que juzga hunde el resultado aunque la memoria sea perfecta`() {
        val conBuena = EscuchaEngine.puntaje(reto.detallesReales, reto, buena)
        val conMala = EscuchaEngine.puntaje(reto.detallesReales, reto, mala)
        assertTrue(conMala < conBuena)
        assertTrue(conMala < 70)
    }

    @Test
    fun `los detalles olvidados se cuentan bien`() {
        val parcial = reto.detallesReales.take(2).toSet()
        assertEquals(reto.detallesReales.size - 2, EscuchaEngine.olvidados(parcial, reto.detallesReales))
    }

    @Test
    fun `el resultado siempre trae explicacion y consejo`() {
        val r = EscuchaEngine.evaluar(emptySet(), reto, mala)
        assertTrue(r.explicacion.isNotBlank())
        assertTrue(r.consejo.isNotBlank())
    }
}

class PuenteEngineTest {

    private val reto = RetosPuente.reto("m_puente_1")

    private fun colocacionPerfecta(): Map<Tablon, PiezaPuente?> =
        Tablon.entries.associateWith { t -> reto.piezas.first { it.tablon == t } }

    @Test
    fun `las tres piezas correctas dan puente firme`() {
        val c = colocacionPerfecta()
        assertEquals(3, PuenteEngine.tablonesCorrectos(c))
        assertEquals(100, PuenteEngine.puntaje(c))
        assertEquals(3, PuenteEngine.evaluar(c, reto).estrellas)
        assertEquals(1f, PuenteEngine.estabilidad(c))
    }

    @Test
    fun `una pieza distractora resta`() {
        val distractora = reto.piezas.first { it.tablon == null }
        val c = colocacionPerfecta().toMutableMap()
        c[Tablon.SIENTE] = distractora
        assertEquals(2, PuenteEngine.tablonesCorrectos(c))
        assertTrue(PuenteEngine.puntaje(c) < 100)
    }

    @Test
    fun `el puente vacio no esta completo y puntua cero`() {
        val vacio = Tablon.entries.associateWith { null }
        assertFalse(PuenteEngine.completo(vacio))
        assertEquals(0, PuenteEngine.puntaje(vacio))
    }

    @Test
    fun `piezas intercambiadas entre tablones no cuentan`() {
        val siente = reto.piezas.first { it.tablon == Tablon.SIENTE }
        val piensa = reto.piezas.first { it.tablon == Tablon.PIENSA }
        val c = mapOf(
            Tablon.SIENTE to piensa,
            Tablon.PIENSA to siente,
            Tablon.NECESITA to reto.piezas.first { it.tablon == Tablon.NECESITA }
        )
        assertEquals(1, PuenteEngine.tablonesCorrectos(c))
    }

    @Test
    fun `todos los retos de puente tienen una pieza por tablon`() {
        listOf("m_bosque_3", "m_puente_1", "m_puente_2", "m_puente_4", "m_mirador_1").forEach { id ->
            val r = RetosPuente.reto(id)
            Tablon.entries.forEach { t ->
                assertEquals("Reto $id tablon $t", 1, r.piezas.count { it.tablon == t })
            }
            assertTrue("Reto $id sin distractoras", r.piezas.any { it.tablon == null })
        }
    }
}

class MensajeEngineTest {

    private val reto = RetosMensaje.reto("m_plaza_1")

    private fun conEstilo(estilo: Estilo): Map<Ranura, Ficha?> =
        Ranura.entries.associateWith { r -> reto.fichas.first { it.ranura == r && it.estilo == estilo } }

    @Test
    fun `mensaje entero asertivo se clasifica como asertivo y puntua alto`() {
        val c = conEstilo(Estilo.ASERTIVO)
        assertEquals(Estilo.ASERTIVO, MensajeEngine.estilo(c))
        assertTrue(MensajeEngine.puntaje(c) >= 88)
        assertTrue(MensajeEngine.completo(c))
    }

    @Test
    fun `una sola ficha agresiva vuelve agresivo todo el mensaje`() {
        val c = conEstilo(Estilo.ASERTIVO).toMutableMap()
        c[Ranura.MOTIVO] = reto.fichas.first { it.ranura == Ranura.MOTIVO && it.estilo == Estilo.AGRESIVO }
        assertEquals(Estilo.AGRESIVO, MensajeEngine.estilo(c))
    }

    @Test
    fun `dos fichas pasivas hacen el mensaje pasivo`() {
        val c = conEstilo(Estilo.ASERTIVO).toMutableMap()
        c[Ranura.SENTIMIENTO] = reto.fichas.first { it.ranura == Ranura.SENTIMIENTO && it.estilo == Estilo.PASIVO }
        c[Ranura.PETICION] = reto.fichas.first { it.ranura == Ranura.PETICION && it.estilo == Estilo.PASIVO }
        assertEquals(Estilo.PASIVO, MensajeEngine.estilo(c))
    }

    @Test
    fun `un mensaje incompleto se detecta y avisa de la parte que falta`() {
        val c = conEstilo(Estilo.ASERTIVO).toMutableMap()
        c[Ranura.PETICION] = null
        val r = MensajeEngine.construir(c)
        assertFalse(r.completo)
        assertTrue(r.observaciones.any { it.contains("Me gustaria") })
    }

    @Test
    fun `la frase se arma en el orden del mensaje yo`() {
        val frase = MensajeEngine.frase(conEstilo(Estilo.ASERTIVO))
        val posSiento = frase.indexOf("Yo me siento")
        val posCuando = frase.indexOf("cuando")
        val posPorque = frase.indexOf("porque")
        assertTrue(posSiento in 0 until posCuando)
        assertTrue(posCuando < posPorque)
        assertTrue(frase.endsWith("."))
    }

    @Test
    fun `la frase vacia no rompe el motor`() {
        val vacio = Ranura.entries.associateWith { null }
        assertEquals("", MensajeEngine.frase(vacio))
        assertEquals(0, MensajeEngine.puntaje(vacio))
    }

    @Test
    fun `todos los retos de mensaje tienen tres fichas por ranura`() {
        listOf("m_plaza_1", "m_plaza_2", "m_plaza_3", "m_taller_2", "m_mirador_3").forEach { id ->
            val r = RetosMensaje.reto(id)
            Ranura.entries.forEach { ranura ->
                assertEquals("Reto $id ranura $ranura", 3, r.fichas.count { it.ranura == ranura })
            }
        }
    }
}

class ConflictoEngineTest {

    private val reto = RetosConflicto.reto("m_taller_1")

    private fun jugar(indiceOpcion: Int): EstadoConflicto {
        var estado = EstadoConflicto(nodoId = reto.nodoInicial)
        var nodo = reto.nodo(estado.nodoId)
        while (nodo != null && nodo.opciones.isNotEmpty()) {
            val opcion = nodo.opciones[minOf(indiceOpcion, nodo.opciones.size - 1)]
            estado = ConflictoEngine.aplicar(estado, opcion)
            nodo = reto.nodo(estado.nodoId)
        }
        return estado
    }

    @Test
    fun `elegir siempre la mejor opcion termina en acuerdo`() {
        val estado = jugar(0)
        assertEquals(Desenlace.ACUERDO, ConflictoEngine.desenlace(estado))
        assertTrue(ConflictoEngine.puntaje(estado) >= 70)
    }

    @Test
    fun `elegir siempre la peor opcion rompe la conversacion`() {
        val estado = jugar(2)
        assertEquals(Desenlace.RUPTURA, ConflictoEngine.desenlace(estado))
    }

    @Test
    fun `las variables se mantienen entre 0 y 100`() {
        val opcion = OpcionConflicto("x", "t", -500, 500, -500, "r", null, "e")
        val estado = ConflictoEngine.aplicar(EstadoConflicto(nodoId = "n1"), opcion)
        assertEquals(0, estado.calma)
        assertEquals(100, estado.confianza)
        assertEquals(0, estado.acuerdo)
    }

    @Test
    fun `cada eleccion avanza un turno`() {
        val nodo = reto.nodo(reto.nodoInicial)!!
        val estado = ConflictoEngine.aplicar(EstadoConflicto(nodoId = reto.nodoInicial), nodo.opciones[0])
        assertEquals(1, estado.turno)
    }

    @Test
    fun `la tension es el complemento de la calma`() {
        val estado = EstadoConflicto(calma = 30, nodoId = "n1")
        assertEquals(70, estado.tension)
    }

    @Test
    fun `el nodo final no tiene opciones y cierra la escena`() {
        val estado = jugar(0)
        assertTrue(ConflictoEngine.terminado(estado, reto))
        assertNotNull(ConflictoEngine.evaluar(estado, reto).explicacion)
    }

    @Test
    fun `todos los retos de conflicto tienen nodo inicial valido y salida`() {
        listOf("m_taller_1", "m_taller_3", "m_taller_4", "m_mirador_2").forEach { id ->
            val r = RetosConflicto.reto(id)
            assertNotNull("Reto $id sin nodo inicial", r.nodo(r.nodoInicial))
            assertTrue("Reto $id sin nodo final", r.nodos.any { it.opciones.isEmpty() })
            r.nodos.flatMap { it.opciones }.forEach { op ->
                val destino = op.siguienteNodo
                if (destino != null) {
                    assertNotNull("Reto $id opcion ${op.id} apunta a nodo inexistente", r.nodo(destino))
                }
            }
        }
    }
}

class TermometroEngineTest {

    private val reto = RetosTermometro.reto("m_faro_2")

    @Test
    fun `medir dentro del margen puntua el maximo de intensidad`() {
        assertEquals(100, TermometroEngine.puntajeIntensidad(reto.intensidadEsperada, reto.intensidadEsperada, reto.margen))
        assertEquals(100, TermometroEngine.puntajeIntensidad(reto.intensidadEsperada + reto.margen, reto.intensidadEsperada, reto.margen))
    }

    @Test
    fun `medir muy lejos puntua cero de intensidad`() {
        assertEquals(0, TermometroEngine.puntajeIntensidad(0, 9, 1))
    }

    @Test
    fun `la estrategia correcta para intensidad alta es la de calma`() {
        val respirar = reto.estrategias.first { it.id == "e_respirar" }
        assertTrue(TermometroEngine.estrategiaAdecuada(respirar, 8))
        assertFalse(TermometroEngine.estrategiaAdecuada(respirar, 2))
    }

    @Test
    fun `medida justa y estrategia adecuada dan tres estrellas`() {
        val respirar = reto.estrategias.first { it.id == "e_respirar" }
        val r = TermometroEngine.evaluar(reto.intensidadEsperada, respirar, reto)
        assertEquals(3, r.estrellas)
    }

    @Test
    fun `una estrategia fuera de franja limita el resultado`() {
        val ignorar = reto.estrategias.first { it.id == "e_ignorar" }
        val r = TermometroEngine.evaluar(reto.intensidadEsperada, ignorar, reto)
        assertTrue(r.puntaje < 88)
        assertTrue(r.consejo.isNotBlank())
    }

    @Test
    fun `una estrategia con rango imposible nunca sirve`() {
        val imposible = Estrategia("x", "Gritar", "", 11, 12)
        (0..10).forEach { assertFalse(imposible.sirvePara(it)) }
    }
}

class EstrellasTest {

    @Test
    fun `los cortes de estrellas son los esperados`() {
        assertEquals(3, Estrellas.de(100))
        assertEquals(3, Estrellas.de(88))
        assertEquals(2, Estrellas.de(87))
        assertEquals(2, Estrellas.de(70))
        assertEquals(1, Estrellas.de(69))
        assertEquals(1, Estrellas.de(45))
        assertEquals(0, Estrellas.de(44))
        assertEquals(0, Estrellas.de(0))
    }
}
