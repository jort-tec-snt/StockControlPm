package com.jort.stockcontrolpm.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jort.stockcontrolpm.data.repository.ProductRepository
import com.jort.stockcontrolpm.domain.model.Product
import java.time.LocalDate
import java.time.format.DateTimeParseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: ProductRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeDashboard()
    }

    fun observeDashboard() {
        viewModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true, errorMessage = null) }

            repository.observeProducts()
                .catch { throwable ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "No se pudo cargar el dashboard."
                        )
                    }
                }
                .collect { products ->
                    _uiState.update {
                        DashboardUiState(
                            isLoading = false,
                            totalProducts = products.size,
                            outOfStockProducts = products.count { product -> product.isOutOfStock },
                            criticalStockProducts = products.count { product -> product.isCriticalStock },
                            expiringSoonProducts = products.count { product -> product.isExpiringSoon() },
                            inventoryValue = products.sumOf { product -> product.inventoryValue }
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { state -> state.copy(errorMessage = null) }
    }

    private fun Product.isExpiringSoon(): Boolean {
        val date = expirationDate ?: return false
        return try {
            val expiration = LocalDate.parse(date)
            val today = LocalDate.now()
            !expiration.isBefore(today) && !expiration.isAfter(today.plusDays(30))
        } catch (_: DateTimeParseException) {
            false
        }
    }
}
