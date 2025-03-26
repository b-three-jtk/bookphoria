package com.example.bookphoria.ui.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookphoria.ui.theme.DarkIndigo
import com.example.bookphoria.ui.theme.Manrope
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(viewModel: AuthViewModel) {
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

            PrimaryButton(
                text = "LOGIN",
                backgroundColor = PrimaryOrange,
                onClick = {
                    viewModel.login(
                        email = email,
                        password = password,
                        onSuccess = {
                            Toast.makeText(context, "Login Berhasil! Selamat Datang.", Toast.LENGTH_SHORT).show()
                        },
                        onError = {
                            Toast.makeText(context, "Terjadi kesalahan saat login! Coba beberapa saat lagi.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
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
    var passwordVisible by remember { mutableStateOf(false) } // State untuk toggle visibility

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label, color = DarkIndigo, style = MaterialTheme.typography.bodyLarge) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = contentDescription,
                tint = DarkIndigo,
                modifier = Modifier.padding(start = 22.dp, end = 15.dp)
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
            focusedTextColor = DarkIndigo.copy(5f),
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
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(15.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Text(text = text, fontFamily = Manrope, fontSize = 18.sp)
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