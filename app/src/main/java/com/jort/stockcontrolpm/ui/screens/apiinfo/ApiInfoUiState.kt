package com.jort.stockcontrolpm.ui.screens.apiinfo

import com.jort.stockcontrolpm.domain.model.ExternalProduct

data class ApiInfoUiState(
    val isLoading: Boolean = false,
    val products: List<ExternalProduct> = emptyList(),
    val importedIds: Set<Int> = emptySet(),   // IDs ya importados en esta sesión
    val lastImported: String? = null,          // nombre del último importado (para Snackbar)
    val errorMessage: String? = null
) {
    val hasProducts: Boolean get() = products.isNotEmpty()
}
