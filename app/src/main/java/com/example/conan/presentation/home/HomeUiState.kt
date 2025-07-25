package com.example.conan.presentation.home

import com.example.conan.data.models.Quote
import com.example.conan.data.models.Streak
import com.example.conan.data.models.UserPreferences

data class HomeUiState(
    val isLoading: Boolean = true,
    val dailyQuote: Quote? = null,
    val currentStreak: Streak? = null,
    val longestStreak: Int = 0,
    val userPreferences: UserPreferences = UserPreferences(),
    val error: String? = null,
    val showPanicConfirmation: Boolean = false
)

sealed class HomeUiEvent {
    object LoadData : HomeUiEvent()
    object RefreshQuote : HomeUiEvent()
    object ResetStreak : HomeUiEvent()
    data class NavigateToSettings(val route: String) : HomeUiEvent()
}
