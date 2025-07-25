package com.example.conan.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conan.domain.repository.UserPreferencesRepository
import com.example.conan.data.models.BlockingStrength
import com.example.conan.data.models.QuoteCategory
import com.example.conan.data.models.Language
import com.example.conan.service.DailyReminderWorker
import com.example.conan.utils.LocaleManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.LoadSettings -> loadSettings()
            is SettingsEvent.UpdateDetectionEnabled -> updateDetectionEnabled(event.enabled)
            is SettingsEvent.UpdateNotificationsEnabled -> updateNotificationsEnabled(event.enabled)
            is SettingsEvent.UpdatePanicButtonEnabled -> updatePanicButtonEnabled(event.enabled)
            is SettingsEvent.UpdateBlockingStrength -> updateBlockingStrength(event.strength)
            is SettingsEvent.UpdateQuoteCategories -> updateQuoteCategories(event.categories)
            is SettingsEvent.UpdateReminderTime -> updateReminderTime(event.time)
            is SettingsEvent.ShowAddCustomMessageDialog -> showAddCustomMessageDialog()
            is SettingsEvent.HideAddCustomMessageDialog -> hideAddCustomMessageDialog()
            is SettingsEvent.UpdateNewCustomMessage -> updateNewCustomMessage(event.message)
            is SettingsEvent.AddCustomMessage -> addCustomMessage()
            is SettingsEvent.RemoveCustomMessage -> removeCustomMessage(event.index)
            is SettingsEvent.RequestPermission -> requestPermission(event.type)
            is SettingsEvent.DismissPermissionDialog -> dismissPermissionDialog()
            is SettingsEvent.ShowLanguageDialog -> showLanguageDialog()
            is SettingsEvent.HideLanguageDialog -> hideLanguageDialog()
            is SettingsEvent.UpdateLanguage -> updateLanguage(event.language)
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            userPreferencesRepository.userPreferences
                .collect { preferences ->
                    _uiState.value = _uiState.value.copy(
                        userPreferences = preferences,
                        isLoading = false
                    )
                }
        }
    }

    private fun updateDetectionEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateDetectionEnabled(enabled)
        }
    }

    private fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateNotificationsEnabled(enabled)

            if (enabled) {
                // Schedule daily reminders when notifications are enabled
                val currentTime = _uiState.value.userPreferences.dailyReminderTime
                DailyReminderWorker.scheduleDaily(context, currentTime)
                android.util.Log.d("SettingsViewModel", "Daily reminders scheduled for $currentTime")
            } else {
                // Cancel daily reminders when notifications are disabled
                DailyReminderWorker.cancelDaily(context)
                android.util.Log.d("SettingsViewModel", "Daily reminders cancelled")
            }
        }
    }

    private fun updatePanicButtonEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updatePanicButtonEnabled(enabled)
        }
    }

    private fun updateBlockingStrength(strength: BlockingStrength) {
        viewModelScope.launch {
            userPreferencesRepository.updateBlockingStrength(strength)
        }
    }

    private fun updateQuoteCategories(categories: Set<QuoteCategory>) {
        viewModelScope.launch {
            userPreferencesRepository.updateSelectedQuoteCategories(categories)
        }
    }

    private fun updateReminderTime(time: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateDailyReminderTime(time)

            // Reschedule with new time if notifications are enabled
            val notificationsEnabled = _uiState.value.userPreferences.isNotificationsEnabled
            if (notificationsEnabled) {
                DailyReminderWorker.cancelDaily(context) // Cancel old schedule
                DailyReminderWorker.scheduleDaily(context, time) // Schedule with new time
                android.util.Log.d("SettingsViewModel", "Daily reminders rescheduled for $time")
            }
        }
    }

    private fun showAddCustomMessageDialog() {
        _uiState.value = _uiState.value.copy(showAddCustomMessageDialog = true)
    }

    private fun hideAddCustomMessageDialog() {
        _uiState.value = _uiState.value.copy(
            showAddCustomMessageDialog = false,
            newCustomMessage = ""
        )
    }

    private fun updateNewCustomMessage(message: String) {
        _uiState.value = _uiState.value.copy(newCustomMessage = message)
    }

    private fun addCustomMessage() {
        val currentMessage = _uiState.value.newCustomMessage.trim()
        if (currentMessage.isNotEmpty()) {
            viewModelScope.launch {
                val currentMessages = _uiState.value.userPreferences.customMessages.toMutableList()
                currentMessages.add(currentMessage)
                userPreferencesRepository.updateCustomMessages(currentMessages)
                hideAddCustomMessageDialog()
            }
        }
    }

    private fun removeCustomMessage(index: Int) {
        viewModelScope.launch {
            val currentMessages = _uiState.value.userPreferences.customMessages.toMutableList()
            if (index in currentMessages.indices) {
                currentMessages.removeAt(index)
                userPreferencesRepository.updateCustomMessages(currentMessages)
            }
        }
    }

    private fun requestPermission(type: PermissionType) {
        _uiState.value = _uiState.value.copy(
            showPermissionDialog = true,
            permissionType = type
        )
    }

    private fun dismissPermissionDialog() {
        _uiState.value = _uiState.value.copy(
            showPermissionDialog = false,
            permissionType = null
        )
    }

    private fun showLanguageDialog() {
        _uiState.value = _uiState.value.copy(showLanguageDialog = true)
    }

    private fun hideLanguageDialog() {
        _uiState.value = _uiState.value.copy(showLanguageDialog = false)
    }

    private fun updateLanguage(language: Language) {
        viewModelScope.launch {
            userPreferencesRepository.updateSelectedLanguage(language)
            LocaleManager.setLocale(context, language)
            hideLanguageDialog()
        }
    }
}
