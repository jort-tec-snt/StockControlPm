package com.jort.stockcontrolpm.data.local

import com.jort.stockcontrolpm.data.repository.ApiInfoRepository
import com.jort.stockcontrolpm.data.repository.ProductRepository
import com.jort.stockcontrolpm.domain.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseSeeder {

    suspend fun seedIfEmpty(
        productRepository: ProductRepository,
        apiInfoRepository: ApiInfoRepository
    ) {
        withContext(Dispatchers.IO) {
            if (productRepository.countProducts() > 0) {
                patchImagesFromApi(productRepository, apiInfoRepository)
                return@withContext
            }
            seedFromApi(productRepository, apiInfoRepository)
        }
    }

    private suspend fun seedFromApi(
        repo: ProductRepository,
        api: ApiInfoRepository
    ) {
        runCatching { api.getExternalProducts() }.getOrNull()?.forEachIndexed { index, external ->
            val now = System.currentTimeMillis()
            // 0,1 → agotado | 2,3,4 → stock bajo | resto → normal
            val stock = when (index) {
                0, 1    -> 0
                2, 3, 4 -> (1..4).random()
                else    -> (10..50).random()
            }
            repo.saveProduct(
                Product(
                    id             = 0L,
                    name           = external.title,
                    category       = external.category.replaceFirstChar { it.uppercase() },
                    stock          = stock,
                    minStock       = 5,
                    unitPrice      = external.price,
                    purchasePrice  = external.price * 0.75,
                    sku            = "API-${external.id}",
                    supplier       = "FakeStore API",
                    expirationDate = null,
                    description    = external.description,
                    imagenUrl      = external.imageUrl,
                    createdAt      = now,
                    updatedAt      = now
                )
            )
        }
    }

    // Reemplaza imágenes vacías O de picsum por imágenes reales de la API
    private suspend fun patchImagesFromApi(
        repo: ProductRepository,
        api: ApiInfoRepository
    ) {
        val necesitan = repo.getAllProducts().filter {
            it.imagenUrl.isNullOrBlank() || it.imagenUrl!!.contains("picsum")
        }
        if (necesitan.isEmpty()) return

        val externos = runCatching { api.getExternalProducts() }.getOrNull() ?: return

        necesitan.forEachIndexed { index, product ->
            val externo = externos[index % externos.size]
            repo.updateProduct(product.copy(imagenUrl = externo.imageUrl))
        }
    }
}
