package com.example.bookphoria.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookphoria.R
import com.example.bookphoria.ui.components.AuthTextField
import com.example.bookphoria.ui.components.GoogleButton
import com.example.bookphoria.ui.theme.AppTypography
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.viewmodel.AuthViewModel
import com.example.bookphoria.ui.components.PrimaryButton

@Composable
fun RegisterScreen(viewModel: AuthViewModel, navController: NavController) {
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    text = "Halo,",
                    style = AppTypography.titleLarge,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(top = 180.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.register),
                    contentDescription = "Register Illustration",
                    modifier = Modifier
                        .size(350.dp)
                )
            }
        }

        Text(
            text = "Buat akun untuk bergabung dengan Bookphoria!",
            style = AppTypography.headlineMedium,
            color = Color.Gray,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Input Fields
        AuthTextField(
            value = username,
            onValueChange = { username = it },
            label = "Username",
            leadingIcon = Icons.Default.Person,
            contentDescription = "Username Icon",
            modifier = Modifier.padding(top = 20.dp)
        )
        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            leadingIcon = Icons.Default.Email,
            contentDescription = "Email Icon",
            modifier = Modifier.padding(top = 20.dp)
        )
        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            leadingIcon = Icons.Default.Lock,
            contentDescription = "Password Icon",
            isPassword = true,
            modifier = Modifier.padding(top = 20.dp)
        )

        PrimaryButton(
            text = "DAFTAR",
            backgroundColor = PrimaryOrange,
            onClick = {
                if (!isLoading) {
                    when {
                        username.isBlank() -> Toast.makeText(context, "Username harus diisi!", Toast.LENGTH_SHORT).show()
                        email.isBlank() -> Toast.makeText(context, "Email harus diisi!", Toast.LENGTH_SHORT).show()
                        password.isBlank() -> Toast.makeText(context, "Password harus diisi!", Toast.LENGTH_SHORT).show()
                        else -> viewModel.register(
                            name = username,
                            email = email,
                            password = password,
                            onSuccess = {
                                Toast.makeText(context, "Anda telah Terdaftar! Selamat Datang.", Toast.LENGTH_SHORT).show()
                                navController.navigate("home")
                            },
                            onError = {
                                Toast.makeText(context, "Terjadi kesalahan: $it", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        )

        // Login area
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sudah punya akun?",
                style = AppTypography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Login",
                fontSize = 14.sp,
                color = PrimaryOrange,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable {
                        navController.navigate("login")
                    }
            )
        }
    }
}
