package com.jort.stockcontrolpm.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FakeStoreProductDto(
    val id: Long,
    val title: String,
    val price: Double,
    val category: String,
    val description: String,
    @SerializedName("image") val imageUrl: String = ""
)
