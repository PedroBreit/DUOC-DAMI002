
package com.example.pizzeriaapp.domain

open class Usuario(
    val nombre: String,
    val contacto: String
) {
    open fun descripcion(): String {
        return "$nombre ($contacto)"
    }
}
