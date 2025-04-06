package com.example.bookphoria.ui.book

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookphoria.data.remote.responses.AddBookRequest
import com.example.bookphoria.ui.components.PrimaryButton
import com.example.bookphoria.ui.theme.BodyBottomSheet
import com.example.bookphoria.ui.theme.DarkIndigo
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.viewmodel.BookViewModel

@Composable
fun EntryBookScreen(
    viewModel: BookViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val coverUrl = remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    val authors = remember { mutableStateListOf<String>() }
    var authorInput by remember { mutableStateOf("") }
    var publisher by remember { mutableStateOf("") }
    var publishedDate by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var pageCount by remember { mutableStateOf("") }
    var synopsis by remember { mutableStateOf("") }
    val genres = remember { mutableStateListOf<String>() }
    var genreInput by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali"
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Tambahkan Buku Baru", style = MaterialTheme.typography.titleSmall)
                Text("Baca buku apa hari ini?", style = BodyBottomSheet)
            }
        }



        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CoverImagePicker(coverUrl.value) { newUrl -> coverUrl.value = newUrl }
        }

        LabeledTextField(
            value = title,
            onValueChange = { title = it },
            label = "Judul Buku"
        )

        LabeledTextField(
            value = authorInput,
            onValueChange = { authorInput = it },
            label = "Penulis",
            trailingIcon = {
                IconButton(onClick = {
                    if (authorInput.isNotBlank()) {
                        authors.add(authorInput.trim())
                        authorInput = ""
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Penulis")
                }
            }
        )

        if (authors.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                authors.forEach { name ->
                    AssistChip(
                        onClick = { /* optional edit */ },
                        label = { Text(name) }
                    )
                }
            }
        }

        LabeledTextField(
            value = publisher,
            onValueChange = { publisher = it },
            label = "Penerbit"
        )

        LabeledTextField(
            value = publishedDate,
            onValueChange = { publishedDate = it },
            label = "Tanggal Terbit"
        )

        LabeledTextField(
            value = isbn,
            onValueChange = { isbn = it },
            label = "ISBN"
        )

        LabeledTextField(
            value = pageCount,
            onValueChange = { pageCount = it },
            label = "Jumlah Halaman",
            keyboardType = KeyboardType.Number
        )

        LabeledTextField(
            value = synopsis,
            onValueChange = { synopsis = it },
            label = "Sinopsis",
        )

        LabeledTextField(
            value = genreInput,
            onValueChange = { genreInput = it },
            label = "Genre",
            trailingIcon = {
                IconButton(onClick = {
                    if (genreInput.isNotBlank()) {
                        genres.add(genreInput.trim())
                        genreInput = ""
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Genre")
                }
            }
        )

        if (genres.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                genres.forEach { g ->
                    AssistChip(
                        onClick = { /* optional edit */ },
                        label = { Text(g) }
                    )
                }
            }
        }

        PrimaryButton(
            text = "Simpan",
            backgroundColor = PrimaryOrange,
            onClick = {
                val request = AddBookRequest(
                    title = title,
                    publisher = publisher,
                    publishedDate = publishedDate,
                    synopsis = synopsis,
                    isbn = isbn,
                    pages = pageCount.toIntOrNull() ?: 0,
                    cover = coverUrl.value,
                    authors = authors.toList(),
                    genres = genres.toList(),
                    userStatus = "owned",
                    userPageCount = 0,
                    userStartDate = null,
                    userFinishDate = null
                )

                viewModel.addBookToDatabase(
                    request = request,
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "Buku berhasil ditambahkan!",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate("home")
                    },
                    onError = {
                        Toast.makeText(
                            navController.context,
                            "Terjadi kesalahan saat menambahkan buku!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        )
    }
}


@Composable
fun CoverImagePicker(imageUrl: String, onImagePicked: (String) -> Unit) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.LightGray)
            .clickable {
                onImagePicked("https://example.com/fake-uploaded-image.jpg")
            },
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl.isEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Image, contentDescription = null)
                Text("Upload", style = MaterialTheme.typography.bodySmall)
            }
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Cover Buku",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun LabeledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        colors = TextFieldDefaults.colors(
            unfocusedTextColor = DarkIndigo,
            focusedTextColor = DarkIndigo.copy(alpha = 0.5f),
            cursorColor = DarkIndigo,
            focusedContainerColor = Color.LightGray,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(20.dp),
    )
}
