package com.example.bookphoria.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.searchDataStore: DataStore<Preferences> by preferencesDataStore(name = "search_prefs")

@Singleton
class SearchPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore: DataStore<Preferences> = context.searchDataStore

    companion object {
        private val SEARCH_HISTORY_KEY = stringSetPreferencesKey("search_history")
        private const val MAX_HISTORY_ITEMS = 10
    }

    val searchHistory: Flow<List<String>> = dataStore.data
        .map { preferences ->
            preferences[SEARCH_HISTORY_KEY]?.toList()?.reversed() ?: emptyList()
        }

    suspend fun addSearchQuery(query: String) {
        dataStore.edit { preferences ->
            val currentHistory = preferences[SEARCH_HISTORY_KEY]?.toMutableSet() ?: mutableSetOf()
            currentHistory.remove(query)
            currentHistory.add(query)

            // Trim to max items
            if (currentHistory.size > MAX_HISTORY_ITEMS) {
                val oldestItem = currentHistory.minByOrNull { it }
                oldestItem?.let { currentHistory.remove(it) }
            }

            preferences[SEARCH_HISTORY_KEY] = currentHistory
        }
    }

    suspend fun clearSearchHistory() {
        dataStore.edit { preferences ->
            preferences.remove(SEARCH_HISTORY_KEY)
        }
    }
}