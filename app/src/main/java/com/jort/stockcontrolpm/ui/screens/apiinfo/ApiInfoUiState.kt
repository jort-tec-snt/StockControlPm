package com.jort.stockcontrolpm.ui.screens.apiinfo

import com.jort.stockcontrolpm.data.remote.dto.FakeStoreProductDto

data class ApiInfoUiState(
    val isLoading: Boolean = false,
    val products: List<FakeStoreProductDto> = emptyList(),
    val errorMessage: String? = null
) {
    val hasProducts: Boolean
        get() = products.isNotEmpty()
}
