<%--
  Created by IntelliJ IDEA.
  User: nika
  Date: 7/8/2025
  Time: 9:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>Quiz Summary</title>
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
        }
        
        .page-title {
            font-size: 2.5rem;
            font-weight: 700;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 15px;
        }
        
        .page-subtitle {
            font-size: 1.1rem;
            opacity: 0.9;
            font-weight: 400;
        }
        
        /* =========================== CONTENT SECTION =========================== */
        .content-area {
            padding: 40px;
        }
        
        /* =========================== LEADERBOARD TABLE =========================== */
        .leaderboard-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 30px;
            background: white;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        }
        
        .leaderboard-table th {
            background: linear-gradient(135deg, #f8f9fa, #e9ecef);
            color: #495057;
            font-weight: 600;
            padding: 18px 15px;
            text-align: left;
            font-size: 0.9rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            border-bottom: 2px solid #dee2e6;
        }
        
        .leaderboard-table td {
            padding: 18px 15px;
            border-bottom: 1px solid #f1f3f5;
            font-weight: 500;
            color: #495057;
            font-size: 1rem;
            transition: all 0.3s ease;
        }
        
        .leaderboard-table tr:hover {
            background: linear-gradient(135deg, #f8f9ff, #f0f4ff);
            transform: scale(1.01);
        }
        
        .leaderboard-table tr:last-child td {
            border-bottom: none;
        }
        
        /* =========================== RANKING STYLES =========================== */
        .rank-cell {
            width: 80px;
            text-align: center;
            font-weight: 700;
            font-size: 1.2rem;
        }
        
        .rank-1 {
            color: #ffd700;
            text-shadow: 1px 1px 2px rgba(0,0,0,0.1);
        }
        
        .rank-2 {
            color: #c0c0c0;
            text-shadow: 1px 1px 2px rgba(0,0,0,0.1);
        }
        
        .rank-3 {
            color: #cd7f32;
            text-shadow: 1px 1px 2px rgba(0,0,0,0.1);
        }
        
        .rank-other {
            color: #6c757d;
            font-weight: 600;
        }
        
        /* =========================== USERNAME STYLES =========================== */
        .username-cell a {
            color: #667eea;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        
        .username-cell a:hover {
            color: #764ba2;
            text-decoration: underline;
        }
        
        /* =========================== SCORE STYLES =========================== */
        .score-cell {
            font-weight: 700;
            font-size: 1.1rem;
            text-align: center;
        }
        
        .score-excellent {
            color: #28a745;
            background: linear-gradient(135deg, #d4edda, #c3e6cb);
            border-radius: 20px;
            padding: 6px 12px;
        }
        
        .score-good {
            color: #007bff;
            background: linear-gradient(135deg, #d1ecf1, #bee5eb);
            border-radius: 20px;
            padding: 6px 12px;
        }
        
        .score-average {
            color: #fd7e14;
            background: linear-gradient(135deg, #ffe8d4, #fed7aa);
            border-radius: 20px;
            padding: 6px 12px;
        }
        
        .score-poor {
            color: #dc3545;
            background: linear-gradient(135deg, #f8d7da, #f1c2c7);
            border-radius: 20px;
            padding: 6px 12px;
        }
        
        /* =========================== TIME AND DATE CELLS =========================== */
        .time-cell {
            color: #6c757d;
            font-weight: 600;
            text-align: center;
        }
        
        .date-cell {
            color: #6c757d;
            font-size: 0.9rem;
        }
        
        /* =========================== EMPTY STATE =========================== */
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
        }
        
        .empty-icon {
            font-size: 4rem;
            margin-bottom: 20px;
            opacity: 0.5;
        }
        
        .empty-title {
            font-size: 1.3rem;
            font-weight: 600;
            margin-bottom: 10px;
            color: #495057;
        }
        
        .empty-text {
            font-size: 1rem;
            margin-bottom: 25px;
        }
        
        /* =========================== BACK BUTTON =========================== */
        .back-section {
            text-align: center;
            padding-top: 20px;
            border-top: 1px solid #e8ecf0;
        }
        
        .back-btn {
            display: inline-block;
            padding: 12px 24px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            text-decoration: none;
            border-radius: 25px;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        
        .back-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
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
            }
            
            .page-header {
                padding: 30px 20px;
            }
            
            .page-title {
                font-size: 2rem;
                flex-direction: column;
                gap: 10px;
            }
            
            .content-area {
                padding: 20px;
            }
            
            .leaderboard-table {
                font-size: 0.9rem;
            }
            
            .leaderboard-table th,
            .leaderboard-table td {
                padding: 12px 8px;
            }
            
            .rank-cell {
                width: 60px;
                font-size: 1rem;
            }
            
            .score-cell {
                font-size: 1rem;
            }
        }
        
        @media (max-width: 480px) {
            .page-title {
                font-size: 1.8rem;
            }
            
            .leaderboard-table th,
            .leaderboard-table td {
                padding: 10px 6px;
                font-size: 0.8rem;
            }
        }
    </style>
</head>
<body>
    <%
        Map<Integer, String> userIdToUsername = (Map<Integer, String>)request.getAttribute("userIdToUsername");
        String quizId = request.getParameter("quizId");
    %>

    <div class="main-container">
        <div class="page-header">
            <div class="header-content">
                <h1 class="page-title">
                    🏆 Quiz Leaderboard
                </h1>
                <p class="page-subtitle">Top performers for this quiz</p>
            </div>
        </div>
        
        <div class="content-area">
            <% java.util.List<model.QuizAttempt> allTimeTop = (java.util.List<model.QuizAttempt>)request.getAttribute("allTimeTop");
               if (allTimeTop != null && !allTimeTop.isEmpty()) { %>
            <table class="leaderboard-table">
                <thead>
                    <tr>
                        <th>Rank</th>
                        <th>Player</th>
                        <th>Score</th>
                        <th>Questions</th>
                        <th>Time</th>
                        <th>Date</th>
                    </tr>
                </thead>
                <tbody>
                    <% int rank = 1;
                       for (model.QuizAttempt a : allTimeTop) { 
                           String rankClass = rank == 1 ? "rank-1" : rank == 2 ? "rank-2" : rank == 3 ? "rank-3" : "rank-other";
                           String scoreClass;
                           if (a.getScore() >= 90) scoreClass = "score-excellent";
                           else if (a.getScore() >= 75) scoreClass = "score-good";
                           else if (a.getScore() >= 60) scoreClass = "score-average";
                           else scoreClass = "score-poor";
                    %>
                    <tr>
                        <td class="rank-cell <%= rankClass %>">
                            <%= rank == 1 ? "🥇" : rank == 2 ? "🥈" : rank == 3 ? "🥉" : "#" + rank %>
                        </td>
                        <td class="username-cell">
                            <a href="${pageContext.request.contextPath}/user?username=<%= userIdToUsername.get(a.getUserId()) %>&quizId=<%= quizId %>">
                                <%= userIdToUsername.get(a.getUserId()) %>
                            </a>
                        </td>
                        <td class="score-cell <%= scoreClass %>"><%= a.getScore() %>%</td>
                        <td><%= a.getTotalQuestions() %></td>
                        <td class="time-cell"><%= a.getTimeTaken() %>s</td>
                        <td class="date-cell"><%= a.getDateTaken() %></td>
                    </tr>
                    <% rank++; } %>
                </tbody>
            </table>
            <% } else { %>
            <div class="empty-state">
                <div class="empty-icon">🏆</div>
                <div class="empty-title">No attempts yet</div>
                <div class="empty-text">Be the first to take this quiz and claim the top spot!</div>
            </div>
            <% } %>
            
            <div class="back-section">
                <a href="quiz?id=<%= quizId %>" class="back-btn">← Back to Quiz</a>
            </div>
        </div>
    </div>
</body>
</html>
