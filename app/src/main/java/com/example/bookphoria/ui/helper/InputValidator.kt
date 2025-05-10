package com.example.bookphoria.ui.helper

import android.util.Patterns

object InputValidator {
    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email tidak boleh kosong."
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Format email tidak valid."
            else -> null
        }
    }

    fun validatePassword(password: String, minLength: Int = 8): String? {
        return when {
            password.isBlank() -> "Password tidak boleh kosong."
            password.length < minLength -> "Password harus memiliki minimal $minLength karakter."
            else -> null
        }
    }

    fun validateUsername(name: String): String? {
        return when {
            name.isBlank() -> "Username tidak boleh kosong."
            name.length < 3 -> "Username harus memiliki minimal 3 karakter."
            name.length > 20 -> "Username tidak boleh lebih dari 20 karakter."
            name.any { !it.isLetterOrDigit() && it != '_' } -> "Username hanya boleh mengandung huruf, angka, dan underscore."
            name.first().isDigit() -> "Username tidak boleh dimulai dengan angka."
            name.contains(" ") -> "Username tidak boleh mengandung spasi."
            name.any { it.isWhitespace() } -> "Username tidak boleh mengandung spasi."
            name.any { it.isUpperCase() } -> "Username tidak boleh mengandung huruf kapital."
            else -> null
        }
    }

}