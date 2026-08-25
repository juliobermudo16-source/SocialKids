package com.socialkids.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.socialkids.app.data.seed.CartasSeed
import com.socialkids.app.ui.art.Avatar
import com.socialkids.app.ui.components.BocadilloNima
import com.socialkids.app.ui.components.BotonGrande
import com.socialkids.app.ui.components.Cabecera
import com.socialkids.app.ui.art.EstadoNima
import com.socialkids.app.ui.theme.Paleta

/**
 * Creacion del perfil: apodo y avatar. Nunca se pide el nombre real
 * ni ningun otro dato personal.
 */
@Composable
fun PantallaCrearPerfil(
    aliasInicial: String,
    avatarInicial: Int,
    esEdicion: Boolean,
    alGuardar: (String, Int) -> Unit,
    alVolver: (() -> Unit)?
) {
    var alias by rememberSaveable { mutableStateOf(aliasInicial) }
    var avatar by rememberSaveable { mutableIntStateOf(avatarInicial) }
    val aliasValido = alias.trim().length in 2..16

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Cabecera(
            titulo = if (esEdicion) "Cambia tu explorador" else "Crea tu explorador",
            subtitulo = "Elige un apodo y una cara",
            alVolver = alVolver
        )

        Column(Modifier.padding(horizontal = 20.dp)) {
            BocadilloNima(
                texto = "No hace falta tu nombre de verdad. Un apodo vale: asi nadie sabe quien eres fuera de la isla.",
                estado = EstadoNima.NEUTRAL
            )
            Spacer(Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .height(96.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Paleta.tono(avatar).copy(alpha = 0.18f))
                ) {
                    Avatar(avatarId = avatar, modifier = Modifier.fillMaxSize())
                }
                Spacer(Modifier.padding(horizontal = 8.dp))
                Column(Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = alias,
                        onValueChange = { if (it.length <= 16) alias = it },
                        label = { Text("Tu apodo") },
                        singleLine = true,
                        isError = alias.isNotEmpty() && !aliasValido,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = if (alias.isNotEmpty() && !aliasValido) {
                            "Entre 2 y 16 letras"
                        } else {
                            "${CartasSeed.avatar(avatar).nombre} - ${alias.trim().length}/16"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (alias.isNotEmpty() && !aliasValido) Paleta.Error
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(
            "Elige tu avatar",
            modifier = Modifier.padding(horizontal = 20.dp),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(CartasSeed.avatares, key = { it.id }) { spec ->
                val elegido = spec.id == avatar
                val escala by animateFloatAsState(if (elegido) 1.06f else 1f, tween(200), label = "av${spec.id}")
                Surface(
                    modifier = Modifier
                        .aspectRatio(0.85f)
                        .scale(escala)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { avatar = spec.id }
                        .border(
                            width = if (elegido) 3.dp else 0.dp,
                            color = if (elegido) Paleta.tono(spec.tono) else androidx.compose.ui.graphics.Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = if (elegido) 6.dp else 2.dp
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Avatar(
                            avatarId = spec.id,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        )
                        Text(
                            spec.nombre,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
            }
        }

        BotonGrande(
            texto = if (esEdicion) "Guardar cambios" else "Entrar a la isla",
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            color = Paleta.Turquesa,
            habilitado = aliasValido
        ) {
            alGuardar(alias.trim(), avatar)
        }
    }
}
