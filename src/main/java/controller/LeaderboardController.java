package controller;

import dao.QuizAttemptDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import model.LeaderboardEntry;

@WebServlet("/leaderboard")
public class LeaderboardController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Fetch leaderboard data from DAO
        Connection connection = (Connection) getServletContext().getAttribute("DBConnection");
        QuizAttemptDAO quizAttemptDAO = (QuizAttemptDAO) getServletContext().getAttribute("quizAttemptDAO");
        List<LeaderboardEntry> leaderboard = null;
        try {
            if (quizAttemptDAO == null && connection != null) {
                quizAttemptDAO = new QuizAttemptDAO(connection);
            }
            if (quizAttemptDAO != null) {
                leaderboard = quizAttemptDAO.getLeaderboardData();
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.setAttribute("leaderboard", leaderboard);
        req.getRequestDispatcher("/jsp/leaderboard.jsp").forward(req, resp);
    }
}
