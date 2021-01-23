package com.example.projektsklep;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class StoreViewModel extends AndroidViewModel {
    private StoreRepository storeRepository;

    private LiveData<List<User>> users;
    private LiveData<List<Product>> products;

    public StoreViewModel(@NonNull Application application) {
        super(application);
        storeRepository = new StoreRepository(application);
        users = storeRepository.findAllUsers();
        products = storeRepository.findAllProducts();
    }

    public User findUserByEmail(String email) {
        return storeRepository.findUserByEmail(email);
    }
    public User findUserByCredentials(String email, String password) {
        return storeRepository.findUserByCredentials(email, password);
    }
    public LiveData<Product> findProductById(int id) {
        return storeRepository.findProductById(id);
    }
    public List<Product> findProductWithName(String text) {
        return storeRepository.findProductWithName(text);
    }
    public List<Product> findProductWithNameOrDescription(String text) {
        return storeRepository.findProductWithNameOrDescription(text);
    }

    public LiveData<List<User>> findAllUsers() {
        return users;
    }
    public LiveData<List<Product>> findAllProducts() {
        return products;
    }

    public void insert(User user) {
        storeRepository.insert(user);
    }
    public void insert(Product product) {
        storeRepository.insert(product);
    }

    public void update(User user) {
        storeRepository.update(user);
    }
    public void update(Product product) {
        storeRepository.update(product);
    }

    public void delete(User user) {
        storeRepository.delete(user);
    }
    public void delete(Product product) {
        storeRepository.delete(product);
    }
}
