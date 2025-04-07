package com.example.bookphoria.ui.book

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
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

    val datePickerState = rememberDatePickerState()

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { uri -> viewModel.imageUrl = uri.toString() }
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

            BookTextField(label = "Judul Buku", value = viewModel.title, onValueChange = {
                viewModel.title = it
            })

            Spacer(modifier = Modifier.height(12.dp))

            BookTextField(label = "Penerbit", value = viewModel.publisher, onValueChange = {
                viewModel.publisher = it
            })

            Row(modifier = Modifier.fillMaxWidth()) {
                BookTextField(
                    label = "Tanggal Terbit",
                    value = viewModel.publishedDate,
                    onValueChange = { viewModel.publishedDate = it },
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pilih Tanggal")
                        }
                    }
                )

                Spacer(modifier = Modifier.width(12.dp))

                BookTextField(
                    label = "Jumlah Halaman",
                    value = viewModel.pages,
                    onValueChange = { viewModel.pages = it },
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Number
                )
            }

            BookTextField(label = "ISBN", value = viewModel.isbn, onValueChange = {
                viewModel.isbn = it
            })

            BookTextField(
                label = "Sinopsis",
                value = viewModel.synopsis,
                onValueChange = { viewModel.synopsis = it },
                maxLines = 6,
                modifier = Modifier.height(120.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Penulis", style = AppTypography.bodyMedium)
            FlowRow {
                viewModel.allAuthors.forEach { author ->
                    val isSelected = author.id in viewModel.selectedAuthorIds
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.selectedAuthorIds = viewModel.selectedAuthorIds.toMutableList().apply {
                                if (isSelected) remove(author.id) else add(author.id)
                            }
                        },
                        label = { Text(author.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Genre", style = AppTypography.bodyMedium)
            FlowRow {
                viewModel.allGenres.forEach { genre ->
                    val isSelected = genre.id in viewModel.selectedGenreIds
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            viewModel.selectedGenreIds = viewModel.selectedGenreIds.toMutableList().apply {
                                if (isSelected) remove(genre.id) else add(genre.id)
                            }
                        },
                        label = { Text(genre.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (viewModel.isValid()) {
                        showConfirmDialog = true
                    } else {
                        Toast.makeText(context, "Mohon lengkapi semua data.", Toast.LENGTH_SHORT).show()
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
                            viewModel.updateBook(bookId) {
                                Toast.makeText(context, "Buku berhasil diperbarui", Toast.LENGTH_SHORT).show()
                                navController.navigate("detail/${bookId}") {
                                    popUpTo("edit_book/$bookId") { inclusive = true }
                                }
                            }
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

@Composable
fun BookTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        maxLines = maxLines,
        readOnly = readOnly,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon = trailingIcon
    )
}
