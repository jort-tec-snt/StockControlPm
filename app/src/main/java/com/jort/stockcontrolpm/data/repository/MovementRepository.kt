package com.jort.stockcontrolpm.data.repository

import com.jort.stockcontrolpm.data.local.dao.MovementDao
import com.jort.stockcontrolpm.domain.model.Movement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MovementRepository(
    private val movementDao: MovementDao
) {
    fun observeRecent(limit: Int = 10): Flow<List<Movement>> {
        return movementDao.observeRecent(limit)
            .map { list -> list.map { it.toDomain() } }
    }

    suspend fun getRecentByProduct(productId: Long, limit: Int = 5): List<Movement> {
        return withContext(Dispatchers.IO) {
            movementDao.getRecentByProduct(productId, limit).map { it.toDomain() }
        }
    }

    suspend fun register(movement: Movement): Long {
        return withContext(Dispatchers.IO) {
            movementDao.insertMovement(movement.toEntity())
        }
    }
}
