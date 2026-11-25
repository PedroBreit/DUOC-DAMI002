
package com.example.pizzeriaapp.domain

import java.util.Locale

open class Producto(
    val nombre: String,
    val precio: Int,
    val ingredientes: Map<String, Int>,
    val categoria: String = "pizza"
) {
    init {
        require(nombre.isNotBlank()) {
            "El nombre del producto no puede estar vacio"
        }
        require(categoria.isNotBlank()) {
            "La categoria no puede estar vacia"
        }
        require(precio >= 0) {
            "El precio debe ser mayor o igual a cero"
        }
        require(ingredientes.values.all { it > 0 }) {
            "Las cantidades de ingredientes deben ser positivas"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Producto) return false

        return nombre.trim().equals(
            other.nombre.trim(),
            ignoreCase = true
        ) && categoria.trim().equals(
            other.categoria.trim(),
            ignoreCase = true
        )
    }

    override fun hashCode(): Int {
        var result = nombre.trim()
            .lowercase(Locale.ROOT).hashCode()

        result = 31 * result +
                categoria.trim()
                    .lowercase(Locale.ROOT).hashCode()

        return result
    }

    fun detallarProducto(): String {
        val detalleIngredientes = ingredientes.entries
            .joinToString {
                "${it.key} (${it.value})"
            }

        return "$nombre: $$precio | Categoria: $categoria" +
                " | Ingredientes: $detalleIngredientes"
    }
}
