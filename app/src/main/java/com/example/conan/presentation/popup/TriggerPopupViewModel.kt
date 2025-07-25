package com.example.conan.presentation.popup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conan.domain.usecases.GetDailyQuoteUseCase
import com.example.conan.domain.usecases.LogTriggerEventUseCase
import com.example.conan.data.models.TriggerAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TriggerPopupViewModel @Inject constructor(
    private val getDailyQuoteUseCase: GetDailyQuoteUseCase,
    private val logTriggerEventUseCase: LogTriggerEventUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TriggerPopupUiState())
    val uiState: StateFlow<TriggerPopupUiState> = _uiState.asStateFlow()

    fun onEvent(event: TriggerPopupEvent) {
        when (event) {
            is TriggerPopupEvent.ShowPopup -> showPopup(event.triggerSource)
            is TriggerPopupEvent.OkayPressed -> handleOkayPressed()
            is TriggerPopupEvent.DismissPressed -> handleDismissPressed()
            is TriggerPopupEvent.RequestBlock -> handleRequestBlock()
            is TriggerPopupEvent.DeclineBlock -> handleDeclineBlock()
            is TriggerPopupEvent.HidePopup -> hidePopup()
        }
    }

    private fun showPopup(triggerSource: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isVisible = true,
                triggerSource = triggerSource,
                isLoading = true
            )

            try {
                val quote = getDailyQuoteUseCase()
                _uiState.value = _uiState.value.copy(
                    motivationalQuote = quote,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false
                )
            }
        }
    }

    private fun handleOkayPressed() {
        viewModelScope.launch {
            // Log the action
            logTriggerEventUseCase(
                appName = _uiState.value.triggerSource,
                triggerKeyword = "detected",
                userAction = TriggerAction.DISMISSED_WITH_OK
            )

            // Show secondary quote for 5 seconds
            val secondaryQuote = getDailyQuoteUseCase()
            _uiState.value = _uiState.value.copy(
                motivationalQuote = secondaryQuote,
                showSecondaryQuote = true,
                autoHideCounter = 5
            )

            // Start countdown
            repeat(5) { count ->
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    autoHideCounter = 4 - count
                )
            }

            hidePopup()
        }
    }

    private fun handleDismissPressed() {
        // Show blocking options dialog instead of hiding immediately
        _uiState.value = _uiState.value.copy(showSecondaryQuote = false)
        // The UI will show blocking options
    }

    private fun handleRequestBlock() {
        viewModelScope.launch {
            logTriggerEventUseCase(
                appName = _uiState.value.triggerSource,
                triggerKeyword = "detected",
                userAction = TriggerAction.REQUESTED_BLOCK,
                wasBlocked = true
            )
            hidePopup()
            // Signal to accessibility service to close the app
        }
    }

    private fun handleDeclineBlock() {
        viewModelScope.launch {
            logTriggerEventUseCase(
                appName = _uiState.value.triggerSource,
                triggerKeyword = "detected",
                userAction = TriggerAction.DISMISSED_COMPLETELY
            )
            hidePopup()
        }
    }

    private fun hidePopup() {
        _uiState.value = TriggerPopupUiState()
    }
}
