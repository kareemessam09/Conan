package com.example.conan.presentation.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conan.R
import com.example.conan.data.models.BlockingStrength
import com.example.conan.data.models.QuoteCategory
import com.example.conan.data.models.Language
import com.example.conan.utils.PermissionHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(SettingsEvent.LoadSettings)
    }

    // Handle dialogs
    if (uiState.showAddCustomMessageDialog) {
        AddCustomMessageDialog(
            currentMessage = uiState.newCustomMessage,
            onMessageChange = { viewModel.onEvent(SettingsEvent.UpdateNewCustomMessage(it)) },
            onConfirm = { viewModel.onEvent(SettingsEvent.AddCustomMessage) },
            onDismiss = { viewModel.onEvent(SettingsEvent.HideAddCustomMessageDialog) }
        )
    }

    if (uiState.showPermissionDialog) {
        PermissionDialog(
            permissionType = uiState.permissionType,
            onDismiss = { viewModel.onEvent(SettingsEvent.DismissPermissionDialog) }
        )
    }

    if (uiState.showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = uiState.userPreferences.selectedLanguage,
            onLanguageSelected = { viewModel.onEvent(SettingsEvent.UpdateLanguage(it)) },
            onDismiss = { viewModel.onEvent(SettingsEvent.HideLanguageDialog) }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1117),
                        Color(0xFF161B22),
                        Color(0xFF0D1117)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Enhanced Top App Bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A202C).copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF4FD1C7).copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4FD1C7)
                    )
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF4FD1C7),
                        strokeWidth = 3.dp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Detection Settings
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(300, delayMillis = 100)
                            ) + fadeIn(animationSpec = tween(300, delayMillis = 100))
                        ) {
                            SettingsSection(
                                title = stringResource(R.string.detection_settings),
                                icon = Icons.Default.Lock
                            ) {
                                BlockingStrengthPreference(
                                    currentStrength = uiState.userPreferences.blockingStrength,
                                    onStrengthChange = {
                                        viewModel.onEvent(SettingsEvent.UpdateBlockingStrength(it))
                                    }
                                )
                            }
                        }
                    }

                    // Language Settings
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(300, delayMillis = 150)
                            ) + fadeIn(animationSpec = tween(300, delayMillis = 150))
                        ) {
                            SettingsSection(
                                title = stringResource(R.string.language_settings),
                                icon = Icons.Default.Build
                            ) {
                                LanguagePreference(
                                    currentLanguage = uiState.userPreferences.selectedLanguage,
                                    onClick = { viewModel.onEvent(SettingsEvent.ShowLanguageDialog) }
                                )
                            }
                        }
                    }

                    // Notifications
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(300, delayMillis = 200)
                            ) + fadeIn(animationSpec = tween(300, delayMillis = 200))
                        ) {
                            SettingsSection(
                                title = stringResource(R.string.notifications),
                                icon = Icons.Default.Notifications
                            ) {
                                SwitchPreference(
                                    title = stringResource(R.string.daily_reminders),
                                    subtitle = stringResource(R.string.daily_reminders_subtitle),
                                    checked = uiState.userPreferences.isNotificationsEnabled,
                                    onCheckedChange = {
                                        viewModel.onEvent(SettingsEvent.UpdateNotificationsEnabled(it))
                                    }
                                )

                                TimePickerPreference(
                                    title = stringResource(R.string.reminder_time),
                                    currentTime = uiState.userPreferences.dailyReminderTime,
                                    onTimeChange = {
                                        viewModel.onEvent(SettingsEvent.UpdateReminderTime(it))
                                    }
                                )
                            }
                        }
                    }

                    // Quote Categories
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(300, delayMillis = 250)
                            ) + fadeIn(animationSpec = tween(300, delayMillis = 250))
                        ) {
                            SettingsSection(
                                title = stringResource(R.string.inspirational_content),
                                icon = Icons.Default.Info
                            ) {
                                QuoteCategoryPreferences(
                                    selectedCategories = uiState.userPreferences.selectedQuoteCategories,
                                    onCategoriesChange = {
                                        viewModel.onEvent(SettingsEvent.UpdateQuoteCategories(it))
                                    }
                                )
                            }
                        }
                    }

                    // Custom Messages
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(300, delayMillis = 300)
                            ) + fadeIn(animationSpec = tween(300, delayMillis = 300))
                        ) {
                            SettingsSection(
                                title = stringResource(R.string.custom_messages),
                                icon = Icons.Default.Create
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = { viewModel.onEvent(SettingsEvent.ShowAddCustomMessageDialog) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF4A90E2)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                        Text(stringResource(R.string.add_message), color = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    // Custom Messages List
                    itemsIndexed(uiState.userPreferences.customMessages) { index, message ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(300, delayMillis = 350 + index * 50)
                            ) + fadeIn(animationSpec = tween(300, delayMillis = 350 + index * 50))
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF1A1A2E).copy(alpha = 0.8f)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = message,
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White
                                    )
                                    IconButton(
                                        onClick = { viewModel.onEvent(SettingsEvent.RemoveCustomMessage(index)) },
                                        modifier = Modifier
                                            .background(
                                                Color(0xFFE74C3C).copy(alpha = 0.2f),
                                                RoundedCornerShape(8.dp)
                                            )
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = stringResource(R.string.delete),
                                            tint = Color(0xFFE74C3C)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Permissions
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(300, delayMillis = 400)
                            ) + fadeIn(animationSpec = tween(300, delayMillis = 400))
                        ) {
                            SettingsSection(
                                title = stringResource(R.string.permissions),
                                icon = Icons.Default.Face
                            ) {
                                PermissionPreference(
                                    title = stringResource(R.string.accessibility_service),
                                    subtitle = stringResource(R.string.accessibility_required),
                                    onClick = { viewModel.onEvent(SettingsEvent.RequestPermission(PermissionType.ACCESSIBILITY)) }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                PermissionPreference(
                                    title = stringResource(R.string.display_over_apps),
                                    subtitle = stringResource(R.string.overlay_required),
                                    onClick = { viewModel.onEvent(SettingsEvent.RequestPermission(PermissionType.OVERLAY)) }
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                PermissionPreference(
                                    title = stringResource(R.string.usage_statistics),
                                    subtitle = stringResource(R.string.usage_required),
                                    onClick = { viewModel.onEvent(SettingsEvent.RequestPermission(PermissionType.USAGE_STATS)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A2E).copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color(0xFF4A90E2),
                    modifier = Modifier
                        .background(
                            Color(0xFF4A90E2).copy(alpha = 0.2f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            content()
        }
    }
}

@Composable
private fun SwitchPreference(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF4A90E2),
                uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                uncheckedTrackColor = Color.White.copy(alpha = 0.3f)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BlockingStrengthPreference(
    currentStrength: BlockingStrength,
    onStrengthChange: (BlockingStrength) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.blocking_strength),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = when (currentStrength) {
                    BlockingStrength.LOW -> stringResource(R.string.blocking_strength_low)
                    BlockingStrength.HIGH -> stringResource(R.string.blocking_strength_high)
                },
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4A90E2),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedTrailingIconColor = Color(0xFF4A90E2),
                    unfocusedTrailingIconColor = Color.White.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color(0xFF1A1A2E))
            ) {
                BlockingStrength.values().forEach { strength ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                when (strength) {
                                    BlockingStrength.LOW -> stringResource(R.string.blocking_strength_low)
                                    BlockingStrength.HIGH -> stringResource(R.string.blocking_strength_high)
                                },
                                color = Color.White
                            )
                        },
                        onClick = {
                            onStrengthChange(strength)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuoteCategoryPreferences(
    selectedCategories: Set<QuoteCategory>,
    onCategoriesChange: (Set<QuoteCategory>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.quote_categories),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )

        QuoteCategory.values().forEach { category ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedCategories.contains(category),
                    onCheckedChange = { checked ->
                        val newCategories = if (checked) {
                            selectedCategories + category
                        } else {
                            selectedCategories - category
                        }
                        onCategoriesChange(newCategories)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF4A90E2),
                        uncheckedColor = Color.White.copy(alpha = 0.6f),
                        checkmarkColor = Color.White
                    )
                )
                Text(
                    text = when (category) {
                        QuoteCategory.QURAN -> stringResource(R.string.quote_category_quran)
                        QuoteCategory.MOTIVATION -> stringResource(R.string.quote_category_motivation)
                        QuoteCategory.HADITH -> stringResource(R.string.quote_category_hadith)
                        QuoteCategory.STRENGTH -> stringResource(R.string.quote_category_strength)
                        QuoteCategory.CUSTOM -> stringResource(R.string.quote_category_custom)
                    },
                    modifier = Modifier.padding(start = 8.dp),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun TimePickerPreference(
    title: String,
    currentTime: String,
    onTimeChange: (String) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        Button(
            onClick = { showTimePicker = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A90E2).copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(currentTime, color = Color.White)
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            currentTime = currentTime,
            onTimeSelected = { selectedTime ->
                onTimeChange(selectedTime)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    currentTime: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val timeParts = currentTime.split(":")
    val currentHour = timeParts.getOrNull(0)?.toIntOrNull() ?: 8
    val currentMinute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

    val timePickerState = rememberTimePickerState(
        initialHour = currentHour,
        initialMinute = currentMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.reminder_time), color = Color.White) },
        text = {
            TimePicker(
                state = timePickerState,
                modifier = Modifier.padding(16.dp),
                colors = TimePickerDefaults.colors(
                    selectorColor = Color(0xFF4A90E2),
                    containerColor = Color(0xFF1A1A2E),
                    periodSelectorBorderColor = Color(0xFF4A90E2),
                    clockDialColor = Color(0xFF1A1A2E),
                    clockDialSelectedContentColor = Color.White,
                    clockDialUnselectedContentColor = Color.White.copy(alpha = 0.6f)
                )
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val hour = timePickerState.hour.toString().padStart(2, '0')
                    val minute = timePickerState.minute.toString().padStart(2, '0')
                    onTimeSelected("$hour:$minute")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2)
                )
            ) {
                Text(stringResource(R.string.ok), color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = Color.White.copy(alpha = 0.7f))
            }
        },
        containerColor = Color(0xFF1A1A2E)
    )
}

@Composable
private fun PermissionPreference(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF16213E).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF4A90E2)
            )
        }
    }
}

@Composable
private fun LanguagePreference(
    currentLanguage: Language,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF16213E).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.language),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = currentLanguage.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF4A90E2)
            )
        }
    }
}

@Composable
private fun AddCustomMessageDialog(
    currentMessage: String,
    onMessageChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_message), color = Color.White) },
        text = {
            OutlinedTextField(
                value = currentMessage,
                onValueChange = onMessageChange,
                label = { Text(stringResource(R.string.personal_motivational_messages), color = Color.White.copy(alpha = 0.7f)) },
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4FD1C7),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF4FD1C7)
                ),
                shape = RoundedCornerShape(12.dp)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = currentMessage.trim().isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4FD1C7)
                )
            ) {
                Text(stringResource(R.string.add_message), color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = Color.White.copy(alpha = 0.7f))
            }
        },
        containerColor = Color(0xFF1A1A2E)
    )
}

@Composable
private fun PermissionDialog(
    permissionType: PermissionType?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    if (permissionType != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.permission_required), color = Color.White) },
            text = {
                Text(
                    when (permissionType) {
                        PermissionType.ACCESSIBILITY -> stringResource(R.string.accessibility_permission_message)
                        PermissionType.OVERLAY -> stringResource(R.string.overlay_permission_message)
                        PermissionType.USAGE_STATS -> stringResource(R.string.usage_permission_message)
                        PermissionType.NOTIFICATIONS -> stringResource(R.string.notification_permission_message)
                    },
                    color = Color.White.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        when (permissionType) {
                            PermissionType.ACCESSIBILITY -> PermissionHelper.openAccessibilitySettings(context)
                            PermissionType.OVERLAY -> PermissionHelper.openOverlaySettings(context)
                            PermissionType.USAGE_STATS -> PermissionHelper.openUsageStatsSettings(context)
                            PermissionType.NOTIFICATIONS -> PermissionHelper.openNotificationSettings(context)
                        }
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D1B1B)
                    )
                ) {
                    Text(stringResource(R.string.go_to_settings), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel), color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = Color(0xFF1A1A2E)
        )
    }
}

@Composable
private fun LanguageSelectionDialog(
    currentLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.language_dialog_title), color = Color.White) },
        text = {
            Column {
                Language.values().forEach { language ->
                    TextButton(
                        onClick = {
                            onLanguageSelected(language)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White,
                            disabledContentColor = Color.White.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = when (language) {
                                Language.ENGLISH -> stringResource(R.string.language_english)
                                Language.ARABIC -> stringResource(R.string.language_arabic)
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (language == currentLanguage) Color(0xFF4A90E2) else Color.White
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2)
                )
            ) {
                Text(stringResource(R.string.close), color = Color.White)
            }
        },
        containerColor = Color(0xFF1A1A2E)
    )
}
