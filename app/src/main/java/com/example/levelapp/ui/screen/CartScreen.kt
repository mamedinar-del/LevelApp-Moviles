package com.example.levelapp.ui.screen

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.levelapp.R
import com.example.levelapp.model.CartItem
import com.example.levelapp.viewmodel.CartViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, cartViewModel: CartViewModel) {
    CartScreenInternal(cartViewModel)
}

@Composable
fun CartScreenInternal(cartViewModel: CartViewModel) {
    val uiState by cartViewModel.uiState.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4EA495))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Surface(shadowElevation = 4.dp, color = Color.White, modifier = Modifier.fillMaxWidth()) {
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
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Mi Carrito", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                }
            }

            if (uiState.cartItems.isEmpty() && !uiState.pagoExitoso) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(80.dp), tint = Color.White.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Tu carrito está vacío", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

                    Box(modifier = Modifier.weight(1f)) {
                        if (uiState.pagoExitoso) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(100.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("¡Pedido Confirmado!", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Gracias por tu compra", style = MaterialTheme.typography.titleMedium, color = Color.White.copy(0.9f))
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(uiState.cartItems) { item ->
                                    CartListItem(
                                        item = item,
                                        onIncrease = { cartViewModel.increaseQuantity(item) },
                                        onDecrease = { cartViewModel.decreaseQuantity(item) },
                                        onRemove = { cartViewModel.removeFromCart(item.productId) }
                                    )
                                }
                            }
                        }
                    }

                    if (!uiState.pagoExitoso) {
                        Divider(color = Color.White.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total a Pagar:", style = MaterialTheme.typography.titleLarge, color = Color.White)
                            Text(
                                text = currencyFormat.format(uiState.total),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { cartViewModel.procesarPago() },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.procesandoPago) Color(0xFF2E7D32) else Color(0xFF1A2B3C)
                            ),
                            enabled = !uiState.procesandoPago
                        ) {
                            if (uiState.procesandoPago) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Procesando Compra...", fontWeight = FontWeight.Bold)
                            } else {
                                Text("Ir a Pagar", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartListItem(item: CartItem, onIncrease: () -> Unit, onDecrease: () -> Unit, onRemove: () -> Unit) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    Card(elevation = CardDefaults.cardElevation(2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = rememberAsyncImagePainter(model = Uri.parse(item.imagenUri)), contentDescription = item.nombre, modifier = Modifier.size(70.dp).padding(end = 12.dp), contentScale = ContentScale.Crop)
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = item.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) { Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error) }
                }
                Text(text = currencyFormat.format(item.precio), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledIconButton(onClick = onDecrease, modifier = Modifier.size(32.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) { Icon(Icons.Default.Remove, "Menos", modifier = Modifier.size(16.dp)) }
                    Text(text = "${item.quantity}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                    FilledIconButton(onClick = onIncrease, modifier = Modifier.size(32.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)) { Icon(Icons.Default.Add, "Más", modifier = Modifier.size(16.dp)) }
                }
            }
        }
    }
}