package com.example.levelapp.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object Home : Screen("home_screen")
    object Admin : Screen("admin_screen")
    object Cart : Screen("cart_screen")
}