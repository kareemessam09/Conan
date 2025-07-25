package com.example.conan.presentation.popup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.conan.R

@Composable
fun TriggerAlertDialog(
    viewModel: TriggerPopupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isVisible) {
        Dialog(
            onDismissRequest = { /* Prevent dismissal by tapping outside */ },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                if (uiState.showSecondaryQuote) {
                    SecondaryQuoteContent(
                        quote = uiState.motivationalQuote,
                        countdown = uiState.autoHideCounter
                    )
                } else {
                    MainPopupContent(
                        quote = uiState.motivationalQuote,
                        isLoading = uiState.isLoading,
                        onOkayPressed = { viewModel.onEvent(TriggerPopupEvent.OkayPressed) },
                        onDismissPressed = { viewModel.onEvent(TriggerPopupEvent.DismissPressed) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MainPopupContent(
    quote: com.example.conan.data.models.Quote?,
    isLoading: Boolean,
    onOkayPressed: () -> Unit,
    onDismissPressed: () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Warning Icon
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )

        // Title
        Text(
            text = stringResource(R.string.stay_strong),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Quote Content
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else {
            quote?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "\"${it.text}\"",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                        )
                        it.source?.let { source ->
                            Text(
                                text = "- $source",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onOkayPressed,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.ok),
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = onDismissPressed,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.close),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SecondaryQuoteContent(
    quote: com.example.conan.data.models.Quote?,
    countdown: Int
) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.remember_this),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        quote?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "\"${it.text}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    it.source?.let { source ->
                        Text(
                            text = "- $source",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        if (countdown > 0) {
            Text(
                text = stringResource(R.string.closing_in_seconds, countdown),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

