package com.example.levelapp.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.levelapp.navigation.Screen
import com.example.levelapp.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val scroll = rememberScrollState()
    LaunchedEffect(Unit) { viewModel.limpiarEstado() }
    if (uiState.registroExitoso) LaunchedEffect(Unit) { navController.navigate(Screen.Login.route) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4EA495))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(scroll),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("¡Únete a nuestra comunidad!", style = MaterialTheme.typography.headlineLarge, color = Color.White)
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(uiState.nombre, { viewModel.onNombreChange(it) }, label = { Text("Nombre") }, isError = uiState.errorNombre != null, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(uiState.apellido, { viewModel.onApellidoChange(it) }, label = { Text("Apellido") }, isError = uiState.errorApellido != null, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(uiState.rut, { viewModel.onRutChange(it) }, label = { Text("RUT") }, isError = uiState.errorRut != null, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(uiState.email, { viewModel.onEmailChange(it) }, label = { Text("Correo Electrónico") }, isError = uiState.errorEmail != null, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(uiState.contrasena, { viewModel.onContrasenaChange(it) }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), isError = uiState.errorContrasena != null, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(uiState.confirmarContrasena, { viewModel.onConfirmarContrasenaChange(it) }, label = { Text("Confirmar Contraseña") }, visualTransformation = PasswordVisualTransformation(), isError = uiState.errorConfirmarContrasena != null, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { viewModel.registrar() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0c6dfc),
                    contentColor = Color.White
                )
            ) {
                Text("Registrarse", fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleMedium.fontSize)
            }
            TextButton({ navController.popBackStack() }) { Text("¿Ya tienes cuenta? Inicia Sesión", color = Color.White) }
            AnimatedVisibility(uiState.mensaje != null) { Text(uiState.mensaje ?: "", color = MaterialTheme.colorScheme.error) }
        }
    }
}