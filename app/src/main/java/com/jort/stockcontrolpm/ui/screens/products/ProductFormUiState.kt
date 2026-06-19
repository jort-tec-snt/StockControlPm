package com.jort.stockcontrolpm.ui.screens.products

data class ProductFormUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val productId: Long? = null,
    // Campos básicos
    val name: String = "",
    val category: String = "",
    val stock: String = "",
    val minStock: String = "",
    val unitPrice: String = "",
    val expirationDate: String = "",
    // Campos nuevos (Pieza 8)
    val sku: String = "",
    val supplier: String = "",
    val purchasePrice: String = "",
    val description: String = "",
    // Control de UI
    val showCategorySheet: Boolean = false,
    val errorMessage: String? = null,
    val wasSaved: Boolean = false
) {
    val isEditing: Boolean get() = productId != null
}
