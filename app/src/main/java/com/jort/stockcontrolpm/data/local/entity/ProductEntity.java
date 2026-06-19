package com.jort.stockcontrolpm.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class ProductEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String name;

    @NonNull
    public String category;

    public int stock;
    public int minStock;
    public double unitPrice;

    // Precio de compra (opcional); null si no se registra
    @Nullable
    public Double purchasePrice;

    // Código único por producto (SKU)
    @NonNull
    public String sku;

    // Proveedor (opcional)
    @Nullable
    public String supplier;

    @Nullable
    public String expirationDate;

    @Nullable
    public String description;

    public long createdAt;
    public long updatedAt;

    public ProductEntity(
            long id,
            @NonNull String name,
            @NonNull String category,
            int stock,
            int minStock,
            double unitPrice,
            @Nullable Double purchasePrice,
            @NonNull String sku,
            @Nullable String supplier,
            @Nullable String expirationDate,
            @Nullable String description,
            long createdAt,
            long updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.stock = stock;
        this.minStock = minStock;
        this.unitPrice = unitPrice;
        this.purchasePrice = purchasePrice;
        this.sku = sku;
        this.supplier = supplier;
        this.expirationDate = expirationDate;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
