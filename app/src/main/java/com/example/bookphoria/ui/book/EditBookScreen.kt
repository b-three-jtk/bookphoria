package com.example.bookphoria.ui.book

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookphoria.ui.components.BookTextField
import com.example.bookphoria.ui.helper.uriToFile
import com.example.bookphoria.ui.theme.*
import com.example.bookphoria.ui.viewmodel.EditBookViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditBookScreen(
    bookId: Int,
    viewModel: EditBookViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var validationErrors by remember { mutableStateOf(emptyList<String>()) } // Inisialisasi kosong

    val datePickerState = rememberDatePickerState()

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = uriToFile(context, it)
            viewModel.imageFile = file
            viewModel.imageUrl = uri.toString()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadBook(bookId)
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(500)),
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
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
                    text = "Edit Buku",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LightBlue)
                    .clickable { imageLauncher.launch("image/*") }
            ) {
                AsyncImage(
                    model = viewModel.imageUrl,
                    contentDescription = "Book Cover",
                    modifier = Modifier.matchParentSize()
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Cover",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(28.dp)
                        .background(PrimaryOrange, CircleShape)
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Judul Buku
            BookTextField(
                label = "Judul Buku",
                modifier = Modifier.padding(top = 16.dp),
                value = viewModel.title,
                onValueChange = { newValue ->
                    viewModel.title = newValue
                    if (newValue.isNotBlank()) {
                        validationErrors = validationErrors.filter { it != "Judul buku belum diisi" }
                    }
                },
                errorMessage = if (validationErrors.contains("Judul buku belum diisi")) "Judul buku harus diisi" else null
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Penerbit
            BookTextField(
                label = "Penerbit",
                modifier = Modifier.padding(top = 16.dp),
                value = viewModel.publisher,
                onValueChange = { newValue ->
                    viewModel.publisher = newValue
                    if (newValue.isNotBlank()) {
                        validationErrors = validationErrors.filter { it != "Penerbit belum diisi" }
                    }
                },
                errorMessage = if (validationErrors.contains("Penerbit belum diisi")) "Penerbit harus diisi" else null
            )

            // Tanggal Terbit dan Jumlah Halaman
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                        value = viewModel.pages,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d+$"))) {
                                viewModel.pages = newValue
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

            // ISBN
            BookTextField(
                label = "ISBN",
                modifier = Modifier.padding(top = 16.dp),
                value = viewModel.isbn,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d{0,13}$"))) {
                        viewModel.isbn = newValue
                        if (newValue.isNotBlank()) {
                            validationErrors = validationErrors.filter { it != "ISBN belum diisi" }
                        }
                    }
                },
                keyboardType = KeyboardType.Number,
                errorMessage = if (validationErrors.contains("ISBN belum diisi")) "ISBN harus diisi" else null
            )

            // Sinopsis
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
                modifier = Modifier.height(120.dp).padding(top = 16.dp),
                errorMessage = if (validationErrors.contains("Sinopsis belum diisi")) "Sinopsis harus diisi" else null
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Penulis",
                style = AppTypography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp),
            )
            FlowRow {
                viewModel.allAuthors.forEach { author ->
                    val isSelected = author.serverId in viewModel.selectedAuthorIds
                    FilterChip(
                        modifier = Modifier.padding(end = 4.dp),
                        selected = isSelected,
                        onClick = {
                            viewModel.selectedAuthorIds = viewModel.selectedAuthorIds.toMutableList().apply {
                                if (isSelected) remove(author.serverId) else add(author.serverId)
                            }
                        },
                        label = { Text(author.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Genre",
                style = AppTypography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp),
            )
            FlowRow {
                viewModel.allGenres.forEach { genre ->
                    val isSelected = genre.serverId in viewModel.selectedGenreIds
                    FilterChip(
                        modifier = Modifier.padding(end = 4.dp),
                        selected = isSelected,
                        onClick = {
                            viewModel.selectedGenreIds = viewModel.selectedGenreIds.toMutableList().apply {
                                if (isSelected) remove(genre.serverId) else add(genre.serverId)
                            }
                        },
                        label = { Text(genre.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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

            if (showConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    title = { Text("Konfirmasi", style = AppTypography.titleMedium) },
                    text = { Text("Apakah kamu yakin ingin menyimpan perubahan?", style = AppTypography.bodyMedium) },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.updateBook(viewModel.bookNetworkId,
                                onSuccess = {
                                    Toast.makeText(context, "Buku berhasil diperbarui", Toast.LENGTH_SHORT).show()
                                    navController.navigate("detail/${bookId}") {
                                        popUpTo("edit_book/$bookId") { inclusive = true }
                                    }
                                },
                                onError = { errorMessage ->
                                    Toast.makeText(context, "Gagal menyimpan: ${errorMessage ?: "Alasan tidak diketahui"}", Toast.LENGTH_LONG).show()
                                }
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

//@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
//@Composable
//fun EditBookScreen(
//    bookId: Int,
//    viewModel: EditBookViewModel,
//    navController: NavController
//) {
//    val context = LocalContext.current
//    val scrollState = rememberScrollState()
//
//    var showConfirmDialog by remember { mutableStateOf(false) }
//    var showDatePicker by remember { mutableStateOf(false) }
//    var validationErrors by remember { mutableStateOf(listOf<String>()) }
//
//    val datePickerState = rememberDatePickerState()
//
//    val imageLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let {
//            val file = uriToFile(context, it)
//            viewModel.imageFile = file
//            viewModel.imageUrl = uri.toString()
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        viewModel.loadBook(bookId)
//    }
//
//    AnimatedVisibility(
//        visible = true,
//        enter = fadeIn(animationSpec = tween(500)),
//        exit = fadeOut()
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//                .verticalScroll(scrollState)
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                IconButton(onClick = { navController.popBackStack() }) {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                        contentDescription = "Kembali"
//                    )
//                }
//                Text(
//                    text = "Edit Buku",
//                    style = MaterialTheme.typography.titleMedium,
//                    modifier = Modifier.padding(start = 8.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Box(
//                contentAlignment = Alignment.BottomEnd,
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally)
//                    .size(140.dp)
//                    .clip(RoundedCornerShape(16.dp))
//                    .background(LightBlue)
//                    .clickable { imageLauncher.launch("image/*") }
//            ) {
//                AsyncImage(
//                    model = viewModel.imageUrl,
//                    contentDescription = "Book Cover",
//                    modifier = Modifier.matchParentSize()
//                )
//                Icon(
//                    imageVector = Icons.Default.Edit,
//                    contentDescription = "Edit Cover",
//                    tint = Color.White,
//                    modifier = Modifier
//                        .padding(8.dp)
//                        .size(28.dp)
//                        .background(PrimaryOrange, CircleShape)
//                        .padding(4.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            BookTextField(label = "Judul Buku", modifier = Modifier.padding(top = 16.dp), value = viewModel.title, onValueChange = {
//                viewModel.title = it
//            })
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            BookTextField(label = "Penerbit", modifier = Modifier.padding(top = 16.dp), value = viewModel.publisher, onValueChange = {
//                viewModel.publisher = it
//            })
//
//            Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
//                BookTextField(
//                    label = "Tanggal Terbit",
//                    value = viewModel.publishedDate,
//                    onValueChange = { viewModel.publishedDate = it },
//                    modifier = Modifier.weight(1f),
//                    readOnly = true,
//                    trailingIcon = {
//                        IconButton(onClick = { showDatePicker = true }) {
//                            Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
//                        }
//                    }
//                )
//
//                Spacer(modifier = Modifier.width(12.dp))
//
//                BookTextField(
//                    label = "Jumlah Halaman",
//                    value = viewModel.pages,
//                    onValueChange = {
//                        if (it.isEmpty() || it.matches(Regex("^\\d+$"))) {
//                        viewModel.pages = it
//                    } },
//                    modifier = Modifier.weight(1f).padding(top = 16.dp),
//                    keyboardType = KeyboardType.Number,
//                    errorMessage = if(viewModel.pages.isNotEmpty() && !viewModel.pages.matches(Regex("^\\d+$"))) {
//                        "Hanya angka yang diperbolehkan"
//                    } else null
//                )
//            }
//
//            BookTextField(
//                label = "ISBN",
//                modifier = Modifier.padding(top = 16.dp),
//                value = viewModel.isbn,
//                onValueChange = {
//                    if (it.isEmpty() || it.matches(Regex("^\\d{0,13}$"))) {
//                        viewModel.isbn = it
//                    }
//                },
//                keyboardType = KeyboardType.Number,
//                errorMessage = if (viewModel.isbn.isNotEmpty() && !viewModel.isbn.matches(Regex("^\\d{10}|\\d{13}$"))) {
//                    "ISBN harus 10 atau 13 digit"
//                } else null
//            )
//
//            BookTextField(
//                label = "Sinopsis",
//                value = viewModel.synopsis,
//                onValueChange = { viewModel.synopsis = it },
//                maxLines = 6,
//                modifier = Modifier.height(120.dp).padding(top = 16.dp)
//            )
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Text(
//                "Penulis",
//                style = AppTypography.bodyMedium,
//                modifier = Modifier.padding(top = 16.dp),
//            )
//            FlowRow {
//                viewModel.allAuthors.forEach { author ->
//                    val isSelected = author.serverId in viewModel.selectedAuthorIds
//                    FilterChip(
//                        modifier = Modifier.padding(end = 4.dp),
//                        selected = isSelected,
//                        onClick = {
//                            viewModel.selectedAuthorIds = viewModel.selectedAuthorIds.toMutableList().apply {
//                                if (isSelected) remove(author.serverId) else add(author.serverId)
//                            }
//                        },
//                        label = { Text(author.name) }
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                "Genre",
//                style = AppTypography.bodyMedium,
//                modifier = Modifier.padding(top = 16.dp),
//            )
//            FlowRow {
//                viewModel.allGenres.forEach { genre ->
//                    val isSelected = genre.serverId in viewModel.selectedGenreIds
//                    FilterChip(
//                        modifier = Modifier.padding(end = 4.dp),
//                        selected = isSelected,
//                        onClick = {
//                            viewModel.selectedGenreIds = viewModel.selectedGenreIds.toMutableList().apply {
//                                if (isSelected) remove(genre.serverId) else add(genre.serverId)
//                            }
//                        },
//                        label = { Text(genre.name) }
//                    )
//                }
//            }
//
//            if (validationErrors.isNotEmpty()) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp),
//                ) {
//                    Text(
//                        text = "Error Validasi:",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = Color.Red
//                    )
//                    validationErrors.forEach { error ->
//                        Text(
//                            text = "• $error",
//                            style = MaterialTheme.typography.bodySmall,
//                            color = Color.Red,
//                            modifier = Modifier.padding(start = 16.dp)
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Button(
//                onClick = {
//                    if (viewModel.isValid()) {
//                        showConfirmDialog = true
//                    } else {
//                        Toast.makeText(context, "Mohon lengkapi semua data.", Toast.LENGTH_SHORT).show()
//                    }
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(48.dp),
//                shape = RoundedCornerShape(50),
//                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
//            ) {
//                Text("SIMPAN", style = AppTypography.bodyLarge, color = Color.White)
//            }
//
//            if (showConfirmDialog) {
//                AlertDialog(
//                    onDismissRequest = { showConfirmDialog = false },
//                    title = { Text("Konfirmasi", style = AppTypography.titleMedium) },
//                    text = { Text("Apakah kamu yakin ingin menyimpan perubahan?", style = AppTypography.bodyMedium) },
//                    confirmButton = {
//                        TextButton(onClick = {
//                            viewModel.updateBook(viewModel.bookNetworkId) {
//                                Toast.makeText(context, "Buku berhasil diperbarui", Toast.LENGTH_SHORT).show()
//                                navController.navigate("detail/${bookId}") {
//                                    popUpTo("edit_book/$bookId") { inclusive = true }
//                                }
//                            }
//                            showConfirmDialog = false
//                        }) {
//                            Text("Ya", color = PrimaryOrange)
//                        }
//                    },
//                    dismissButton = {
//                        TextButton(onClick = { showConfirmDialog = false }) {
//                            Text("Batal")
//                        }
//                    }
//                )
//            }
//        }
//    }
//
//    if (showDatePicker) {
//        DatePickerDialog(
//            onDismissRequest = { showDatePicker = false },
//            confirmButton = {
//                TextButton(onClick = {
//                    datePickerState.selectedDateMillis?.let {
//                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                        viewModel.publishedDate = formatter.format(Date(it))
//                    }
//                    showDatePicker = false
//                }) {
//                    Text("OK")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showDatePicker = false }) {
//                    Text("Batal")
//                }
//            }
//        ) {
//            DatePicker(state = datePickerState)
//        }
//    }
//}
