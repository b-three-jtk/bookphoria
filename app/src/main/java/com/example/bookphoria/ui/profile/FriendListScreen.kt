package com.example.bookphoria.ui.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.bookphoria.R
import com.example.bookphoria.ui.theme.SubTitleExtraSmall
import com.example.bookphoria.ui.theme.TitleExtraSmall
import com.example.bookphoria.ui.viewmodel.FriendViewModel
import kotlinx.coroutines.delay
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.platform.LocalContext
import com.example.bookphoria.data.remote.api.UserWrapperResponse
import com.example.bookphoria.ui.components.LoadingState
import com.example.bookphoria.ui.components.SearchBar
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.theme.SoftOrange

@Composable
fun FriendListContent(
    viewModel: FriendViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val friends by viewModel.friends
    val friendDetail by viewModel.friendSearchDetail
    val isLoading by viewModel.isLoading.collectAsState()
    val showDialog = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }
    val searchResults = remember { mutableStateOf<List<UserWrapperResponse>>(emptyList()) }
    val debouncedQuery = rememberDebouncedState(searchQuery.value)
    var showRequestDialog by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(debouncedQuery) {
        if (debouncedQuery.isNotEmpty()) {
            viewModel.getUserByUsername(debouncedQuery)
        } else {
            searchResults.value = emptyList()
        }
    }

    LaunchedEffect(friendDetail) {
        friendDetail?.let { user ->
            searchResults.value = listOf(user)
        }
    }

    if (isLoading) {
        LoadingState()
        return
    }

    if (friends.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.no_data)
                )
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(200.dp)
                )
                Text(
                    text = "Kamu tidak punya teman:(",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(containerColor = SoftOrange, shape = RoundedCornerShape(24.dp), onClick = { showDialog.value = true }) {
                    Icon(Icons.Filled.PersonAdd, tint = Color.White, contentDescription = "Cari Teman")
                }
            }
        }
    } else {
        Column {
            friends.forEach {
                val fullName = listOfNotNull(it.firstName, it.lastName).joinToString(" ")
                val username = it.username ?: ""

                FriendListItem(
                    name = fullName,
                    username = "@$username",
                    onClick = {
                        navController.navigate("user-profile/${it.id}")
                    },
                    isNotFriend = false
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(containerColor = SoftOrange, shape = RoundedCornerShape(24.dp), onClick = { showDialog.value = true }) {
            Icon(Icons.Filled.PersonAdd, tint = Color.White, contentDescription = "Cari Teman")
        }
    }

    if (showDialog.value) {
        AlertDialog(
            containerColor = SoftCream,
            onDismissRequest = {
                showDialog.value = false
                searchQuery.value = ""
                searchResults.value = emptyList()
            },
            title = { Text("Cari Teman") },
            text = {
                Column {
                    SearchBar(
                        hint = "Masukkan username",
                        modifier = Modifier.fillMaxWidth(),
                        onTextChange = { searchQuery.value = it },
                        onSearchClicked = { },
                        backgroundColor = Color.White,
                        elevation = 4.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search results
                    if (searchQuery.value.isNotEmpty()) {
                        if (searchResults.value.isEmpty()) {
                            Text(
                                text = "Tidak ditemukan hasil untuk '${searchQuery.value}'",
                                color = Color.Gray,
                                modifier = Modifier.padding(8.dp)
                            )
                        } else {
                            LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                                items(searchResults.value) { data ->
                                    val fullName = listOfNotNull(data.user.firstName, data.user.lastName).joinToString(" ")
                                    Log.d("FriendListContent", "User: $data.user")
                                    val username = data.user.username ?: ""

                                    if (username != "") {
                                        FriendListItem(
                                            name = fullName,
                                            username = "@$username",
                                            onClick = {
                                                navController.navigate("user-profile/${data.user.id}")
                                                showDialog.value = false
                                            },
                                            isNotFriend = true,
                                            onAdd = {
                                                showRequestDialog = data.user.id
                                            }
                                        )
                                    } else {
                                        Text(
                                            text = "Tidak ditemukan hasil untuk '${searchQuery.value}'",
                                            color = Color.Gray,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }

                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Masukkan username untuk mencari",
                            color = Color.Gray,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog.value = false
                        searchQuery.value = ""
                        searchResults.value = emptyList()
                    }
                ) {
                    Text("Tutup")
                }
            }
        )

        if (showRequestDialog != null) {
            val selectedUser = searchResults.value.find { it.user.id == showRequestDialog }
            selectedUser?.let { data ->
                AlertDialog(
                    onDismissRequest = { showRequestDialog = null },
                    title = { Text("Kirim Permintaan Teman") },
                    text = {
                        Text("Apakah kamu yakin ingin mengirim permintaan pertemanan ke @${data.user.username}?")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            data.user.username?.let {
                                viewModel.sendFriendRequest(
                                    it,
                                    onSuccess = {
                                        showDialog.value = false
                                        searchQuery.value = ""
                                        searchResults.value = emptyList()
                                        showRequestDialog = null
                                        Toast.makeText(
                                            context,
                                            "Permintaan pertemanan dikirim",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    onError = {
                                        showDialog.value = false
                                        searchQuery.value = ""
                                        searchResults.value = emptyList()
                                        showRequestDialog = null
                                    },
                                )
                            }
                        }) {
                            Text("Kirim")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showRequestDialog = null
                        }) {
                            Text("Batal")
                        }
                    }
                )
            }
        }

    }
}

@Composable
fun rememberDebouncedState(
    value: String,
    delayMillis: Long = 500
): String {
    var debouncedValue by remember { mutableStateOf(value) }

    LaunchedEffect(value) {
        delay(delayMillis)
        debouncedValue = value
    }

    return debouncedValue
}

@Preview
@Composable
private fun FriendListItem(
    name : String = "Nama Dia",
    username : String = "@username",
    onClick : () -> Unit = {},
    isNotFriend : Boolean = false,
    onAdd : () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .clickable { onClick.invoke() },
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .background(color = Color.White)
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.user),
                contentDescription = "Profile Picture",
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Gray, CircleShape)
            )
            Row (
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.8f)
                        .padding(10.dp, 8.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (name.isNotEmpty()) {
                        Text(
                            text = name,
                            style = TitleExtraSmall
                        )
                        Box(modifier = Modifier.height(2.dp))
                    }
                    Text(
                        text = username,
                        style = SubTitleExtraSmall,
                        color = Color.Gray
                    )
                }
                if (isNotFriend) {
                    OutlinedButton(
                        onClick = onAdd,
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, Color.LightGray),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = "Add",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
