package com.example.levelapp.model

data class Order(
    val id: Long,
    val fecha: String,
    val total: Double,
    val estado: String,
    val usuario: User,
    val detalles: List<OrderDetail>
)

data class OrderDetail(
    val id: Long,
    val cantidad: Int,
    val precioUnitario: Double,
    val producto: Product
)