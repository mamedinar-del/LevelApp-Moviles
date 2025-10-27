package com.example.levelapp.model

data class CartItem(
    val id: Long = 0,
    val productId: Long,
    val nombre: String,
    val precio: Double,
    val imagenUri: String,
    var quantity: Int
)