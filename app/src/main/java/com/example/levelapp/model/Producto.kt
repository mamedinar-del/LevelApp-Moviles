package com.example.levelapp.model

data class Product(
    val id: Long = 0,
    val nombre: String,
    val descripcion: String,
    val stock: Int,
    val precio: Double,
    val categoria: String,
    val imagenUri: String
)