package com.jort.stockcontrolpm.data.repository

import com.jort.stockcontrolpm.data.remote.api.FakeStoreApiService
import com.jort.stockcontrolpm.data.remote.dto.FakeStoreProductDto
import com.jort.stockcontrolpm.domain.model.ExternalProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApiInfoRepository(
    private val apiService: FakeStoreApiService
) {
    // El DTO queda confinado aquí; las capas superiores solo ven ExternalProduct
    suspend fun getExternalProducts(): List<ExternalProduct> {
        return withContext(Dispatchers.IO) {
            apiService.getProducts().map { it.toDomain() }
        }
    }

    private fun FakeStoreProductDto.toDomain() = ExternalProduct(
        id          = id.toInt(),
        title       = title,
        price       = price,
        category    = category,
        description = description,
        imageUrl    = ""   // FakeStore no incluye URL directa en el DTO actual
    )
}
