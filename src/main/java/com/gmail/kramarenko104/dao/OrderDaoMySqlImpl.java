package com.gmail.kramarenko104.dao;

import com.gmail.kramarenko104.model.Order;
import com.gmail.kramarenko104.model.Product;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class OrderDaoMySqlImpl implements OrderDao {

    private final static String CREATE_ORDER = "INSERT INTO orders (orderNumber, userId, productId, quantity, status) VALUES(?,?,?,?,?);";
    private final static String DELETE_ALL_ORDERS_BY_USERID = "DELETE FROM orders WHERE userId = ?;";
    private final static String GET_LAST_ORDER_NUMBER= "SELECT DISTINCT max(orderNumber) as lastOrderNumber FROM orders;";
    private final static String GET_ALL_ORDERS_BY_USER_ID = "SELECT o.orderNumber, o.status, c.id AS cartId, c.userId, p.id AS productId, p.name, p.price, c.quantity, " +
            "FROM carts AS c INNER JOIN orders AS o ON o.cartId = c.id " +
            "INNER JOIN products AS p ON p.id == c.productId" +
            " WHERE c.userId = ?;";
    private final static String PROCESSED_ORDER = "ordered";
    private static Logger logger = Logger.getLogger(OrderDaoMySqlImpl.class);
    private Connection conn;


    public OrderDaoMySqlImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Order createOrder(int userId, Map<Product, Integer> products) {

        // generate order number (orderNumber) is not auto-increment in 'orders' table
        // (one order can have many products ==> many rows can have the same order number)
        logger.debug("OrderDAO.createOrder:...enter....");
        int lastOrderNumber = 0;
        ResultSet rs = null;
        try (Statement pst = conn.createStatement()) {
            rs = pst.executeQuery(GET_LAST_ORDER_NUMBER);
            while (rs.next()) {
                lastOrderNumber = rs.getInt("lastOrderNumber");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
        }
        ++lastOrderNumber;

        // create new order
        int totalSum = 0;
        int itemsCount = 0;
        for(Map.Entry<Product, Integer> entry: products.entrySet()) {
            itemsCount += entry.getValue();
            totalSum += entry.getValue() * entry.getKey().getPrice();
            try (PreparedStatement pst = conn.prepareStatement(CREATE_ORDER)) {
                conn.setAutoCommit(false);
                pst.setInt(1, lastOrderNumber);
                pst.setInt(2, userId);
                pst.setInt(3, entry.getKey().getId()); // productId
                pst.setInt(4, entry.getValue()); // quantity
                pst.setString(5, PROCESSED_ORDER);
                pst.execute();
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // prepare created order to return
        Order newOrder = new Order();
        newOrder.setOrderNumber(lastOrderNumber);
        newOrder.setStatus(PROCESSED_ORDER);
        newOrder.setUserId(userId);
        newOrder.setProducts(products);
        newOrder.setTotalSum(totalSum);
        newOrder.setItemsCount(itemsCount);
        logger.debug("OrderDAO.createOrder:...new Order was created with orderNumber = " + lastOrderNumber);
        return newOrder;
    }

    @Override
    public void deleteAllOrders(int userId) {
        try (PreparedStatement pst = conn.prepareStatement(DELETE_ALL_ORDERS_BY_USERID)) {
            pst.setInt(1, userId);
            pst.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Order> getAllOrders(int userId) {
        ResultSet rs = null;
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(GET_ALL_ORDERS_BY_USER_ID)) {
            pst.setInt(1, userId);
            rs = pst.executeQuery();
            while (rs.next()) {
//                Product product = new Product();
//                product.setOrderNumber(rs.getInt("productId"));
//                product.setName(rs.getString("name"));
//                int price = rs.getInt("price");
//                product.setPrice(price);
//                int quantity = rs.getInt("quantity");
//                productsMap.put(product, quantity);
//                itemsCount += quantity;
//                totalSum += quantity * price;
//                if (orderId != 0) orderId = rs.getInt("orderId");
//                if (userId != 0) userId = rs.getInt("userId");
//                if (status.equals("")) status = rs.getString("status");
//
//                Order order = new Order();
//                order.setOrderNumber(rs.getInt("orderId"));
//                order.setCart(rs.getString("name"));
//                order.setStatus(rs.getString("status"));
//                int quantity = rs.getInt("quantity");
//                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(rs);
        }
        return orders;
    }


    @Override
    public Order getOrder(int orderId) {
        return null;
    }

    private void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
            }
        }
    }
}
