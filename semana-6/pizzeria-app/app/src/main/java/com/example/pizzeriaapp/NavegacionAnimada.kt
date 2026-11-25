package com.example.pizzeriaapp

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import com.example.pizzeriaapp.ui.theme.FondoOscuro
import kotlinx.coroutines.flow.first

// Semana 6: duracion uniforme de las animaciones.
private const val DURACION_ANIMACION = 300

@Composable
fun NavegacionAnimada(
    destino: Int,
    onVolver: () -> Unit = {},
    contenido: @Composable (Int) -> Unit
) {

    // Pantalla actualmente visible.
    var pantallaVisible by remember {
        mutableIntStateOf(destino)
    }

    // Estado de la animacion.
    val visibilidad = remember {
        MutableTransitionState(false)
    }

    // Detecta si existe una transicion en curso.
    val enTransicion =
        !visibilidad.isIdle ||
                !visibilidad.currentState ||
                pantallaVisible != destino

    // Controla el boton Atras de Android.
    BackHandler(
        enabled = destino != 0 || enTransicion
    ) {

        if (!enTransicion) {
            onVolver()
        }
    }

    // Ejecuta la transicion al cambiar de destino.
    LaunchedEffect(destino) {

        if (pantallaVisible != destino) {

            // Fade Out.
            visibilidad.targetState = false

            // Espera la finalizacion de la salida.
            snapshotFlow {
                visibilidad.isIdle &&
                        !visibilidad.currentState
            }.first { finalizada ->
                finalizada
            }

            // Cambia el contenido.
            pantallaVisible = destino
        }

        // Fade In.
        visibilidad.targetState = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoOscuro)
    ) {

        AnimatedVisibility(
            visibleState = visibilidad,

            modifier = Modifier.fillMaxSize(),

            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = DURACION_ANIMACION
                )
            ),

            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = DURACION_ANIMACION
                )
            )
        ) {

            key(pantallaVisible) {

                contenido(pantallaVisible)
            }
        }

        // Evita pulsaciones repetidas durante
        // las transiciones entre pantallas.
        if (enTransicion) {

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {

                        awaitPointerEventScope {

                            while (true) {

                                val evento = awaitPointerEvent(
                                    PointerEventPass.Initial
                                )

                                evento.changes.forEach {
                                    it.consume()
                                }
                            }
                        }
                    }
            )
        }
    }
}
