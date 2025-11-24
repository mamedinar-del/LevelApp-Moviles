package com.example.levelapp.viewmodel

import android.app.Application
import com.example.levelapp.util.MainDispatcherRule
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>(relaxed = true)

    @Test
    fun `si el email esta vacio, debe generar error`() {
        val viewModel = AuthViewModel(application)
        viewModel.onEmailChange("")

        viewModel.registrar()

        val estado = viewModel.uiState.value
        assertNotNull("El error de email no debería ser nulo", estado.errorEmail)
        assertEquals("Requerido", estado.errorEmail)
    }

    @Test
    fun `si la contraseña es corta, debe generar error`() {
        val viewModel = AuthViewModel(application)
        viewModel.onContrasenaChange("123")

        viewModel.registrar()

        val estado = viewModel.uiState.value
        assertNotNull("El error de contraseña no debería ser nulo", estado.errorContrasena)
    }

    @Test
    fun `si todos los datos son validos en login, no debe haber mensajes de error local`() {
        val viewModel = AuthViewModel(application)
        viewModel.onEmailChange("test@duoc.cl")
        viewModel.onContrasenaChange("password123")

        viewModel.iniciarSesion({}, {})

        val estado = viewModel.uiState.value
        assertNotEquals("Campos vacíos", estado.mensaje)
    }
}