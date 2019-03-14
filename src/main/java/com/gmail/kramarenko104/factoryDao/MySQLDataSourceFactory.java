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
import javax.sql.*;

public class MySQLDataSourceFactory extends DaoFactory {

    private static Logger logger = Logger.getLogger(MySQLDataSourceFactory.class);
    UserDaoMySqlImpl userDaoMySqlImpl;
    ProductDaoMySqlImpl productDaoMySqlImpl;
    CartDaoMySqlImpl cartDaoMySqlImpl;
    OrderDaoMySqlImpl orderDaoMySqlImpl;
    MysqlConnectionPoolDataSource dataSource;
//    Connection conn;

//    public DataSource simpleDataSource() {
//        final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
//        dataSource.setDriverClass(Driver.class);
//        dataSource.setUrl(env.getProperty("db.url"));
//        dataSource.setUsername(env.getProperty("db.username"));
//        dataSource.setPassword(env.getProperty("db.password"));
//        return dataSource;
//    }

    public MySQLDataSourceFactory(){
        Properties props = new Properties();
        MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
        try ( FileInputStream fis = new FileInputStream("db.properties")) {
            props.load(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Class.forName(props.getProperty("driverClassName")).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        logger.debug("Connecting...");
        try {
            dataSource.setUrl(props.getProperty("url"));
            dataSource.setDatabaseName(props.getProperty("dbName"));
            dataSource.setUser(props.getProperty("usr"));
            dataSource.setPassword(props.getProperty("password"));
            dataSource.setAutoReconnect(true);
            this.dataSource = dataSource;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection(){
        Connection conn = null;
        try {
            conn = dataSource.getPooledConnection().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger.debug("Connection obtained");
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
    }

    public void deleteProductDao(ProductDao productDao) {
        if (productDao != null) {
            productDao = null;
        }
    }

    public void deleteCartDao(CartDao cartDao) {
        if (cartDao != null) {
            cartDao = null;
        }
    }

    public void deleteOrderDao(OrderDao orderDao) {
        if (orderDao != null) {
            orderDao = null;
        }
    }

    public void closeConnection() {
//        try {
//            if (conn != null)
//                conn.close();
//            logger.debug("Connection to DB was closed");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
}
