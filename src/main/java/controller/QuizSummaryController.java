package controller;

import dao.QuizAttemptDAO;
import dao.UserDAO;
import model.QuizAttempt;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@WebServlet("/quiz-summery")
public class QuizSummaryController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String quizIdParam = req.getParameter("quizId");
        if (quizIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing quizId parameter");
            return;
        }
        int quizId = Integer.parseInt(quizIdParam);
        QuizAttemptDAO attemptDAO = (QuizAttemptDAO) getServletContext().getAttribute("quizAttemptDAO");
        UserDAO userDAO = (UserDAO) getServletContext().getAttribute("userDAO");
        try {
            List<QuizAttempt> allAttempts = attemptDAO.getAttemptsByQuiz(quizId).stream()
                .filter(a -> !a.isPractice())
                .collect(Collectors.toList());

            // Helper: for a list of attempts, return only the best attempt per user
            java.util.function.Function<List<QuizAttempt>, List<QuizAttempt>> bestPerUser = (attempts) ->
                attempts.stream()
                    .collect(Collectors.groupingBy(QuizAttempt::getUserId))
                    .values().stream()
                    .map(userAttempts -> userAttempts.stream()
                        .sorted((a, b) -> {
                            int cmp = Double.compare(b.getScore(), a.getScore());
                            if (cmp != 0) return cmp;
                            cmp = Integer.compare(b.getTotalQuestions(), a.getTotalQuestions());
                            if (cmp != 0) return cmp;
                            cmp = Long.compare(a.getTimeTaken(), b.getTimeTaken());
                            if (cmp != 0) return cmp;
                            return b.getDateTaken().compareTo(a.getDateTaken());
                        })
                        .findFirst().get())
                    .collect(Collectors.toList());

            // All-time top performers (best per user)
            List<QuizAttempt> allTimeTop = bestPerUser.apply(allAttempts).stream()
                .sorted((a, b) -> {
                    int cmp = Double.compare(b.getScore(), a.getScore());
                    if (cmp != 0) return cmp;
                    cmp = Integer.compare(b.getTotalQuestions(), a.getTotalQuestions());
                    if (cmp != 0) return cmp;
                    cmp = Long.compare(a.getTimeTaken(), b.getTimeTaken());
                    if (cmp != 0) return cmp;
                    return b.getDateTaken().compareTo(a.getDateTaken());
                })
                .limit(10)
                .collect(Collectors.toList());

            // Top performers in the last day (best per user)
            long oneDayAgo = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
            List<QuizAttempt> lastDayTop = bestPerUser.apply(
                allAttempts.stream()
                    .filter(a -> a.getDateTaken().getTime() >= oneDayAgo)
                    .collect(Collectors.toList())
            ).stream()
                .sorted((a, b) -> {
                    int cmp = Double.compare(b.getScore(), a.getScore());
                    if (cmp != 0) return cmp;
                    cmp = Integer.compare(b.getTotalQuestions(), a.getTotalQuestions());
                    if (cmp != 0) return cmp;
                    cmp = Long.compare(a.getTimeTaken(), b.getTimeTaken());
                    if (cmp != 0) return cmp;
                    return b.getDateTaken().compareTo(a.getDateTaken());
                })
                .limit(10)
                .collect(Collectors.toList());

            // Recent test takers (most recent non-practice attempts, best per user)
            List<QuizAttempt> recent = bestPerUser.apply(allAttempts).stream()
                .sorted((a, b) -> b.getDateTaken().compareTo(a.getDateTaken()))
                .limit(10)
                .collect(Collectors.toList());

            // Collect all userIds from all lists
            Set<Integer> userIds = new HashSet<>();
            for (QuizAttempt a : allTimeTop) userIds.add(a.getUserId());
            for (QuizAttempt a : lastDayTop) userIds.add(a.getUserId());
            for (QuizAttempt a : recent) userIds.add(a.getUserId());

            // Map userId to username
            Map<Integer, String> userIdToUsername = new HashMap<>();
            for (Integer userId : userIds) {
                User user = userDAO.findById(userId);
                userIdToUsername.put(userId, user != null ? user.getUsername() : ("User#" + userId));
            }

            req.setAttribute("allTimeTop", allTimeTop);
            req.setAttribute("lastDayTop", lastDayTop);
            req.setAttribute("recentAttempts", recent);
            req.setAttribute("userIdToUsername", userIdToUsername);

            req.getRequestDispatcher("/jsp/quizSummary.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
