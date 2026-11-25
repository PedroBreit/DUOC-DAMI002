
package com.example.pizzeriaapp.logic

// Validación de correos electrónicos mediante Regex.
fun correoEsValido(correo: String): Boolean {
    val patron = Regex(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )
    return patron.matches(correo.trim())
}

// Valida y normaliza un correo.
fun validarCorreo(correo: String): String {
    require(correoEsValido(correo)) {
        "Correo inválido. Ejemplo: nombre@dominio.com"
    }
    return correo.trim().lowercase()
}

// Formato original: +XX (XXX) XXX-XXXX.
// Se requieren exactamente 12 dígitos.
fun formatearTelefono(telefono: String): String? {
    val digitos = telefono.filter { it.isDigit() }

    if (!Regex("^\\d{12}$").matches(digitos)) {
        return null
    }

    val pais = digitos.substring(0, 2)
    val area = digitos.substring(2, 5)
    val central = digitos.substring(5, 8)
    val numero = digitos.substring(8, 12)

    return "+$pais ($area) $central-$numero"
}

fun validarTelefono(telefono: String): String {
    return formatearTelefono(telefono)
        ?: throw IllegalArgumentException(
            "Teléfono inválido. Formato: +XX (XXX) XXX-XXXX"
        )
}

// Rango utilizado en la Semana 3.
fun cantidadPermitida(cantidad: Int): Boolean {
    return cantidad in 1..100
}

// Conversión segura para entradas desde Android.
fun registrarCantidad(texto: String): Int? {
    val cantidad = texto.toIntOrNull()
    return cantidad?.takeIf { cantidadPermitida(it) }
}
