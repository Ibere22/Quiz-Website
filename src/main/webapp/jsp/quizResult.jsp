<%--
  Created by IntelliJ IDEA.
  User: nika
  Date: 7/8/2025
  Time: 6:12 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Double score = (Double) request.getAttribute("score");
    Integer correct = (Integer) request.getAttribute("correct");
    Integer totalQuestions = (Integer) request.getAttribute("totalQuestions");
    Long timeTaken = (Long) request.getAttribute("timeTaken");
    Boolean practiceMode = (Boolean) request.getAttribute("practiceMode");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Quiz Result</title>
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
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            line-height: 1.6;
        }
        
        /* =========================== MAIN CONTAINER =========================== */
        .result-container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 25px 80px rgba(0, 0, 0, 0.15);
            width: 100%;
            max-width: 600px;
            text-align: center;
            position: relative;
            overflow: hidden;
        }
        
        .result-container::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #667eea, #764ba2);
        }
        
        /* =========================== HEADER SECTION =========================== */
        .result-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px;
            position: relative;
        }
        
        .result-header::after {
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
        
        .result-icon {
            font-size: 4rem;
            margin-bottom: 20px;
            position: relative;
            z-index: 2;
        }
        
        .result-title {
            font-size: 2.5rem;
            font-weight: 700;
            margin-bottom: 15px;
            position: relative;
            z-index: 2;
        }
        
        .practice-notice {
            background: rgba(255, 255, 255, 0.15);
            border: 2px solid rgba(255, 255, 255, 0.2);
            border-radius: 12px;
            padding: 15px 20px;
            margin-top: 20px;
            font-weight: 600;
            backdrop-filter: blur(10px);
            position: relative;
            z-index: 2;
        }
        
        /* =========================== CONTENT SECTION =========================== */
        .result-content {
            padding: 50px 40px;
        }
        
        /* =========================== SCORE DISPLAY =========================== */
        .score-showcase {
            margin-bottom: 40px;
        }
        
        .score-circle {
            width: 180px;
            height: 180px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea, #764ba2);
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 30px;
            position: relative;
            box-shadow: 0 15px 40px rgba(102, 126, 234, 0.3);
        }
        
        .score-circle::before {
            content: '';
            position: absolute;
            inset: 8px;
            border-radius: 50%;
            background: white;
        }
        
        .score-text {
            position: relative;
            z-index: 2;
            font-size: 2.8rem;
            font-weight: 700;
            background: linear-gradient(135deg, #667eea, #764ba2);
            -webkit-background-clip: text;
            background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        
        .score-label {
            color: #7f8c8d;
            font-size: 1.1rem;
            font-weight: 500;
        }
        
        /* =========================== STATS SECTION =========================== */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 25px;
            margin-bottom: 40px;
        }
        
        .stat-card {
            background: #f8f9fa;
            border-radius: 16px;
            padding: 25px;
            border-left: 4px solid;
            transition: all 0.3s ease;
        }
        
        .stat-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
        }
        
        .stat-card.correct {
            border-left-color: #00b894;
        }
        
        .stat-card.time {
            border-left-color: #667eea;
        }
        
        .stat-icon {
            font-size: 2rem;
            margin-bottom: 10px;
        }
        
        .stat-value {
            font-size: 1.8rem;
            font-weight: 700;
            color: #2c3e50;
            margin-bottom: 5px;
        }
        
        .stat-label {
            color: #7f8c8d;
            font-size: 0.95rem;
            font-weight: 500;
        }
        
        /* =========================== PERFORMANCE INDICATOR =========================== */
        .performance-indicator {
            margin-bottom: 40px;
        }
        
        .performance-bar {
            height: 12px;
            background: #e8ecf0;
            border-radius: 6px;
            overflow: hidden;
            margin: 15px 0;
        }
        
        .performance-fill {
            height: 100%;
            border-radius: 6px;
            transition: width 1s ease;
        }
        
        .performance-excellent {
            background: linear-gradient(90deg, #00b894, #00a085);
        }
        
        .performance-good {
            background: linear-gradient(90deg, #00a3ff, #0078d4);
        }
        
        .performance-average {
            background: linear-gradient(90deg, #ffc107, #ff9500);
        }
        
        .performance-poor {
            background: linear-gradient(90deg, #ff6b6b, #ee5a24);
        }
        
        .performance-text {
            font-weight: 600;
            margin-top: 10px;
        }
        
        /* =========================== ACTION BUTTON =========================== */
        .action-section {
            border-top: 2px solid #e8ecf0;
            padding-top: 30px;
        }
        
        .home-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 18px 40px;
            font-size: 16px;
            font-weight: 600;
            border-radius: 12px;
            text-decoration: none;
            display: inline-block;
            cursor: pointer;
            transition: all 0.3s ease;
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
        }
        
        .home-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
            text-decoration: none;
            color: white;
        }
        
        .home-btn:active {
            transform: translateY(0);
        }
        
        /* =========================== RESPONSIVE DESIGN =========================== */
        @media (max-width: 768px) {
            .result-container {
                margin: 0 10px;
                border-radius: 16px;
            }
            
            .result-header,
            .result-content {
                padding: 30px 25px;
            }
            
            .result-title {
                font-size: 2rem;
            }
            
            .result-icon {
                font-size: 3rem;
            }
            
            .score-circle {
                width: 150px;
                height: 150px;
            }
            
            .score-text {
                font-size: 2.2rem;
            }
            
            .stats-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <div class="result-container">
        <div class="result-header">
            <div class="result-icon">🎉</div>
            <h1 class="result-title">Quiz Complete!</h1>
            <% if (practiceMode != null && practiceMode) { %>
                <div class="practice-notice">
                    🎯 You took this quiz in Practice Mode. Your score will not appear on the leaderboard.
                </div>
            <% } %>
        </div>
        
        <div class="result-content">
            <!-- Score Showcase -->
            <div class="score-showcase">
                <div class="score-circle">
                    <div class="score-text"><%= String.format("%.0f", score) %>%</div>
                </div>
                <div class="score-label">Your Final Score</div>
            </div>
            
            <!-- Stats Grid -->
            <div class="stats-grid">
                <div class="stat-card correct">
                    <div class="stat-icon">✅</div>
                    <div class="stat-value"><%= correct %> / <%= totalQuestions %></div>
                    <div class="stat-label">Correct Answers</div>
                </div>
                
                <div class="stat-card time">
                    <div class="stat-icon">⏱️</div>
                    <div class="stat-value"><%= timeTaken %></div>
                    <div class="stat-label">Seconds</div>
                </div>
            </div>
            
            <!-- Performance Indicator -->
            <div class="performance-indicator">
                <% 
                    String performanceClass;
                    String performanceText;
                    if (score >= 90) {
                        performanceClass = "performance-excellent";
                        performanceText = "🌟 Excellent work!";
                    } else if (score >= 75) {
                        performanceClass = "performance-good";
                        performanceText = "🎯 Great job!";
                    } else if (score >= 60) {
                        performanceClass = "performance-average";
                        performanceText = "👍 Good effort!";
                    } else {
                        performanceClass = "performance-poor";
                        performanceText = "💪 Keep practicing!";
                    }
                %>
                <div class="performance-bar">
                    <div class="performance-fill <%= performanceClass %>" style="width: <%= score %>%;"></div>
                </div>
                <div class="performance-text"><%= performanceText %></div>
            </div>
            
            <!-- Action Section -->
            <div class="action-section">
                <a href="${pageContext.request.contextPath}/" class="home-btn">🏠 Back to Home</a>
            </div>
        </div>
    </div>
</body>
</html>
