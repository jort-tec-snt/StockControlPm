package com.jort.stockcontrolpm.ui.screens.products

data class ProductFormUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val productId: Long? = null,
    val name: String = "",
    val category: String = "",
    val stock: String = "",
    val minStock: String = "",
    val unitPrice: String = "",
    val expirationDate: String = "",
    val errorMessage: String? = null,
    val wasSaved: Boolean = false
) {
    val isEditing: Boolean
        get() = productId != null
}

