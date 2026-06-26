package com.jort.stockcontrolpm.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.jort.stockcontrolpm.domain.model.Product
import kotlinx.coroutines.tasks.await

/**
 * FirestoreService
 *
 * Encapsula todas las operaciones de lectura/escritura en Cloud Firestore.
 *
 * ── Estructura de Firestore ────────────────────────────────────────────────────
 *
 *   usuarios/
 *     {uid}/                  ← UID del usuario Firebase (cada usuario su "espacio")
 *       productos/
 *         {productId}/        ← Documento con los datos del producto
 *
 * ¿Por qué esta estructura?
 * → Separación de datos: cada usuario solo puede leer/escribir bajo su propio UID
 * → Reglas de seguridad Firestore pueden validar: request.auth.uid == resource.data.userId
 * → Permite que múltiples usuarios usen la misma app con datos completamente independientes
 *
 * ── Modelo de datos (dual write) ──────────────────────────────────────────────
 * La app usa Room como fuente de verdad LOCAL (funciona offline).
 * Firestore es la fuente de verdad EN LA NUBE (sincronización entre dispositivos).
 * Cuando hay conexión: Room + Firestore | Sin conexión: solo Room.
 */
class FirestoreService {

    // FirebaseFirestore.getInstance(): instancia singleton del SDK de Firestore
    private val db = FirebaseFirestore.getInstance()

    // ── Ruta base para un usuario específico ─────────────────────────────────
    // usuarios/{uid}/productos → colección de productos del usuario uid
    private fun productosCollection(uid: String) =
        db.collection("usuarios").document(uid).collection("productos")

    // ── GUARDAR / ACTUALIZAR ──────────────────────────────────────────────────

    /**
     * Guarda o actualiza un producto en Firestore bajo el UID del usuario.
     *
     * SetOptions.merge(): si el documento ya existe, solo actualiza los campos
     * presentes en el mapa (no borra campos ausentes). Si no existe, lo crea.
     *
     * @param uid     UID de Firebase del usuario dueño del producto
     * @param product Producto a sincronizar
     */
    suspend fun saveProduct(uid: String, product: Product) {
        if (uid.isBlank()) return  // Seguridad: no escribir sin UID válido

        // Asegura que el documento del usuario exista antes de escribir la subcolección.
        // Sin esto Firestore crea documentos "fantasma" que no aparecen en consultas.
        db.collection("usuarios").document(uid)
            .set(mapOf("uid" to uid), SetOptions.merge())
            .await()

        // Convertimos el Product a un Map para Firestore
        // Firestore almacena documentos como pares clave-valor
        val data = mapOf(
            "id"             to product.id,
            "name"           to product.name,
            "category"       to product.category,
            "stock"          to product.stock,
            "minStock"       to product.minStock,
            "unitPrice"      to product.unitPrice,
            "expirationDate" to product.expirationDate,
            "userId"         to uid,
            "createdAt"      to product.createdAt,
            "updatedAt"      to product.updatedAt
        )

        // El ID del documento en Firestore es el mismo que el ID en Room
        // para mantener consistencia entre ambas fuentes de datos
        productosCollection(uid)
            .document(product.id.toString())
            .set(data, SetOptions.merge())  // merge: no borra campos que no estén en data
            .await()
    }

    // ── ELIMINAR ──────────────────────────────────────────────────────────────

    /**
     * Elimina un producto de Firestore.
     * Se llama junto con la eliminación en Room para mantener sincronía.
     */
    suspend fun deleteProduct(uid: String, productId: Long) {
        if (uid.isBlank()) return

        productosCollection(uid)
            .document(productId.toString())
            .delete()
            .await()
    }

    // ── LEER ──────────────────────────────────────────────────────────────────

    /**
     * Obtiene todos los productos del usuario desde Firestore.
     *
     * Usado para sincronización inicial: al hacer login, cargamos
     * los productos de Firestore y los guardamos en Room local.
     *
     * @return Lista de mapas con los datos de cada producto
     */
    suspend fun getProducts(uid: String): List<Map<String, Any>> {
        if (uid.isBlank()) return emptyList()

        // get(): trae todos los documentos de la colección (one-time read)
        // Para escucha en tiempo real usaríamos addSnapshotListener()
        val snapshot = productosCollection(uid).get().await()

        return snapshot.documents.mapNotNull { it.data }
    }
}
