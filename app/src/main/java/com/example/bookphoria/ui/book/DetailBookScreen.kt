package com.example.bookphoria.ui.book

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.bookphoria.R
import com.example.bookphoria.ui.components.LoadingState
import com.example.bookphoria.ui.theme.AppTypography
import com.example.bookphoria.ui.theme.DarkIndigo
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.theme.SubTitleExtraSmall
import com.example.bookphoria.ui.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailBookScreen(
    navController: NavController,
    bookId: Int,
    bookViewModel: BookViewModel
) {
    val selectedBookState = bookViewModel.selectedBook.collectAsState()
    val readingProgressState = bookViewModel.readingProgress.collectAsState()
    val bookStatusState = bookViewModel.bookStatus.collectAsState()
    val statusUpdateSuccess = bookViewModel.statusUpdateSuccess.collectAsState()
    val reviewUpdateSuccess = bookViewModel.reviewUpdateSuccess.collectAsState()
    val book = selectedBookState.value
    var showCreateDialog by remember { mutableStateOf(false) }
    var showAddReview by remember { mutableStateOf(false) }
    val reviewsState = bookViewModel.reviews.collectAsState()
    var showStatusBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(bookId) {
        bookViewModel.getBookById(bookId)
        bookViewModel.getReadingProgress(bookId)
        bookViewModel.getReviews(bookId)
        bookViewModel.getBookStatus(bookId)
    }

    LaunchedEffect(statusUpdateSuccess.value) {
        if (statusUpdateSuccess.value) {
            bookViewModel.getBookStatus(bookId)
            bookViewModel.updateStatusUpdateSuccess(false)
        }
    }

    LaunchedEffect(reviewUpdateSuccess.value) {
        if (reviewUpdateSuccess.value) {
            bookViewModel.getReviews(bookId)
            bookViewModel.updateStatusReview(false)
        }
    }

    if (book == null) {
        LoadingState()
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
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
                        Text(
                            text = book.book.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "by ${book.author}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            // Status button
                            val currentStatus = bookStatusState.value?.lowercase() ?: "none"
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

                            Spacer(modifier = Modifier.width(8.dp))

                            // Edit button
                            IconButton(
                                onClick = { navController.navigate("edit_book/${book.book.id}") },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = DarkIndigo.copy(alpha = 0.1f),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Book",
                                    tint = DarkIndigo,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Row {
                            OutlinedButton(
                                onClick = { showCreateDialog = true },
                                shape = RoundedCornerShape(24.dp),
                                border = BorderStroke(1.dp, color = Color.Gray),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.White,
                                    contentColor = LocalContentColor.current
                                )
                            ) {
                                Text("Progress Baca", style = AppTypography.bodyMedium)
                            }
                        }
                    }
                }

            if (showCreateDialog) {
                CreateProgressDialog(
                    onDismiss = { showCreateDialog = false },
                    onSave = { pagesRead ->
                        bookViewModel.updateReadingProgress(book.book.id, pagesRead)
                        showCreateDialog = false
                    },
                    totalPages = book.book.pages,
                    currentProgress = readingProgressState.value ?: 0,
                    previousProgress = readingProgressState.value
                )
            }
                if (showCreateDialog) {
                    CreateProgressDialog(
                        onDismiss = { showCreateDialog = false },
                        onSave = { pagesRead ->
                            bookViewModel.updateReadingProgress(book.book.id, pagesRead)
                            showCreateDialog = false
                        },
                        totalPages = book.book.pages,
                        currentProgress = readingProgressState.value ?: 0,
                        previousProgress = readingProgressState.value
                    )
                }

            if (showAddReview) {
                AddReviewDialog(
                    onDismiss = { showAddReview = false },
                    onSubmit = { desc, rate ->
                        bookViewModel.addReview(bookId = book.book.id, desc = desc, rate = rate)
                    }
                )

            }

            Text(
                text = book.book.synopsis,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
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

                Button(
                    onClick = { showAddReview = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryOrange.copy(alpha = 0.1f),
                        contentColor = PrimaryOrange
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "+ Add a Review",
                        style = SubTitleExtraSmall,
                        fontWeight = FontWeight.Medium
                    )
                }

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
                            title = "Owned",
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
                                    bookViewModel.deleteUserBook(bookId)
                                    showStatusBottomSheet = false
                                },
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
    reviewerAvatar: String,
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
                Image(
                    painter = rememberAsyncImagePainter(model = reviewerAvatar.ifBlank { R.drawable.user }),
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape)
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

@Composable
fun AddReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Int) -> Unit = { _, _ -> }
) {
    var reviewText by remember { mutableStateOf("") }
    var selectedRating by remember { mutableStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Tambahkan Ulasan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    singleLine = false
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Rating:")
                    Spacer(modifier = Modifier.width(8.dp))
                    Row {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= selectedRating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Rating Star",
                                tint = if (i <= selectedRating) Color(0xFFFF9800) else Color.Gray,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable { selectedRating = i }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }
                    Button(
                        onClick = {
                            if (reviewText.isNotBlank() && selectedRating > 0) {
                                onSubmit(reviewText, selectedRating)
                                onDismiss()
                            }
                        },
                        enabled = reviewText.isNotBlank() && selectedRating > 0
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}

@Composable
fun CreateProgressDialog(
    onDismiss: () -> Unit,
    onSave: (pagesRead: Int) -> Unit,
    totalPages : Int,
    currentProgress: Int = 0,
    previousProgress: Int? = null
) {
    var currentPage by remember { mutableStateOf(currentProgress.toString())}
    var isError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Update Progress Baccan",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    previousProgress?.let { progress ->
                        if (progress > 0) {
                            Column(
                                modifier = Modifier.padding(horizontal = 24.dp)
                            ) {
                                Text(
                                    text = "Sebelumnya: $progress/$totalPages halaman",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { progress.toFloat() / totalPages.toFloat() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp),
                                    color = PrimaryOrange,
                                    trackColor = Color.LightGray
                                )
                            }
                        }
                    }

                    Text("Halaman", style = MaterialTheme.typography.bodyMedium)

                    OutlinedTextField(
                        value = currentPage,
                        onValueChange = { currentPage = it
                            isError = it.toIntOrNull()?.let { num ->
                                num > totalPages || num < 0
                            } ?: false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            errorIndicatorColor = Color.Red
                        ),
                        isError = isError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = {
                            if (isError) {
                                Icon(Icons.Filled.Error, "Error", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    if (isError) {
                        Text(
                            text = "Halaman harus antara 0 dan $totalPages",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Text(
                    text = "Dari $totalPages halaman",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(bottomStart = 20.dp)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            if (!isError) {
                                currentPage.toIntOrNull()?.let { pages ->
                                    onSave(pages)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryOrange,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(bottomEnd = 20.dp),
                        enabled = !isError && currentPage.isNotEmpty()
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateProgressDialogPreview() {
    MaterialTheme {
        CreateProgressDialog(
            onDismiss = {},
            onSave = { /* pagesRead -> */ },
            totalPages = 300,
            currentProgress = 50,
            previousProgress = 100
        )
    }
}

@Composable
fun BottomSheetCard(
    icon: ImageVector,
    bgColor: Color,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleSmall, color = Color(0xFF1D1B20))
                Text(text = description, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF6F6F6F))
            }
        }
    }
}