package com.jort.stockcontrolpm.ui.screens.apiinfo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ApiInfoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        ApiInfoUiState(content = "Consumo de API pendiente para el bloque Retrofit.")
    )
    val uiState: StateFlow<ApiInfoUiState> = _uiState.asStateFlow()

    fun clearError() {
        _uiState.update { state -> state.copy(errorMessage = null) }
    }
}

