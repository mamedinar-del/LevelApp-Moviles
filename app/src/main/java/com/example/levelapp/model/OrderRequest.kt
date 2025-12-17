package com.example.levelapp.model

data class OrderRequest(
    val userId: Long,
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    val productId: Long,
    val quantity: Int
)