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
    public String expirationDate;

    public long createdAt;
    public long updatedAt;

    public ProductEntity(
            long id,
            @NonNull String name,
            @NonNull String category,
            int stock,
            int minStock,
            double unitPrice,
            @Nullable String expirationDate,
            long createdAt,
            long updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.stock = stock;
        this.minStock = minStock;
        this.unitPrice = unitPrice;
        this.expirationDate = expirationDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

