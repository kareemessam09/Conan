package com.example.conan.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conan.domain.usecases.GetDailyQuoteUseCase
import com.example.conan.domain.usecases.ManageStreakUseCase
import com.example.conan.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.time.LocalDate
import java.time.chrono.ChronoLocalDateTime

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDailyQuoteUseCase: GetDailyQuoteUseCase,
    private val manageStreakUseCase: ManageStreakUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.LoadData -> loadData()
            is HomeUiEvent.RefreshQuote -> refreshQuote()
            is HomeUiEvent.ResetStreak -> resetStreak()
            is HomeUiEvent.NavigateToSettings -> {
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val preferences = userPreferencesRepository.userPreferences.first()
                val currentStreak = manageStreakUseCase.getCurrentStreak().first()
                val longestStreak = manageStreakUseCase.getLongestStreak()
                val quote = getDailyQuoteUseCase()

                _uiState.value = _uiState.value.copy(
                    currentStreak = currentStreak,
                    userPreferences = preferences,
                    longestStreak = longestStreak,
                    dailyQuote = quote,
                    isLoading = false,
                    error = null
                )

                incrementStreakIfNeeded()

                // Continue observing changes in the background
                observeDataChanges()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Unknown error occurred",
                    isLoading = false
                )
            }
        }
    }

    private fun observeDataChanges() {
        viewModelScope.launch {
            combine(
                manageStreakUseCase.getCurrentStreak(),
                userPreferencesRepository.userPreferences
            ) { streak, preferences ->
                _uiState.value = _uiState.value.copy(
                    currentStreak = streak,
                    userPreferences = preferences
                )
            }.collect()
        }
    }

    private fun refreshQuote() {
        viewModelScope.launch {
            try {
                val quote = getDailyQuoteUseCase()
                _uiState.value = _uiState.value.copy(dailyQuote = quote)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }


    suspend fun incrementStreakIfNeeded(): Int {
        val lastIncrementDate = manageStreakUseCase.getCurrentStreak().first()?.endDate
        val today = LocalDate.now()

        return if (lastIncrementDate == null || lastIncrementDate.isBefore(today as ChronoLocalDateTime<*>?)) {
            val newStreak = manageStreakUseCase.incrementStreak()
            newStreak?.dayCount ?: 1

        } else {
            manageStreakUseCase.getCurrentStreak().first()?.dayCount ?: 1
        }
    }

     fun resetStreak() {
        viewModelScope.launch {
            manageStreakUseCase.resetStreak()
            _uiState.value = _uiState.value.copy(currentStreak = null)
        }
    }






}
