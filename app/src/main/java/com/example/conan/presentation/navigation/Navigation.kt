package com.example.conan.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object Onboarding : Screen("onboarding")
}

object NavigationActions {
    const val NAVIGATE_TO_SETTINGS = "navigate_to_settings"
    const val NAVIGATE_BACK = "navigate_back"
    const val NAVIGATE_TO_HOME = "navigate_to_home"
}
