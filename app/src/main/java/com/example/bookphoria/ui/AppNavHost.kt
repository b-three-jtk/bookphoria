package com.example.bookphoria.ui


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bookphoria.ui.auth.ForgotpassScreen
import com.example.bookphoria.ui.auth.LoginScreen
import com.example.bookphoria.ui.auth.RegisterScreen
import com.example.bookphoria.ui.auth.ResetpassScreen
import com.example.bookphoria.ui.book.DetailBookScreen
import com.example.bookphoria.ui.book.EditBookScreen
import com.example.bookphoria.ui.book.EntryBookScreen
import com.example.bookphoria.ui.book.SearchScreen
import com.example.bookphoria.ui.home.HomeScreen
import com.example.bookphoria.ui.onboarding.OnboardingScreen
import com.example.bookphoria.ui.viewmodel.AuthViewModel
import com.example.bookphoria.ui.viewmodel.BookViewModel
import com.example.bookphoria.ui.viewmodel.EditBookViewModel
import com.example.bookphoria.ui.viewmodel.OnboardingViewModel

@Composable
fun AppNavHost(
    authViewModel: AuthViewModel,
    onDeepLinkTriggered: (NavController) -> Unit = {},
    bookViewModel: BookViewModel,
    onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val isOnboardingCompleteState = onboardingViewModel
        .isOnboardingComplete
        .collectAsState(initial = null)

    val isOnboardingComplete = isOnboardingCompleteState.value

    LaunchedEffect(navController) {
        onDeepLinkTriggered(navController)
    }

    // Memastikan tidak ada yang muncul sebelum onboarding tersedia
    if (isOnboardingComplete == null) return

    val startDestination = if (isOnboardingComplete) "register" else "onboarding"

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("onboarding") {
                OnboardingScreen(
                    onFinished = {
                        onboardingViewModel.completeOnboarding()
                        navController.navigate("register") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }
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
                HomeScreen(navController = navController)
            }
            composable("search") {
                SearchScreen()
            }

            composable(
                route = "edit_book/{bookId}",
                arguments = listOf(navArgument("bookId") { type = NavType.IntType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getInt("bookId") ?: return@composable

                val viewModel: EditBookViewModel = hiltViewModel()

                EditBookScreen(
                    bookId = bookId,
                    viewModel = viewModel,
                    navController = navController
                )
            }

            composable(
                "reset?token={token}&email={email}",
                arguments = listOf(
                    navArgument("token") { type = NavType.StringType; defaultValue = "" },
                    navArgument("email") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                val token = backStackEntry.arguments?.getString("token") ?: ""
                val email = backStackEntry.arguments?.getString("email") ?: ""
                ResetpassScreen(
                    viewModel = authViewModel,
                    navController = navController,
                    token = token,
                    email = email
                )
            }
            composable("add-new-book") {
                EntryBookScreen(navController = navController, viewModel = bookViewModel)
            }
            composable(
                route = "detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                DetailBookScreen(navController = navController, bookViewModel = bookViewModel, bookId = id)
            }
        }
    }
}
