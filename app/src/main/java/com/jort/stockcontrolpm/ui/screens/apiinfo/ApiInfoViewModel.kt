package com.jort.stockcontrolpm.ui.screens.apiinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jort.stockcontrolpm.data.repository.ApiInfoRepository
import com.jort.stockcontrolpm.data.repository.ProductRepository
import com.jort.stockcontrolpm.domain.model.ExternalProduct
import com.jort.stockcontrolpm.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ApiInfoViewModel(
    private val repository: ApiInfoRepository,
    private val productRepository: ProductRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ApiInfoUiState())
    val uiState: StateFlow<ApiInfoUiState> = _uiState.asStateFlow()

    init {
        loadExternalProducts()
    }

    fun loadExternalProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { repository.getExternalProducts() }
                .onSuccess { products ->
                    _uiState.update { it.copy(isLoading = false, products = products) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(
                        isLoading    = false,
                        products     = emptyList(),
                        errorMessage = throwable.message ?: "No se pudo consultar la API pública."
                    ) }
                }
        }
    }

    fun importProduct(external: ExternalProduct) {
        if (_uiState.value.importedIds.contains(external.id)) return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val product = Product(
                id             = 0L,
                name           = external.title,
                category       = external.category.replaceFirstChar { it.uppercase() },
                stock          = 0,
                minStock       = 5,
                unitPrice      = external.price,
                purchasePrice  = null,
                sku            = "EXT-${external.id}",
                supplier       = "FakeStore API",
                expirationDate = null,
                description    = external.description,
                imagenUrl      = external.imageUrl,
                createdAt      = now,
                updatedAt      = now
            )
            runCatching { productRepository.saveProduct(product) }
                .onSuccess {
                    _uiState.update { state -> state.copy(
                        importedIds  = state.importedIds + external.id,
                        lastImported = external.title
                    ) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
