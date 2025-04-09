package com.example.bookphoria

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.bookphoria.ui.AppNavHost
import com.example.bookphoria.ui.theme.BookPhoriaTheme
import com.example.bookphoria.ui.viewmodel.AuthViewModel
import com.example.bookphoria.ui.viewmodel.BookViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bookphoria.ui.viewmodel.HomeViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleDeepLink(intent)
        setContent {
            CobaSplash()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent) {
        if (intent?.action == Intent.ACTION_VIEW) {
            intent.data?.let { uri ->
                if (uri.host == "reset-password") {
                    DeepLinkHolder.apply {
                        token = uri.getQueryParameter("token") ?: ""
                        email = uri.getQueryParameter("email") ?: ""
                        shouldNavigate = true
                    }
                }
            }
        }
    }
}

object DeepLinkHolder {
    var token by mutableStateOf("")
    var email by mutableStateOf("")
    var shouldNavigate by mutableStateOf(false)
}

@Composable
fun CobaSplash() {
    var isSplashVisible by remember { mutableStateOf(true) }
    val navController = rememberNavController()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splashbuku))
    val progress by animateLottieCompositionAsState(composition, iterations = 1)

    LaunchedEffect(progress) {
        if (progress == 1f) {
            isSplashVisible = false
        }
    }

    LaunchedEffect(DeepLinkHolder.shouldNavigate) {
        if (DeepLinkHolder.shouldNavigate && !isSplashVisible) {
            navController.navigate("reset/${DeepLinkHolder.token}/${DeepLinkHolder.email}") {
                popUpTo("login") { inclusive = true }
            }
            DeepLinkHolder.shouldNavigate = false
        }
    }

    if (isSplashVisible) {
        SplashScreen(composition, progress)
    } else {
        BookPhoriaTheme {
            AppNavHost(
                onDeepLinkTriggered = { navController ->
                    if (DeepLinkHolder.shouldNavigate) {
                        navController.navigate("reset/${DeepLinkHolder.token}/${DeepLinkHolder.email}") {
                            popUpTo("login") { inclusive = true }
                        }
                        DeepLinkHolder.shouldNavigate = false
                    }
                }
            )

            AppNavHost()
        }
    }
}

@Composable
fun SplashScreen(composition: LottieComposition?, progress: Float) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(250.dp)
        )
    }
}
