package com.example.pizzeriaapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pizzeriaapp.domain.Producto
import com.example.pizzeriaapp.ui.theme.*

// Semana 6: consulta independiente del catalogo.
@Composable
fun ProductosScreen(
    productos: List<Producto>,
    onVolver: () -> Unit,
    onRegistrarPedido: () -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        BotonVolver(onClick = onVolver)

        Text(
            text = "Catálogo de productos",
            color = TextoPrincipal,
            style = MaterialTheme.typography.headlineSmall
        )
    }

    Text(
        text = "Pizzas disponibles",
        color = TextoSecundario
    )

    productos.forEach { producto ->

        Card(
            modifier = Modifier.fillMaxWidth(),

            colors = CardDefaults.cardColors(
                containerColor = SuperficieOscura
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically,

                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.LocalPizza,
                        contentDescription = null,
                        tint = AzulClaro
                    )

                    Text(
                        text = producto.nombre,
                        color = TextoPrincipal,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Text(
                    text = formatoPesos(producto.precio),
                    color = AzulClaro,
                    style = MaterialTheme.typography.titleMedium
                )

                HorizontalDivider()

                Text(
                    text = "Ingredientes:",
                    color = TextoPrincipal
                )

                Text(
                    text = producto.ingredientes.keys
                        .joinToString(", "),
                    color = TextoSecundario
                )
            }
        }
    }

    Button(
        onClick = onRegistrarPedido,

        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        Text("Registrar pedido")
    }
}
