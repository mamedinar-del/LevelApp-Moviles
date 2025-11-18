package com.example.levelapp.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.levelapp.viewmodel.AuthViewModel
import java.io.File

@Composable
fun ProfileScreen(authViewModel: AuthViewModel, onLogout: () -> Unit) {
    val uiState by authViewModel.uiState.collectAsState()
    val user = uiState.usuarioActual
    val context = LocalContext.current

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { authViewModel.actualizarFoto(it) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempCameraUri != null) {
            authViewModel.actualizarFoto(tempCameraUri!!)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val uri = createTempImageUri(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    if (uiState.mostrarDialogoFoto) {
        AlertDialog(
            onDismissRequest = { authViewModel.mostrarDialogoFoto(false) },
            title = { Text("Cambiar Foto") },
            confirmButton = {
                TextButton(onClick = {
                    authViewModel.mostrarDialogoFoto(false)
                    galleryLauncher.launch("image/*")
                }) { Text("Galería") }
            },
            dismissButton = {
                TextButton(onClick = {
                    authViewModel.mostrarDialogoFoto(false)

                    val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        val uri = createTempImageUri(context)
                        tempCameraUri = uri
                        cameraLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }

                }) { Text("Cámara") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Mi Perfil", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        Box(contentAlignment = Alignment.BottomEnd) {
            val painter = if (user?.imagenPerfilUri != null) rememberAsyncImagePainter(user.imagenPerfilUri) else null

            if (painter != null) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.size(150.dp).clip(CircleShape).clickable { authViewModel.mostrarDialogoFoto(true) },
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.size(150.dp).clip(CircleShape).background(Color.LightGray).clickable { authViewModel.mostrarDialogoFoto(true) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(80.dp), tint = Color.White)
                }
            }

            Icon(
                Icons.Default.Edit,
                null,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(8.dp)
                    .size(20.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (user != null) {
            ProfileInfoItem("Nombre", "${user.nombre} ${user.apellido}")
            ProfileInfoItem("RUT", user.rut)
            ProfileInfoItem("Correo", user.email)
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onLogout, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Cerrar Sesión") }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}

fun createTempImageUri(context: Context): Uri {
    val tempFile = File.createTempFile("profile_${System.currentTimeMillis()}", ".jpg", context.externalCacheDir).apply { createNewFile(); deleteOnExit() }
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", tempFile)
}