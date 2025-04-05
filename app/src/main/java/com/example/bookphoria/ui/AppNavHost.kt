package com.example.bookphoria.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookphoria.ui.auth.ForgotpassScreen
import com.example.bookphoria.ui.auth.LoginScreen
import com.example.bookphoria.ui.auth.RegisterScreen
import com.example.bookphoria.ui.auth.ResetpassScreen
import com.example.bookphoria.ui.book.SearchScreen
import com.example.bookphoria.ui.home.HomeScreen
import com.example.bookphoria.ui.viewmodel.AuthViewModel

@Composable
fun AppNavHost(authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "register",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(viewModel = authViewModel, navController = navController)
            }
            composable("register") {
                RegisterScreen(viewModel = authViewModel, navController = navController)
            }
            composable("forgot") {
                ForgotpassScreen(viewModel = authViewModel, navController = navController)
            }
            composable("reset") {
                ResetpassScreen(viewModel = authViewModel, navController = navController)
            }
            composable("home") {
                HomeScreen()
            }
            composable("search") {
                SearchScreen()
            }
        }
    }
}
