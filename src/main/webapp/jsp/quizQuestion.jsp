<%--
  Created by IntelliJ IDEA.
  User: nika
  Date: 7/8/2025
  Time: 6:12 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Question" %>
<%
    Question question = (Question) request.getAttribute("question");
    int questionNumber = (request.getAttribute("questionNumber") != null) ? (Integer) request.getAttribute("questionNumber") : 1;
    int totalQuestions = (request.getAttribute("totalQuestions") != null) ? (Integer) request.getAttribute("totalQuestions") : 1;
    Boolean practiceMode = (Boolean) request.getAttribute("practiceMode");
    model.Quiz quiz = (model.Quiz) session.getAttribute("currentQuiz");
    Boolean immediateCorrection = (quiz != null) ? quiz.isImmediateCorrection() : false;
    String feedback = (String) request.getAttribute("feedback");
    String correctAnswer = (String) request.getAttribute("correctAnswer");
    String submittedAnswer = (String) request.getAttribute("submittedAnswer");
    boolean showNext = (immediateCorrection != null && immediateCorrection && feedback != null);
%>
<!DOCTYPE html>
<html>
<head>
    <title>Quiz Question</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        /* =========================== GLOBAL STYLES =========================== */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 40px 20px;
            line-height: 1.6;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        /* =========================== MAIN CONTAINER =========================== */
        .quiz-container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 25px 80px rgba(0, 0, 0, 0.15);
            width: 100%;
            max-width: 700px;
            position: relative;
            overflow: hidden;
        }
        
        .quiz-container::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #667eea, #764ba2);
        }
        
        /* =========================== HEADER SECTION =========================== */
        .quiz-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px 40px;
            text-align: center;
            position: relative;
        }
        
        .quiz-header::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: 
                radial-gradient(circle at 20% 30%, rgba(255,255,255,0.1) 0%, transparent 50%),
                radial-gradient(circle at 80% 70%, rgba(255,255,255,0.08) 0%, transparent 50%);
            pointer-events: none;
        }
        
        .progress-section {
            position: relative;
            z-index: 2;
        }
        
        .question-counter {
            font-size: 1.1rem;
            font-weight: 600;
            margin-bottom: 15px;
            opacity: 0.9;
        }
        
        .progress-bar {
            background: rgba(255, 255, 255, 0.2);
            height: 8px;
            border-radius: 4px;
            overflow: hidden;
            margin-bottom: 20px;
        }
        
        .progress-fill {
            background: rgba(255, 255, 255, 0.8);
            height: 100%;
            border-radius: 4px;
            transition: width 0.3s ease;
        }
        
        .practice-notice {
            background: rgba(255, 255, 255, 0.15);
            border: 2px solid rgba(255, 255, 255, 0.2);
            border-radius: 12px;
            padding: 15px 20px;
            margin-top: 20px;
            font-weight: 600;
            backdrop-filter: blur(10px);
        }
        
        /* =========================== CONTENT SECTION =========================== */
        .quiz-content {
            padding: 40px;
        }
        
        /* =========================== QUESTION SECTION =========================== */
        .question-section {
            margin-bottom: 30px;
        }
        
        .question-text {
            font-size: 1.3rem;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 25px;
            line-height: 1.5;
        }
        
        .question-image {
            max-width: 100%;
            height: auto;
            max-height: 350px;
            border-radius: 12px;
            margin: 20px 0;
            box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
            display: block;
            margin-left: auto;
            margin-right: auto;
        }
        
        /* =========================== ANSWER SECTION =========================== */
        .answer-section {
            margin-bottom: 30px;
        }
        
        .answer-options {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }
        
        .option-item {
            position: relative;
        }
        
        .option-label {
            display: flex;
            align-items: center;
            padding: 18px 20px;
            background: #f8f9fa;
            border: 2px solid #e8ecf0;
            border-radius: 12px;
            cursor: pointer;
            transition: all 0.3s ease;
            font-weight: 500;
            font-size: 1rem;
        }
        
        .option-label:hover {
            border-color: #667eea;
            background: #f0f4ff;
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
        }
        
        .option-label.disabled {
            opacity: 0.7;
            cursor: not-allowed;
        }
        
        .option-label.disabled:hover {
            transform: none;
            box-shadow: none;
        }
        
        .option-input {
            margin-right: 15px;
            transform: scale(1.3);
            accent-color: #667eea;
        }
        
        .text-input {
            width: 100%;
            padding: 18px 20px;
            border: 2px solid #e8ecf0;
            border-radius: 12px;
            font-size: 16px;
            background: #f8f9fa;
            transition: all 0.3s ease;
            font-weight: 500;
        }
        
        .text-input:focus {
            outline: none;
            border-color: #667eea;
            background: white;
            box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
        }
        
        .text-input:disabled {
            opacity: 0.7;
            cursor: not-allowed;
        }
        
        /* =========================== FEEDBACK SECTION =========================== */
        .feedback-section {
            margin: 25px 0;
            padding: 20px;
            border-radius: 12px;
            font-weight: 600;
            animation: fadeIn 0.3s ease;
        }
        
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        .feedback-correct {
            background: linear-gradient(135deg, #00b894, #00a085);
            color: white;
            border-left: 5px solid #00a085;
        }
        
        .feedback-incorrect {
            background: linear-gradient(135deg, #ff6b6b, #ee5a24);
            color: white;
            border-left: 5px solid #ee5a24;
        }
        
        .feedback-text {
            font-size: 1.1rem;
            margin-bottom: 10px;
        }
        
        .correct-answer {
            font-size: 1rem;
            opacity: 0.9;
            font-weight: 500;
        }
        
        /* =========================== ACTION SECTION =========================== */
        .action-section {
            text-align: center;
            padding-top: 25px;
            border-top: 2px solid #e8ecf0;
        }
        
        .action-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 18px 40px;
            font-size: 16px;
            font-weight: 600;
            border-radius: 12px;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
            min-width: 140px;
        }
        
        .action-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
        }
        
        .action-btn:active {
            transform: translateY(0);
        }
        
        .btn-submit {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        
        .btn-next {
            background: linear-gradient(135deg, #00b894 0%, #00a085 100%);
            box-shadow: 0 6px 20px rgba(0, 184, 148, 0.3);
        }
        
        .btn-next:hover {
            box-shadow: 0 12px 35px rgba(0, 184, 148, 0.4);
        }
        
        /* =========================== RESPONSIVE DESIGN =========================== */
        @media (max-width: 768px) {
            body {
                padding: 20px 10px;
            }
            
            .quiz-container {
                border-radius: 16px;
            }
            
            .quiz-header,
            .quiz-content {
                padding: 25px 20px;
            }
            
            .question-text {
                font-size: 1.2rem;
            }
            
            .option-label,
            .text-input {
                padding: 15px 16px;
            }
            
            .action-btn {
                padding: 16px 32px;
                font-size: 15px;
            }
        }
    </style>
</head>
<body>
    <div class="quiz-container">
        <div class="quiz-header">
            <div class="progress-section">
                <div class="question-counter">
                    Question <%= questionNumber %> of <%= totalQuestions %>
                </div>
                <div class="progress-bar">
                    <div class="progress-fill" style="width: <%= (questionNumber * 100.0 / totalQuestions) %>%;"></div>
                </div>
                <% if (practiceMode != null && practiceMode) { %>
                    <div class="practice-notice">
                        🎯 You are taking this quiz in Practice Mode. Your score will not appear on the leaderboard.
                    </div>
                <% } %>
            </div>
        </div>
        
        <div class="quiz-content">
            <div class="question-section">
                <div class="question-text"><%= question.getQuestionText() %></div>
                
                <% if ("picture-response".equals(question.getQuestionType()) && question.getImageUrl() != null && !question.getImageUrl().isEmpty()) { %>
                    <img src="<%= question.getImageUrl() %>" alt="Question Image" class="question-image"/>
                <% } %>
            </div>
            
            <form action="${pageContext.request.contextPath}/takeQuiz" method="post">
                <input type="hidden" name="questionNumber" value="<%= questionNumber %>" />
                <input type="hidden" name="feedbackState" value="<%= (showNext ? "shown" : "none") %>" />
                
                <div class="answer-section">
                    <% if ("multiple-choice".equals(question.getQuestionType()) && question.getChoices() != null) { %>
                        <div class="answer-options">
                            <% for (String choice : question.getChoices()) { %>
                                <div class="option-item">
                                    <label class="option-label <%= showNext ? "disabled" : "" %>">
                                        <input type="radio" 
                                               name="answer" 
                                               value="<%= choice %>" 
                                               class="option-input"
                                               <%= showNext ? "disabled" : "" %> 
                                               <%= (submittedAnswer != null && submittedAnswer.equals(choice)) ? "checked" : "" %> 
                                               required>
                                        <%= choice %>
                                    </label>
                                </div>
                            <% } %>
                        </div>
                    <% } else { %>
                        <input type="text" 
                               name="answer" 
                               value="<%= submittedAnswer != null ? submittedAnswer : "" %>" 
                               class="text-input"
                               placeholder="Enter your answer..."
                               <%= showNext ? "disabled" : "" %> 
                               required />
                    <% } %>
                </div>
                
                <% if (immediateCorrection != null && immediateCorrection && feedback != null) { 
                     String feedbackClass = ("Correct".equals(feedback)) ? "feedback-correct" : "feedback-incorrect"; %>
                    <div class="feedback-section <%= feedbackClass %>">
                        <div class="feedback-text">
                            <%= "Correct".equals(feedback) ? "✅ " + feedback : "❌ " + feedback %>
                        </div>
                        <% if ("Incorrect".equals(feedback) && correctAnswer != null) { %>
                            <div class="correct-answer">💡 Correct answer: <%= correctAnswer %></div>
                        <% } %>
                    </div>
                <% } %>
                
                <div class="action-section">
                    <% if (!showNext) { %>
                        <button type="submit" name="action" value="submit" class="action-btn btn-submit">
                            📝 Submit Answer
                        </button>
                    <% } else { %>
                        <% if (questionNumber >= totalQuestions) { %>
                            <button type="submit" name="action" value="next" class="action-btn btn-next">
                                🎯 See Results
                            </button>
                        <% } else { %>
                            <button type="submit" name="action" value="next" class="action-btn btn-next">
                                ➡️ Next Question
                            </button>
                        <% } %>
                    <% } %>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
