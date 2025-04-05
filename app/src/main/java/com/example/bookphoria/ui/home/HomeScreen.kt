package com.example.bookphoria.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookphoria.ui.book.SearchScreen
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.rahad.riobottomnavigation.composables.RioBottomNavItemData
import com.rahad.riobottomnavigation.composables.RioBottomNavigation

@Composable
fun HomeScreen() {
    val items: List<ImageVector> = listOf(
        Icons.Default.Home,
        Icons.Default.Search,
        Icons.Default.Book,
        Icons.Default.Person
    )

    val labels = listOf(
        "Home",
        "Search",
        "My Shelf",
        "Profile"
    )

    val selectedIndex = rememberSaveable { mutableIntStateOf(0) }

    val buttons = items.mapIndexed { index, icon ->
        RioBottomNavItemData(
            imageVector = icon,
            selected = index == selectedIndex.value,
            onClick = { selectedIndex.value = index },
            label = labels[index]
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(buttons = buttons)
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        ScreenContent(selectedIndex.intValue, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun ScreenContent(selectedIndex: Int, modifier: Modifier = Modifier) {
    when (selectedIndex) {
        0 -> HomeScreen()
        1 -> SearchScreen()
        2 -> ShowText("My Shelf")
        3 -> ShowText("Profile")
    }
}

@Composable
fun BottomNavigationBar(buttons: List<RioBottomNavItemData>) {
    RioBottomNavigation(
        fabIcon = Icons.Default.QrCodeScanner,
        buttons = buttons,
        fabSize = 70.dp,
        barHeight = 70.dp,
        selectedItemColor = PrimaryOrange,
        fabBackgroundColor = PrimaryOrange,
    )
}

@Composable
fun ShowText(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = TextStyle(fontSize = 24.sp))
    }
}
