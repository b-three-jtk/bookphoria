package com.example.bookphoria.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val ONBOARDING_PREFS_NAME = "onboarding_prefs"
val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(name = ONBOARDING_PREFS_NAME)

class OnboardingPreferences(private val context: Context) {

    companion object {
        private val IS_ONBOARDING_COMPLETE_KEY = booleanPreferencesKey("is_onboarding_complete")
    }

    // Membaca status onboarding
    val isOnboardingComplete: Flow<Boolean> = context.onboardingDataStore.data
        .map { preferences ->
            preferences[IS_ONBOARDING_COMPLETE_KEY] ?: false
        }

    // Menyimpan status onboarding
    suspend fun setOnboardingComplete(isComplete: Boolean) {
        context.onboardingDataStore.edit { preferences ->
            preferences[IS_ONBOARDING_COMPLETE_KEY] = isComplete
        }
    }
}