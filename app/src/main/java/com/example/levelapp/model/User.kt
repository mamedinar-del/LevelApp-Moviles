package com.example.levelapp.model

data class User(
    val id: Long = 0,
    val nombre: String,
    val apellido: String,
    val rut: String,
    val email: String,
    val contrasena: String,
    val imagenPerfilUri: String? = null
)