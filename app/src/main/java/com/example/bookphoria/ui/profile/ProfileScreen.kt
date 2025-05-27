package com.example.bookphoria.ui.profile

import android.util.Log
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookphoria.ui.theme.PrimaryOrange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.constraintlayout.motion.widget.MotionScene.Transition.TransitionOnClick
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.bookphoria.R
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userData by viewModel.userData.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val bookCount by viewModel.bookCount.collectAsState()
    val readingListCount by viewModel.readingListCount.collectAsState()
    val friendCount by viewModel.friendCount.collectAsState()

    // Handle error with a snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(error) {
        error?.let { errorMessage ->
            scope.launch {
                snackbarHostState.showSnackbar(errorMessage)
                viewModel.clearError()
            }
        }
    }

    LaunchedEffect(Unit) {
        Log.d("ProfileScreen", "Rendering: userData=$userData, bookCount=$bookCount, readingListCount=$readingListCount, friendCount=$friendCount, loading=$loading, error=$error")
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(SoftCream)
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Edit Profile Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { navController.navigate("edit-profile") }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Profile Picture
                    Image(
                        painter = painterResource(R.drawable.user),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Username
                    Text(
                        text = userData?.username ?: "Guest",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatColumn(
                            value = bookCount.toString(),
                            label = "Total Buku",
                            onClick = { navController.navigate("book-list") }
                        )
                        StatColumn(
                            value = readingListCount.toString(),
                            label = "List Bacaan",
                            onClick = { navController.navigate("shelves") }
                        )
                        StatColumn(
                            value = friendCount.toString(),
                            label = "Teman",
                            onClick = { navController.navigate("friend-list") }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Profile Info Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            ProfileInfoRow("Username", userData?.username ?: "-")
                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            // Nama lengkap sengaja dikosongkan
                            ProfileInfoRow("Nama Lengkap", "-")
                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            ProfileInfoRow("Email", userData?.email ?: "-")
                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            Spacer(modifier = Modifier.height(16.dp))

                            // Change Password Button
                            Button(
                                onClick = { navController.navigate("change-password") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(Color.Gray),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = "Password Icon",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Change Password", color = Color.White)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Logout Button
                            Button(
                                onClick = { viewModel.logout { navController.navigate("login") } },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(PrimaryOrange)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Logout Icon",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Logout", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatColumn(value: String, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.Gray
            )
        )
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.Gray
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}