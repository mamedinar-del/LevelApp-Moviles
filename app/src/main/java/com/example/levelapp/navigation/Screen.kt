package com.example.levelapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Home : Screen("home_screen")
    object Admin : Screen("admin_screen")
    object Cart : Screen("cart_screen")
}

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Inicio")
    object Search : BottomNavItem("search", Icons.Default.Search, "Buscar")
    object Cart : BottomNavItem("cart", Icons.Default.ShoppingCart, "Carrito")
    object Coupons : BottomNavItem("coupons", Icons.Default.Star, "Cupones")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Perfil")
}