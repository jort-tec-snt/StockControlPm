package com.jort.stockcontrolpm.ui.screens.pos

import com.jort.stockcontrolpm.domain.model.Product

data class CartItem(
    val productId: Long,
    val name: String,
    val unitPrice: Double,
    val qty: Int,
    val maxStock: Int
) {
    val subtotal: Double get() = unitPrice * qty
}

data class PosUiState(
    val searchQuery: String = "",
    val searchResults: List<Product> = emptyList(),
    val isSearching: Boolean = false,
    val cartItems: List<CartItem> = emptyList(),
    val isProcessing: Boolean = false,
    val showQrSheet: Boolean = false,
    val lastSaleRef: String? = null,
    val errorMessage: String? = null
) {
    val subtotal: Double get() = cartItems.sumOf { it.subtotal }
    val igv: Double     get() = subtotal * 0.18
    val total: Double   get() = subtotal + igv
    val cartCount: Int  get() = cartItems.sumOf { it.qty }
    val isEmpty: Boolean get() = cartItems.isEmpty()
}
