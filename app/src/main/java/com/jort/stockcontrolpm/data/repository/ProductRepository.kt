package com.jort.stockcontrolpm.data.repository

import com.jort.stockcontrolpm.data.local.dao.ProductDao
import com.jort.stockcontrolpm.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
        return productDao.getProductById(productId)?.toDomain()
    }

    suspend fun createProduct(product: Product): Long {
        return productDao.insertProduct(product.toEntity())
    }

    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product.toEntity())
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
        productDao.deleteProduct(product.toEntity())
    }

    suspend fun deleteProductById(productId: Long) {
        productDao.deleteProductById(productId)
    }
}

