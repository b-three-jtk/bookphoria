package com.example.bookphoria.ui.book

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.bookphoria.R
import com.example.bookphoria.ui.theme.*

@Composable
fun MyShelfScreen(
    onCreateCollectionClick: () -> Unit = {}
) {
    val dummyCollections = listOf(
        "Books to make you smile",
        "Current favs",
        "In the feels",
        "Crying in the prettiest places",
        "<3 <3 <3"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftCream)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Shelf",
                style = AppTypography.titleSmall
            )
            OutlinedButton(
                onClick = onCreateCollectionClick,
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, color = Color.Gray),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = LocalContentColor.current
                )
            ) {
                Text("Buat koleksi baru",
                    style = AppTypography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(dummyCollections) { title ->
                ShelfItem(title = title)
            }
        }
    }
}

@Composable
fun ShelfItem(title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.sample_koleksi),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    title,
                    style = AppTypography.bodyLarge
                )
                Text(
                    "3 Books",
                    style = AppTypography.bodyMedium.copy(color = Color.Gray)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyShelfScreenPreview() {
    MyShelfScreen()
}
