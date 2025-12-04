<%--
  Created by IntelliJ IDEA.
  User: nika
  Date: 7/7/2025
  Time: 6:35 PM//
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Quiz" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    List<Quiz> quizzes = (List<Quiz>) request.getAttribute("quizzes");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Browse Quizzes - QuizWebsite</title>
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
            max-width: 1200px;
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
            position: relative;
        }
        
        .page-header::after {
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
        
        .header-content {
            position: relative;
            z-index: 2;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 20px;
        }
        
        .page-title {
            font-size: 2.5rem;
            font-weight: 700;
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        .back-btn {
            display: inline-block;
            padding: 12px 24px;
            background: rgba(255, 255, 255, 0.15);
            border: 2px solid rgba(255, 255, 255, 0.2);
            color: white;
            text-decoration: none;
            border-radius: 25px;
            font-weight: 600;
            transition: all 0.3s ease;
            backdrop-filter: blur(10px);
        }
        
        .back-btn:hover {
            background: rgba(255, 255, 255, 0.25);
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
            text-decoration: none;
            color: white;
        }
        
        /* =========================== CONTENT SECTION =========================== */
        .content-area {
            padding: 40px;
        }
        
        .quiz-count {
            text-align: center;
            margin-bottom: 30px;
            color: #6c757d;
            font-size: 1.1rem;
        }
        
        /* =========================== QUIZ GRID =========================== */
        .quizzes-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
            gap: 25px;
        }
        
        .quiz-card {
            background: white;
            border: 2px solid #e8ecf0;
            border-radius: 16px;
            padding: 25px;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }
        
        .quiz-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #667eea, #764ba2);
            transform: scaleX(0);
            transition: transform 0.3s ease;
        }
        
        .quiz-card:hover {
            border-color: #667eea;
            transform: translateY(-5px);
            box-shadow: 0 15px 35px rgba(102, 126, 234, 0.2);
        }
        
        .quiz-card:hover::before {
            transform: scaleX(1);
        }
        
        .quiz-title {
            font-size: 1.4rem;
            font-weight: 700;
            color: #2c3e50;
            margin-bottom: 12px;
            line-height: 1.3;
        }
        
        .quiz-description {
            color: #6c757d;
            margin-bottom: 20px;
            line-height: 1.5;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        
        .quiz-meta {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            font-size: 0.9rem;
            color: #7f8c8d;
        }
        
        .quiz-date {
            background: #f8f9fa;
            padding: 6px 12px;
            border-radius: 20px;
            font-weight: 500;
        }
        
        .quiz-actions {
            display: flex;
            gap: 12px;
        }
        
        .quiz-btn {
            flex: 1;
            padding: 12px 20px;
            border: none;
            border-radius: 10px;
            font-weight: 600;
            font-size: 0.9rem;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
            text-align: center;
            display: inline-block;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
            text-decoration: none;
            color: white;
        }
        
        .btn-secondary {
            background: #f8f9fa;
            color: #667eea;
            border: 2px solid #e8ecf0;
        }
        
        .btn-secondary:hover {
            background: #e9ecef;
            border-color: #667eea;
            text-decoration: none;
            color: #667eea;
        }
        
        /* =========================== EMPTY STATE =========================== */
        .empty-state {
            text-align: center;
            padding: 80px 20px;
            color: #6c757d;
        }
        
        .empty-icon {
            font-size: 5rem;
            margin-bottom: 25px;
            opacity: 0.5;
        }
        
        .empty-title {
            font-size: 1.5rem;
            font-weight: 600;
            margin-bottom: 15px;
            color: #495057;
        }
        
        .empty-text {
            font-size: 1.1rem;
            margin-bottom: 30px;
            line-height: 1.6;
        }
        
        .empty-action {
            display: inline-block;
            padding: 16px 32px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            text-decoration: none;
            border-radius: 25px;
            font-weight: 600;
            font-size: 1.1rem;
            transition: all 0.3s ease;
        }
        
        .empty-action:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
            text-decoration: none;
            color: white;
        }
        
        /* =========================== RESPONSIVE DESIGN =========================== */
        @media (max-width: 768px) {
            body {
                padding: 20px 10px;
            }
            
            .main-container {
                margin: 0;
                border-radius: 16px;
            }
            
            .page-header {
                padding: 30px 20px;
            }
            
            .header-content {
                flex-direction: column;
                text-align: center;
            }
            
            .page-title {
                font-size: 2rem;
                justify-content: center;
            }
            
            .content-area {
                padding: 20px;
            }
            
            .quizzes-grid {
                grid-template-columns: 1fr;
                gap: 20px;
            }
            
            .quiz-card {
                padding: 20px;
            }
            
            .quiz-actions {
                flex-direction: column;
            }
        }
        
        @media (max-width: 480px) {
            .page-title {
                font-size: 1.8rem;
                flex-direction: column;
                gap: 10px;
            }
            
            .quiz-title {
                font-size: 1.2rem;
            }
            
            .quiz-meta {
                flex-direction: column;
                gap: 10px;
                align-items: flex-start;
            }
        }
    </style>
</head>
<body>
    <div class="main-container">
        <div class="page-header">
            <div class="header-content">
                <h1 class="page-title">
                    📚 Browse Quizzes
                </h1>
                <a href="${pageContext.request.contextPath}/" class="back-btn">← Back to Home</a>
            </div>
        </div>
        
        <div class="content-area">
            <% if (quizzes != null && !quizzes.isEmpty()) { %>
                <div class="quiz-count">
                    Showing <%= quizzes.size() %> quiz<%= quizzes.size() != 1 ? "es" : "" %> available
                </div>
                
                <div class="quizzes-grid">
                    <% for (Quiz quiz : quizzes) { %>
                        <div class="quiz-card">
                            <h3 class="quiz-title"><%= quiz.getTitle() %></h3>
                            <p class="quiz-description">
                                <%= quiz.getDescription() != null ? quiz.getDescription() : "Test your knowledge with this quiz!" %>
                            </p>
                            <div class="quiz-meta">
                                <span class="quiz-date">
                                    📅 <%= quiz.getCreatedDate() != null ? quiz.getCreatedDate().toString().substring(0,10) : "Recently created" %>
                                </span>
                            </div>
                            <div class="quiz-actions">
                                <a href="${pageContext.request.contextPath}/quiz?id=<%= quiz.getQuizId() %>" class="quiz-btn btn-primary">
                                    🎯 Take Quiz
                                </a>
                                <a href="${pageContext.request.contextPath}/quiz-summery?quizId=<%= quiz.getQuizId() %>" class="quiz-btn btn-secondary">
                                    📊 Leaderboard
                                </a>
                            </div>
                        </div>
                    <% } %>
                </div>
            <% } else { %>
                <div class="empty-state">
                    <div class="empty-icon">📚</div>
                    <div class="empty-title">No Quizzes Available Yet</div>
                    <div class="empty-text">
                        Be the first to create a quiz and share your knowledge with others!<br>
                        Start building engaging quizzes that challenge and educate.
                    </div>
                    <a href="${pageContext.request.contextPath}/quiz/create" class="empty-action">
                        ➕ Create Your First Quiz
                    </a>
                </div>
            <% } %>
        </div>
    </div>
</body>
</html>
