package com.example.levelapp.ui.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.levelapp.R
import com.example.levelapp.model.CartItem
import com.example.levelapp.viewmodel.CartViewModel
import com.example.levelapp.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: Long,
    productViewModel: ProductViewModel,
    cartViewModel: CartViewModel
) {
    val product = productViewModel.uiState.collectAsState().value.productos.find { it.id == productId }

    var quantity by remember { mutableIntStateOf(1) }

    if (product != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detalles del Producto", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4EA495))
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFF5F5F5))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    val model = when {
                        product.imagenUri.startsWith("android.resource") -> Uri.parse(product.imagenUri)
                        product.imagenUri.startsWith("http") -> product.imagenUri
                        else -> java.io.File(product.imagenUri)
                    }

                    Image(
                        painter = rememberAsyncImagePainter(
                            model = model,
                            error = painterResource(R.drawable.logo),
                            placeholder = painterResource(R.drawable.logo)
                        ),
                        contentDescription = product.nombre,
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = product.categoria.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(20.dp))
                            Text("4.5", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.nombre,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$${product.precio.toInt()}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Descripción", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = product.descripcion,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Stock disponible: ", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${product.stock} unidades",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if(product.stock > 0) Color(0xFF4CAF50) else Color.Red
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Cantidad", style = MaterialTheme.typography.titleMedium)

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { if (quantity > 1) quantity-- },
                                        enabled = quantity > 1
                                    ) {
                                        Icon(Icons.Default.Remove, "Menos")
                                    }

                                    Text(
                                        text = quantity.toString(),
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )

                                    IconButton(
                                        onClick = { if (quantity < product.stock) quantity++ },
                                        enabled = quantity < product.stock
                                    ) {
                                        Icon(Icons.Default.Add, "Más")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    repeat(quantity) {
                                        cartViewModel.addToCart(product)
                                    }
                                    navController.popBackStack()
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                enabled = product.stock > 0,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff38a1ee))
                            ) {
                                Text("Agregar al Carrito - $${(product.precio * quantity).toInt()}", fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Producto no encontrado")
        }
    }
}