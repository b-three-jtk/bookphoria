package com.example.bookphoria.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.bookphoria.ui.theme.TitleExtraSmall

@Composable
fun ProfileFriendScreen(
    navController: NavController
) {
    val scrollState = rememberScrollState()

    var profileName by remember { mutableStateOf("Nama Teman") }
    var profileUserName by remember { mutableStateOf("@hasemeleh") }
    var profileBio by remember { mutableStateOf("lorem ipsum sit dolor amet") }
    var bookCount by remember { mutableStateOf("200") }
    var listCount by remember { mutableStateOf("2") }
    var friendCount by remember { mutableStateOf("21") }

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

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(Color.LightGray, CircleShape)
                    .border(1.dp, Color.Gray, CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier
                    .padding(bottom = 15.dp),
                text = profileUserName,
                style = TitleExtraSmall
            )

            Text(
                modifier = Modifier
                    .padding(bottom = 15.dp),
                text = profileBio,
                style = TitleExtraSmall
            )

            Row {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .wrapContentWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE45758)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Friends", color = Color.White, textAlign = TextAlign.Center, style = TitleExtraSmall)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Berteman",
                            tint = Color.White
                        )
                    }
                }
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


            Row(
                modifier = Modifier
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    modifier = Modifier
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(50))
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    text = "Collection",
                    style = TitleExtraSmall,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.width(20.dp))

                Text(
                    modifier = Modifier
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(50))
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    text = "Readlists",
                    style = TitleExtraSmall,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.width(20.dp))


                Text(
                    modifier = Modifier
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(50))
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    text = "Rules",
                    style = TitleExtraSmall,
                    color = Color.Black
                )
            }
        }

    }
}