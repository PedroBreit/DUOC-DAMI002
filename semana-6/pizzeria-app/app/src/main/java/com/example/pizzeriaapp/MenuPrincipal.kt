package com.example.pizzeriaapp

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.pizzeriaapp.ui.theme.*

// Semana 6: estructura de las opciones del menu.
private data class OpcionMenu(
    val titulo: String,
    val icono: ImageVector,
    val destino: Int
)

// Barra superior con menu desplegable.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuPrincipal(
    onNavegar: (Int) -> Unit
) {

    // Controla la apertura y cierre del menu.
    var menuExpandido by remember {
        mutableStateOf(false)
    }

    // Opciones disponibles en PizzaApp.
    val opciones = remember {
        listOf(
            OpcionMenu(
                "Inicio",
                Icons.Default.Home,
                0
            ),
            OpcionMenu(
                "Gestión de clientes",
                Icons.Default.People,
                6
            ),
            OpcionMenu(
                "Productos",
                Icons.Default.LocalPizza,
                7
            ),
            OpcionMenu(
                "Registrar pedido",
                Icons.Default.Assignment,
                1
            ),
            OpcionMenu(
                "Gestionar inventario",
                Icons.Default.Inventory,
                5
            ),
            OpcionMenu(
                "Resumen de operaciones",
                Icons.Default.Assessment,
                8
            )
        )
    }

    TopAppBar(

        title = {
            Text(
                text = "PizzaApp",
                color = TextoPrincipal,
                style = MaterialTheme.typography.titleLarge
            )
        },

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = FondoOscuro
        ),

        actions = {

            Box {

                // Boton que abre el menu.
                IconButton(
                    onClick = {
                        menuExpandido = true
                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Abrir menú principal",
                        tint = TextoPrincipal
                    )
                }

                // Menu desplegable de Jetpack Compose.
                DropdownMenu(
                    expanded = menuExpandido,

                    onDismissRequest = {
                        menuExpandido = false
                    }
                ) {

                    opciones.forEach { opcion ->

                        DropdownMenuItem(

                            text = {
                                Text(opcion.titulo)
                            },

                            leadingIcon = {
                                Icon(
                                    imageVector = opcion.icono,
                                    contentDescription = null,
                                    tint = AzulClaro
                                )
                            },

                            onClick = {

                                // Cierra el menu antes de navegar.
                                menuExpandido = false

                                onNavegar(opcion.destino)
                            }
                        )
                    }
                }
            }
        }
    )
}
