package com.example.bookphoria.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookphoria.ui.book.SearchScreen
import com.example.bookphoria.ui.theme.BodyBottomSheet
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.theme.TitleExtraSmall
import com.rahad.riobottomnavigation.composables.RioBottomNavItemData
import com.rahad.riobottomnavigation.composables.RioBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isSheetOpen by remember { mutableStateOf(false) }

    val items = listOf(
        Icons.Default.Home,
        Icons.Default.Search,
        Icons.Default.Book,
        Icons.Default.Person
    )

    val labels = listOf("Home", "Search", "My Shelf", "Profile")
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
            BottomNavigationBar(
                buttons = buttons,
                onFabClick = { isSheetOpen = true }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        ScreenContent(selectedIndex.intValue, modifier = Modifier.padding(innerPadding))
    }

    if (isSheetOpen) {
        ModalBottomSheet(sheetState = sheetState, onDismissRequest = { isSheetOpen = false }) {
            BottomSheetContent()
        }
    }
}

@Composable
fun ScreenContent(selectedIndex: Int, modifier: Modifier = Modifier) {
    when (selectedIndex) {
        0 -> HomeContent()
        1 -> SearchScreen()
        2 -> ShowText("My Shelf")
        3 -> ShowText("Profile")
    }
}

@Composable
fun BottomNavigationBar(
    buttons: List<RioBottomNavItemData>,
    onFabClick: () -> Unit
) {
    RioBottomNavigation(
        fabIcon = Icons.Default.Add,
        onFabClick = onFabClick,
        buttons = buttons,
        fabSize = 70.dp,
        barHeight = 70.dp,
        selectedItemColor = PrimaryOrange,
        fabBackgroundColor = PrimaryOrange,
    )
}

@Composable
fun ShowText(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, style = TextStyle(fontSize = 24.sp))
    }
}

@Composable
fun HomeContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Home", style = TextStyle(fontSize = 24.sp))
    }
}

@Composable
fun BottomSheetContent() {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = "Tambahkan buku Anda ke koleksi",
            style = MaterialTheme.typography.titleSmall
        )
        Text(text = "Scan QR Code", style = BodyBottomSheet)
        BottomSheetCard(
            icon = Icons.Default.QrCodeScanner,
            bgColor = PrimaryOrange,
            title = "Scan Barcode ISBN",
            description = "Scan Barcode ISBN Buku Anda untuk pencarian cepat",
            onClick = {}
        )
        BottomSheetCard(
            icon = Icons.Default.Search,
            bgColor = Color(0xFF96ADD6),
            title = "Cari berdasarkan Judul",
            description = "Cari berdasarkan judul jika tidak memiliki ISBN",
            onClick = {}
        )
        BottomSheetCard(
            icon = Icons.Default.AddBox,
            bgColor = Color(0xFFE5A22D),
            title = "Tambahkan buku Anda",
            description = "Tambahkan buku Anda secara manual",
            onClick = {}
        )
    }
}

@Composable
fun BottomSheetCard(
    icon: ImageVector,
    bgColor: Color,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = TitleExtraSmall, color = Color(0xFF1D1B20))
                Text(text = description, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF6F6F6F))
            }
        }
    }
}
