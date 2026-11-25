package com.example.pizzeriaapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pizzeriaapp.ui.theme.*

// Semana 6: componente reutilizable de progreso.
@Composable
fun IndicadorCarga(
    mensaje: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier.fillMaxWidth(),

        colors = CardDefaults.cardColors(
            containerColor = SuperficieOscura
        ),

        shape = MaterialTheme.shapes.large
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            // Indicador de progreso indeterminado.
            CircularProgressIndicator(

                modifier = Modifier.size(48.dp),

                color = AzulClaro,

                strokeWidth = 4.dp
            )

            // Mensaje asociado al proceso.
            Text(
                text = mensaje.ifBlank {
                    "Cargando datos..."
                },

                color = TextoPrincipal,

                style = MaterialTheme.typography.titleMedium,

                textAlign = TextAlign.Center
            )

            Text(
                text = "Por favor, espera un momento.",

                color = TextoSecundario,

                style = MaterialTheme.typography.bodySmall,

                textAlign = TextAlign.Center
            )
        }
    }
}
