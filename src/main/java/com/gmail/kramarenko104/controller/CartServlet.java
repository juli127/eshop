package com.gmail.kramarenko104.controller;

import com.gmail.kramarenko104.dao.CartDao;
import com.gmail.kramarenko104.factoryDao.DaoFactory;
import com.gmail.kramarenko104.model.Cart;
import com.gmail.kramarenko104.model.Product;
import com.gmail.kramarenko104.model.User;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(CartServlet.class);
    private DaoFactory daoFactory;

    public CartServlet() {
        daoFactory = DaoFactory.getSpecificDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        boolean needRefresh = false;

        if (session.getAttribute("user") != null) {
            User currentUser = (User) session.getAttribute("user");
            logger.debug("CartServlet: Current user: " + currentUser.getName());
            int userId = currentUser.getId();

            Cart userCart = null;
            if (session.getAttribute("userCart") == null) {
                CartDao cartDao = daoFactory.getCartDao();
                userCart = cartDao.getCart(userId);
                if (userCart == null) {
                    logger.debug("CartServlet: cart from DB == null! create new cart for userId: " + userId);
                    userCart = new Cart(userId);
                }
                session.setAttribute("userCart", userCart);
                daoFactory.deleteCartDao(cartDao);
            }
        } else {
            session.setAttribute("message", "You should login to see your cart");
            logger.debug("CartServlet: Current user == null ");
        }
        request.getRequestDispatcher("WEB-INF/view/cart.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        boolean needRefresh = false;

        if (session.getAttribute("user") != null) {
            User currentUser = (User) session.getAttribute("user");
            logger.debug("CartServlet: Current user: " + currentUser.getName());
            int userId = currentUser.getId();

            //////////////////// CHANGE CART /////////////////////////////////////
            CartDao cartDao = daoFactory.getCartDao();
            // got this info from updateCart.js (TODO: parse it as JSON format)
            String addProducts = request.getParameter("addPurchase");
            if (addProducts != null) {
                logger.debug("CatServlet: GOT PARAMETER addPurchase: === " + addProducts);
                String[] addProductsArr = ((String)addProducts).split(":");
                int productId = Integer.valueOf(addProductsArr[0]);
                int quantity = Integer.valueOf(addProductsArr[1]);
                cartDao.addProduct(currentUser.getId(), productId, quantity);
                logger.debug("CartServlet: for user '" + currentUser.getName() + "' was added " + quantity + " of productId: " + productId);

                needRefresh = true;
            }

            // got this info from updateCart.js (TODO: parse it as JSON format
            String rmProducts = request.getParameter("removePurchase");
            if (rmProducts != null) {
                logger.debug("CartServlet: GOT PARAMETER removePurchase: === " + rmProducts);
                String[] rmProductsArr = ((String)rmProducts).split(":");
                int productId = Integer.valueOf(rmProductsArr[0]);
                int quantity = Integer.valueOf(rmProductsArr[1]);
                cartDao.removeProduct(currentUser.getId(), productId, quantity);
                logger.debug("CartServlet: for user: " + currentUser.getName() + "was removed " + quantity + " of productId " + productId);
                needRefresh = true;
            }

            ///////////////// REFRESH CART's characteristics if refresh need ////////////////////////////////////////
            logger.debug("CartServlet: needRefresh ==  "+ needRefresh);

            Cart userCart = null;
            if (session.getAttribute("userCart") == null || needRefresh) {
                userCart = cartDao.getCart(userId);
                if (userCart == null) {
                    logger.debug("CartServlet: cart from DB == null! create new cart fror userId: " + userId);
                    userCart = new Cart(userId);
                }
                session.setAttribute("userCart", userCart);
            }
            daoFactory.deleteCartDao(cartDao);
        } else {
            logger.debug("CartServlet: Current user == null ");
        }

//        OutputStream out = response.getOutputStream();
//        out.write("hello world".getBytes());
        request.getRequestDispatcher("WEB-INF/view/cart.jsp").forward(request, response);

    }

    @Override
    public void destroy() {
        daoFactory.closeConnection();
    }
}
