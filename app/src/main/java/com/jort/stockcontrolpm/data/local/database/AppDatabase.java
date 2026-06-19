package com.jort.stockcontrolpm.data.local.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.jort.stockcontrolpm.data.local.dao.MovementDao;
import com.jort.stockcontrolpm.data.local.dao.ProductDao;
import com.jort.stockcontrolpm.data.local.entity.MovementEntity;
import com.jort.stockcontrolpm.data.local.entity.ProductEntity;

@Database(
        entities = {ProductEntity.class, MovementEntity.class},
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract ProductDao productDao();
    public abstract MovementDao movementDao();

    // Migración 1→2: nuevos campos en products + tabla movements
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            // Agregar columnas nuevas a products (nullable para compatibilidad)
            db.execSQL("ALTER TABLE products ADD COLUMN purchasePrice REAL");
            db.execSQL("ALTER TABLE products ADD COLUMN sku TEXT NOT NULL DEFAULT ''");
            db.execSQL("ALTER TABLE products ADD COLUMN supplier TEXT");
            db.execSQL("ALTER TABLE products ADD COLUMN description TEXT");

            // Crear tabla movements
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS movements (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "productId INTEGER NOT NULL, " +
                "productName TEXT NOT NULL, " +
                "type TEXT NOT NULL, " +
                "qty INTEGER NOT NULL, " +
                "reason TEXT NOT NULL, " +
                "date INTEGER NOT NULL, " +
                "userId TEXT NOT NULL, " +
                "notes TEXT, " +
                "FOREIGN KEY(productId) REFERENCES products(id) ON DELETE CASCADE)"
            );
            db.execSQL("CREATE INDEX IF NOT EXISTS index_movements_productId ON movements(productId)");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "stockcontrol_pm.db"
                    )
                    .addMigrations(MIGRATION_1_2)
                    .build();
                }
            }
        }
        return instance;
    }
}
