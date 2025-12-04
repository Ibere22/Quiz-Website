package controller;

import dao.UserDAO;
import model.User;
import util.PasswordHasher;
import util.DbUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import dao.QuizAttemptDAO;
import dao.AchievementDAO;
import dao.FriendshipDAO;

@WebServlet(urlPatterns = {"/login", "/register", "/logout", "/user"})
public class UserController extends HttpServlet {
    private UserDAO userDAO;
    private QuizAttemptDAO quizAttemptDAO;
    private AchievementDAO achievementDAO;
    private FriendshipDAO friendshipDAO;

    @Override
    public void init() throws ServletException
    {

        try
        {
            Connection connection = (Connection) getServletContext().getAttribute("DBConnection");
            userDAO = (UserDAO) getServletContext().getAttribute("userDAO");
            quizAttemptDAO = (QuizAttemptDAO) getServletContext().getAttribute("quizAttemptDAO");
            achievementDAO = (AchievementDAO) getServletContext().getAttribute("achievementDAO");
            friendshipDAO = (FriendshipDAO) getServletContext().getAttribute("friendshipDAO");
        }
        catch (Exception e)
        {
            throw new ServletException("DB connection error", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        switch (path) {
            case "/login":
                req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
                break;
            case "/register":
                req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
                break;
            case "/logout":
                HttpSession session = req.getSession(false);
                if (session != null) session.invalidate();
                resp.sendRedirect(req.getContextPath() + "/");
                break;
            case "/user":
                handleUserProfile(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        switch (path) {
            case "/login":
                handleLogin(req, resp);
                break;
            case "/register":
                handleRegister(req, resp);
                break;
            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        try {
            User user = userDAO.authenticateUser(username, password);
            if (user != null) {
                HttpSession session = req.getSession();
                session.setAttribute("user", user);
                resp.sendRedirect(req.getContextPath() + "/");
            } else {
                req.setAttribute("error", "Invalid username or password");
                req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String email = req.getParameter("email");
        try {
            if (!password.equals(confirmPassword)) {
                req.setAttribute("error", "Passwords do not match");
                req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
                return;
            }
            if (userDAO.findByUsername(username) != null) {
                req.setAttribute("error", "Username already exists");
                req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
                return;
            }
            String hash = PasswordHasher.hashPassword(password);
            User user = new User();
            user.setUsername(username);
            user.setPasswordHash(hash);
            user.setEmail(email);
            userDAO.createUser(user);
            resp.sendRedirect(req.getContextPath() + "/");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void handleUserProfile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String quizId = req.getParameter("quizId");
        HttpSession session = req.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute("user") : null;
        try {
            User profileUser = userDAO.findByUsername(username);
            if (profileUser == null) {
                req.setAttribute("error", "User not found");
                req.getRequestDispatcher("/jsp/userProfile.jsp").forward(req, resp);
                return;
            }
            boolean isOwnProfile = (loggedInUser != null && loggedInUser.getUsername().equals(username));
            req.setAttribute("userInfo", profileUser);
            req.setAttribute("isOwnProfile", isOwnProfile);
            if (quizId != null) req.setAttribute("quizId", quizId);
            // Optionally, add stats/achievements as in ProfileController
            int quizCount = quizAttemptDAO.getAttemptCountByUser(profileUser.getUserId());
            double avgScore = quizAttemptDAO.getAverageScore(profileUser.getUserId(), false);
            req.setAttribute("quizCount", quizCount);
            req.setAttribute("avgScore", avgScore);
            java.util.List<model.Achievement> achievements = achievementDAO.getAchievementsByUser(profileUser.getUserId());
            req.setAttribute("achievements", achievements);
            // Friendship logic
            boolean areFriends = false;
            boolean pendingRequest = false;
            boolean canSendFriendRequest = false;
            if (!isOwnProfile && loggedInUser != null) {
                areFriends = friendshipDAO.areFriends(loggedInUser.getUserId(), profileUser.getUserId());
                if (!areFriends) {
                    boolean sentPending = friendshipDAO.hasPendingRequest(loggedInUser.getUserId(), profileUser.getUserId());
                    boolean receivedPending = friendshipDAO.hasPendingRequest(profileUser.getUserId(), loggedInUser.getUserId());
                    pendingRequest = sentPending || receivedPending;
                    canSendFriendRequest = !pendingRequest;
                }
            }
            req.setAttribute("areFriends", areFriends);
            req.setAttribute("pendingRequest", pendingRequest);
            req.setAttribute("canSendFriendRequest", canSendFriendRequest);
            // Move success/error message from session to request (for redirect after friend request)
            HttpSession httpSession = req.getSession(false);
            if (httpSession != null) {
                Object successMsg = httpSession.getAttribute("success");
                if (successMsg != null) {
                    req.setAttribute("success", successMsg);
                    httpSession.removeAttribute("success");
                }
                Object errorMsg = httpSession.getAttribute("error");
                if (errorMsg != null) {
                    req.setAttribute("error", errorMsg);
                    httpSession.removeAttribute("error");
                }
            }
            req.getRequestDispatcher("/jsp/userProfile.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
} 