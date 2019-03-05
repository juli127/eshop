package com.gmail.kramarenko104.model;

import java.util.HashMap;
import java.util.Map;

public class Cart {

    private int userId;

    private Map<Product, Integer> products;

    public Cart(int userId) {
        this.userId = userId;
        this.products = new HashMap<>();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }

    public void setProducts(Map<Product, Integer> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Cart: [");
        products.forEach((product, quantity) -> result.append("(product : " + product + ", quantity : " + quantity + ")"));
        return result.append("]").toString();
    }
}
