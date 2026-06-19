package com.jort.stockcontrolpm.data.repository

import com.jort.stockcontrolpm.data.local.dao.UserDao
import com.jort.stockcontrolpm.data.local.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class UserRepository(private val userDao: UserDao) {

    suspend fun register(name: String, email: String, password: String, role: String): Result<Long> {
        return withContext(Dispatchers.IO) {
            runCatching {
                if (userDao.emailExists(email.trim().lowercase()) > 0) {
                    error("Ya existe una cuenta con ese correo.")
                }
                val entity = UserEntity().apply {
                    this.name         = name.trim()
                    this.email        = email.trim().lowercase()
                    this.passwordHash = sha256(password)
                    this.role         = role
                    this.createdAt    = System.currentTimeMillis()
                }
                userDao.insertUser(entity)
            }
        }
    }

    suspend fun login(email: String, password: String): UserEntity? {
        return withContext(Dispatchers.IO) {
            userDao.login(email.trim().lowercase(), sha256(password))
        }
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
