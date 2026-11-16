package com.example.pizzeriaapp.domain

class Repartidor(
    nombre: String,
    telefono: String,
    val distanciaKm: Double
) : Usuario(nombre, telefono) {

    // Indica si puede recibir pedidos.
    var disponible: Boolean = true
        private set

    // Asigna un pedido listo al repartidor.
    fun entregarPedido(pedido: Pedido) {
        require(disponible) {
            "El repartidor no está disponible"
        }

        require(pedido.estado == EstadoPedido.LISTO) {
            "El pedido debe estar listo para despacharse"
        }

        pedido.cambiarEstado(EstadoPedido.EN_CAMINO)
        disponible = false
    }

    // Libera al repartidor al entregar su pedido.
    fun marcarEntregado() {
        require(!disponible) {
            "El repartidor ya está disponible"
        }

        disponible = true
    }
}
