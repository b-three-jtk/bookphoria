package com.example.bookphoria.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookphoria.R
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun ForgotpassScreen(viewModel: AuthViewModel, navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val forgotPasswordState by viewModel.forgotPasswordState.collectAsState()

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@([a-zA-Z0-9.-]+\\.)+[a-zA-Z]{2,}$".toRegex()
        val allowedDomains = listOf(
            "gmail.com", "yahoo.com", "outlook.com", "hotmail.com",
            "aol.com", "icloud.com", "protonmail.com", "polban.ac.id"
        )
        if (!email.matches(emailRegex)) return false
        val domain = email.substringAfter("@")
        return allowedDomains.contains(domain) || domain.matches("^[a-zA-Z0-9.-]+\\.edu\\.[a-zA-Z]{2,}$".toRegex())
    }

    LaunchedEffect(viewModel) {
        viewModel.isLoading.collect { loading ->
            isLoading = loading
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(150.dp))

        // Key Icon
        Image(
            painter = painterResource(id = R.drawable.forgot),
            contentDescription = "Key Icon",
            modifier = Modifier
                .size(80.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Title
        Text(
            text = "Lupa Password ?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Subtitle
        Text(
            text = "Masukan email yang digunakan dan kami akan mengirimkan email instruksi untuk melakukan reset password",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email*", color = Color.Gray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email Icon",
                    tint = Color.Gray
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            isError = email.isNotBlank() && !isValidEmail(email)
        )
        if (email.isNotBlank() && !isValidEmail(email)) {
            Text(
                text = "Gunakan email dari penyedia seperti Gmail, Yahoo, Outlook, atau email instansi",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Email Tidak Valid") },
                text = { Text("Gunakan email dari penyedia seperti Gmail, Yahoo, Outlook, atau email instansi") },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        LaunchedEffect(forgotPasswordState) {
            forgotPasswordState?.let { result ->
                if (result.isSuccess) {
                    message = "Link reset telah dikirim ke $email"
                    delay(20000L)
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        launchSingleTop = true
                    }
                } else {
                    message = result.exceptionOrNull()?.message ?: "Gagal mengirim email reset"
                }
            }
        }

        // Reset Button
        Button(
            onClick = {
                when {
                    email.isBlank() -> Toast.makeText(context, "Email harus diisi!", Toast.LENGTH_SHORT).show()
                    !isValidEmail(email) -> {
                        Log.d("ForgotpassScreen", "Invalid email: $email")
                        showDialog = true
                    }
                    else -> viewModel.forgotPassword(email)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
            enabled = !isLoading && email.isNotEmpty()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "RESET PASSWORD",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Email Tidak Valid") },
                text = { Text("Gunakan email dari penyedia seperti Gmail, Yahoo, Outlook, atau email instansi seperti polban.ac.id.") },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

        // Show message if available
        message?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = PrimaryOrange,
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Navigation Indicator
        Box(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Gray)
        )
    }
}