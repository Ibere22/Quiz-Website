<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Question</title>
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
            max-width: 600px;
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
        input[type="text"], textarea, select {
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
        input[type="text"]:focus, textarea:focus, select:focus {
            outline: none;
            border-color: #667eea;
            background: white;
            box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
        }
        .choices-group { margin-top: 1em; }
        .choice-input { display: flex; margin-bottom: 0.5em; }
        .choice-input input { flex: 1; }
        .choice-input button { margin-left: 0.5em; }
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
        .add-btn {
            background: #667eea;
            color: white;
            border: none;
            border-radius: 8px;
            padding: 8px 16px;
            margin-left: 0;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.3s;
        }
        .add-btn:hover { background: #764ba2; }
        @media (max-width: 700px) {
            .main-container {
                margin: 20px;
                padding: 35px 10px;
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
        function showFields() {
            var type = document.getElementById('questionType').value;
            var choicesSection = document.getElementById('choicesSection');
            var imageSection = document.getElementById('imageSection');
            choicesSection.style.display = (type === 'multiple-choice') ? 'block' : 'none';
            imageSection.style.display = (type === 'picture-response') ? 'block' : 'none';
            // Toggle 'required' for choices inputs
            var choicesInputs = document.getElementsByName('choices');
            for (var i = 0; i < choicesInputs.length; i++) {
                choicesInputs[i].required = (type === 'multiple-choice');
            }
        }
        function addChoiceField() {
            var group = document.getElementById('choicesGroup');
            var div = document.createElement('div');
            div.className = 'choice-input';
            var type = document.getElementById('questionType').value;
            var requiredAttr = (type === 'multiple-choice') ? 'required' : '';
            div.innerHTML = '<input type="text" name="choices" placeholder="Choice" ' + requiredAttr + '> <button type="button" class="add-btn" onclick="this.parentNode.remove()">Remove</button>';
            group.appendChild(div);
        }
        window.onload = function() {
            showFields();
        };
    </script>
</head>
<body>
<a href="${pageContext.request.contextPath}/" class="nav-home">&larr; Home</a>
<div class="main-container">
    <div class="page-title">Add a Question</div>
    <div class="form-container">
        <% if (request.getAttribute("error") != null) { %>
            <div class="error"> <%= request.getAttribute("error") %> </div>
        <% } %>
        <form action="${pageContext.request.contextPath}/quiz/addQuestion" method="post">
            <label for="questionType">Question Type:</label>
            <select id="questionType" name="questionType" onchange="showFields()" required>
                <option value="multiple-choice" <%= "multiple-choice".equals(request.getAttribute("questionType")) ? "selected" : "" %>>Multiple Choice</option>
                <option value="fill-in-blank" <%= "fill-in-blank".equals(request.getAttribute("questionType")) ? "selected" : "" %>>Fill in the Blank</option>
                <option value="picture-response" <%= "picture-response".equals(request.getAttribute("questionType")) ? "selected" : "" %>>Picture Response</option>
                <option value="question-response" <%= "question-response".equals(request.getAttribute("questionType")) ? "selected" : "" %>>Question Response</option>
            </select>

            <label for="questionText">Question Text:</label>
            <textarea id="questionText" name="questionText" rows="3" required><%= request.getAttribute("questionText") != null ? request.getAttribute("questionText") : "" %></textarea>

            <div id="choicesSection" style="display:none;">
                <label>Choices:</label>
                <div id="choicesGroup" class="choices-group">
                    <% 
                    java.util.List choices = (java.util.List) request.getAttribute("choices");
                    if (choices != null && !choices.isEmpty()) {
                        for (Object c : choices) { %>
                            <div class="choice-input">
                                <input type="text" name="choices" placeholder="Choice" value="<%= c %>" required>
                                <button type="button" class="add-btn" onclick="this.parentNode.remove()">Remove</button>
                            </div>
                    <%  }
                    } else { %>
                        <div class="choice-input">
                            <input type="text" name="choices" placeholder="Choice" required>
                            <button type="button" class="add-btn" onclick="this.parentNode.remove()">Remove</button>
                        </div>
                        <div class="choice-input">
                            <input type="text" name="choices" placeholder="Choice" required>
                            <button type="button" class="add-btn" onclick="this.parentNode.remove()">Remove</button>
                        </div>
                    <% } %>
                </div>
                <button type="button" class="add-btn" onclick="addChoiceField()">Add Choice</button>
            </div>

            <div id="imageSection" style="display:none;">
                <label for="imageUrl">Image URL:</label>
                <input type="text" id="imageUrl" name="imageUrl" placeholder="http://...">
            </div>

            <label for="correctAnswer">Correct Answer(s):</label>
            <input type="text" id="correctAnswer" name="correctAnswer" required value="<%= request.getAttribute("correctAnswer") != null ? request.getAttribute("correctAnswer") : "" %>">
            <small>For multiple correct answers, separate them with a comma (e.g., "George Washington, Washington")</small>

            <button type="submit" name="addAnother" class="primary-btn">Add Another Question</button>
            <button type="submit" class="primary-btn">Finish Quiz</button>
        </form>
    </div>
</div>
</body>
</html> 