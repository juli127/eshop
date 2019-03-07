package com.gmail.kramarenko104.model;

import java.util.HashMap;
import java.util.Map;

public class Cart {

    private int userId;
    private int cartSize;
    private int totalSum;
    private Map<Product, Integer> products;

    public Cart(int userId) {
        this.userId = userId;
        this.cartSize = 0;
        this.totalSum = 0;
        this.products = new HashMap<>();
    }

    public int getCartSize() {
        return cartSize;
    }

    public void setCartSize(int cartSize) {
        this.cartSize = cartSize;
    }

    public int getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(int totalSum) {
        this.totalSum = totalSum;
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
        return "Cart{" +
                "userId=" + userId +
                ", cartSize=" + cartSize +
                ", totalSum=" + totalSum +
                '}';
    }
}
