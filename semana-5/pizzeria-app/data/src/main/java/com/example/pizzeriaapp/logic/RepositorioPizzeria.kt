package com.example.pizzeriaapp.logic

import com.example.pizzeriaapp.domain.Cliente
import com.example.pizzeriaapp.domain.Pedido

// Resumen general de la pizzeria.
data class ResumenPizzeria(
    val totalClientes: Int = 0,
    val totalPedidos: Int = 0,
    val ultimoCliente: String = "Sin clientes registrados"
)

// Semana 5: repositorio centralizado de datos.
class RepositorioPizzeria {

    private val clientes = mutableListOf<Cliente>()
    private val pedidos = mutableListOf<Pedido>()

    // Registra un cliente evitando duplicados.
    fun registrarCliente(cliente: Cliente): Boolean {

        if (clientes.any { it == cliente }) {
            return false
        }

        clientes.add(cliente)
        return true
    }

    // Registra un pedido confirmado.
    fun registrarPedido(pedido: Pedido): Boolean {

        if (pedidos.any { it.numero == pedido.numero }) {
            return false
        }

        require(clientes.any { it == pedido.cliente }) {
            "El cliente debe estar registrado."
        }

        pedidos.add(pedido)
        return true
    }

    // Obtiene la lista de clientes registrados.
    fun obtenerClientes(): List<Cliente> {
        return clientes.toList()
    }

    // Obtiene la lista de pedidos realizados.
    fun obtenerPedidos(): List<Pedido> {
        return pedidos.toList()
    }

    // Obtiene el siguiente numero de pedido.
    fun siguienteNumeroPedido(): Int {
        return (pedidos.maxOfOrNull { it.numero } ?: 0) + 1
    }

    // Obtiene el total de clientes.
    fun totalClientes(): Int {
        return clientes.size
    }

    // Obtiene el total de pedidos.
    fun totalPedidos(): Int {
        return pedidos.size
    }

    // Obtiene el ultimo cliente registrado.
    fun ultimoClienteRegistrado(): String {
        return clientes.lastOrNull()?.nombre
            ?: "Sin clientes registrados"
    }

    // Genera el resumen actualizado para la interfaz.
    fun obtenerResumen(): ResumenPizzeria {

        return ResumenPizzeria(
            totalClientes = totalClientes(),
            totalPedidos = totalPedidos(),
            ultimoCliente = ultimoClienteRegistrado()
        )
    }
}
