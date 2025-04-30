package com.example.bookphoria.ui.book

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.bookphoria.R
import com.example.bookphoria.ui.viewmodel.SearchViewModel

@Composable
fun SearchScreen(viewModel: SearchViewModel = hiltViewModel(), navController: NavController) {
    val query by viewModel.searchQuery.collectAsState()
    val results = viewModel.searchResults.collectAsLazyPagingItems()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val navBackStackEntry = navController.currentBackStackEntry

    val scannedQuery = navBackStackEntry?.savedStateHandle?.get<String>("search_query")

    LaunchedEffect(scannedQuery) {
        scannedQuery?.let { scanResult ->
            navBackStackEntry.savedStateHandle.remove<String>("search_query")
            viewModel.setSearchQuery(scanResult)
            viewModel.addToHistory(scanResult)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            SearchBarCustom(
                query = query,
                onQueryChange = { viewModel.setSearchQuery(it) },
                onSearch = { if (query.isNotBlank()) viewModel.addToHistory(query) },
                searchHistory = searchHistory,
                onClearHistory = { viewModel.clearHistory() }
            )

            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (query.isNotEmpty()) {
                    items(results.itemCount) { index ->
                        val book = results[index]
                        if (book != null) {
                            BookSearchItem(
                                title = book.title,
                                author = book.authors?.joinToString(", ") { it.name } ?: "No Author",
                                imageUrl = book.cover,
                            )
                        }
                    }

                    results.apply {
                        when {
                            loadState.refresh is LoadState.Loading -> {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }

                            loadState.append is LoadState.Loading -> {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }

                            loadState.refresh is LoadState.Error -> {
                                val e = loadState.refresh as LoadState.Error
                                item {
                                    Text(
                                        "Error: ${e.error.message}",
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Silakan ketik untuk mencari buku")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookSearchItem(
    title: String,
    author: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = imageUrl ?: R.drawable.bookshelf,
            contentDescription = title,
            modifier = Modifier
                .height(160.dp)
                .width(120.dp)
                .clip(MaterialTheme.shapes.small),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = author,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarCustom(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    searchHistory: List<String>,
    onClearHistory: () -> Unit
) {
    var active by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
    ) {
        DockedSearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = {
                onSearch()
                active = false
            },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text("Search") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = if (active) {
                {
                    IconButton(onClick = {
                        if (query.isNotEmpty()) onQueryChange("") else active = false
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            } else null
        ) {
            if (searchHistory.isEmpty()) {
                Text(
                    "No search history",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Recent searches",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = onClearHistory) {
                            Text("Clear all")
                        }
                    }

                    searchHistory.take(5).forEach { item ->
                        ListItem(
                            modifier = Modifier.clickable {
                                onQueryChange(item)
                                onSearch()
                                active = false
                            },
                            headlineContent = { Text(item) },
                            leadingContent = {
                                Icon(Icons.Default.History, contentDescription = "History")
                            }
                        )
                    }
                }
            }
        }
    }
}