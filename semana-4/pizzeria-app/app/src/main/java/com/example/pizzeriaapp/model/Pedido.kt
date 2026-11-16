
package com.example.pizzeriaapp.model

class Pedido(
    val id: Int,
    val usuario: Usuario
) {
    private val productos = mutableListOf<Producto>()

    init {
        require(id > 0) {
            "El ID del pedido debe ser positivo"
        }
    }

    fun agregarProducto(producto: Producto) {
        productos.add(producto)
    }

    fun eliminarProducto(producto: Producto): Boolean {
        return productos.remove(producto)
    }

    fun obtenerProductos(): List<Producto> {
        return productos.toList()
    }

    fun calcularTotal(): Int {
        return productos.sumOf { it.precio }
    }

    fun obtenerCantidadProductos(): Int {
        return productos.size
    }

    fun mostrarResumen(): String {
        val detalle = if (productos.isEmpty()) {
            "No hay productos en el pedido"
        } else {
            productos.joinToString("\n") {
                it.mostrarDetalle()
            }
        }

        return """
            Pedido N° $id
            Cliente: ${usuario.nombre}

            Productos:
            $detalle

            Cantidad: ${obtenerCantidadProductos()}
            Total: $${calcularTotal()}
        """.trimIndent()
    }
}
