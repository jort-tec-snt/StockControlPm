package com.jort.stockcontrolpm.ui.screens.products

import com.jort.stockcontrolpm.domain.model.Product

data class ProductListUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && products.isEmpty() && errorMessage == null
}

