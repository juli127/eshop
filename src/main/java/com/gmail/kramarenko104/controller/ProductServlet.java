package com.gmail.kramarenko104.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gmail.kramarenko104.dao.CartDao;
import com.gmail.kramarenko104.dao.ProductDao;
import com.gmail.kramarenko104.factoryDao.DaoFactory;
import com.gmail.kramarenko104.model.User;
import org.apache.log4j.Logger;
import com.gmail.kramarenko104.model.Product;

@WebServlet(name = "ProductServlet", urlPatterns = {"/", "/products"})
public class ProductServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(ProductServlet.class);
    private DaoFactory daoFactory;

    public ProductServlet() {
        daoFactory = DaoFactory.getSpecificDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        // prepare products list to show depending on selected category
        ProductDao productDao = daoFactory.getProductDao();
        String selectedCateg = req.getParameter("selectedCategory");
        List<Product> products;

        // when form is opened at the first time, selectedCateg == null
        if (selectedCateg != null) {
            products = productDao.getProductsByCategory(Integer.parseInt(selectedCateg));
        } else {
            products = productDao.getAllProducts();
        }
        session.setAttribute("selectedCateg", selectedCateg);
        session.setAttribute("products", products);
//        products.forEach(e -> System.out.println(e));
        daoFactory.deleteProductDao(productDao);

        // be sure that when we enter on the main application page (products.jsp), user's info is full and correct
        if (session.getAttribute("user") == null) {
            session.setAttribute("cartSize", null);
            session.setAttribute("userName", null);
            session.setAttribute("totalSum", null);
            session.setAttribute("productsInCart", null);
        } else {
            User currentUser = (User) session.getAttribute("user");
            CartDao cartDao = daoFactory.getCartDao();

            logger.debug("ProductServlet: Current user: " + currentUser.getName());
            int userId = currentUser.getId();

            Map<Product, Integer> productsInCart = null;
            if (session.getAttribute("productsInCart") == null) {
                productsInCart = cartDao.getAllProducts(userId);
                session.setAttribute("productsInCart", productsInCart);
                //logger.debug("CartServlet.doGet: Refresh  productsInCart==  "+ productsInCart);
            }

            if (session.getAttribute("cartSize") == null) {
                int cartSize = productsInCart.entrySet().stream().map(e -> e.getValue()).reduce(0, (a, b) -> a + b);
                session.setAttribute("cartSize", cartSize);
                logger.debug("ProductServlet: cart size==  "+ cartSize);
            }

            if (session.getAttribute("totalSum") == null) {
                int totalSum = 0;
                for (Map.Entry entry: productsInCart.entrySet()){
                    totalSum += (int)entry.getValue() * ((Product)entry.getKey()).getPrice();
                }
                session.setAttribute("totalSum", totalSum);
                logger.debug("ProductServlet.doGet: totalSum ==  "+ totalSum);
            }
//            session.setAttribute("user", currentUser);

            daoFactory.deleteCartDao(cartDao);
        }

        RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/view/products.jsp");
        rd.forward(req, resp);
    }

    @Override
    public void destroy() {
        daoFactory.closeConnection();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
