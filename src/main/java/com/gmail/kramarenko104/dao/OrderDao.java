package com.gmail.kramarenko104.dao;

import com.gmail.kramarenko104.model.Order;
import com.gmail.kramarenko104.model.Product;
import java.util.List;
import java.util.Map;

public interface OrderDao {

    Order getOrder(int orderNumber);

    void deleteAllOrders(int userId);

    List<Order> getAllOrders (int userId);

    Order createOrder(int userId, Map<Product, Integer> products);
}