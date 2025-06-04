package com.example.bookphoria.ui.book

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.bookphoria.R
import com.example.bookphoria.data.local.entities.BookEntity
import com.example.bookphoria.ui.theme.AppTypography
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.viewmodel.MyShelfViewModel

@Composable
fun YourBooksScreen(
    viewModel: MyShelfViewModel = hiltViewModel(),
    navController: NavController
) {
    val booksWithAuthors by viewModel.booksWithAuthors.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserBooks()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Your Books",
                style = AppTypography.titleSmall
            )

            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (booksWithAuthors.isNotEmpty()) {
            LazyColumn {
                items(booksWithAuthors) { bookWithAuthors ->
                    ShelfItem(
                        book = bookWithAuthors.book,
                        authors = bookWithAuthors.authors.joinToString(", ") { it.name ?: "Unknown" },
                        onClick = {
                            navController.navigate("detail/${bookWithAuthors.book.id}")
                        }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada buku dalam rakmu.", style = AppTypography.bodyMedium)
            }
        }
    }
}

@Composable
fun ShelfItem(book: BookEntity, authors: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = if (book.imageUrl.isNullOrEmpty()) {
                    painterResource(id = R.drawable.sample_koleksi)
                } else {
                    rememberAsyncImagePainter(model = book.imageUrl)
                },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(book.title, style = AppTypography.bodyLarge)
                Text(authors, style = AppTypography.bodyMedium.copy(color = Color.Gray))
            }
        }
    }
}



