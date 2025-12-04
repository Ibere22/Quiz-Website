package controller;

import dao.QuizDAO;
import dao.QuizAttemptDAO;
import dao.AnnouncementDAO;
import dao.MessageDAO;
import model.Quiz;
import model.QuizAttempt;
import model.Announcement;
import model.User;
import model.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet(urlPatterns = {"", "/"})
public class HomeController extends HttpServlet {
    private QuizDAO quizDAO;
    private QuizAttemptDAO quizAttemptDAO;
    private AnnouncementDAO announcementDAO;
    private MessageDAO messageDAO;

    @Override
    public void init() throws ServletException {
        Connection connection = (Connection) getServletContext().getAttribute("DBConnection");
        quizDAO = (QuizDAO) getServletContext().getAttribute("quizDAO");
        quizAttemptDAO = (QuizAttemptDAO) getServletContext().getAttribute("quizAttemptDAO");
        announcementDAO = (AnnouncementDAO) getServletContext().getAttribute("announcementDAO");
        messageDAO = (MessageDAO) getServletContext().getAttribute("messageDAO");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Quiz> quizzes = quizDAO.getAllQuizzes();
            req.setAttribute("quizzes", quizzes);

            // Popular quizzes
            List<Quiz> popularQuizzes = quizDAO.getPopularQuizzes(5);
            req.setAttribute("popularQuizzes", popularQuizzes);

            // Recently created quizzes
            List<Quiz> recentQuizzes = quizDAO.getAllQuizzes(0, 5);
            req.setAttribute("recentQuizzes", recentQuizzes);

            // Get active announcements for homepage
            List<Announcement> activeAnnouncements = announcementDAO.getActiveAnnouncements();
            req.setAttribute("activeAnnouncements", activeAnnouncements);

            // User-specific lists
            User user = (User) req.getSession().getAttribute("user");
            if (user != null) {
                // Recent quiz attempts
                List<QuizAttempt> recentAttempts = quizAttemptDAO.getRecentAttemptsForUser(user.getUserId(), 5);
                req.setAttribute("recentAttempts", recentAttempts);

                // User's created quizzes
                List<Quiz> userCreatedQuizzes = quizDAO.getQuizzesByCreator(user.getUserId());
                req.setAttribute("userCreatedQuizzes", userCreatedQuizzes);

                // Add unread message badge info for logged-in users
                try {
                    int unreadCount = messageDAO.getUnreadMessageCount(user.getUserId());
                    req.setAttribute("unreadMessageCount", unreadCount);
                    String recentTypeEmoji = null;
                    if (unreadCount > 0) {
                        java.util.List<Message> unreadMessages = messageDAO.getUnreadMessages(user.getUserId());
                        if (!unreadMessages.isEmpty()) {
                            String type = unreadMessages.get(0).getMessageType();
                            if ("note".equals(type)) recentTypeEmoji = "📝";
                            else if ("challenge".equals(type)) recentTypeEmoji = "🎯";
                            else if ("friend_request".equals(type)) recentTypeEmoji = "👥";
                        }
                    }
                    req.setAttribute("recentUnreadTypeEmoji", recentTypeEmoji);
                } catch (Exception e) {
                    req.setAttribute("unreadMessageCount", 0);
                    req.setAttribute("recentUnreadTypeEmoji", null);
                }
            }

            req.getRequestDispatcher("/index.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
