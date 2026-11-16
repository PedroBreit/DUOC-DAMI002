package com.example.pizzeriaapp.domain

import com.example.pizzeriaapp.logic.calcularTotales
import com.example.pizzeriaapp.logic.cantidadPermitida
import java.time.LocalDate

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
            "El pedido debe tener entre 1 y 100 productos"
        }
    }

    fun calcularTotal(): Totales {
        return calcularTotales(productos, fecha)
    }

    // Desestructuracion
    operator fun component1(): Cliente = cliente

    operator fun component2(): List<Producto> = productos

    operator fun component3(): Int =
        calcularTotal().total

    // Sobrecarga del operador +
    operator fun plus(otro: Pedido): Pedido {
        require(cliente == otro.cliente) {
            "Los pedidos deben ser del mismo cliente"
        }

        require(fecha == otro.fecha) {
            "Los pedidos deben tener la misma fecha"
        }

        val unidos = productos + otro.productos

        require(cantidadPermitida(unidos.size)) {
            "El pedido combinado supera el limite"
        }

        return Pedido(
            numero = numero,
            cliente = cliente,
            productos = unidos,
            notas = listOfNotNull(notas, otro.notas)
                .filter { it.isNotBlank() }
                .distinct()
                .joinToString("; ")
                .ifBlank { null },
            fecha = fecha
        )
    }

    fun cambiarEstado(nuevoEstado: EstadoPedido) {
        val permitido = when (estado) {
            EstadoPedido.EN_PREPARACION ->
                nuevoEstado == EstadoPedido.LISTO

            EstadoPedido.LISTO ->
                nuevoEstado == EstadoPedido.EN_CAMINO

            EstadoPedido.EN_CAMINO -> false
        }

        require(permitido) {
            "Cambio de estado no permitido"
        }

        estado = nuevoEstado
    }

    fun resumen(): String {
        val totales = calcularTotal()

        val cantidades = productos
            .groupingBy { it.nombre to it.precio }
            .eachCount()

        return buildString {
            appendLine("PEDIDO #$numero")
            appendLine("Cliente: ${cliente.nombre}")
            appendLine("Fecha: $fecha")
            appendLine("Productos:")

            for ((producto, cantidad) in cantidades) {
                appendLine(
                    "- ${producto.first}: " +
                            "$cantidad x $${producto.second}"
                )
            }

            appendLine("Subtotal: $${totales.subtotal}")
            appendLine("Descuento: $${totales.descuento}")
            appendLine("IVA: $${totales.impuestos}")
            appendLine("TOTAL: $${totales.total}")
            appendLine("Estado: ${estado.etiqueta}")
            appendLine("Notas: ${notas ?: "Sin notas"}")
        }
    }
}
