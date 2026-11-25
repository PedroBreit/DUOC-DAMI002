package com.example.pizzeriaapp.domain

@Promocionable
class PizzaPromocionable(
    nombre: String,
    precio: Int,
    ingredientes: Map<String, Int>
) : Producto(
    nombre,
    precio,
    ingredientes,
    "pizza"
)
