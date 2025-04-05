package com.example.bookphoria.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookphoria.data.local.preferences.OnboardingPreferences
import com.example.bookphoria.data.local.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val onboardingPreferences = OnboardingPreferences(context)

    // Membaca status onboarding
    val isOnboardingComplete: Flow<Boolean> = onboardingPreferences.isOnboardingComplete

    // onboarding selesai
    fun completeOnboarding() {
        viewModelScope.launch {
            onboardingPreferences.setOnboardingComplete(true)
        }
    }
}