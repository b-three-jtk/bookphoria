package com.example.bookphoria.ui.profile

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bookphoria.ui.theme.DarkIndigo
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.theme.SubTitleExtraSmall
import com.example.bookphoria.ui.theme.TitleExtraSmall
import com.example.bookphoria.ui.viewmodel.FriendViewModel

@Composable
fun FriendScreen(
    viewModel: FriendViewModel = hiltViewModel(),
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.loadRequests()
    }

    Box(modifier = Modifier.fillMaxSize().background(SoftCream)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val tabsList = listOf("Teman", "Requests")
            val selectedTabIndex = remember { mutableStateOf(0) }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                        )
                    }
                    Text(
                        text = "Pertemanan",
                        style = TitleExtraSmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(70.dp))
                        .border(
                            border = BorderStroke(1.dp, Color.LightGray),
                            RoundedCornerShape(70.dp)
                        )
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ClickableTabs(
                        selectedItem = selectedTabIndex.value,
                        tabsList = tabsList,
                        onClick = { index -> selectedTabIndex.value = index }
                    )
                }

                when (selectedTabIndex.value) {
                    0 -> FriendListContent(viewModel = viewModel, navController = navController)
                    1 -> FriendRequestListContent(
                        viewModel = viewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun ClickableTabs(selectedItem: Int, tabsList: List<String>, onClick: (Int) -> Unit) {
    val selectedItemIndex = remember {
        mutableStateOf(selectedItem)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(70.dp))
            .border(
                border = BorderStroke(
                    1.dp,
                    Color.LightGray
                ), RoundedCornerShape(70.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabsList.forEachIndexed { index, s ->
                TabItem(isSelected = index == selectedItemIndex.value, text = s, Modifier.weight(0.5f)) {
                    selectedItemIndex.value = index
                    onClick.invoke(selectedItemIndex.value)
                }
            }
        }
    }
}

@Composable
fun TabItem(isSelected: Boolean, text: String, modifier: Modifier, onClick: () -> Unit) {
    val tabTextColor: Color by animateColorAsState(
        targetValue = if (isSelected) {
            Color.White
        } else {
            DarkIndigo
        },
        animationSpec = tween(easing = LinearEasing), label = ""
    )

    val background : Color by animateColorAsState(targetValue = if (isSelected)
        PrimaryOrange
    else
        Color.White,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
        , label = "")
    val border = if (isSelected)
        BorderStroke(
            1.dp,
            Color.LightGray
        )
    else
        BorderStroke(
            0.dp,
            Color.White
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
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = SubTitleExtraSmall,
            textAlign = TextAlign.Center,
            color = tabTextColor
        )
    }
}


@Composable
fun FriendRequestsContent() {
    Column {
        Text("Daftar Request Teman", style = typography.titleMedium)
    }
}