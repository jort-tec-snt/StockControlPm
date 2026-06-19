package com.jort.stockcontrolpm.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jort.stockcontrolpm.data.repository.ProductRepository
import com.jort.stockcontrolpm.domain.model.Product
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
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

    private fun observeDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.observeProducts()
                .catch { throwable ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "No se pudo cargar el dashboard."
                    ) }
                }
                .collect { products ->
                    val today = LocalDate.now()
                    val expiring = products
                        .mapNotNull { p -> p.daysUntilExpiry(today)?.let { days ->
                            ExpiringProductInfo(p.name, days)
                        }}
                        .sortedBy { it.daysLeft }

                    _uiState.update {
                        DashboardUiState(
                            isLoading              = false,
                            totalProducts          = products.size,
                            outOfStockProducts     = products.count { it.isOutOfStock },
                            criticalStockProducts  = products.count { it.isCriticalStock },
                            expiringSoonProducts   = expiring.size,
                            inventoryValue         = products.sumOf { it.inventoryValue },
                            expiringProductsList   = expiring.take(3)
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // Retorna días restantes si vence en los próximos 10 días, null si no aplica
    private fun Product.daysUntilExpiry(today: LocalDate): Int? {
        val date = expirationDate ?: return null
        return try {
            val expiration = LocalDate.parse(date)
            val days = ChronoUnit.DAYS.between(today, expiration).toInt()
            if (days in 0..10) days else null
        } catch (_: DateTimeParseException) {
            null
        }
    }
}
