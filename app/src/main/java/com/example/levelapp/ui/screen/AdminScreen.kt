package com.example.levelapp.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.levelapp.model.Product
import com.example.levelapp.navigation.Screen
import com.example.levelapp.viewmodel.ProductViewModel

@Composable
fun AdminScreen(navController: NavController, productViewModel: ProductViewModel = viewModel()) {
    val uiState by productViewModel.uiState.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            productViewModel.onImagenUriChange(uri)
        }
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Panel de Administrador", style = MaterialTheme.typography.headlineLarge)
                TextButton(onClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }) {
                    Text("Cerrar Sesión")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = uiState.nombre, onValueChange = productViewModel::onNombreChange, label = { Text("Nombre del Producto") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = uiState.descripcion, onValueChange = productViewModel::onDescripcionChange, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = uiState.stock, onValueChange = productViewModel::onStockChange, label = { Text("Stock") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                OutlinedTextField(value = uiState.precio, onValueChange = productViewModel::onPrecioChange, label = { Text("Precio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = uiState.categoria, onValueChange = productViewModel::onCategoriaChange, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Seleccionar Imagen")
                }
                uiState.imagenUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { productViewModel.agregarProducto() }, modifier = Modifier.fillMaxWidth()) {
                Text("Agregar Producto")
            }
            uiState.mensaje?.let {
                Text(it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
            }

            Divider(modifier = Modifier.padding(vertical = 24.dp))
            Text("Productos en Tienda", style = MaterialTheme.typography.headlineSmall)
        }

        items(uiState.productos) { product ->
            ProductListItem(product)
        }
    }
}

@Composable
fun ProductListItem(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = product.imagenUri),
                contentDescription = product.nombre,
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(product.nombre, style = MaterialTheme.typography.titleMedium)
                Text("Stock: ${product.stock} | Precio: $${product.precio}", style = MaterialTheme.typography.bodyMedium)
                Text(product.categoria, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}