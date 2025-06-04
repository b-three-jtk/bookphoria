package com.example.bookphoria.ui.book

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookphoria.R
import com.example.bookphoria.ui.viewmodel.DetailBookViewModel

@Composable
fun DetailBookNetworkScreen(
    navController: NavController,
    bookId: String,
    bookViewModel: DetailBookViewModel
) {
    val selectedBookState = bookViewModel.selectedBook.collectAsState()
    val book = selectedBookState.value
    var showAddReview by remember { mutableStateOf(false) }
    val reviewsState = bookViewModel.reviews.collectAsState()

    LaunchedEffect(bookId) {
        bookViewModel.getBookById(bookId)
        bookViewModel.getReviews(bookId)
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
                    model = book.cover ?: R.drawable.bookshelf,
                    contentDescription = book.title,
                    modifier = Modifier
                        .height(160.dp)
                        .width(120.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.align(Alignment.Top)) {
                    Text(book.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "by ${book.authors.joinToString { it.name }}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Button(
                            onClick = { /* TODO: Toggle owned/bookmarked */ },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE45758)),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Text("Wishlist", color = Color.White)
                        }
                    }
                }
            }

            Text(
                text = book.synopsis,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                book.genres.forEach { genre ->
                    Text(
                        text = genre.name,
                        modifier = Modifier
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoItem(label = "Penerbit", value = book.publisher)
                InfoItem(label = "Tanggal Terbit", value = book.publishedDate)
                InfoItem(label = "Jumlah Halaman", value = "${book.pages} halaman")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Review", style = MaterialTheme.typography.titleSmall)

            if (reviewsState.value.isEmpty()) {
                Text("Belum ada review.", style = MaterialTheme.typography.bodySmall)
            } else {
                reviewsState.value.forEach { review ->
                    ReviewSection(
                        reviewerAvatar = review.user.avatar,
                        reviewerName = review.user.username,
                        reviewText = review.desc,
                        rating = review.rate
                    )
                }
            }
        }
    }
}