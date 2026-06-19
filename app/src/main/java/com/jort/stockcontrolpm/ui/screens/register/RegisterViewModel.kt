package com.jort.stockcontrolpm.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jort.stockcontrolpm.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String)            = _uiState.update { it.copy(name = value, errorMessage = null) }
    fun onEmailChange(value: String)           = _uiState.update { it.copy(email = value, errorMessage = null) }
    fun onPasswordChange(value: String)        = _uiState.update { it.copy(password = value, errorMessage = null) }
    fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }
    fun onRoleChange(value: String)            = _uiState.update { it.copy(role = value) }
    fun onTogglePasswordVisibility()           = _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    fun clearError()                           = _uiState.update { it.copy(errorMessage = null) }

    fun register() {
        val state = _uiState.value
        if (!state.isValid) {
            _uiState.update { it.copy(errorMessage = "Corrige los campos marcados en rojo.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            userRepository.register(state.name, state.email, state.password, state.role)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(
                        isLoading    = false,
                        errorMessage = error.message ?: "No se pudo crear la cuenta."
                    ) }
                }
        }
    }
}
