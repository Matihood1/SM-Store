package com.example.projektsklep;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Product product);

    @Update
    public void update(Product product);

    @Delete
    public void delete(Product product);

    @Query("DELETE FROM product")
    public void deleteAll();

    @Query("SELECT * FROM product ORDER BY name")
    public LiveData<List<Product>> findAll();

    @Query("SELECT * FROM product WHERE name LIKE :text")
    public List<Product> findProductWithName(String text);

    @Query("SELECT * FROM product WHERE name LIKE :text OR description LIKE :text")
    public List<Product> findProductWithNameOrDescription(String text);

    @Query("SELECT * FROM product WHERE id = :id")
    public LiveData<Product> findViewById(int id);
}
