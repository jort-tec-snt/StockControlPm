package com.jort.stockcontrolpm.ui.screens.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jort.stockcontrolpm.data.repository.ApiInfoRepository
import com.jort.stockcontrolpm.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val repository: ProductRepository,
    private val apiInfoRepository: ApiInfoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductListUiState(isLoading = true))
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    init {
        observeProducts()
        refreshFromApi()
    }

    private fun observeProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.observeProducts()
                .catch { throwable ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "No se pudo cargar el inventario."
                    ) }
                }
                .collect { products ->
                    _uiState.update { it.copy(isLoading = false, allProducts = products, errorMessage = null) }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategoryChange(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun refreshFromApi() {
        if (_uiState.value.isSyncingApi) return

        viewModelScope.launch {
            _uiState.update { it.copy(
                isSyncingApi = true,
                apiSyncError = null,
                lastImportedCount = null
            ) }

            runCatching {
                val externalProducts = apiInfoRepository.getExternalProducts()
                repository.importExternalProducts(externalProducts)
            }
                .onSuccess { importedCount ->
                    _uiState.update { it.copy(
                        isSyncingApi = false,
                        apiSyncError = null,
                        lastImportedCount = importedCount
                    ) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(
                        isSyncingApi = false,
                        apiSyncError = throwable.message ?: "No se pudo sincronizar FakeStore API."
                    ) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
