package controller;

import dao.UserDAO;
import dao.QuizAttemptDAO;
import dao.AchievementDAO;
import model.User;
import model.Achievement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet(urlPatterns = {"/profile"})
public class ProfileController extends HttpServlet {
    private UserDAO userDAO;
    private QuizAttemptDAO quizAttemptDAO;
    private AchievementDAO achievementDAO;

    @Override
    public void init() throws ServletException {
        Connection connection = (Connection) getServletContext().getAttribute("DBConnection");
        userDAO = (UserDAO) getServletContext().getAttribute("userDAO");
        quizAttemptDAO = (QuizAttemptDAO) getServletContext().getAttribute("quizAttemptDAO");
        achievementDAO = (AchievementDAO) getServletContext().getAttribute("achievementDAO");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/profile".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                resp.sendRedirect("login");
                return;
            }
            User user = (User) session.getAttribute("user");
            try {
                // Refresh user info from DB
                User dbUser = userDAO.findById(user.getUserId());
                req.setAttribute("userInfo", dbUser);
                req.setAttribute("isOwnProfile", true);
                // Quiz stats
                int quizCount = quizAttemptDAO.getAttemptCountByUser(user.getUserId());
                double avgScore = quizAttemptDAO.getAverageScore(user.getUserId(), false);
                req.setAttribute("quizCount", quizCount);
                req.setAttribute("avgScore", avgScore);
                // Achievements
                List<Achievement> achievements = achievementDAO.getAchievementsByUser(user.getUserId());
                req.setAttribute("achievements", achievements);
                req.getRequestDispatcher("/jsp/profile.jsp").forward(req, resp);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/profile".equals(path)) {
            HttpSession session = req.getSession(false);
            User user = (session != null) ? (User) session.getAttribute("user") : null;
            if (user == null) {
                resp.sendRedirect("login");
                return;
            }
            String currentPassword = req.getParameter("currentPassword");
            String newPassword = req.getParameter("newPassword");
            String confirmNewPassword = req.getParameter("confirmNewPassword");
            try {
                // Check current password
                User authenticated = userDAO.authenticateUser(user.getUsername(), currentPassword);
                if (authenticated == null) {
                    req.setAttribute("passwordError", "Current password is incorrect");
                } else if (!newPassword.equals(confirmNewPassword)) {
                    req.setAttribute("passwordError", "New passwords do not match");
                } else {
                    boolean updated = userDAO.updatePassword(user.getUserId(), newPassword);
                    if (updated) {
                        req.setAttribute("passwordSuccess", "Password changed successfully");
                    } else {
                        req.setAttribute("passwordError", "Failed to update password. Please try again.");
                    }
                }
                // Refresh user info and stats for redisplay
                User dbUser = userDAO.findById(user.getUserId());
                req.setAttribute("userInfo", dbUser);
                req.setAttribute("isOwnProfile", true);
                int quizCount = quizAttemptDAO.getAttemptCountByUser(user.getUserId());
                double avgScore = quizAttemptDAO.getAverageScore(user.getUserId(), false);
                req.setAttribute("quizCount", quizCount);
                req.setAttribute("avgScore", avgScore);
                List<Achievement> achievements = achievementDAO.getAchievementsByUser(user.getUserId());
                req.setAttribute("achievements", achievements);
                req.getRequestDispatcher("/jsp/profile.jsp").forward(req, resp);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
