package com.example.conan.presentation.popup

import com.example.conan.data.models.Quote

data class TriggerPopupUiState(
    val isVisible: Boolean = false,
    val motivationalQuote: Quote? = null,
    val triggerSource: String = "",
    val isLoading: Boolean = false,
    val showSecondaryQuote: Boolean = false,
    val autoHideCounter: Int = 5
)

sealed class TriggerPopupEvent {
    data class ShowPopup(val triggerSource: String) : TriggerPopupEvent()
    object OkayPressed : TriggerPopupEvent()
    object DismissPressed : TriggerPopupEvent()
    object RequestBlock : TriggerPopupEvent()
    object DeclineBlock : TriggerPopupEvent()
    object HidePopup : TriggerPopupEvent()
}
