<%--
  Created by IntelliJ IDEA.
  User: nika
  Date: 7/7/2025
  Time: 6:36 PM //
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create Quiz</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            line-height: 1.6;
        }
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
        .main-container {
            background: white;
            padding: 45px 35px 35px 35px;
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
        .page-title {
            color: #2c3e50;
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 8px;
        }
        .form-container {
            width: 100%;
            text-align: left;
        }
        label {
            display: block;
            margin-top: 1em;
            font-weight: 600;
            color: #34495e;
            font-size: 15px;
        }
        input[type="text"], textarea {
            width: 100%;
            padding: 16px;
            border: 2px solid #e8ecf0;
            border-radius: 12px;
            font-size: 16px;
            transition: all 0.3s ease;
            background: #f8f9fa;
            color: #2c3e50;
            margin-top: 0.5em;
        }
        input[type="text"]:focus, textarea:focus {
            outline: none;
            border-color: #667eea;
            background: white;
            box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
        }
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
            margin-top: 25px;
            margin-bottom: 10px;
        }
        .primary-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
        }
        .primary-btn:active { transform: translateY(0); }
        .error {
            color: white;
            background: linear-gradient(135deg, #ff6b6b, #ee5a24);
            box-shadow: 0 6px 20px rgba(255, 107, 107, 0.3);
            border-radius: 12px;
            padding: 16px;
            margin-top: 1em;
            margin-bottom: 1em;
            text-align: center;
            font-weight: 500;
        }
        @media (max-width: 600px) {
            .main-container {
                margin: 20px;
                padding: 35px 15px;
            }
            .nav-home {
                position: relative;
                top: auto;
                left: auto;
                margin-bottom: 20px;
                display: inline-block;
            }
        }
    </style>
    <script>
        function updateMutualExclusion() {
            var onePage = document.getElementById('onePage');
            var immediate = document.getElementById('immediateCorrection');
            if (onePage.checked) {
                immediate.checked = false;
                immediate.disabled = true;
                onePage.disabled = false;
            } else if (immediate.checked) {
                onePage.checked = false;
                onePage.disabled = true;
                immediate.disabled = false;
            } else {
                onePage.disabled = false;
                immediate.disabled = false;
            }
        }
        window.onload = function() {
            document.getElementById('onePage').addEventListener('change', updateMutualExclusion);
            document.getElementById('immediateCorrection').addEventListener('change', updateMutualExclusion);
            updateMutualExclusion();
        };
    </script>
</head>
<body>
<a href="${pageContext.request.contextPath}/" class="nav-home">&larr; Home</a>
<div class="main-container">
    <div class="page-title">Create a New Quiz</div>
    <div class="form-container">
        <form action="${pageContext.request.contextPath}/quiz/create" method="post">
            <label for="title">Quiz Title<span style="color:red;">*</span>:</label>
            <input type="text" id="title" name="title" required maxlength="100" placeholder="Enter quiz title">

            <label for="description">Description:</label>
            <textarea id="description" name="description" rows="4" maxlength="500" placeholder="Enter a short description (optional)"></textarea>

            <label><input type="checkbox" id="randomOrder" name="randomOrder" value="true"> Randomize question order</label>

            <label><input type="checkbox" id="onePage" name="onePage" value="true" checked> Show all questions on one page</label>
            <label><input type="checkbox" id="immediateCorrection" name="immediateCorrection" value="true"> Show correct answers immediately after each question</label>

            <button type="submit" class="primary-btn">Create Quiz</button>
        </form>
        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>
    </div>
</div>
</body>
</html>
