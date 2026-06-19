package com.jort.stockcontrolpm.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "movements",
    foreignKeys = @ForeignKey(
        entity = ProductEntity.class,
        parentColumns = "id",
        childColumns = "productId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("productId")}
)
public class MovementEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long productId;

    // Snapshot del nombre al momento del movimiento
    @NonNull
    public String productName;

    // "IN" | "OUT" | "ADJUST"
    @NonNull
    public String type;

    public int qty;

    @NonNull
    public String reason;

    public long date;

    @NonNull
    public String userId;

    @Nullable
    public String notes;

    public MovementEntity(
            long id,
            long productId,
            @NonNull String productName,
            @NonNull String type,
            int qty,
            @NonNull String reason,
            long date,
            @NonNull String userId,
            @Nullable String notes
    ) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.type = type;
        this.qty = qty;
        this.reason = reason;
        this.date = date;
        this.userId = userId;
        this.notes = notes;
    }
}
