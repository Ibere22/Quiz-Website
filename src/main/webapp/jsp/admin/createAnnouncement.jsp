<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Announcement - Admin Panel</title>
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

        .btn-secondary {
            background-color: #757575;
            color: white;
        }

        .btn-secondary:hover {
            background-color: #616161;
        }

        .container {
            max-width: 800px;
            margin: 2rem auto;
            padding: 0 1rem;
        }

        .alert {
            padding: 0.75rem 1rem;
            margin-bottom: 1rem;
            border-radius: 4px;
            font-weight: 500;
        }

        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .form-container {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            padding: 2rem;
        }

        .page-title {
            font-size: 2rem;
            font-weight: 600;
            color: #333;
            margin-bottom: 2rem;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 600;
            color: #555;
        }

        .form-control {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 1rem;
            transition: border-color 0.3s ease;
        }

        .form-control:focus {
            outline: none;
            border-color: #1976d2;
            box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.2);
        }

        .form-select {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 1rem;
            background-color: white;
            cursor: pointer;
        }

        .form-select:focus {
            outline: none;
            border-color: #1976d2;
            box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.2);
        }

        textarea.form-control {
            resize: vertical;
            min-height: 120px;
        }

        .form-actions {
            display: flex;
            gap: 1rem;
            justify-content: flex-end;
            margin-top: 2rem;
            padding-top: 1rem;
            border-top: 1px solid #eee;
        }

        .priority-info {
            font-size: 0.9rem;
            color: #666;
            margin-top: 0.25rem;
        }

        .required {
            color: #d32f2f;
        }

        .breadcrumb {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            margin-bottom: 1rem;
            font-size: 0.9rem;
            color: #666;
        }

        .breadcrumb a {
            color: #1976d2;
            text-decoration: none;
        }

        .breadcrumb a:hover {
            text-decoration: underline;
        }

        .char-counter {
            font-size: 0.8rem;
            color: #666;
            text-align: right;
            margin-top: 0.25rem;
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="header-content">
            <h1>➕ Create Announcement</h1>
            <div class="header-actions">
                <span>Admin: ${sessionScope.admin.username}</span>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary">Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/logout" class="btn btn-danger">Logout</a>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- Breadcrumb -->
        <div class="breadcrumb">
            <a href="${pageContext.request.contextPath}/admin/dashboard">Admin Dashboard</a>
            <span>›</span>
            <a href="${pageContext.request.contextPath}/admin/announcements">Announcements</a>
            <span>›</span>
            <span>Create New</span>
        </div>

        <!-- Error Messages -->
        <c:if test="${not empty requestScope.error}">
            <div class="alert alert-error">${requestScope.error}</div>
        </c:if>

        <!-- Form Container -->
        <div class="form-container">
            <h2 class="page-title">Create New Announcement</h2>
            
            <form method="post" action="${pageContext.request.contextPath}/admin/announcements/create">
                <div class="form-group">
                    <label for="title" class="form-label">
                        Title <span class="required">*</span>
                    </label>
                    <input type="text" 
                           id="title" 
                           name="title" 
                           class="form-control" 
                           placeholder="Enter announcement title..."
                           maxlength="255"
                           required
                           value="${param.title}">
                    <div class="char-counter">
                        <span id="titleCounter">0</span>/255 characters
                    </div>
                </div>

                <div class="form-group">
                    <label for="content" class="form-label">
                        Content <span class="required">*</span>
                    </label>
                    <textarea id="content" 
                              name="content" 
                              class="form-control" 
                              placeholder="Enter announcement content..."
                              required
                              rows="6">${param.content}</textarea>
                    <div class="char-counter">
                        <span id="contentCounter">0</span> characters
                    </div>
                </div>

                <div class="form-group">
                    <label for="priority" class="form-label">Priority</label>
                    <select id="priority" name="priority" class="form-select">
                        <option value="low" ${param.priority == 'low' ? 'selected' : ''}>Low Priority</option>
                        <option value="medium" ${param.priority == 'medium' || empty param.priority ? 'selected' : ''}>Medium Priority</option>
                        <option value="high" ${param.priority == 'high' ? 'selected' : ''}>High Priority</option>
                    </select>
                    <div class="priority-info">
                        High priority announcements appear first on the homepage
                    </div>
                </div>

                <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/admin/announcements" class="btn btn-secondary">
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-success">
                        ✅ Create Announcement
                    </button>
                </div>
            </form>
        </div>
    </div>

    <script>
        // Character counters
        function updateCounter(input, counterId) {
            document.getElementById(counterId).textContent = input.value.length;
        }

        document.getElementById('title').addEventListener('input', function() {
            updateCounter(this, 'titleCounter');
        });

        document.getElementById('content').addEventListener('input', function() {
            updateCounter(this, 'contentCounter');
        });

        // Initialize counters
        document.addEventListener('DOMContentLoaded', function() {
            updateCounter(document.getElementById('title'), 'titleCounter');
            updateCounter(document.getElementById('content'), 'contentCounter');
        });

        // Form validation
        document.querySelector('form').addEventListener('submit', function(e) {
            const title = document.getElementById('title').value.trim();
            const content = document.getElementById('content').value.trim();

            if (!title) {
                alert('Please enter a title for the announcement.');
                e.preventDefault();
                return;
            }

            if (!content) {
                alert('Please enter content for the announcement.');
                e.preventDefault();
                return;
            }

            if (title.length > 255) {
                alert('Title is too long. Maximum 255 characters allowed.');
                e.preventDefault();
                return;
            }
        });
    </script>
</body>
</html> 