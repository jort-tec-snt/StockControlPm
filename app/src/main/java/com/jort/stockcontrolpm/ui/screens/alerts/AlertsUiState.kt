package com.jort.stockcontrolpm.ui.screens.alerts

import com.jort.stockcontrolpm.domain.model.Product

data class AlertsUiState(
    val isLoading: Boolean = false,
    val outOfStock: List<Product> = emptyList(),
    val lowStock: List<Product> = emptyList(),
    val expiringSoon: List<Product> = emptyList(),
    val errorMessage: String? = null
) {
    val totalAlerts: Int get() = outOfStock.size + lowStock.size + expiringSoon.size
    val hasAlerts: Boolean get() = totalAlerts > 0
}
