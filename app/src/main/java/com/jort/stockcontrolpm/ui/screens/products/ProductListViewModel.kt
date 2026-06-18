package com.jort.stockcontrolpm.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jort.stockcontrolpm.data.repository.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val repository: ProductRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductListUiState(isLoading = true))
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private var productsJob: Job? = null

    init {
        observeProducts()
    }

    fun observeProducts() {
        productsJob?.cancel()
        productsJob = viewModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true, errorMessage = null) }

            repository.observeProducts()
                .catch { throwable ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "No se pudo cargar el inventario."
                        )
                    }
                }
                .collect { products ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            products = products,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state -> state.copy(searchQuery = query) }
        productsJob?.cancel()
        productsJob = viewModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true, errorMessage = null) }

            val source = if (query.isBlank()) {
                repository.observeProducts()
            } else {
                repository.searchProducts(query.trim())
            }

            source
                .catch { throwable ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "No se pudo buscar productos."
                        )
                    }
                }
                .collect { products ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            products = products,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { state -> state.copy(errorMessage = null) }
    }
}

