package com.jort.stockcontrolpm.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jort.stockcontrolpm.data.local.dao.ProductDao
import com.jort.stockcontrolpm.data.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}

