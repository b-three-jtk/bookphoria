package com.example.bookphoria.ui.profile

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.bookphoria.R
import com.example.bookphoria.ui.components.AuthTextField
import com.example.bookphoria.ui.components.PrimaryButton
import com.example.bookphoria.ui.helper.uriToFile
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.theme.SoftCream
import com.example.bookphoria.ui.viewmodel.ProfileViewModel
import java.io.File

@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf("") }
    var avatarFile by remember { mutableStateOf<File?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getProfile(
            username,
            onSuccess = { user ->
                username = user.username ?: ""
                firstName = user.firstName ?: ""
                lastName = user.lastName ?: ""
                email = user.email ?: ""
                avatarUrl = user.profilePicture ?: ""
            },
            onError = {
                Toast.makeText(context, "Gagal mengambil data profil", Toast.LENGTH_SHORT).show()
            }
        )
        Log.d("Edit Profile", "Avatar URL: $avatarUrl")
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = uriToFile(context, it)
            avatarFile = file
            avatarUrl = uri.toString()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
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
                text = "Edit Profil",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(32.dp))
            Image(
                painter = rememberAsyncImagePainter(model = avatarUrl.ifBlank { R.drawable.user }),
                contentDescription = "Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape)
                    .align(Alignment.CenterHorizontally)
                    .clickable { imagePickerLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                label = "Username",
                value = username,
                onValueChange = { username = it },
                leadingIcon = Icons.Default.Person,
                contentDescription = "Username",
                isPassword = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            AuthTextField(
                label = "First Name",
                value = firstName,
                onValueChange = { firstName = it },
                leadingIcon = null,
                contentDescription = "First Name",
                isPassword = false,
            )

            Spacer(modifier = Modifier.height(8.dp))

            AuthTextField(
                label = "Last Name",
                value = lastName,
                onValueChange = { lastName = it },
                leadingIcon = null,
                contentDescription = "Last Name",
                isPassword = false,
            )

            Spacer(modifier = Modifier.height(8.dp))

            AuthTextField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                leadingIcon = Icons.Default.Mail,
                contentDescription = "Email"
            )

            PrimaryButton(
                text = "Simpan",
                backgroundColor = PrimaryOrange,
                onClick = {
                    viewModel.editProfile(
                        username = username,
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        avatar = avatarFile,
                        onSuccess = {
                            Toast.makeText(context, "Profil berhasil diupdate.", Toast.LENGTH_SHORT).show()
                            navController.navigate("profile")
                        },
                        onError = {
                            Toast.makeText(context, "Gagal update profil.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            )
        }
    }
}
