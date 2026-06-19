package com.jort.stockcontrolpm.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onRoleChange(role: UserRole) {
        _uiState.update { it.copy(role = role) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun login() {
        val state = _uiState.value

        // Validaciones básicas (Firebase Auth se integra en v2.5)
        when {
            state.email.isBlank() || !state.email.contains("@") -> {
                _uiState.update { it.copy(errorMessage = "Ingresa un correo electrónico válido.") }
                return
            }
            state.password.length < 4 -> {
                _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 4 caracteres.") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            // Simula latencia de red hasta integrar Firebase Auth
            delay(1200)
            _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
