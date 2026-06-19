package com.jort.stockcontrolpm.data.repository

import com.jort.stockcontrolpm.data.local.dao.ProductDao
import com.jort.stockcontrolpm.domain.model.ExternalProduct
import com.jort.stockcontrolpm.domain.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ProductRepository(
    private val productDao: ProductDao
) {
    fun observeProducts(): Flow<List<Product>> {
        return productDao.observeProducts()
            .map { products -> products.map { product -> product.toDomain() } }
    }

    fun searchProducts(query: String): Flow<List<Product>> {
        return productDao.searchProducts(query)
            .map { products -> products.map { product -> product.toDomain() } }
    }

    suspend fun getProductById(productId: Long): Product? {
        return withContext(Dispatchers.IO) {
            productDao.getProductById(productId)?.toDomain()
        }
    }

    suspend fun createProduct(product: Product): Long {
        return withContext(Dispatchers.IO) {
            productDao.insertProduct(product.toEntity())
        }
    }

    suspend fun updateProduct(product: Product) {
        withContext(Dispatchers.IO) {
            productDao.updateProduct(product.toEntity())
        }
    }

    suspend fun saveProduct(product: Product): Long {
        return if (product.id == 0L) {
            createProduct(product)
        } else {
            updateProduct(product)
            product.id
        }
    }

    suspend fun deleteProduct(product: Product) {
        withContext(Dispatchers.IO) {
            productDao.deleteProduct(product.toEntity())
        }
    }

    suspend fun deleteProductById(productId: Long) {
        withContext(Dispatchers.IO) {
            productDao.deleteProductById(productId)
        }
    }

    suspend fun countProducts(): Int {
        return withContext(Dispatchers.IO) {
            productDao.countProducts()
        }
    }

    /**
     * Persiste productos de la API que todavía no existen localmente.
     * El SKU remoto estable evita duplicarlos en cada sincronización.
     */
    suspend fun importExternalProducts(externalProducts: List<ExternalProduct>): Int {
        return withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            var importedCount = 0

            externalProducts.forEach { external ->
                val sku = "EXT-${external.id}"
                if (productDao.getProductBySku(sku) == null) {
                    productDao.insertProduct(
                        Product(
                            id = 0L,
                            name = external.title,
                            category = external.category.replaceFirstChar { it.uppercase() },
                            stock = 0,
                            minStock = 5,
                            unitPrice = external.price,
                            purchasePrice = null,
                            sku = sku,
                            supplier = "FakeStore API",
                            expirationDate = null,
                            description = external.description,
                            createdAt = now,
                            updatedAt = now
                        ).toEntity()
                    )
                    importedCount++
                }
            }

            importedCount
        }
    }
}
