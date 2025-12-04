<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Question" %>
<%
    List<Question> questions = (List<Question>) request.getAttribute("questions");
    Boolean practiceMode = (Boolean) request.getAttribute("practiceMode");
    model.Quiz quiz = (model.Quiz) session.getAttribute("currentQuiz");
    Boolean immediateCorrection = (quiz != null) ? quiz.isImmediateCorrection() : false;
    String[] feedbacks = (String[]) request.getAttribute("feedbacks"); // Optional: feedback per question
    String[] correctAnswers = (String[]) request.getAttribute("correctAnswers"); // Optional: correct answers per question
%>
<!DOCTYPE html>
<html>
<head>
    <title>Quiz - All Questions</title>
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
        }
        
        /* =========================== MAIN CONTAINER =========================== */
        .main-container {
            max-width: 900px;
            margin: 0 auto;
            background: white;
            border-radius: 20px;
            box-shadow: 0 25px 80px rgba(0, 0, 0, 0.15);
            overflow: hidden;
            position: relative;
        }
        
        .main-container::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #667eea, #764ba2);
        }
        
        /* =========================== HEADER SECTION =========================== */
        .page-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px;
            text-align: center;
        }
        
        .page-title {
            font-size: 2.2rem;
            font-weight: 700;
            margin-bottom: 10px;
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
        .content-area {
            padding: 40px;
        }
        
        /* =========================== QUESTION STYLES =========================== */
        .questions-list {
            list-style: none;
            padding: 0;
        }
        
        .question-item {
            background: #f8f9fa;
            border-radius: 16px;
            padding: 30px;
            margin-bottom: 30px;
            border-left: 5px solid #667eea;
            transition: all 0.3s ease;
            position: relative;
        }
        
        .question-item:hover {
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
            transform: translateY(-2px);
        }
        
        .question-number {
            position: absolute;
            top: -10px;
            left: 20px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            padding: 8px 16px;
            border-radius: 20px;
            font-weight: 600;
            font-size: 14px;
        }
        
        .question-text {
            font-size: 1.1rem;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 20px;
            margin-top: 10px;
        }
        
        .question-image {
            max-width: 100%;
            height: auto;
            max-height: 300px;
            border-radius: 12px;
            margin: 15px 0;
            box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
        }
        
        /* =========================== ANSWER OPTIONS =========================== */
        .answer-options {
            margin: 20px 0;
        }
        
        .option-group {
            margin-bottom: 12px;
        }
        
        .option-label {
            display: flex;
            align-items: center;
            padding: 12px 16px;
            background: white;
            border: 2px solid #e8ecf0;
            border-radius: 12px;
            cursor: pointer;
            transition: all 0.3s ease;
            font-weight: 500;
        }
        
        .option-label:hover {
            border-color: #667eea;
            background: #f0f4ff;
        }
        
        .option-input {
            margin-right: 12px;
            transform: scale(1.2);
        }
        
        .text-input {
            width: 100%;
            padding: 16px;
            border: 2px solid #e8ecf0;
            border-radius: 12px;
            font-size: 16px;
            background: white;
            transition: all 0.3s ease;
        }
        
        .text-input:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
        }
        
        /* =========================== FEEDBACK STYLES =========================== */
        .feedback-section {
            margin-top: 20px;
            padding: 16px;
            border-radius: 12px;
            font-weight: 600;
        }
        
        .feedback-correct {
            background: linear-gradient(135deg, #00b894, #00a085);
            color: white;
        }
        
        .feedback-incorrect {
            background: linear-gradient(135deg, #ff6b6b, #ee5a24);
            color: white;
        }
        
        .correct-answer {
            margin-top: 8px;
            font-weight: 500;
            opacity: 0.9;
        }
        
        /* =========================== SUBMIT BUTTON =========================== */
        .submit-section {
            text-align: center;
            margin-top: 40px;
            padding-top: 30px;
            border-top: 2px solid #e8ecf0;
        }
        
        .submit-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 18px 40px;
            font-size: 18px;
            font-weight: 600;
            border-radius: 12px;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
        }
        
        .submit-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
        }
        
        .submit-btn:active {
            transform: translateY(0);
        }
        
        /* =========================== RESPONSIVE DESIGN =========================== */
        @media (max-width: 768px) {
            .main-container {
                margin: 0 10px;
                border-radius: 16px;
            }
            
            .page-header,
            .content-area {
                padding: 25px 20px;
            }
            
            .page-title {
                font-size: 1.8rem;
            }
            
            .question-item {
                padding: 20px;
            }
            
            .question-number {
                position: static;
                display: inline-block;
                margin-bottom: 10px;
            }
        }
    </style>
</head>
<body>
    <div class="main-container">
        <div class="page-header">
            <h1 class="page-title">📝 Quiz - All Questions</h1>
            <% if (practiceMode != null && practiceMode) { %>
                <div class="practice-notice">
                    🎯 You are taking this quiz in Practice Mode. Your score will not appear on the leaderboard.
                </div>
            <% } %>
        </div>
        
        <div class="content-area">
            <form action="${pageContext.request.contextPath}/takeQuiz" method="post">
                <input type="hidden" name="allAtOnce" value="true" />
                
                <ol class="questions-list">
                    <% for (int i = 0; i < questions.size(); i++) {
                        Question q = questions.get(i);
                    %>
                    <li class="question-item">
                        <div class="question-number">Question <%= i + 1 %></div>
                        
                        <div class="question-text"><%= q.getQuestionText() %></div>
                        
                        <% if ("picture-response".equals(q.getQuestionType()) && q.getImageUrl() != null && !q.getImageUrl().isEmpty()) { %>
                            <img src="<%= q.getImageUrl() %>" alt="Question Image" class="question-image"/>
                        <% } %>
                        
                        <div class="answer-options">
                            <% if ("multiple-choice".equals(q.getQuestionType()) && q.getChoices() != null) { %>
                                <% for (String choice : q.getChoices()) { %>
                                    <div class="option-group">
                                        <label class="option-label">
                                            <input type="radio" name="answer<%=i%>" value="<%= choice %>" class="option-input" required>
                                            <%= choice %>
                                        </label>
                                    </div>
                                <% } %>
                            <% } else { %>
                                <input type="text" name="answer<%=i%>" class="text-input" placeholder="Enter your answer..." required />
                            <% } %>
                        </div>
                        
                        <% if (immediateCorrection != null && immediateCorrection && feedbacks != null && feedbacks.length > i && feedbacks[i] != null) {
                            String feedbackClass = ("Correct".equals(feedbacks[i])) ? "feedback-correct" : "feedback-incorrect"; %>
                            <div class="feedback-section <%= feedbackClass %>">
                                <%= feedbacks[i] %>
                                <% if ("Incorrect".equals(feedbacks[i]) && correctAnswers != null && correctAnswers.length > i && correctAnswers[i] != null) { %>
                                    <div class="correct-answer">Correct answer: <%= correctAnswers[i] %></div>
                                <% } %>
                            </div>
                        <% } %>
                    </li>
                    <% } %>
                </ol>
                
                <div class="submit-section">
                    <button type="submit" class="submit-btn">🚀 Finish Quiz</button>
                </div>
            </form>
        </div>
    </div>
</body>
</html> 