<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quiz Management - Admin Panel</title>
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
        
        .quizzes-table-container {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 15px;
            padding: 30px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            backdrop-filter: blur(10px);
        }
        
        .table-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
        }
        
        .table-title {
            font-size: 1.4em;
            font-weight: 600;
            color: #4a5568;
        }
        
        .quizzes-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        
        .quizzes-table th,
        .quizzes-table td {
            padding: 15px 12px;
            text-align: left;
            border-bottom: 1px solid #e2e8f0;
        }
        
        .quizzes-table th {
            background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
            font-weight: 600;
            color: #4a5568;
            text-transform: uppercase;
            font-size: 0.85em;
            letter-spacing: 0.5px;
        }
        
        .quizzes-table tr {
            transition: all 0.3s ease;
        }
        
        .quizzes-table tbody tr:hover {
            background: rgba(198, 40, 40, 0.05);
            transform: translateY(-1px);
        }
        
        .quiz-title {
            font-weight: 600;
            color: #2d3748;
            margin-bottom: 5px;
        }
        
        .quiz-description {
            color: #718096;
            font-size: 0.9em;
            line-height: 1.4;
            max-width: 300px;
        }
        
        .quiz-meta {
            display: flex;
            flex-direction: column;
            gap: 3px;
        }
        
        .meta-item {
            font-size: 0.85em;
            color: #718096;
        }
        
        .creator-name {
            color: #c62828;
            font-weight: 500;
        }
        
        .quiz-stats {
            text-align: center;
        }
        
        .stat-value {
            font-size: 1.1em;
            font-weight: 600;
            color: #2d3748;
        }
        
        .practice-badge {
            display: inline-block;
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 0.75em;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .practice-yes {
            background: rgba(72, 187, 120, 0.1);
            color: #38a169;
        }
        
        .practice-no {
            background: rgba(160, 174, 192, 0.1);
            color: #718096;
        }
        
        .settings-indicators {
            display: flex;
            flex-direction: column;
            gap: 5px;
        }
        
        .setting-indicator {
            display: inline-block;
            padding: 2px 6px;
            border-radius: 8px;
            font-size: 0.7em;
            font-weight: 500;
        }
        
        .setting-enabled {
            background: rgba(72, 187, 120, 0.1);
            color: #38a169;
        }
        
        .setting-disabled {
            background: rgba(160, 174, 192, 0.1);
            color: #a0aec0;
        }
        
        .btn-delete {
            background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
            color: white;
            border: none;
            padding: 8px 15px;
            border-radius: 8px;
            cursor: pointer;
            font-size: 0.85em;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        
        .btn-delete:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(220, 38, 38, 0.4);
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
            width: 120px;
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
            
            .quizzes-table {
                font-size: 0.9em;
            }
            
            .quiz-description {
                max-width: 200px;
            }
        }
    </style>
</head>
<body>
    <!-- Header with Navigation -->
    <div class="header">
        <div class="header-content">
            <h1>🎯 Quiz Management</h1>
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
            <a href="${pageContext.request.contextPath}/admin/dashboard">Admin Dashboard</a> / Quiz Management
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
                       placeholder="Search quizzes by title or creator..." 
                       id="searchInput"
                       onkeyup="filterQuizzes()">
                <button class="btn btn-search" onclick="clearSearch()">Clear</button>
            </div>
        </div>

        <!-- Statistics Summary -->
        <div class="stats-summary">
            <div class="stat-card">
                <div class="stat-number">${fn:length(quizzes)}</div>
                <div class="stat-label">Total Quizzes</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">
                    <c:set var="practiceCount" value="0"/>
                    <c:forEach items="${quizzes}" var="quiz">
                        <c:if test="${quiz.practiceMode}">
                            <c:set var="practiceCount" value="${practiceCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${practiceCount}
                </div>
                <div class="stat-label">Practice Quizzes</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">
                    <c:set var="totalQuestions" value="0"/>
                    <c:forEach items="${questionCounts}" var="entry">
                        <c:set var="totalQuestions" value="${totalQuestions + entry.value}"/>
                    </c:forEach>
                    ${totalQuestions}
                </div>
                <div class="stat-label">Total Questions</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">
                    <c:set var="totalAttempts" value="0"/>
                    <c:forEach items="${attemptCounts}" var="entry">
                        <c:set var="totalAttempts" value="${totalAttempts + entry.value}"/>
                    </c:forEach>
                    ${totalAttempts}
                </div>
                <div class="stat-label">Total Attempts</div>
            </div>
        </div>

        <!-- Quizzes Table -->
        <div class="quizzes-table-container">
            <div class="table-header">
                <h2 class="table-title">All Quizzes</h2>
                <span class="stat-label">Showing ${fn:length(quizzes)} quizzes</span>
            </div>

            <c:choose>
                <c:when test="${empty quizzes}">
                    <div class="empty-state">
                        <h3>No Quizzes Found</h3>
                        <p>There are no quizzes in the system yet.</p>
                        <a href="${pageContext.request.contextPath}/quiz/create" class="btn btn-primary" style="margin-top: 20px;">
                            Create First Quiz
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="quizzes-table" id="quizzesTable">
                        <thead>
                            <tr>
                                <th>Quiz Details</th>
                                <th>Creator</th>
                                <th>Questions</th>
                                <th>Attempts</th>
                                <th>Practice Mode</th>
                                <th>Settings</th>
                                <th>Created Date</th>
                                <th class="actions-column">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${quizzes}" var="quiz">
                                <tr class="quiz-row">
                                    <td>
                                        <div class="quiz-title">${fn:escapeXml(quiz.title)}</div>
                                        <div class="quiz-description">
                                            <c:choose>
                                                <c:when test="${fn:length(quiz.description) > 100}">
                                                    ${fn:substring(fn:escapeXml(quiz.description), 0, 100)}...
                                                </c:when>
                                                <c:otherwise>
                                                    ${fn:escapeXml(quiz.description)}
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="quiz-meta">
                                            <span class="creator-name">${fn:escapeXml(creatorNames[quiz.quizId])}</span>
                                            <span class="meta-item">ID: ${quiz.creatorId}</span>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="quiz-stats">
                                            <div class="stat-value">${questionCounts[quiz.quizId]}</div>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="quiz-stats">
                                            <div class="stat-value">${attemptCounts[quiz.quizId]}</div>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="practice-badge ${quiz.practiceMode ? 'practice-yes' : 'practice-no'}">
                                            ${quiz.practiceMode ? 'Yes' : 'No'}
                                        </span>
                                    </td>
                                    <td>
                                        <div class="settings-indicators">
                                            <span class="setting-indicator ${quiz.randomOrder ? 'setting-enabled' : 'setting-disabled'}">
                                                Random Order
                                            </span>
                                            <span class="setting-indicator ${quiz.onePage ? 'setting-enabled' : 'setting-disabled'}">
                                                One Page
                                            </span>
                                            <span class="setting-indicator ${quiz.immediateCorrection ? 'setting-enabled' : 'setting-disabled'}">
                                                Immediate Correction
                                            </span>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="date-display">
                                            <fmt:formatDate value="${quiz.createdDate}" pattern="MMM dd, yyyy"/>
                                            <br>
                                            <fmt:formatDate value="${quiz.createdDate}" pattern="HH:mm"/>
                                        </div>
                                    </td>
                                    <td class="actions-column">
                                        <form method="post" action="${pageContext.request.contextPath}/admin/quizzes/delete" 
                                              style="display: inline;"
                                              onsubmit="return confirm('Are you sure you want to delete this quiz? This will also delete all questions and quiz attempts associated with it.');">
                                            <input type="hidden" name="id" value="${quiz.quizId}">
                                            <button type="submit" class="btn-delete">Delete</button>
                                        </form>
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
        function filterQuizzes() {
            const searchInput = document.getElementById('searchInput');
            const filter = searchInput.value.toLowerCase();
            const table = document.getElementById('quizzesTable');
            const rows = table.getElementsByClassName('quiz-row');

            for (let i = 0; i < rows.length; i++) {
                const row = rows[i];
                const title = row.cells[0].textContent.toLowerCase();
                const creator = row.cells[1].textContent.toLowerCase();
                
                if (title.includes(filter) || creator.includes(filter)) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            }
        }

        function clearSearch() {
            document.getElementById('searchInput').value = '';
            filterQuizzes();
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