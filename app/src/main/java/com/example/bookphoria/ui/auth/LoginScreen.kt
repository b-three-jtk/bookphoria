package com.example.bookphoria.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.bookphoria.ui.components.PrimaryButton
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookphoria.R
import com.example.bookphoria.ui.components.AuthTextField
import com.example.bookphoria.ui.components.GoogleButton
import com.example.bookphoria.ui.theme.AppTypography
import com.example.bookphoria.ui.theme.DarkIndigo
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.viewmodel.AuthViewModel
import com.example.bookphoria.ui.viewmodel.BookViewModel

@Composable
fun LoginScreen(viewModel: AuthViewModel, navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var showValidationDialog by remember { mutableStateOf(false) }
    var showLoginErrorDialog by remember { mutableStateOf(false) }
    var validationErrors by remember { mutableStateOf(emptyList<String>()) } // Inisialisasi kosong
    val savedCredentials by viewModel.getSavedCredentials().collectAsState(initial = Pair(null, null))
    val scrollState = rememberScrollState()

    // State untuk melacak apakah validasi sudah dilakukan
    var hasValidationOccurred by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val (savedEmail, savedPassword) = savedCredentials
        savedEmail?.let { email = it }
        savedPassword?.let { password = it }
        if (savedEmail != null && savedPassword != null) {
            rememberMe = true
        }
    }

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
                .height(240.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.login),
                    contentDescription = "Login Illustration",
                    modifier = Modifier
                        .size(180.dp)
                )
                Text(
                    text = "Halo",
                    style = AppTypography.titleLarge,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .padding(top = 190.dp)
                )
            }
        }

        Text(
            text = "Selamat datang kembali di Bookphoria!",
            style = AppTypography.headlineMedium,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            textAlign = TextAlign.End
        )

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
            // Tampilkan error hanya jika validasi terjadi dan kondisi error masih berlaku
            if (hasValidationOccurred && (validationErrors.contains("Email belum diisi") && email.isBlank() ||
                        validationErrors.contains("Email tidak valid") && !viewModel.isValidEmail(email) && email.isNotBlank())) {
                Text(
                    text = when {
                        validationErrors.contains("Email belum diisi") -> "Email tidak boleh kosong!"
                        else -> "Gunakan email dari penyedia seperti Gmail, Yahoo, Outlook, atau email instansi"
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
                modifier = Modifier.padding(top = 16.dp)
            )
            // Tampilkan error hanya jika validasi terjadi dan kondisi error masih berlaku
            if (hasValidationOccurred && (validationErrors.contains("Password belum diisi") && password.isBlank() ||
                        validationErrors.contains("Password tidak valid") && !viewModel.isValidPassword(password) && password.isNotBlank())) {
                Text(
                    text = when {
                        validationErrors.contains("Password belum diisi") -> "Password tidak boleh kosong!"
                        else -> "Password minimal 8 karakter dengan angka dan simbol (contoh: Ab1!abcd)"
                    },
                    color = MaterialTheme.colorScheme.error,
                    style = AppTypography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LabeledCheckbox(
                label = "Ingat Saya",
                onCheckChanged = { rememberMe = it },
                isChecked = rememberMe
            )
            Text(
                text = "Lupa Password?",
                color = DarkIndigo,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable {
                    navController.navigate("forgot")
                }
            )
        }

        PrimaryButton(
            text = "MASUK",
            backgroundColor = PrimaryOrange,
            onClick = {
                hasValidationOccurred = true // Tandai bahwa validasi telah dilakukan
                validationErrors = viewModel.validateLoginForm(email, password)
                if (validationErrors.isNotEmpty()) {
                    showValidationDialog = true
                } else {
                    viewModel.login(
                        email = email,
                        password = password,
                        rememberMe = rememberMe,
                        onSuccess = {
                            Toast.makeText(
                                context,
                                "Login Berhasil! Selamat Datang.",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate("home")
                        },
                        onError = { errorMessage ->
                            showLoginErrorDialog = true
                        }
                    )
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

        // Dialog untuk error login
        if (showLoginErrorDialog) {
            AlertDialog(
                onDismissRequest = { showLoginErrorDialog = false },
                title = { Text("Login Gagal") },
                text = { Text("Terjadi kesalahan saat login! Coba beberapa saat lagi.") },
                confirmButton = {
                    TextButton(onClick = { showLoginErrorDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Belum punya akun?",
                style = AppTypography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Daftar",
                fontSize = 14.sp,
                color = PrimaryOrange,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable {
                        navController.navigate("register")
                    }
            )
        }
    }
}

@Composable
fun LabeledCheckbox(
    label: String,
    onCheckChanged: (Boolean) -> Unit,
    isChecked: Boolean
) {
    Row(
        modifier = Modifier
            .clickable {
                onCheckChanged(!isChecked)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onCheckChanged(it) }
        )
        Spacer(Modifier.width(4.dp))
        Text(label, style = AppTypography.bodyMedium)
    }
}

//@Composable
//fun LoginScreen(viewModel: AuthViewModel, navController: NavController) {
//    val context = LocalContext.current
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var rememberMe by remember { mutableStateOf(false) }
//    var showDialog by remember { mutableStateOf(false) }
//    val savedCredentials by viewModel.getSavedCredentials().collectAsState(initial = Pair(null, null))
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
//    LaunchedEffect(Unit) {
//        val (savedEmail, savedPassword) = savedCredentials
//        savedEmail?.let { email = it }
//        savedPassword?.let { password = it }
//        if (savedEmail != null && savedPassword != null) {
//            rememberMe = true
//        }
//    }
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(SoftCream)
//                .verticalScroll(scrollState)
//                .padding(24.dp)
//            ,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(240.dp)
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.login),
//                        contentDescription = "Login Illustration",
//                        modifier = Modifier
//                            .size(180.dp)
//                    )
//                    Text(
//                        text = "Halo",
//                        style = AppTypography.titleLarge,
//                        textAlign = TextAlign.End,
//                        modifier = Modifier
//                            .padding(top = 190.dp)
//                    )
//                }
//            }
//
//            Text(
//                text = "Selamat datang kembali di Bookphoria!",
//                style = AppTypography.headlineMedium,
//                color = Color.Gray,
//                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
//                textAlign = TextAlign.End
//            )
//
//            Column {
//                AuthTextField(
//                    value = email,
//                    onValueChange = { email = it },
//                    label = "Email",
//                    leadingIcon = Icons.Default.Email,
//                    contentDescription = "Email Icon",
//                    modifier = Modifier.padding(top = 20.dp)
//                )
//                if (email.isNotBlank() && !isValidEmail(email)) {
//                    Text(
//                        text = "Gunakan email dari penyedia seperti Gmail, Yahoo, Outlook, atau email instansi",
//                        color = Color.Red,
//                        style = AppTypography.bodySmall,
//                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
//                    )
//                }
//            }
//
//            AuthTextField(
//                value = password,
//                onValueChange = { password = it },
//                label = "Password",
//                leadingIcon = Icons.Default.Lock,
//                contentDescription = "Password Icon",
//                isPassword = true,
//                modifier = Modifier.padding(top = 16.dp)
//            )
//
//            Row(
//                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//
//                LabeledCheckbox(
//                    label = "Ingat Saya",
//                    onCheckChanged = { rememberMe = it },
//                    isChecked = rememberMe
//                )
//                Text(
//                    text = "Lupa Password?",
//                    color = DarkIndigo,
//                    style = MaterialTheme.typography.bodyMedium,
//                    modifier = Modifier.clickable {
//                        navController.navigate("forgot")
//                    }
//
//                )
//            }
//
//            PrimaryButton(
//                text = "MASUK",
//                backgroundColor = PrimaryOrange,
//                onClick = {
//                    when {
//                        email.isBlank() -> Toast.makeText(context, "Email harus diisi!", Toast.LENGTH_SHORT).show()
//                        !isValidEmail(email) -> {
//                            Log.d("LoginScreen", "Invalid email: $email")
//                            showDialog = true
//                        }
//                        password.isBlank() -> Toast.makeText(context, "Password harus diisi!", Toast.LENGTH_SHORT).show()
//                        else -> viewModel.login(
//                            email = email,
//                            password = password,
//                            rememberMe = rememberMe,
//                            onSuccess = {
//                                Toast.makeText(
//                                    context,
//                                    "Login Berhasil! Selamat Datang.",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                navController.navigate("home")
//                            },
//                            onError = {
//                                Toast.makeText(
//                                    context,
//                                    "Terjadi kesalahan saat login! Coba beberapa saat lagi.",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        )
//                    }
//                }
//            )
//
//            if (showDialog) {
//                AlertDialog(
//                    onDismissRequest = { showDialog = false },
//                    title = { Text("Email Tidak Valid") },
//                    text = { Text("Gunakan email dari penyedia seperti Gmail, Yahoo, Outlook, atau email instansi") },
//                    confirmButton = {
//                        TextButton(onClick = { showDialog = false }) {
//                            Text("OK")
//                        }
//                    }
//                )
//            }
//
//            Row(
//                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Belum punya akun?",
//                    style = AppTypography.bodyMedium,
//                    color = Color.Gray,
//                    modifier = Modifier.align(Alignment.CenterVertically)
//                )
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(
//                    text = "Daftar",
//                    fontSize = 14.sp,
//                    color = PrimaryOrange,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier
//                        .align(Alignment.CenterVertically)
//                        .clickable {
//                            navController.navigate("register")
//                        }
//                )
//            }
//        }
//    }
//
//@Composable
//fun LabeledCheckbox(
//    label: String,
//    onCheckChanged: (Boolean) -> Unit,
//    isChecked: Boolean
//) {
//    Row(
//        modifier = Modifier
//            .clickable {
//                onCheckChanged(!isChecked)
//            },
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Checkbox(
//            checked = isChecked,
//            onCheckedChange = { onCheckChanged(it) }
//        )
//        Spacer(Modifier.width(4.dp))
//        Text(label, style = AppTypography.bodyMedium)
//    }
//}