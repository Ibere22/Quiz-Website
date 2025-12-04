package controller;

import dao.QuizDAO;
import model.Quiz;
import dao.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import dao.QuestionDAO;
import model.Question;
import java.util.ArrayList;
import dao.AchievementDAO;
import model.Achievement;

@WebServlet(urlPatterns = {"/quizzes", "/quiz", "/quiz/create", "/quiz/addQuestion"})
public class QuizController extends HttpServlet {
    private QuizDAO quizDAO;
    private QuestionDAO questionDAO;
    private AchievementDAO achievementDAO;

    @Override
    public void init() throws ServletException
    {
        try
        {
            Connection connection = (Connection) getServletContext().getAttribute("DBConnection");
            quizDAO = (QuizDAO)getServletContext().getAttribute("quizDAO");
            questionDAO = (QuestionDAO)getServletContext().getAttribute("questionDAO");
            achievementDAO = (AchievementDAO)getServletContext().getAttribute("achievementDAO");
        }
        catch (Exception e)
        {
            throw new ServletException("DB connection error", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        try {
            switch (path) {
                case "/quizzes":
                    List<Quiz> quizzes = quizDAO.getAllQuizzes();
                    req.setAttribute("quizzes", quizzes);
                    req.getRequestDispatcher("/jsp/quizzes.jsp").forward(req, resp);
                    break;
                case "/quiz":
                    if (req.getSession().getAttribute("user") == null) {
                        resp.sendRedirect("login");
                        return;
                    }
                    int quizId = Integer.parseInt(req.getParameter("id"));
                    Quiz quiz = quizDAO.findById(quizId);
                    req.setAttribute("quiz", quiz);
                    req.getRequestDispatcher("/jsp/quiz.jsp").forward(req, resp);
                    break;
                case "/quiz/create":
                    if (req.getSession().getAttribute("user") == null) {
                        resp.sendRedirect("login");
                        return;
                    }
                    req.getRequestDispatcher("/jsp/createQuiz.jsp").forward(req, resp);
                    break;
                case "/quiz/addQuestion":
                    if (req.getSession().getAttribute("user") == null) {
                        resp.sendRedirect("login");
                        return;
                    }
                    // Only require quizId if you are editing an existing quiz
                    String quizIdStr = req.getParameter("quizId");
                    if (quizIdStr != null) {
                         quizId = Integer.parseInt(quizIdStr);
                        req.setAttribute("quizId", quizId);
                    }
                    req.getRequestDispatcher("/jsp/addQuestion.jsp").forward(req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        try {
            switch (path) {
                case "/quiz/create":
                    if (req.getSession().getAttribute("user") == null) {
                        resp.sendRedirect("login");
                        return;
                    }
                    String title = req.getParameter("title");
                    String description = req.getParameter("description");
                    boolean randomOrder = "true".equals(req.getParameter("randomOrder"));
                    boolean onePage = "true".equals(req.getParameter("onePage"));
                    boolean immediateCorrection = "true".equals(req.getParameter("immediateCorrection"));
                    HttpSession session = req.getSession(false);
                    model.User user = (model.User) session.getAttribute("user");
                    if (user == null) {
                        resp.sendRedirect("login");
                        return;
                    }
                    // Store quiz info in session, not DB
                    session.setAttribute("pendingQuizTitle", title);
                    session.setAttribute("pendingQuizDescription", description);
                    session.setAttribute("pendingQuizRandomOrder", randomOrder);
                    session.setAttribute("pendingQuizOnePage", onePage);
                    session.setAttribute("pendingQuizImmediateCorrection", immediateCorrection);
                    session.setAttribute("pendingQuizQuestions", new ArrayList<Question>());
                    resp.sendRedirect(req.getContextPath() + "/quiz/addQuestion");
                    break;
                case "/quiz/addQuestion":
                    if (req.getSession().getAttribute("user") == null) {
                        resp.sendRedirect("login");
                        return;
                    }
                    HttpSession qSession = req.getSession(false);
                    String questionType = req.getParameter("questionType");
                    String questionText = req.getParameter("questionText");
                    String correctAnswer = req.getParameter("correctAnswer");
                    String[] choices = req.getParameterValues("choices");
                    String imageUrl = req.getParameter("imageUrl");
                    ArrayList<Question> pendingQuestions = (ArrayList<Question>) qSession.getAttribute("pendingQuizQuestions");
                    int orderNum = pendingQuestions.size() + 1;
                    Question question;
                    if ("multiple-choice".equals(questionType)) {
                        ArrayList<String> choiceList = new ArrayList<>();
                        if (choices != null) {
                            for (String c : choices) choiceList.add(c);
                        }
                        // Validation: correct answer must be in choices
                        boolean valid = false;
                        if (correctAnswer != null && !correctAnswer.trim().isEmpty()) {
                            for (String c : choiceList) {
                                if (c.trim().equalsIgnoreCase(correctAnswer.trim())) {
                                    valid = true;
                                    break;
                                }
                            }
                        }
                        if (!valid) {
                            req.setAttribute("error", "<span style='color:red;'>Correct answer must match one of the choices exactly.</span>");
                            req.setAttribute("questionType", questionType);
                            req.setAttribute("questionText", questionText);
                            req.setAttribute("choices", choiceList);
                            req.setAttribute("correctAnswer", correctAnswer);
                            req.getRequestDispatcher("/jsp/addQuestion.jsp").forward(req, resp);
                            return;
                        }
                        question = new Question(-1, questionType, questionText, correctAnswer, orderNum);
                        question.setChoices(choiceList);
                    } else if ("picture-response".equals(questionType)) {
                        question = new Question(-1, questionType, questionText, correctAnswer, orderNum);
                        question.setImageUrl(imageUrl);
                    } else {
                        question = new Question(-1, questionType, questionText, correctAnswer, orderNum);
                    }
                    pendingQuestions.add(question);
                    qSession.setAttribute("pendingQuizQuestions", pendingQuestions);
                    if (req.getParameter("addAnother") != null) {
                        resp.sendRedirect(req.getContextPath() + "/quiz/addQuestion");
                    } else {
                        // On finish: save quiz and questions to DB
                        String quizTitle = (String) qSession.getAttribute("pendingQuizTitle");
                        String quizDescription = (String) qSession.getAttribute("pendingQuizDescription");
                        boolean quizRandomOrder = qSession.getAttribute("pendingQuizRandomOrder") != null && (Boolean) qSession.getAttribute("pendingQuizRandomOrder");
                        boolean quizOnePage = qSession.getAttribute("pendingQuizOnePage") != null && (Boolean) qSession.getAttribute("pendingQuizOnePage");
                        boolean quizImmediateCorrection = qSession.getAttribute("pendingQuizImmediateCorrection") != null && (Boolean) qSession.getAttribute("pendingQuizImmediateCorrection");
                        model.User quizUser = (model.User) qSession.getAttribute("user");
                        int creatorId = quizUser.getUserId();
                        // Duplicate title check
                        if (quizDAO.findByTitle(quizTitle) != null) {
                            req.setAttribute("error", "A quiz with this title already exists. Please choose a different name.");
                            req.getRequestDispatcher("/jsp/createQuiz.jsp").forward(req, resp);
                            return;
                        }
                        Quiz quiz = new Quiz(quizTitle, quizDescription, creatorId);
                        quiz.setRandomOrder(quizRandomOrder);
                        quiz.setOnePage(quizOnePage);
                        quiz.setImmediateCorrection(quizImmediateCorrection);
                        quizDAO.createQuiz(quiz);
                        int quizId = quiz.getQuizId();
                        int qOrder = 1;
                        for (Question q : pendingQuestions) {
                            q.setQuizId(quizId);
                            q.setOrderNum(qOrder++);
                            questionDAO.createQuestion(q);
                        }
                        // Award achievements for quiz creation
                        int createdCount = quizDAO.getQuizzesByCreator(creatorId).size();
                        if (createdCount >= 1) {
                            achievementDAO.awardAchievement(creatorId, model.Achievement.AMATEUR_AUTHOR);
                        }
                        if (createdCount >= 5) {
                            achievementDAO.awardAchievement(creatorId, model.Achievement.PROLIFIC_AUTHOR);
                        }
                        if (createdCount >= 10) {
                            achievementDAO.awardAchievement(creatorId, model.Achievement.PRODIGIOUS_AUTHOR);
                        }
                        // Clear session data
                        qSession.removeAttribute("pendingQuizTitle");
                        qSession.removeAttribute("pendingQuizDescription");
                        qSession.removeAttribute("pendingQuizRandomOrder");
                        qSession.removeAttribute("pendingQuizOnePage");
                        qSession.removeAttribute("pendingQuizImmediateCorrection");
                        qSession.removeAttribute("pendingQuizQuestions");
                        resp.sendRedirect(req.getContextPath() + "/");
                    }
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
} 