
package com.example.pizzeriaapp.domain

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Promocionable

fun esPromocionable(producto: Producto): Boolean {
    return producto.javaClass.isAnnotationPresent(
        Promocionable::class.java
    )
}
