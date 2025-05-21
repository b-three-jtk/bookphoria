package com.example.bookphoria.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore: DataStore<Preferences> = context.dataStore

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        private val SAVED_EMAIL_KEY = stringPreferencesKey("saved_email")
        private val SAVED_PASSWORD_KEY = stringPreferencesKey("saved_password")
    }

    suspend fun saveLoginData(token: String, userId: Int) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
            preferences[USER_ID_KEY] = userId
            preferences[IS_LOGGED_IN_KEY] = true
        }
    }

    suspend fun saveCredentials(email: String, password: String) {
        dataStore.edit { preferences ->
            preferences[SAVED_EMAIL_KEY] = email
            preferences[SAVED_PASSWORD_KEY] = password
        }
    }

    fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN_KEY] ?: false
        }
    }

    fun getAccessToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }
    }

    fun getUserId(): Flow<Int?> {
        return dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }
    }

    fun getSavedCredentials(): Flow<Pair<String?, String?>> {
        return dataStore.data.map { preferences ->
            Pair(
                preferences[SAVED_EMAIL_KEY],
                preferences[SAVED_PASSWORD_KEY]
            )
        }
    }

    suspend fun clearCredentials() {
        dataStore.edit { preferences ->
            preferences.remove(SAVED_EMAIL_KEY)
            preferences.remove(SAVED_PASSWORD_KEY)
        }
    }

    suspend fun clearLoginData() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(IS_LOGGED_IN_KEY)
            preferences.remove(SAVED_EMAIL_KEY)
            preferences.remove(SAVED_PASSWORD_KEY)
        }
    }
}
