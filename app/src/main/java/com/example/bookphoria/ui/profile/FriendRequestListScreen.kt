package com.example.bookphoria.ui.profile

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.bookphoria.ui.components.ConfirmationDialog
import com.example.bookphoria.ui.theme.SubTitleExtraSmall
import com.example.bookphoria.ui.theme.TitleExtraSmall
import com.example.bookphoria.ui.viewmodel.FriendViewModel

@Composable
fun FriendRequestListContent(
    viewModel: FriendViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    var showApproveDialog by remember { mutableStateOf<Int?>(null) }
    var showRejectDialog by remember { mutableStateOf<Int?>(null) }
    val isLoading by viewModel.isLoading.collectAsState()

    val requests by viewModel.friendRequest

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.splashbuku)
            )
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = LottieConstants.IterateForever
            )

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(200.dp)
            )
        }
        return
    }

    if (requests.isEmpty()) {
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
                    text = "Tidak ada permintaan pertemanan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
        return
    }

    Column {
        requests.forEach { request ->
            val fullName = listOfNotNull(request.user.firstName, request.user.lastName).joinToString(" ")
            val username = request.user.username ?: ""

            if (showApproveDialog == request.user.id) {
                ConfirmationDialog(
                    title = "Konfirmasi Persetujuan",
                    message = "Anda yakin ingin menerima permintaan pertemanan dari ${request.user.username}?",
                    onConfirm = {
                        viewModel.approveRequest(
                            request.user.id,
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "Permintaan pertemanan diterima",
                                    Toast.LENGTH_SHORT
                                ).show()
                                showApproveDialog = null
                            },
                            onError = { error ->
                                Toast.makeText(
                                    context,
                                    "Gagal: ${error ?: "Terjadi kesalahan."}",
                                    Toast.LENGTH_LONG
                                ).show()
                                showApproveDialog = null
                            }
                        )
                    },
                    onDismiss = { showApproveDialog = null }
                )
            }

            if (showRejectDialog == request.user.id) {
                ConfirmationDialog(
                    title = "Konfirmasi Penolakan",
                    message = "Anda yakin ingin menolak permintaan pertemanan dari ${request.user.username}?",
                    onConfirm = {
                        viewModel.rejectRequest(
                            request.user.id,
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "Permintaan pertemanan ditolak",
                                    Toast.LENGTH_SHORT
                                ).show()
                                showRejectDialog = null
                            },
                            onError = { error ->
                                Toast.makeText(
                                    context,
                                    "Gagal: ${error ?: "Terjadi kesalahan."}",
                                    Toast.LENGTH_LONG
                                ).show()
                                showRejectDialog = null
                            }
                        )
                    },
                    onDismiss = { showRejectDialog = null }
                )
            }

            FriendRequestListItem(
                name = fullName,
                username = "@$username",
                onClick = { navController.navigate("user-profile/${request.user.id}") },
                onApprove = { showApproveDialog = request.user.id },
                onReject = { showRejectDialog = request.user.id }
            )
        }
    }
}

@Composable
fun FriendRequestListItem(
    name: String = "Nama Dia",
    username: String = "@username",
    onClick: () -> Unit = {},
    onApprove: () -> Unit = {},
    onReject: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(18.dp),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Row(
            modifier = Modifier
                .background(color = Color.White)
                .padding(8.dp)
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
                    .clickable(onClick = onClick)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                if (name.isNotEmpty()) {
                    Text(
                        text = name,
                        style = TitleExtraSmall
                    )
                }
                Text(
                    text = username,
                    style = SubTitleExtraSmall,
                    color = Color.Gray
                )
            }

            Row {
                OutlinedButton(
                    onClick = onApprove,
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color.LightGray),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Green)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Approve",
                        tint = Color.Green
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color.LightGray),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Reject",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}