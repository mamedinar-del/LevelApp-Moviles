package com.example.levelapp.ui.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.levelapp.R
import com.example.levelapp.model.Product
import com.example.levelapp.navigation.Screen
import com.example.levelapp.viewmodel.*

data class BottomNavItem(val label: String, val icon: ImageVector, val index: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    pViewModel: ProductViewModel,
    cViewModel: CartViewModel,
    aViewModel: AuthViewModel
) {
    var idx by remember { mutableIntStateOf(0) }
    val items = listOf(BottomNavItem("Home", Icons.Default.Home, 0), BottomNavItem("Buscar", Icons.Default.Search, 1), BottomNavItem("Carrito", Icons.Default.ShoppingCart, 2), BottomNavItem("Perfil", Icons.Default.Person, 3))

    Box(Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.login_background), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Scaffold(
            containerColor = Color.Transparent,
            topBar = { TopAppBar(title = { Text("Level-Up") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)) },
            bottomBar = { NavigationBar(containerColor = MaterialTheme.colorScheme.surface.copy(0.9f)) { items.forEach { i -> NavigationBarItem(icon = { Icon(i.icon, i.label) }, label = { Text(i.label) }, selected = idx == i.index, onClick = { idx = i.index }) } } }
        ) { pad ->
            Box(Modifier.padding(pad)) {
                when (idx) {
                    0 -> ProductList(pViewModel, cViewModel)
                    2 -> CartScreenInternal(cViewModel)
                    3 -> ProfileScreen(aViewModel) { navController.navigate(Screen.Login.route) { popUpTo(0) } }
                    else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Próximamente") }
                }
            }
        }
    }
}

@Composable
fun ProductList(pvm: ProductViewModel, cvm: CartViewModel) {
    val st by pvm.uiState.collectAsState()
    LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(st.productos) { p ->
            Card {
                Column(Modifier.padding(8.dp)) {
                    Image(rememberAsyncImagePainter(Uri.parse(p.imagenUri)), null, Modifier.height(100.dp).fillMaxWidth(), contentScale = ContentScale.Crop)
                    Text(p.nombre); Text("$${p.precio}")
                    Button({ cvm.addToCart(p) }) { Text("Añadir") }
                }
            }
        }
    }
}