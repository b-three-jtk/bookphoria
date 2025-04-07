package com.example.bookphoria.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookphoria.R
import com.example.bookphoria.ui.book.MyShelfScreen
import com.example.bookphoria.ui.book.SearchScreen
import com.example.bookphoria.ui.components.BottomSheetCard
import com.example.bookphoria.ui.profile.ProfileScreen
import com.example.bookphoria.ui.theme.BodyBottomSheet
import com.example.bookphoria.ui.theme.PrimaryOrange
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
            composable("shelf-tab") { MyShelfScreen() }
            composable("profile-tab") { ProfileScreen() }
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
fun HomeContent(userName: String = "Asep") {
    Box(modifier = Modifier.fillMaxSize()) {
        // Header Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFE4B5),
                            Color(0xFFFCD8C4)
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Morning Icon",
                        tint = Color.Black
                    )
                    Image(
                        painter = painterResource(id = R.drawable.forgot),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Halo, $userName",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )

                Text(
                    text = "Baca buku apa hari ini?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        SearchBarHome()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 240.dp)
        ) {
            BookSection()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarHome() {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val searchHistory = listOf("Buku A", "Buku B", "Buku C")

    DockedSearchBar(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .offset(y = 170.dp),
        query = query,
        onQueryChange = { query = it },
        onSearch = {
            println("do search with query $query")
            active = false
        },
        active = active,
        onActiveChange = { active = it },
        placeholder = {
            Text(text = "Cari buku...")
        },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
        },
        trailingIcon = {
            if (active) {
                IconButton(
                    onClick = {
                        if (query.isNotEmpty()) query = "" else active = false
                    }
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Icon")
                }
            }
        },
        tonalElevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            searchHistory.takeLast(3).forEach { item ->
                ListItem(
                    modifier = Modifier.clickable {
                        query = item
                        active = false
                    },
                    headlineContent = {
                        Text(text = item)
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun GradientBackgroundBrush(
    isVerticalGradient: Boolean,
    colors: List<Color>
): Brush {
    val endOffset = if (isVerticalGradient) {
        Offset(0f, Float.POSITIVE_INFINITY)
    } else {
        Offset(Float.POSITIVE_INFINITY, 0f)
    }

    return Brush.linearGradient(
        colors = colors,
        start = Offset.Zero,
        end = endOffset
    )
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
fun BookSection() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Your Books",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Lainnya",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            BookItem("The Little Prince", "Antoine de Saint-Exupéry", R.drawable.bookshelf)
            BookItem("Tentang Kamu", "Tere Liye", R.drawable.bookshelf)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Currently Reading",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(12.dp))

        CurrentReadingItem(
            title = "The Little Prince",
            author = "Antoine de Saint-Exupéry",
            progress = 0.5f,
            imageRes = R.drawable.bookshelf
        )
    }
}

@Composable
fun BookItem(title: String, author: String, imageRes: Int) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFEAE9F3))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .height(140.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, fontWeight = FontWeight.Bold)
        Text(text = author, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
fun CurrentReadingItem(title: String, author: String, progress: Float, imageRes: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(text = title, fontWeight = FontWeight.Bold)
            Text(text = author, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {},
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD3F1DE)),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(text = "Reading", color = Color(0xFF228B22))
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = progress,
                color = Color(0xFF4F46E5),
                trackColor = Color(0xFFEAEAEA),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
            )
            Text(text = "${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
        }
    }
}
