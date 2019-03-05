package com.gmail.kramarenko104.dao;

import com.gmail.kramarenko104.model.Cart;
import com.gmail.kramarenko104.model.Product;

import java.util.Map;

public interface CartDao {

    void addProduct(int userId, int productId, int quantity);

    void removeProduct(int userId, int productId, int quantity);

    Map<Product, Integer> getAllProducts(int userId);

    void deleteCart(int userId);



}
