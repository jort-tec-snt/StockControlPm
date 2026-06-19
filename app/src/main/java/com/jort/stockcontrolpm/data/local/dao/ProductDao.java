package com.jort.stockcontrolpm.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.annotation.Nullable;

import com.jort.stockcontrolpm.data.local.entity.ProductEntity;

import java.util.List;

import kotlinx.coroutines.flow.Flow;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    Flow<List<ProductEntity>> observeProducts();

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    ProductEntity getProductById(long productId);

    @Nullable
    @Query("SELECT * FROM products WHERE sku = :sku LIMIT 1")
    ProductEntity getProductBySku(String sku);

    @Query(
            "SELECT * FROM products " +
                    "WHERE name LIKE '%' || :query || '%' " +
                    "OR category LIKE '%' || :query || '%' " +
                    "ORDER BY name ASC"
    )
    Flow<List<ProductEntity>> searchProducts(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertProduct(ProductEntity product);

    @Update
    void updateProduct(ProductEntity product);

    @Delete
    void deleteProduct(ProductEntity product);

    @Query("DELETE FROM products WHERE id = :productId")
    void deleteProductById(long productId);

    @Query("SELECT COUNT(*) FROM products")
    int countProducts();
}

