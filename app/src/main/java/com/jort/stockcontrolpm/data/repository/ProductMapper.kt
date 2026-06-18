package com.jort.stockcontrolpm.data.repository

import com.jort.stockcontrolpm.data.local.entity.ProductEntity
import com.jort.stockcontrolpm.domain.model.Product

internal fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        category = category,
        stock = stock,
        minStock = minStock,
        unitPrice = unitPrice,
        expirationDate = expirationDate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

internal fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        category = category,
        stock = stock,
        minStock = minStock,
        unitPrice = unitPrice,
        expirationDate = expirationDate,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

