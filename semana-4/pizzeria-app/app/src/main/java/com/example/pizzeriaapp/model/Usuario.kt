
package com.example.pizzeriaapp.model

data class Usuario(
    val id: Int,
    val nombre: String,
    val correo: String
) {
    init {
        require(id > 0) {
            "El ID del usuario debe ser positivo"
        }
        require(nombre.isNotBlank()) {
            "El nombre es obligatorio"
        }
        require(
            Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                .matches(correo)
        ) {
            "El correo electrónico no es válido"
        }
    }

    fun mostrarInformacion(): String {
        return "Cliente: $nombre\nCorreo: $correo"
    }
}
