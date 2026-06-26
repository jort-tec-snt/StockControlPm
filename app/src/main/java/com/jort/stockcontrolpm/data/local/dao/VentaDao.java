package com.jort.stockcontrolpm.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.jort.stockcontrolpm.data.local.entity.VentaEntity;
import com.jort.stockcontrolpm.data.local.entity.VentaItemEntity;

import java.util.List;

@Dao
public interface VentaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVenta(VentaEntity venta);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItems(List<VentaItemEntity> items);

    @Query("SELECT * FROM ventas ORDER BY fecha DESC")
    List<VentaEntity> getAllVentas();

    @Query("SELECT * FROM ventas WHERE id = :ventaId LIMIT 1")
    VentaEntity getVentaById(String ventaId);

    @Query("SELECT * FROM ventas WHERE fecha BETWEEN :from AND :to ORDER BY fecha DESC")
    List<VentaEntity> getVentasByDateRange(long from, long to);

    @Query("SELECT * FROM venta_items WHERE ventaId = :ventaId")
    List<VentaItemEntity> getItemsByVenta(String ventaId);

    @Query("SELECT * FROM ventas WHERE sincronizado = 0")
    List<VentaEntity> getVentasPendientesSync();

    @Query("UPDATE ventas SET sincronizado = 1 WHERE id = :ventaId")
    void markSincronizado(String ventaId);

    @Query("SELECT COUNT(*) FROM ventas WHERE fecha BETWEEN :from AND :to")
    int countVentasInRange(long from, long to);

    @Query("SELECT SUM(total) FROM ventas WHERE fecha BETWEEN :from AND :to")
    Double sumTotalInRange(long from, long to);
}
