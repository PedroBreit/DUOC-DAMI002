package com.example.pizzeriaapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzeriaapp.domain.Cliente
import com.example.pizzeriaapp.domain.Pedido
import com.example.pizzeriaapp.logic.RepositorioPizzeria
import com.example.pizzeriaapp.logic.ResumenPizzeria
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PizzeriaViewModel : ViewModel() {

    private val repositorio = RepositorioPizzeria()

    // Datos observables del resumen.
    private val _resumen =
        MutableLiveData(ResumenPizzeria())

    val resumen: LiveData<ResumenPizzeria> = _resumen

    // Clientes registrados.
    private val _clientes =
        MutableLiveData<List<Cliente>>(emptyList())

    val clientes: LiveData<List<Cliente>> = _clientes

    // Pedidos registrados.
    private val _pedidos =
        MutableLiveData<List<Pedido>>(emptyList())

    val pedidos: LiveData<List<Pedido>> = _pedidos

    // Estado de carga.
    private val _cargando =
        MutableLiveData(false)

    val cargando: LiveData<Boolean> = _cargando

    // Mensaje contextual.
    private val _mensajeCarga =
        MutableLiveData("")

    val mensajeCarga: LiveData<String> = _mensajeCarga

    // Manejo de errores.
    private val _errorCarga =
        MutableLiveData("")

    val errorCarga: LiveData<String> = _errorCarga

    // Control de consultas simultaneas.
    private var trabajoCarga: Job? = null

    // Identificador de la solicitud vigente.
    private var solicitudCarga = 0L

    init {

        cargarDatosIniciales()
    }

    // Carga los clientes originales.
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

        actualizarDatos(
            "Cargando datos iniciales..."
        )
    }

    // Registro de clientes.
    fun registrarCliente(
        cliente: Cliente
    ): Boolean {

        val registrado =
            repositorio.registrarCliente(cliente)

        if (registrado) {

            actualizarDatos(
                "Actualizando clientes..."
            )
        }

        return registrado
    }

    // Registro de pedidos.
    fun registrarPedido(
        pedido: Pedido
    ): Boolean {

        val registrado =
            repositorio.registrarPedido(pedido)

        if (registrado) {

            cargarPedidos()
        }

        return registrado
    }

    // Consulta de clientes.
    fun cargarClientes() {

        actualizarDatos(
            "Cargando clientes..."
        )
    }

    // Consulta de pedidos.
    fun cargarPedidos() {

        actualizarDatos(
            "Actualizando pedidos..."
        )
    }

    // Generacion del resumen.
    fun generarResumen() {

        actualizarDatos(
            "Generando resumen..."
        )
    }

    // Siguiente numero de pedido.
    fun siguienteNumeroPedido(): Int {

        return repositorio.siguienteNumeroPedido()
    }

    // Semana 6: actualizacion asincrona.
    private fun actualizarDatos(
        mensaje: String
    ) {

        // Identifica la nueva solicitud.
        val solicitudActual = ++solicitudCarga

        // Cancela una consulta anterior.
        trabajoCarga?.cancel()

        _cargando.value = true
        _mensajeCarga.value = mensaje
        _errorCarga.value = ""

        trabajoCarga = viewModelScope.launch {

            try {

                // Demora demostrativa para visualizar
                // el progreso del repositorio en memoria.
                delay(1200)

                // Consulta al modulo data.
                val clientesActualizados =
                    repositorio.obtenerClientes()

                val pedidosActualizados =
                    repositorio.obtenerPedidos()

                val resumenActualizado =
                    repositorio.obtenerResumen()

                // Publica solo los datos de la
                // solicitud vigente.
                if (solicitudActual == solicitudCarga) {

                    _clientes.value =
                        clientesActualizados

                    _pedidos.value =
                        pedidosActualizados

                    _resumen.value =
                        resumenActualizado
                }

            } catch (e: CancellationException) {

                // Respeta la cancelacion de corrutinas.
                throw e

            } catch (e: Exception) {

                if (solicitudActual == solicitudCarga) {

                    _errorCarga.value =
                        "No fue posible cargar los datos."
                }

            } finally {

                // Una consulta antigua no puede
                // finalizar una consulta mas reciente.
                if (solicitudActual == solicitudCarga) {

                    _cargando.value = false
                }
            }
        }
    }
}
