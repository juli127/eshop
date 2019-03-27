package com.gmail.kramarenko104.controller;

import com.gmail.kramarenko104.dao.UserDao;
import com.gmail.kramarenko104.factoryDao.DaoFactory;
import com.gmail.kramarenko104.model.User;
import org.apache.log4j.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin"})
public class AdminServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(LoginServlet.class);
    private static final String DB_WARNING = "Check your connection to DB!";
    private DaoFactory daoFactory;

    public AdminServlet() {
        daoFactory = DaoFactory.getSpecificDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session != null) {
            if (daoFactory.openConnection()) {
                UserDao userDao = daoFactory.getUserDao();
                List<User> usersList = userDao.getAllUsers();
                session.setAttribute("usersList", usersList);
                daoFactory.deleteUserDao(userDao);
                daoFactory.closeConnection();
            } else {
                session.setAttribute("warning", DB_WARNING);
            }
        }
        req.getRequestDispatcher("WEB-INF/view/admin.jsp").forward(req, resp);
    }
}
