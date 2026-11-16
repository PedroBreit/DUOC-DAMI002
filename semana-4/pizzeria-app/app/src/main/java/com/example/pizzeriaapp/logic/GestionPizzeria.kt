package com.example.pizzeriaapp.logic

import com.example.pizzeriaapp.domain.Inventario
import com.example.pizzeriaapp.domain.Pedido
import com.example.pizzeriaapp.domain.Producto
import com.example.pizzeriaapp.domain.Repartidor

class GestionPizzeria {

    val inventario = Inventario(
        mapOf(
            "Masa" to 8,
            "Queso" to 12,
            "Tomate" to 3,
            "Pepperoni" to 1,
            "Verduras" to 6
        )
    )

    fun faltantes(
        productos: List<Producto>
    ): Map<String, Int> =
        inventario.faltantesParaLote(productos)

    fun verificarDisponibilidad(
        productos: List<Producto>
    ): Map<String, Int> =
        inventario.cantidadesParaLote(productos)

    fun confirmarPreparacion(
        productos: List<Producto>
    ): Boolean {
        if (!cantidadPermitida(productos.size)) {
            return false
        }

        return inventario.prepararLote(productos)
    }

    fun consultarInventario(): String =
        inventario.mostrar()

    fun reabastecer(
        ingrediente: String,
        cantidad: Int
    ) {
        inventario.reabastecer(ingrediente, cantidad)
    }

    fun asignarRepartidor(
        pedido: Pedido,
        repartidores: List<Repartidor>
    ): Repartidor? {
        val seleccionado = repartidores
            .filter { it.disponible }
            .minByOrNull { it.distanciaKm }

        seleccionado?.entregarPedido(pedido)

        return seleccionado
    }
}
