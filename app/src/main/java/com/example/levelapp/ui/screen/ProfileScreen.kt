package com.example.levelapp.ui.screen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.levelapp.R
import com.example.levelapp.viewmodel.AuthViewModel
import java.io.File

@Composable
fun ProfileScreen(authViewModel: AuthViewModel, onLogout: () -> Unit) {
    val uiState by authViewModel.uiState.collectAsState()
    val user = uiState.usuarioActual
    val context = LocalContext.current
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> uri?.let { authViewModel.actualizarFoto(it) } }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success -> if (success && tempCameraUri != null) authViewModel.actualizarFoto(tempCameraUri!!) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted -> if (isGranted) { val uri = createTempImageUri(context); tempCameraUri = uri; cameraLauncher.launch(uri) } }

    if (uiState.mostrarDialogoFoto) {
        AlertDialog(
            onDismissRequest = { authViewModel.mostrarDialogoFoto(false) },
            title = { Text("Cambiar Foto") },
            confirmButton = { TextButton(onClick = { authViewModel.mostrarDialogoFoto(false); galleryLauncher.launch("image/*") }) { Text("Galería") } },
            dismissButton = { TextButton(onClick = {
                authViewModel.mostrarDialogoFoto(false)
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    val uri = createTempImageUri(context); tempCameraUri = uri; cameraLauncher.launch(uri)
                } else { permissionLauncher.launch(android.Manifest.permission.CAMERA) }
            }) { Text("Cámara") } }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF4EA495))) {
        Column(modifier = Modifier.fillMaxSize()) {

            Surface(shadowElevation = 4.dp, color = Color.White, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo", modifier = Modifier.size(40.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Mi Perfil", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                }
            }

            Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(16.dp))

                Box(contentAlignment = Alignment.BottomEnd) {
                    val painter = if (user?.imagenPerfilUri != null) rememberAsyncImagePainter(user.imagenPerfilUri) else null
                    if (painter != null) Image(painter = painter, contentDescription = null, modifier = Modifier.size(140.dp).clip(CircleShape).clickable { authViewModel.mostrarDialogoFoto(true) }.border(4.dp, Color.White, CircleShape), contentScale = ContentScale.Crop)
                    else Box(modifier = Modifier.size(140.dp).clip(CircleShape).background(Color.White).clickable { authViewModel.mostrarDialogoFoto(true) }, contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, null, modifier = Modifier.size(80.dp), tint = Color.Gray) }

                    Icon(Icons.Default.Edit, null, modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape).padding(8.dp).size(20.dp), tint = Color.White)
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (user != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            ProfileInfoItem("Nombre", "${user.nombre} ${user.apellido}")
                            ProfileInfoItem("RUT", user.rut)
                            ProfileInfoItem("Correo", user.email)
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
        Divider(modifier = Modifier.padding(top = 8.dp), color = Color.LightGray)
    }
}

fun createTempImageUri(context: Context): Uri {
    val tempFile = File.createTempFile("profile_${System.currentTimeMillis()}", ".jpg", context.externalCacheDir).apply { createNewFile(); deleteOnExit() }
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", tempFile)
}