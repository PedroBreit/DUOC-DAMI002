package com.example.pizzeriaapp.domain

// Estados posibles durante el ciclo de vida de un pedido.
enum class EstadoPedido(val etiqueta: String) {
    EN_PREPARACION("En preparación"),
    LISTO("Listo"),
    EN_CAMINO("En camino"),
    ENTREGADO("Entregado")
}
