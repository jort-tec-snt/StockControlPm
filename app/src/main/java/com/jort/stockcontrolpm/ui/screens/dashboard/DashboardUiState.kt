package com.jort.stockcontrolpm.ui.screens.dashboard

data class ExpiringProductInfo(
    val name: String,
    val daysLeft: Int
)

data class DashboardUiState(
    val isLoading: Boolean = false,
    val totalProducts: Int = 0,
    val outOfStockProducts: Int = 0,
    val criticalStockProducts: Int = 0,
    val expiringSoonProducts: Int = 0,
    val inventoryValue: Double = 0.0,
    val salesToday: Double = 0.0,           // preparado para POS (Pieza 9)
    val transactionsToday: Int = 0,         // preparado para POS (Pieza 9)
    val expiringProductsList: List<ExpiringProductInfo> = emptyList(),
    val errorMessage: String? = null
) {
    // Nivel de alerta para el AlertBanner
    val alertLevel: AlertLevel
        get() = when {
            outOfStockProducts > 0   -> AlertLevel.CRITICAL
            criticalStockProducts > 0 -> AlertLevel.WARNING
            else                      -> AlertLevel.NONE
        }
}

enum class AlertLevel { NONE, WARNING, CRITICAL }
