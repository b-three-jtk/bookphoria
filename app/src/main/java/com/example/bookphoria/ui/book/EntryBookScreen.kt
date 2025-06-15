package com.example.bookphoria.ui.book

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.bookphoria.ui.components.BookTextField
import com.example.bookphoria.ui.helper.uriToFile
import com.example.bookphoria.ui.theme.AppTypography
import com.example.bookphoria.ui.theme.LightBlue
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.viewmodel.EntryBookViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryBookScreen(
    viewModel: EntryBookViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var validationErrors by remember { mutableStateOf(emptyList<String>()) } // Inisialisasi kosong

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = uriToFile(context, it)
            viewModel.coverFile = file
            viewModel.coverUrl = uri.toString()
        }
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
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
                    text = "Tambahkan Buku Baru",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        spotColor = Color(0x40000000),
                        ambientColor = Color(0x40000000)
                    )
                    .align(Alignment.CenterHorizontally)
                    .size(width = 140.dp, height = 200.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(LightBlue)
                    .clickable { imageLauncher.launch("image/*") }
            ) {
                if (viewModel.coverFile != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = viewModel.coverUrl!!.ifBlank { "" }),
                        contentDescription = "Book Cover",
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Box(
                        modifier = Modifier.matchParentSize(),
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
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Edit Cover",
                    tint = Color.Gray,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            BookTextField(
                label = "Judul Buku",
                value = viewModel.title,
                onValueChange = { newValue ->
                    viewModel.title = newValue
                    if (newValue.isNotBlank()) {
                        validationErrors = validationErrors.filter { it != "Judul buku belum diisi" }
                    }
                },
                errorMessage = if (validationErrors.contains("Judul buku belum diisi")) "Judul buku harus diisi" else null
            )

            Spacer(modifier = Modifier.height(20.dp))

            BookTextField(
                label = "Penerbit",
                value = viewModel.publisher,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^[a-zA-Z\\s]*$"))) {
                        viewModel.publisher = newValue
                        if (newValue.isNotBlank()) {
                            validationErrors = validationErrors.filter { it != "Penerbit belum diisi" }
                        }
                    }
                },
                errorMessage = if (validationErrors.contains("Penerbit belum diisi")) "Penerbit harus diisi" else null
            )

            Spacer(modifier = Modifier.height(20.dp))

            BookTextField(
                value = viewModel.authorInput,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^[a-zA-Z\\s]*$"))) {
                        viewModel.authorInput = newValue
                        if (newValue.isNotEmpty() && newValue.matches(Regex("^[a-zA-Z\\s]*$"))) {
                            validationErrors = validationErrors.filter { it != "Penulis belum ditambahkan" }
                        }
                    }
                },
                label = "Penulis",
                trailingIcon = {
                    IconButton(onClick = {
                        if (viewModel.authorInput.isNotBlank() && viewModel.authorInput.matches(Regex("^[a-zA-Z\\s]*$"))) {
                            viewModel.authors.add(viewModel.authorInput.trim())
                            viewModel.authorInput = ""
                            if (viewModel.authors.isNotEmpty()) {
                                validationErrors = validationErrors.filter { it != "Penulis belum ditambahkan" }
                            }
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah Penulis")
                    }
                },
                keyboardType = KeyboardType.Text,
                errorMessage = if (viewModel.authorInput.isNotEmpty() && !viewModel.authorInput.matches(Regex("^[a-zA-Z\\s]*$"))) {
                    "Hanya huruf dan spasi yang diperbolehkan"
                } else if (validationErrors.contains("Penulis belum ditambahkan")) "Penulis harus ditambahkan" else null
            )

            if (viewModel.authors.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.authors.forEach { name ->
                        AssistChip(
                            onClick = { },
                            label = { Text(name) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Hapus Penulis",
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable {
                                            viewModel.authors.remove(name)
                                            if (viewModel.authors.isEmpty()) {
                                                validationErrors = validationErrors + "Penulis belum ditambahkan"
                                            }
                                        }
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    BookTextField(
                        label = "Tanggal Terbit",
                        value = viewModel.publishedDate,
                        onValueChange = { newValue ->
                            viewModel.publishedDate = newValue
                            if (newValue.isNotBlank()) {
                                validationErrors = validationErrors.filter { it != "Tanggal terbit belum diisi" }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                            }
                        },
                        errorMessage = if (validationErrors.contains("Tanggal terbit belum diisi")) "Tanggal terbit harus diisi" else null
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    BookTextField(
                        label = "Jumlah Halaman",
                        value = viewModel.pageCount,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d+$"))) {
                                viewModel.pageCount = newValue
                                if (newValue.isNotBlank()) {
                                    validationErrors = validationErrors.filter { it != "Jumlah halaman belum diisi" }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardType = KeyboardType.Number,
                        errorMessage = if (validationErrors.contains("Jumlah halaman belum diisi")) "Jumlah halaman harus diisi" else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            BookTextField(
                label = "ISBN",
                value = viewModel.isbn,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d+$"))) {
                        viewModel.isbn = newValue
                        if (newValue.isNotBlank()) {
                            validationErrors = validationErrors.filter { it != "ISBN belum diisi" }
                        }
                    }
                },
                keyboardType = KeyboardType.Number,
                errorMessage = if (validationErrors.contains("ISBN belum diisi")) "ISBN harus diisi" else null
            )

            Spacer(modifier = Modifier.height(20.dp))

            BookTextField(
                label = "Sinopsis",
                value = viewModel.synopsis,
                onValueChange = { newValue ->
                    viewModel.synopsis = newValue
                    if (newValue.isNotBlank()) {
                        validationErrors = validationErrors.filter { it != "Sinopsis belum diisi" }
                    }
                },
                maxLines = 6,
                modifier = Modifier.height(120.dp),
                errorMessage = if (validationErrors.contains("Sinopsis belum diisi")) "Sinopsis harus diisi" else null
            )

            Spacer(modifier = Modifier.height(20.dp))

            BookTextField(
                label = "Genre",
                value = viewModel.genreInput,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^[a-zA-Z\\s]*$"))) {
                        viewModel.genreInput = newValue
                        if (newValue.isNotEmpty() && newValue.matches(Regex("^[a-zA-Z\\s]*$"))) {
                            validationErrors = validationErrors.filter { it != "Genre belum ditambahkan" }
                        }
                    }
                },
                trailingIcon = {
                    IconButton(onClick = {
                        if (viewModel.genreInput.isNotBlank() && viewModel.genreInput.matches(Regex("^[a-zA-Z\\s]*$"))) {
                            viewModel.genres.add(viewModel.genreInput.trim())
                            viewModel.genreInput = ""
                            if (viewModel.genres.isNotEmpty()) {
                                validationErrors = validationErrors.filter { it != "Genre belum ditambahkan" }
                            }
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah Genre")
                    }
                },
                keyboardType = KeyboardType.Text,
                errorMessage = if (viewModel.genreInput.isNotEmpty() && !viewModel.genreInput.matches(Regex("^[a-zA-Z\\s]*$"))) {
                    "Hanya huruf dan spasi yang diperbolehkan"
                } else if (validationErrors.contains("Genre belum ditambahkan")) "Genre harus ditambahkan" else null
            )

            if (viewModel.genres.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.genres.forEach { g ->
                        AssistChip(
                            onClick = { /* optional edit */ },
                            label = { Text(g) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Hapus Genre",
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable {
                                            viewModel.genres.remove(g)
                                            if (viewModel.genres.isEmpty()) {
                                                validationErrors = validationErrors + "Genre belum ditambahkan"
                                            }
                                        }
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    validationErrors = viewModel.validateForm()
                    if (validationErrors.isEmpty()) {
                        showConfirmDialog = true
                    } else {
                        Toast.makeText(context, "Mohon lengkapi data yang kurang atau perbaiki kesalahan.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
            ) {
                Text("SIMPAN", style = AppTypography.bodyLarge, color = Color.White)
            }
        }

        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Konfirmasi", style = AppTypography.titleMedium) },
                text = {
                    Text(
                        "Apakah kamu yakin ingin menyimpan perubahan?",
                        style = AppTypography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.addBookToDatabase(
                            onSuccess = { newBookId ->
                                Toast.makeText(
                                    context,
                                    "Buku berhasil ditambahkan!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("detail/$newBookId")
                            },
                            onError = { errorMessage ->
                                Toast.makeText(
                                    context,
                                    "Gagal menyimpan: ${errorMessage ?: "Alasan tidak diketahui"}",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            context = context
                        )
                        showConfirmDialog = false
                    }) {
                        Text("Ya", color = PrimaryOrange)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        viewModel.publishedDate = formatter.format(Date(it))
                        validationErrors = validationErrors.filter { it != "Tanggal terbit belum diisi" }
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
