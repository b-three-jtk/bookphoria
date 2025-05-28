package com.example.bookphoria.ui.book

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Book
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookphoria.R
import com.example.bookphoria.data.local.entities.BookEntity
import com.example.bookphoria.data.local.entities.ShelfEntity
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.viewmodel.ShelfDetailViewModel
import com.example.bookphoria.ui.viewmodel.ShelfDetailViewModelFactory

@Composable
fun ShelfDetailScreen(
    navController: NavController,
    userId: Int,
    shelfId: Int,
    viewModel: ShelfDetailViewModel = hiltViewModel()
    ) {

    val shelfWithBooks by viewModel.shelfWithBooks.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadShelfWithBooks(userId, shelfId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
    ) {
        shelfWithBooks?.let { shelf ->
            ShelfHeader(shelf = shelf.shelf, bookCount = shelf.books.size)
            BookCollection(books = shelf.books, userId = userId, viewModel = viewModel)
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                CircularProgressIndicator()
            }
        }
    }
}


@Composable
fun ShelfHeader(shelf: ShelfEntity, bookCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(50.dp))

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
                    shelf.imagePath?.let { imagePath ->
                        AsyncImage(
                            model = imagePath,
                            contentDescription = "Shelf Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(150.dp)
                        )
                    } ?: Image(
                        painter = painterResource(id = R.drawable.sample_koleksi),
                        contentDescription = "Shelf Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(150.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Book count
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$bookCount Books",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Shelf title
                Text(
                    text = shelf.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Shelf description
                shelf.description?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun BookCollection(
    books: List<BookEntity>,
    userId: Int,
    viewModel: ShelfDetailViewModel
) {
    if (books.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            Text(
                text = "No books in this shelf",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(books, key = { it.id }) { book ->
            var authorName by remember { mutableStateOf("Unknown Author") }
            var isFinished by remember(book.id) { mutableStateOf(false) }

            LaunchedEffect(book.id) {
                try {
                    val bookId = book.id
                    authorName = viewModel.getBookAuthor(bookId)
                    isFinished = viewModel.getReadingProgress(userId, bookId)
                } catch (e: Exception) {
                    authorName = "Unknown Author"
                    isFinished = false
                    Log.e("BookCollection", "Error loading book details: ${e.message}")
                }
            }

            BookItem(
                coverUrl = book.imageUrl,
                title = book.title,
                author = authorName,
                isFinished = isFinished
            )
        }
    }
}

@Composable
fun BookItem(
    coverUrl: String?,
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
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(50.dp))
            // Book Cover
            Card(
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.size(80.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                coverUrl?.let {
                    AsyncImage(
                        model = coverUrl,
                        contentDescription = "Book Cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
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

//@Preview(showBackground = true)
//@Composable
//fun ShelfDetailScreenPreview() {
//    ShelfDetailScreen()
//}