package com.example.bookphoria.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookphoria.R
import com.example.bookphoria.ui.theme.AppTypography

@Composable
fun RegisterScreen() {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.register),
            contentDescription = "Register Illustration",
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Halo,",
            style = AppTypography.titleLarge,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = "Buat akun untuk bergabung dengan Bookphoria!",
            style = AppTypography.headlineMedium,
            color = Color.Gray,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // Input Fields
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username *") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username Icon") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email *") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password *") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        Button(
            onClick = { /* ini navigate ke login/home */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE4583E)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "DAFTAR", style = AppTypography.bodyLarge, color = Color.White)
        }


        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Text(text = "Sudah punya akun?", style = AppTypography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(onClick = { /* Navigate ke Login */ }) {
                Text(text = "Login", fontSize = 14.sp, color = Color(0xFFE4583E), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SocialLoginButton(iconRes: Int, contentDesc: String) {
    IconButton(
        onClick = { /* Handle Social Login */ },
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Image(painter = painterResource(id = iconRes), contentDescription = contentDesc)
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}
