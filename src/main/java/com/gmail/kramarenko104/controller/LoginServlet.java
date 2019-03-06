package com.gmail.kramarenko104.controller;

import com.gmail.kramarenko104.dao.CartDao;
import com.gmail.kramarenko104.dao.UserDao;
import com.gmail.kramarenko104.dao.UserDaoMySqlImpl;
import com.gmail.kramarenko104.factoryDao.DaoFactory;
import com.gmail.kramarenko104.model.Cart;
import com.gmail.kramarenko104.model.User;
import org.apache.log4j.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(LoginServlet.class);
    private int attempt;
    private DaoFactory daoFactory;
    private int LOGIN_ATTEMPT_QUANTITY = 3;
    private int WAIT_SECONDS = 15;

    public LoginServlet() throws ServletException, IOException {
        daoFactory = DaoFactory.getSpecificDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StringBuilder msgText = new StringBuilder();
        logger.debug("LoginServlet: =================enter========================");
        boolean showLoginForm = true;
        boolean accessGranted = false;
        UserDao userDao = daoFactory.getUserDao();

        String viewToGo = "WEB-INF/view/login.jsp";
        HttpSession session = req.getSession();
        String login = req.getParameter("login");
        String pass = req.getParameter("password");
        logger.debug("LoginServlet: login param: " + login);
        logger.debug("LoginServlet: pass param: " + pass);
        User currentUser = null;

        if (session != null) {
            attempt = (session.getAttribute("attempt") == null) ? 0 : (int) session.getAttribute("attempt");

            // already logged in
            if (session.getAttribute("user") != null) {
                currentUser = (User) session.getAttribute("user");
                logger.debug("LoginServlet: user already logged in: " + currentUser);
                accessGranted = true;
            } // not logged in yet
            else {
                logger.debug("LoginServlet: user didn't logged in yet");
                long waitTime = 0;

                if ((login != null) && !("".equals(login))) {
                    logger.debug("LoginServlet: login = " + login);
                    currentUser = userDao.getUserByLogin(login);
                    boolean exist = (currentUser != null);
                    logger.debug("LoginServlet: user is present in DB = " + exist);

                    if (exist) {
                        String passVerif = UserDaoMySqlImpl.hashString(pass);
                        logger.debug("LoginServlet: currentUser = " + currentUser);
                        accessGranted = (currentUser.getPassword().equals(passVerif));
                        logger.debug("LoginServlet: accessGranted = " + accessGranted);
                        showLoginForm = !accessGranted && attempt < LOGIN_ATTEMPT_QUANTITY;
                        logger.debug("LoginServlet: showLoginForm = " + showLoginForm);

                        if (accessGranted) {
                            attempt = 0;
                            showLoginForm = false;
                            session.setAttribute("user", currentUser);
                            logger.debug("LoginServlet: User was registered: " + currentUser.getName() + " and passed autorization");
                        } else {
                            attempt++;
                            if (attempt >= LOGIN_ATTEMPT_QUANTITY) {
                                long startTime = 0L;
                                if (attempt == LOGIN_ATTEMPT_QUANTITY) {
                                    startTime = System.currentTimeMillis();
                                }
                                waitTime = WAIT_SECONDS - (System.currentTimeMillis() - startTime) / 1000;
                                if (waitTime > 0) {
                                    msgText.append("<br><font size=4 color='red'><b> Attempts' limit is exceeded. Login form will be available in " + waitTime + " seconds</b></font>");
                                    showLoginForm = false;
                                } else {
                                    attempt = 0;
                                    showLoginForm = true;
                                }
                            } else if (attempt >= 0) {
                                msgText.append("<b><font size=4 color='red'>Wrong password, try again! (attempt #" + attempt + ")</font>");
                            }
                        }
                    } else {
                        attempt = 0;
                        showLoginForm = false;
                        msgText.append("<br><b><font size=3 color='green'><center>You wasn't registered yet.</b>");
                        msgText.append("<br><b>You need <a href='registration'>register</a> or <a href='login'>login</a> before shopping.</b></font>");
                    }
                } else {
                    attempt = 0;
                }
            }
        }

        // for authorized user get corresponding shopping Cart
        if (accessGranted) {
            CartDao cartDao = daoFactory.getCartDao();
            showLoginForm = false;
            Cart userCart = (Cart) session.getAttribute("userCart");
            if (userCart == null) {
                userCart = cartDao.getCart(currentUser.getId());
                if (userCart == null) {
                    userCart = new Cart(currentUser.getId());
                }
                session.setAttribute("userCart", userCart);
            }
            if (userCart.getCartSize() > 0) {
                viewToGo = "./cart";
            }
            daoFactory.deleteCartDao(cartDao);
        }

        session.setAttribute("showLoginForm", showLoginForm);
        session.setAttribute("message", msgText.toString());
        session.setAttribute("attempt", attempt);

        daoFactory.deleteUserDao(userDao);

        logger.debug(">>>>>>>>>>>>LoginServlet: go to " + viewToGo);
        req.getRequestDispatcher(viewToGo).forward(req, resp);
        // login was successful, redirect to cart controller
        //        if (viewToGo.equals("./cart")){
//    }
//            logger.debug("LoginServlet: login was successful, redirect to cart controller");
//            resp.sendRedirect(viewToGo);
//        }
//        else { // login was unsuccessful, try again, go to login.jsp
//            logger.debug("LoginServlet: login was unsuccessful, try again, go to login.jsp");
//            req.getRequestDispatcher(viewToGo).forward(req, resp);
//        }
        logger.debug("LoginServlet: =================exit========================");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    public void destroy() {
        daoFactory.closeConnection();
    }

}
