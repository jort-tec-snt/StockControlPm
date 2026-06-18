package com.jort.stockcontrolpm.ui.screens.apiinfo

data class ApiInfoUiState(
    val isLoading: Boolean = false,
    val content: String? = null,
    val errorMessage: String? = null
)

