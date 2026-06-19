package com.jort.stockcontrolpm.domain.model

data class ExternalProduct(
    val id: Int,
    val title: String,
    val price: Double,
    val category: String,
    val description: String,
    val imageUrl: String
)
