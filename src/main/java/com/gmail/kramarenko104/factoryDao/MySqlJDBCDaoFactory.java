package com.gmail.kramarenko104.factoryDao;

import com.gmail.kramarenko104.dao.*;
import org.apache.log4j.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MySqlJDBCDaoFactory extends DaoFactory {

    private static Logger logger = Logger.getLogger(MySqlJDBCDaoFactory.class);
    UserDaoMySqlImpl userDaoMySqlImpl;
    ProductDaoMySqlImpl productDaoMySqlImpl;
    CartDaoMySqlImpl cartDaoMySqlImpl;
    OrderDaoMySqlImpl orderDaoMySqlImpl;
    String connStr;
    Connection conn;

    public MySqlJDBCDaoFactory() {
        ResourceBundle config = null;
        try {
            config = ResourceBundle.getBundle("dbconfig");
        } catch (MissingResourceException e) {
            e.printStackTrace();
        }
        try {
            Class.forName(config.getString("driverClassName")).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        connStr = new StringBuilder().append(config.getString("url"))
                .append("?").append("user=").append(config.getString("username"))
                .append("&password=").append(config.getString("password")).toString();
    }

    @Override
    public void openConnection() {
        try {
            conn = DriverManager.getConnection(connStr);
            logger.debug("Connection obtained: " + conn);
        } catch (SQLException e) {
            logger.debug("Connection failed. SQLException: " + e.getMessage());
        }
    }

    @Override
    public void closeConnection() {
        try {
            if (conn != null)
                conn.close();
            logger.debug("Connection to db is closed");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public UserDao getUserDao() {
        userDaoMySqlImpl = new UserDaoMySqlImpl(conn);
        return userDaoMySqlImpl;
    }

    @Override
    public ProductDao getProductDao() {
        productDaoMySqlImpl = new ProductDaoMySqlImpl(conn);
        return productDaoMySqlImpl;
    }

    @Override
    public CartDao getCartDao() {
        cartDaoMySqlImpl = new CartDaoMySqlImpl(conn);
        return cartDaoMySqlImpl;
    }

    @Override
    public OrderDao getOrderDao() {
        orderDaoMySqlImpl = new OrderDaoMySqlImpl(conn);
        return orderDaoMySqlImpl;
    }

    @Override
    public void deleteUserDao(UserDao userDao) {
        if (userDao != null) {
            userDao = null;
        }
    }

    @Override
    public void deleteProductDao(ProductDao productDao) {
        if (productDao != null) {
            productDao = null;
        }
    }

    @Override
    public void deleteCartDao(CartDao cartDao) {
        if (cartDao != null) {
            cartDao = null;
        }
    }

    @Override
    public void deleteOrderDao(OrderDao orderDao) {
        if (orderDao != null) {
            orderDao = null;
        }
    }
}
