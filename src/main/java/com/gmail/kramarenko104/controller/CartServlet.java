package com.gmail.kramarenko104.controller;

import com.gmail.kramarenko104.dao.CartDao;
import com.gmail.kramarenko104.factoryDao.DaoFactory;
import com.gmail.kramarenko104.model.Cart;
import com.gmail.kramarenko104.model.User;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(CartServlet.class);
    private DaoFactory daoFactory;

    public CartServlet() {
        daoFactory = DaoFactory.getSpecificDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("CartServlet: ----enter GET -------");
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
        logger.debug("CartServlet: ----exit GET -------");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        boolean needRefresh = false;
        logger.debug("CartServlet: ----enter POST -------");

        if (session.getAttribute("user") != null) {
            User currentUser = (User) session.getAttribute("user");
            logger.debug("CartServlet: Current user: " + currentUser.getName());
            int userId = currentUser.getId();

            //////////////////// CHANGE CART /////////////////////////////////////
            CartDao cartDao = daoFactory.getCartDao();
            // get info from Ajax POST request (from updateCart.js)
            String param = request.getParameter("action");
            if (param != null && param.length() > 0) {
                int productId = 0;
                int quantity = 0;
                switch (param) {
                    case "add":
                        logger.debug("CatServlet: GOT PARAMETER 'add'....");
                        productId = Integer.valueOf(request.getParameter("productId"));
                        quantity = Integer.valueOf(request.getParameter("quantity"));
                        cartDao.addProduct(currentUser.getId(), productId, quantity);
                        logger.debug("CartServlet: for user '" + currentUser.getName() + "' was added " + quantity + " of productId: " + productId);
                        break;
                    case "remove":
                        logger.debug("CartServlet: GOT PARAMETER 'remove' ");
                        productId = Integer.valueOf(request.getParameter("productId"));
                        quantity = Integer.valueOf(request.getParameter("quantity"));
                        cartDao.removeProduct(currentUser.getId(), productId, quantity);
                        logger.debug("CartServlet: for user: " + currentUser.getName() + "was removed " + quantity + " of productId " + productId);
                        break;
                    case "makeOrder":
                        logger.debug("CartServlet: GOT PARAMETER 'makeOrder' ");
                        cartDao.deleteCart(Integer.valueOf(request.getParameter("userId")));
                        logger.debug("CartServlet: for user: " + currentUser.getName() + " order was created and cart was cleared");
                }
                needRefresh = true;
            }
            ///////////////// REFRESH CART's characteristics if refresh need
            logger.debug("CartServlet: needRefresh ==  " + needRefresh);
            Cart userCart = null;
            if (session.getAttribute("userCart") == null || needRefresh) {
                userCart = cartDao.getCart(userId);
                if (userCart == null) {
                    logger.debug("CartServlet: cart from DB == null! create the new cart for userId: " + userId);
                    userCart = new Cart(userId);
                }
                session.setAttribute("userCart", userCart);

                // send JSON with updated Cart to cart.jsp
                if (userCart != null) {
                    logger.debug("CartServlet: updated cartSize: " + userCart.getCartSize());
                    logger.debug("CartServlet: updated totalSum: " + userCart.getTotalSum());
                    String jsondata = new Gson().toJson(userCart);
                    logger.debug("CartServlet: send JSON data to cart.jsp ---->" + jsondata);
                    PrintWriter out = response.getWriter();
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    out.print(jsondata);
                    out.flush();
                    out.close();
                }
            }
            daoFactory.deleteCartDao(cartDao);
        } else {
            session.setAttribute("message", "You should login to see your cart");
            logger.debug("CartServlet: Current user == null ");
        }

        request.getRequestDispatcher("WEB-INF/view/cart.jsp").forward(request, response);
        logger.debug("CartServlet: ----exit POST -------");
    }

    @Override
    public void destroy() {
        daoFactory.closeConnection();
    }
}
