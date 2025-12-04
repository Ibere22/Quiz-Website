package controller;

import dao.QuestionDAO;
import dao.QuizDAO;
import dao.QuizAttemptDAO;
import dao.AchievementDAO;
import model.Question;
import model.Quiz;
import model.QuizAttempt;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet(urlPatterns = {"/takeQuiz"})
public class QuizTakingController extends HttpServlet {
    private QuizDAO quizDAO;
    private QuestionDAO questionDAO;
    private QuizAttemptDAO quizAttemptDAO;
    private AchievementDAO achievementDAO;

    @Override
    public void init() throws ServletException {
        try {
            Connection connection = (Connection) getServletContext().getAttribute("DBConnection");
            quizDAO = (QuizDAO) getServletContext().getAttribute("quizDAO");
            questionDAO = (QuestionDAO) getServletContext().getAttribute("questionDAO");
            quizAttemptDAO = (QuizAttemptDAO) getServletContext().getAttribute("quizAttemptDAO");
            achievementDAO = (AchievementDAO) getServletContext().getAttribute("achievementDAO");
        } catch (Exception e) {
            throw new ServletException("DB connection error", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Start quiz attempt: load quiz, questions, set session state, show first question
        HttpSession session = req.getSession();
        String quizIdStr = req.getParameter("id");
        boolean practiceMode = "true".equals(req.getParameter("practiceMode"));
        if (quizIdStr == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Quiz ID required");
            return;
        }
        int quizId = Integer.parseInt(quizIdStr);
        try {
            Quiz quiz = quizDAO.findById(quizId);
            List<Question> questions = questionDAO.getQuestionsByQuizId(quizId);
            if (quiz == null || questions.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Quiz not found");
                return;
            }
            if (quiz.isRandomOrder()) {
                java.util.Collections.shuffle(questions);
            }
            session.setAttribute("currentQuiz", quiz);
            session.setAttribute("quizQuestions", questions);
            session.setAttribute("currentQuestionIndex", 0);
            session.setAttribute("userAnswers", new ArrayList<String>());
            session.setAttribute("quizStartTime", System.currentTimeMillis());
            session.setAttribute("practiceMode", practiceMode);
            // Branch: one page or multi-page
            if (quiz.isOnePage()) {
                req.setAttribute("questions", questions);
                req.setAttribute("practiceMode", practiceMode);
                req.getRequestDispatcher("/jsp/quizAllQuestions.jsp").forward(req, resp);
            } else {
                req.setAttribute("question", questions.get(0));
                req.setAttribute("questionNumber", 1);
                req.setAttribute("totalQuestions", questions.size());
                req.setAttribute("practiceMode", practiceMode);
                req.getRequestDispatcher("/jsp/quizQuestion.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Quiz quiz = (Quiz) session.getAttribute("currentQuiz");
        List<Question> questions = (List<Question>) session.getAttribute("quizQuestions");
        Integer currentIndex = (Integer) session.getAttribute("currentQuestionIndex");
        ArrayList<String> userAnswers = (ArrayList<String>) session.getAttribute("userAnswers");
        Boolean practiceMode = (Boolean) session.getAttribute("practiceMode");
        String allAtOnce = req.getParameter("allAtOnce");
        if (quiz == null || questions == null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        // All-at-once mode
        if ("true".equals(allAtOnce)) {
            int correct = 0;
            ArrayList<String> allAnswers = new ArrayList<>();
            for (int i = 0; i < questions.size(); i++) {
                String answer = req.getParameter("answer" + i);
                allAnswers.add(answer != null ? answer : "");
                String[] correctAnswers = questions.get(i).getCorrectAnswer().split(",");
                boolean isCorrect = false;
                if (answer != null) {
                    for (String ca : correctAnswers) {
                        if (answer.trim().equalsIgnoreCase(ca.trim())) {
                            isCorrect = true;
                            break;
                        }
                    }
                }
                if (isCorrect) correct++;
            }
            double score = (double) correct / questions.size() * 100.0;
            long startTime = (long) session.getAttribute("quizStartTime");
            long timeTaken = (System.currentTimeMillis() - startTime) / 1000;
            // Save attempt if user is logged in
            User user = (User) session.getAttribute("user");
            if (user != null) {
                try {
                    if (practiceMode != null && practiceMode) {
                        quizAttemptDAO.createPracticeAttempt(user.getUserId(), quiz.getQuizId(), score, questions.size(), timeTaken);
                        achievementDAO.awardAchievement(user.getUserId(), Achievement.PRACTICE_MAKES_PERFECT);
                    } else {
                        quizAttemptDAO.createSimpleAttempt(user.getUserId(), quiz.getQuizId(), score, questions.size(), timeTaken);
                        int nonPracticeAttempts = quizAttemptDAO.getAttemptCountByUser(user.getUserId()) - quizAttemptDAO.getPracticeAttemptCount();
                        if (nonPracticeAttempts >= 10) {
                            achievementDAO.awardAchievement(user.getUserId(), Achievement.QUIZ_MACHINE);
                        }
                        // Award I_AM_THE_GREATEST if user is top scorer for this quiz
                        List<model.QuizAttempt> topAttempts = quizAttemptDAO.getTopScoresForQuiz(quiz.getQuizId(), 1, false);
                        if (!topAttempts.isEmpty() && topAttempts.get(0).getUserId() == user.getUserId()) {
                            achievementDAO.awardAchievement(user.getUserId(), Achievement.I_AM_THE_GREATEST);
                        }
                    }
                } catch (SQLException e) {
                    throw new ServletException(e);
                }
            }
            req.setAttribute("score", score);
            req.setAttribute("correct", correct);
            req.setAttribute("totalQuestions", questions.size());
            req.setAttribute("timeTaken", timeTaken);
            req.setAttribute("practiceMode", practiceMode);
            // Clean up session
            session.removeAttribute("currentQuiz");
            session.removeAttribute("quizQuestions");
            session.removeAttribute("currentQuestionIndex");
            session.removeAttribute("userAnswers");
            session.removeAttribute("quizStartTime");
            session.removeAttribute("practiceMode");
            req.getRequestDispatcher("/jsp/quizResult.jsp").forward(req, resp);
            return;
        }
        // Multi-page, immediate correction logic
        if (quiz != null && quiz.isImmediateCorrection()) {
            String action = req.getParameter("action");
            String feedbackState = req.getParameter("feedbackState");
            if (action != null && "submit".equals(action) && (feedbackState == null || !"shown".equals(feedbackState))) {
                // Show feedback for current question
                if (currentIndex == null) currentIndex = 0;
                String answer = req.getParameter("answer");
                if (userAnswers == null) userAnswers = new ArrayList<>();
                // Store the answer for this question (replace if already present)
                if (userAnswers.size() > currentIndex) {
                    userAnswers.set(currentIndex, answer != null ? answer : "");
                } else {
                    userAnswers.add(answer != null ? answer : "");
                }
                Question currentQuestion = questions.get(currentIndex);
                String[] correctAnswers = currentQuestion.getCorrectAnswer().split(",");
                boolean isCorrect = false;
                if (answer != null) {
                    for (String ca : correctAnswers) {
                        if (answer.trim().equalsIgnoreCase(ca.trim())) {
                            isCorrect = true;
                            break;
                        }
                    }
                }
                req.setAttribute("question", currentQuestion);
                req.setAttribute("questionNumber", currentIndex + 1);
                req.setAttribute("totalQuestions", questions.size());
                req.setAttribute("practiceMode", practiceMode);
                req.setAttribute("submittedAnswer", answer);
                req.setAttribute("feedback", isCorrect ? "Correct" : "Incorrect");
                if (!isCorrect) req.setAttribute("correctAnswer", currentQuestion.getCorrectAnswer());
                // Mark that feedback has been shown for this question
                session.setAttribute("feedbackShown", true);
                req.getRequestDispatcher("/jsp/quizQuestion.jsp").forward(req, resp);
                return;
            } else if (action != null && ("next".equals(action) || ("submit".equals(action) && "shown".equals(feedbackState)))) {
                // Move to next question
                session.setAttribute("feedbackShown", false);
                currentIndex = (currentIndex == null) ? 0 : currentIndex + 1;
                if (currentIndex < questions.size()) {
                    session.setAttribute("currentQuestionIndex", currentIndex);
                    session.setAttribute("userAnswers", userAnswers);
                    req.setAttribute("question", questions.get(currentIndex));
                    req.setAttribute("questionNumber", currentIndex + 1);
                    req.setAttribute("totalQuestions", questions.size());
                    req.setAttribute("practiceMode", practiceMode);
                    req.getRequestDispatcher("/jsp/quizQuestion.jsp").forward(req, resp);
                    return;
                } else {
                    // Quiz finished: grade and show result
                    int correct = 0;
                    for (int i = 0; i < questions.size(); i++) {
                        String[] correctAnswers = questions.get(i).getCorrectAnswer().split(",");
                        String userAnswer = userAnswers.get(i).trim();
                        boolean isCorrect = false;
                        for (String ca : correctAnswers) {
                            if (userAnswer.equalsIgnoreCase(ca.trim())) {
                                isCorrect = true;
                                break;
                            }
                        }
                        if (isCorrect) correct++;
                    }
                    double score = (double) correct / questions.size() * 100.0;
                    long startTime = (long) session.getAttribute("quizStartTime");
                    long timeTaken = (System.currentTimeMillis() - startTime) / 1000;
                    // Save attempt if user is logged in
                    User user = (User) session.getAttribute("user");
                    if (user != null) {
                        try {
                            if (practiceMode != null && practiceMode) {
                                quizAttemptDAO.createPracticeAttempt(user.getUserId(), quiz.getQuizId(), score, questions.size(), timeTaken);
                                achievementDAO.awardAchievement(user.getUserId(), Achievement.PRACTICE_MAKES_PERFECT);
                            } else {
                                quizAttemptDAO.createSimpleAttempt(user.getUserId(), quiz.getQuizId(), score, questions.size(), timeTaken);
                                int nonPracticeAttempts = quizAttemptDAO.getAttemptCountByUser(user.getUserId()) - quizAttemptDAO.getPracticeAttemptCount();
                                if (nonPracticeAttempts >= 10) {
                                    achievementDAO.awardAchievement(user.getUserId(), Achievement.QUIZ_MACHINE);
                                }
                                // Award I_AM_THE_GREATEST if user is top scorer for this quiz
                                List<model.QuizAttempt> topAttempts = quizAttemptDAO.getTopScoresForQuiz(quiz.getQuizId(), 1, false);
                                if (!topAttempts.isEmpty() && topAttempts.get(0).getUserId() == user.getUserId()) {
                                    achievementDAO.awardAchievement(user.getUserId(), Achievement.I_AM_THE_GREATEST);
                                }
                            }
                        } catch (SQLException e) {
                            throw new ServletException(e);
                        }
                    }
                    req.setAttribute("score", score);
                    req.setAttribute("correct", correct);
                    req.setAttribute("totalQuestions", questions.size());
                    req.setAttribute("timeTaken", timeTaken);
                    req.setAttribute("practiceMode", practiceMode);
                    // Clean up session
                    session.removeAttribute("currentQuiz");
                    session.removeAttribute("quizQuestions");
                    session.removeAttribute("currentQuestionIndex");
                    session.removeAttribute("userAnswers");
                    session.removeAttribute("quizStartTime");
                    session.removeAttribute("practiceMode");
                    session.removeAttribute("feedbackShown");
                    req.getRequestDispatcher("/jsp/quizResult.jsp").forward(req, resp);
                    return;
                }
            } else {
                // If action is not recognized, just reload current question with no feedback, do not advance
                if (currentIndex == null) currentIndex = 0;
                Question currentQuestion = questions.get(currentIndex);
                req.setAttribute("question", currentQuestion);
                req.setAttribute("questionNumber", currentIndex + 1);
                req.setAttribute("totalQuestions", questions.size());
                req.setAttribute("practiceMode", practiceMode);
                req.setAttribute("submittedAnswer", (userAnswers != null && userAnswers.size() > currentIndex) ? userAnswers.get(currentIndex) : "");
                req.getRequestDispatcher("/jsp/quizQuestion.jsp").forward(req, resp);
                return;
            }
        }
        // Fallback logic for non-immediate-correction quizzes only
        // Get submitted answer
        String answer = req.getParameter("answer");
        userAnswers.add(answer != null ? answer : "");
        currentIndex++;
        if (currentIndex < questions.size()) {
            // Next question
            session.setAttribute("currentQuestionIndex", currentIndex);
            session.setAttribute("userAnswers", userAnswers);
            req.setAttribute("question", questions.get(currentIndex));
            req.setAttribute("questionNumber", currentIndex + 1);
            req.setAttribute("totalQuestions", questions.size());
            req.setAttribute("practiceMode", practiceMode);
            req.getRequestDispatcher("/jsp/quizQuestion.jsp").forward(req, resp);
        } else {
            // Quiz finished: grade and show result
            int correct = 0;
            for (int i = 0; i < questions.size(); i++) {
                String[] correctAnswers = questions.get(i).getCorrectAnswer().split(",");
                String userAnswer = userAnswers.get(i).trim();
                boolean isCorrect = false;
                for (String ca : correctAnswers) {
                    if (userAnswer.equalsIgnoreCase(ca.trim())) {
                        isCorrect = true;
                        break;
                    }
                }
                if (isCorrect) {
                    correct++;
                }
            }
            double score = (double) correct / questions.size() * 100.0;
            long startTime = (long) session.getAttribute("quizStartTime");
            long timeTaken = (System.currentTimeMillis() - startTime) / 1000;
            // Save attempt if user is logged in
            User user = (User) session.getAttribute("user");
            if (user != null) {
                try {
                    if (practiceMode != null && practiceMode) {
                        quizAttemptDAO.createPracticeAttempt(user.getUserId(), quiz.getQuizId(), score, questions.size(), timeTaken);
                        achievementDAO.awardAchievement(user.getUserId(), Achievement.PRACTICE_MAKES_PERFECT);
                    } else {
                        quizAttemptDAO.createSimpleAttempt(user.getUserId(), quiz.getQuizId(), score, questions.size(), timeTaken);
                        int nonPracticeAttempts = quizAttemptDAO.getAttemptCountByUser(user.getUserId()) - quizAttemptDAO.getPracticeAttemptCount();
                        if (nonPracticeAttempts >= 10) {
                            achievementDAO.awardAchievement(user.getUserId(), Achievement.QUIZ_MACHINE);
                        }
                        // Award I_AM_THE_GREATEST if user is top scorer for this quiz
                        List<model.QuizAttempt> topAttempts = quizAttemptDAO.getTopScoresForQuiz(quiz.getQuizId(), 1, false);
                        if (!topAttempts.isEmpty() && topAttempts.get(0).getUserId() == user.getUserId()) {
                            achievementDAO.awardAchievement(user.getUserId(), Achievement.I_AM_THE_GREATEST);
                        }
                    }
                } catch (SQLException e) {
                    throw new ServletException(e);
                }
            }
            req.setAttribute("score", score);
            req.setAttribute("correct", correct);
            req.setAttribute("totalQuestions", questions.size());
            req.setAttribute("timeTaken", timeTaken);
            req.setAttribute("practiceMode", practiceMode);
            // Clean up session
            session.removeAttribute("currentQuiz");
            session.removeAttribute("quizQuestions");
            session.removeAttribute("currentQuestionIndex");
            session.removeAttribute("userAnswers");
            session.removeAttribute("quizStartTime");
            session.removeAttribute("practiceMode");
            req.getRequestDispatcher("/jsp/quizResult.jsp").forward(req, resp);
        }
    }
}
