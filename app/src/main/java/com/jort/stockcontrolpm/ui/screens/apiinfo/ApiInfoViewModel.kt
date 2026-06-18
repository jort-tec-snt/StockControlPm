package com.jort.stockcontrolpm.ui.screens.apiinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jort.stockcontrolpm.data.repository.ApiInfoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ApiInfoViewModel(
    private val repository: ApiInfoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ApiInfoUiState())
    val uiState: StateFlow<ApiInfoUiState> = _uiState.asStateFlow()

    init {
        loadExternalProducts()
    }

    fun loadExternalProducts() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(isLoading = true, errorMessage = null)
            }

            runCatching { repository.getExternalProducts() }
                .onSuccess { products ->
                    _uiState.update {
                        ApiInfoUiState(
                            isLoading = false,
                            products = products.take(5)
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            products = emptyList(),
                            errorMessage = throwable.message ?: "No se pudo consultar la API publica."
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { state -> state.copy(errorMessage = null) }
    }
}

