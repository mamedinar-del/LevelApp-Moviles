package com.example.levelapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.levelapp.ui.screen.HomeScreen
import com.example.levelapp.ui.screen.LoginScreen
import com.example.levelapp.ui.screen.RegisterScreen
import com.example.levelapp.viewmodel.AuthViewModel
import com.example.levelapp.ui.screen.AdminScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController, authViewModel)
        }
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Admin.route) {
            AdminScreen()
        }
    }
}