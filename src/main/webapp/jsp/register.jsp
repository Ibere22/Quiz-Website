<%--
  Created by IntelliJ IDEA.
  User: nika
  Date: 7/6/2025
  Time: 9:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register - Quiz Website</title>
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
            line-height: 1.6;
        }
        
        /* =========================== NAVIGATION =========================== */
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
        
        /* =========================== MAIN CONTAINER =========================== */
        .main-container {
            background: white;
            padding: 45px;
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
        
        /* Decorative corner */
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
        
        /* =========================== HEADER SECTION =========================== */
        .page-header {
            margin-bottom: 35px;
        }
        
        .page-icon {
            font-size: 50px;
            margin-bottom: 15px;
            color: #667eea;
        }
        
        .page-title {
            color: #2c3e50;
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 8px;
        }
        
        .page-subtitle {
            color: #7f8c8d;
            font-size: 16px;
            font-weight: 400;
        }
        
        /* =========================== FORM STYLES =========================== */
        .form-group {
            margin-bottom: 25px;
            text-align: left;
        }
        
        .form-label {
            display: block;
            margin-bottom: 8px;
            color: #34495e;
            font-weight: 600;
            font-size: 14px;
        }
        
        .form-input {
            width: 100%;
            padding: 16px;
            border: 2px solid #e8ecf0;
            border-radius: 12px;
            font-size: 16px;
            transition: all 0.3s ease;
            background: #f8f9fa;
            color: #2c3e50;
        }
        
        .form-input:focus {
            outline: none;
            border-color: #667eea;
            background: white;
            box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
        }
        
        .form-input::placeholder {
            color: #95a5a6;
        }
        
        /* =========================== BUTTON STYLES =========================== */
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
            margin-bottom: 25px;
        }
        
        .primary-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
        }
        
        .primary-btn:active {
            transform: translateY(0);
        }
        
        /* =========================== ALERT STYLES =========================== */
        .alert {
            padding: 16px;
            border-radius: 12px;
            margin-bottom: 25px;
            font-weight: 500;
            text-align: center;
        }
        
        .alert-error {
            background: linear-gradient(135deg, #ff6b6b, #ee5a24);
            color: white;
            box-shadow: 0 6px 20px rgba(255, 107, 107, 0.3);
        }
        
        .alert-success {
            background: linear-gradient(135deg, #00b894, #00a085);
            color: white;
            box-shadow: 0 6px 20px rgba(0, 184, 148, 0.3);
        }
        
        /* =========================== DIVIDER STYLES =========================== */
        .divider {
            margin: 30px 0 20px 0;
            text-align: center;
        }
        
        .divider-line {
            height: 1px;
            background: #e8ecf0;
            margin-bottom: 15px;
        }
        
        .divider-text {
            color: #95a5a6;
            font-size: 14px;
            font-weight: 500;
        }
        
        /* =========================== LINK STYLES =========================== */
        .secondary-link {
            color: #7f8c8d;
            font-size: 14px;
            text-align: center;
        }
        
        .secondary-link a {
            color: #667eea;
            text-decoration: none;
            font-weight: 600;
            transition: color 0.3s ease;
        }
        
        .secondary-link a:hover {
            color: #764ba2;
            text-decoration: underline;
        }
        

        
        /* =========================== RESPONSIVE DESIGN =========================== */
        @media (max-width: 600px) {
            .main-container {
                margin: 20px;
                padding: 35px 25px;
            }
            
            .nav-home {
                position: relative;
                top: auto;
                left: auto;
                margin-bottom: 20px;
                display: inline-block;
            }
            
            body {
                padding: 20px;
                align-items: flex-start;
            }
            
            .page-title {
                font-size: 24px;
            }
            
            .page-icon {
                font-size: 40px;
            }
        }
    </style>
</head>
<body>
    <a href="${pageContext.request.contextPath}/" class="nav-home">← Back to Home</a>
    
    <div class="main-container">
        <div class="page-header">
            <div class="page-icon">✨</div>
            <h1 class="page-title">Join Our Quiz Website</h1>
            <p class="page-subtitle">Create your account and start your learning journey</p>
        </div>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">
                ${error}
            </div>
        </c:if>
        
        <form method="post" action="register">
            <div class="form-group">
                <label for="username" class="form-label">Username</label>
                <input type="text" id="username" name="username" class="form-input" required placeholder="Choose a unique username" />
            </div>
            
            <div class="form-group">
                <label for="email" class="form-label">Email Address</label>
                <input type="email" id="email" name="email" class="form-input" required placeholder="Enter your email address" />
            </div>
            
            <div class="form-group">
                <label for="password" class="form-label">Password</label>
                <input type="password" id="password" name="password" class="form-input" required placeholder="Create a password" />
            </div>
            
            <div class="form-group">
                <label for="confirmPassword" class="form-label">Confirm Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" class="form-input" required placeholder="Confirm your password" />
            </div>
            
            <button type="submit" class="primary-btn">Create Account</button>
        </form>
        
        <div class="divider">
            <div class="divider-line"></div>
            <div class="divider-text">Already have an account?</div>
        </div>
        
        <div class="secondary-link">
            Sign in to your existing account <a href="login">here</a>
        </div>
    </div>
</body>
</html>
