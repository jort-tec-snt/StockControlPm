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
import com.jort.stockcontrolpm.data.local.dao.UserDao;
import com.jort.stockcontrolpm.data.local.dao.VentaDao;
import com.jort.stockcontrolpm.data.local.entity.MovementEntity;
import com.jort.stockcontrolpm.data.local.entity.ProductEntity;
import com.jort.stockcontrolpm.data.local.entity.UserEntity;
import com.jort.stockcontrolpm.data.local.entity.VentaEntity;
import com.jort.stockcontrolpm.data.local.entity.VentaItemEntity;

@Database(
        entities = {
            ProductEntity.class,
            MovementEntity.class,
            UserEntity.class,
            VentaEntity.class,
            VentaItemEntity.class
        },
        version = 4,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract ProductDao  productDao();
    public abstract MovementDao movementDao();
    public abstract UserDao     userDao();
    public abstract VentaDao    ventaDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE products ADD COLUMN purchasePrice REAL");
            db.execSQL("ALTER TABLE products ADD COLUMN sku TEXT NOT NULL DEFAULT ''");
            db.execSQL("ALTER TABLE products ADD COLUMN supplier TEXT");
            db.execSQL("ALTER TABLE products ADD COLUMN description TEXT");
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

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "name TEXT, " +
                "email TEXT, " +
                "passwordHash TEXT, " +
                "role TEXT, " +
                "createdAt INTEGER NOT NULL DEFAULT 0)"
            );
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_users_email ON users(email)");
        }
    };

    // Migración 3→4: campos Tabla 21 en products, ventaId en movements,
    // nuevas tablas ventas y venta_items (Tablas 22 y 22b del PDF)
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            // Campos nuevos en products
            db.execSQL("ALTER TABLE products ADD COLUMN codigoBarras TEXT");
            db.execSQL("ALTER TABLE products ADD COLUMN imagenUrl TEXT");
            db.execSQL("ALTER TABLE products ADD COLUMN visible INTEGER NOT NULL DEFAULT 1");
            db.execSQL("ALTER TABLE products ADD COLUMN userId TEXT NOT NULL DEFAULT ''");

            // Campo nuevo en movements
            db.execSQL("ALTER TABLE movements ADD COLUMN ventaId TEXT");

            // Tabla ventas
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS ventas (" +
                "id TEXT PRIMARY KEY NOT NULL, " +
                "cajeroUid TEXT NOT NULL, " +
                "fecha INTEGER NOT NULL, " +
                "total REAL NOT NULL, " +
                "metodoPago TEXT NOT NULL, " +
                "referenciaPago TEXT, " +
                "sincronizado INTEGER NOT NULL DEFAULT 0, " +
                "userId TEXT NOT NULL DEFAULT '')"
            );

            // Tabla venta_items
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS venta_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "ventaId TEXT NOT NULL, " +
                "productId INTEGER NOT NULL, " +
                "productName TEXT NOT NULL, " +
                "qty INTEGER NOT NULL, " +
                "unitPrice REAL NOT NULL, " +
                "FOREIGN KEY(ventaId) REFERENCES ventas(id) ON DELETE CASCADE)"
            );
            db.execSQL("CREATE INDEX IF NOT EXISTS index_venta_items_ventaId ON venta_items(ventaId)");
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build();
                }
            }
        }
        return instance;
    }
}
