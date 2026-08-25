package com.socialkids.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.socialkids.app.ui.JuegoViewModel
import com.socialkids.app.ui.components.CapaArrastre
import com.socialkids.app.ui.navigation.NavegacionSocialKids
import com.socialkids.app.ui.theme.SocialKidsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val juegoVM: JuegoViewModel = viewModel(factory = JuegoViewModel.Factory)
            val ajustes by juegoVM.ajustes.collectAsStateWithLifecycle()

            SocialKidsTheme(ajustes = ajustes) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    CapaArrastre(modifier = Modifier.fillMaxSize()) {
                        NavegacionSocialKids(juegoVM)
                    }
                }
            }
        }
    }
}
