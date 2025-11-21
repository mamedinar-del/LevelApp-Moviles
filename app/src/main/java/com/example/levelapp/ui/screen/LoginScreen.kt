package com.example.levelapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.levelapp.R
import com.example.levelapp.navigation.Screen
import com.example.levelapp.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) { viewModel.limpiarEstado() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4EA495))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "¡Hola! ¡Que gusto verte!",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(uiState.email, { viewModel.onEmailChange(it) }, label = { Text("Correo Electrónico") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(uiState.contrasena, { viewModel.onContrasenaChange(it) }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.iniciarSesion(
                        onLoginSuccess = { navController.navigate(Screen.Home.route) },
                        onAdminLoginSuccess = { navController.navigate(Screen.Admin.route) }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0c6dfc),
                    contentColor = Color.White
                )
            ) {
                Text("Entrar", fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.titleMedium.fontSize)
            }

            Spacer(Modifier.height(8.dp))
            TextButton({ navController.navigate(Screen.Register.route) }) { Text("¿No tienes cuenta? Regístrate", color = Color.White) }
            uiState.mensaje?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}