package com.gmail.kramarenko104.factoryDao;

import com.gmail.kramarenko104.dao.*;
import org.apache.log4j.Logger;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;

public class MySqlDataSourceFactory extends DaoFactory {

    private static Logger logger = Logger.getLogger(MySqlDataSourceFactory.class);
    private UserDaoMySqlImpl userDaoMySqlImpl;
    private ProductDaoMySqlImpl productDaoMySqlImpl;
    private CartDaoMySqlImpl cartDaoMySqlImpl;
    private OrderDaoMySqlImpl orderDaoMySqlImpl;
    private BasicDataSource dataSource;
    private Connection conn;

    public MySqlDataSourceFactory(){
        if (dataSource == null)
        {
            ResourceBundle config = null;
            try {
                config = ResourceBundle.getBundle("dbconfig");
            } catch (MissingResourceException e) {
                e.printStackTrace();
            }
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName(config.getString("driverClassName"));
            ds.setUrl(config.getString("url"));
            ds.setUsername(config.getString("username"));
            ds.setPassword(config.getString("password"));
            ds.setMinIdle(5);
            ds.setMaxIdle(10);
            ds.setMaxOpenPreparedStatements(100);
            dataSource = ds;
        }
    }

    @Override
    public void openConnection() {
        try {
            conn = dataSource.getConnection();
            logger.debug("Connection obtained...");
        } catch (SQLException e) {
            logger.debug("Connection failed. SQLException: " + e.getMessage());
        }
    }

    @Override
    public void closeConnection() {
        try {
            if (conn != null)
                conn.close();
            logger.debug("Connection closed...");
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
