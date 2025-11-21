package com.example.levelapp.model

data class AuthUiState(
    val email: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val rut: String = "",

    val errorEmail: String? = null,
    val errorContrasena: String? = null,
    val errorConfirmarContrasena: String? = null,
    val errorNombre: String? = null,
    val errorApellido: String? = null,
    val errorRut: String? = null,

    val mensaje: String? = null,
    val registroExitoso: Boolean = false,

    val usuarioActual: User? = null,
    val mostrarDialogoFoto: Boolean = false,

    val listaUsuarios: List<User> = emptyList()
)