package com.example.bookphoria.ui.book

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookphoria.R
import com.example.bookphoria.ui.theme.SoftCream

@Composable
fun ShelfDetailScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
    ) {
        // Title and Book Count
        ShelfHeader()

        // Book Collection
        BookCollection()
    }
}


@Composable
fun ShelfHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        // Books Collection Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SoftCream)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Shelf Image
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                        .align(Alignment.CenterHorizontally)
                ) {
                    // This would be your shelf image
                    Image(
                        painter = painterResource(id = R.drawable.sample_koleksi),
                        contentDescription = "Shelf Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(150.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Book count
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.Start)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "3 Books",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Shelf title
                Text(
                    text = "Books to make you smile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Shelf description
                Text(
                    text = "Enjoy a delightful journey through laughter, love, and positivity!",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun BookCollection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Book 1: Tentang Kamu
        BookItem(
            coverResId = android.R.drawable.ic_menu_gallery,
            title = "Tentang Kamu",
            author = "Tere Liye",
            isFinished = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Book 2: The Little Prince
        BookItem(
            coverResId = android.R.drawable.ic_menu_gallery,
            title = "The Little Prince",
            author = "Antoine de Saint-Exupéry",
            isFinished = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Book 2: The Little Prince
        BookItem(
            coverResId = android.R.drawable.ic_menu_gallery,
            title = "The Little Prince",
            author = "Antoine de Saint-Exupéry",
            isFinished = false
        )
    }
}

@Composable
fun BookItem(
    coverResId: Int,
    title: String,
    author: String,
    isFinished: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        border = BorderStroke(1.dp, color = Color.Gray),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)

    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(50.dp))
            // Book Cover
            Card(
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.size(80.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Image(
                    painter = painterResource(id = coverResId),
                    contentDescription = "Book Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = author,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFinished) Color(0xFFD7F9D7) else Color(0xFFF9E2E2)
                    )
                ) {
                    Text(
                        text = if (isFinished) "Finished" else "Unfinished",
                        fontSize = 12.sp,
                        color = if (isFinished) Color(0xFF2E7D32) else Color(0xFFB71C1C),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShelfDetailScreenPreview() {
    ShelfDetailScreen()
}