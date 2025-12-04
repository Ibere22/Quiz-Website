<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Announcements - Admin Panel</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f5f5f5;
            color: #333;
        }

        .header {
            background: linear-gradient(135deg, #c62828, #b71c1c);
            color: white;
            padding: 1rem 2rem;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .header-content {
            display: flex;
            justify-content: space-between;
            align-items: center;
            max-width: 1200px;
            margin: 0 auto;
        }

        .header h1 {
            font-size: 1.5rem;
            font-weight: 600;
        }

        .header-actions {
            display: flex;
            gap: 1rem;
            align-items: center;
        }

        .btn {
            padding: 0.5rem 1rem;
            border: none;
            border-radius: 4px;
            text-decoration: none;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
            display: inline-block;
        }

        .btn-primary {
            background-color: #1976d2;
            color: white;
        }

        .btn-primary:hover {
            background-color: #1565c0;
        }

        .btn-success {
            background-color: #388e3c;
            color: white;
        }

        .btn-success:hover {
            background-color: #2e7d32;
        }

        .btn-danger {
            background-color: #d32f2f;
            color: white;
        }

        .btn-danger:hover {
            background-color: #c62828;
        }

        .btn-warning {
            background-color: #f57c00;
            color: white;
        }

        .btn-warning:hover {
            background-color: #ef6c00;
        }

        .btn-secondary {
            background-color: #757575;
            color: white;
        }

        .btn-secondary:hover {
            background-color: #616161;
        }

        .container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 1rem;
        }

        .alert {
            padding: 0.75rem 1rem;
            margin-bottom: 1rem;
            border-radius: 4px;
            font-weight: 500;
        }

        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
        }

        .page-title {
            font-size: 2rem;
            font-weight: 600;
            color: #333;
        }

        .announcements-table {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid #eee;
        }

        th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #555;
        }

        tr:hover {
            background-color: #f8f9fa;
        }

        .priority-badge {
            padding: 0.25rem 0.5rem;
            border-radius: 12px;
            font-size: 0.75rem;
            font-weight: 600;
            text-transform: uppercase;
        }

        .priority-high {
            background-color: #ffebee;
            color: #c62828;
        }

        .priority-medium {
            background-color: #fff3e0;
            color: #ef6c00;
        }

        .priority-low {
            background-color: #e8f5e8;
            color: #2e7d32;
        }

        .status-badge {
            padding: 0.25rem 0.5rem;
            border-radius: 12px;
            font-size: 0.75rem;
            font-weight: 600;
        }

        .status-active {
            background-color: #e8f5e8;
            color: #2e7d32;
        }

        .status-inactive {
            background-color: #f5f5f5;
            color: #757575;
        }

        .actions {
            display: flex;
            gap: 0.5rem;
        }

        .btn-sm {
            padding: 0.25rem 0.5rem;
            font-size: 0.75rem;
        }

        .btn-delete {
            background-color: #dc3545;
            color: white;
            border: none;
            padding: 0.5rem 1rem;
            border-radius: 4px;
            cursor: pointer;
            font-size: 0.75rem;
            font-weight: 600;
            transition: background-color 0.2s;
        }

        .btn-delete:hover {
            background-color: #c82333;
        }

        .empty-state {
            text-align: center;
            padding: 3rem;
            color: #666;
        }

        .empty-state h3 {
            margin-bottom: 0.5rem;
        }

        .content-preview {
            max-width: 200px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="header-content">
            <h1>📢 Manage Announcements</h1>
            <div class="header-actions">
                <span>Admin: ${sessionScope.admin.username}</span>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary">Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/logout" class="btn btn-danger">Logout</a>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- Success/Error Messages -->
        <c:if test="${not empty sessionScope.success}">
            <div class="alert alert-success">${sessionScope.success}</div>
            <c:remove var="success" scope="session"/>
        </c:if>

        <c:if test="${not empty sessionScope.error}">
            <div class="alert alert-error">${sessionScope.error}</div>
            <c:remove var="error" scope="session"/>
        </c:if>

        <c:if test="${not empty requestScope.error}">
            <div class="alert alert-error">${requestScope.error}</div>
        </c:if>

        <!-- Page Header -->
        <div class="page-header">
            <h2 class="page-title">Announcements Management</h2>
            <a href="${pageContext.request.contextPath}/admin/announcements/create" class="btn btn-success">
                ➕ Create New Announcement
            </a>
        </div>

        <!-- Announcements Table -->
        <div class="announcements-table">
            <c:choose>
                <c:when test="${not empty announcements}">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Title</th>
                                <th>Content</th>
                                <th>Priority</th>
                                <th>Status</th>
                                <th>Created Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="announcement" items="${announcements}">
                                <tr>
                                    <td>#${announcement.id}</td>
                                    <td><strong>${announcement.title}</strong></td>
                                    <td>
                                        <div class="content-preview" title="${announcement.content}">
                                            ${announcement.content}
                                        </div>
                                    </td>
                                    <td>
                                        <span class="priority-badge priority-${announcement.priority.value}">
                                            ${announcement.priority.value}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="status-badge ${announcement.active ? 'status-active' : 'status-inactive'}">
                                            ${announcement.active ? 'Active' : 'Inactive'}
                                        </span>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${announcement.createdDate}" pattern="MMM dd, yyyy HH:mm"/>
                                    </td>
                                    <td class="actions-cell">
                                        <form method="post" action="${pageContext.request.contextPath}/admin/announcements/delete" 
                                              onsubmit="return confirm('Are you sure you want to delete this announcement?')" style="display: inline;">
                                            <input type="hidden" name="id" value="${announcement.id}">
                                            <button type="submit" class="btn-delete">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <h3>No announcements found</h3>
                        <p>Create your first announcement to get started!</p>
                        <a href="${pageContext.request.contextPath}/admin/announcements/create" 
                           class="btn btn-success" style="margin-top: 1rem;">
                            ➕ Create Announcement
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html> 