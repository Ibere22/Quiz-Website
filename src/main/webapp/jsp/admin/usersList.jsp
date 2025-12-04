<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management - Admin Panel</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #c62828 0%, #b71c1c 100%);
            min-height: 100vh;
            color: #333;
        }
        
        .header {
            background: linear-gradient(135deg, #c62828 0%, #b71c1c 100%);
            color: white;
            padding: 20px 0;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
            position: sticky;
            top: 0;
            z-index: 100;
        }
        
        .header-content {
            max-width: 1400px;
            margin: 0 auto;
            padding: 0 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .header h1 {
            font-size: 2.2em;
            font-weight: 700;
            margin: 0;
        }
        
        .nav-buttons {
            display: flex;
            gap: 15px;
            align-items: center;
        }
        
        .nav-buttons span {
            font-size: 1.1em;
            font-weight: 500;
        }
        
        .nav-btn {
            background: rgba(255, 255, 255, 0.2);
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 8px;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s ease;
            cursor: pointer;
            font-size: 0.95em;
        }
        
        .nav-btn:hover {
            background: rgba(255, 255, 255, 0.3);
            transform: translateY(-2px);
        }
        
        .container {
            max-width: 1400px;
            margin: 0 auto;
            padding: 30px 20px;
        }
        
        .breadcrumb {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 10px;
            padding: 15px 20px;
            margin-bottom: 30px;
            color: #666;
            font-size: 0.95em;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        
        .breadcrumb a {
            color: #c62828;
            text-decoration: none;
            font-weight: 500;
        }
        
        .breadcrumb a:hover {
            color: #b71c1c;
        }
        
        .admin-actions {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 15px;
            padding: 20px;
            margin-bottom: 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 15px;
            box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
        }
        
        .search-container {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .search-box {
            padding: 12px 15px;
            border: 2px solid #e2e8f0;
            border-radius: 10px;
            width: 300px;
            font-size: 0.95em;
            transition: all 0.3s ease;
        }
        
        .search-box:focus {
            outline: none;
            border-color: #c62828;
            box-shadow: 0 0 0 3px rgba(198, 40, 40, 0.1);
        }
        
        .btn {
            padding: 12px 20px;
            border: none;
            border-radius: 10px;
            cursor: pointer;
            font-size: 0.95em;
            font-weight: 600;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-block;
            text-align: center;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #c62828 0%, #b71c1c 100%);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(198, 40, 40, 0.4);
        }
        
        .btn-search {
            background: linear-gradient(135deg, #c62828 0%, #b71c1c 100%);
            color: white;
        }
        
        .btn-search:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(198, 40, 40, 0.4);
        }
        
        .stats-summary {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 15px;
            padding: 20px;
            margin-bottom: 30px;
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
        }
        
        .stat-card {
            text-align: center;
            padding: 15px;
            border-radius: 10px;
            background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
        }
        
        .stat-number {
            font-size: 2.5em;
            font-weight: 700;
            color: #c62828;
            margin-bottom: 5px;
        }
        
        .stat-label {
            font-size: 0.9em;
            color: #718096;
            font-weight: 500;
        }
        
        .users-table-container {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 15px;
            overflow: hidden;
            box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
        }
        
        .table-header {
            background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
            padding: 20px;
            border-bottom: 1px solid #e2e8f0;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .table-title {
            font-size: 1.3em;
            font-weight: 700;
            color: #2d3748;
            margin: 0;
        }
        
        .users-table {
            width: 100%;
            border-collapse: collapse;
        }
        
        .users-table th {
            background: linear-gradient(135deg, #c62828 0%, #b71c1c 100%);
            color: white;
            padding: 15px 12px;
            text-align: left;
            font-weight: 600;
            font-size: 0.9em;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .users-table td {
            padding: 15px 12px;
            border-bottom: 1px solid #e2e8f0;
            vertical-align: middle;
        }
        
        .users-table tbody tr {
            transition: all 0.3s ease;
        }
        
        .users-table tbody tr:hover {
            background: rgba(198, 40, 40, 0.05);
            transform: translateY(-1px);
        }
        
        .user-info {
            font-weight: 600;
            color: #2d3748;
            margin-bottom: 5px;
        }
        
        .user-email {
            color: #718096;
            font-size: 0.9em;
            line-height: 1.4;
        }
        
        .user-meta {
            display: flex;
            flex-direction: column;
            gap: 3px;
        }
        
        .meta-item {
            font-size: 0.85em;
            color: #718096;
        }
        
        .user-id {
            color: #c62828;
            font-weight: 500;
        }
        
        .admin-badge {
            display: inline-block;
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 0.75em;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .admin-yes {
            background: rgba(72, 187, 120, 0.1);
            color: #38a169;
        }
        
        .admin-no {
            background: rgba(160, 174, 192, 0.1);
            color: #718096;
        }
        
        .btn-action {
            border: none;
            padding: 6px 12px;
            border-radius: 6px;
            cursor: pointer;
            font-size: 0.8em;
            font-weight: 600;
            transition: all 0.3s ease;
            margin: 2px;
        }
        
        .btn-delete {
            background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
            color: white;
        }
        
        .btn-delete:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(220, 38, 38, 0.4);
        }
        
        .btn-promote {
            background: linear-gradient(135deg, #059669 0%, #047857 100%);
            color: white;
        }
        
        .btn-promote:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(5, 150, 105, 0.4);
        }
        
        .btn-disabled {
            background: #e2e8f0;
            color: #a0aec0;
            cursor: not-allowed;
        }
        
        .alert {
            padding: 15px 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            font-weight: 500;
        }
        
        .alert-success {
            background: rgba(72, 187, 120, 0.1);
            color: #38a169;
            border: 1px solid rgba(72, 187, 120, 0.2);
        }
        
        .alert-error {
            background: rgba(220, 38, 38, 0.1);
            color: #dc2626;
            border: 1px solid rgba(220, 38, 38, 0.2);
        }
        
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #718096;
        }
        
        .empty-state h3 {
            margin-bottom: 10px;
            font-size: 1.2em;
        }
        
        .date-display {
            color: #718096;
            font-size: 0.85em;
        }
        
        .actions-column {
            width: 140px;
            text-align: center;
        }
        
        @media (max-width: 768px) {
            .header-content {
                flex-direction: column;
                gap: 15px;
                text-align: center;
            }
            
            .nav-buttons {
                flex-wrap: wrap;
                justify-content: center;
            }
            
            .container {
                padding: 15px;
            }
            
            .admin-actions {
                flex-direction: column;
                align-items: stretch;
            }
            
            .search-box {
                width: 100%;
            }
            
            .stats-summary {
                grid-template-columns: repeat(2, 1fr);
            }
            
            .users-table {
                font-size: 0.9em;
            }
            
            .user-email {
                max-width: 200px;
            }
        }
    </style>
</head>
<body>
    <!-- Header with Navigation -->
    <div class="header">
        <div class="header-content">
            <h1>👥 User Management</h1>
            <div class="nav-buttons">
                <span>Welcome, ${sessionScope.admin.username}!</span>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-btn">Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/logout" class="nav-btn">Logout</a>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- Breadcrumb -->
        <div class="breadcrumb">
            <a href="${pageContext.request.contextPath}/admin/dashboard">Admin Dashboard</a> / User Management
        </div>

        <!-- Success/Error Messages -->
        <c:if test="${not empty sessionScope.success}">
            <div class="alert alert-success">
                ${sessionScope.success}
            </div>
            <c:remove var="success" scope="session"/>
        </c:if>
        
        <c:if test="${not empty sessionScope.error}">
            <div class="alert alert-error">
                ${sessionScope.error}
            </div>
            <c:remove var="error" scope="session"/>
        </c:if>

        <!-- Admin Actions -->
        <div class="admin-actions">
            <div class="search-container">
                <input type="text" 
                       class="search-box" 
                       placeholder="Search users by username or email..." 
                       id="searchInput"
                       onkeyup="filterUsers()">
                <button class="btn btn-search" onclick="clearSearch()">Clear</button>
            </div>
        </div>

        <!-- Statistics Summary -->
        <div class="stats-summary">
            <div class="stat-card">
                <div class="stat-number">${fn:length(users)}</div>
                <div class="stat-label">Total Users</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">
                    <c:set var="adminCount" value="0"/>
                    <c:forEach items="${users}" var="user">
                        <c:if test="${user.admin}">
                            <c:set var="adminCount" value="${adminCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${adminCount}
                </div>
                <div class="stat-label">Admin Users</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">
                    <c:set var="regularCount" value="${fn:length(users) - adminCount}"/>
                    ${regularCount}
                </div>
                <div class="stat-label">Regular Users</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">
                    <c:set var="recentCount" value="0"/>
                    <jsp:useBean id="now" class="java.util.Date"/>
                    <c:set var="weekAgo" value="${now.time - (7 * 24 * 60 * 60 * 1000)}"/>
                    <c:forEach items="${users}" var="user">
                        <c:if test="${user.createdDate.time > weekAgo}">
                            <c:set var="recentCount" value="${recentCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${recentCount}
                </div>
                <div class="stat-label">New This Week</div>
            </div>
        </div>

        <!-- Users Table -->
        <div class="users-table-container">
            <div class="table-header">
                <h2 class="table-title">All Users</h2>
                <span class="stat-label">Showing ${fn:length(users)} users</span>
            </div>

            <c:choose>
                <c:when test="${empty users}">
                    <div class="empty-state">
                        <h3>No Users Found</h3>
                        <p>There are no users registered in the system yet.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="users-table" id="usersTable">
                        <thead>
                            <tr>
                                <th>User Information</th>
                                <th>User ID</th>
                                <th>Admin Status</th>
                                <th>Registration Date</th>
                                <th class="actions-column">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${users}" var="user">
                                <tr class="user-row">
                                    <td>
                                        <div class="user-info">${fn:escapeXml(user.username)}</div>
                                        <div class="user-email">${fn:escapeXml(user.email)}</div>
                                    </td>
                                    <td>
                                        <div class="user-meta">
                                            <span class="user-id">#${user.userId}</span>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="admin-badge ${user.admin ? 'admin-yes' : 'admin-no'}">
                                            ${user.admin ? 'Admin' : 'User'}
                                        </span>
                                    </td>
                                    <td>
                                        <div class="date-display">
                                            <fmt:formatDate value="${user.createdDate}" pattern="MMM dd, yyyy"/>
                                            <br>
                                            <fmt:formatDate value="${user.createdDate}" pattern="HH:mm"/>
                                        </div>
                                    </td>
                                    <td class="actions-column">
                                        <c:choose>
                                            <c:when test="${user.userId == sessionScope.admin.userId}">
                                                <!-- Current admin can't delete/modify themselves -->
                                                <button class="btn-action btn-disabled" disabled title="Cannot modify your own account">
                                                    Self
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <!-- Delete User Form -->
                                                <form method="post" action="${pageContext.request.contextPath}/admin/users/delete" 
                                                      style="display: inline;"
                                                      onsubmit="return confirm('Are you sure you want to delete user \'${fn:escapeXml(user.username)}\'? This action cannot be undone.');">
                                                    <input type="hidden" name="id" value="${user.userId}">
                                                    <button type="submit" class="btn-action btn-delete">Delete</button>
                                                </form>
                                                
                                                <!-- Promote/Demote User Form -->
                                                <c:choose>
                                                    <c:when test="${user.admin}">
                                                        <!-- Already admin - could add demote functionality later -->
                                                        <button class="btn-action btn-disabled" disabled title="Already an admin">
                                                            Admin
                                                        </button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <form method="post" action="${pageContext.request.contextPath}/admin/users/promote" 
                                                              style="display: inline;"
                                                              onsubmit="return confirm('Are you sure you want to promote \'${fn:escapeXml(user.username)}\' to admin?');">
                                                            <input type="hidden" name="id" value="${user.userId}">
                                                            <button type="submit" class="btn-action btn-promote">Promote</button>
                                                        </form>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <script>
        function filterUsers() {
            const searchInput = document.getElementById('searchInput');
            const filter = searchInput.value.toLowerCase();
            const table = document.getElementById('usersTable');
            const rows = table.getElementsByClassName('user-row');

            for (let i = 0; i < rows.length; i++) {
                const row = rows[i];
                const username = row.cells[0].querySelector('.user-info').textContent.toLowerCase();
                const email = row.cells[0].querySelector('.user-email').textContent.toLowerCase();
                
                if (username.includes(filter) || email.includes(filter)) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            }
        }

        function clearSearch() {
            document.getElementById('searchInput').value = '';
            filterUsers();
        }

        // Auto-hide alerts after 5 seconds
        document.addEventListener('DOMContentLoaded', function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(alert => {
                setTimeout(() => {
                    alert.style.opacity = '0';
                    alert.style.transform = 'translateY(-10px)';
                    setTimeout(() => alert.remove(), 300);
                }, 5000);
            });
        });
    </script>
</body>
</html> 