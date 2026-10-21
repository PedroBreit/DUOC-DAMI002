fun formatoPesos(valor: Number): String {
    return "$" + "%,.0f".format(
        java.util.Locale.US,
        valor.toDouble()
    ).replace(",", ".")
}


fun main() {

    println("========================================")
    println("       PIZZERIA - SISTEMA KOTLIN")
    println("========================================")

    // 1. REGISTRO DEL CLIENTE

    println("\n--- DATOS DEL CLIENTE ---")

    print("Ingrese nombre: ")
    val nombre: String = readLine()?.trim()
        ?.takeIf { it.isNotEmpty() } ?: "Cliente"

    print("Ingrese direccion: ")
    val direccion: String = readLine()?.trim()
        ?.takeIf { it.isNotEmpty() } ?: "Sin direccion"

    // VALIDACION DEL TELEFONO

    var telefono: String = ""

    while (true) {
        print("Ingrese telefono (9 digitos, comienza con 9): ")
        val telefonoIngresado: String = readLine()?.trim() ?: ""

        if (telefonoIngresado.matches(Regex("9[0-9]{8}"))) {
            telefono = telefonoIngresado
            println("Telefono registrado correctamente.")
            break
        } else {
            println("ERROR: telefono invalido.")
            println("Debe ingresar 9 digitos y comenzar con 9.")
            println("Ejemplo: 912345678")
        }
    }

    // 2. INVENTARIO DE INGREDIENTES

    var masa: Int = 20
    var salsa: Int = 20
    var queso: Int = 15
    var pepperoni: Int = 8
    var jamon: Int = 10
    var pina: Int = 0
    var champinones: Int = 6

    // 3. VARIABLES DEL PEDIDO

    val idPedido: Int = 1001
    var nombrePizza: String = ""
    var precioUnitario: Int = 0
    var cantidadPizzas: Int = 0
    var subtotal: Int = 0
    var descuento: Double = 0.0
    var totalPedido: Double = 0.0
    var estadoPedido: String = "Pendiente"
    var repartidorAsignado: String = "Sin asignar"

    // 4. MENU REPETITIVO

    while (true) {

        println("\n========== MENU DE PIZZAS ==========")
        println("1. Pepperoni   - \$10.000")
        println("2. Hawaiana    - \$11.000")
        println("3. Campestre   - \$12.000")
        println("4. Especial    - \$13.000")
        println("0. Cancelar pedido")
        println("====================================")


        print("Seleccione una pizza (0-4): ")

        val opcion: Int = try {
            readLine()?.trim()?.toInt() ?: -1
        } catch (e: NumberFormatException) {
            println("Error: debe ingresar un numero valido.")
            -1
        }

        if (opcion == 0) {
            estadoPedido = "Cancelado"
            println("Pedido cancelado por el cliente.")
            break
        }

        if (opcion !in 1..4) {
            println("Opcion invalida. Intente nuevamente.")
            continue
        }

        nombrePizza = when (opcion) {
            1 -> "Pepperoni"
            2 -> "Hawaiana"
            3 -> "Campestre"
            else -> "Especial"
        }

        precioUnitario = when (opcion) {
            1 -> 10000
            2 -> 11000
            3 -> 12000
            else -> 13000
        }

        println("\nPizza seleccionada: $nombrePizza")


        print("Ingrese cantidad de pizzas: ")

        cantidadPizzas = try {
            readLine()?.trim()?.toInt() ?: 0
        } catch (e: NumberFormatException) {
            println("Error: debe ingresar una cantidad numerica.")
            0
        }

        if (cantidadPizzas <= 0) {
            println("Cantidad invalida. Volviendo al menu...")
            continue
        }

        // 5. CALCULAR INGREDIENTES NECESARIOS

        val necesitaMasa = cantidadPizzas
        val necesitaSalsa = cantidadPizzas
        val necesitaQueso = cantidadPizzas

        val necesitaPepperoni =
            if (opcion == 1 || opcion == 4) cantidadPizzas else 0

        val necesitaJamon =
            if (opcion == 2 || opcion == 3) cantidadPizzas else 0

        val necesitaPina =
            if (opcion == 2) cantidadPizzas else 0

        val necesitaChampinones =
            if (opcion == 3 || opcion == 4) cantidadPizzas else 0

        // 6. VERIFICAR STOCK

        val stockSuficiente: Boolean =
            masa >= necesitaMasa &&
                    salsa >= necesitaSalsa &&
                    queso >= necesitaQueso &&
                    pepperoni >= necesitaPepperoni &&
                    jamon >= necesitaJamon &&
                    pina >= necesitaPina &&
                    champinones >= necesitaChampinones

        if (!stockSuficiente) {

            println("\nNO HAY INGREDIENTES SUFICIENTES")
            println("Ingredientes faltantes:")

            if (masa < necesitaMasa)
                println("- Masa: ${necesitaMasa - masa}")

            if (salsa < necesitaSalsa)
                println("- Salsa: ${necesitaSalsa - salsa}")

            if (queso < necesitaQueso)
                println("- Queso: ${necesitaQueso - queso}")

            if (pepperoni < necesitaPepperoni)
                println("- Pepperoni: ${necesitaPepperoni - pepperoni}")

            if (jamon < necesitaJamon)
                println("- Jamon: ${necesitaJamon - jamon}")

            if (pina < necesitaPina)
                println("- Pina: ${necesitaPina - pina}")

            if (champinones < necesitaChampinones)
                println("- Champinones: ${necesitaChampinones - champinones}")

            println("\nSeleccione otra pizza o cambie la cantidad.")
            continue
        }

        // 7. DESCONTAR INGREDIENTES

        masa -= necesitaMasa
        salsa -= necesitaSalsa
        queso -= necesitaQueso
        pepperoni -= necesitaPepperoni
        jamon -= necesitaJamon
        pina -= necesitaPina
        champinones -= necesitaChampinones

        // 8. CALCULAR TOTAL Y DESCUENTO

        subtotal = precioUnitario * cantidadPizzas

        descuento = if (cantidadPizzas > 3) {
            subtotal * 0.10
        } else {
            0.0
        }

        totalPedido = subtotal - descuento
        estadoPedido = "Preparacion"
        repartidorAsignado = "Carlos Gonzalez"

        println("\nPEDIDO CONFIRMADO CORRECTAMENTE")
        break
    }

    // 9. RESUMEN DEL PEDIDO CONFIRMADO

    if (estadoPedido == "Preparacion") {

        println("\n========================================")
        println("           RESUMEN DEL PEDIDO")
        println("========================================")

        println("Pedido N: $idPedido")
        println("Cliente: $nombre")
        println("Direccion: $direccion")
        println("Telefono: $telefono")

        println("\nPizza: $nombrePizza")
        println("Precio unitario: ${formatoPesos(precioUnitario)}")
        println("Cantidad: $cantidadPizzas")

        println("\nSubtotal: ${formatoPesos(subtotal)}")
        println("Descuento: ${formatoPesos(descuento)}")
        println("Total a pagar: ${formatoPesos(totalPedido)}")
        println("Estado: $estadoPedido")
        println("Repartidor: $repartidorAsignado")

        println("\n--- INVENTARIO RESTANTE ---")
        println("Masa: $masa")
        println("Salsa: $salsa")
        println("Queso: $queso")
        println("Pepperoni: $pepperoni")
        println("Jamon: $jamon")
        println("Pina: $pina")
        println("Champinones: $champinones")

    } else {
        println("\nNo se genero ningun pedido.")
    }

    println("\n========================================")
    println("           FIN DEL PROGRAMA")
    println("========================================")
}
