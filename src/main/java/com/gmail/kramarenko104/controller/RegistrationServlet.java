package com.gmail.kramarenko104.controller;

import com.gmail.kramarenko104.dao.UserDao;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "RegistrationServlet", urlPatterns = {"/registration"})
public class RegistrationServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(RegistrationServlet.class);
    private DaoFactory daoFactory;

    public RegistrationServlet() {
        daoFactory = DaoFactory.getSpecificDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("WEB-INF/view/registration.jsp");
        rd.forward(req, resp);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        StringBuilder message = new StringBuilder();
        boolean needRegistration = false;

        String login = request.getParameter("login");
        String pass = request.getParameter("password");
        String repass = request.getParameter("repassword");
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String comment = request.getParameter("comment");
        Map<String, String> errors = new HashMap<>();
        StringBuilder errorsMsg = new StringBuilder();

        if (session != null) {
            if (session.getAttribute("user") != null) { // already logged in
                logger.debug("RegisrtServlet: some user is already logged in");
                User currentUser = (User) session.getAttribute("user");
                String currentLogin = currentUser.getLogin();
                if (currentLogin.equals(login)) {
                    logger.debug("RegisrtServlet: logged-in user tries to register. Just forward him to page with products");
                    message.append("<br><b><font color='green'><center>Hi, " + currentUser.getName() + ". <br>You have registered already.</font><b>");
                } else { // we should logout and login as the new user
                    logger.debug("RegisrtServlet: try to register the new user when the previous one is logged in. Logout and forward to registartion.jsp");
                    session.invalidate();
                    session = request.getSession();
                    needRegistration = true;
                }
            } else {// not logged in yet
                if (!"".equals(login)) {
                    Map<String, String> regData = new HashMap<>();
                    regData.put("login", login);
                    regData.put("pass", pass);
                    regData.put("repass", repass);
                    regData.put("name", name);
                    regData.put("address", address);
                    logger.debug("RegisrtServlet: no one user is logged in. Just check entered fields from registration form");

                    for (Map.Entry<String, String> entry : regData.entrySet()) {
                        logger.debug("RegisrtServlet: user entered: " + entry.getKey() + ": " + entry.getValue());
                        if (entry.getValue() == null || entry.getValue().length() < 1) {
                            errors.put(entry.getKey(), "Cannot be empty!");
                        }
                    }
                    if (repass.length() > 0 && !pass.equals(repass)) {
                        errors.put("", "Password and retyped one don't match!");
                    }

                    String patternString = "([0-9a-zA-Z]+){4,}";
                    Pattern pattern = Pattern.compile(patternString);
                    Matcher matcher = pattern.matcher(pass);
                    logger.debug("RegisrtServlet: pass.length(): " + pass.length());
                    logger.debug("RegisrtServlet: repass.length(): " + repass.length());
                    if (pass.length() > 0 && !matcher.matches()) {
                        errors.put("", "Password should have minimum 4 symbols!");
                    }

                    if (errors.size() == 0) {
                        logger.debug("RegisrtServlet: anything was entered correctly");
                        UserDao userDao = daoFactory.getUserDao();
                        User newUser = new User();
                        newUser.setLogin(login);
                        newUser.setName(name);
                        newUser.setPassword(pass);
                        newUser.setAddress(address);
                        newUser.setComment(comment);
                        if (userDao.createUser(newUser)) {
                            newUser = userDao.getUserByLogin(login);
                            logger.debug("RegisrtServlet: new user was created: " + newUser);
                            message.append("<br><font color='green'><center>Hi, " + name + "! <br>You have been registered. You can shopping now!</font>");
                            session.setAttribute("user", newUser);
                            session.setAttribute("userCart", new Cart(newUser.getId()));
                        } else {
                            needRegistration = true;
                            message.append("<br><font color='red'><center>User wan't registered because of DB problems!</font>");
                        }
                        daoFactory.deleteUserDao(userDao);
                    } else {
                        needRegistration = true;
                        errorsMsg.append("<ul>");
                        for (Map.Entry<String, String> entry : errors.entrySet()) {
                            errorsMsg.append("<li>").append(entry.getKey()).append(" ").append(entry.getValue()).append("</li>");
                        }
                        errorsMsg.append("</ul>");
                    }
                }
            }
            session.setAttribute("RegMessage", message.toString());

            if (needRegistration) {
                session.setAttribute("login", login);
                session.setAttribute("name", name);
                session.setAttribute("address", address);
                session.setAttribute("comment", comment);
                session.setAttribute("errorsMsg", errorsMsg);

            } else {
                session.setAttribute("login", null);
                session.setAttribute("name", null);
                session.setAttribute("address", null);
                session.setAttribute("comment", null);
                session.setAttribute("errorsMsg", null);
            }
            request.getRequestDispatcher("WEB-INF/view/registration.jsp").forward(request, response);
        }
    }

    @Override
    public void destroy() {
        daoFactory.closeConnection();
    }

}
