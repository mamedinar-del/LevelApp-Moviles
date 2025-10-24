package com.example.levelapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.levelapp.data.repository.UserRepository
import com.example.levelapp.model.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(application)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorEmail = null) }
    }

    fun onContrasenaChange(contrasena: String) {
        _uiState.update { it.copy(contrasena = contrasena, errorContrasena = null) }
    }

    fun onConfirmarContrasenaChange(contrasena: String) {
        _uiState.update { it.copy(confirmarContrasena = contrasena, errorConfirmarContrasena = null) }
    }

    fun registrar() {
        if (!validarRegistro()) return

        viewModelScope.launch {
            val result = userRepository.registrarUsuario(
                _uiState.value.email,
                _uiState.value.contrasena
            )
            result.onSuccess {
                _uiState.update { it.copy(registroExitoso = true, mensaje = "¡Registro exitoso!") }
            }.onFailure { exception ->
                _uiState.update { it.copy(mensaje = exception.message) }
            }
        }
    }

    fun iniciarSesion(onLoginSuccess: () -> Unit, onAdminLoginSuccess: () -> Unit) {
        val email = _uiState.value.email.trim()
        val contrasena = _uiState.value.contrasena.trim()

        if (email == "admin" && contrasena == "admin") {
            onAdminLoginSuccess()
            return
        }

        if (!validarLogin()) return

        viewModelScope.launch {
            val result = userRepository.iniciarSesion(email, contrasena)
            result.onSuccess {
                onLoginSuccess()
            }.onFailure { exception ->
                _uiState.update { it.copy(mensaje = exception.message) }
            }
        }
    }

    fun limpiarEstado() {
        _uiState.value = AuthUiState()
    }

    private fun validarRegistro(): Boolean {
        var esValido = true
        val currentState = _uiState.value

        if (currentState.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _uiState.update { it.copy(errorEmail = "Correo inválido") }
            esValido = false
        }
        if (currentState.contrasena.length < 6) {
            _uiState.update { it.copy(errorContrasena = "Debe tener al menos 6 caracteres") }
            esValido = false
        }
        if (currentState.contrasena != currentState.confirmarContrasena) {
            _uiState.update { it.copy(errorConfirmarContrasena = "Las contraseñas no coinciden") }
            esValido = false
        }
        return esValido
    }

    private fun validarLogin(): Boolean {
        var esValido = true
        if (_uiState.value.email.isBlank() || _uiState.value.contrasena.isBlank()) {
            _uiState.update { it.copy(mensaje = "Todos los campos son obligatorios") }
            esValido = false
        }
        return esValido
    }
}