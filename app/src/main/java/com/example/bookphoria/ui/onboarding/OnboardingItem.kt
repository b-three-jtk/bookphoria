package com.example.bookphoria.ui.onboarding

import androidx.compose.ui.graphics.Color.Companion.LightGray
import com.example.bookphoria.R
import com.example.bookphoria.ui.theme.DeepBlue
import com.example.bookphoria.ui.theme.LightBlue
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.theme.SoftPink

data class OnboardingItem(
    val title: String,
    val description: String,
    val imageRes: Int,
    val backgroundColor: androidx.compose.ui.graphics.Color,
    val pagerActiveColor: androidx.compose.ui.graphics.Color,
    val pagerInactiveColor: androidx.compose.ui.graphics.Color,
    val buttonColor: androidx.compose.ui.graphics.Color,
    val buttonText: String,
    val buttonTextColor: androidx.compose.ui.graphics.Color
    )

val onboardingItems = listOf(
    OnboardingItem(
        title = "Atur Koleksi Buku dengan Mudah!",
        description = "Kelola daftar buku pribadi, tambahkan kategori, dan cari buku favorit dalam koleksi dengan cepat!",
        imageRes = R.drawable.bookshelf,
        backgroundColor = LightBlue,
        pagerActiveColor = DeepBlue,
        pagerInactiveColor = LightGray,
        buttonColor = DeepBlue,
        buttonText = "Lewati",
        buttonTextColor = SoftCream
    ),
    OnboardingItem(
        title = "Pantau Riwayat Membaca & Peminjaman",
        description = "Catat buku yang sudah dibaca, sedang dibaca, atau ingin dibaca. Lacak buku yang dipinjam dan pastikan teman mengembalikannya tepat waktu!",
        imageRes = R.drawable.book,
        backgroundColor = SoftCream,
        pagerActiveColor = PrimaryOrange,
        pagerInactiveColor = LightGray,
        buttonColor = PrimaryOrange,
        buttonText = "Lewati",
        buttonTextColor = SoftCream
    ),
    OnboardingItem(
        title = "Berbagi Buku, \n" +
                "Dukung Literasi!",
        description = "Kurangi pembajakan dan konsumsi berlebihan dengan berbagi buku secara legal. Maksimalkan manfaat dari setiap buku yang kamu miliki!",
        imageRes = R.drawable.people,
        backgroundColor = SoftPink,
        pagerActiveColor = SoftCream,
        pagerInactiveColor = LightGray,
        buttonColor = SoftCream,
        buttonText = "Mulai",
        buttonTextColor = PrimaryOrange
    )
)
