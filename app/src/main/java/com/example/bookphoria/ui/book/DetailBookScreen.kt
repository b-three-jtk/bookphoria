package com.example.bookphoria.ui.book

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookphoria.R
import com.example.bookphoria.ui.theme.DarkIndigo
import com.example.bookphoria.ui.viewmodel.BookViewModel

@Composable
fun DetailBookScreen(
    navController: NavController,
    bookId: Int,
    bookViewModel: BookViewModel
) {
    val selectedBookState = bookViewModel.selectedBook.collectAsState()
    val book = selectedBookState.value

    LaunchedEffect(bookId) {
        bookViewModel.getBookById(bookId)
    }

    if (book == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali"
                    )
                }
                Text(
                    text = "Detail Buku",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = book.book.imageUrl ?: R.drawable.bookshelf,
                    contentDescription = book.book.title,
                    modifier = Modifier
                        .height(160.dp)
                        .width(120.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.align(Alignment.Top)) {
                    Text(book.book.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("by ${book.author}", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button(
                            onClick = { /* TODO: Toggle owned/bookmarked */ },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE45758)),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Text("Owned", color = Color.White)
                        }
                        IconButton(
                            onClick = {
                                navController.navigate("edit_book/${book.book.id}")
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = DarkIndigo,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                    }

                }
            }

            Text(
                text = book.book.synopsis,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                book.genres.forEach { genre ->
                    Text(
                        text = genre,
                        modifier = Modifier
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoItem(label = "Penerbit", value = book.book.publisher)
                InfoItem(label = "Tanggal Terbit", value = book.book.publishedDate)
                InfoItem(label = "Jumlah Halaman", value = "${book.book.pages} halaman")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Review", style = MaterialTheme.typography.titleMedium)

            ReviewSection(
                reviewerName = "Ningen Sastrawijaya",
                reviewText = "Bagus bukunya, andalan tiap aku mau tidur",
                rating = 4
            )
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Text(value, fontSize = 14.sp)
    }
}

@Composable
fun ReviewSection(
    reviewerName: String,
    reviewText: String,
    rating: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = reviewerName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row {
                repeat(rating) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating Star",
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(16.dp)
                    )
                }
                repeat(5 - rating) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = "Unfilled Star",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Text(
            text = reviewText,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 44.dp)
        )
    }
}
