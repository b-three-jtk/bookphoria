package com.example.bookphoria.ui.book

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import java.time.format.TextStyle

@Composable
fun EntryBookScreen() {
    Box() {
        Text(text = "Entry Book Screen", style = MaterialTheme.typography.bodyMedium)
    }
}