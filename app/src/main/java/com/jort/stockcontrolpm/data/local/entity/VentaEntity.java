package com.jort.stockcontrolpm.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Tabla 22b del PDF (Diseño de datos) — Venta.
 * Se persiste en Room para uso offline y se marca sincronizado=false
 * hasta que se suba a Firestore (integración futura).
 */
@Entity(tableName = "ventas")
public class VentaEntity {

    // UUID generado en la capa de repositorio antes de insertar
    @PrimaryKey
    @NonNull
    public String id;

    // UID del cajero que registró la venta
    @NonNull
    public String cajeroUid;

    // Epoch millis al momento del checkout
    public long fecha;

    public double total;

    // "EFECTIVO" | "TARJETA" | "YAPE" | etc.
    @NonNull
    public String metodoPago;

    // Número de operación / referencia (opcional)
    @Nullable
    public String referenciaPago;

    // false mientras no se sincronice con Firestore
    public boolean sincronizado = false;

    // UID del propietario de la tienda (para Firestore multi-tenant futuro)
    @NonNull
    public String userId;

    public VentaEntity(
            @NonNull String id,
            @NonNull String cajeroUid,
            long fecha,
            double total,
            @NonNull String metodoPago,
            @Nullable String referenciaPago,
            boolean sincronizado,
            @NonNull String userId
    ) {
        this.id = id;
        this.cajeroUid = cajeroUid;
        this.fecha = fecha;
        this.total = total;
        this.metodoPago = metodoPago;
        this.referenciaPago = referenciaPago;
        this.sincronizado = sincronizado;
        this.userId = userId;
    }
}
