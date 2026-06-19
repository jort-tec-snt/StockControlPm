package com.jort.stockcontrolpm.ui.screens.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jort.stockcontrolpm.data.repository.ProductRepository
import com.jort.stockcontrolpm.domain.model.Product
import com.jort.stockcontrolpm.domain.model.StockStatus
import java.time.LocalDate
import java.time.format.DateTimeParseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlertsViewModel(
    private val repository: ProductRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AlertsUiState(isLoading = true))
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()

    init {
        observe()
    }

    private fun observe() {
        viewModelScope.launch {
            repository.observeProducts()
                .catch { throwable ->
                    _uiState.update { it.copy(isLoading = false,
                        errorMessage = throwable.message ?: "Error al cargar alertas.") }
                }
                .collect { products ->
                    val today = LocalDate.now()
                    _uiState.update {
                        AlertsUiState(
                            isLoading    = false,
                            outOfStock   = products.filter { it.stockStatus == StockStatus.OUT_OF_STOCK },
                            lowStock     = products.filter { it.stockStatus == StockStatus.LOW },
                            expiringSoon = products.filter { p -> p.isExpiringSoon(today) }
                        )
                    }
                }
        }
    }

    private fun Product.isExpiringSoon(today: LocalDate): Boolean {
        val date = expirationDate ?: return false
        return try {
            val exp = LocalDate.parse(date)
            val days = java.time.temporal.ChronoUnit.DAYS.between(today, exp)
            days in 0..10
        } catch (_: DateTimeParseException) { false }
    }
}
