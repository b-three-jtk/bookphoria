package com.example.bookphoria.ui.book

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
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
import com.example.bookphoria.ui.components.LoadingState
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.theme.TitleExtraSmall
import com.example.bookphoria.ui.viewmodel.DetailBookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailBookNetworkScreen(
    navController: NavController,
    bookId: String,
    bookViewModel: DetailBookViewModel
) {
    val selectedBookState = bookViewModel.selectedBook.collectAsState()
    val book = selectedBookState.value
    val statusUpdateSuccess = bookViewModel.statusUpdateSuccess.collectAsState()
    val reviewsState = bookViewModel.reviews.collectAsState()
    val bookStatusState = bookViewModel.bookStatus.collectAsState()
    var showStatusBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(bookId) {
        bookViewModel.getBookById(bookId)
        bookViewModel.getReviews(bookId)
        bookViewModel.getBookStatus(bookId)
    }

    LaunchedEffect(statusUpdateSuccess.value) {
        if (statusUpdateSuccess.value) {
            bookViewModel.getBookStatus(bookId)
            bookViewModel.updateStatusUpdateSuccess(false)
        }
    }

    if (book == null) {
        LoadingState()
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
                    style = TitleExtraSmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = book.cover,
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        val currentStatus = bookStatusState.value?.lowercase() ?: "none"
                        Log.d("DetailBookScreen", "Current Status: $currentStatus")
                        val (buttonText, buttonColor) = when (currentStatus) {
                            "owned" -> "Owned" to Color(0xFFE45758)
                            "reading" -> "Reading" to Color.Blue
                            "borrowed" -> "Borrowed" to Color.Green
                            else -> "Add to Collection" to Color.Gray
                        }

                        Button(
                            onClick = { showStatusBottomSheet = true },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonColor,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(buttonText, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            InfoItem(label = "Sinopsis", value = book.synopsis)

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

    if (showStatusBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showStatusBottomSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = SoftCream
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Update Status Buku",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Owned option
                BottomSheetCard(
                    icon = Icons.Default.Star,
                    bgColor = Color(0xFFE45758),
                    title = "0wned",
                    description = "Buku ini milikmu",
                    onClick = {
                        bookViewModel.updateBookStatus(bookId, "owned")
                        showStatusBottomSheet = false
                    }
                )

                // Reading option
                BottomSheetCard(
                    icon = Icons.Default.Star,
                    bgColor = Color.Blue,
                    title = "Reading",
                    description = "Sedang membaca buku ini",
                    onClick = {
                        bookViewModel.updateBookStatus(bookId, "reading")
                        showStatusBottomSheet = false
                    }
                )

                // Borrowed option
                BottomSheetCard(
                    icon = Icons.Default.Star,
                    bgColor = Color.Green,
                    title = "Borrowed",
                    description = "Buku ini dipinjam",
                    onClick = {
                        bookViewModel.updateBookStatus(bookId, "borrowed")
                        showStatusBottomSheet = false
                    }
                )

                // Remove from collection option
                if (bookStatusState.value?.isNotEmpty() == true) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            bookViewModel.updateBookStatus(bookId, "none")
                            showStatusBottomSheet = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Red
                        ),
                        border = BorderStroke(1.dp, Color.Red)
                    ) {
                        Text("Hapus dari Koleksi")
                    }
                }
            }
        }
    }
}