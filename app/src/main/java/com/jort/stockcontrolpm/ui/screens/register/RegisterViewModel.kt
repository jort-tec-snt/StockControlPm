package com.jort.stockcontrolpm.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jort.stockcontrolpm.data.repository.FirebaseAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String)            = _uiState.update { it.copy(name = value, errorMessage = null) }
    fun onEmailChange(value: String)           = _uiState.update { it.copy(email = value, errorMessage = null) }
    fun onPasswordChange(value: String)        = _uiState.update { it.copy(password = value, errorMessage = null) }
    fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }
    fun onTogglePasswordVisibility()           = _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }

    fun register() {
        val state = _uiState.value

        // Validaciones locales
        when {
            state.name.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Ingresa tu nombre") }
                return
            }
            state.email.isBlank() || !state.email.contains("@") -> {
                _uiState.update { it.copy(errorMessage = "Ingresa un correo válido") }
                return
            }
            state.password.length < 6 -> {
                _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres") }
                return
            }
            state.password != state.confirmPassword -> {
                _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = authRepository.register(state.email.trim(), state.password)

            when (result) {
                is FirebaseAuthRepository.AuthResult.Success -> {
                    // Guarda el nombre en el perfil de Firebase para mostrarlo en ProfileScreen
                    if (state.name.isNotBlank()) {
                        authRepository.updateDisplayName(state.name.trim())
                    }
                    _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
                }
                is FirebaseAuthRepository.AuthResult.Error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}
