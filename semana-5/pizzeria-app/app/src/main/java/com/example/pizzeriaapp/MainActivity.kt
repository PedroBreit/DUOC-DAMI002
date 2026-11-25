
package com.example.pizzeriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pizzeriaapp.domain.*
import com.example.pizzeriaapp.logic.*
import com.example.pizzeriaapp.ui.theme.*
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PizzeriaAppTheme {
                PizzaAppPrincipal()
            }
        }
    }
}

fun formatoPesos(valor: Int): String =
    "$" + NumberFormat.getIntegerInstance(
        Locale.forLanguageTag("es-CL")
    ).format(valor)

@Composable
fun PizzaAppPrincipal(
    pizzeriaViewModel: PizzeriaViewModel = viewModel()
) {

    // Semana 5: datos observables del ViewModel.
    val resumen by pizzeriaViewModel.resumen.observeAsState(
        ResumenPizzeria()
    )

    val clientes by pizzeriaViewModel.clientes.observeAsState(
        emptyList()
    )

    // Indica qué pantalla se muestra.
    var pantalla by remember { mutableIntStateOf(0) }
    var aviso by remember { mutableStateOf("") }
    var stockRevision by remember { mutableIntStateOf(0) }
    var estadoRevision by remember { mutableIntStateOf(0) }


    // Repartidor asociado al pedido actual.
    var repartidorAsignado by remember {
        mutableStateOf<Repartidor?>(null)
    }

    // Conserva inventario y reglas del negocio durante el uso.
    val gestion = remember { GestionPizzeria() }
    val fecha = remember { LocalDate.now() }

    val catalogo = remember {
        listOf(
            PizzaPromocionable(
                "Margarita", 8000,
                mapOf(
                    "Masa" to 1,
                    "Queso" to 2,
                    "Tomate" to 1
                )
            ),
            Producto(
                "Pepperoni", 10000,
                mapOf(
                    "Masa" to 1,
                    "Queso" to 2,
                    "Pepperoni" to 2
                )
            ),
            PizzaPromocionable(
                "Vegetariana", 9000,
                mapOf(
                    "Masa" to 1,
                    "Queso" to 1,
                    "Verduras" to 2
                )
            )
        )
    }

    val repartidores = remember {
        listOf(
            Repartidor("Carla", "+56911111111", 3.0),
            Repartidor("Diego", "+56922222222", 1.2)
        )
    }


    // Lista de pizzas elegidas para el pedido.
    val carrito = remember {
        mutableStateListOf<Producto>()
    }

    var clienteSeleccionado by remember {
        mutableStateOf<Cliente?>(null)
    }

    var pedidoActual by remember {
        mutableStateOf<Pedido?>(null)
    }

    var nombreNuevo by remember { mutableStateOf("") }
    var telefonoNuevo by remember { mutableStateOf("") }
    var correoNuevo by remember { mutableStateOf("") }
    var mostrarFormulario by remember { mutableStateOf(false) }

    val inventarioTexto = remember(stockRevision) {
        gestion.consultarInventario()
    }

    val totales = calcularTotales(carrito.toList(), fecha)

    val fondo = Modifier
        .fillMaxSize()
        .background(FondoOscuro)
        .safeDrawingPadding()

    // Pantalla de inventario separada del proceso de compra.
    if (pantalla == 5) {
        Column(modifier = fondo) {
            PantallaInventario(
                gestion = gestion,
                revision = stockRevision,
                onActualizar = { stockRevision++ },
                onVolver = { pantalla = 0; aviso = "" }
            )
        }
    } else if (pantalla == 1) {

        // ======================================
        // PIZZAS: DISEÑO COMPACTO SIN SCROLL
        // ======================================

        Column(
            modifier = fondo.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        aviso = ""
                        pantalla = 0
                    }
                ) {
                    Text(
                        "<",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextoPrincipal
                    )
                }

                Text(
                    "Seleccionar pizzas",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextoPrincipal
                )
            }

            catalogo.forEach { producto ->

                val cantidad = carrito.count { it == producto }

                val faltantes = gestion.faltantes(
                    carrito.toList() + producto
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SuperficieOscura
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                producto.nombre,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextoPrincipal
                            )

                            Text(
                                formatoPesos(producto.precio),
                                style = MaterialTheme.typography.titleMedium,
                                color = TextoPrincipal
                            )
                        }

                        Text(
                            producto.ingredientes.keys.joinToString(", "),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (faltantes.isNotEmpty()) {
                            Text(
                                "Sin stock: " +
                                        faltantes.entries.joinToString {
                                            "${it.key} (${it.value})"
                                        },
                                style = MaterialTheme.typography.labelSmall,
                                color = ColorError,
                                maxLines = 1
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val indice = carrito.indexOfLast {
                                        it == producto
                                    }

                                    if (indice >= 0) {
                                        carrito.removeAt(indice)
                                    }
                                },
                                enabled = cantidad > 0,
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(36.dp)
                            ) {
                                Text("−")
                            }

                            Text(
                                "$cantidad",
                                modifier = Modifier.width(42.dp),
                                textAlign = TextAlign.Center,
                                color = TextoPrincipal
                            )

                            Button(
                                onClick = {
                                    if (
                                        carrito.size < 100 &&
                                        gestion.faltantes(
                                            carrito.toList() + producto
                                        ).isEmpty()
                                    ) {
                                        carrito.add(producto)
                                    }
                                },
                                enabled = carrito.size < 100 &&
                                        faltantes.isEmpty(),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(36.dp)
                            ) {
                                Text("+")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Pizzas: ${carrito.size}",
                    color = TextoSecundario
                )

                Text(
                    "Subtotal: ${formatoPesos(totales.subtotal)}",
                    color = TextoPrincipal,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Button(
                onClick = {
                    aviso = ""
                    pantalla = 2
                },
                enabled = carrito.isNotEmpty() &&
                        gestion.faltantes(carrito.toList()).isEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Continuar")
            }
        }

    } else {

        // ======================================
        // RESTO DE PANTALLAS
        // ======================================

        Column(
            modifier = fondo
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            when (pantalla) {

                // BIENVENIDA
                0 -> {
                    Spacer(Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Text(
                            "Bienvenido a PizzaApp",
                            style = MaterialTheme.typography.headlineLarge,
                            color = TextoPrincipal,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            "Sistema de gestión de pedidos, " +
                                    "inventario y despacho",
                            color = TextoSecundario,
                            textAlign = TextAlign.Center
                        )

                        ResumenScreen(resumen = resumen)

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = {
                                aviso = ""
                                pantalla = 1
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text("Ingresar pedido")
                        }

                        OutlinedButton(
                            onClick = {
                                aviso = ""
                                pantalla = 5
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text("Gestionar inventario")
                        }
                    }
                }

                // CLIENTES: elegir un registro existente.
                2 -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            aviso = ""
                            pantalla = 1
                        }) {
                            Text(
                                "<",
                                style = MaterialTheme.typography.headlineMedium,
                                color = TextoPrincipal
                            )
                        }
                        Text(
                            "Seleccionar cliente",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextoPrincipal
                        )
                    }

                    // Lista de clientes disponibles.
                    clientes.forEach { cliente ->
                        Card(
                            onClick = {
                                clienteSeleccionado = cliente
                                aviso = ""
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = SuperficieOscura
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = clienteSeleccionado == cliente,
                                    onClick = {
                                        clienteSeleccionado = cliente
                                        aviso = ""
                                    }
                                )
                                Column {
                                    Text(cliente.nombre, color = TextoPrincipal)
                                    Text(cliente.correo, color = TextoSecundario)
                                }
                            }
                        }
                    }

                    // Abre un formulario en una ventana independiente.
                    OutlinedButton(
                        onClick = {
                            nombreNuevo = ""
                            telefonoNuevo = ""
                            correoNuevo = ""
                            aviso = ""
                            mostrarFormulario = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("+ Agregar cliente")
                    }

                    HorizontalDivider()

                    // Continúa solo con un cliente seleccionado.
                    Button(
                        onClick = {
                            aviso = ""
                            pantalla = 3
                        },
                        enabled = clienteSeleccionado != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        Text("Continuar al resumen")
                    }

                    // Ventana modal de registro de clientes.
                    if (mostrarFormulario) {
                        var errorFormulario by remember { mutableStateOf("") }

                        AlertDialog(
                            onDismissRequest = {
                                mostrarFormulario = false
                                errorFormulario = ""
                            },
                            containerColor = SuperficieOscura,
                            title = {
                                Text("Nuevo cliente", color = TextoPrincipal)
                            },
                            text = {
                                Column(
                                    modifier = Modifier.verticalScroll(
                                        rememberScrollState()
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Nombre con ejemplo dentro del campo.
                                    Text("Nombre completo", color = TextoPrincipal)
                                    OutlinedTextField(
                                        value = nombreNuevo,
                                        onValueChange = {
                                            nombreNuevo = it
                                            errorFormulario = ""
                                        },
                                        placeholder = { Text("Ej.: María González") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    // Teléfono con el formato de Semana 3.
                                    Text("Teléfono", color = TextoPrincipal)
                                    OutlinedTextField(
                                        value = telefonoNuevo,
                                        onValueChange = {
                                            telefonoNuevo = it
                                            errorFormulario = ""
                                        },
                                        placeholder = { Text("Ej.: +56 (912) 345-6789") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    // Correo validado mediante Regex.
                                    Text("Correo electrónico", color = TextoPrincipal)
                                    OutlinedTextField(
                                        value = correoNuevo,
                                        onValueChange = {
                                            correoNuevo = it
                                            errorFormulario = ""
                                        },
                                        placeholder = { Text("Ej.: maria@correo.cl") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    if (errorFormulario.isNotEmpty()) {
                                        Text(errorFormulario, color = ColorError)
                                    }
                                }
                            },
                            confirmButton = {
                                Button(onClick = {
                                    val nombre = nombreNuevo.trim()
                                    val telefono = formatearTelefono(telefonoNuevo)

                                    when {
                                        nombre.isBlank() ->
                                            errorFormulario = "Ingresa el nombre."
                                        telefono == null ->
                                            errorFormulario = "Teléfono inválido: debe tener 12 dígitos."
                                        !correoEsValido(correoNuevo) ->
                                            errorFormulario = "Correo electrónico inválido."
                                        else -> {
                                            val nuevo = Cliente(
                                                nombre, telefono, correoNuevo
                                            )
                                            if (!pizzeriaViewModel.registrarCliente(nuevo)) {
                                                errorFormulario = "El cliente ya existe."
                                            } else {
                                                
                                                clienteSeleccionado = nuevo
                                                mostrarFormulario = false
                                                nombreNuevo = ""
                                                telefonoNuevo = ""
                                                correoNuevo = ""
                                                aviso = "Cliente registrado correctamente."
                                            }
                                        }
                                    }
                                }) {
                                    Text("Guardar cliente")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    mostrarFormulario = false
                                    errorFormulario = ""
                                }) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }
                }

                // RESUMEN: muestra importes y confirma el pedido.
                3 -> {
                    // Flecha de regreso al cliente.
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { pantalla = 2; aviso = "" }) {
                            Text("<", color = TextoPrincipal,
                                style = MaterialTheme.typography.headlineMedium)
                        }
                        Text("Resumen del pedido", color = TextoPrincipal,
                            style = MaterialTheme.typography.headlineSmall)
                    }

                    Text(
                        "Cliente: ${clienteSeleccionado?.nombre ?: ""}",
                        color = TextoPrincipal
                    )

                    Text("Fecha: $fecha", color = TextoPrincipal)

                    HorizontalDivider()

                    carrito.groupingBy { it.nombre to it.precio }
                        .eachCount()
                        .forEach { (detalle, cantidad) ->
                            Text(
                                "${detalle.first} x $cantidad - " +
                                        formatoPesos(detalle.second * cantidad),
                                color = TextoPrincipal
                            )
                        }

                    HorizontalDivider()

                    Text(
                        "Subtotal: ${formatoPesos(totales.subtotal)}",
                        color = TextoPrincipal
                    )

                    Text(
                        "Descuentos: ${formatoPesos(totales.descuento)}",
                        color = TextoPrincipal
                    )

                    Text(
                        "IVA (19%): ${formatoPesos(totales.impuestos)}",
                        color = TextoPrincipal
                    )

                    Text(
                        "Total final: ${formatoPesos(totales.total)}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextoPrincipal
                    )

                    Button(
                        onClick = {
                            val cliente = clienteSeleccionado

                            if (cliente != null && carrito.isNotEmpty()) {
                                val productos = carrito.toList()

                                if (gestion.confirmarPreparacion(productos)) {
                                    pedidoActual = Pedido(
                                        numero = pizzeriaViewModel.siguienteNumeroPedido(),
                                        cliente = cliente,
                                        productos = productos,
                                        fecha = fecha
                                    )

                                    pedidoActual?.let {
                                        pizzeriaViewModel.registrarPedido(it)
                                    }

                                    carrito.clear()
                                    stockRevision++
                                    aviso = "Pedido confirmado."
                                    pantalla = 4
                                } else {
                                    aviso = "Ingredientes insuficientes."
                                }
                            }
                        },
                        enabled = clienteSeleccionado != null &&
                                carrito.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirmar pedido")
                    }


                }

                // DESPACHO: gestiona estados y repartidores.
                4 -> {
                    val pedido = pedidoActual
                    val revision = estadoRevision

                    if (pedido != null) {
                        TituloPantalla(
                            "Estado del pedido",
                            "Seguimiento de preparación y despacho."
                        )

                        Text(
                            "Pedido #${pedido.numero}",
                            color = TextoPrincipal
                        )

                        Text(
                            "Cliente: ${pedido.cliente.nombre}",
                            color = TextoPrincipal
                        )

                        Text(
                            "Estado: ${pedido.estado.etiqueta}",
                            color = TextoPrincipal
                        )

                        Text(
                            "Total: " +
                                    formatoPesos(pedido.calcularTotal().total),
                            color = TextoPrincipal
                        )

                        if (pedido.estado == EstadoPedido.EN_PREPARACION) {
                            Button(
                                onClick = {
                                    pedido.cambiarEstado(EstadoPedido.LISTO)
                                    estadoRevision = revision + 1
                                    aviso = "Pedido listo para despacho."
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Marcar como listo")
                            }
                        }

                        if (pedido.estado == EstadoPedido.LISTO) {
                            Button(
                                onClick = {
                                    val repartidor =
                                        gestion.asignarRepartidor(
                                            pedido,
                                            repartidores
                                        )

                                    aviso = if (repartidor != null) {
                                        repartidorAsignado = repartidor
                                        "Repartidor asignado: " +
                                                repartidor.nombre
                                    } else {
                                        "No hay repartidores disponibles."
                                    }

                                    estadoRevision++
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Asignar repartidor")
                            }
                        }

                        HorizontalDivider()

                        Text(
                            "Repartidores",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextoPrincipal
                        )

                        // Cada repartidor ocupado puede finalizar su entrega.
                        repartidores.forEach { repartidor ->
                            Card(colors = CardDefaults.cardColors(
                                containerColor = SuperficieOscura
                            ), modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("${repartidor.nombre} · ${repartidor.distanciaKm} km",
                                        color = TextoPrincipal)
                                    Text(if (repartidor.disponible) "Disponible" else "Ocupado",
                                        color = TextoSecundario)
                                    if (
                                        pedido.estado == EstadoPedido.EN_CAMINO &&
                                        repartidor === repartidorAsignado &&
                                        !repartidor.disponible
                                    ) {
                                        OutlinedButton(onClick = {
                                            // Finalizar pedido y liberar repartidor.
                                            pedido.cambiarEstado(EstadoPedido.ENTREGADO)
                                            repartidor.marcarEntregado()
                                            estadoRevision++
                                            aviso = "Pedido entregado. ${repartidor.nombre} quedó disponible."
                                        }) { Text("Entregado") }
                                    }
                                }
                            }
                        }

                        if (pedido.estado == EstadoPedido.ENTREGADO) {
                            Button(
                                onClick = {
                                    pedidoActual = null
                                    clienteSeleccionado = null
                                    carrito.clear()
                                    aviso = ""
                                    pantalla = 0
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Nuevo pedido")
                            }
                        }
                    }
                }


            }

            if (aviso.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = SuperficieOscura
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        aviso,
                        color = TextoPrincipal,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TituloPantalla(titulo: String, subtitulo: String) {
    Text(
        titulo,
        style = MaterialTheme.typography.headlineSmall,
        color = TextoPrincipal
    )

    Text(
        subtitulo,
        color = TextoSecundario
    )

    HorizontalDivider()
}

// Flecha reutilizable para regresar a la pantalla anterior.
@Composable
fun BotonVolver(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Text(
            text = "<",
            color = TextoPrincipal,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

