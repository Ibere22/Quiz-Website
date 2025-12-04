<%--
  Created by IntelliJ IDEA.
  User: nika
  Date: 7/7/2025
  Time: 6:36 PM//
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Quiz" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    Quiz quiz = (Quiz) request.getAttribute("quiz");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Quiz Details</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            line-height: 1.6;
        }
        .nav-home {
            position: absolute;
            top: 20px;
            left: 20px;
            padding: 12px 24px;
            text-decoration: none;
            color: white;
            font-weight: 600;
            background: rgba(255, 255, 255, 0.15);
            border: 2px solid rgba(255, 255, 255, 0.2);
            border-radius: 30px;
            transition: all 0.3s ease;
            backdrop-filter: blur(10px);
            font-size: 14px;
        }
        .nav-home:hover {
            background: rgba(255, 255, 255, 0.25);
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
            text-decoration: none;
            color: white;
        }
        .main-container {
            background: white;
            padding: 45px 35px 35px 35px;
            border-radius: 20px;
            box-shadow: 0 25px 80px rgba(0, 0, 0, 0.15);
            width: 100%;
            max-width: 500px;
            text-align: center;
            position: relative;
            overflow: hidden;
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
        .main-container::after {
            content: '';
            position: absolute;
            top: 0;
            right: 0;
            width: 80px;
            height: 80px;
            background: linear-gradient(135deg, transparent 50%, rgba(102, 126, 234, 0.08) 50%);
            border-radius: 0 20px 0 80px;
            pointer-events: none;
        }
        .page-title {
            color: #2c3e50;
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 8px;
        }
        .quiz-desc {
            color: #555;
            margin-bottom: 18px;
        }
        .primary-btn {
            width: 100%;
            padding: 16px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 12px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            margin-bottom: 18px;
        }
        .primary-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
        }
        .primary-btn:active { transform: translateY(0); }
        .quiz-links {
            margin-bottom: 18px;
        }
        .quiz-links a {
            margin: 0 8px;
            color: #667eea;
            text-decoration: none;
            font-weight: 600;
            transition: color 0.3s;
        }
        .quiz-links a:hover {
            color: #764ba2;
            text-decoration: underline;
        }
        .form-group {
            margin-bottom: 18px;
            text-align: left;
        }
        label {
            font-weight: 600;
            color: #34495e;
            font-size: 15px;
        }
        @media (max-width: 600px) {
            .main-container {
                margin: 20px;
                padding: 35px 15px;
            }
            .nav-home {
                position: relative;
                top: auto;
                left: auto;
                margin-bottom: 20px;
                display: inline-block;
            }
        }
    </style>
</head>
<body>
<a href="${pageContext.request.contextPath}/" class="nav-home">&larr; Home</a>
<div class="main-container">
    <div class="page-title">${quiz.title}</div>
    <div class="quiz-desc">${quiz.description}</div>
    <div class="quiz-links">
        <a href="${pageContext.request.contextPath}/quiz-summery?quizId=${quiz.quizId}">View Quiz Rankings & Summary</a>
        <c:if test="${not empty sessionScope.user}">
            | <a href="${pageContext.request.contextPath}/quizHistory?quizId=${quiz.quizId}">History</a>
        </c:if>
    </div>
    <form action="${pageContext.request.contextPath}/takeQuiz" method="get">
        <input type="hidden" name="id" value="${quiz.quizId}" />
        <div class="form-group">
            <label><input type="checkbox" name="practiceMode" value="true"> Practice Mode</label>
        </div>
        <button type="submit" class="primary-btn">Start Quiz</button>
    </form>
    <a href="${pageContext.request.contextPath}/" class="primary-btn" style="width:auto; background:rgba(102,126,234,0.12); color:#667eea; border:1px solid #667eea; margin-top:10px;">Back to Home</a>
</div>
</body>
</html>
