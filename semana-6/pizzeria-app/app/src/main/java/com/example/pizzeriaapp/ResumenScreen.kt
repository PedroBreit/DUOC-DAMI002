package com.example.pizzeriaapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pizzeriaapp.logic.ResumenPizzeria
import com.example.pizzeriaapp.ui.theme.*

// Semana 6: resumen dinamico con estado de carga.
@Composable
fun ResumenScreen(
    resumen: ResumenPizzeria,
    cargando: Boolean = false,
    mensajeCarga: String = "",
    errorCarga: String = "",
    onActualizar: () -> Unit = {}
) {

    Card(
        modifier = Modifier.fillMaxWidth(),

        colors = CardDefaults.cardColors(
            containerColor = SuperficieOscura
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            // Encabezado del resumen.
            Row(
                modifier = Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = "Resumen de operaciones",

                    modifier = Modifier.weight(1f),

                    style =
                        MaterialTheme.typography.titleLarge,

                    fontWeight = FontWeight.Bold,

                    color = TextoPrincipal,

                    textAlign = TextAlign.Center
                )

                IconButton(
                    onClick = onActualizar,

                    enabled = !cargando
                ) {

                    Icon(
                        imageVector = Icons.Default.Refresh,

                        contentDescription =
                            "Actualizar resumen",

                        tint = if (cargando)
                            TextoSecundario
                        else
                            AzulClaro
                    )
                }
            }

            HorizontalDivider(
                color = AzulClaro.copy(alpha = 0.3f)
            )

            // Semana 6: indicador animado de progreso.
            AnimatedVisibility(
                visible = cargando,

                enter = fadeIn(
                    animationSpec = tween(300)
                ),

                exit = fadeOut(
                    animationSpec = tween(300)
                )
            ) {

                IndicadorCarga(
                    mensaje = mensajeCarga
                )
            }

            // Muestra los resultados al finalizar.
            AnimatedVisibility(
                visible = !cargando &&
                        errorCarga.isBlank(),

                enter = fadeIn(
                    animationSpec = tween(300)
                ),

                exit = fadeOut(
                    animationSpec = tween(300)
                )
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),

                    verticalArrangement =
                        Arrangement.spacedBy(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        IndicadorResumen(
                            titulo = "Clientes registrados",

                            valor = resumen.totalClientes
                                .toString(),

                            modifier = Modifier.weight(1f)
                        )

                        IndicadorResumen(
                            titulo = "Pedidos realizados",

                            valor = resumen.totalPedidos
                                .toString(),

                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(
                        color = AzulClaro.copy(alpha = 0.3f)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalAlignment =
                            Alignment.CenterHorizontally,

                        verticalArrangement =
                            Arrangement.spacedBy(6.dp)
                    ) {

                        Text(
                            text = "Último cliente registrado",

                            style =
                                MaterialTheme.typography.bodyMedium,

                            color = TextoSecundario,

                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = resumen.ultimoCliente,

                            style =
                                MaterialTheme.typography.titleLarge,

                            fontWeight = FontWeight.Bold,

                            color = AzulClaro,

                            textAlign = TextAlign.Center
                        )
                    }

                    Text(
                        text =
                            "Información actualizada automáticamente",

                        modifier = Modifier.fillMaxWidth(),

                        style =
                            MaterialTheme.typography.bodySmall,

                        color = TextoSecundario,

                        textAlign = TextAlign.Center
                    )
                }
            }

            // Estado de error y opcion de reintento.
            AnimatedVisibility(
                visible = !cargando &&
                        errorCarga.isNotBlank(),

                enter = fadeIn(),

                exit = fadeOut()
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),

                    horizontalAlignment =
                        Alignment.CenterHorizontally,

                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = errorCarga,

                        color = ColorError,

                        textAlign = TextAlign.Center
                    )

                    OutlinedButton(
                        onClick = onActualizar
                    ) {

                        Text("Reintentar")
                    }
                }
            }
        }
    }
}

// Componente reutilizable de resumen.
@Composable
private fun IndicadorResumen(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,

        colors = CardDefaults.cardColors(
            containerColor = FondoOscuro
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = valor,

                style =
                    MaterialTheme.typography.headlineLarge,

                fontWeight = FontWeight.Bold,

                color = AzulClaro
            )

            Text(
                text = titulo,

                style =
                    MaterialTheme.typography.bodyMedium,

                color = TextoPrincipal,

                textAlign = TextAlign.Center
            )
        }
    }
}
