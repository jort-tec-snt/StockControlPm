package com.jort.stockcontrolpm.ui.screens.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // true cuando Firebase confirmó las credenciales → AppNavigation navega al Dashboard
    val loginSuccess: Boolean = false
)
