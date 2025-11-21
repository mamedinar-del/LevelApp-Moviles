package com.example.levelapp.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.levelapp.R
import com.example.levelapp.model.Product
import com.example.levelapp.model.User
import com.example.levelapp.navigation.Screen
import com.example.levelapp.viewmodel.AuthViewModel
import com.example.levelapp.viewmodel.ProductViewModel

@Composable
fun AdminScreen(
    navController: NavController,
    pvm: ProductViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Productos", "Usuarios", "Pedidos")

    val primaryColor = Color(0xFF4ea495)

    Box(modifier = Modifier.fillMaxSize().background(primaryColor)) {
        Column(modifier = Modifier.fillMaxSize()) {

            Surface(color = Color.White, shadowElevation = 4.dp) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(painter = painterResource(R.drawable.logo), contentDescription = null, modifier = Modifier.size(40.dp).clip(CircleShape))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Admin Panel", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { navController.navigate(Screen.Login.route) { popUpTo(0) } }) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Salir", tint = Color.Red)
                        }
                    }

                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.White,
                        contentColor = primaryColor
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                when (selectedTab) {
                    0 -> AdminProductsTab(pvm)
                    1 -> AdminUsersTab(authViewModel)
                    2 -> AdminOrdersTab()
                }
            }
        }
    }
}

@Composable
fun AdminProductsTab(pvm: ProductViewModel) {
    val st by pvm.uiState.collectAsState()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { pvm.onImagenUriChange(it) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(if (st.idEdicion == null) "Agregar Producto" else "Editar Producto", style = MaterialTheme.typography.titleMedium, color = Color.Black, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(st.nombre, { pvm.onNombreChange(it) }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(st.descripcion, { pvm.onDescripcionChange(it) }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(st.stock, { pvm.onStockChange(it) }, label = { Text("Stock") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(st.precio, { pvm.onPrecioChange(it) }, label = { Text("Precio") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(st.categoria, { pvm.onCategoriaChange(it) }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(onClick = { launcher.launch("image/*") }) { Text("Imagen") }
                        Spacer(modifier = Modifier.width(12.dp))
                        if (st.imagenUri != null) {
                            Image(rememberAsyncImagePainter(st.imagenUri), null, modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (st.idEdicion != null) {
                            Button(
                                onClick = { pvm.cancelarEdicion() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                modifier = Modifier.weight(1f)
                            ) { Text("Cancelar") }
                        }
                        Button(
                            onClick = { pvm.guardarProducto() },
                            modifier = Modifier.weight(1f)
                        ) { Text(if (st.idEdicion == null) "Guardar" else "Actualizar") }
                    }

                    st.mensaje?.let { Text(it, color = Color.Blue, modifier = Modifier.padding(top = 8.dp)) }
                }
            }
        }

        item { Text("Toca un producto para editarlo:", color = Color.White, fontWeight = FontWeight.Bold) }

        items(st.productos) { p ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { pvm.empezarEdicion(p) },
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    val painter = rememberAsyncImagePainter(
                        model = androidx.compose.ui.platform.LocalContext.current.let { if (p.imagenUri.startsWith("android.resource")) Uri.parse(p.imagenUri) else java.io.File(p.imagenUri) }
                    )
                    Image(painter, null, modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(p.nombre, fontWeight = FontWeight.Bold)
                        Text("Stock: ${p.stock} - $${p.precio.toInt()}", style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Edit, null, tint = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun AdminUsersTab(authViewModel: AuthViewModel) {
    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { authViewModel.cargarTodosLosUsuarios() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Usuarios Registrados (${uiState.listaUsuarios.size})", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(uiState.listaUsuarios) { user ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("${user.nombre} ${user.apellido}", fontWeight = FontWeight.Bold)
                        Text(user.email, style = MaterialTheme.typography.bodyMedium)
                        Text("RUT: ${user.rut}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrdersTab() {
    var cliente by remember { mutableStateOf("") }
    var producto by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Ingresar Nuevo Pedido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = cliente, onValueChange = { cliente = it }, label = { Text("Nombre del Cliente") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = producto, onValueChange = { producto = it }, label = { Text("Producto a comprar") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = cantidad, onValueChange = { cantidad = it }, label = { Text("Cantidad") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (cliente.isNotEmpty() && producto.isNotEmpty()) {
                        mensaje = "Pedido creado para $cliente"
                        cliente = ""; producto = ""; cantidad = ""
                    } else {
                        mensaje = "Faltan datos"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generar Pedido")
            }

            mensaje?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
            }
        }
    }
}