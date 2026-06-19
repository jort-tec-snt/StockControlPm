package com.jort.stockcontrolpm.ui.screens.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val role: UserRole = UserRole.OWNER,
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false
)

enum class UserRole(val label: String) {
    OWNER("Propietario"),
    CASHIER("Cajero")
}
