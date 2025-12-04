package controller;

import dao.QuizAttemptDAO;
import model.QuizAttempt;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Comparator;

@WebServlet("/quizHistory")
public class QuizHistoryController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String quizIdParam = req.getParameter("quizId");
        if (quizIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing quizId parameter");
            return;
        }
        int quizId = Integer.parseInt(quizIdParam);
        String mode = req.getParameter("mode");
        QuizAttemptDAO attemptDAO = (QuizAttemptDAO) getServletContext().getAttribute("quizAttemptDAO");
        try {
            List<QuizAttempt> allAttempts = attemptDAO.getAttemptsByQuiz(quizId);
            List<QuizAttempt> attempts = new java.util.ArrayList<>();
            for (QuizAttempt a : allAttempts) {
                if (a.getUserId() == user.getUserId()) {
                    if (mode == null || mode.equals("all") ||
                        (mode.equals("practice") && a.isPractice()) ||
                        (mode.equals("nonpractice") && !a.isPractice())) {
                        attempts.add(a);
                    }
                }
            }
            attempts.sort(Comparator.comparing(QuizAttempt::getDateTaken).reversed());
            // Calculate average score
            double averageScore = -1;
            if (!attempts.isEmpty()) {
                double sum = 0;
                for (QuizAttempt a : attempts) sum += a.getScore();
                averageScore = sum / attempts.size();
            }
            req.setAttribute("attempts", attempts);
            req.setAttribute("quizId", quizId);
            req.setAttribute("mode", mode == null ? "all" : mode);
            req.setAttribute("averageScore", averageScore);
            req.getRequestDispatcher("/jsp/quizHistory.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
} 