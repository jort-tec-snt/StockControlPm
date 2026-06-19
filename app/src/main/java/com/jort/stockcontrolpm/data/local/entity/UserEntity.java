package com.jort.stockcontrolpm.data.local.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "users",
    indices = { @Index(value = "email", unique = true) }
)
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String email;
    public String passwordHash;   // SHA-256 del password
    public String role;           // "OWNER" | "CASHIER"
    public long   createdAt;
}
