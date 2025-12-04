package controller;

import dao.MessageDAO;
import dao.UserDAO;
import dao.FriendshipDAO;
import dao.QuizDAO;
import dao.QuizAttemptDAO;
import dao.AchievementDAO;
import model.Message;
import model.User;
import model.Achievement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(urlPatterns = {"/messages", "/messages/send", "/messages/markRead", "/messages/delete"})public class MessageController extends HttpServlet {
    
    private MessageDAO messageDAO;
    private UserDAO userDAO;
    private FriendshipDAO friendshipDAO;
    private QuizDAO quizDAO;
    private QuizAttemptDAO quizAttemptDAO;
    private AchievementDAO achievementDAO;

    @Override
    public void init() throws ServletException {
        try {
            messageDAO = (MessageDAO) getServletContext().getAttribute("messageDAO");
            userDAO = (UserDAO) getServletContext().getAttribute("userDAO");
            friendshipDAO = (FriendshipDAO) getServletContext().getAttribute("friendshipDAO");
            quizDAO = (QuizDAO) getServletContext().getAttribute("quizDAO");
            quizAttemptDAO = (QuizAttemptDAO) getServletContext().getAttribute("quizAttemptDAO");
            achievementDAO = (AchievementDAO) getServletContext().getAttribute("achievementDAO");
        } catch (Exception e) {
            throw new ServletException("Database connection error", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        String path = req.getServletPath();
        
        try {
            if ("/messages".equals(path)) {
                handleViewMessages(req, resp, currentUser);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        String path = req.getServletPath();
        
        try {
            if ("/messages/send".equals(path)) {
                handleSendMessage(req, resp, currentUser);
            } else if ("/messages/markRead".equals(path)) {
                handleMarkAsRead(req, resp, currentUser);
            } else if ("/messages/delete".equals(path)) {
                handleDeleteMessage(req, resp, currentUser);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
    
    private void handleViewMessages(HttpServletRequest req, HttpServletResponse resp, User user) 
            throws SQLException, ServletException, IOException {
        
        List<Message> messages = messageDAO.getReceivedMessages(user.getUserId());
        req.setAttribute("messages", messages);
        // After getting messages, set quizName for challenge messages
        for (Message msg : messages) {
            if (Message.TYPE_CHALLENGE.equals(msg.getMessageType()) && msg.getQuizId() != null) {
                model.Quiz quiz = quizDAO.findById(msg.getQuizId());
                if (quiz != null) {
                    msg.setQuizName(quiz.getTitle());
                }
            }
        }
        req.getRequestDispatcher("/jsp/messages.jsp").forward(req, resp);
    }
    
    private void handleSendMessage(HttpServletRequest req, HttpServletResponse resp, User user) 
            throws SQLException, ServletException, IOException {
        
        String recipientUsername = req.getParameter("recipient");
        String messageType = req.getParameter("messageType");
        String content = req.getParameter("content");
        String quizIdParam = req.getParameter("quizId");
        
        if (recipientUsername == null || messageType == null || content == null) {
            req.setAttribute("error", "Missing required fields");
            handleViewMessages(req, resp, user);
            return;
        }
        
        User recipient = userDAO.findByUsername(recipientUsername);
        if (recipient == null) {
            req.setAttribute("error", "Recipient not found");
            handleViewMessages(req, resp, user);
            return;
        }
        // Block sending friend request to yourself immediately
        if (user.getUserId() == recipient.getUserId() && Message.TYPE_FRIEND_REQUEST.equals(messageType)) {
            req.setAttribute("error", "You cannot send a friend request to yourself.");
            handleViewMessages(req, resp, user);
            return;
        }
        
        Message message = null;
        boolean handled = false;
        
        switch (messageType) {
            case Message.TYPE_NOTE:
                message = messageDAO.sendNote(user.getUserId(), recipient.getUserId(), content);
                if (message != null) {
                    req.setAttribute("success", "Message sent successfully");
                } else {
                    req.setAttribute("error", "Failed to send message");
                }
                handled = true;
                break;
            case Message.TYPE_FRIEND_REQUEST:
                // Use FriendshipDAO for robust checks
                if (user.getUserId() == recipient.getUserId()) {
                    req.setAttribute("error", "You cannot send a friend request to yourself.");
                    handleViewMessages(req, resp, user);
                    return;
                } else if (friendshipDAO.areFriends(user.getUserId(), recipient.getUserId())) {
                    req.setAttribute("error", "You are already friends with this user.");
                } else if (friendshipDAO.hasPendingRequest(user.getUserId(), recipient.getUserId()) ||
                           friendshipDAO.hasPendingRequest(recipient.getUserId(), user.getUserId())) {
                    req.setAttribute("error", "A friend request is already pending between you and this user.");
                } else {
                    try {
                        friendshipDAO.sendFriendRequest(user.getUserId(), recipient.getUserId());
                        message = messageDAO.sendFriendRequest(user.getUserId(), recipient.getUserId(), content);
                        if (message != null) {
                            req.setAttribute("success", "Friend request sent successfully");
                        } else {
                            req.setAttribute("error", "Failed to send friend request message");
                        }
                    } catch (Exception e) {
                        req.setAttribute("error", "Failed to create friendship: " + e.getMessage());
                    }
                }
                handled = true;
                break;
            case Message.TYPE_CHALLENGE:
                String quizNameParam = req.getParameter("quizName");
                if (quizNameParam != null && !quizNameParam.trim().isEmpty()) {
                    model.Quiz quiz = quizDAO.findByTitle(quizNameParam.trim());
                    if (quiz == null) {
                        req.setAttribute("error", "Quiz not found with that name");
                    } else {
                        // Friendship check before sending challenge
                        if (!friendshipDAO.areFriends(user.getUserId(), recipient.getUserId())) {
                            req.setAttribute("error", "You can only send a challenge to users who are your friends.");
                            handleViewMessages(req, resp, user);
                            return;
                        }
                        int senderId = user.getUserId();
                        double bestScore;
                        try {
                            bestScore = quizAttemptDAO.getBestScore(senderId, quiz.getQuizId(), false);
                        } catch (Exception e) {
                            req.setAttribute("error", "Error checking your quiz attempts. Please try again.");
                            handleViewMessages(req, resp, user);
                            return;
                        }
                        if (bestScore < 0) {
                            req.setAttribute("error", "You must complete this quiz (not in practice mode) before you can send a challenge!");
                            handleViewMessages(req, resp, user);
                            return;
                        }
                        // Format the challenge message to include the score and quiz name (not id), and remove 'Can you beat it?'
                        String challengeMsg = "I challenge you to quiz '" + quiz.getTitle() + "'! My best score is: " + String.format("%.2f", bestScore) + ".";
                        req.setAttribute("challengeMessageContent", challengeMsg);
                        message = messageDAO.sendChallenge(user.getUserId(), recipient.getUserId(), challengeMsg, quiz.getQuizId());
                        if (message != null) {
                            message.setQuizName(quiz.getTitle());
                            req.setAttribute("success", "Challenge sent successfully");
                        } else {
                            req.setAttribute("error", "Failed to send challenge");
                        }
                    }
                } else {
                    req.setAttribute("error", "Quiz name is required for a challenge");
                }
                handled = true;
                break;
        }
        
        if (!handled) {
            req.setAttribute("error", "Invalid message type or failed to send message");
        }
        
        handleViewMessages(req, resp, user);
    }
    
    private void handleMarkAsRead(HttpServletRequest req, HttpServletResponse resp, User user)
            throws SQLException, ServletException, IOException {
        String messageIdParam = req.getParameter("messageId");
        if (messageIdParam != null) {
            int messageId = Integer.parseInt(messageIdParam);
            messageDAO.markAsRead(messageId);
        }
        resp.sendRedirect(req.getContextPath() + "/messages");
    }
    
    private void handleDeleteMessage(HttpServletRequest req, HttpServletResponse resp, User user)
            throws SQLException, ServletException, IOException {
        String messageIdParam = req.getParameter("messageId");
        if (messageIdParam != null) {
            int messageId = Integer.parseInt(messageIdParam);
            messageDAO.deleteMessage(messageId);
        }
        resp.sendRedirect(req.getContextPath() + "/messages");
    }
    
    private User getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }
}
