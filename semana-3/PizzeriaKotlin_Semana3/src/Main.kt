import java.time.LocalDate
import java.util.Locale
import kotlin.math.roundToInt

const val IVA = 0.19
const val DESCUENTO_CUATRO_PIZZAS = 0.10
const val DESCUENTO_PROMOCIONAL = 0.05

val INICIO_PROMOCION: LocalDate = LocalDate.of(2026, 11, 1)
val FIN_PROMOCION: LocalDate = LocalDate.of(2026, 11, 30)

fun pesos(monto: Int): String =
    "$" + String.format(Locale.forLanguageTag("es-CL"), "%,d", monto)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Promocionable

fun esPromocionable(producto: Producto): Boolean =
    producto.javaClass.isAnnotationPresent(Promocionable::class.java)

fun validarTelefono(telefono: String): String =
    formatearTelefono(telefono)
        ?: throw IllegalArgumentException("Telefono invalido. Formato: +XX (XXX) XXX-XXXX")

fun validarCorreo(correo: String): String {
    require(correoEsValido(correo)) {
        "Correo invalido. Ejemplo: nombre@dominio.com"
    }
    return correo.trim().lowercase()
}

open class Usuario(val nombre: String, val contacto: String) {
    open fun descripcion(): String = "$nombre ($contacto)"
}

class Cliente(nombre: String, telefono: String, correo: String) :
    Usuario(nombre, validarTelefono(telefono)) {
    val correo: String = validarCorreo(correo)

    override fun descripcion(): String =
        "$nombre | Telefono: $contacto | Correo: $correo"

    // Desestructuracion: nombre, correo, telefono.
    operator fun component1(): String = nombre
    operator fun component2(): String = correo
    operator fun component3(): String = contacto

    // Identidad de cliente: nombre y correo.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cliente) return false
        return nombre.trim().equals(other.nombre.trim(), ignoreCase = true) &&
                correo.equals(other.correo, ignoreCase = true)
    }

    override fun hashCode(): Int {
        var result = nombre.trim().lowercase(Locale.ROOT).hashCode()
        result = 31 * result + correo.lowercase(Locale.ROOT).hashCode()
        return result
    }
}

class Repartidor(
    nombre: String,
    telefono: String,
    val distanciaKm: Double
) : Usuario(nombre, telefono) {
    var disponible: Boolean = true
        private set

    fun entregarPedido(pedido: Pedido) {
        require(disponible) { "El repartidor no esta disponible" }
        require(pedido.estado == EstadoPedido.LISTO) {
            "El pedido debe estar listo para despacharse"
        }
        pedido.cambiarEstado(EstadoPedido.EN_CAMINO)
        disponible = false
        println("Despacho asignado: $nombre ($distanciaKm km) para pedido #${pedido.numero}")
    }
}

// En Kotlin, el operador == invoca equals().
open class Producto(
    val nombre: String,
    val precio: Int,
    val ingredientes: Map<String, Int>,
    val categoria: String = "pizza"
) {
    init {
        require(nombre.isNotBlank()) { "El nombre del producto no puede estar vacio" }
        require(categoria.isNotBlank()) { "La categoria no puede estar vacia" }
        require(precio >= 0) { "El precio debe ser mayor o igual a cero" }
        require(ingredientes.values.all { it > 0 }) {
            "Las cantidades de ingredientes deben ser positivas"
        }
    }

    // La identidad se basa en nombre y categoria, no en precio.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Producto) return false
        return nombre.trim().equals(other.nombre.trim(), ignoreCase = true) &&
                categoria.trim().equals(other.categoria.trim(), ignoreCase = true)
    }

    override fun hashCode(): Int {
        var result = nombre.trim().lowercase(Locale.ROOT).hashCode()
        result = 31 * result + categoria.trim().lowercase(Locale.ROOT).hashCode()
        return result
    }

    fun detallarProducto(): String =
        "$nombre: ${pesos(precio)} | Categoria: $categoria | Ingredientes: " +
                ingredientes.entries.joinToString { "${it.key} (${it.value})" }
}

@Promocionable
class PizzaPromocionable(
    nombre: String,
    precio: Int,
    ingredientes: Map<String, Int>
) : Producto(nombre, precio, ingredientes, "pizza")

enum class EstadoPedido(val etiqueta: String) {
    EN_PREPARACION("En preparacion"),
    LISTO("Listo"),
    EN_CAMINO("En camino")
}

data class Totales(
    val subtotal: Int,
    val descuento: Int,
    val impuestos: Int,
    val total: Int
)

fun calcularTotales(productos: List<Producto>, fechaPedido: LocalDate): Totales {
    val subtotal = productos.sumOf { it.precio }
    val descuentoCantidad = if (productos.size >= 4) {
        (subtotal * DESCUENTO_CUATRO_PIZZAS).roundToInt()
    } else 0

    val subtotalPromocionable = productos
        .filter { esPromocionable(it) }
        .sumOf { it.precio }

    val descuentoFecha = if (fechaEnPromocion(
            fechaPedido, INICIO_PROMOCION, FIN_PROMOCION
        )) {
        val factorCantidad = if (productos.size >= 4) {
            1.0 - DESCUENTO_CUATRO_PIZZAS
        } else 1.0
        (subtotalPromocionable * factorCantidad * DESCUENTO_PROMOCIONAL).roundToInt()
    } else 0

    val descuento = descuentoCantidad + descuentoFecha
    val neto = subtotal - descuento
    val impuestos = (neto * IVA).roundToInt()
    return Totales(subtotal, descuento, impuestos, neto + impuestos)
}

class Pedido(
    val numero: Int,
    val cliente: Cliente,
    val productos: List<Producto>,
    val notas: String? = null,
    val fecha: LocalDate = LocalDate.now()
) {
    var estado: EstadoPedido = EstadoPedido.EN_PREPARACION
        private set

    init {
        require(cantidadPermitida(productos.size)) {
            "El pedido debe contener entre 1 y 100 productos"
        }
    }

    fun calcularTotal(): Totales = calcularTotales(productos, fecha)

    // Desestructuracion: cliente, productos y total final.
    operator fun component1(): Cliente = cliente
    operator fun component2(): List<Producto> = productos
    operator fun component3(): Int = calcularTotal().total

    // Se combinan solo pedidos del mismo cliente y la misma fecha.
    // El nuevo pedido queda en preparacion y no descuenta inventario.
    operator fun plus(otro: Pedido): Pedido {
        require(cliente == otro.cliente) {
            "No se pueden combinar pedidos de clientes diferentes"
        }
        require(fecha == otro.fecha) {
            "No se pueden combinar pedidos de fechas diferentes"
        }
        val unidos = productos + otro.productos
        require(cantidadPermitida(unidos.size)) {
            "El pedido combinado debe contener entre 1 y 100 productos"
        }
        return Pedido(
            numero = numero,
            cliente = cliente,
            productos = unidos,
            notas = listOfNotNull(notas, otro.notas)
                .filter { it.isNotBlank() }
                .distinct()
                .joinToString("; ").ifBlank { null },
            fecha = fecha
        )
    }

    fun cambiarEstado(nuevoEstado: EstadoPedido) {
        val permitido = when (estado) {
            EstadoPedido.EN_PREPARACION -> nuevoEstado == EstadoPedido.LISTO
            EstadoPedido.LISTO -> nuevoEstado == EstadoPedido.EN_CAMINO
            EstadoPedido.EN_CAMINO -> false
        }
        require(permitido) {
            "Cambio de estado no permitido: ${estado.etiqueta} -> ${nuevoEstado.etiqueta}"
        }
        estado = nuevoEstado
    }

    fun resumen(): String {
        val totales = calcularTotal()
        val cantidades = productos.groupingBy { it.nombre to it.precio }.eachCount()
        val notaVisible = notas?.trim()?.takeIf { it.isNotEmpty() } ?: "Sin notas"
        val promocion = fechaEnPromocion(
            fecha, INICIO_PROMOCION, FIN_PROMOCION
        ) && productos.any { esPromocionable(it) }

        return buildString {
            appendLine("----------------------------------------")
            appendLine("PEDIDO #$numero")
            appendLine("Fecha: $fecha")
            appendLine("Cliente: ${cliente.descripcion()}")
            appendLine("Productos:")
            for ((productoPrecio, cantidad) in cantidades) {
                appendLine(
                    " - ${productoPrecio.first} | Unitario: " +
                            "${pesos(productoPrecio.second)} | Cantidad: $cantidad"
                )
            }
            appendLine("Promocion por fecha: ${if (promocion) "Aplicada" else "No aplicada"}")
            appendLine("Subtotal: ${pesos(totales.subtotal)}")
            appendLine("Descuento: ${pesos(totales.descuento)}")
            appendLine("IVA (19%): ${pesos(totales.impuestos)}")
            appendLine("TOTAL FINAL: ${pesos(totales.total)}")
            appendLine("Estado: ${estado.etiqueta}")
            appendLine("Notas: $notaVisible")
            append("----------------------------------------")
        }
    }
}

fun mostrarReflection(tipo: Class<*>) {
    println("\n=== REFLECTION: ${tipo.simpleName} ===")
    println("Propiedades:")
    var claseActual: Class<*>? = tipo
    while (claseActual != null && claseActual != Any::class.java) {
        claseActual.declaredFields.filterNot { it.isSynthetic }
            .sortedBy { it.name }
            .forEach { println(" - ${it.name}: ${it.type.simpleName}") }
        claseActual = claseActual.superclass
    }
    println("Metodos:")
    tipo.declaredMethods.filterNot { it.isSynthetic }
        .sortedBy { it.name }
        .forEach { println(" - ${it.name}()") }
}

class Inventario(cantidadesIniciales: Map<String, Int>) {
    private val existencias = cantidadesIniciales.toMutableMap()

    init {
        require(existencias.values.all { it >= 0 }) {
            "No se admiten existencias negativas"
        }
    }

    fun mostrar(): String = existencias.entries.joinToString("\n") {
        "${it.key}: ${it.value} unidades"
    }

    fun reabastecer(ingrediente: String, cantidad: Int) {
        require(cantidad > 0) { "La cantidad a reabastecer debe ser positiva" }
        existencias[ingrediente] = (existencias[ingrediente] ?: 0) + cantidad
    }

    fun cantidadesParaLote(productos: List<Producto>): Map<String, Int> {
        val requeridos = mutableMapOf<String, Int>()
        for (producto in productos) {
            for ((ingrediente, cantidad) in producto.ingredientes) {
                requeridos[ingrediente] = (requeridos[ingrediente] ?: 0) + cantidad
            }
        }
        return requeridos
    }

    fun prepararLote(productos: List<Producto>): Boolean {
        val requeridos = cantidadesParaLote(productos)
        val faltantes = requeridos.filter { (ingrediente, cantidad) ->
            cantidad > (existencias[ingrediente] ?: 0)
        }
        if (faltantes.isNotEmpty()) {
            println("Pedido rechazado: ingredientes insuficientes:")
            faltantes.forEach { (ingrediente, necesario) ->
                val disponible = existencias[ingrediente] ?: 0
                println(
                    " - $ingrediente: disponible $disponible, requerido $necesario, " +
                            "faltan ${necesario - disponible}"
                )
            }
            println("Pasos: revisar faltantes, reabastecer y reintentar el pedido.")
            return false
        }
        for ((ingrediente, cantidad) in requeridos) {
            existencias[ingrediente] = (existencias[ingrediente] ?: 0) - cantidad
        }
        println("Pedido aceptado: inventario actualizado.")
        return true
    }
}

fun asignarRepartidor(
    pedido: Pedido,
    repartidores: List<Repartidor>
): Repartidor? {
    val seleccionado = repartidores.filter { it.disponible }
        .minByOrNull { it.distanciaKm }
    seleccionado?.entregarPedido(pedido)
    return seleccionado
}

fun registrarCantidad(texto: String): Int? {
    val numero = texto.toIntOrNull()
    if (numero == null) {
        println("Error de entrada: '$texto' no es un numero entero valido")
        return null
    }
    if (!cantidadPermitida(numero)) {
        println("Error: la cantidad debe estar entre 1 y 100")
        return null
    }
    return numero
}

fun main() {
    println("=== SISTEMA DE GESTION | PIZZERIA KOTLIN ===")
    val margarita = PizzaPromocionable(
        "Margarita", 8000, mapOf("Masa" to 1, "Queso" to 2, "Tomate" to 1)
    )
    val pepperoni = Producto(
        "Pepperoni", 10000, mapOf("Masa" to 1, "Queso" to 2, "Pepperoni" to 2)
    )
    val vegetariana = PizzaPromocionable(
        "Vegetariana", 9000, mapOf("Masa" to 1, "Queso" to 1, "Verduras" to 2)
    )
    val catalogo: List<Producto> = listOf(margarita, pepperoni, vegetariana)

    println("\nCATALOGO")
    catalogo.forEach { println(it.detallarProducto()) }

    println("\n=== PRODUCTOS PROMOCIONABLES ===")
    catalogo.filter { esPromocionable(it) }
        .forEach { println(" - ${it.nombre}: promocion especial disponible") }

    val inventario = Inventario(
        mapOf(
            "Masa" to 8, "Queso" to 12, "Tomate" to 3,
            "Pepperoni" to 1, "Verduras" to 6
        )
    )
    println("\nINVENTARIO INICIAL")
    println(inventario.mostrar())

    val ana = Cliente("Ana Perez", "+56 (912) 345-6789", "ana@example.com")
    println("\n=== REGISTRO DE CLIENTE ===")
    println("Ingrese los datos de Luis Soto.")
    val correoLuis = solicitarCorreoValido()
    val telefonoLuis = solicitarTelefonoValido()
    val luis = Cliente("Luis Soto", telefonoLuis, correoLuis)
    // Set aplica equals() y hashCode() para evitar clientes duplicados.
    val anaRepetida = Cliente("ANA PEREZ", "+56 (999) 111-2222", "ANA@example.com")
    val clientes = linkedSetOf(ana, luis, anaRepetida)

    println("\nCLIENTES REGISTRADOS")
    clientes.forEach { println(it.descripcion()) }
    println("Clientes unicos: ${clientes.size}")

    // Demostracion explicita de la desestructuracion del cliente.
    val (nombreCliente, correoCliente, telefonoCliente) = ana
    println("\n=== DESESTRUCTURACION DE CLIENTE ===")
    println("Nombre: $nombreCliente")
    println("Correo: $correoCliente")
    println("Telefono: $telefonoCliente")

    val repartidores = listOf(
        Repartidor("Carla", "+56911111111", 3.0),
        Repartidor("Diego", "+56922222222", 1.2)
    )

    println("\n=== REGISTRO DE CANTIDAD ===")
    println("Cantidad de pizzas Margarita para pedido #1:")
    val cantidadMargarita = solicitarCantidadValida()
    val productosPedido1 = List(cantidadMargarita) { margarita } + listOf(vegetariana)
    if (!cantidadPermitida(productosPedido1.size)) {
        println("Error: el pedido completo supera las 100 unidades.")
        return
    }

    val pedido1 = Pedido(
        numero = 1, cliente = ana, productos = productosPedido1,
        notas = "Sin cebolla", fecha = LocalDate.of(2026, 11, 4)
    )
    if (inventario.prepararLote(pedido1.productos)) {
        pedido1.cambiarEstado(EstadoPedido.LISTO)
        asignarRepartidor(pedido1, repartidores)
    }

    val pedido2 = Pedido(
        numero = 2, cliente = luis, productos = listOf(pepperoni),
        fecha = LocalDate.of(2026, 12, 1)
    )
    if (!inventario.prepararLote(pedido2.productos)) {
        println("Reponiendo 2 unidades de Pepperoni...")
        inventario.reabastecer("Pepperoni", 2)
        if (inventario.prepararLote(pedido2.productos)) {
            pedido2.cambiarEstado(EstadoPedido.LISTO)
        }
    } else {
        pedido2.cambiarEstado(EstadoPedido.LISTO)
    }

    val pedidosDelDia = arrayOf(pedido1, pedido2)
    println("\n=== REPORTE DE PEDIDOS ===")
    for (pedido in pedidosDelDia) {
        println(
            "#${pedido.numero} | ${pedido.cliente.nombre} | " +
                    "Productos: ${pedido.productos.size} | " +
                    "${pesos(pedido.calcularTotal().total)} | ${pedido.estado.etiqueta}"
        )
    }
    val despachados = pedidosDelDia.count { it.estado == EstadoPedido.EN_CAMINO }
    println(
        "En camino: $despachados | " +
                "Pendientes de despacho: ${pedidosDelDia.size - despachados}"
    )
    pedidosDelDia.forEach { println("\n${it.resumen()}") }

    println("\nINVENTARIO FINAL")
    println(inventario.mostrar())

    // Operador +: demostracion sin alterar los pedidos ni el inventario.
    val pedidoAdicional = Pedido(
        numero = 3, cliente = ana, productos = listOf(margarita),
        notas = "Extra salsa", fecha = pedido1.fecha
    )
    val pedidoCombinado = pedido1 + pedidoAdicional
    println("\n=== PEDIDOS COMBINADOS CON + ===")
    println("Pedido #${pedido1.numero} + pedido #${pedidoAdicional.numero}")
    println("Cantidad combinada: ${pedidoCombinado.productos.size}")
    println("Subtotal combinado: ${pesos(pedidoCombinado.calcularTotal().subtotal)}")
    println("Descuento combinado: ${pesos(pedidoCombinado.calcularTotal().descuento)}")
    println("Total combinado: ${pesos(pedidoCombinado.calcularTotal().total)}")
    println("El pedido combinado se muestra sin descontar nuevamente inventario.")

    // El operador == compara nombre y categoria mediante equals().
    val margaritaDuplicada = Producto(
        "margarita", 8500, mapOf("Masa" to 1), categoria = "PIZZA"
    )
    val margaritaBebida = Producto(
        "Margarita", 2500, emptyMap(), categoria = "bebida"
    )
    println("\n=== IGUALDAD DE PRODUCTOS CON == ===")
    println("Misma pizza (nombre y categoria): ${margarita == margaritaDuplicada}")
    println("Igual nombre, distinta categoria: ${margarita == margaritaBebida}")
    println("HashCode coherente: ${margarita.hashCode() == margaritaDuplicada.hashCode()}")

    // Set elimina productos equivalentes por nombre y categoria.
    val productosUnicos = linkedSetOf(margarita, margaritaDuplicada, pepperoni, vegetariana)
    println("Productos unicos en el registro: ${productosUnicos.size}")
    println("Duplicado de Margarita rechazado: ${productosUnicos.size == 3}")

    println("\n=== IGUALDAD DE CLIENTES ===")
    println("Ana y registro repetido iguales: ${ana == anaRepetida}")
    println("HashCode de clientes coherente: ${ana.hashCode() == anaRepetida.hashCode()}")
    println("Clientes unicos registrados: ${clientes.size}")

    // Demostracion explicita de la desestructuracion del pedido combinado.
    val (clientePedido, productosCombinados, totalCombinado) = pedidoCombinado
    println("\n=== DESESTRUCTURACION DE PEDIDO ===")
    println("Cliente: ${clientePedido.nombre}")
    println("Cantidad de productos: ${productosCombinados.size}")
    println("Total: ${pesos(totalCombinado)}")

    mostrarReflection(Cliente::class.java)
    mostrarReflection(Pedido::class.java)

    println("\n=== VALIDACION DE ENTRADAS ===")
    registrarCantidad("0")
    registrarCantidad("101")
    registrarCantidad("no es numero")
    try {
        Producto("Pizza invalida", -500, mapOf("Masa" to 1))
    } catch (e: IllegalArgumentException) {
        println("Error de precio: ${e.message}")
    }
    try {
        inventario.reabastecer("Queso", -2)
    } catch (e: IllegalArgumentException) {
        println("Error de inventario: ${e.message}")
    }
    clientes.forEach {
        println("Notificacion simulada enviada a ${it.correo}")
    }
    println("\n=== FIN DEL PROGRAMA ===")
}

