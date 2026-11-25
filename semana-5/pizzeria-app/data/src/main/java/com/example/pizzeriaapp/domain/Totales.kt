package com.example.pizzeriaapp.domain

data class Totales(
    val subtotal: Int,
    val descuento: Int,
    val impuestos: Int,
    val total: Int
)
