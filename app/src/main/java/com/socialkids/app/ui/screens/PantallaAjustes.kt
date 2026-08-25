package com.socialkids.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.socialkids.app.ui.JuegoViewModel
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.components.BocadilloNima
import com.socialkids.app.ui.components.BotonSuave
import com.socialkids.app.ui.components.Cabecera
import com.socialkids.app.ui.components.TituloSeccion
import com.socialkids.app.ui.theme.Paleta

/** Ajustes de confort, accesibilidad y privacidad. Todo se puede desactivar. */
@Composable
fun PantallaAjustes(
    juegoVM: JuegoViewModel,
    alVolver: () -> Unit,
    alReiniciar: () -> Unit
) {
    val ajustes by juegoVM.ajustes.collectAsStateWithLifecycle()
    var confirmar by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Cabecera(
            titulo = "Ajustes",
            subtitulo = "Tu decides como suena y como se ve",
            alVolver = alVolver,
            modifier = Modifier.padding(top = 28.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { TituloSeccion("Sonido y vibracion", color = Paleta.Turquesa) }
            item {
                FilaAjuste(
                    "Efectos de sonido",
                    "Sonidos cortos al acertar y desbloquear",
                    ajustes.sonido
                ) { juegoVM.cambiarSonido(it) }
            }
            item {
                FilaAjuste(
                    "Vibracion",
                    "Toques suaves al pulsar y al conseguir algo",
                    ajustes.vibracion
                ) { juegoVM.cambiarVibracion(it) }
            }

            item { Spacer(Modifier.height(4.dp)) }
            item { TituloSeccion("Accesibilidad", color = Paleta.Violeta) }
            item {
                FilaAjuste(
                    "Animaciones",
                    "Desactivalo si el movimiento te molesta",
                    ajustes.animaciones
                ) { juegoVM.cambiarAnimaciones(it) }
            }
            item {
                FilaAjuste(
                    "Texto mas grande",
                    "Aumenta el tamanio de todas las letras",
                    ajustes.textoGrande
                ) { juegoVM.cambiarTextoGrande(it) }
            }
            item {
                FilaAjuste(
                    "Alto contraste",
                    "Fondos planos y colores mas marcados",
                    ajustes.altoContraste
                ) { juegoVM.cambiarAltoContraste(it) }
            }

            item { Spacer(Modifier.height(4.dp)) }
            item { TituloSeccion("Privacidad", color = Paleta.Menta) }
            item {
                Surface(
                    color = Paleta.Menta.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Que datos guarda SocialKids", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "Solo tu apodo, tu avatar y tu progreso, y todo se queda dentro de este movil.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "No pide internet, no tiene cuentas, no muestra anuncios y no envia nada a ningun servidor.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "No pide correo, telefono, direccion, ubicacion, contactos, camara ni microfono.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(4.dp)) }
            item { TituloSeccion("Datos", color = Paleta.Coral) }
            item {
                BotonSuave(
                    texto = "Borrar mi progreso",
                    icono = Icons.Filled.Delete,
                    modifier = Modifier.fillMaxWidth()
                ) { confirmar = true }
            }

            item {
                Spacer(Modifier.height(8.dp))
                BocadilloNima(
                    texto = "SocialKids v1.0.0 - Isla Conecta. Hecho para jugar entre 5 y 20 minutos al dia.",
                    estado = EstadoNima.NEUTRAL,
                    tamanioNima = 64.dp
                )
            }
        }
    }

    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text("Borrar todo tu progreso?") },
            text = {
                Text(
                    "Se borran tu perfil, tus cartas, tus insignias y tu diario. Esto no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmar = false
                    alReiniciar()
                }) { Text("Si, borrar", color = Paleta.Error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmar = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun FilaAjuste(
    titulo: String,
    detalle: String,
    valor: Boolean,
    alCambiar: (Boolean) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleSmall)
                Text(
                    detalle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = valor, onCheckedChange = alCambiar)
        }
    }
}
