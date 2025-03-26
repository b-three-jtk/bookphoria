package com.example.bookphoria.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.bookphoria.R

// Define Variable Font Families
val Manrope = FontFamily(
    Font(R.font.manrope_extralight, FontWeight.W200),
    Font(R.font.manrope_light, FontWeight.W300),
    Font(R.font.manrope_regular, FontWeight.W400),
    Font(R.font.manrope_medium, FontWeight.W500),
    Font(R.font.manrope_semibold, FontWeight.W600),
    Font(R.font.manrope_bold, FontWeight.W700),
    Font(R.font.manrope_extrabold, FontWeight.W800),
)

val Quicksand = FontFamily(
    Font(R.font.quicksand_light, FontWeight.W100),
    Font(R.font.quicksand_regular, FontWeight.W400), // Normal
    Font(R.font.quicksand_medium, FontWeight.W500),
    Font(R.font.quicksand_semibold, FontWeight.W600),
    Font(R.font.quicksand_bold, FontWeight.W700), // Bold
)

// Set Typography with Variable Fonts
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.W400, // Normal weight
        fontSize = 14.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Quicksand,
        fontWeight = FontWeight.W700, // Bold weight
        fontSize = 32.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )
)
