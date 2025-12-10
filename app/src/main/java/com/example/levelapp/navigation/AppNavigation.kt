package com.example.levelapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.levelapp.ui.screen.*
import com.example.levelapp.viewmodel.*
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel()
    val productViewModel: ProductViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {

        composable(Screen.Splash.route) {
            AnimatedSplashScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController, authViewModel)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController, productViewModel, cartViewModel, authViewModel)
        }

        composable(Screen.Admin.route) {
            AdminScreen(navController, productViewModel, authViewModel = authViewModel)
        }

        composable(Screen.Cart.route) {
            CartScreen(navController, cartViewModel)
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
            ProductDetailScreen(navController, productId, productViewModel, cartViewModel)
        }
    }
}