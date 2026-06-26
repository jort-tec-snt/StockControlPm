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

    @Nullable
    public Double purchasePrice;

    @NonNull
    public String sku;

    @Nullable
    public String supplier;

    @Nullable
    public String expirationDate;

    @Nullable
    public String description;

    // Campos Tabla 21 del PDF (Diseño de datos)
    @Nullable
    public String codigoBarras;

    @Nullable
    public String imagenUrl;

    public boolean visible = true;

    @NonNull
    public String userId = "";

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
            @Nullable String codigoBarras,
            @Nullable String imagenUrl,
            boolean visible,
            @NonNull String userId,
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
        this.codigoBarras = codigoBarras;
        this.imagenUrl = imagenUrl;
        this.visible = visible;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
