package com.jort.stockcontrolpm.ui.screens.dashboard

data class DashboardUiState(
    val isLoading: Boolean = false,
    val totalProducts: Int = 0,
    val outOfStockProducts: Int = 0,
    val criticalStockProducts: Int = 0,
    val expiringSoonProducts: Int = 0,
    val inventoryValue: Double = 0.0,
    val errorMessage: String? = null
)
