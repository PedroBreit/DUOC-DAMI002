
import java.util.Locale
import kotlin.math.roundToInt

// Configuración de precios y descuentos.
const val IVA = 0.19
const val DESCUENTO_CUATRO_PIZZAS = 0.10

fun pesos(monto: Int): String =
    "$" + String.format(Locale.forLanguageTag("es-CL"), "%,d", monto)

// Datos de contacto de clientes.
fun validarTelefono(telefono: String): String =
    if (Regex("^\\+?\\d{9,12}$").matches(telefono.replace(" ", "")))
        telefono.trim()
    else
        "(000) 000-0000"

fun validarCorreo(correo: String): String =
    if (Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
            .matches(correo.trim()))
        correo.trim().lowercase()
    else
        "correo@invalido.com"

// Usuarios, clientes y repartidores.
open class Usuario(val nombre: String, val contacto: String) {
    open fun descripcion(): String = "$nombre ($contacto)"
}

class Cliente(
    nombre: String,
    telefono: String,
    correo: String
) : Usuario(nombre, validarTelefono(telefono)) {

    val correo: String = validarCorreo(correo)

    override fun descripcion(): String =
        "$nombre | Teléfono: $contacto | Correo: $correo"
}

class Repartidor(
    nombre: String,
    telefono: String,
    val distanciaKm: Double
) : Usuario(nombre, telefono) {

    var disponible: Boolean = true
        private set

    fun entregarPedido(pedido: Pedido) {
        require(disponible) {
            "El repartidor no está disponible"
        }
        require(pedido.estado == EstadoPedido.LISTO) {
            "El pedido debe estar listo antes del despacho"
        }

        pedido.cambiarEstado(EstadoPedido.EN_CAMINO)
        disponible = false

        println(
            "Despacho asignado: $nombre " +
                    "(${distanciaKm} km) para pedido #${pedido.numero}"
        )
    }
}

// Productos disponibles en la pizzería.
class Producto(
    val nombre: String,
    val precio: Int,
    val ingredientes: Map<String, Int>
) {
    init {
        require(nombre.isNotBlank()) {
            "El nombre del producto no puede estar vacío"
        }
        require(precio >= 0) {
            "El precio debe ser mayor o igual a cero"
        }
        require(ingredientes.values.all { it > 0 }) {
            "Las cantidades de ingredientes deben ser positivas"
        }
    }

    fun detallarProducto(): String =
        "$nombre: ${pesos(precio)} | Ingredientes: " +
                ingredientes.entries.joinToString {
                    "${it.key} (${it.value})"
                }
}

// Estados y cálculos de pedidos.
enum class EstadoPedido(val etiqueta: String) {
    EN_PREPARACION("En preparación"),
    LISTO("Listo"),
    EN_CAMINO("En camino")
}

data class Totales(
    val subtotal: Int,
    val descuento: Int,
    val impuestos: Int,
    val total: Int
)

fun calcularTotales(productos: List<Producto>): Totales {
    val subtotal = productos.sumOf { it.precio }

    val descuento =
        if (productos.size > 3)
            (subtotal * DESCUENTO_CUATRO_PIZZAS).roundToInt()
        else
            0

    val impuestos = ((subtotal - descuento) * IVA).roundToInt()

    return Totales(
        subtotal,
        descuento,
        impuestos,
        subtotal - descuento + impuestos
    )
}

// Información y seguimiento de pedidos.
class Pedido(
    val numero: Int,
    val cliente: Cliente,
    val productos: List<Producto>,
    val notas: String? = null
) {
    var estado: EstadoPedido = EstadoPedido.EN_PREPARACION
        private set

    init {
        require(productos.isNotEmpty()) {
            "Un pedido debe incluir al menos una pizza"
        }
    }

    fun calcularTotal(): Totales = calcularTotales(productos)

    fun cambiarEstado(nuevoEstado: EstadoPedido) {
        val permitido = when (estado) {
            EstadoPedido.EN_PREPARACION ->
                nuevoEstado == EstadoPedido.LISTO

            EstadoPedido.LISTO ->
                nuevoEstado == EstadoPedido.EN_CAMINO

            EstadoPedido.EN_CAMINO -> false
        }

        require(permitido) {
            "Cambio de estado no permitido: " +
                    "${estado.etiqueta} -> ${nuevoEstado.etiqueta}"
        }

        estado = nuevoEstado
    }

    fun resumen(): String {
        val totales = calcularTotal()

        val cantidades = productos
            .groupingBy { it.nombre to it.precio }
            .eachCount()

        val notaVisible =
            notas?.trim()?.takeIf { it.isNotEmpty() }
                ?: "Sin notas"

        return buildString {
            appendLine("--------------------------------------------------")
            appendLine("PEDIDO #$numero")
            appendLine("Cliente: ${cliente.descripcion()}")
            appendLine("Productos:")

            for ((productoPrecio, cantidad) in cantidades) {
                appendLine(
                    "  - ${productoPrecio.first} | " +
                            "Unitario: ${pesos(productoPrecio.second)} | " +
                            "Cantidad: $cantidad"
                )
            }

            appendLine("Subtotal: ${pesos(totales.subtotal)}")
            appendLine("Descuento: ${pesos(totales.descuento)}")
            appendLine("IVA (19%): ${pesos(totales.impuestos)}")
            appendLine("TOTAL FINAL: ${pesos(totales.total)}")
            appendLine("Estado: ${estado.etiqueta}")
            appendLine("Notas: $notaVisible")
            append("--------------------------------------------------")
        }
    }
}

// Control de ingredientes y existencias.
class Inventario(cantidadesIniciales: Map<String, Int>) {
    private val existencias = cantidadesIniciales.toMutableMap()

    init {
        require(existencias.values.all { it >= 0 }) {
            "No se admiten existencias negativas"
        }
    }

    fun mostrar(): String =
        existencias.entries.joinToString("\n") {
            "${it.key}: ${it.value} unidades"
        }

    fun reabastecer(ingrediente: String, cantidad: Int) {
        require(cantidad > 0) {
            "La cantidad a reabastecer debe ser positiva"
        }

        existencias[ingrediente] =
            (existencias[ingrediente] ?: 0) + cantidad
    }

    fun cantidadesParaLote(
        productos: List<Producto>
    ): Map<String, Int> {

        val requeridos = mutableMapOf<String, Int>()

        for (producto in productos) {
            for ((ingrediente, cantidad) in producto.ingredientes) {
                requeridos[ingrediente] =
                    (requeridos[ingrediente] ?: 0) + cantidad
            }
        }

        return requeridos
    }

    fun prepararLote(productos: List<Producto>): Boolean {
        val requeridos = cantidadesParaLote(productos)

        val faltantes = requeridos.filter { (ingrediente, cantidad) ->
            cantidad > (existencias[ingrediente] ?: 0)
        }

        when {
            faltantes.isEmpty() -> {
                for ((ingrediente, cantidad) in requeridos) {
                    existencias[ingrediente] =
                        (existencias[ingrediente] ?: 0) - cantidad
                }

                println("Pedido aceptado: inventario actualizado.")
                return true
            }

            else -> {
                println("Pedido rechazado: ingredientes insuficientes:")

                faltantes.forEach { (ingrediente, necesario) ->
                    val disponible = existencias[ingrediente] ?: 0

                    println(
                        " - $ingrediente: disponible $disponible, " +
                                "requerido $necesario, " +
                                "faltan ${necesario - disponible}"
                    )
                }

                println(
                    "Pasos: 1) Revisar faltantes. " +
                            "2) Comprar insumos. " +
                            "3) Registrar reabastecimiento. " +
                            "4) Reintentar pedido."
                )

                return false
            }
        }
    }
}

// Asignación de despachos y validación de entradas.
fun asignarRepartidor(
    pedido: Pedido,
    repartidores: List<Repartidor>
): Repartidor? {

    val ordenados = repartidores.sortedBy { it.distanciaKm }
    val seleccionado = ordenados.find { it.disponible }

    seleccionado?.let {
        it.entregarPedido(pedido)
    }

    return seleccionado
}

fun registrarCantidad(texto: String): Int? = try {
    val numero = texto.toInt()

    require(numero >= 0) {
        "La cantidad no puede ser negativa"
    }

    numero

} catch (e: NumberFormatException) {
    println(
        "Error de entrada: '$texto' no es un número entero válido"
    )
    null

} catch (e: IllegalArgumentException) {
    println("Error de entrada: ${e.message}")
    null
}

// Ejemplo de operación diaria de la pizzería.
fun main() {
    println("=== SISTEMA DE GESTIÓN | PIZZERÍA KOTLIN ===")

    val margarita = Producto(
        "Margarita", 8000,
        mapOf("Masa" to 1, "Queso" to 2, "Tomate" to 1)
    )

    val pepperoni = Producto(
        "Pepperoni", 10000,
        mapOf("Masa" to 1, "Queso" to 2, "Pepperoni" to 2)
    )

    val vegetariana = Producto(
        "Vegetariana", 9000,
        mapOf("Masa" to 1, "Queso" to 1, "Verduras" to 2)
    )

    val catalogo = listOf(margarita, pepperoni, vegetariana)

    println("\nCATÁLOGO")
    catalogo.forEach { println(it.detallarProducto()) }

    val inventario = Inventario(
        mapOf(
            "Masa" to 8,
            "Queso" to 12,
            "Tomate" to 3,
            "Pepperoni" to 1,
            "Verduras" to 6
        )
    )

    println("\nINVENTARIO INICIAL\n${inventario.mostrar()}")

    val ana = Cliente(
        "Ana Pérez",
        "+56912345678",
        "ana@example.com"
    )

    val luis = Cliente(
        "Luis Soto",
        "mal",
        "correo-mal"
    )

    val repartidores = listOf(
        Repartidor("Carla", "+56911111111", 3.0),
        Repartidor("Diego", "+56922222222", 1.2)
    )

    val nombresClientes = setOf(
        ana.nombre,
        luis.nombre,
        ana.nombre
    )

    println(
        "\nCLIENTES ÚNICOS: ${nombresClientes.joinToString()}"
    )

    val pedido1 = Pedido(
        1,
        ana,
        listOf(
            margarita,
            margarita,
            vegetariana,
            margarita
        ),
        "Sin cebolla"
    )

    if (inventario.prepararLote(pedido1.productos)) {
        pedido1.cambiarEstado(EstadoPedido.LISTO)
        asignarRepartidor(pedido1, repartidores)
    }

    val pedido2 = Pedido(
        2,
        luis,
        listOf(pepperoni),
        null
    )

    if (!inventario.prepararLote(pedido2.productos)) {
        println("Reponiendo 2 unidades de Pepperoni...")

        inventario.reabastecer("Pepperoni", 2)

        if (inventario.prepararLote(pedido2.productos)) {
            pedido2.cambiarEstado(EstadoPedido.LISTO)
        }
    }

    val pedidosDelDia: Array<Pedido> =
        arrayOf(pedido1, pedido2)

    println("\n=== REPORTE DE PEDIDOS DEL DÍA ===")

    for (pedido in pedidosDelDia) {
        println(
            "#${pedido.numero} | " +
                    "${pedido.cliente.nombre} | " +
                    "Pizzas: ${pedido.productos.size} | " +
                    "${pesos(pedido.calcularTotal().total)} | " +
                    pedido.estado.etiqueta
        )
    }

    val despachados = pedidosDelDia.filter {
        it.estado == EstadoPedido.EN_CAMINO
    }

    val pendientes = pedidosDelDia.filter {
        it.estado != EstadoPedido.EN_CAMINO
    }

    println(
        "En camino: ${despachados.size} | " +
                "Pendientes de despacho: ${pendientes.size}"
    )

    for (pedido in pedidosDelDia) {
        println("\n${pedido.resumen()}")
    }

    println("\nINVENTARIO FINAL\n${inventario.mostrar()}")

    println("\n=== PRUEBAS DE ERRORES CONTROLADOS ===")

    registrarCantidad("-3")
    registrarCantidad("no es número")

    try {
        Producto(
            "Pizza inválida",
            -500,
            mapOf("Masa" to 1)
        )
    } catch (e: IllegalArgumentException) {
        println("Error de precio: ${e.message}")
    }

    try {
        inventario.reabastecer("Queso", -2)
    } catch (e: IllegalArgumentException) {
        println("Error de inventario: ${e.message}")
    }

    listOf(ana, luis).forEach { cliente ->
        cliente.correo
            .takeIf { it != "correo@invalido.com" }
            ?.let { correo ->
                println(
                    "Notificación simulada enviada a $correo"
                )
            }
    }
}
