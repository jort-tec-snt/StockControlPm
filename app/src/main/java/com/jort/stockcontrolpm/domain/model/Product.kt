package com.jort.stockcontrolpm.domain.model

data class Product(
    val id: Long = 0,
    val name: String,
    val category: String,
    val stock: Int,
    val minStock: Int = 5,
    val unitPrice: Double,
    val purchasePrice: Double?,
    val sku: String = "",
    val supplier: String?,
    val expirationDate: String?,
    val description: String?,
    // Campos Tabla 21 del PDF
    val codigoBarras: String? = null,
    val imagenUrl: String? = null,
    val visible: Boolean = true,
    val userId: String = "",
    val createdAt: Long,
    val updatedAt: Long
) {
    val isOutOfStock: Boolean
        get() = stock <= 0

    val isCriticalStock: Boolean
        get() = stock > 0 && stock <= minStock

    val inventoryValue: Double
        get() = stock * unitPrice

    val margin: Double?
        get() = purchasePrice?.let { cost ->
            if (cost > 0) ((unitPrice - cost) / cost) * 100 else null
        }

    val stockStatus: StockStatus
        get() = when {
            isOutOfStock    -> StockStatus.OUT_OF_STOCK
            isCriticalStock -> StockStatus.LOW
            else            -> StockStatus.OK
        }
}

enum class StockStatus { OK, LOW, OUT_OF_STOCK }

val PredefinedCategories = listOf(
    "Men's clothing", "Women's clothing", "Electronics", "Jewelery", "Otros"
)
