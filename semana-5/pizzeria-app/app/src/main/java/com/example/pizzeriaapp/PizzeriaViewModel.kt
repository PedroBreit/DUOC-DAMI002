package com.example.pizzeriaapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzeriaapp.domain.Cliente
import com.example.pizzeriaapp.domain.Pedido
import com.example.pizzeriaapp.logic.RepositorioPizzeria
import com.example.pizzeriaapp.logic.ResumenPizzeria
import kotlinx.coroutines.launch

// Semana 5: conexion entre la logica y la interfaz.
class PizzeriaViewModel : ViewModel() {

    private val repositorio = RepositorioPizzeria()

    // Estado observable del resumen.
    private val _resumen = MutableLiveData(
        ResumenPizzeria()
    )

    val resumen: LiveData<ResumenPizzeria> = _resumen

    // Lista observable de clientes.
    private val _clientes = MutableLiveData<List<Cliente>>(
        emptyList()
    )

    val clientes: LiveData<List<Cliente>> = _clientes

    // Lista observable de pedidos.
    private val _pedidos = MutableLiveData<List<Pedido>>(
        emptyList()
    )

    val pedidos: LiveData<List<Pedido>> = _pedidos

    init {
        cargarDatosIniciales()
    }

    // Conserva los clientes originales de Semana 4.
    private fun cargarDatosIniciales() {

        repositorio.registrarCliente(
            Cliente(
                "Ana Perez",
                "+56 (912) 345-6789",
                "ana@example.com"
            )
        )

        repositorio.registrarCliente(
            Cliente(
                "Luis Soto",
                "+56 (912) 111-2222",
                "luis@example.com"
            )
        )

        actualizarDatos()
    }

    // Registra un cliente y actualiza la interfaz.
    fun registrarCliente(cliente: Cliente): Boolean {

        val registrado = repositorio.registrarCliente(cliente)

        if (registrado) {
            actualizarDatos()
        }

        return registrado
    }

    // Registra un pedido confirmado.
    fun registrarPedido(pedido: Pedido): Boolean {

        val registrado = repositorio.registrarPedido(pedido)

        if (registrado) {
            actualizarDatos()
        }

        return registrado
    }

    // Obtiene el siguiente numero disponible.
    fun siguienteNumeroPedido(): Int {
        return repositorio.siguienteNumeroPedido()
    }

    // Actualiza los datos observados por Jetpack Compose.
    private fun actualizarDatos() {

        viewModelScope.launch {

            _clientes.value = repositorio.obtenerClientes()

            _pedidos.value = repositorio.obtenerPedidos()

            _resumen.value = repositorio.obtenerResumen()
        }
    }
}
