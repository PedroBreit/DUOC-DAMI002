package com.example.pizzeriaapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pizzeriaapp.logic.GestionPizzeria
import com.example.pizzeriaapp.ui.theme.*

// Pantalla independiente para revisar y reponer ingredientes.
@Composable
fun PantallaInventario(
    gestion: GestionPizzeria,
    revision: Int,
    onActualizar: () -> Unit,
    onVolver: () -> Unit
) {
    val ingredientes = listOf("Masa", "Queso", "Tomate", "Pepperoni", "Verduras")
    val cantidades = remember { mutableStateMapOf<String, String>() }
    var mensaje by remember { mutableStateOf("") }

    // Se vuelve a consultar al actualizar una reposicion.
    val existencias = remember(revision) {
        gestion.consultarInventario()
            .lineSequence()
            .mapNotNull { linea ->
                val partes = linea.split(":", limit = 2)
                if (partes.size != 2) null
                else partes[0].trim() to
                    (partes[1].trim().substringBefore(' ').toIntOrNull() ?: 0)
            }.toMap()
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVolver) {
                Text("<", color = TextoPrincipal,
                    style = MaterialTheme.typography.headlineMedium)
            }
            Text("Gestionar inventario", color = TextoPrincipal,
                style = MaterialTheme.typography.headlineSmall)
        }
        Text("Existencias y reposición", color = TextoSecundario)

        ingredientes.forEach { ingrediente ->
            val actual = existencias[ingrediente] ?: 0
            val entrada = cantidades[ingrediente] ?: ""
            val valor = entrada.toIntOrNull()
            val valido = valor != null && valor in 1..10000
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SuperficieOscura)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(ingrediente, color = TextoPrincipal,
                            style = MaterialTheme.typography.titleMedium)
                        Text("Stock: $actual", color = TextoSecundario)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = entrada,
                            onValueChange = { nuevo ->
                                if (nuevo.length <= 5 && nuevo.all { it.isDigit() }) {
                                    cantidades[ingrediente] = nuevo
                                }
                            },
                            label = { Text("Cantidad") },
                            placeholder = { Text("Ej. 5") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                val cantidad = valor ?: return@Button
                                gestion.reabastecer(ingrediente, cantidad)
                                cantidades[ingrediente] = ""
                                mensaje = "Se agregaron $cantidad unidades de $ingrediente."
                                onActualizar()
                            },
                            enabled = valido,
                            modifier = Modifier.height(56.dp)
                        ) { Text("Reponer") }
                    }
                }
            }
        }
        if (mensaje.isNotEmpty()) {
            Text(mensaje, color = MaterialTheme.colorScheme.primary)
        }
    }
}
