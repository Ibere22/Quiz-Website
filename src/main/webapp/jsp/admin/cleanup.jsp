<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Data Cleanup - Admin Panel</title>
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

        .warning-banner {
            background-color: #fff3cd;
            color: #856404;
            border: 1px solid #ffeaa7;
            border-radius: 4px;
            padding: 1rem;
            margin-bottom: 2rem;
        }

        .warning-banner h3 {
            margin-bottom: 0.5rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .cleanup-grid {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 2rem;
        }

        .cleanup-operations {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }

        .operations-header {
            background-color: #f8f9fa;
            padding: 1rem;
            border-bottom: 1px solid #dee2e6;
        }

        .operations-title {
            font-size: 1.2rem;
            font-weight: 600;
            color: #333;
        }

        .cleanup-item {
            padding: 1.5rem;
            border-bottom: 1px solid #eee;
        }

        .cleanup-item:last-child {
            border-bottom: none;
        }

        .item-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 1rem;
        }

        .item-title {
            font-size: 1.1rem;
            font-weight: 600;
            color: #333;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .danger-badge {
            padding: 0.25rem 0.5rem;
            border-radius: 12px;
            font-size: 0.75rem;
            font-weight: 600;
            text-transform: uppercase;
        }

        .danger-medium {
            background-color: #fff3e0;
            color: #ef6c00;
        }

        .danger-high {
            background-color: #ffebee;
            color: #c62828;
        }

        .item-description {
            color: #666;
            line-height: 1.5;
            margin-bottom: 1rem;
        }

        .item-details {
            background-color: #f8f9fa;
            border-radius: 4px;
            padding: 1rem;
            margin-bottom: 1rem;
        }

        .detail-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.5rem;
        }

        .detail-row:last-child {
            margin-bottom: 0;
        }

        .detail-label {
            font-weight: 500;
            color: #333;
        }

        .detail-value {
            color: #666;
        }

        .cleanup-button {
            width: 100%;
            padding: 0.75rem 1rem;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: 600;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
        }

        .statistics-panel {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            padding: 1.5rem;
            height: fit-content;
        }

        .stats-title {
            font-size: 1.2rem;
            font-weight: 600;
            color: #333;
            margin-bottom: 1rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: 1fr;
            gap: 1rem;
        }

        .stat-item {
            background-color: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 4px;
            padding: 1rem;
            text-align: center;
        }

        .stat-number {
            font-size: 1.8rem;
            font-weight: 700;
            color: #c62828;
            margin-bottom: 0.25rem;
        }

        .stat-label {
            font-size: 0.85rem;
            color: #666;
            font-weight: 500;
        }

        .recommendations {
            background-color: #e3f2fd;
            border: 1px solid #bbdefb;
            border-radius: 4px;
            padding: 1rem;
            margin-top: 1.5rem;
        }

        .recommendations h4 {
            color: #333;
            margin-bottom: 0.75rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .recommendations ul {
            color: #666;
            font-size: 0.9rem;
            margin-left: 1rem;
        }

        .recommendations li {
            margin-bottom: 0.5rem;
            line-height: 1.4;
        }

        .confirmation-modal {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            z-index: 1000;
        }

        .modal-content {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: white;
            border-radius: 8px;
            padding: 2rem;
            max-width: 500px;
            width: 90%;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
        }

        .modal-header {
            margin-bottom: 1rem;
            text-align: center;
        }

        .modal-title {
            font-size: 1.25rem;
            font-weight: 600;
            color: #333;
            margin-bottom: 0.5rem;
        }

        .modal-description {
            color: #666;
            line-height: 1.5;
        }

        .modal-actions {
            display: flex;
            gap: 1rem;
            margin-top: 1.5rem;
        }

        .btn-modal {
            padding: 0.75rem 1rem;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: 600;
            flex: 1;
            transition: all 0.3s ease;
        }

        .btn-confirm {
            background-color: #d32f2f;
            color: white;
        }

        .btn-confirm:hover {
            background-color: #c62828;
        }

        .btn-cancel {
            background-color: #e0e0e0;
            color: #333;
        }

        .btn-cancel:hover {
            background-color: #d4d4d4;
        }

        @media (max-width: 768px) {
            .container {
                padding: 0 0.5rem;
            }

            .cleanup-grid {
                grid-template-columns: 1fr;
            }

            .header-content {
                flex-direction: column;
                gap: 1rem;
                align-items: flex-start;
            }

            .stats-grid {
                grid-template-columns: repeat(2, 1fr);
            }
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="header-content">
            <h1>🧹 Data Cleanup</h1>
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
            <h2 class="page-title">System Data Cleanup</h2>
        </div>

        <!-- Warning Banner -->
        <div class="warning-banner">
            <h3>
                ⚠️ CAUTION: Destructive Operations
            </h3>
            <p>
                The cleanup operations below will permanently delete data from the database. 
                Please ensure you have proper backups before proceeding. All operations are <strong>irreversible</strong>.
            </p>
        </div>

        <!-- Content Grid -->
        <div class="cleanup-grid">
            <!-- Cleanup Operations -->
            <div class="cleanup-operations">
                <div class="operations-header">
                    <h3 class="operations-title">Available Cleanup Operations</h3>
                </div>
                
                <!-- Quiz History Cleanup -->
                <div class="cleanup-item">
                    <div class="item-header">
                        <div class="item-title">
                            📊 Quiz History Cleanup
                        </div>
                        <span class="danger-badge danger-medium">Medium Risk</span>
                    </div>
                    <div class="item-description">
                        Remove all quiz attempt records from the system. This will clear the complete history 
                        of user quiz performances but preserve the quizzes and questions themselves.
                    </div>
                    <div class="item-details">
                        <div class="detail-row">
                            <span class="detail-label">What will be deleted:</span>
                            <span class="detail-value">All quiz attempt records, scores, and timing data</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">What will be preserved:</span>
                            <span class="detail-value">Quizzes, questions, users, and announcements</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Impact:</span>
                            <span class="detail-value">Users will lose their quiz history and leaderboard rankings</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Current Records:</span>
                            <span class="detail-value">${totalQuizAttempts} quiz attempts</span>
                        </div>
                    </div>
                    <button type="button" class="cleanup-button btn-warning" 
                            onclick="showConfirmation('clearHistory', 'Clear All Quiz History', 'This will permanently delete all ${totalQuizAttempts} quiz attempt records. Users will lose their complete quiz history and scores. Are you absolutely sure?')">
                        🗑️ Clear Quiz History
                    </button>
                </div>

                <!-- All Announcements Cleanup -->
                <div class="cleanup-item">
                    <div class="item-header">
                        <div class="item-title">
                            📢 All Announcements Cleanup
                        </div>
                        <span class="danger-badge danger-high">High Risk</span>
                    </div>
                    <div class="item-description">
                        Remove all announcements from the system. This will clear all current announcements 
                        that are displayed to users, including both active and inactive ones.
                    </div>
                    <div class="item-details">
                        <div class="detail-row">
                            <span class="detail-label">What will be deleted:</span>
                            <span class="detail-value">All announcements (active and inactive)</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">What will be preserved:</span>
                            <span class="detail-value">Users, quizzes, questions, and quiz attempts</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Impact:</span>
                            <span class="detail-value">Homepage will show no announcements until new ones are created</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Current Records:</span>
                            <span class="detail-value">${totalAnnouncements} total announcements</span>
                        </div>
                    </div>
                    <button type="button" class="cleanup-button btn-danger" 
                            onclick="showConfirmation('clearAllAnnouncements', 'Clear All Announcements', 'This will delete all ${totalAnnouncements} announcements (both active and inactive). The homepage will show no announcements until you create new ones. Continue?')">
                        🗑️ Clear All Announcements
                    </button>
                </div>
            </div>

            <!-- Statistics Panel -->
            <div class="statistics-panel">
                <h3 class="stats-title">
                    📈 System Statistics
                </h3>
                <div class="stats-grid">
                    <div class="stat-item">
                        <div class="stat-number">${totalQuizAttempts}</div>
                        <div class="stat-label">Quiz Attempts</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-number">${totalQuizzes}</div>
                        <div class="stat-label">Total Quizzes</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-number">${totalUsers}</div>
                        <div class="stat-label">Total Users</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-number">${totalAnnouncements}</div>
                        <div class="stat-label">Total Announcements</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-number">${totalQuestions}</div>
                        <div class="stat-label">Total Questions</div>
                    </div>
                </div>

                <div class="recommendations">
                    <h4>💡 Cleanup Recommendations</h4>
                    <ul>
                        <li>Create database backups before cleanup</li>
                        <li>Test operations in development first</li>
                        <li>Schedule cleanup during maintenance windows</li>
                        <li>Monitor system performance after cleanup</li>
                        <li>Document all cleanup operations performed</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <!-- Hidden Forms for Cleanup Actions -->
    <form id="clearHistoryForm" method="post" action="${pageContext.request.contextPath}/admin/cleanup" style="display: none;">
        <input type="hidden" name="action" value="clearHistory">
    </form>
    
    <form id="clearAllAnnouncementsForm" method="post" action="${pageContext.request.contextPath}/admin/cleanup" style="display: none;">
        <input type="hidden" name="action" value="clearAllAnnouncements">
    </form>

    <!-- Confirmation Modal -->
    <div id="confirmationModal" class="confirmation-modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title" id="modalTitle">Confirm Operation</h3>
                <p class="modal-description" id="modalDescription">
                    Are you sure you want to proceed with this operation?
                </p>
            </div>
            <div class="modal-actions">
                <button type="button" class="btn-modal btn-cancel" onclick="hideConfirmation()">
                    Cancel
                </button>
                <button type="button" class="btn-modal btn-confirm" id="confirmButton" onclick="executeAction()">
                    Confirm Delete
                </button>
            </div>
        </div>
    </div>

    <script>
        let currentAction = '';
        
        function showConfirmation(action, title, description) {
            currentAction = action;
            document.getElementById('modalTitle').textContent = title;
            document.getElementById('modalDescription').textContent = description;
            document.getElementById('confirmationModal').style.display = 'block';
            
            // Disable body scroll
            document.body.style.overflow = 'hidden';
        }
        
        function hideConfirmation() {
            document.getElementById('confirmationModal').style.display = 'none';
            document.body.style.overflow = 'auto';
            currentAction = '';
        }
        
        function executeAction() {
            if (currentAction) {
                // Submit the corresponding form
                const form = document.getElementById(currentAction + 'Form');
                if (form) {
                    form.submit();
                }
            }
            hideConfirmation();
        }
        
        // Close modal when clicking outside
        document.getElementById('confirmationModal').addEventListener('click', function(e) {
            if (e.target === this) {
                hideConfirmation();
            }
        });
        
        // Escape key to close modal
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                hideConfirmation();
            }
        });
        
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