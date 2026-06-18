package com.jort.stockcontrolpm.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jort.stockcontrolpm.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val repository: ProductRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun loadProduct(productId: Long) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(isLoading = true, errorMessage = null, wasDeleted = false)
            }

            runCatching { repository.getProductById(productId) }
                .onSuccess { product ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            product = product,
                            errorMessage = if (product == null) "Producto no encontrado." else null
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "No se pudo cargar el producto."
                        )
                    }
                }
        }
    }

    fun deleteProduct() {
        val product = _uiState.value.product ?: return
        viewModelScope.launch {
            runCatching { repository.deleteProduct(product) }
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(product = null, wasDeleted = true, errorMessage = null)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { state ->
                        state.copy(
                            errorMessage = throwable.message ?: "No se pudo eliminar el producto."
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { state -> state.copy(errorMessage = null) }
    }
}

