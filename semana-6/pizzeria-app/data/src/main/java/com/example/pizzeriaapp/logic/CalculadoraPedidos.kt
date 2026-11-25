package com.example.pizzeriaapp.logic

import com.example.pizzeriaapp.domain.Producto
import com.example.pizzeriaapp.domain.Totales
import com.example.pizzeriaapp.domain.esPromocionable
import java.time.LocalDate
import kotlin.math.roundToInt

const val IVA = 0.19
const val DESCUENTO_CUATRO_PIZZAS = 0.10
const val DESCUENTO_PROMOCIONAL = 0.05

val INICIO_PROMOCION: LocalDate =
    LocalDate.of(2026, 11, 1)

val FIN_PROMOCION: LocalDate =
    LocalDate.of(2026, 11, 30)

fun fechaEnPromocion(
    fechaPedido: LocalDate,
    inicio: LocalDate,
    termino: LocalDate
): Boolean {
    require(!inicio.isAfter(termino)) {
        "La fecha inicial no puede superar la final"
    }

    return fechaPedido in inicio..termino
}

fun calcularTotales(
    productos: List<Producto>,
    fechaPedido: LocalDate
): Totales {

    val subtotal = productos.sumOf { it.precio }

    val descuentoCantidad =
        if (productos.size >= 4) {
            (subtotal * DESCUENTO_CUATRO_PIZZAS)
                .roundToInt()
        } else {
            0
        }

    val subtotalPromocionable = productos
        .filter { esPromocionable(it) }
        .sumOf { it.precio }

    val descuentoFecha =
        if (
            fechaEnPromocion(
                fechaPedido,
                INICIO_PROMOCION,
                FIN_PROMOCION
            )
        ) {
            val factorCantidad =
                if (productos.size >= 4) {
                    1.0 - DESCUENTO_CUATRO_PIZZAS
                } else {
                    1.0
                }

            (
                    subtotalPromocionable *
                            factorCantidad *
                            DESCUENTO_PROMOCIONAL
                    ).roundToInt()
        } else {
            0
        }

    val descuento =
        descuentoCantidad + descuentoFecha

    val neto = subtotal - descuento

    val impuestos = (neto * IVA).roundToInt()

    return Totales(
        subtotal = subtotal,
        descuento = descuento,
        impuestos = impuestos,
        total = neto + impuestos
    )
}
