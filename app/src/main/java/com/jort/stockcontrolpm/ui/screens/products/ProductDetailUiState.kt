package com.jort.stockcontrolpm.ui.screens.products

import com.jort.stockcontrolpm.domain.model.Product

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val errorMessage: String? = null,
    val wasDeleted: Boolean = false
)

