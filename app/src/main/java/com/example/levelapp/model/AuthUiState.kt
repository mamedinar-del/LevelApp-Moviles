package com.example.levelapp.model

data class AuthUiState(
    val email: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",

    val errorEmail: String? = null,
    val errorContrasena: String? = null,
    val errorConfirmarContrasena: String? = null,

    val mensaje: String? = null,
    val registroExitoso: Boolean = false
)