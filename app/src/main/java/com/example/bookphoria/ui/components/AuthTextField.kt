package com.example.bookphoria.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.bookphoria.ui.theme.DarkIndigo


@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector?,
    contentDescription: String,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label, color = DarkIndigo, style = MaterialTheme.typography.bodyLarge) },
        leadingIcon = {
            leadingIcon?.let {
                val iconPadding = Modifier.padding(start = 16.dp, end = 10.dp)
                Icon(
                    imageVector = it,
                    contentDescription = contentDescription,
                    tint = DarkIndigo,
                    modifier = iconPadding
                )
            }
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