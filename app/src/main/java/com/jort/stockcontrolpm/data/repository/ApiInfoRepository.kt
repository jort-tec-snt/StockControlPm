package com.jort.stockcontrolpm.data.repository

import com.jort.stockcontrolpm.data.remote.api.FakeStoreApiService
import com.jort.stockcontrolpm.data.remote.dto.FakeStoreProductDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApiInfoRepository(
    private val apiService: FakeStoreApiService
) {
    suspend fun getExternalProducts(): List<FakeStoreProductDto> {
        return withContext(Dispatchers.IO) {
            apiService.getProducts()
        }
    }
}

