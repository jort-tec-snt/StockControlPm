package com.jort.stockcontrolpm.ui.screens.apiinfo

import com.jort.stockcontrolpm.domain.model.ExternalProduct

data class ApiInfoUiState(
    val isLoading: Boolean = false,
    val products: List<ExternalProduct> = emptyList(),
    val errorMessage: String? = null
) {
    val hasProducts: Boolean get() = products.isNotEmpty()
}
