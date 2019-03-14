package com.gmail.kramarenko104.factoryDao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import com.gmail.kramarenko104.dao.*;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.log4j.Logger;
import java.sql.*;
import java.util.ResourceBundle;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.*;

public class MySQLDataSourceFactory extends DaoFactory {

    private static Logger logger = Logger.getLogger(MySQLDataSourceFactory.class);
    UserDaoMySqlImpl userDaoMySqlImpl;
    ProductDaoMySqlImpl productDaoMySqlImpl;
    CartDaoMySqlImpl cartDaoMySqlImpl;
    OrderDaoMySqlImpl orderDaoMySqlImpl;
    DataSource dataSource;
    Connection conn;
    ResourceBundle config;

    public MySQLDataSourceFactory(){
        Context ctx = null;
        ResourceBundle config = ResourceBundle.getBundle("db");
        try {
            ctx = new InitialContext();
            dataSource = (DataSource)ctx.lookup("java:comp/env/jdbc/" + config.getString("dbName"));
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection(){
        logger.debug("Connecting...");
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger.debug("Connection obtained: " + conn.toString());
        return conn;
    }

    public UserDao getUserDao() {
        userDaoMySqlImpl = new UserDaoMySqlImpl(getConnection());
        return userDaoMySqlImpl;
    }

    public ProductDao getProductDao() {
        productDaoMySqlImpl = new ProductDaoMySqlImpl(getConnection());
        return productDaoMySqlImpl;
    }

    public CartDao getCartDao() {
        cartDaoMySqlImpl = new CartDaoMySqlImpl(getConnection());
        return cartDaoMySqlImpl;
    }

    public OrderDao getOrderDao() {
        orderDaoMySqlImpl = new OrderDaoMySqlImpl(getConnection());
        return orderDaoMySqlImpl;
    }

    public void deleteUserDao(UserDao userDao) {
        if (userDao != null) {
            userDao = null;
        }
        closeConnection();
    }

    public void deleteProductDao(ProductDao productDao) {
        if (productDao != null) {
            productDao = null;
        }
        closeConnection();
    }

    public void deleteCartDao(CartDao cartDao) {
        if (cartDao != null) {
            cartDao = null;
        }
        closeConnection();
    }

    public void deleteOrderDao(OrderDao orderDao) {
        if (orderDao != null) {
            orderDao = null;
        }
        closeConnection();
    }

    private void closeConnection() {
        try {
            if (conn != null)
                conn.close();
            logger.debug("Connection to DB was closed");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
