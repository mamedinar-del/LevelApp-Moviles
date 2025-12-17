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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.levelapp.R
import com.example.levelapp.navigation.Screen
import com.example.levelapp.viewmodel.AuthViewModel
import com.example.levelapp.viewmodel.OrderViewModel
import com.example.levelapp.viewmodel.ProductViewModel
import java.io.File
import com.example.levelapp.model.Order

@Composable
fun AdminScreen(
    navController: NavController,
    pvm: ProductViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    orderViewModel: OrderViewModel = viewModel()

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
                            Image(
                                painter = painterResource(R.drawable.logo),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Panel de Administrador", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Salir", tint = Color.Red)
                        }
                    }
                    TabRow(selectedTabIndex = selectedTab, containerColor = Color.White, contentColor = primaryColor) {
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
                    2 -> AdminOrdersTab(orderViewModel)
                }
            }
        }
    }
}

@Composable
fun AdminUsersTab(authViewModel: AuthViewModel) {
    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.cargarTodosLosUsuarios()
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text(
                "Usuarios Registrados (${uiState.listaUsuarios.size})",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
        }

        if (uiState.listaUsuarios.isEmpty()) {
            item {
                Text("No hay usuarios registrados (o error de conexión)", color = Color.LightGray)
            }
        } else {
            items(uiState.listaUsuarios) { user ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!user.imagenPerfilUri.isNullOrEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(user.imagenPerfilUri),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                        }

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
}

@Composable
fun AdminProductsTab(pvm: ProductViewModel) {
    val st by pvm.uiState.collectAsState()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { pvm.onImagenUriChange(it) }

    if (st.mostrarDialogoApi) {
        Dialog(onDismissRequest = { pvm.toggleDialogoApi() }) {
            Card(
                modifier = Modifier.fillMaxWidth().height(600.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Buscar en RAWG", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = st.busquedaApiQuery,
                        onValueChange = { pvm.buscarEnApi(it) },
                        label = { Text("Nombre del juego") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Default.Search, null) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (st.buscandoApi) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(st.resultadosApi) { juego ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable { pvm.seleccionarJuegoApi(juego) },
                                    elevation = CardDefaults.cardElevation(2.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
                                ) {
                                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        if (juego.backgroundImage != null) {
                                            Image(
                                                rememberAsyncImagePainter(juego.backgroundImage),
                                                null,
                                                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(4.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(juego.name, fontWeight = FontWeight.Bold)
                                            Text("Rating: ${juego.rating}", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Button(onClick = { pvm.toggleDialogoApi() }, modifier = Modifier.fillMaxWidth()) { Text("Cerrar") }
                }
            }
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (st.idEdicion == null) "Nuevo Producto" else "Editar Producto",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedButton(onClick = { pvm.toggleDialogoApi() }) {
                            Icon(Icons.Default.Download, null, Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Importar")
                        }
                    }
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(st.nombre, { pvm.onNombreChange(it) }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(st.descripcion, { pvm.onDescripcionChange(it) }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(st.stock, { pvm.onStockChange(it) }, label = { Text("Stock") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(st.precio, { pvm.onPrecioChange(it) }, label = { Text("Precio") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(st.categoria, { pvm.onCategoriaChange(it) }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(onClick = { launcher.launch("image/*") }) { Text("Subir Imagen") }
                        Spacer(Modifier.width(12.dp))
                        if (st.imagenUri != null) {
                            val model = if (st.imagenUri.toString().startsWith("http") || st.imagenUri.toString().startsWith("content")) st.imagenUri else File(st.imagenUri.toString())
                            Image(
                                rememberAsyncImagePainter(model),
                                null,
                                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (st.idEdicion != null) {
                            Button(
                                onClick = { pvm.cancelarEdicion() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                modifier = Modifier.weight(1f)
                            ) { Text("Cancelar") }
                        }
                        Button(onClick = { pvm.guardarProducto() }, modifier = Modifier.weight(1f)) {
                            Text(if (st.idEdicion == null) "Guardar" else "Actualizar")
                        }
                    }
                    st.mensaje?.let { Text(it, color = Color.Blue, modifier = Modifier.padding(top = 8.dp)) }
                }
            }
        }

        item { Text("Inventario Backend:", color = Color.White, fontWeight = FontWeight.Bold) }

        items(st.productos) { p ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    val model = if (p.imagenUri.startsWith("http")) p.imagenUri else if (p.imagenUri.startsWith("content")) Uri.parse(p.imagenUri) else File(p.imagenUri)

                    Image(
                        rememberAsyncImagePainter(model),
                        null,
                        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(p.nombre, fontWeight = FontWeight.Bold)
                        Text("Stock: ${p.stock} - $${p.precio.toInt()}", style = MaterialTheme.typography.bodyMedium)
                    }

                    Row {
                        IconButton(onClick = { pvm.empezarEdicion(p) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Blue)
                        }
                        IconButton(onClick = { pvm.eliminarProducto(p) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrdersTab(viewModel: OrderViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // Cargar los pedidos automáticamente al entrar en la pestaña
    LaunchedEffect(Unit) {
        viewModel.cargarPedidos()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Encabezado con botón de refrescar
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Historial de Pedidos Reales",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { viewModel.cargarPedidos() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Recargar", tint = Color.White)
            }
        }

        // Manejo de estados (Carga, Error, Lista vacía, Lista con datos)
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else if (uiState.error != null) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))) {
                Text(
                    text = "Error: ${uiState.error}",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else if (uiState.orders.isEmpty()) {
            Text("No se encontraron pedidos registrados.", color = Color.LightGray)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.orders) { order ->
                    OrderItemCard(order)
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila superior: ID y Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pedido #${order.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = if (order.estado == "COMPLETADO") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = order.estado,
                        color = if (order.estado == "COMPLETADO") Color(0xFF2E7D32) else Color(0xFFE65100),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información del Cliente
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${order.usuario.nombre} ${order.usuario.apellido}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = order.fecha,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(start = 20.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Lista de productos
            Text("Productos:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            order.detalles.forEach { detalle ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${detalle.cantidad}x ${detalle.producto.nombre}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "$${(detalle.precioUnitario * detalle.cantidad).toInt()}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Total: $${order.total.toInt()}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF4ea495)
                )
            }
        }
    }
}