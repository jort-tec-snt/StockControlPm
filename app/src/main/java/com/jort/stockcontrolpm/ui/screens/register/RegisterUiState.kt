package com.jort.stockcontrolpm.ui.screens.register

data class RegisterUiState(
    val name: String             = "",
    val email: String            = "",
    val password: String         = "",
    val confirmPassword: String  = "",
    val role: String             = "OWNER",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean       = false,
    val registerSuccess: Boolean = false,
    val errorMessage: String?    = null
) {
    val nameError: String? get() = when {
        name.isBlank() -> "Ingresa tu nombre"
        name.length < 3 -> "Mínimo 3 caracteres"
        else -> null
    }
    val emailError: String? get() = when {
        email.isBlank() -> "Ingresa tu correo"
        !email.contains("@") -> "Correo inválido"
        else -> null
    }
    val passwordError: String? get() = when {
        password.isBlank() -> "Ingresa una contraseña"
        password.length < 6 -> "Mínimo 6 caracteres"
        else -> null
    }
    val confirmError: String? get() = when {
        confirmPassword.isBlank() -> "Confirma tu contraseña"
        confirmPassword != password -> "Las contraseñas no coinciden"
        else -> null
    }
    val isValid: Boolean get() =
        nameError == null && emailError == null &&
        passwordError == null && confirmError == null
}
