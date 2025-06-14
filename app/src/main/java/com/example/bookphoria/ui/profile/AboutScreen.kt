package com.example.bookphoria.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookphoria.ui.theme.DarkIndigo
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.theme.SoftOrange

@Composable
fun AboutScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                SoftCream
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Logo & Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App Logo
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        PrimaryOrange.copy(alpha = 0.3f),
                                        PrimaryOrange.copy(alpha = 0.1f)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = "Bookphoria Logo",
                            tint = SoftCream,
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // App Name
                    Text(
                        text = "Bookphoria",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Version
                    Text(
                        text = "Versi 1.0.0",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tagline
                    Text(
                        text = "Jelajahi Dunia Buku dengan Penuh Kegembiraan",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Light
                    )
                }
            }

            // Description Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Tentang Aplikasi",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkIndigo,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Text(
                            text = "Bookphoria adalah aplikasi mobile yang dirancang khusus untuk para pecinta buku. Dengan antarmuka yang intuitif dan fitur-fitur canggih, kami membantu Anda menemukan, membaca, dan mengelola koleksi buku digital Anda dengan mudah.",
                            fontSize = 14.sp,
                            color = Color.Black.copy(alpha = 0.7f),
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }

            // Features Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Fitur Unggulan",
                            fontSize = 20.sp,
                            color = DarkIndigo,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FeatureItem(
                                icon = Icons.Default.Search,
                                title = "Pencarian Cerdas",
                                description = "Temukan buku favorit dengan mudah"
                            )
                            FeatureItem(
                                icon = Icons.Default.BookmarkBorder,
                                title = "Bookmark & Favorit",
                                description = "Simpan dan kelola buku kesukaan"
                            )
                        }
                    }
                }
            }

            // Developer Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Developer",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkIndigo,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(
                                        color = PrimaryOrange.copy(alpha = 0.1f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = "Developer",
                                    tint = SoftOrange,
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "Binary Three",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black.copy(alpha = 0.9f)
                                )
                                Text(
                                    text = "Mobile App Developer",
                                    fontSize = 14.sp,
                                    color = Color.Black.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }

            // Contact & Links
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Hubungi Kami",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftOrange,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ContactItem(
                                icon = Icons.Default.Email,
                                title = "Email",
                                value = "support@bookphoria.com",
                                onClick = { /* Handle email click */ }
                            )
                            ContactItem(
                                icon = Icons.Default.Language,
                                title = "Website",
                                value = "www.bookphoria.polban.studio",
                                onClick = { /* Handle website click */ }
                            )
                            ContactItem(
                                icon = Icons.Default.Share,
                                title = "Media Sosial",
                                value = "@bookphoria_app",
                                onClick = { /* Handle social media click */ }
                            )
                        }
                    }
                }
            }

            // App Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Informasi Aplikasi",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftOrange,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            InfoRow("Versi", "1.0.0")
                            InfoRow("Build", "2024.01.15")
                            InfoRow("Platform", "Android")
                            InfoRow("Min SDK", "24 (Android 7.0)")
                            InfoRow("Ukuran", "45.2 MB")
                        }
                    }
                }
            }

            // Copyright
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "© 2024 Bookphoria",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "All rights reserved",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = PrimaryOrange.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = SoftOrange,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black.copy(alpha = 0.9f)
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.Black.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ContactItem(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = SoftOrange,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(alpha = 0.9f)
            )
            Text(
                text = value,
                fontSize = 13.sp,
                color = SoftOrange
            )
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black.copy(alpha = 0.9f)
        )
    }
}
