package com.example.bookphoria.ui.book

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookphoria.R
import com.example.bookphoria.data.remote.responses.BookNetworkModel
import com.example.bookphoria.ui.theme.AppTypography
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.viewmodel.ShelfDetailNetworkViewModel

@Composable
fun ShelfDetailNetworkScreen(
    navController: NavController,
    shelfId: String,
    viewModel: ShelfDetailNetworkViewModel = hiltViewModel(),
) {
    val shelfWithBooks by viewModel.shelfWithBooks.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadShelf(shelfId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
    ) {
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

            shelfWithBooks?.let {
                Text(
                    text = it.name,
                    style = AppTypography.titleSmall
                )
            }

            Spacer(modifier = Modifier.width(48.dp))
        }
        shelfWithBooks?.let { shelf ->

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Column {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                    ) {
                        shelf.image?.let { imagePath ->
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Shelf title
                    Text(
                        text = shelf.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    shelf.desc?.let {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                        )
                    }
                }
            }
            BookNetworkCollection(
                books = shelf.books,
                viewModel = viewModel,
                navController = navController
            )
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
fun BookNetworkCollection(
    books: List<BookNetworkModel>,
    viewModel: ShelfDetailNetworkViewModel,
    navController: NavController
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
            val pageFinished by viewModel.pageFinished.collectAsState()
            var isFinished = false
            if (pageFinished == book.pages) {
                isFinished = true
            }

            BookItem(
                coverUrl = book.cover,
                title = book.title,
                author = book.authors?.joinToString(", ") { it.name }.orEmpty(),
                isFinished = isFinished,
                onClick = {
                    navController.navigate("detail/search/${book.id}")
                }
            )
        }
    }
}