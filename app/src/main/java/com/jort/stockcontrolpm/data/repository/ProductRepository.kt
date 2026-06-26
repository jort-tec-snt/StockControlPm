package com.jort.stockcontrolpm.data.repository

import android.util.Log
import com.jort.stockcontrolpm.data.local.dao.ProductDao
import com.jort.stockcontrolpm.domain.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * ProductRepository
 *
 * Fuente única de verdad para productos. Implementa dual-write:
 *   1. Room (local) — siempre se escribe primero, funciona offline
 *   2. Firestore (nube) — se sincroniza en background, no bloquea la UI
 *
 * Si Firestore falla (sin internet), Room ya tiene los datos → app sigue funcionando.
 * La sincronización con Firestore usa runCatching para no propagar errores de red a la UI.
 */
class ProductRepository(
    private val productDao: ProductDao,
    // Servicios opcionales: tienen valores por defecto para no romper código existente
    private val firestoreService: FirestoreService = FirestoreService(),
    private val authRepository: FirebaseAuthRepository = FirebaseAuthRepository()
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
            // Asignar el UID del usuario autenticado al producto
            val uid = authRepository.getCurrentUid()
            val productWithUser = product.copy(userId = uid)
            val newId = productDao.insertProduct(productWithUser.toEntity())

            // Sincronizar en Firestore: el ID definitivo lo asigna Room (autoincrement)
            val savedProduct = productWithUser.copy(id = newId)
            Log.d("Firestore", "Guardando producto id=$newId uid='$uid'")
            runCatching { firestoreService.saveProduct(uid, savedProduct) }
                .onSuccess { Log.d("Firestore", "Producto $newId guardado en Firestore OK") }
                .onFailure { e -> Log.e("Firestore", "ERROR al guardar en Firestore: ${e.message}", e) }

            newId
        }
    }

    suspend fun updateProduct(product: Product) {
        withContext(Dispatchers.IO) {
            val uid = authRepository.getCurrentUid()
            val productWithUser = product.copy(userId = uid)
            productDao.updateProduct(productWithUser.toEntity())

            // Sincronizar actualización en Firestore
            runCatching { firestoreService.saveProduct(uid, productWithUser) }
        }
    }

    /**
     * Crea o actualiza un producto según si su id es 0 (nuevo) o no (existente).
     * Devuelve el ID del producto guardado (útil para disparar notificación de stock bajo).
     */
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
            val uid = authRepository.getCurrentUid()
            productDao.deleteProduct(product.toEntity())
            // Eliminar también de Firestore para mantener sincronía
            runCatching { firestoreService.deleteProduct(uid, product.id) }
        }
    }

    suspend fun deleteProductById(productId: Long) {
        withContext(Dispatchers.IO) {
            val uid = authRepository.getCurrentUid()
            productDao.deleteProductById(productId)
            runCatching { firestoreService.deleteProduct(uid, productId) }
        }
    }

    suspend fun getAllProducts(): List<Product> {
        return withContext(Dispatchers.IO) {
            productDao.getAllProducts().map { it.toDomain() }
        }
    }

    suspend fun countProducts(): Int {
        return withContext(Dispatchers.IO) {
            productDao.countProducts()
        }
    }
}
