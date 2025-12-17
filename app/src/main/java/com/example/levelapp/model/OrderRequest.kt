package com.example.levelapp.model

data class OrderRequest(
    val userId: Long,
    val items: List<OrderItemRequest>,
    val comuna: String,
    val region: String,
    val direccion: String,
    val numero: String
)

data class OrderItemRequest(
    val productId: Long,
    val quantity: Int
)