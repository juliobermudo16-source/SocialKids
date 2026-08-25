package com.socialkids.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.socialkids.app.domain.model.ZonaId
import com.socialkids.app.ui.JuegoViewModel
import com.socialkids.app.ui.screens.PantallaAjustes
import com.socialkids.app.ui.screens.PantallaCalma
import com.socialkids.app.ui.screens.PantallaColeccion
import com.socialkids.app.ui.screens.PantallaCrearPerfil
import com.socialkids.app.ui.screens.PantallaDiario
import com.socialkids.app.ui.screens.PantallaEstadisticas
import com.socialkids.app.ui.screens.PantallaInsignias
import com.socialkids.app.ui.screens.PantallaMapa
import com.socialkids.app.ui.screens.PantallaMision
import com.socialkids.app.ui.screens.PantallaOnboarding
import com.socialkids.app.ui.screens.PantallaPerfil
import com.socialkids.app.ui.screens.PantallaPortada
import com.socialkids.app.ui.screens.PantallaRepaso
import com.socialkids.app.ui.screens.PantallaZona

object Rutas {
    const val PORTADA = "portada"
    const val ONBOARDING = "onboarding"
    const val CREAR_PERFIL = "crear_perfil"
    const val MAPA = "mapa"
    const val ZONA = "zona/{zonaId}"
    const val MISION = "mision/{misionId}"
    const val COLECCION = "coleccion"
    const val INSIGNIAS = "insignias"
    const val DIARIO = "diario"
    const val ESTADISTICAS = "estadisticas"
    const val CALMA = "calma"
    const val AJUSTES = "ajustes"
    const val PERFIL = "perfil"
    const val REPASO = "repaso"

    fun zona(id: ZonaId) = "zona/${id.name}"
    fun mision(id: String) = "mision/$id"
}

@Composable
fun NavegacionSocialKids(
    juegoVM: JuegoViewModel,
    navController: NavHostController = rememberNavController()
) {
    val estado by juegoVM.estado.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Rutas.PORTADA,
        enterTransition = { slideInHorizontally(tween(280)) { it / 6 } + fadeIn(tween(280)) },
        exitTransition = { fadeOut(tween(180)) },
        popEnterTransition = { fadeIn(tween(220)) },
        popExitTransition = { slideOutHorizontally(tween(240)) { it / 6 } + fadeOut(tween(200)) }
    ) {
        composable(Rutas.PORTADA) {
            PantallaPortada(
                estado = estado,
                alEntrar = {
                    val destino = when {
                        estado.perfil == null -> Rutas.ONBOARDING
                        else -> Rutas.MAPA
                    }
                    juegoVM.registrarVisita()
                    navController.navigate(destino) {
                        popUpTo(Rutas.PORTADA) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.ONBOARDING) {
            PantallaOnboarding(
                alTerminar = { navController.navigate(Rutas.CREAR_PERFIL) }
            )
        }

        composable(Rutas.CREAR_PERFIL) {
            PantallaCrearPerfil(
                aliasInicial = estado.perfil?.alias ?: "",
                avatarInicial = estado.perfil?.avatarId ?: 0,
                esEdicion = estado.perfil != null,
                alGuardar = { alias, avatar ->
                    if (estado.perfil == null) juegoVM.crearPerfil(alias, avatar)
                    else juegoVM.actualizarIdentidad(alias, avatar)
                    navController.navigate(Rutas.MAPA) {
                        popUpTo(Rutas.PORTADA) { inclusive = true }
                    }
                },
                alVolver = if (estado.perfil != null) ({ navController.popBackStack() }) else null
            )
        }

        composable(Rutas.MAPA) {
            PantallaMapa(
                juegoVM = juegoVM,
                alAbrirZona = { navController.navigate(Rutas.zona(it)) },
                alAbrirMision = { navController.navigate(Rutas.mision(it)) },
                alAbrirRuta = { navController.navigate(it) }
            )
        }

        composable(
            route = Rutas.ZONA,
            arguments = listOf(navArgument("zonaId") { type = NavType.StringType })
        ) { entrada ->
            val zonaId = runCatching {
                ZonaId.valueOf(entrada.arguments?.getString("zonaId") ?: ZonaId.FARO.name)
            }.getOrDefault(ZonaId.FARO)
            PantallaZona(
                zonaId = zonaId,
                juegoVM = juegoVM,
                alAbrirMision = { navController.navigate(Rutas.mision(it)) },
                alVolver = { navController.popBackStack() }
            )
        }

        composable(
            route = Rutas.MISION,
            arguments = listOf(navArgument("misionId") { type = NavType.StringType })
        ) { entrada ->
            val misionId = entrada.arguments?.getString("misionId").orEmpty()
            PantallaMision(
                misionId = misionId,
                juegoVM = juegoVM,
                alSalir = { navController.popBackStack() },
                alIrAColeccion = {
                    navController.navigate(Rutas.COLECCION)
                }
            )
        }

        composable(Rutas.COLECCION) {
            PantallaColeccion(juegoVM = juegoVM, alVolver = { navController.popBackStack() })
        }
        composable(Rutas.INSIGNIAS) {
            PantallaInsignias(juegoVM = juegoVM, alVolver = { navController.popBackStack() })
        }
        composable(Rutas.DIARIO) {
            PantallaDiario(
                juegoVM = juegoVM,
                alVolver = { navController.popBackStack() },
                alIrACalma = { navController.navigate(Rutas.CALMA) }
            )
        }
        composable(Rutas.ESTADISTICAS) {
            PantallaEstadisticas(juegoVM = juegoVM, alVolver = { navController.popBackStack() })
        }
        composable(Rutas.CALMA) {
            PantallaCalma(alVolver = { navController.popBackStack() })
        }
        composable(Rutas.AJUSTES) {
            PantallaAjustes(
                juegoVM = juegoVM,
                alVolver = { navController.popBackStack() },
                alReiniciar = {
                    juegoVM.reiniciarProgreso()
                    navController.navigate(Rutas.PORTADA) {
                        popUpTo(Rutas.MAPA) { inclusive = true }
                    }
                }
            )
        }
        composable(Rutas.PERFIL) {
            PantallaPerfil(
                juegoVM = juegoVM,
                alVolver = { navController.popBackStack() },
                alEditar = { navController.navigate(Rutas.CREAR_PERFIL) },
                alAbrirRuta = { navController.navigate(it) }
            )
        }
        composable(Rutas.REPASO) {
            PantallaRepaso(
                juegoVM = juegoVM,
                alVolver = { navController.popBackStack() },
                alAbrirMision = { navController.navigate(Rutas.mision(it)) }
            )
        }
    }
}
