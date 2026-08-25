package com.socialkids.app

import android.app.Application
import android.content.Context
import com.socialkids.app.data.local.SocialKidsDatabase
import com.socialkids.app.data.repository.AjustesRepository
import com.socialkids.app.data.repository.JuegoRepository
import com.socialkids.app.util.RelojSistema

/**
 * Contenedor de dependencias manual. La app es pequenia y offline,
 * asi que no necesita un framework de inyeccion.
 */
class Contenedor(context: Context) {
    private val base = SocialKidsDatabase.obtener(context)

    val juegoRepository: JuegoRepository = JuegoRepository(
        perfilDao = base.perfilDao(),
        progresoDao = base.progresoDao(),
        intentoDao = base.intentoDao(),
        coleccionDao = base.coleccionDao(),
        animoDao = base.animoDao(),
        visitaDao = base.visitaDao(),
        reloj = RelojSistema
    )

    val ajustesRepository: AjustesRepository = AjustesRepository(context)
}

class SocialKidsApp : Application() {
    lateinit var contenedor: Contenedor
        private set

    override fun onCreate() {
        super.onCreate()
        contenedor = Contenedor(this)
    }
}
