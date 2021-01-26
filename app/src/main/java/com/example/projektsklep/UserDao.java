package com.example.projektsklep;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Update
    public void update(User user);

    @Delete
    public void delete(User user);

    @Query("DELETE FROM user")
    public void deleteAll();

    @Query("SELECT * FROM user ORDER BY email")
    public LiveData<List<User>> findAll();

    @Query("SELECT * FROM user WHERE email = :email COLLATE NOCASE")
    public User findUserByEmail(String email);

    @Query("SELECT * FROM user WHERE email = :email COLLATE NOCASE AND password = :password")
    public User findUserByCredentials(String email, String password);

    @Query("SELECT * FROM user WHERE email LIKE :text OR firstName LIKE :text OR lastName LIKE :text")
    public List<User> findUserByNameOrEmail(String text);
}
