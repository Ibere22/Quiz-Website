<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Login - Quiz Website</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f8f9fa;
        }
        
        .admin-container {
            max-width: 400px;
            margin: 50px auto;
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            border-top: 4px solid #dc3545;
        }
        
        .admin-header {
            text-align: center;
            margin-bottom: 30px;
        }
        
        .admin-header h1 {
            color: #dc3545;
            margin: 0;
            font-size: 28px;
        }
        
        .admin-header .subtitle {
            color: #6c757d;
            font-size: 14px;
            margin-top: 5px;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #495057;
        }
        
        .form-group input {
            width: 100%;
            padding: 12px;
            border: 1px solid #ced4da;
            border-radius: 4px;
            font-size: 16px;
            box-sizing: border-box;
        }
        
        .form-group input:focus {
            outline: none;
            border-color: #dc3545;
            box-shadow: 0 0 0 0.2rem rgba(220,53,69,0.25);
        }
        
        .btn-admin {
            width: 100%;
            padding: 12px;
            background-color: #dc3545;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        
        .btn-admin:hover {
            background-color: #c82333;
        }
        
        .error-message {
            background-color: #f8d7da;
            color: #721c24;
            padding: 12px;
            border: 1px solid #f5c6cb;
            border-radius: 4px;
            margin-bottom: 20px;
            text-align: center;
        }
        
        .nav-home {
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
        
        .nav-home:hover {
            background-color: #007bff;
            color: white;
        }
        
        .warning-notice {
            background-color: #fff3cd;
            color: #856404;
            padding: 15px;
            border: 1px solid #ffeaa7;
            border-radius: 4px;
            margin-bottom: 20px;
            text-align: center;
            font-size: 14px;
        }
        
        .security-icon {
            font-size: 24px;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
    <a href="${pageContext.request.contextPath}/" class="nav-home">← Back to Main Site</a>
    
    <div class="admin-container">
        <div class="admin-header">
            <div class="security-icon">🔒</div>
            <h1>Admin Access</h1>
            <div class="subtitle">Restricted Area - Authorized Personnel Only</div>
        </div>
        
        <div class="warning-notice">
            ⚠️ This is a secure admin area. Only authorized administrators may access this section.
        </div>
        
        <c:if test="${not empty error}">
            <div class="error-message">
                ${error}
            </div>
        </c:if>
        
        <form method="post" action="${pageContext.request.contextPath}/admin/login">
            <div class="form-group">
                <label for="username">Administrator Username:</label>
                <input type="text" id="username" name="username" required placeholder="Enter admin username" />
            </div>
            
            <div class="form-group">
                <label for="password">Administrator Password:</label>
                <input type="password" id="password" name="password" required placeholder="Enter admin password" />
            </div>
            
            <button type="submit" class="btn-admin">
                🔐 Admin Login
            </button>
        </form>
        
        <div style="text-align: center; margin-top: 20px; font-size: 12px; color: #6c757d;">
            Having trouble? Contact the system administrator.
        </div>
    </div>
</body>
</html> 