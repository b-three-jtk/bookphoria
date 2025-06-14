package com.example.bookphoria.ui.profile

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookphoria.ui.theme.SoftCream
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import com.example.bookphoria.R
import com.example.bookphoria.data.remote.responses.BookNetworkModel
import com.example.bookphoria.data.remote.responses.BookStatusNetworkModel
import com.example.bookphoria.data.remote.responses.ShelfNetworkModel
import com.example.bookphoria.ui.book.BookSearchItem
import com.example.bookphoria.ui.components.ConfirmationDialog
import com.example.bookphoria.ui.components.LoadingState
import com.example.bookphoria.ui.theme.DarkIndigo
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.theme.SubTitleExtraSmall
import com.example.bookphoria.ui.theme.TitleExtraSmall
import com.example.bookphoria.ui.viewmodel.FriendViewModel
import com.example.bookphoria.ui.viewmodel.ProfileFriendViewModel
import com.example.bookphoria.ui.viewmodel.isContains

@Composable
fun ProfileFriendScreen(
    userId: Int,
    navController: NavController,
    viewModel: ProfileFriendViewModel,
    friendViewModel: FriendViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val friendDetail by viewModel.friendDetail.collectAsState()
    val profileUserName = friendDetail?.username
    val bookCount by viewModel.bookCount.collectAsState()
    val listCount by viewModel.listCount.collectAsState()
    val friendCount by viewModel.friendCount.collectAsState()
    val tabsList = listOf("Collection", "Readlist")
    val selectedTabIndex = remember { mutableIntStateOf(0) }
    var showRequestDialog by remember { mutableStateOf<Int?>(null) }
    val friends by viewModel.friends.collectAsState()
    val friendBooks by viewModel.friendBooks.collectAsState()
    val friendReadlist by viewModel.shelfBooks.collectAsState()

    LaunchedEffect(userId) {
        viewModel.getFriendById(userId)
    }

    if (friendDetail == null) {
        LoadingState()
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali"
                    )
                }
                friendDetail?.username?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            AsyncImage(
                model = friendDetail?.profilePicture ?: R.drawable.user,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (profileUserName != null) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 15.dp),
                    text = profileUserName,
                    style = TitleExtraSmall
                )
            }

            Row {
                Button(
                    onClick = {
                        if (!friends.any { it.username == friendDetail?.username }) {
                            showRequestDialog = friendDetail!!.id
                        }
                    },
                    modifier = Modifier
                        .wrapContentWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors =
                    if (friends.isContains(friendDetail!!.username.orEmpty())) {
                        ButtonDefaults.buttonColors(containerColor = Color(0xFFE45758))
                    } else {
                        ButtonDefaults.buttonColors(containerColor = Color.White)
                    },
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (friends.any { it.username == friendDetail?.username }) {
                            Text(
                                text = "Friends",
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                style = SubTitleExtraSmall
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Berteman",
                                tint = Color.White
                            )
                        } else {
                            Text(
                                text = "Add Friends",
                                color = DarkIndigo,
                                textAlign = TextAlign.Center,
                                style = SubTitleExtraSmall
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Berteman",
                                tint = DarkIndigo
                            )
                        }
                    }
                }
            }

            if (showRequestDialog != null) {
                ConfirmationDialog(
                    title = "Konfirmasi Permintaan",
                    message = "Anda yakin ingin mengirim permintaan pertemanan ke ${friendDetail!!.username}?",
                    onConfirm = {
                        friendDetail!!.username?.let {
                            friendViewModel.sendFriendRequest(
                                it,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Permintaan pertemanan dikirim",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    showRequestDialog = null
                                },
                                onError = { error ->
                                    Toast.makeText(
                                        context,
                                        "Gagal: $error",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    showRequestDialog = null
                                }
                            )
                        }
                    },
                    onDismiss = { showRequestDialog = null }
                )
            }

            Row(
                modifier = Modifier
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row {
                    Text(
                        modifier = Modifier.padding(end = 3.dp),
                        text = "$bookCount",
                        style = TitleExtraSmall,
                        color = Color.Black
                    )
                    Text(
                        text = "Buku",
                        style = TitleExtraSmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.width(30.dp))

                Row {
                    Text(
                        modifier = Modifier.padding(end = 3.dp),
                        text = "$listCount",
                        style = TitleExtraSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "List Bacaan",
                        style = TitleExtraSmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.width(30.dp))

                Row {
                    Text(
                        modifier = Modifier.padding(end = 3.dp),
                        text = "$friendCount",
                        style = TitleExtraSmall,
                        color = Color.Black
                    )
                    Text(
                        text = "Teman",
                        style = TitleExtraSmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                ClickableTabsProfile(
                    selectedItem = selectedTabIndex.intValue,
                    tabsList = tabsList,
                    onClick = { index -> selectedTabIndex.intValue = index }
                )
            }

            when (selectedTabIndex.intValue) {
                0 -> CollectionContent(friendBooks, navController)
                1 -> ReadlistContent(friendReadlist, navController)
            }
        }

    }
}

@Composable
fun CollectionContent(books: List<BookStatusNetworkModel>, navController: NavController) {
    Spacer(modifier = Modifier.height(10.dp))
    Column {
        if (books.isEmpty()) {
            Text(
                text = "No books available",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            books.forEach { data ->
                BookSearchItem(
                    title = data.book.title ?: "Unknown Title",
                    author = data.book.authors?.joinToString(", ") { it.name ?: "Unknown Author" } ?: "No Authors",
                    imageUrl = data.book.cover ?: "",
                    modifier = Modifier.padding(vertical = 8.dp),
                    onClick = {
                        navController.navigate("detail/search/${data.book.id}")
                    }
                )
            }
        }
    }
}

@Composable
fun ReadlistContent(friendReadlist: List<ShelfNetworkModel>, navController: NavController) {
    Spacer(modifier = Modifier.height(10.dp))
    Column {
        friendReadlist.forEach { shelf ->
            BookSearchItem(
                title = shelf.name,
                author = shelf.desc,
                imageUrl = shelf.image,
                modifier = Modifier.padding(vertical = 8.dp),
                onClick = {
                    navController.navigate("search")
                }
            )
        }
    }
}

@Composable
fun ClickableTabsProfile(selectedItem: Int, tabsList: List<String>, onClick: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabsList.forEachIndexed { index, s ->
            TabItemProfile(
                isSelected = index == selectedItem,
                text = s,
                modifier = Modifier.weight(1f)
            ) {
                onClick(index)
            }
        }
    }
}

@Composable
fun TabItemProfile(isSelected: Boolean, text: String, modifier: Modifier, onClick: () -> Unit) {
    val tabTextColor: Color by animateColorAsState(
        targetValue = if (isSelected) DarkIndigo else Color.Gray,
        animationSpec = tween(easing = LinearEasing), label = ""
    )

    val background : Color by animateColorAsState(targetValue = if (isSelected)
        PrimaryOrange.copy(alpha = 0.3f)
    else
        Color.Transparent,
        animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing), label = "")
    val border = if (isSelected)
        BorderStroke(
            1.dp,
            Color.LightGray
        )
    else
        BorderStroke(
            0.dp,
            Color.Transparent
        )
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxWidth(1f)
            .fillMaxHeight(1f)
            .background(background, RoundedCornerShape(70.dp))
            .border(
                border = border, RoundedCornerShape(70.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {

                onClick.invoke()
            }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TitleExtraSmall,
            textAlign = TextAlign.Center,
            color = tabTextColor
        )
    }
}