package com.jort.stockcontrolpm.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Línea de detalle de una venta (ítems del carrito al momento del checkout).
 * FK a VentaEntity — se elimina en cascada si se borra la venta padre.
 */
@Entity(
    tableName = "venta_items",
    foreignKeys = @ForeignKey(
        entity = VentaEntity.class,
        parentColumns = "id",
        childColumns = "ventaId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("ventaId")}
)
public class VentaItemEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String ventaId;

    public long productId;

    // Snapshot del nombre al momento de la venta
    @NonNull
    public String productName;

    public int qty;

    // Precio unitario de venta al momento del checkout
    public double unitPrice;

    public VentaItemEntity(
            long id,
            @NonNull String ventaId,
            long productId,
            @NonNull String productName,
            int qty,
            double unitPrice
    ) {
        this.id = id;
        this.ventaId = ventaId;
        this.productId = productId;
        this.productName = productName;
        this.qty = qty;
        this.unitPrice = unitPrice;
    }
}
