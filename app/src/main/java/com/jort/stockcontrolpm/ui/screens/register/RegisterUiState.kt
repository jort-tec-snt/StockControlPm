package com.jort.stockcontrolpm.ui.screens.register

data class RegisterUiState(
    val name: String            = "",
    val email: String           = "",
    val password: String        = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean      = false,
    val registerSuccess: Boolean = false,
    val errorMessage: String?   = null
)
