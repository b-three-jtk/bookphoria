package com.example.bookphoria.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.bookphoria.R
import com.example.bookphoria.data.local.entities.BookWithGenresAndAuthors
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavController) {
    val userName by viewModel.userName.collectAsState()
    val avatar by viewModel.avatar.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(SoftCream)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFE4B5),
                            Color(0xFFFCD8C4)
                        )
                    ),
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = avatar.ifBlank { R.drawable.user }),
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.LightGray, CircleShape)
                    )
                }
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = "Morning Icon",
                    tint = Color.Black
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Halo, $userName",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )

                Text(
                    text = "Baca buku apa hari ini?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray

                )
            }
        }

        SearchBarHome()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 240.dp)
        ) {
            BookSection(viewModel = viewModel, navController = navController)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarHome() {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val searchHistory = listOf("")

    DockedSearchBar(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .offset(y = 170.dp),
        colors = SearchBarDefaults.colors(
            containerColor = Color.White
        ),
        query = query,
        onQueryChange = { query = it },
        onSearch = {
            println("do search with query $query")
            active = false
        },
        active = active,
        onActiveChange = { active = it },
        placeholder = {
            Text(text = "Search", color = Color.Gray)
        },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
        },
        trailingIcon = {
            if (active) {
                IconButton(
                    onClick = {
                        if (query.isNotEmpty()) query = "" else active = false
                    }
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Icon")
                }
            }
        },
        tonalElevation = 4.dp,
        shape = RoundedCornerShape(25.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            searchHistory.takeLast(3).forEach { item ->
                ListItem(
                    modifier = Modifier.clickable {
                        query = item
                        active = false
                    },
                    headlineContent = {
                        Text(text = item)
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun BookSection(
    userBooks: List<BookWithGenresAndAuthors> = emptyList(),
    viewModel: HomeViewModel,
    navController: NavController
) {
    val yourBooks by viewModel.yourBooks.collectAsState(initial = userBooks)
    val yourCurrentlyReadingBooks by viewModel.currentlyReading.collectAsState()

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Your Books",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Lainnya",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                modifier = Modifier.clickable { /* Handle "Lainnya" click */ }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        if (yourBooks.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                    Row(
                        modifier = Modifier.width(174.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        yourBooks.take(5).forEach { bookWithDetails ->
                            val book = bookWithDetails.book
                            val authorNames = bookWithDetails.authors.joinToString(", ") { it.name }

                            BookItem(
                                title = book.title,
                                author = authorNames,
                                imageUrl = book.imageUrl,
                                onClick = {
                                    navController.navigate("detail/${book.id}")
                                }
                            )
                        }
                    }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.book),
                    contentDescription = "Bookshelf",
                    modifier = Modifier
                        .height(140.dp)
                        .padding(10.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Belum ada buku yang ditambahkan ke koleksi", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Currently Reading",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (yourCurrentlyReadingBooks.isNotEmpty()) {
            val currentBook = yourCurrentlyReadingBooks.first()
            val authorNames = currentBook.authors.joinToString(", ") { it.name }

            CurrentReadingItem(
                title = currentBook.book.title,
                author = authorNames,
                progress = 0.5f,
                imageUrl = currentBook.book.imageUrl
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bookshelf),
                    contentDescription = "Bookshelf",
                    modifier = Modifier
                        .height(140.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Belum ada buku yang sedang dibaca", color = Color.Gray)
            }
        }
    }
}

@Composable
fun BookItem(
    title: String,
    author: String,
    imageUrl: String?,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF96ADD6).copy(alpha = 0.76f))
            .padding(12.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (imageUrl != "") {
            AsyncImage(
                model = imageUrl ?: R.drawable.bookshelf,
                contentDescription = title,
                modifier = Modifier
                    .height(160.dp)
                    .width(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier.height(160.dp)
                    .width(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray)
                    Text(
                        "Add Cover",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = author,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CurrentReadingItem(
    title: String,
    author: String,
    progress: Float,
    imageUrl: String? // Ubah dari imageRes: Int ke imageUrl: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        AsyncImage(
            model = imageUrl ?: R.drawable.bookshelf,
            contentDescription = title,
            modifier = Modifier
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(text = title, fontWeight = FontWeight.Bold)
            Text(text = author, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {},
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD3F1DE)),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(text = "Reading", color = Color(0xFF228B22))
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = Color(0xFF4F46E5),
                trackColor = Color(0xFFEAEAEA),
            )
            Text(text = "${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
        }
    }
}