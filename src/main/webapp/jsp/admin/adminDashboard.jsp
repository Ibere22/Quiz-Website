<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard - Quiz Website</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f8f9fa;
        }
        
        .admin-header {
            background-color: #dc3545;
            color: white;
            padding: 15px 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .admin-header h1 {
            margin: 0;
            font-size: 24px;
        }
        
        .admin-user-info {
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        .btn-logout {
            background-color: rgba(255,255,255,0.2);
            color: white;
            border: 1px solid rgba(255,255,255,0.3);
            padding: 8px 15px;
            border-radius: 4px;
            text-decoration: none;
            font-size: 14px;
            transition: background-color 0.3s;
        }
        
        .btn-logout:hover {
            background-color: rgba(255,255,255,0.3);
            color: white;
            text-decoration: none;
        }
        
        .dashboard-container {
            max-width: 1200px;
            margin: 20px auto;
            padding: 0 20px;
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .stat-card {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            text-align: center;
            border-top: 4px solid;
        }
        
        .stat-card.users { border-top-color: #007bff; }
        .stat-card.quizzes { border-top-color: #28a745; }
        .stat-card.attempts { border-top-color: #ffc107; }
        .stat-card.announcements { border-top-color: #dc3545; }
        
        .stat-number {
            font-size: 36px;
            font-weight: bold;
            color: #495057;
            margin: 10px 0;
        }
        
        .stat-label {
            font-size: 14px;
            color: #6c757d;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .stat-detail {
            font-size: 12px;
            color: #6c757d;
            margin-top: 5px;
        }
        
        .admin-actions {
            background-color: white;
            padding: 25px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        
        .admin-actions h3 {
            margin: 0 0 20px 0;
            color: #495057;
            border-bottom: 2px solid #e9ecef;
            padding-bottom: 10px;
        }
        
        .action-buttons {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
        }
        
        .action-btn {
            display: block;
            padding: 15px;
            background-color: #f8f9fa;
            border: 2px solid #dee2e6;
            border-radius: 8px;
            text-decoration: none;
            color: #495057;
            text-align: center;
            font-weight: bold;
            transition: all 0.3s;
        }
        
        .action-btn:hover {
            background-color: #e9ecef;
            border-color: #adb5bd;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            text-decoration: none;
            color: #495057;
        }
        
        .action-btn .icon {
            font-size: 24px;
            display: block;
            margin-bottom: 8px;
        }
        
        .recent-activity {
            background-color: white;
            padding: 25px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .recent-activity h3 {
            margin: 0 0 20px 0;
            color: #495057;
            border-bottom: 2px solid #e9ecef;
            padding-bottom: 10px;
        }
        
        .announcement-item {
            padding: 12px;
            border-left: 4px solid #dee2e6;
            margin-bottom: 10px;
            background-color: #f8f9fa;
            border-radius: 0 4px 4px 0;
        }
        
        .announcement-item.high { border-left-color: #dc3545; }
        .announcement-item.medium { border-left-color: #ffc107; }
        .announcement-item.low { border-left-color: #6c757d; }
        
        .announcement-title {
            font-weight: bold;
            margin-bottom: 5px;
        }
        
        .announcement-meta {
            font-size: 12px;
            color: #6c757d;
        }
        
        .no-data {
            text-align: center;
            color: #6c757d;
            font-style: italic;
            padding: 20px;
        }
        
        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            text-decoration: none;
            color: #007bff;
            font-weight: bold;
            padding: 8px 12px;
            border: 1px solid #007bff;
            border-radius: 4px;
            transition: all 0.3s;
        }
        
        .back-link:hover {
            background-color: #007bff;
            color: white;
            text-decoration: none;
        }
    </style>
</head>
<body>
    <div class="admin-header">
        <h1>🛡️ Admin Dashboard</h1>
        <div class="admin-user-info">
            <span>Welcome, <strong>${admin.username}</strong></span>
            <a href="${pageContext.request.contextPath}/admin/logout" class="btn-logout">Logout</a>
        </div>
    </div>
    
    <div class="dashboard-container">
        <a href="${pageContext.request.contextPath}/" class="back-link">← Back to Main Site</a>
        
        <!-- Site Statistics -->
        <div class="stats-grid">
            <div class="stat-card users">
                <div class="stat-number">${totalUsers}</div>
                <div class="stat-label">Total Users</div>
                <div class="stat-detail">${totalAdmins} administrators</div>
                <div class="stat-detail">${recentUsers} new this week</div>
            </div>
            
            <div class="stat-card quizzes">
                <div class="stat-number">${totalQuizzes}</div>
                <div class="stat-label">Total Quizzes</div>
                <div class="stat-detail">Created by users</div>
            </div>
            
            <div class="stat-card attempts">
                <div class="stat-number">${totalQuizAttempts}</div>
                <div class="stat-label">Quiz Attempts</div>
                <div class="stat-detail">${recentQuizAttempts} this week</div>
            </div>
            
            <div class="stat-card announcements">
                <div class="stat-number">${activeAnnouncements}</div>
                <div class="stat-label">Active Announcements</div>
                <div class="stat-detail">${totalAnnouncements} total created</div>
            </div>
        </div>
        
        <!-- Admin Actions Grid -->
        <div class="actions-grid">
            <a href="${pageContext.request.contextPath}/admin/announcements" class="action-btn">
                <i class="icon">📢</i>
                <h3>Manage Announcements</h3>
                <p>Create, edit, and manage site announcements</p>
            </a>
            
            <a href="${pageContext.request.contextPath}/admin/users" class="action-btn">
                <i class="icon">👤</i>
                <h3>Manage Users</h3>
                <p>View users, delete accounts, promote to admin</p>
            </a>
            
            <a href="${pageContext.request.contextPath}/admin/quizzes" class="action-btn">
                <i class="icon">📝</i>
                <h3>Manage Quizzes</h3>
                <p>View and delete quizzes from the system</p>
            </a>
            
            <a href="${pageContext.request.contextPath}/admin/cleanup" class="action-btn">
                <i class="icon">🧹</i>
                <h3>Data Cleanup</h3>
                <p>Clear quiz history and inactive content</p>
            </a>
        </div>
        
        <!-- Recent Activity -->
        <div class="recent-activity">
            <h3>Recent Announcements</h3>
            <c:choose>
                <c:when test="${not empty recentAnnouncements}">
                    <c:forEach var="announcement" items="${recentAnnouncements}">
                        <div class="announcement-item ${announcement.priority.value}">
                            <div class="announcement-title">${announcement.title}</div>
                            <div class="announcement-meta">
                                Priority: ${announcement.priority.value} | 
                                Created: <fmt:formatDate value="${announcement.createdDate}" pattern="MMM dd, yyyy HH:mm" /> |
                                Status: ${announcement.active ? 'Active' : 'Inactive'}
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="no-data">No announcements found</div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html> 