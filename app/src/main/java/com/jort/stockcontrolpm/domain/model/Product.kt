package com.jort.stockcontrolpm.domain.model

data class Product(
    val id: Long = 0,
    val name: String,
    val category: String,
    val stock: Int,
    val minStock: Int = 5,
    val unitPrice: Double,       // precio de venta
    val purchasePrice: Double?,  // precio de compra (opcional)
    val sku: String = "",        // código único por producto
    val supplier: String?,       // nombre del proveedor
    val expirationDate: String?,
    val description: String?,
    val createdAt: Long,
    val updatedAt: Long
) {
    val isOutOfStock: Boolean
        get() = stock <= 0

    val isCriticalStock: Boolean
        get() = stock > 0 && stock <= minStock

    val inventoryValue: Double
        get() = stock * unitPrice

    // Margen calculado solo cuando existe precio de compra
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

// Categorías predefinidas del MVP
val PredefinedCategories = listOf(
    "Abarrotes", "Bebidas", "Lácteos", "Panadería",
    "Limpieza", "Snacks", "Frutas", "Otros"
)
