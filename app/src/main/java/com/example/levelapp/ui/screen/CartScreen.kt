package com.example.levelapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.levelapp.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, cartViewModel: CartViewModel) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi Carrito") }) }
    ) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            CartScreenInternal(cartViewModel)
        }
    }
}

@Composable
fun CartScreenInternal(cartViewModel: CartViewModel) {
    val uiState by cartViewModel.uiState.collectAsState()

    if (uiState.cartItems.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tu carrito está vacío")
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(uiState.cartItems) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(item.nombre, style = MaterialTheme.typography.titleMedium)
                            Text("Cant: ${item.quantity} x $${item.precio}")
                        }
                        IconButton(onClick = { cartViewModel.removeFromCart(item.productId) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                    Divider()
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Total: $${uiState.total}",
                style = MaterialTheme.typography.headlineMedium
            )
            Button(
                onClick = { /* Lógica de pago futura */ },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Pagar")
            }
        }
    }
}