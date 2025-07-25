package com.example.conan.presentation.settings

import com.example.conan.data.models.BlockingStrength
import com.example.conan.data.models.QuoteCategory
import com.example.conan.data.models.UserPreferences
import com.example.conan.data.models.Language

data class SettingsUiState(
    val userPreferences: UserPreferences = UserPreferences(),
    val isLoading: Boolean = false,
    val showAddCustomMessageDialog: Boolean = false,
    val newCustomMessage: String = "",
    val showPermissionDialog: Boolean = false,
    val permissionType: PermissionType? = null,
    val showLanguageDialog: Boolean = false
)

enum class PermissionType {
    ACCESSIBILITY,
    OVERLAY,
    USAGE_STATS,
    NOTIFICATIONS
}

sealed class SettingsEvent {
    object LoadSettings : SettingsEvent()
    data class UpdateDetectionEnabled(val enabled: Boolean) : SettingsEvent()
    data class UpdateNotificationsEnabled(val enabled: Boolean) : SettingsEvent()
    data class UpdatePanicButtonEnabled(val enabled: Boolean) : SettingsEvent()
    data class UpdateBlockingStrength(val strength: BlockingStrength) : SettingsEvent()
    data class UpdateQuoteCategories(val categories: Set<QuoteCategory>) : SettingsEvent()
    data class UpdateReminderTime(val time: String) : SettingsEvent()
    object ShowAddCustomMessageDialog : SettingsEvent()
    object HideAddCustomMessageDialog : SettingsEvent()
    data class UpdateNewCustomMessage(val message: String) : SettingsEvent()
    object AddCustomMessage : SettingsEvent()
    data class RemoveCustomMessage(val index: Int) : SettingsEvent()
    data class RequestPermission(val type: PermissionType) : SettingsEvent()
    object DismissPermissionDialog : SettingsEvent()
    object ShowLanguageDialog : SettingsEvent()
    object HideLanguageDialog : SettingsEvent()
    data class UpdateLanguage(val language: Language) : SettingsEvent()
}
