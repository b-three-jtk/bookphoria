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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookphoria.ui.book.SearchScreen
import com.example.bookphoria.ui.theme.BodyBottomSheet
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.theme.TitleExtraSmall
import com.rahad.riobottomnavigation.composables.RioBottomNavItemData
import com.rahad.riobottomnavigation.composables.RioBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val childNavController = rememberNavController()
    val selectedIndex = rememberSaveable { mutableIntStateOf(0) }
    val showSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showSheet.value = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            BottomSheetContent(navController)
        }
    }

    val items = listOf(
        BottomNavItem("home-tab", Icons.Default.Home, "Home"),
        BottomNavItem("search-tab", Icons.Default.Search, "Search"),
        BottomNavItem("shelf-tab", Icons.Default.Book, "My Shelf"),
        BottomNavItem("profile-tab", Icons.Default.Person, "Profile")
    )

    Scaffold(
        bottomBar = {
            RioBottomNavigation(
                fabIcon = Icons.Default.Add,
                onFabClick = { showSheet.value = true },
                buttons = items.mapIndexed { index, item ->
                    RioBottomNavItemData(
                        imageVector = item.icon,
                        selected = selectedIndex.intValue == index,
                        onClick = {
                            selectedIndex.intValue = index
                            childNavController.navigate(item.route) {
                                popUpTo(childNavController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = item.label
                    )
                },
                fabSize = 70.dp,
                barHeight = 70.dp,
                selectedItemColor = PrimaryOrange,
                fabBackgroundColor = PrimaryOrange,
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = childNavController,
            startDestination = "home-tab",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home-tab") { HomeContent() }
            composable("search-tab") { SearchScreen() }
            composable("shelf-tab") { ShowText("My Shelf") }
            composable("profile-tab") { ShowText("Profile") }
        }
    }
}

data class BottomNavItem(val route: String, val icon: ImageVector, val label: String)

@Composable
fun ShowText(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, style = TextStyle(fontSize = 24.sp))
    }
}

@Composable
fun HomeContent() {

}

@Composable
fun BottomSheetContent(navController: NavController) {
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
            onClick = {
                navController.navigate("add-new-book")
            }
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
