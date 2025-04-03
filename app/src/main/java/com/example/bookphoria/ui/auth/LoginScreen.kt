package com.example.bookphoria.ui.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookphoria.R
import com.example.bookphoria.ui.theme.DarkIndigo
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(viewModel: AuthViewModel, navController: NavController) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SoftCream
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp).align(Alignment.Start),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.login),
                    contentDescription = "Login Illustration",
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.TopStart)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 56.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Halo",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkIndigo,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
                Text(
                    text = "Selamat datang kembali di Bookphoria!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkIndigo,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }


            LoginTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = Icons.Default.Email,
                contentDescription = "Email Icon",
                modifier = Modifier.padding(top = 20.dp)
            )

            LoginTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                contentDescription = "Password Icon",
                isPassword = true,
                modifier = Modifier.padding(top = 20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 55.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = false,
                        onCheckedChange = {},
                    )
                    Text(
                        text = "Ingat Saya",
                        color = DarkIndigo,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = "Lupa Password?",
                    color = DarkIndigo,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            PrimaryButton(
                text = "MASUK",
                backgroundColor = PrimaryOrange,
                onClick = {
                    viewModel.login(
                        email = email,
                        password = password,
                        onSuccess = {
                            Toast.makeText(
                                context,
                                "Login Berhasil! Selamat Datang.",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate("home")
                        },
                        onError = {
                            Toast.makeText(
                                context,
                                "Terjadi kesalahan saat login! Coba beberapa saat lagi.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            )

            Text(
                text = "Atau masuk menggunakan",
                color = DarkIndigo,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { /* Implementasi login dengan Google */ },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = "Google")
                }
            }

            Text(
                text = "Belum punya akun? Daftar",
                color = DarkIndigo,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable {  }.padding(top = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTextField(
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
        modifier = modifier.fillMaxWidth().padding(horizontal = 56.dp),
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
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().padding(vertical = 22.dp, horizontal = 56.dp),
        shape = RoundedCornerShape(15.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge, color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginTextFieldPreview() {
    var email by remember { mutableStateOf("") }
    LoginTextField(
        value = email,
        onValueChange = { email = it },
        label = "Label",
        leadingIcon = Icons.Default.Email,
        contentDescription = "Email Icon",
        modifier = Modifier.padding(top = 20.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun LoginButtonPreview() {
    PrimaryButton(text = "LOGIN", backgroundColor = PrimaryOrange, onClick = {})
}