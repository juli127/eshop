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

        CartDao cartDao = daoFactory.getCartDao();
        UserDao userDao = daoFactory.getUserDao();

        String viewToGo = "WEB-INF/view/login.jsp";
        HttpSession session = req.getSession();
        String login = req.getParameter("login");
        String pass = req.getParameter("password");

        if (session != null) {
            logger.debug("LoginServlet: session != null");
            session.setAttribute("session", session);
            attempt = (session.getAttribute("attempt") == null) ? 0 : (int) session.getAttribute("attempt");
            User currentUser;

            // already logged in
            if (session.getAttribute("user") != null) {
                currentUser = (User) session.getAttribute("user");
                logger.debug("LoginServlet: user already logged in: " + currentUser);
                showLoginForm = false;
                if ((int)session.getAttribute("cartSize") > 0) {
                    viewToGo = "./cart";
                }
            } // not logged in yet
            else {
                logger.debug("LoginServlet: user didn't log in yet");
                boolean accessGranted = false;
                long waitTime = 0;

                if ((login != null) && !("".equals(login))) {
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
                            logger.debug("LoginServlet: user.getName() = " + currentUser.getName());
                            if ((int)session.getAttribute("cartSize") > 0) {
                                viewToGo = "./cart";
                            }
                        }
                        else {
                            attempt++;
                            if (attempt >= LOGIN_ATTEMPT_QUANTITY) {
                                long startTime = 0L;
                                if (attempt == LOGIN_ATTEMPT_QUANTITY) {
                                    startTime = System.currentTimeMillis();
                                }
                                waitTime = WAIT_SECONDS - (System.currentTimeMillis() - startTime) / 1000;
                                if (waitTime > 0) {
                                    msgText.append("<br><font size=4 color='red'><b> Форма будет снова доступна через " + waitTime + " секунд</b></font>");
                                    showLoginForm = false;
                                }
                                else {
                                    attempt = 0;
                                    showLoginForm = true;
                                }
                            }
                            else if (attempt >= 0) {
                                msgText.append("<b><font size=4 color='red'>Неправильный пароль, попробуйте еще раз! (попытка #" + attempt + ")</font>");
                            }
                        }
                    }
                    else {
                        attempt = 0;
                        showLoginForm = false;
                        msgText.append("<br><b><font size=3 color='green'><center>Пользователь с таким логином еще не был зарегистрирован.</b>");
                        msgText.append("<br><b>Вы можете <a href='registration'>зарегестрироваться по ссылке</a></b></font>");
                    }
                }
                else {
                    attempt = 0;
                }
            }
        }

        logger.debug("LoginServlet: go to " + viewToGo);
        session.setAttribute("showLoginForm", showLoginForm);
        session.setAttribute("message", msgText.toString());
        session.setAttribute("attempt", attempt);

        daoFactory.deleteCartDao(cartDao);
        daoFactory.deleteUserDao(userDao);

        // login was successful, redirect to cart controller
        if (viewToGo.equals("./cart")){
            logger.debug("LoginServlet: login was successful, redirect to cart controller");
            resp.sendRedirect(viewToGo);
        }
        else { // login was unsuccessful, try again, go to login.jsp
            logger.debug("LoginServlet: login was unsuccessful, try again, go to login.jsp");
            RequestDispatcher rd = req.getRequestDispatcher(viewToGo);
            rd.forward(req, resp);
        }
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
