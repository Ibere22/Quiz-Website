<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.QuizAttempt, java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Quiz History</title>
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
            font-size: 2.2rem;
            font-weight: 700;
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        .back-btn {
            background: rgba(255, 255, 255, 0.15);
            border: 2px solid rgba(255, 255, 255, 0.2);
            color: white;
            padding: 12px 24px;
            border-radius: 12px;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s ease;
            backdrop-filter: blur(10px);
        }
        
        .back-btn:hover {
            background: rgba(255, 255, 255, 0.25);
            transform: translateY(-2px);
            text-decoration: none;
            color: white;
        }
        
        /* =========================== CONTENT SECTION =========================== */
        .content-area {
            padding: 40px;
        }
        
        /* =========================== CONTROLS SECTION =========================== */
        .controls-section {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 20px;
            margin-bottom: 30px;
            padding: 25px;
            background: #f8f9fa;
            border-radius: 16px;
            border-left: 4px solid #667eea;
        }
        
        .filter-form {
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        .filter-label {
            font-weight: 600;
            color: #2c3e50;
        }
        
        .filter-select {
            padding: 10px 16px;
            border: 2px solid #e8ecf0;
            border-radius: 8px;
            background: white;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .filter-select:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        
        .avg-score-display {
            display: flex;
            align-items: center;
            gap: 10px;
            font-weight: 600;
            color: #2c3e50;
        }
        
        .avg-score-value {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 14px;
        }
        
        /* =========================== TABLE STYLES =========================== */
        .table-container {
            background: white;
            border-radius: 16px;
            overflow: hidden;
            box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
            border: 1px solid #e8ecf0;
        }
        
        .history-table {
            width: 100%;
            border-collapse: collapse;
        }
        
        .history-table th {
            background: linear-gradient(135deg, #f8f9fa, #e9ecef);
            color: #2c3e50;
            font-weight: 600;
            padding: 20px 16px;
            text-align: left;
            border-bottom: 2px solid #e8ecf0;
            font-size: 0.95rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .history-table td {
            padding: 18px 16px;
            border-bottom: 1px solid #f1f3f5;
            font-weight: 500;
            color: #495057;
        }
        
        .history-table tr:hover {
            background: #f8f9fa;
            transform: scale(1.002);
            transition: all 0.2s ease;
        }
        
        .history-table tr:last-child td {
            border-bottom: none;
        }
        
        /* =========================== TABLE CELL STYLES =========================== */
        .date-cell {
            font-family: 'Courier New', monospace;
            font-size: 0.9rem;
            color: #6c757d;
        }
        
        .score-cell {
            font-weight: 700;
            font-size: 1.1rem;
        }
        
        .score-excellent {
            color: #00b894;
        }
        
        .score-good {
            color: #0078d4;
        }
        
        .score-average {
            color: #ff9500;
        }
        
        .score-poor {
            color: #ee5a24;
        }
        
        .questions-cell {
            font-weight: 600;
            color: #2c3e50;
        }
        
        .time-cell {
            font-family: 'Courier New', monospace;
            color: #6c757d;
        }
        
        .practice-badge {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.85rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .practice-yes {
            background: linear-gradient(135deg, #00b894, #00a085);
            color: white;
        }
        
        .practice-no {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
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
            color: #6c757d;
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
            
            .header-content {
                flex-direction: column;
                align-items: flex-start;
            }
            
            .controls-section {
                flex-direction: column;
                align-items: flex-start;
            }
            
            .history-table {
                font-size: 0.9rem;
            }
            
            .history-table th,
            .history-table td {
                padding: 12px 8px;
            }
            
            /* Stack table cells on mobile */
            .history-table thead {
                display: none;
            }
            
            .history-table,
            .history-table tbody,
            .history-table tr,
            .history-table td {
                display: block;
            }
            
            .history-table tr {
                border: 1px solid #e8ecf0;
                border-radius: 12px;
                margin-bottom: 15px;
                padding: 15px;
                background: white;
            }
            
            .history-table td {
                border: none;
                padding: 8px 0;
                position: relative;
                padding-left: 50%;
            }
            
            .history-table td:before {
                content: attr(data-label);
                position: absolute;
                left: 0;
                width: 45%;
                font-weight: 600;
                color: #2c3e50;
                text-transform: uppercase;
                font-size: 0.8rem;
                letter-spacing: 0.5px;
            }
        }
    </style>
</head>
<body>
    <div class="main-container">
        <div class="page-header">
            <div class="header-content">
                <h1 class="page-title">
                    📊 Quiz History
                </h1>
                <a href="quiz?id=${quizId}" class="back-btn">← Back to Quiz</a>
            </div>
        </div>
        
        <div class="content-area">
            <div class="controls-section">
                <form class="filter-form" method="get" action="quizHistory">
                    <input type="hidden" name="quizId" value="${quizId}" />
                    <label class="filter-label">Show:</label>
                    <select name="mode" class="filter-select" onchange="this.form.submit()">
                        <option value="all" ${mode == 'all' ? 'selected' : ''}>All Attempts</option>
                        <option value="practice" ${mode == 'practice' ? 'selected' : ''}>Practice Mode Only</option>
                        <option value="nonpractice" ${mode == 'nonpractice' ? 'selected' : ''}>Non-Practice Only</option>
                    </select>
                </form>
                
                <c:if test="${averageScore >= 0}">
                    <div class="avg-score-display">
                        <span>Average Score:</span>
                        <span class="avg-score-value">
                            <fmt:formatNumber value="${averageScore}" maxFractionDigits="2" />%
                        </span>
                    </div>
                </c:if>
            </div>
            
            <div class="table-container">
                <c:choose>
                    <c:when test="${not empty attempts}">
                        <table class="history-table">
                            <thead>
                                <tr>
                                    <th>📅 Date</th>
                                    <th>🎯 Score</th>
                                    <th>❓ Questions</th>
                                    <th>⏱️ Time</th>
                                    <th>🎮 Mode</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="attempt" items="${attempts}">
                                    <tr>
                                        <td class="date-cell" data-label="Date">${attempt.dateTaken}</td>
                                        <td class="score-cell 
                                            <c:choose>
                                                <c:when test="${attempt.score >= 90}">score-excellent</c:when>
                                                <c:when test="${attempt.score >= 75}">score-good</c:when>
                                                <c:when test="${attempt.score >= 60}">score-average</c:when>
                                                <c:otherwise>score-poor</c:otherwise>
                                            </c:choose>" 
                                            data-label="Score">${attempt.score}%</td>
                                        <td class="questions-cell" data-label="Questions">${attempt.totalQuestions}</td>
                                        <td class="time-cell" data-label="Time">${attempt.timeTaken}s</td>
                                        <td data-label="Mode">
                                            <span class="practice-badge ${attempt.practice ? 'practice-yes' : 'practice-no'}">
                                                <c:choose>
                                                    <c:when test="${attempt.practice}">Practice</c:when>
                                                    <c:otherwise>Non Practice</c:otherwise>
                                                </c:choose>
                                            </span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div class="empty-icon">📝</div>
                            <div class="empty-title">No attempts yet</div>
                            <div class="empty-text">Take the quiz to see your history here!</div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</body>
</html> 