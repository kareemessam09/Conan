package com.example.conan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.conan.presentation.home.HomeScreen
import com.example.conan.presentation.settings.SettingsScreen
import com.example.conan.presentation.popup.TriggerAlertDialog
import com.example.conan.presentation.popup.TriggerPopupViewModel
import com.example.conan.presentation.popup.TriggerPopupEvent
import com.example.conan.presentation.navigation.Screen
import com.example.conan.ui.theme.PurityPathTheme
import com.example.conan.utils.PermissionChecker
import com.example.conan.utils.PermissionHelper
import com.example.conan.utils.DatabaseInitializer
import com.example.conan.utils.LocaleManager
import com.example.conan.domain.repository.UserPreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.example.conan.utils.NotificationHelper

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Add database initializer here instead of in Application
    @Inject
    lateinit var databaseInitializer: DatabaseInitializer

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { context ->
            // Apply saved language preference when activity is created
            LocaleManager.setLocale(context, getStoredLanguage(context))
        } ?: newBase)
    }

    private fun getStoredLanguage(context: Context): com.example.conan.data.models.Language {
        // Get the stored language preference synchronously
        // This is a simplified version - in production you might want to use a more sophisticated approach
        val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val languageCode = sharedPrefs.getString("language_code", "en") ?: "en"
        return com.example.conan.data.models.Language.values().find { it.code == languageCode }
            ?: com.example.conan.data.models.Language.ENGLISH
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initializeAppData()

        lifecycleScope.launch {
            userPreferencesRepository.userPreferences.collect { preferences ->
                val currentLocale = LocaleManager.getCurrentLocale(this@MainActivity)
                if (currentLocale.language != preferences.selectedLanguage.code) {
                    saveLanguagePreference(preferences.selectedLanguage)
                    LocaleManager.setLocale(this@MainActivity, preferences.selectedLanguage)
                    recreate()
                }
            }
        }

        setContent {
            PurityPathTheme {
                val navController = rememberNavController()
                val triggerPopupViewModel: TriggerPopupViewModel = hiltViewModel()
                var showPermissionPrompt by remember { mutableStateOf(false) }
                var missingPermissions by remember { mutableStateOf<List<String>>(emptyList()) }

                // Check permissions on app start
                LaunchedEffect(Unit) {
                    val missing = PermissionChecker.getMissingPermissions(this@MainActivity)
                    if (missing.isNotEmpty()) {
                        missingPermissions = missing
                        showPermissionPrompt = true
                    }
                }


                // Handle trigger popup intent
                LaunchedEffect(intent) {
                    handleTriggerIntent(intent, triggerPopupViewModel)
                }

                // Show permission prompt if needed
                if (showPermissionPrompt) {
                    MainActivityPermissionDialog(
                        missingPermissions = missingPermissions,
                        onOpenSettings = { permissionType: String ->
                            when (permissionType) {
                                "Accessibility Service" -> PermissionHelper.openAccessibilitySettings(this@MainActivity)
                                "Display Over Other Apps" -> PermissionHelper.openOverlaySettings(this@MainActivity)
                                "Usage Statistics" -> PermissionHelper.openUsageStatsSettings(this@MainActivity)
                                "Notifications" -> PermissionHelper.openNotificationSettings(this@MainActivity)
                            }
                        },
                        onDismiss = { showPermissionPrompt = false },
                        onLater = { showPermissionPrompt = false }
                    )
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                onNavigateToSettings = {
                                    navController.navigate(Screen.Settings.route)
                                }
                            )
                        }

                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }

                    TriggerAlertDialog(
                        viewModel = triggerPopupViewModel
                    )
                }
            }
        }
    }

    private fun saveLanguagePreference(language: com.example.conan.data.models.Language) {
        val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("language_code", language.code).apply()
    }

    private fun initializeAppData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                databaseInitializer.initializeDatabase()
            } catch (e: Exception) {
            }
        }
    }

    private fun handleTriggerIntent(intent: Intent, viewModel: TriggerPopupViewModel) {
        if (intent.action == "SHOW_TRIGGER_POPUP") {
            viewModel.onEvent(TriggerPopupEvent.ShowPopup("MainActivity"))
        }
    }
}

@Composable
fun MainActivityPermissionDialog(
    missingPermissions: List<String>,
    onOpenSettings: (String) -> Unit,
    onDismiss: () -> Unit,
    onLater: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.permission_required),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Purity Path needs the following permissions to work properly:",
                    style = MaterialTheme.typography.bodyMedium
                )

                missingPermissions.forEach { permission ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "• $permission",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = { onOpenSettings(permission) }
                        ) {
                            Text(stringResource(R.string.enable))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onLater) {
                Text("Later")
            }
        }
    )
}
