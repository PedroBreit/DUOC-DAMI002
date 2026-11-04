
import java.time.LocalDate

// Validacion de correos electronicos mediante Regex.
fun correoEsValido(correo: String): Boolean {
    val patron = Regex(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )
    return patron.matches(correo.trim())
}

// Solicita nuevamente el correo si es incorrecto.
fun solicitarCorreoValido(): String {
    while (true) {
        print("Ingrese correo electronico: ")
        val correo = readlnOrNull()
            ?: throw IllegalStateException("Entrada finalizada")

        if (correoEsValido(correo)) {
            return correo.trim().lowercase()
        }

        println("Error: formato de correo incorrecto.")
        println("Ejemplo valido: cliente@dominio.com")
    }
}

// Formato solicitado: +XX (XXX) XXX-XXXX.
// Se utilizan 12 digitos: 2 + 3 + 3 + 4.
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

// Solicita nuevamente el telefono si es incorrecto.
fun solicitarTelefonoValido(): String {
    while (true) {
        print("Ingrese telefono (+XX (XXX) XXX-XXXX): ")
        val telefono = readlnOrNull()
            ?: throw IllegalStateException("Entrada finalizada")

        val telefonoFormateado = formatearTelefono(telefono)

        if (telefonoFormateado != null) {
            return telefonoFormateado
        }

        println("Error: el telefono debe contener 12 digitos.")
        println("Ejemplo valido: +56 (912) 345-6789")
    }
}

// Cantidades permitidas: entre 1 y 100.
fun cantidadPermitida(cantidad: Int): Boolean {
    return cantidad in 1..100
}

// Solicita nuevamente la cantidad si es incorrecta.
fun solicitarCantidadValida(): Int {
    while (true) {
        print("Ingrese cantidad de productos (1-100): ")

        val entrada = readlnOrNull()
            ?: throw IllegalStateException("Entrada finalizada")

        val cantidad = entrada.toIntOrNull()

        if (cantidad != null && cantidadPermitida(cantidad)) {
            return cantidad
        }

        println("Error: ingrese un numero entre 1 y 100.")
    }
}

// Verifica el rango de fechas promocionales.
fun fechaEnPromocion(
    fechaPedido: LocalDate,
    inicio: LocalDate,
    termino: LocalDate
): Boolean {
    require(!inicio.isAfter(termino)) {
        "La fecha inicial no puede superar la fecha final"
    }

    return fechaPedido in inicio..termino
}
