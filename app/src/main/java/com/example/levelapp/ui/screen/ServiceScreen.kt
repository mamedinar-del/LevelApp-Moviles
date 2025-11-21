package com.example.levelapp.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.levelapp.R
import com.example.levelapp.viewmodel.ServiceViewModel
import kotlinx.coroutines.delay

@Composable
fun ServiceScreen(serviceViewModel: ServiceViewModel = viewModel()) {
    val uiState by serviceViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val backgroundColor = Color(0xFF4ea495)
    val primaryColor = Color(0xFF4EA495)
    val secondaryColor = Color(0xFF2E7D32)
    val errorTextColor = Color(0xFFFFCDD2)

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            serviceViewModel.obtenerUbicacionReal(context)
        }
    }

    fun detectarUbicacion() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            serviceViewModel.obtenerUbicacionReal(context)
        } else {
            locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Surface(
                    shadowElevation = 4.dp,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(40.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Servicio Técnico",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Expertos en reparación de consolas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Nueva Solicitud",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = uiState.dispositivo,
                            onValueChange = { serviceViewModel.onDispositivoChange(it) },
                            label = { Text("Dispositivo") },
                            placeholder = { Text("Ej: PlayStation 5") },
                            leadingIcon = { Icon(Icons.Default.Gamepad, null, tint = primaryColor) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                focusedLabelColor = primaryColor
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = uiState.descripcion,
                            onValueChange = { serviceViewModel.onDescripcionChange(it) },
                            label = { Text("Detalle del Problema") },
                            leadingIcon = { Icon(Icons.Default.Description, null, tint = primaryColor) },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                focusedLabelColor = primaryColor
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Ubicación de Retiro",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { detectarUbicacion() }
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(primaryColor.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocationOn, null, tint = primaryColor)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                if (uiState.cargandoUbicacion) {
                                    Text("Detectando...", style = MaterialTheme.typography.bodyMedium, color = primaryColor)
                                } else if (uiState.direccion.isEmpty()) {
                                    Text("Toca para detectar ubicación", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                } else {
                                    Text(uiState.direccion, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 2)
                                }
                            }

                            if (uiState.cargandoUbicacion) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = primaryColor)
                            } else {
                                Icon(Icons.Default.MyLocation, null, tint = Color.Gray)
                            }
                        }

                        if (uiState.direccion.isNotEmpty() && !uiState.cargandoUbicacion) {
                            Text(
                                text = "Dirección detectada por GPS",
                                style = MaterialTheme.typography.labelSmall,
                                color = secondaryColor,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { serviceViewModel.enviarSolicitud() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .shadow(4.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            enabled = !uiState.enviandoSolicitud && !uiState.solicitudEnviada
                        ) {
                            if (uiState.enviandoSolicitud) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Procesando...", fontSize = 16.sp)
                            } else {
                                Text("Enviar Solicitud", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                uiState.mensaje?.let { mensaje ->
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = mensaje,
                        color = if (uiState.solicitudEnviada) Color.White else errorTextColor,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    if (uiState.solicitudEnviada) {
                        LaunchedEffect(Unit) {
                            delay(6000)
                            serviceViewModel.limpiarMensaje()
                        }
                    }
                }
            }
        }
    }
}