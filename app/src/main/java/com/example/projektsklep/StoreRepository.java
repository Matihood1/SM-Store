package com.example.projektsklep;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class StoreRepository {
    private UserDao userDao;
    private ProductDao productDao;
    private LiveData<List<User>> users;
    private LiveData<List<Product>> products;

    StoreRepository(Application application) {
        StoreDatabase database = StoreDatabase.getDatabase(application);
        userDao = database.userDao();
        productDao = database.productDao();

        users = userDao.findAll();
        products = productDao.findAll();
    }

    LiveData<List<User>> findAllUsers() { return users; }
    LiveData<List<Product>> findAllProducts() { return products; }

    User findUserByEmail(String email) {
        return userDao.findUserByEmail(email);
    }

    User findUserByCredentials(String email, String password) {
        return userDao.findUserByCredentials(email, password);
    }

    List<User> findUserWithNameOrEmail(String text) { return userDao.findUserByNameOrEmail(text); }

    LiveData<Product> findProductById(int id) {
        return productDao.findViewById(id);
    }

    List<Product> findProductWithName(String text) {
        return productDao.findProductWithName(text);
    }

    List<Product> findProductWithNameOrDescription(String text) {
        return productDao.findProductWithNameOrDescription(text);
    }

    void insert(User user) {
        StoreDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insert(user);
        });
    }

    void insert(Product product) {
        StoreDatabase.databaseWriteExecutor.execute(() -> {
            productDao.insert(product);
        });
    }

    void update(User user) {
        StoreDatabase.databaseWriteExecutor.execute(() -> {
            userDao.update(user);
        });
    }

    void update(Product product) {
        StoreDatabase.databaseWriteExecutor.execute(() -> {
            productDao.update(product);
        });
    }

    void delete(User user) {
        StoreDatabase.databaseWriteExecutor.execute(() -> {
            userDao.delete(user);
        });
    }

    void delete(Product product) {
        StoreDatabase.databaseWriteExecutor.execute(() -> {
            productDao.delete(product);
        });
    }
}
