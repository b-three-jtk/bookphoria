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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.bookphoria.R
import com.example.bookphoria.ui.components.ConfirmationDialog
import com.example.bookphoria.ui.components.LoadingState
import com.example.bookphoria.ui.theme.DarkIndigo
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.theme.SubTitleExtraSmall
import com.example.bookphoria.ui.theme.TitleExtraSmall
import com.example.bookphoria.ui.viewmodel.FriendViewModel

@Composable
fun ProfileFriendScreen(
    userId: Int,
    navController: NavController,
    viewModel: FriendViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val friendDetail = viewModel.friendDetail.value
    val profileName = friendDetail?.username.takeIf {
        friendDetail?.firstName.isNullOrBlank() && friendDetail?.lastName.isNullOrBlank()
    } ?: "${friendDetail?.firstName.orEmpty()} ${friendDetail?.lastName.orEmpty()}".trim()
    val profileUserName = friendDetail?.username
    val bookCount by remember { mutableStateOf("0") }
    val listCount by remember { mutableStateOf("0") }
    val friendCount by remember { mutableStateOf("0") }
    val tabsList = listOf("Collection", "Readlist", "Borrow Rules")
    val selectedTabIndex = remember { mutableStateOf(0) }
    var showRequestDialog by remember { mutableStateOf<Int?>(null) }

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
                Text(
                    text = profileName,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Image(
                painter = painterResource(id = R.drawable.user),
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
                        if (!viewModel.friends.value.contains(friendDetail)) {
                            showRequestDialog = friendDetail.id
                        }
                    },
                    modifier = Modifier
                        .wrapContentWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors =
                    if (viewModel.friends.value.contains(friendDetail)) {
                        ButtonDefaults.buttonColors(containerColor = Color(0xFFE45758))
                    } else {
                        ButtonDefaults.buttonColors(containerColor = Color.White)
                    },
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (viewModel.friends.value.contains(friendDetail)) {
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
                    message = "Anda yakin ingin mengirim permintaan pertemanan ke ${friendDetail.username}?",
                    onConfirm = {
                        friendDetail.username?.let {
                            viewModel.sendFriendRequest(
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
                                        "Gagal: ${error ?: "Terjadi kesalahan."}",
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
                        text = bookCount,
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
                        text = listCount,
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
                        text = friendCount,
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
                    selectedItem = selectedTabIndex.value,
                    tabsList = tabsList,
                    onClick = { index -> selectedTabIndex.value = index }
                )
            }

            when (selectedTabIndex.value) {
                0 -> CollectionContent()
                1 -> ReadlistContent()
                2 -> RulesContent()
            }
        }

    }
}

@Composable
fun CollectionContent() {
    Text(text = "Collection")
}

@Composable
fun ReadlistContent() {
    Text("Readlist")
}

@Composable
fun RulesContent() {
    Text("Rules")
}

@Composable
fun ClickableTabsProfile(selectedItem: Int, tabsList: List<String>, onClick: (Int) -> Unit) {
    val selectedItemIndex = remember {
        mutableStateOf(selectedItem)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabsList.forEachIndexed { index, s ->
                TabItemProfile(isSelected = index == selectedItemIndex.value, text = s, Modifier.weight(0.5f)) {
                    selectedItemIndex.value = index
                    onClick.invoke(selectedItemIndex.value)
                }
            }
        }
    }
}

@Composable
fun TabItemProfile(isSelected: Boolean, text: String, modifier: Modifier, onClick: () -> Unit) {
    val tabTextColor: Color by animateColorAsState(
        targetValue = DarkIndigo,
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