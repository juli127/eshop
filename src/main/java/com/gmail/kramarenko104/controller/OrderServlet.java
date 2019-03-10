package com.gmail.kramarenko104.controller;

import com.gmail.kramarenko104.dao.CartDao;
import com.gmail.kramarenko104.dao.OrderDao;
import com.gmail.kramarenko104.factoryDao.DaoFactory;
import com.gmail.kramarenko104.model.Cart;
import com.gmail.kramarenko104.model.Order;
import com.gmail.kramarenko104.model.Product;
import com.gmail.kramarenko104.model.User;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet(name = "OrderServlet", urlPatterns = {"/order"})
public class OrderServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(OrderServlet.class);
    private DaoFactory daoFactory;

    public OrderServlet() {
        daoFactory = DaoFactory.getSpecificDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("OrderServlet.GET:...enter....");
        HttpSession session = req.getSession();

        if (session.getAttribute("user") == null) {
            session.setAttribute("message", "You should login to see your order");
            logger.debug("OrderServlet: Current user == null ");
        }
        req.getRequestDispatcher("WEB-INF/view/order.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        boolean needRefresh = false;

        logger.debug("OrderServlet.POST:...enter....");
        if (session.getAttribute("user") != null) {
            // get info from Ajax POST request (from updateCart.js)
            String param = req.getParameter("action");
            if (param != null && (param.equals("makeOrder"))) {
                int userId = Integer.valueOf(req.getParameter("userId"));
                logger.debug("OrderServlet.POST: got from updateCart.js POST request userId: " + userId);

                OrderDao orderDao = daoFactory.getOrderDao();
                CartDao cartDao = daoFactory.getCartDao();

                // any user can have only one existing now cart and many processed orders (userId uniquely identifies cart)
                Cart cart = cartDao.getCart(userId);
                logger.debug("OrderServlet.POST: got cart from DB: " + cart);

                // order will be created based on the cart's content
                Order newOrder = orderDao.createOrder(userId, cart.getProducts());
                logger.debug("OrderServlet.POST: !!! new Order was created: " + newOrder);
                session.setAttribute("newOrder", newOrder);

                // send JSON with new Order to cart.jsp
                if (newOrder != null) {
                    String jsondata = new Gson().toJson(newOrder);
                    logger.debug("OrderServlet: send JSON data to cart.jsp ---->" + jsondata);
                    PrintWriter out = resp.getWriter();
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    out.print(jsondata);
                    out.flush();
                    out.close();
                }

                daoFactory.deleteOrderDao(orderDao);

                logger.debug("OrderServlet.POST: delete cart for userId: " + userId);
                cartDao.deleteCart(Integer.valueOf(userId));
                session.setAttribute("userCart", null);
                daoFactory.deleteCartDao(cartDao);
            }
        } else {
            session.setAttribute("message", "You should login to see your order");
        }
        req.getRequestDispatcher("WEB-INF/view/order.jsp").forward(req, resp);
        logger.debug("OrderServlet.POST: ---exit---");
    }

    @Override
    public void destroy() {
        daoFactory.closeConnection();
    }
}
