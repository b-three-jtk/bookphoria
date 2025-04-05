package com.example.bookphoria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookphoria.ui.AppNavHost
import com.example.bookphoria.ui.theme.BookPhoriaTheme
import com.example.bookphoria.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CobaSplash()
        }
    }
}

@Composable
fun CobaSplash() {
    var isSplashVisible by remember { mutableStateOf(true) }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splashbuku))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = 1 // hanya sekali animasi
    )

    // Ganti screen saat animasi selesai
    LaunchedEffect(key1 = progress) {
        if (progress == 1f) {
            isSplashVisible = false
        }
    }

    if (isSplashVisible) {
        SplashScreen(composition, progress)
    } else {
        BookPhoriaTheme {
            val authViewModel: AuthViewModel = hiltViewModel()
            AppNavHost(authViewModel = authViewModel)
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
