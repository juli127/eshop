package com.gmail.kramarenko104.factoryDao;

import com.gmail.kramarenko104.dao.*;
import org.apache.log4j.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MySqlDaoFactory extends DaoFactory {

    private static Logger logger = Logger.getLogger(MySqlDaoFactory.class);
    UserDaoMySqlImpl userDaoMySqlImpl;
    ProductDaoMySqlImpl productDaoMySqlImpl;
    CartDaoMySqlImpl cartDaoMySqlImpl;
    OrderDaoMySqlImpl orderDaoMySqlImpl;
    Connection conn;

    public MySqlDaoFactory() {
        ResourceBundle config = ResourceBundle.getBundle("db");
        try {
            Class.forName(config.getString("driverClassName")).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        logger.debug("Connecting...");
        try {
            StringBuilder connStr = new StringBuilder();
            connStr.append(config.getString("url"))
                    .append("/").append(config.getString("dbName"))
                    .append("?").append("user=").append(config.getString("usr"))
                    .append("&password=").append(config.getString("password"));
            logger.debug("Connection string:" + connStr.toString());

            conn = DriverManager.getConnection(connStr.toString());
            logger.debug("Connection obtained");

        } catch (SQLException ex) {
            logger.debug("Connection to DB failed...");
            logger.debug("SQLException: " + ex.getMessage());
            logger.debug("SQLState: " + ex.getSQLState());
            logger.debug("VendorError: " + ex.getErrorCode());
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

    @Override
    public void closeConnection() {
        try {
            if (conn != null)
                conn.close();
            logger.debug("Connection to DB was closed");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
