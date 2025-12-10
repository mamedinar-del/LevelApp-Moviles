package com.example.levelapp.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelapp.data.repository.UserRepository
import com.example.levelapp.model.AuthUiState
import com.example.levelapp.model.User
import com.example.levelapp.ui.utils.copiarImagenAInternalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = UserRepository(application)
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(v: String) = _uiState.update { it.copy(email = v, errorEmail = null) }
    fun onContrasenaChange(v: String) = _uiState.update { it.copy(contrasena = v, errorContrasena = null) }
    fun onConfirmarContrasenaChange(v: String) = _uiState.update { it.copy(confirmarContrasena = v, errorConfirmarContrasena = null) }
    fun onNombreChange(v: String) = _uiState.update { it.copy(nombre = v, errorNombre = null) }
    fun onApellidoChange(v: String) = _uiState.update { it.copy(apellido = v, errorApellido = null) }
    fun onRutChange(v: String) = _uiState.update { it.copy(rut = v, errorRut = null) }

    fun registrar() {
        if (!validarRegistro()) return

        viewModelScope.launch {
            val s = _uiState.value

            val nuevoUsuario = User(
                email = s.email,
                contrasena = s.contrasena,
                nombre = s.nombre,
                apellido = s.apellido,
                rut = s.rut
            )

            userRepository.registrarUsuario(nuevoUsuario)
                .onSuccess {
                    _uiState.update { it.copy(registroExitoso = true, mensaje = "¡Registro exitoso!") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(mensaje = e.message ?: "Error al registrar") }
                }
        }
    }

    fun iniciarSesion(onLoginSuccess: () -> Unit, onAdminLoginSuccess: () -> Unit) {
        val (email, pass) = _uiState.value.email.trim() to _uiState.value.contrasena.trim()

        if (email == "admin" && pass == "admin") { onAdminLoginSuccess(); return }

        if (email.isBlank() || pass.isBlank()) { _uiState.update { it.copy(mensaje = "Campos vacíos") }; return }

        viewModelScope.launch {
            userRepository.iniciarSesion(email, pass)
                .onSuccess { user ->
                    _uiState.update { it.copy(usuarioActual = user) }
                    onLoginSuccess()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(mensaje = e.message ?: "Error al iniciar sesión") }
                }
        }
    }

    fun actualizarFoto(uri: Uri) {
        val user = _uiState.value.usuarioActual ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val rutaPermanente = copiarImagenAInternalStorage(getApplication(), uri)

            if (rutaPermanente != null) {
                userRepository.actualizarFotoPerfil(user.id, rutaPermanente)
                    .onSuccess {
                        _uiState.update { s ->
                            s.copy(usuarioActual = s.usuarioActual?.copy(imagenPerfilUri = rutaPermanente))
                        }
                    }
                    .onFailure {
                        _uiState.update { it.copy(mensaje = "No se pudo subir la foto") }
                    }
            }
        }
    }

    fun cargarTodosLosUsuarios() {
        viewModelScope.launch {
            val usuarios = userRepository.getAllUsers()
            _uiState.update { it.copy(listaUsuarios = usuarios) }
        }
    }

    fun mostrarDialogoFoto(mostrar: Boolean) = _uiState.update { it.copy(mostrarDialogoFoto = mostrar) }
    fun limpiarEstado() { _uiState.value = AuthUiState() }

    private fun validarRegistro(): Boolean {
        var esValido = true
        val s = _uiState.value
        if (s.email.isBlank()) { _uiState.update { it.copy(errorEmail = "Requerido") }; esValido = false }
        if (s.contrasena.length < 6) { _uiState.update { it.copy(errorContrasena = "Mínimo 6 chars") }; esValido = false }
        if (s.contrasena != s.confirmarContrasena) { _uiState.update { it.copy(errorConfirmarContrasena = "No coinciden") }; esValido = false }
        if (s.nombre.isBlank()) { _uiState.update { it.copy(errorNombre = "Requerido") }; esValido = false }
        if (s.apellido.isBlank()) { _uiState.update { it.copy(errorApellido = "Requerido") }; esValido = false }
        if (s.rut.isBlank()) { _uiState.update { it.copy(errorRut = "Requerido") }; esValido = false }
        return esValido
    }
}