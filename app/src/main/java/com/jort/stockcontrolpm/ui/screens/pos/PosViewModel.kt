package com.jort.stockcontrolpm.ui.screens.pos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jort.stockcontrolpm.data.repository.ProductRepository
import com.jort.stockcontrolpm.domain.model.Product
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PosViewModel(
    private val repository: ProductRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PosUiState())
    val uiState: StateFlow<PosUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            repository.searchProducts(query.trim())
                .catch { _uiState.update { it.copy(isSearching = false) } }
                .collect { results ->
                    _uiState.update { it.copy(isSearching = false, searchResults = results.take(8)) }
                }
        }
    }

    fun addToCart(product: Product) {
        _uiState.update { state ->
            val existing = state.cartItems.find { it.productId == product.id }
            val updated = if (existing != null) {
                if (existing.qty >= existing.maxStock) return@update state
                state.cartItems.map {
                    if (it.productId == product.id) it.copy(qty = it.qty + 1) else it
                }
            } else {
                if (product.stock <= 0) return@update state
                state.cartItems + CartItem(
                    productId = product.id,
                    name      = product.name,
                    unitPrice = product.price,
                    qty       = 1,
                    maxStock  = product.stock
                )
            }
            state.copy(cartItems = updated, searchQuery = "", searchResults = emptyList())
        }
    }

    fun incrementQty(productId: Long) {
        _uiState.update { state ->
            state.copy(cartItems = state.cartItems.map {
                if (it.productId == productId && it.qty < it.maxStock)
                    it.copy(qty = it.qty + 1) else it
            })
        }
    }

    fun decrementQty(productId: Long) {
        _uiState.update { state ->
            val updated = state.cartItems.mapNotNull {
                when {
                    it.productId != productId -> it
                    it.qty > 1               -> it.copy(qty = it.qty - 1)
                    else                     -> null    // elimina del carrito
                }
            }
            state.copy(cartItems = updated)
        }
    }

    fun removeFromCart(productId: Long) {
        _uiState.update { it.copy(cartItems = it.cartItems.filter { item -> item.productId != productId }) }
    }

    fun clearCart() {
        _uiState.update { it.copy(cartItems = emptyList(), lastSaleRef = null) }
    }

    fun checkout() {
        val state = _uiState.value
        if (state.isEmpty || state.isProcessing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, errorMessage = null) }
            // Simula procesamiento (integración de pagos en v2.5)
            delay(1500)
            val ref = "VNT-${System.currentTimeMillis() % 100_000}"
            _uiState.update { it.copy(
                isProcessing  = false,
                showQrSheet   = true,
                lastSaleRef   = ref
            ) }
        }
    }

    fun dismissQrSheet() {
        _uiState.update { it.copy(showQrSheet = false, cartItems = emptyList(), lastSaleRef = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
