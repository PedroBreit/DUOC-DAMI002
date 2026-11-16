
package com.example.pizzeriaapp.model

data class Producto(
    val id: Int,
    val nombre: String,
    val precio: Int,
    val descripcion: String
) {
    init {
        require(id > 0) { "El ID debe ser positivo" }
        require(nombre.isNotBlank()) { "El nombre es obligatorio" }
        require(precio > 0) { "El precio debe ser mayor a cero" }
    }

    fun mostrarDetalle(): String {
        return "$nombre - $$precio"
    }
}
