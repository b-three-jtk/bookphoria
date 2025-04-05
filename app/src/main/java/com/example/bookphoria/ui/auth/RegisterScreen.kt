package com.example.bookphoria.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bookphoria.R
import com.example.bookphoria.ui.theme.AppTypography
import com.example.bookphoria.ui.theme.DarkIndigo
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(viewModel: AuthViewModel, navController: NavController) {
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
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
        RegisterTextField(
            value = username,
            onValueChange = { username = it },
            label = "Username",
            leadingIcon = Icons.Default.Person,
            contentDescription = "Username Icon",
            modifier = Modifier.padding(top = 20.dp)
        )
        RegisterTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            leadingIcon = Icons.Default.Email,
            contentDescription = "Email Icon",
            modifier = Modifier.padding(top = 20.dp)
        )
        RegisterTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            leadingIcon = Icons.Default.Lock,
            contentDescription = "Password Icon",
            isPassword = true,
            modifier = Modifier.padding(top = 20.dp)
        )

        RegisterButton(
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

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "atau lanjutkan dengan",
            style = AppTypography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier
            .height(8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            GoogleRegisterButton(
                iconRes = R.drawable.ic_google,
                contentDesc = "Google Register"
            )
        }

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

@Composable
fun RegisterButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color
) {
    Button(
        onClick = {
            onClick()
        },
        modifier = modifier.fillMaxWidth().padding(vertical = 22.dp),
        shape = RoundedCornerShape(15.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge, color = Color.White)
    }
}

@Composable
fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    contentDescription: String,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label, color = DarkIndigo, style = MaterialTheme.typography.bodyLarge) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = contentDescription,
                tint = DarkIndigo,
                modifier = Modifier.padding(start = 16.dp, end = 10.dp)
            )
        },
        trailingIcon = {
            if (isPassword) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                    tint = DarkIndigo,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .clickable { passwordVisible = !passwordVisible }
                )
            }
        },
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        colors = TextFieldDefaults.colors(
            unfocusedTextColor = DarkIndigo,
            focusedTextColor = DarkIndigo.copy(alpha = 0.5f),
            cursorColor = DarkIndigo,
            focusedContainerColor = Color.LightGray,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(20.dp),
    )
}

@Composable
fun GoogleRegisterButton(iconRes: Int, contentDesc: String) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { /* Handle Google Register */ },
            modifier = Modifier
                .requiredSize(64.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = contentDesc,
                modifier = Modifier.size(60.dp)
            )
        }
    }
}

