package com.jort.stockcontrolpm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val stock: Int,
    val minStock: Int,
    val unitPrice: Double,
    val expirationDate: String?,
    val createdAt: Long,
    val updatedAt: Long
)

