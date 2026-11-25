
package com.example.pizzeriaapp.domain

import com.example.pizzeriaapp.logic.validarCorreo
import com.example.pizzeriaapp.logic.validarTelefono
import java.util.Locale

class Cliente(
    nombre: String,
    telefono: String,
    correo: String
) : Usuario(nombre, validarTelefono(telefono)) {

    val correo: String = validarCorreo(correo)

    override fun descripcion(): String {
        return "$nombre | Telefono: $contacto | Correo: $correo"
    }

    // Desestructuracion original de Semana 3
    operator fun component1(): String = nombre
    operator fun component2(): String = correo
    operator fun component3(): String = contacto

    // Dos clientes son iguales si coinciden
    // nombre y correo, sin importar mayusculas.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cliente) return false

        return nombre.trim().equals(
            other.nombre.trim(),
            ignoreCase = true
        ) && correo.equals(
            other.correo,
            ignoreCase = true
        )
    }

    override fun hashCode(): Int {
        var result = nombre.trim()
            .lowercase(Locale.ROOT)
            .hashCode()

        result = 31 * result +
                correo.lowercase(Locale.ROOT).hashCode()

        return result
    }
}
