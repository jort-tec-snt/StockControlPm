package com.jort.stockcontrolpm.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.jort.stockcontrolpm.data.local.entity.UserEntity;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertUser(UserEntity user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND passwordHash = :hash LIMIT 1")
    UserEntity login(String email, String hash);

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int emailExists(String email);
}
