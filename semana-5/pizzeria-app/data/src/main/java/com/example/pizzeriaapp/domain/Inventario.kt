package com.example.pizzeriaapp.domain

class Inventario(
    cantidadesIniciales: Map<String, Int>
) {
    private val existencias =
        cantidadesIniciales.toMutableMap()

    init {
        require(existencias.values.all { it >= 0 }) {
            "No se admiten existencias negativas"
        }
    }

    fun mostrar(): String =
        existencias.entries.joinToString("\n") {
            "${it.key}: ${it.value} unidades"
        }

    fun reabastecer(
        ingrediente: String,
        cantidad: Int
    ) {
        require(cantidad > 0) {
            "La cantidad debe ser positiva"
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

    // Devuelve solo los ingredientes faltantes
    fun faltantesParaLote(
        productos: List<Producto>
    ): Map<String, Int> {
        val requeridos = cantidadesParaLote(productos)

        return requeridos.mapNotNull { (ingrediente, cantidad) ->
            val disponible = existencias[ingrediente] ?: 0
            val faltante = cantidad - disponible

            if (faltante > 0) ingrediente to faltante
            else null
        }.toMap()
    }

    fun prepararLote(
        productos: List<Producto>
    ): Boolean {
        if (productos.isEmpty()) return false

        if (faltantesParaLote(productos).isNotEmpty()) {
            return false
        }

        for ((ingrediente, cantidad) in cantidadesParaLote(productos)) {
            existencias[ingrediente] =
                (existencias[ingrediente] ?: 0) - cantidad
        }

        return true
    }
}
