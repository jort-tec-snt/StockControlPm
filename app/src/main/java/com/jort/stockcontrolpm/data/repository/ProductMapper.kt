package com.jort.stockcontrolpm.data.repository

import com.jort.stockcontrolpm.data.local.entity.MovementEntity
import com.jort.stockcontrolpm.data.local.entity.ProductEntity
import com.jort.stockcontrolpm.domain.model.Movement
import com.jort.stockcontrolpm.domain.model.MovementType
import com.jort.stockcontrolpm.domain.model.Product

internal fun ProductEntity.toDomain(): Product {
    return Product(
        id            = id,
        name          = name,
        category      = category,
        stock         = stock,
        minStock      = minStock,
        unitPrice     = unitPrice,
        purchasePrice = purchasePrice,
        sku           = sku,
        supplier      = supplier,
        expirationDate = expirationDate,
        description   = description,
        createdAt     = createdAt,
        updatedAt     = updatedAt
    )
}

internal fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id, name, category, stock, minStock, unitPrice,
        purchasePrice, sku, supplier, expirationDate, description,
        createdAt, updatedAt
    )
}

internal fun MovementEntity.toDomain(): Movement {
    return Movement(
        id          = id,
        productId   = productId,
        productName = productName,
        type        = MovementType.valueOf(type),
        qty         = qty,
        reason      = reason,
        date        = date,
        userId      = userId,
        notes       = notes
    )
}

internal fun Movement.toEntity(): MovementEntity {
    return MovementEntity(
        id, productId, productName, type.name, qty, reason, date, userId, notes
    )
}
