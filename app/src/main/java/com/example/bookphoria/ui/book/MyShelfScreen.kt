package com.example.bookphoria.ui.book

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.bookphoria.R
import com.example.bookphoria.ui.theme.*
import com.example.bookphoria.ui.viewmodel.ShelfUiState
import com.example.bookphoria.ui.viewmodel.ShelfViewModel

@Composable
fun MyShelfScreen(
    viewModel: ShelfViewModel = hiltViewModel(),
    onCreateCollectionClick: () -> Unit = {}
) {
    val dummyCollections = listOf(
        "Books to make you smile",
        "Current favs",
        "In the feels",
        "Crying in the prettiest places",
        "<3 <3 <3"
    )

    var showCreateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Shelf",
                style = AppTypography.titleSmall
            )
            OutlinedButton(
                onClick = { showCreateDialog = true },
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, color = Color.Gray),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = LocalContentColor.current
                )
            ) {
                Text("Buat koleksi baru",
                    style = AppTypography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(dummyCollections) { title ->
                ShelfItem(title = title)
            }
        }
    }
    if (showCreateDialog) {
        CreateCollectionDialog(
            onDismiss = { showCreateDialog = false },
            onSaveSuccess = {
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun CreateCollectionDialog(
    viewModel: ShelfViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    var collectionName by remember { mutableStateOf("") }
    var collectionDescription by remember { mutableStateOf("") }
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri.value = uri
    }

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is ShelfUiState.Success -> {
                onSaveSuccess()
                onDismiss()
                viewModel.resetState()
            }
            is ShelfUiState.Error -> {
                val errorState = uiState as ShelfUiState.Error
                Toast.makeText(context, errorState.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SoftCream,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (uiState is ShelfUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .zIndex(1f)
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp)
                        .align(Alignment.TopCenter)
                        .alpha(if (uiState is ShelfUiState.Loading) 0.5f else 1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (imageUri.value != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri.value),
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(BorderStroke(1.dp, Color.Gray))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(BorderStroke(1.dp, Color.Gray))
                                .clickable { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
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

                    TextField(
                        value = collectionName,
                        onValueChange = { collectionName = it },
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
                        enabled = uiState !is ShelfUiState.Loading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Give your shelf description",
                        style = AppTypography.headlineSmall,
                        textAlign = TextAlign.Left
                    )

                    TextField(
                        value = collectionDescription,
                        onValueChange = { collectionDescription = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SoftCream,
                            unfocusedContainerColor = SoftCream,
                            focusedIndicatorColor = Color.Black,
                            unfocusedIndicatorColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        enabled = uiState !is ShelfUiState.Loading
                    )

                    Spacer(modifier = Modifier.height(72.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(bottomStart = 20.dp),
                        enabled = uiState !is ShelfUiState.Loading
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            if (collectionName.isNotBlank()) {
                                viewModel.createShelf(
                                    name = collectionName,
                                    desc = collectionDescription.takeIf { it?.isNotBlank() == true },
                                    imageUri = imageUri.value
                                )
                            }
                        },
                        enabled = collectionName.isNotBlank() && uiState !is ShelfUiState.Loading
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}

@Composable
fun ShelfItem(title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.sample_koleksi),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    title,
                    style = AppTypography.bodyLarge
                )
                Text(
                    "3 Books",
                    style = AppTypography.bodyMedium.copy(color = Color.Gray)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyShelfScreenPreview() {
    MyShelfScreen()
}

@Preview
@Composable
fun CreateCollectionDialogPreview() {
    CreateCollectionDialog(
        onDismiss = {},
        onSaveSuccess = {}
    )
}