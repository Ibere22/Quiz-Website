<%@ page import="model.Quiz" %>
<%@ page import="model.QuizAttempt" %>
<%@ page import="model.Announcement" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    User user = (User) session.getAttribute("user");
    List<Quiz> quizzes = (List<Quiz>) request.getAttribute("quizzes");
    List<Quiz> popularQuizzes = (List<Quiz>) request.getAttribute("popularQuizzes");
    List<Quiz> recentQuizzes = (List<Quiz>) request.getAttribute("recentQuizzes");
    List<QuizAttempt> recentAttempts = (List<QuizAttempt>) request.getAttribute("recentAttempts");
    List<Quiz> userCreatedQuizzes = (List<Quiz>) request.getAttribute("userCreatedQuizzes");
    List<Announcement> activeAnnouncements = (List<Announcement>) request.getAttribute("activeAnnouncements");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Quiz Website - Home</title>
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
            line-height: 1.6;
            color: #2c3e50;
        }
        
        /* =========================== NAVIGATION BAR =========================== */
        .navbar {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(20px);
            border-bottom: 1px solid rgba(255, 255, 255, 0.2);
            z-index: 1000;
            transition: all 0.3s ease;
        }
        
        .nav-container {
            max-width: 1200px;
            margin: 0 auto;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 15px 20px;
        }
        
        .nav-logo {
            font-size: 1.5rem;
            font-weight: 700;
            background: linear-gradient(135deg, #667eea, #764ba2);
            -webkit-background-clip: text;
            background-clip: text;
            -webkit-text-fill-color: transparent;
            text-decoration: none;
        }
        
        .nav-links {
            display: flex;
            gap: 30px;
            align-items: center;
        }
        
        .nav-link {
            color: #2c3e50;
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s ease;
            position: relative;
        }
        
        .nav-link:hover {
            color: #667eea;
        }
        
        .nav-link::after {
            content: '';
            position: absolute;
            bottom: -5px;
            left: 0;
            width: 0;
            height: 2px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            transition: width 0.3s ease;
        }
        
        .nav-link:hover::after {
            width: 100%;
        }
        
        .nav-user {
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        .nav-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea, #764ba2);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: 600;
            font-size: 1.2rem;
        }
        
        .nav-username {
            color: #2c3e50;
            font-weight: 600;
        }
        
        .nav-auth-buttons {
            display: flex;
            gap: 15px;
        }
        
        .nav-btn {
            padding: 8px 20px;
            border-radius: 20px;
            text-decoration: none;
            font-weight: 600;
            font-size: 0.9rem;
            transition: all 0.3s ease;
        }
        
        .nav-btn-primary {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
        }
        
        .nav-btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.3);
            color: white;
            text-decoration: none;
        }
        
        .nav-btn-secondary {
            background: transparent;
            color: #667eea;
            border: 2px solid #667eea;
        }
        
        .nav-btn-secondary:hover {
            background: #667eea;
            color: white;
            text-decoration: none;
        }
        
        /* =========================== HERO SECTION =========================== */
        .hero-section {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 120px 40px 80px;
            text-align: center;
            position: relative;
            overflow: hidden;
            margin-top: 70px;
        }
        
        .hero-section::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(10px);
        }
        
        .hero-section::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: 
                radial-gradient(circle at 10% 20%, rgba(255,255,255,0.1) 0%, transparent 50%),
                radial-gradient(circle at 90% 10%, rgba(255,255,255,0.08) 0%, transparent 50%),
                radial-gradient(circle at 20% 80%, rgba(255,255,255,0.06) 0%, transparent 50%),
                radial-gradient(circle at 80% 90%, rgba(255,255,255,0.08) 0%, transparent 50%);
            pointer-events: none;
        }
        
        .hero-content {
            position: relative;
            z-index: 2;
            max-width: 1200px;
            margin: 0 auto;
        }
        
        .hero-title {
            font-size: 3.5rem;
            font-weight: 700;
            margin-bottom: 25px;
            text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
            animation: slideInUp 0.8s ease;
        }
        
        @keyframes slideInUp {
            from { opacity: 0; transform: translateY(30px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        .hero-subtitle {
            font-size: 1.4rem;
            margin-bottom: 50px;
            opacity: 0.9;
            font-weight: 400;
            animation: slideInUp 0.8s ease 0.2s both;
        }
        
        .hero-cta {
            margin-bottom: 50px;
            animation: slideInUp 0.8s ease 0.4s both;
        }
        
        .hero-btn {
            display: inline-block;
            padding: 16px 32px;
            background: rgba(255, 255, 255, 0.15);
            border: 2px solid rgba(255, 255, 255, 0.3);
            color: white;
            text-decoration: none;
            border-radius: 30px;
            font-weight: 600;
            font-size: 1.1rem;
            backdrop-filter: blur(10px);
            transition: all 0.3s ease;
            margin: 0 10px;
        }
        
        .hero-btn:hover {
            background: rgba(255, 255, 255, 0.25);
            transform: translateY(-2px);
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
            text-decoration: none;
            color: white;
        }
        
        .hero-btn-primary {
            background: rgba(255, 255, 255, 0.9);
            color: #667eea;
            border-color: transparent;
        }
        
        .hero-btn-primary:hover {
            background: white;
            color: #764ba2;
        }
        
        .hero-stats {
            display: flex;
            justify-content: center;
            gap: 80px;
            margin-top: 50px;
            animation: slideInUp 0.8s ease 0.6s both;
        }
        
        .stat-item {
            text-align: center;
        }
        
        .stat-number {
            font-size: 2.8rem;
            font-weight: 700;
            display: block;
            margin-bottom: 8px;
        }
        
        .stat-label {
            font-size: 1rem;
            opacity: 0.8;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        
        /* =========================== MAIN CONTAINER =========================== */
        .main-container {
            max-width: 1600px;
            margin: -60px auto 0;
            padding: 0 40px 80px;
            position: relative;
            z-index: 3;
        }
        
        /* =========================== QUICK ACTIONS =========================== */
        .quick-actions {
            display: flex;
            justify-content: center;
            gap: 20px;
            margin-bottom: 50px;
            flex-wrap: wrap;
        }
        
        .quick-action-btn {
            padding: 12px 24px;
            background: white;
            border: none;
            border-radius: 25px;
            color: #667eea;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .quick-action-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
            text-decoration: none;
            color: #764ba2;
        }
        
        /* =========================== ANNOUNCEMENT SECTION =========================== */
        .announcements-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 25px 80px rgba(0, 0, 0, 0.15);
            margin-bottom: 50px;
            overflow: hidden;
            border-top: 4px solid #ff6b6b;
            position: relative;
            animation: fadeInUp 0.6s ease;
        }
        
        @keyframes fadeInUp {
            from { opacity: 0; transform: translateY(30px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        .announcements-card::before {
            content: '';
            position: absolute;
            top: 0;
            right: 0;
            width: 100px;
            height: 100px;
            background: linear-gradient(135deg, transparent 50%, rgba(255, 107, 107, 0.1) 50%);
            border-radius: 0 20px 0 100px;
            pointer-events: none;
        }
        
        .announcements-header {
            background: linear-gradient(135deg, #ff6b6b, #ee5a24);
            color: white;
            padding: 30px 40px;
            display: flex;
            align-items: center;
            gap: 20px;
        }
        
        .announcements-icon {
            font-size: 2rem;
        }
        
        .announcements-title {
            font-size: 1.6rem;
            font-weight: 600;
        }
        
        .announcements-content {
            padding: 40px;
        }
        
        .announcement-item {
            padding: 25px;
            border-radius: 12px;
            margin-bottom: 20px;
            border-left: 4px solid;
            transition: all 0.3s ease;
        }
        
        .announcement-item:hover {
            transform: translateX(5px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
        }
        
        .announcement-item.high {
            border-left-color: #dc3545;
            background: linear-gradient(135deg, #fff5f5, #ffeaea);
        }
        
        .announcement-item.medium {
            border-left-color: #ffc107;
            background: linear-gradient(135deg, #fffbf0, #fff8e1);
        }
        
        .announcement-item.low {
            border-left-color: #28a745;
            background: linear-gradient(135deg, #f0fff4, #e8f5e8);
        }
        
        .announcement-item-title {
            font-size: 1.2rem;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 10px;
        }
        
        .announcement-item-content {
            color: #495057;
            margin-bottom: 15px;
            line-height: 1.6;
        }
        
        .announcement-item-meta {
            display: flex;
            gap: 20px;
            font-size: 0.9rem;
            color: #6c757d;
        }
        
        /* =========================== CONTENT GRID =========================== */
        .content-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
            gap: 40px;
            margin-bottom: 50px;
        }
        
        .content-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 15px 50px rgba(0, 0, 0, 0.1);
            overflow: hidden;
            transition: all 0.3s ease;
            animation: fadeInUp 0.6s ease;
        }
        
        .content-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 25px 80px rgba(0, 0, 0, 0.15);
        }
        
        .content-card-header {
            background: linear-gradient(135deg, #f8f9fa, #e9ecef);
            padding: 30px 35px;
            display: flex;
            align-items: center;
            gap: 15px;
            border-bottom: 2px solid #e8ecf0;
        }
        
        .content-card-icon {
            font-size: 2rem;
            opacity: 0.8;
        }
        
        .content-card-title {
            font-size: 1.5rem;
            font-weight: 600;
            color: #2c3e50;
        }
        
        .content-card-body {
            padding: 35px;
        }
        
        /* =========================== ENHANCED QUIZ LISTS =========================== */
        .quiz-list {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        
        .quiz-item {
            margin-bottom: 15px;
        }
        
        .quiz-link {
            display: block;
            padding: 18px 20px;
            background: #f8f9fa;
            border: 2px solid #e8ecf0;
            border-radius: 12px;
            color: #2c3e50;
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }
        
        .quiz-link::before {
            content: '';
            position: absolute;
            left: 0;
            top: 0;
            bottom: 0;
            width: 4px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            transform: scaleY(0);
            transition: transform 0.3s ease;
        }
        
        .quiz-link:hover {
            background: #f0f4ff;
            border-color: #667eea;
            transform: translateX(5px);
            color: #2c3e50;
            text-decoration: none;
        }
        
        .quiz-link:hover::before {
            transform: scaleY(1);
        }
        
        .quiz-title {
            font-size: 1.1rem;
            font-weight: 600;
            margin-bottom: 5px;
        }
        
        .quiz-meta {
            font-size: 0.85rem;
            color: #6c757d;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        /* =========================== ENHANCED EMPTY STATES =========================== */
        .empty-state {
            text-align: center;
            padding: 50px 20px;
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
        
        .empty-action {
            display: inline-block;
            padding: 12px 24px;
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            text-decoration: none;
            border-radius: 25px;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        
        .empty-action:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
            text-decoration: none;
            color: white;
        }
        
        /* =========================== USER SECTION =========================== */
        .user-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 15px 50px rgba(0, 0, 0, 0.1);
            margin-bottom: 40px;
            overflow: hidden;
            animation: fadeInUp 0.6s ease;
        }
        
        .user-card-header {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            padding: 30px 35px;
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        .user-card-body {
            padding: 35px;
        }
        
        .user-actions {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 35px;
        }
        
        .user-action-btn {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 18px 20px;
            background: #f8f9fa;
            border: 2px solid #e8ecf0;
            border-radius: 12px;
            color: #2c3e50;
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s ease;
            position: relative;
        }
        
        .user-action-btn:hover {
            background: #f0f4ff;
            border-color: #667eea;
            transform: translateY(-2px);
            color: #2c3e50;
            text-decoration: none;
        }
        
        .user-action-icon {
            font-size: 1.3rem;
        }
        
        .message-badge {
            position: absolute;
            top: -5px;
            right: -5px;
            background: #ff6b6b;
            color: white;
            border-radius: 50%;
            width: 24px;
            height: 24px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 0.8rem;
            font-weight: 600;
        }
        
        .user-section {
            margin-bottom: 30px;
        }
        
        .user-section-title {
            font-size: 1.3rem;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .logout-btn {
            background: linear-gradient(135deg, #ff6b6b, #ee5a24);
            color: white;
            border: none;
            padding: 14px 28px;
            border-radius: 25px;
            font-weight: 600;
            font-size: 1rem;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .logout-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(255, 107, 107, 0.3);
        }
        
        /* =========================== AUTH SECTION =========================== */
        .auth-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 15px 50px rgba(0, 0, 0, 0.1);
            padding: 50px;
            text-align: center;
            animation: fadeInUp 0.6s ease;
        }
        
        .auth-title {
            font-size: 2.2rem;
            font-weight: 700;
            color: #2c3e50;
            margin-bottom: 15px;
        }
        
        .auth-subtitle {
            font-size: 1.1rem;
            color: #6c757d;
            margin-bottom: 35px;
            line-height: 1.6;
        }
        
        .auth-buttons {
            display: flex;
            justify-content: center;
            gap: 20px;
            flex-wrap: wrap;
        }
        
        .auth-btn {
            display: inline-flex;
            align-items: center;
            gap: 10px;
            padding: 16px 32px;
            border-radius: 25px;
            text-decoration: none;
            font-weight: 600;
            font-size: 1.1rem;
            transition: all 0.3s ease;
        }
        
        .auth-btn-primary {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
        }
        
        .auth-btn-primary:hover {
            transform: translateY(-3px);
            box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
            text-decoration: none;
            color: white;
        }
        
        .auth-btn-secondary {
            background: transparent;
            color: #667eea;
            border: 2px solid #667eea;
        }
        
        .auth-btn-secondary:hover {
            background: #667eea;
            color: white;
            text-decoration: none;
        }
        
        /* =========================== RESPONSIVE DESIGN =========================== */
        @media (max-width: 768px) {
            .navbar {
                padding: 10px 0;
            }
            
            .nav-container {
                padding: 10px 20px;
                flex-direction: column;
                gap: 15px;
            }
            
            .nav-links {
                gap: 20px;
            }
            
            .hero-section {
                padding: 100px 20px 60px;
                margin-top: 100px;
            }
            
            .hero-title {
                font-size: 2.5rem;
            }
            
            .hero-subtitle {
                font-size: 1.1rem;
            }
            
            .hero-stats {
                flex-direction: column;
                gap: 30px;
            }
            
            .hero-btn {
                display: block;
                margin: 10px 0;
            }
            
            .content-grid {
                grid-template-columns: 1fr;
                gap: 25px;
            }
            
            .main-container {
                padding: 0 20px 40px;
            }
            
            .quick-actions {
                flex-direction: column;
                align-items: center;
            }
            
            .auth-buttons {
                flex-direction: column;
                align-items: center;
            }
            
            .user-actions {
                grid-template-columns: 1fr;
            }
        }
        
        @media (max-width: 480px) {
            .hero-section {
                padding: 80px 15px 50px;
            }
            
            .hero-title {
                font-size: 2rem;
            }
            
            .content-card-header,
            .announcements-header,
            .user-card-header {
                padding: 20px;
            }
            
            .content-card-body,
            .announcements-content,
            .user-card-body {
                padding: 20px;
            }
            
            .auth-card {
                padding: 30px 20px;
            }
        }
    </style>
</head>
<body>
    <!-- Navigation Bar -->
    <nav class="navbar">
        <div class="nav-container">
            <a href="${pageContext.request.contextPath}/" class="nav-logo">🎯 QuizWebsite</a>
            
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/quizzes" class="nav-link">Browse Quizzes</a>
                <% if (user != null) { %>
                    <a href="${pageContext.request.contextPath}/quiz/create" class="nav-link">Create Quiz</a>
                    <a href="${pageContext.request.contextPath}/profile" class="nav-link">Profile</a>
                <% } %>
            </div>
            
            <div class="nav-user">
                <% if (user != null) { %>
                    <div class="nav-avatar"><%= user.getUsername().charAt(0) %></div>
                    <span class="nav-username"><%= user.getUsername() %></span>
                <% } else { %>
                    <div class="nav-auth-buttons">
                        <a href="login" class="nav-btn nav-btn-secondary">Login</a>
                        <a href="register" class="nav-btn nav-btn-primary">Sign Up</a>
                    </div>
                <% } %>
            </div>
        </div>
    </nav>

    <!-- Hero Section -->
    <div class="hero-section">
        <div class="hero-content">
            <h1 class="hero-title">🎓 Master Any Subject</h1>
            <p class="hero-subtitle">Challenge yourself, learn something new, and compete with friends in our interactive quiz platform!</p>
            
            <div class="hero-cta">
                <% if (user == null) { %>
                    <a href="register" class="hero-btn hero-btn-primary">🚀 Get Started Free</a>
                <% } else { %>
                    <a href="quiz/create" class="hero-btn hero-btn-primary">➕ Create New Quiz</a>
                <% } %>
            </div>
            
            <div class="hero-stats">
                <div class="stat-item">
                    <span class="stat-number"><%= quizzes != null ? quizzes.size() : 0 %></span>
                    <span class="stat-label">Active Quizzes</span>
                </div>
                <% if (user == null) { %>
                <div class="stat-item">
                    <span class="stat-number">∞</span>
                    <span class="stat-label">Learning Opportunities</span>
                </div>
                <% } else { %>
                <div class="stat-item">
                    <span class="stat-number"><%= userCreatedQuizzes != null ? userCreatedQuizzes.size() : 0 %></span>
                    <span class="stat-label">Your Quizzes</span>
                </div>
                <% } %>
                <div class="stat-item">
                    <span class="stat-number"><%= activeAnnouncements != null ? activeAnnouncements.size() : 0 %></span>
                    <span class="stat-label">Announcements</span>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Main Container -->
    <div class="main-container">
        <!-- Quick Actions -->
        <div class="quick-actions">
            <% if (user != null) { %>
                <a href="profile" class="quick-action-btn">
                    <span>👤</span> My Profile
                </a>
            <% } %>
        </div>

        <!-- Announcements Section -->
        <% if (activeAnnouncements != null && !activeAnnouncements.isEmpty()) { %>
        <div class="announcements-card">
            <div class="announcements-header">
                <div class="announcements-icon">📢</div>
                <div class="announcements-title">Latest Announcements</div>
            </div>
            <div class="announcements-content">
                <% for (Announcement announcement : activeAnnouncements) { %>
                <div class="announcement-item <%= announcement.getPriority().toString().toLowerCase() %>">
                    <div class="announcement-item-title"><%= announcement.getTitle() %></div>
                    <div class="announcement-item-content"><%= announcement.getContent() %></div>
                    <div class="announcement-item-meta">
                        <span>Priority: <%= announcement.getPriority().toString() %></span>
                        <span>Date: <%= announcement.getCreatedDate() %></span>
                    </div>
                </div>
                <% } %>
            </div>
        </div>
        <% } %>
        
        <!-- Content Grid -->
        <div class="content-grid">
            <!-- Popular Quizzes -->
            <div class="content-card">
                <div class="content-card-header">
                    <div class="content-card-icon">🔥</div>
                    <div class="content-card-title">Trending Quizzes</div>
                </div>
                <div class="content-card-body">
                    <% if (popularQuizzes != null && !popularQuizzes.isEmpty()) { %>
                    <ul class="quiz-list">
                        <% for (Quiz quiz : popularQuizzes) { %>
                        <li class="quiz-item">
                            <a href="quiz?id=<%= quiz.getQuizId() %>" class="quiz-link">
                                <div class="quiz-title"><%= quiz.getTitle() %></div>
                            </a>
                        </li>
                        <% } %>
                    </ul>
                    <% } else { %>
                    <div class="empty-state">
                        <div class="empty-icon">🔥</div>
                        <div class="empty-title">No trending quizzes yet</div>
                        <div class="empty-text">Be the first to create a popular quiz!</div>
                        <% if (user != null) { %>
                        <a href="quiz/create" class="empty-action">Create First Quiz</a>
                        <% } %>
                    </div>
                    <% } %>
                </div>
            </div>
            
            <!-- Recent Quizzes -->
            <div class="content-card">
                <div class="content-card-header">
                    <div class="content-card-icon">✨</div>
                    <div class="content-card-title">Fresh Content</div>
                </div>
                <div class="content-card-body">
                    <% if (recentQuizzes != null && !recentQuizzes.isEmpty()) { %>
                    <ul class="quiz-list">
                        <% for (Quiz quiz : recentQuizzes) { %>
                        <li class="quiz-item">
                            <a href="quiz?id=<%= quiz.getQuizId() %>" class="quiz-link">
                                <div class="quiz-title"><%= quiz.getTitle() %></div>
                            </a>
                        </li>
                        <% } %>
                    </ul>
                    <% } else { %>
                    <div class="empty-state">
                        <div class="empty-icon">✨</div>
                        <div class="empty-title">No fresh content yet</div>
                        <div class="empty-text">Create the first quiz to get started!</div>
                        <% if (user != null) { %>
                        <a href="quiz/create" class="empty-action">Create Quiz Now</a>
                        <% } %>
                    </div>
                    <% } %>
                </div>
            </div>
        </div>
        
        <!-- User Section (Logged In) -->
        <% if (user != null) { %>
        <div class="user-card">
            <div class="user-card-header">
                <div class="content-card-icon">👋</div>
                <div class="content-card-title">Welcome back, <%= user.getUsername() %>!</div>
            </div>
            <div class="user-card-body">
                <div class="user-actions">
                    <a href="${pageContext.request.contextPath}/profile" class="user-action-btn">
                        <span class="user-action-icon">👤</span>
                        <span>My Profile</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/quiz/create" class="user-action-btn">
                        <span class="user-action-icon">➕</span>
                        <span>Create Quiz</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/friends" class="user-action-btn">
                        <span class="user-action-icon">👥</span>
                        <span>Manage Friends</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/messages" class="user-action-btn">
                        <span class="user-action-icon">💬</span>
                        <span>View Messages</span>
                        <% Integer unreadCount = (Integer) request.getAttribute("unreadMessageCount"); %>
                        <% String recentTypeEmoji = (String) request.getAttribute("recentUnreadTypeEmoji"); %>
                        <% if (unreadCount != null && unreadCount > 0) { %>
                        <span class="message-badge"><%= unreadCount %></span>
                        <% if (recentTypeEmoji != null) { %>
                        <span style="font-size: 1.1em; margin-left: 5px;"><%= recentTypeEmoji %></span>
                        <% } %>
                        <% } %>
                    </a>
                </div>
                
                <!-- User's Recent Attempts -->
                <% if (recentAttempts != null && !recentAttempts.isEmpty()) { %>
                <div class="user-section">
                    <h3 class="user-section-title">
                        <span>📊</span>
                        Your Recent Quiz Attempts
                    </h3>
                    <ul class="quiz-list">
                        <% java.util.Set<Integer> shownQuizIds = new java.util.HashSet<>();
                           java.util.Map<Integer, String> quizIdToTitle = new java.util.HashMap<>();
                           if (quizzes != null) {
                               for (Quiz quiz : quizzes) {
                                   quizIdToTitle.put(quiz.getQuizId(), quiz.getTitle());
                               }
                           }
                           for (QuizAttempt attempt : recentAttempts) {
                               int quizId = attempt.getQuizId();
                               if (!shownQuizIds.contains(quizId)) {
                                   shownQuizIds.add(quizId);
                                   String quizTitle = quizIdToTitle.getOrDefault(quizId, "Quiz #" + quizId);
                        %>
                        <li class="quiz-item">
                            <a href="quiz?id=<%= quizId %>" class="quiz-link">
                                <div class="quiz-title"><%= quizTitle %></div>
                            </a>
                        </li>
                        <%     }
                           }
                        %>
                    </ul>
                </div>
                <% } %>
                
                <!-- User's Created Quizzes -->
                <% if (userCreatedQuizzes != null && !userCreatedQuizzes.isEmpty()) { %>
                <div class="user-section">
                    <h3 class="user-section-title">
                        <span>🎨</span>
                        Your Created Quizzes
                    </h3>
                    <ul class="quiz-list">
                        <% for (Quiz quiz : userCreatedQuizzes) { %>
                        <li class="quiz-item">
                            <a href="quiz?id=<%= quiz.getQuizId() %>" class="quiz-link">
                                <div class="quiz-title"><%= quiz.getTitle() %></div>
                            </a>
                        </li>
                        <% } %>
                    </ul>
                </div>
                <% } %>
                
                <div style="margin-top: 30px; text-align: center;">
                    <form action="logout" method="get" style="display: inline;">
                        <button type="submit" class="logout-btn">🚪 Logout</button>
                    </form>
                </div>
            </div>
        </div>
        <% } else { %>
        
        <!-- Auth Section (Not Logged In) -->
        <div class="auth-card">
            <h2 class="auth-title">🚀 Ready to Get Started?</h2>
            <p class="auth-subtitle">Join thousands of learners testing their knowledge and creating amazing quizzes on our platform!</p>
            <div class="auth-buttons">
                <a href="register" class="auth-btn auth-btn-primary">✨ Sign Up Free</a>
                <a href="login" class="auth-btn auth-btn-secondary">🔐 Login</a>
            </div>
        </div>
        <% } %>
    </div>
</body>
</html>