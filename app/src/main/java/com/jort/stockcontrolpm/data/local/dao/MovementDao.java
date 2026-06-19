package com.jort.stockcontrolpm.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.jort.stockcontrolpm.data.local.entity.MovementEntity;

import java.util.List;

import kotlinx.coroutines.flow.Flow;

@Dao
public interface MovementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertMovement(MovementEntity movement);

    @Query("SELECT * FROM movements ORDER BY date DESC")
    Flow<List<MovementEntity>> observeAll();

    @Query("SELECT * FROM movements WHERE productId = :productId ORDER BY date DESC LIMIT :limit")
    List<MovementEntity> getRecentByProduct(long productId, int limit);

    @Query("SELECT * FROM movements ORDER BY date DESC LIMIT :limit")
    Flow<List<MovementEntity>> observeRecent(int limit);

    @Query("DELETE FROM movements WHERE productId = :productId")
    void deleteByProduct(long productId);
}
