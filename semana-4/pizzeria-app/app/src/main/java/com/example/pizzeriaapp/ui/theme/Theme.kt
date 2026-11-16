package com.example.pizzeriaapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PizzaAppDarkColors = darkColorScheme(
    primary = AzulPrincipal,
    onPrimary = TextoPrincipal,
    primaryContainer = SuperficieOscura,
    onPrimaryContainer = TextoPrincipal,

    secondary = AzulClaro,
    onSecondary = FondoOscuro,

    background = FondoOscuro,
    onBackground = TextoPrincipal,

    surface = FondoOscuro,
    onSurface = TextoPrincipal,

    surfaceVariant = SuperficieOscura,
    onSurfaceVariant = TextoSecundario,

    error = ColorError,
    onError = FondoOscuro
)

@Composable
fun PizzeriaAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PizzaAppDarkColors,
        content = content
    )
}
