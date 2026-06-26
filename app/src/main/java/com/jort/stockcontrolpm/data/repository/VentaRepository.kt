package com.jort.stockcontrolpm.data.repository

import com.jort.stockcontrolpm.data.local.dao.VentaDao
import com.jort.stockcontrolpm.data.local.entity.VentaEntity
import com.jort.stockcontrolpm.data.local.entity.VentaItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

data class VentaItemInput(
    val productId: Long,
    val productName: String,
    val qty: Int,
    val unitPrice: Double
)

class VentaRepository(private val ventaDao: VentaDao) {

    suspend fun registrarVenta(
        cajeroUid: String,
        items: List<VentaItemInput>,
        metodoPago: String,
        referenciaPago: String? = null,
        userId: String = ""
    ): String = withContext(Dispatchers.IO) {
        val ventaId = UUID.randomUUID().toString()
        val total = items.sumOf { it.qty * it.unitPrice }
        val now = System.currentTimeMillis()

        val venta = VentaEntity(
            ventaId, cajeroUid, now, total, metodoPago, referenciaPago, false, userId
        )
        val ventaItems = items.map { item ->
            VentaItemEntity(
                0L, ventaId, item.productId, item.productName, item.qty, item.unitPrice
            )
        }

        ventaDao.insertVenta(venta)
        ventaDao.insertItems(ventaItems)
        ventaId
    }

    suspend fun getAllVentas(): List<VentaEntity> = withContext(Dispatchers.IO) {
        ventaDao.getAllVentas()
    }

    suspend fun getVentaById(ventaId: String): VentaEntity? = withContext(Dispatchers.IO) {
        ventaDao.getVentaById(ventaId)
    }

    suspend fun getItemsByVenta(ventaId: String): List<VentaItemEntity> = withContext(Dispatchers.IO) {
        ventaDao.getItemsByVenta(ventaId)
    }

    suspend fun getVentasByDateRange(from: Long, to: Long): List<VentaEntity> = withContext(Dispatchers.IO) {
        ventaDao.getVentasByDateRange(from, to)
    }

    suspend fun sumTotalInRange(from: Long, to: Long): Double = withContext(Dispatchers.IO) {
        ventaDao.sumTotalInRange(from, to) ?: 0.0
    }

    suspend fun countVentasInRange(from: Long, to: Long): Int = withContext(Dispatchers.IO) {
        ventaDao.countVentasInRange(from, to)
    }

    suspend fun getPendientesSync(): List<VentaEntity> = withContext(Dispatchers.IO) {
        ventaDao.getVentasPendientesSync()
    }

    suspend fun markSincronizado(ventaId: String) = withContext(Dispatchers.IO) {
        ventaDao.markSincronizado(ventaId)
    }
}
