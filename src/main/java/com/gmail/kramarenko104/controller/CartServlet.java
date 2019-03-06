package com.gmail.kramarenko104.controller;

import com.gmail.kramarenko104.dao.CartDao;
import com.gmail.kramarenko104.factoryDao.DaoFactory;
import com.gmail.kramarenko104.model.Cart;
import com.gmail.kramarenko104.model.User;
import com.google.gson.stream.JsonWriter;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

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
            String param = request.getParameter("action");
            int productId = 0;
            int quantity = 0;

            switch (param) {
                case "addPurchase":
                    logger.debug("CatServlet: GOT PARAMETER addPurchase....");
                    productId = Integer.valueOf(request.getParameter("productId"));
                    quantity = Integer.valueOf(request.getParameter("quantity"));
                    cartDao.addProduct(currentUser.getId(), productId, quantity);
                    logger.debug("CartServlet: for user '" + currentUser.getName() + "' was added " + quantity + " of productId: " + productId);
                    needRefresh = true;
                    break;
                case "removePurchase":
                    logger.debug("CartServlet: GOT PARAMETER removePurchase ");
                    productId = Integer.valueOf(request.getParameter("productId"));
                    quantity = Integer.valueOf(request.getParameter("quantity"));
                    cartDao.removeProduct(currentUser.getId(), productId, quantity);
                    logger.debug("CartServlet: for user: " + currentUser.getName() + "was removed " + quantity + " of productId " + productId);
                    needRefresh = true;
                    break;
                case "makeOrder":
                    logger.debug("CartServlet: GOT PARAMETER makeOrder ");
                    cartDao.deleteCart(currentUser.getId());
                    logger.debug("CartServlet: for user: " + currentUser.getName() + " order was created and cart was cleared");
                    needRefresh = true;
            }

            ///////////////// REFRESH CART's characteristics if refresh need ////////////////////////////////////////
            logger.debug("CartServlet: needRefresh ==  " + needRefresh);

            Cart userCart = null;
            if (session.getAttribute("userCart") == null || needRefresh) {
                userCart = cartDao.getCart(userId);
                if (userCart == null) {
                    logger.debug("CartServlet: cart from DB == null! create new cart fror userId: " + userId);
                    userCart = new Cart(userId);
                }
                session.setAttribute("userCart", userCart);
                // send JSON to cart.jsp
                if (userCart != null) {
                    OutputStream outputStream = new ByteArrayOutputStream();
                    JsonWriter writer = new JsonWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    writer.beginObject();
                    writer.name("cartSize");
                    writer.value(userCart.getCartSize());
                    writer.name("totalSum");
                    writer.value(userCart.getTotalSum());
                    writer.endObject();
                    writer.close();
                }
            }
            daoFactory.deleteCartDao(cartDao);
        } else {
            logger.debug("CartServlet: Current user == null ");
        }

        request.getRequestDispatcher("WEB-INF/view/cart.jsp").forward(request, response);
    }

    @Override
    public void destroy() {
        daoFactory.closeConnection();
    }
}
