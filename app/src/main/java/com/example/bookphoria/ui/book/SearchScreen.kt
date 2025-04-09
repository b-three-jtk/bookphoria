package com.example.bookphoria.ui.book

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.bookphoria.ui.viewmodel.SearchViewModel

@Composable
fun SearchScreen(viewModel: SearchViewModel = hiltViewModel(), navController: NavController) {
    val query by viewModel.searchQuery.collectAsState()
    val results = viewModel.searchResults.collectAsLazyPagingItems()
    // Get the current NavBackStackEntry
    val navBackStackEntry = navController.currentBackStackEntry

    // Check if there's a scan result to use as search query
    val scannedQuery = navBackStackEntry?.savedStateHandle?.get<String>("search_query")

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

    // When a scanned query is available, update the search
    LaunchedEffect(scannedQuery) {
        scannedQuery?.let { scanResult ->
            // Clear the saved state to avoid reusing it if user navigates away and comes back
            navBackStackEntry.savedStateHandle.remove<String>("search_query")

            // Update the search query with the scan result
            viewModel.setSearchQuery(scanResult)
            // No need to explicitly trigger search since your ViewModel already does that
            // when the query changes through the reactive flow
        }
    }

    Column {
        SearchBarCustom(
            query = query,
            onQueryChange = { viewModel.setSearchQuery(it) },
            onSearch = {}
        )

        LazyColumn {
            if (query.isNotEmpty()) {
                items(results.itemCount) { index ->
                    val book = results[index]
                    if (book != null) {
                        ListItem(
                            headlineContent = { Text(book.title) },
                            supportingContent = {
                                Text(book.authors.joinToString(", ") { it.name })
                            }
                        )
                    }
                }

                results.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item { Text("Loading...") }
                        }
                        loadState.append is LoadState.Loading -> {
                            item { Text("Loading more...") }
                        }
                        loadState.refresh is LoadState.Error -> {
                            val e = loadState.refresh as LoadState.Error
                            item { Text("Error: ${e.error.message}") }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarCustom(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    var active by remember { mutableStateOf(false) }
    val searchHistory = listOf("Search History 1", "Search History 2", "Search History 3")


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
    ) {
        DockedSearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = { onSearch() },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text(text = "Search") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
            trailingIcon = if (active) {
                {
                    IconButton(onClick = {
                        if (query.isNotEmpty()) onQueryChange("") else active = false
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Close Icon")
                    }
                }
            } else null
        ) {
            searchHistory.takeLast(3).forEach { item ->
                ListItem(
                    modifier = Modifier.clickable {
                        onQueryChange(item)
                        onSearch()
                    },
                    headlineContent = { Text(text = item) },
                    leadingContent = {
                        Icon(Icons.Default.History, contentDescription = "History Icon")
                    }
                )
            }
        }
    }
}
