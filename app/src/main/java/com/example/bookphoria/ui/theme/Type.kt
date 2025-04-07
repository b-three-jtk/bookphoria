package com.example.bookphoria.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.bookphoria.R

// FontFamily Quicksand
val Quicksand = FontFamily(
    Font(R.font.quicksand)
)

val Manrope = FontFamily(
    Font(R.font.manrope)
)

// Typography
val AppTypography = Typography(
    //Title
    titleLarge = TextStyle(
        fontFamily = Quicksand,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 40.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Quicksand,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Quicksand,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp
    ),

    //Subtitle
    headlineLarge = TextStyle(
        fontFamily = Quicksand,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Quicksand,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Quicksand,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),

    //Body
    bodyLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
)

val BodyBottomSheet = TextStyle(
    fontFamily = Quicksand,
    fontWeight = FontWeight.Normal,
    fontSize = 18.sp
)

val TitleExtraSmall = TextStyle(
    fontFamily = Quicksand,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp
)