package com.example.bookphoria.ui.book

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SearchScreen() {
    Column {
        val results =

        SearchBarCustom()
//        LazyColumn {
//            items()
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarCustom() {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val searchHistory = listOf("Search History 1", "Search History 2", "Search History 3")

    DockedSearchBar(
        query = query,
        onQueryChange = { query = it },
        onSearch = {
            println("do search with query $query")
        },
        active = active,
        onActiveChange = { active = it },
        placeholder = {
            Text(text = "Search")
        },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
        },
        trailingIcon = if (active) {
            {
                IconButton(onClick = { if (query.isNotEmpty()) query = "" else active = false }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Icon")
                }
            }
        } else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            searchHistory.takeLast(3).forEach { item ->
                ListItem(
                    modifier = Modifier.clickable { query = item },
                    headlineContent = { Text(text = item) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History Icon"
                        )
                    }
                )
            }
        }
    }
}
