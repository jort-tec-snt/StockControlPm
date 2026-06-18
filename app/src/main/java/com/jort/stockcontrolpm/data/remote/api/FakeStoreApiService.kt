package com.jort.stockcontrolpm.data.remote.api

import com.jort.stockcontrolpm.data.remote.dto.FakeStoreProductDto
import retrofit2.http.GET

interface FakeStoreApiService {
    @GET("products")
    suspend fun getProducts(): List<FakeStoreProductDto>
}

