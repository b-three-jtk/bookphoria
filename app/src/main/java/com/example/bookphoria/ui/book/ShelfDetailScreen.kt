package com.example.bookphoria.ui.book

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.bookphoria.R
import com.example.bookphoria.data.local.entities.BookEntity
import com.example.bookphoria.data.local.entities.ShelfEntity
import com.example.bookphoria.ui.helper.uriToFile
import com.example.bookphoria.ui.theme.AppTypography
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.viewmodel.MyShelfViewModel
import com.example.bookphoria.ui.viewmodel.ShelfDetailViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ShelfDetailScreen(
    navController: NavController,
    userId: Int,
    shelfId: Int,
    viewModel: ShelfDetailViewModel = hiltViewModel(),
    myShelfViewModel: MyShelfViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val shelfWithBooks by viewModel.shelfWithBooks.collectAsState()
    val booksWithAuthors by myShelfViewModel.booksWithAuthors.collectAsState()
    val addResult by viewModel.addBookResult.collectAsState()
    var showBookPicker by remember { mutableStateOf(false) }
    val deleteResult by viewModel.deleteResult.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showEditShelf by remember { mutableStateOf(false) }
    var shelfName by remember { mutableStateOf("") }
    var shelfDescription by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var imageFile by remember { mutableStateOf<File?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadShelfWithBooks(userId, shelfId)
        myShelfViewModel.loadUserBooks()
    }

    LaunchedEffect(showEditShelf) {
        if (showEditShelf) {
            shelfWithBooks?.shelf?.let {
                shelfName = it.name
                shelfDescription = it.description ?: ""
            }
        }
    }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = uriToFile(context, it)
            imageFile = file
            shelfWithBooks?.shelf?.imagePath = uri.toString()
            Log.d("ShelfDetailScreen", "Image URI: $uri")
        }
    }


    LaunchedEffect(addResult) {
        addResult?.let {
            if (it.isSuccess) {
                showBookPicker = false
            }
            delay(1500)
            viewModel.resetAddBookResult()
        }
    }

    LaunchedEffect(deleteResult) {
        deleteResult?.let {
            if (it.isSuccess) {
                navController.popBackStack() // kembali setelah hapus sukses
            } else {
                // tampilkan error
                Log.e("ShelfDelete", "Error: ${it.exceptionOrNull()}")
            }
            viewModel.resetDeleteResult()
        }
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

            Text(
                text = "Your Books",
                style = AppTypography.titleSmall
            )

            Spacer(modifier = Modifier.width(48.dp))
        }
        shelfWithBooks?.let { shelf ->
            // Call ShelfHeader with proper parameters
            ShelfHeader(
                shelf = shelf.shelf,
                bookCount = shelf.books.size,
                onAddClick = { showBookPicker = true },
                onRemoveClick =  { showDialog = true },
                showEditShelf = { showEditShelf = true }
            )

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
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Konfirmasi") },
                text = { Text("Yakin ingin menghapus rak ini?") },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        viewModel.deleteShelf(shelfId = shelfId)
                    }) {
                        Text("Ya")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }

        // Dialog popup untuk pilih buku
        if (showBookPicker) {
            Dialog(onDismissRequest = { showBookPicker = false }) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Pilih Buku", style = AppTypography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (booksWithAuthors.isEmpty()) {
                            Text("Kamu belum punya buku.")
                        } else {
                            LazyColumn(modifier = Modifier.height(300.dp)) {
                                items(booksWithAuthors) { bookWithAuthors ->
                                    val book = bookWithAuthors.book
                                    val authors =
                                        bookWithAuthors.authors.joinToString(", ") { it.name }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.addBookToShelf(
                                                    shelfId = shelfId,
                                                    bookId = book.id
                                                )
                                            }
                                            .padding(vertical = 8.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = book.title,
                                                style = AppTypography.bodyMedium
                                            )
                                            Text(
                                                text = "by $authors",
                                                style = AppTypography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showEditShelf) {
            Dialog(onDismissRequest = { showBookPicker = false }) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SoftCream,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (shelfWithBooks?.shelf?.imagePath != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = imageFile ?: shelfWithBooks?.shelf?.imagePath),
                                    contentDescription = "Selected Image",
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(BorderStroke(1.dp, Color.Gray))
                                        .clickable { imageLauncher.launch("image/*") }
                                )

                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(BorderStroke(1.dp, Color.Gray))
                                        .clickable { imageLauncher.launch("image/*") },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = rememberAsyncImagePainter(model = shelfWithBooks?.shelf?.imagePath),
                                        contentDescription = "Select Image"
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Give your shelf a name",
                                style = AppTypography.headlineSmall,
                                textAlign = TextAlign.Left
                            )

                            shelfWithBooks?.shelf?.let {
                                OutlinedTextField(
                                    value = shelfName,
                                    onValueChange = { shelfName = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = SoftCream,
                                        unfocusedContainerColor = SoftCream,
                                        focusedIndicatorColor = Color.Black,
                                        unfocusedIndicatorColor = Color.Black,
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black
                                    ),
                                    singleLine = true,
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Give your shelf description",
                                style = AppTypography.headlineSmall,
                                textAlign = TextAlign.Left
                            )

                            OutlinedTextField(
                                value = shelfDescription ?: "",
                                onValueChange = { shelfDescription = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = SoftCream,
                                    unfocusedContainerColor = SoftCream,
                                    focusedIndicatorColor = Color.Black,
                                    unfocusedIndicatorColor = Color.Black,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                ),
                            )
                        }

                        Spacer(modifier = Modifier.height(72.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            Button(
                                onClick = { showEditShelf = false },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Gray,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(bottomStart = 20.dp),
                            ) {
                                Text("Batal")
                            }

                            Button(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(bottomEnd = 20.dp),
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.updateShelf(
                                            name = shelfName,
                                            desc = shelfDescription.takeIf { it.isNotBlank() },
                                            imageUri = shelfWithBooks?.shelf?.imagePath,
                                            imageFile = imageFile
                                        )
                                        showEditShelf = false
                                    }
                                }
                            ) {
                                Text("Simpan")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShelfHeader(
    shelf: ShelfEntity,
    bookCount: Int,
    onAddClick: () -> Unit,
    onRemoveClick: () -> Unit,
    showEditShelf: () -> Unit
)
{
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

                // Add & book count
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    IconButton(
                        onClick = { onAddClick() },
                        modifier = Modifier.size(16.dp)
                    ){
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Book",
                            tint = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = {
                            showEditShelf()
                        },
                        modifier = Modifier.size(16.dp)
                    ){
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Shelf",
                            tint = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = { onRemoveClick() },
                        modifier = Modifier.size(16.dp)
                    ){
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Shelf",
                            tint = Color.Red
                        )
                    }

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
            var authorName by remember { mutableStateOf("") }
            var isFinished by remember(book.id) { mutableStateOf(false) }

            LaunchedEffect(book.id) {
                try {
                    val bookId = book.id
                    authorName =
                        viewModel.getBookAuthor(bookId)?.authors?.joinToString(", ") { it.name ?: "Unknown" }
                            .toString()
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