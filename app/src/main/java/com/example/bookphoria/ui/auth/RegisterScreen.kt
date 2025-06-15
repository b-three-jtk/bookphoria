package com.example.bookphoria.ui.auth

import android.util.Log
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
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(viewModel: AuthViewModel, navController: NavController) {
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showValidationDialog by remember { mutableStateOf(false) }
    var showRegisterErrorDialog by remember { mutableStateOf(false) }
    var validationErrors by remember { mutableStateOf(emptyList<String>()) } // Inisialisasi kosong
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var hasValidationOccurred by remember { mutableStateOf(false) }

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
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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

        // Username Field
        Column {
            AuthTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                leadingIcon = Icons.Default.Person,
                contentDescription = "Username Icon",
                modifier = Modifier.padding(top = 20.dp)
            )
            if (hasValidationOccurred && validationErrors.contains("Username Tidak Boleh Kosong!") && username.isBlank()) {
                Text(
                    text = "Username Tidak Boleh Kosong!",
                    color = MaterialTheme.colorScheme.error,
                    style = AppTypography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        // Email Field
        Column {
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = Icons.Default.Email,
                contentDescription = "Email Icon",
                modifier = Modifier.padding(top = 20.dp)
            )
            if (hasValidationOccurred && (validationErrors.contains("Email belum diisi") && email.isBlank() ||
                        validationErrors.contains("Email tidak valid") && !viewModel.isValidEmail(email) && email.isNotBlank())) {
                Text(
                    text = when {
                        validationErrors.contains("Email belum diisi") -> "Email Tidak Boleh Kosong!"
                        validationErrors.contains("Email tidak valid") -> "Gunakan email dari penyedia seperti Gmail, Yahoo, Outlook, atau email instansi"
                        else -> ""
                    },
                    color = MaterialTheme.colorScheme.error,
                    style = AppTypography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        // Password Field
        Column {
            AuthTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                contentDescription = "Password Icon",
                isPassword = true,
                modifier = Modifier.padding(top = 20.dp)
            )
            if (hasValidationOccurred && (validationErrors.contains("Password belum diisi") && password.isBlank() ||
                        validationErrors.contains("Password tidak valid") && !viewModel.isValidPassword(password) && password.isNotBlank())) {
                Text(
                    text = when {
                        validationErrors.contains("Password belum diisi") -> "Password Tidak Boleh Kosong!"
                        validationErrors.contains("Password tidak valid") -> "Password minimal 8 karakter dengan angka dan simbol (contoh: Ab1!abcd)"
                        else -> ""
                    },
                    color = MaterialTheme.colorScheme.error,
                    style = AppTypography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        // Confirm Password Field
        Column {
            AuthTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                leadingIcon = Icons.Default.Lock,
                contentDescription = "Confirm Password Icon",
                isPassword = true,
                modifier = Modifier.padding(top = 20.dp)
            )
            if (hasValidationOccurred && (validationErrors.contains("Confirm password belum diisi") && confirmPassword.isBlank() ||
                        validationErrors.contains("Confirm Password tidak cocok") && password != confirmPassword && confirmPassword.isNotBlank())) {
                Text(
                    text = when {
                        validationErrors.contains("Confirm password belum diisi") -> "Confirm Password Tidak Boleh Kosong!"
                        validationErrors.contains("Confirm Password tidak cocok") -> "Password dan Confirm Password tidak cocok"
                        else -> ""
                    },
                    color = MaterialTheme.colorScheme.error,
                    style = AppTypography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        PrimaryButton(
            text = "DAFTAR",
            backgroundColor = PrimaryOrange,
            onClick = {
                if (!isLoading) {
                    hasValidationOccurred = true // Tandai bahwa validasi telah terjadi
                    validationErrors = viewModel.validateLoginForm(email, password) +
                            listOfNotNull(
                                if (username.isBlank()) "Username Tidak Boleh Kosong!" else null,
                                if (confirmPassword.isBlank()) "Confirm password belum diisi" else if (password != confirmPassword) "Confirm Password tidak cocok" else null
                            )
                    if (validationErrors.isNotEmpty()) {
                        showValidationDialog = true
                    } else {
                        scope.launch {
                            viewModel.register(
                                name = username,
                                email = email,
                                password = password,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Anda telah Terdaftar! Selamat Datang.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("home")
                                },
                                onError = { errorMessage ->
                                    showRegisterErrorDialog = true
                                }
                            )
                        }
                    }
                }
            }
        )

        // Dialog untuk validasi field yang belum terisi
        if (showValidationDialog) {
            AlertDialog(
                onDismissRequest = { showValidationDialog = false },
                title = { Text("Validasi Gagal") },
                text = { Text("Harap lengkapi data berikut: ${validationErrors.joinToString(", ")}") },
                confirmButton = {
                    TextButton(onClick = { showValidationDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

        // Dialog untuk error registrasi
        if (showRegisterErrorDialog) {
            AlertDialog(
                onDismissRequest = { showRegisterErrorDialog = false },
                title = { Text("Registrasi Gagal") },
                text = { Text("Terjadi kesalahan: ${viewModel.emailError.value ?: viewModel.passwordError.value ?: "Coba beberapa saat lagi"}") },
                confirmButton = {
                    TextButton(onClick = { showRegisterErrorDialog = false }) {
                        Text("OK")
                    }
                }
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


//@Composable
//fun RegisterScreen(viewModel: AuthViewModel, navController: NavController) {
//    val isLoading by viewModel.isLoading.collectAsState()
//    val context = LocalContext.current
//    var username by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var showDialog by remember { mutableStateOf(false) }
//    val scrollState = rememberScrollState()
//
//    fun isValidEmail(email: String): Boolean {
//        val emailRegex = "^[A-Za-z0-9+_.-]+@([a-zA-Z0-9.-]+\\.)+[a-zA-Z]{2,}$".toRegex()
//        val allowedDomains = listOf(
//            "gmail.com", "yahoo.com", "outlook.com", "hotmail.com",
//            "aol.com", "icloud.com", "protonmail.com", "polban.ac.id"
//        )
//        if (!email.matches(emailRegex)) return false
//        val domain = email.substringAfter("@")
//        return allowedDomains.contains(domain) || domain.matches("^[a-zA-Z0-9.-]+\\.edu\\.[a-zA-Z]{2,}$".toRegex())
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(SoftCream)
//            .verticalScroll(scrollState)
//            .padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(300.dp)
//        ){
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ){
//                Text(
//                    text = "Halo,",
//                    style = AppTypography.titleLarge,
//                    textAlign = TextAlign.Start,
//                    modifier = Modifier
//                        .padding(top = 180.dp)
//                )
//
//                Image(
//                    painter = painterResource(id = R.drawable.register),
//                    contentDescription = "Register Illustration",
//                    modifier = Modifier
//                        .size(350.dp)
//                )
//            }
//        }
//
//        Text(
//            text = "Buat akun untuk bergabung dengan Bookphoria!",
//            style = AppTypography.headlineMedium,
//            color = Color.Gray,
//            textAlign = TextAlign.Start,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp)
//        )
//
//        // Input Fields
//        AuthTextField(
//            value = username,
//            onValueChange = { username = it },
//            label = "Username",
//            leadingIcon = Icons.Default.Person,
//            contentDescription = "Username Icon",
//            modifier = Modifier.padding(top = 20.dp)
//        )
//        Column {
//            AuthTextField(
//                value = email,
//                onValueChange = { email = it },
//                label = "Email",
//                leadingIcon = Icons.Default.Email,
//                contentDescription = "Email Icon",
//                modifier = Modifier.padding(top = 20.dp)
//            )
//            if (email.isNotBlank() && !isValidEmail(email)) {
//                Text(
//                    text = "Gunakan email dari penyedia seperti Gmail, Yahoo, Outlook, atau email instansi",
//                    color = Color.Red,
//                    style = AppTypography.bodySmall,
//                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
//                )
//            }
//        }
//        AuthTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = "Password",
//            leadingIcon = Icons.Default.Lock,
//            contentDescription = "Password Icon",
//            isPassword = true,
//            modifier = Modifier.padding(top = 20.dp)
//        )
//
//        PrimaryButton(
//            text = "DAFTAR",
//            backgroundColor = PrimaryOrange,
//            onClick = {
//                if (!isLoading) {
//                    when {
//                        username.isBlank() -> Toast.makeText(context, "Username harus diisi!", Toast.LENGTH_SHORT).show()
//                        !isValidEmail(email) -> {
//                            Log.d("RegisterScreen", "Invalid email: $email")
//                            showDialog = true
//                        }
//                        password.isBlank() -> Toast.makeText(context, "Password harus diisi!", Toast.LENGTH_SHORT).show()
//                        else -> viewModel.register(
//                            name = username,
//                            email = email,
//                            password = password,
//                            onSuccess = {
//                                Toast.makeText(context, "Anda telah Terdaftar! Selamat Datang.", Toast.LENGTH_SHORT).show()
//                                navController.navigate("home")
//                            },
//                            onError = {
//                                Toast.makeText(context, "Terjadi kesalahan: $it", Toast.LENGTH_SHORT).show()
//                            }
//                        )
//                    }
//                }
//            }
//        )
//
//        if (showDialog) {
//            AlertDialog(
//                onDismissRequest = { showDialog = false },
//                title = { Text("Email Tidak Valid") },
//                text = { Text("Gunakan email dari penyedia seperti Gmail, Yahoo, Outlook, atau email instansi") },
//                confirmButton = {
//                    TextButton(onClick = { showDialog = false }) {
//                        Text("OK")
//                    }
//                }
//            )
//        }
//
//        // Login area
//        Spacer(modifier = Modifier.height(16.dp))
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = "Sudah punya akun?",
//                style = AppTypography.bodyMedium,
//                color = Color.Gray,
//                modifier = Modifier.align(Alignment.CenterVertically)
//            )
//            Spacer(modifier = Modifier.width(4.dp))
//            Text(
//                text = "Login",
//                fontSize = 14.sp,
//                color = PrimaryOrange,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier
//                    .align(Alignment.CenterVertically)
//                    .clickable {
//                        navController.navigate("login")
//                    }
//            )
//        }
//    }
//}