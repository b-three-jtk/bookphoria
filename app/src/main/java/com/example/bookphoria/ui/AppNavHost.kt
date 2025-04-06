package com.example.bookphoria.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.example.bookphoria.ui.book.EntryBookScreen
import com.example.bookphoria.ui.book.SearchScreen
import com.example.bookphoria.ui.home.HomeScreen
import com.example.bookphoria.ui.onboarding.OnboardingScreen
import com.example.bookphoria.ui.viewmodel.AuthViewModel
import com.example.bookphoria.ui.viewmodel.BookViewModel
import com.example.bookphoria.ui.viewmodel.OnboardingViewModel

@Composable
fun AppNavHost(
    authViewModel: AuthViewModel,
    onboardingViewModel: OnboardingViewModel = hiltViewModel(),
    onDeepLinkTriggered: (NavController) -> Unit = {}
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

    Scaffold { innerPadding ->
        when (isOnboardingComplete) {
            null -> {
                // loading screen sementara
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            else -> {
                val startDestination = if (isOnboardingComplete) "register" else "onboarding"

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
                }
            }
        }
    }
}

@Composable
fun CircularProgressIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    Canvas(
        modifier = Modifier
            .size(40.dp)
            .rotate(angle)
    ) {
        drawArc(
            color = Color.Gray,
            startAngle = 0f,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
