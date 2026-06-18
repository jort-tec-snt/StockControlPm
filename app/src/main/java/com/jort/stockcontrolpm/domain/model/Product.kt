package com.jort.stockcontrolpm.domain.model

data class Product(
    val id: Long = 0,
    val name: String,
    val category: String,
    val stock: Int,
    val minStock: Int,
    val unitPrice: Double,
    val expirationDate: String?,
    val createdAt: Long,
    val updatedAt: Long
) {
    val isOutOfStock: Boolean
        get() = stock <= 0

    val isCriticalStock: Boolean
        get() = stock > 0 && stock <= minStock

    val inventoryValue: Double
        get() = stock * unitPrice
}

