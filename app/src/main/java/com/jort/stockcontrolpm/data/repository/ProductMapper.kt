package com.jort.stockcontrolpm.data.repository

import com.jort.stockcontrolpm.data.local.entity.MovementEntity
import com.jort.stockcontrolpm.data.local.entity.ProductEntity
import com.jort.stockcontrolpm.domain.model.Movement
import com.jort.stockcontrolpm.domain.model.MovementType
import com.jort.stockcontrolpm.domain.model.Product

internal fun ProductEntity.toDomain(): Product = Product(
    id, name, category, stock, minStock, unitPrice,
    purchasePrice, sku, supplier, expirationDate, description,
    codigoBarras, imagenUrl, visible, userId,
    createdAt, updatedAt
)

internal fun Product.toEntity(): ProductEntity = ProductEntity(
    id, name, category, stock, minStock, unitPrice,
    purchasePrice, sku, supplier, expirationDate, description,
    codigoBarras, imagenUrl, visible, userId,
    createdAt, updatedAt
)

internal fun MovementEntity.toDomain(): Movement = Movement(
    id, productId, productName, MovementType.valueOf(type), qty, reason, date, userId, notes
)

internal fun Movement.toEntity(): MovementEntity = MovementEntity(
    id, productId, productName, type.name, qty, reason, date, userId, notes, null
)
