package com.example.bookphoria.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookphoria.ui.book.MyShelfScreen
import com.example.bookphoria.ui.book.SearchScreen
import com.example.bookphoria.ui.components.BottomSheetCard
import com.example.bookphoria.ui.profile.ProfileScreen
import com.example.bookphoria.ui.theme.BodyBottomSheet
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.viewmodel.HomeViewModel
import com.rahad.riobottomnavigation.composables.RioBottomNavItemData
import com.rahad.riobottomnavigation.composables.RioBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, viewModel: HomeViewModel) {
    val childNavController = rememberNavController()
    val selectedIndex = rememberSaveable { mutableIntStateOf(0) }
    val showSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showSheet.value = false },
            sheetState = sheetState,
            containerColor = SoftCream
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftCream)
        ) {
            NavHost(
                navController = childNavController,
                startDestination = "home-tab",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home-tab") {
                    HomeScreen(
                        viewModel = viewModel,
                        navController = navController
                    )
                }
                composable("search-tab") { SearchScreen(navController = navController) }
                composable("shelf-tab") { MyShelfScreen(navController = navController) }
                composable("profile-tab") { ProfileScreen(navController = navController) }
            }
        }
    }
}

data class BottomNavItem(val route: String, val icon: ImageVector, val label: String)

@Composable
fun BottomSheetContent(navController: NavController) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
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
            onClick = {
                navController.navigate("scan")
            }
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
