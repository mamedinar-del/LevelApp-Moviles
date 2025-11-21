package com.example.levelapp.ui.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.levelapp.R
import com.example.levelapp.model.Product
import com.example.levelapp.navigation.Screen
import com.example.levelapp.viewmodel.AuthViewModel
import com.example.levelapp.viewmodel.CartViewModel
import com.example.levelapp.viewmodel.ProductViewModel

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val index: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel
) {
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    val productState by productViewModel.uiState.collectAsState()

    val navItems = listOf(
        BottomNavItem("Inicio", Icons.Default.Home, 0),
        BottomNavItem("Servicio", Icons.Default.Build, 1),
        BottomNavItem("Carrito", Icons.Default.ShoppingCart, 2),
        BottomNavItem("Perfil", Icons.Default.Person, 3)
    )

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF4EA495))) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                if (selectedItemIndex == 0) {
                    TopSearchBar(
                        searchText = productState.searchText,
                        onSearchChange = { productViewModel.onSearchTextChange(it) }
                    )
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ) {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selectedItemIndex == item.index,
                            onClick = { selectedItemIndex = item.index }
                        )
                    }
                }
            }
        ) { paddingValues ->

            val finalPadding = if (selectedItemIndex != 0) {
                PaddingValues(
                    start = 0.dp, end = 0.dp,
                    bottom = paddingValues.calculateBottomPadding(),
                    top = 0.dp
                )
            } else {
                paddingValues
            }

            Box(modifier = Modifier.fillMaxSize().padding(finalPadding)) {
                when (selectedItemIndex) {
                    0 -> ProductListContent(productState.productosFiltrados, cartViewModel)
                    1 -> com.example.levelapp.ui.screen.ServiceScreen()
                    2 -> CartScreenInternal(cartViewModel)
                    3 -> ProfileScreen(
                        authViewModel = authViewModel,
                        onLogout = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TopSearchBar(searchText: String, onSearchChange: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 50.dp, start = 16.dp, end = 16.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo", modifier = Modifier.size(45.dp).clip(CircleShape), contentScale = ContentScale.Crop)
        Spacer(modifier = Modifier.width(12.dp))
        OutlinedTextField(value = searchText, onValueChange = onSearchChange, placeholder = { Text("Buscar productos...", color = Color.Gray) }, leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) }, modifier = Modifier.weight(1f).height(56.dp), shape = RoundedCornerShape(24.dp), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent), singleLine = true)
    }
}

@Composable
fun ProductListContent(products: List<Product>, cartViewModel: CartViewModel) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)) {
        item(span = { GridItemSpan(2) }) { Card(modifier = Modifier.fillMaxWidth().height(140.dp).padding(bottom = 8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2B3C)), shape = RoundedCornerShape(16.dp)) { Row(modifier = Modifier.fillMaxSize().padding(20.dp), verticalAlignment = Alignment.CenterVertically) { Column(modifier = Modifier.weight(1f)) { Text("Ofertas de Verano", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold); Text("Hasta 40% de descuento", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f)) }; Icon(Icons.Default.LocalOffer, null, tint = Color.White, modifier = Modifier.size(60.dp)) } } }
        item(span = { GridItemSpan(2) }) { Text("Populares", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(4.dp)) }
        if (products.isEmpty()) { item(span = { GridItemSpan(2) }) { Box(modifier = Modifier.height(200.dp), contentAlignment = Alignment.Center) { Text("No se encontraron productos", color = Color.White, fontWeight = FontWeight.Bold) } } } else { items(products) { product -> ProductCard(product, { cartViewModel.addToCart(product) }) } }
    }
}

@Composable
fun ProductCard(product: Product, onAddToCart: () -> Unit) {
    Card(elevation = CardDefaults.cardElevation(6.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(Color.White), contentAlignment = Alignment.Center) {
                val painter = rememberAsyncImagePainter(model = androidx.compose.ui.platform.LocalContext.current.let { if (product.imagenUri.startsWith("android.resource")) Uri.parse(product.imagenUri) else java.io.File(product.imagenUri) }, error = painterResource(R.drawable.logo))
                Image(painter, product.nombre, modifier = Modifier.size(110.dp), contentScale = ContentScale.Fit)
            }
            Column(Modifier.padding(12.dp)) {
                Text(product.categoria.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(product.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 2, minLines = 2)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("$${product.precio.toInt()}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    FilledIconButton({ onAddToCart() }, Modifier.size(36.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)) { Icon(Icons.Default.AddShoppingCart, "Añadir", Modifier.size(18.dp)) }
                }
            }
        }
    }
}