<%--
  Messages page for Quiz Website
  Handles viewing and sending messages (notes, friend requests, challenges)
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="model.Message" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <title>Messages - Quiz Website</title>
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
            max-width: 900px;
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
        .stats {
            display: flex;
            gap: 1em;
            margin-bottom: 1em;
            justify-content: center;
        }
        .stat-item {
            background: #e9ecef;
            padding: 0.8em;
            border-radius: 12px;
            text-align: center;
            flex: 1;
            min-width: 120px;
        }
        .stat-number {
            font-size: 1.2em;
            font-weight: bold;
            color: #667eea;
        }
        .stat-label {
            color: #666;
            font-size: 0.8em;
        }
        .section {
            margin-bottom: 2em;
            padding: 1.5em;
            border-radius: 16px;
            background: #fafbff;
            box-shadow: 0 2px 12px rgba(102,126,234,0.07);
            text-align: left;
        }
        .section h3 {
            margin-top: 0;
            color: #333;
            border-bottom: 2px solid #667eea;
            padding-bottom: 0.5em;
        }
        .form-row {
            display: flex;
            gap: 1em;
        }
        .form-group {
            margin-bottom: 1em;
            flex: 1;
        }
        .form-group label {
            display: block;
            margin-bottom: 0.3em;
            font-weight: bold;
            color: #333;
        }
        .form-group input, .form-group select, .form-group textarea {
            width: 100%;
            padding: 0.5em;
            border: 1px solid #aaa;
            border-radius: 8px;
            font-size: 1em;
            background: #f8f9fa;
        }
        .form-group textarea { height: 80px; resize: vertical; }
        .alert {
            padding: 16px;
            border-radius: 12px;
            margin-bottom: 25px;
            font-weight: 500;
            text-align: center;
        }
        .alert-success {
            background: linear-gradient(135deg, #00b894, #00a085);
            color: white;
            box-shadow: 0 6px 20px rgba(0, 184, 148, 0.3);
        }
        .alert-danger {
            background: linear-gradient(135deg, #ff6b6b, #ee5a24);
            color: white;
            box-shadow: 0 6px 20px rgba(255, 107, 107, 0.3);
        }
        .empty-state {
            text-align: center;
            color: #666;
            font-style: italic;
            padding: 2em;
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
            margin-bottom: 18px;
        }
        .primary-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
        }
        .primary-btn:active { transform: translateY(0); }
        .btn, .btn-small {
            padding: 0.4em 0.8em;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            text-decoration: none;
            font-size: 0.95em;
            font-weight: 600;
            margin-right: 0.3em;
            margin-bottom: 0.2em;
        }
        .btn-primary { background: #667eea; color: white; }
        .btn-primary:hover { background: #764ba2; }
        .btn-success { background: #28a745; color: white; }
        .btn-success:hover { background: #218838; }
        .btn-warning { background: #ffc107; color: #212529; }
        .btn-warning:hover { background: #e0a800; }
        .btn-danger { background: #dc3545; color: white; }
        .btn-danger:hover { background: #b52a37; }
        .filter-btn { background: #e9ecef; color: #495057; border: 1px solid #adb5bd; margin-right: 0.5em; }
        .filter-btn.active { background: #667eea; color: white; border-color: #667eea; }
        .message-item {
            padding: 1em;
            margin: 0.5em 0;
            border: 1px solid #eee;
            border-radius: 12px;
            background: #fafafa;
            box-shadow: 0 2px 8px rgba(102,126,234,0.04);
        }
        .message-item.unread { border-left: 4px solid #667eea; background: #f0f8ff; }
        .message-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.5em; }
        .message-type { font-weight: bold; color: #667eea; font-size: 0.9em; }
        .message-type.friend_request { color: #28a745; }
        .message-type.challenge { color: #fd7e14; }
        .message-type.note { color: #6c757d; }
        .message-date { color: #666; font-size: 0.8em; }
        .message-sender { font-weight: bold; color: #333; margin-bottom: 0.3em; }
        .message-content { color: #555; line-height: 1.4; }
        .message-actions { margin-top: 0.5em; display: flex; gap: 0.5em; flex-wrap: wrap; }
        .challenge-info { background: #fff3cd; padding: 0.5em; border-radius: 3px; margin-top: 0.5em; font-size: 0.9em; }
        @media (max-width: 1000px) {
            .main-container { max-width: 98vw; }
        }
        @media (max-width: 700px) {
            .main-container { margin: 20px; padding: 35px 10px; }
            .nav-home { position: relative; top: auto; left: auto; margin-bottom: 20px; display: inline-block; }
            .form-row { flex-direction: column; gap: 0; }
            .stats { flex-direction: column; gap: 0.5em; }
        }
    </style>
    <script>
        function showMessageForm(type) {
            document.getElementById('messageType').value = type;
            const quizField = document.getElementById('quizIdField');
            const submitBtn = document.getElementById('submitBtn');
            if (type === 'challenge') {
                quizField.style.display = 'block';
                submitBtn.textContent = 'Send Challenge';
            } else {
                quizField.style.display = 'none';
                submitBtn.textContent = type === 'friend_request' ? 'Send Friend Request' : 'Send Note';
            }
        }
        function filterMessages(type) {
            const messages = document.querySelectorAll('.message-item');
            const buttons = document.querySelectorAll('.filter-btn');
            buttons.forEach(btn => btn.classList.remove('active'));
            event.target.classList.add('active');
            messages.forEach(msg => {
                if (type === 'all' || msg.dataset.type === type) {
                    msg.style.display = 'block';
                } else {
                    msg.style.display = 'none';
                }
            });
        }
    </script>
</head>
<body>
<a href="${pageContext.request.contextPath}/" class="nav-home">&larr; Home</a>
<div class="main-container">
    <div class="page-title">Messages</div>
    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>
    <div class="stats">
        <div class="stat-item">
            <div class="stat-number">${messages.size()}</div>
            <div class="stat-label">Total Messages</div>
        </div>
        <div class="stat-item">
            <div class="stat-number">
                <c:set var="unreadCount" value="0" />
                <c:forEach var="message" items="${messages}">
                    <c:if test="${!message.read}">
                        <c:set var="unreadCount" value="${unreadCount + 1}" />
                    </c:if>
                </c:forEach>
                ${unreadCount}
            </div>
            <div class="stat-label">Unread</div>
        </div>
        <div class="stat-item">
            <div class="stat-number">
                <c:set var="challengeCount" value="0" />
                <c:forEach var="message" items="${messages}">
                    <c:if test="${message.messageType == 'challenge'}">
                        <c:set var="challengeCount" value="${challengeCount + 1}" />
                    </c:if>
                </c:forEach>
                ${challengeCount}
            </div>
            <div class="stat-label">Challenges</div>
        </div>
    </div>
    <div class="section">
        <h3>Send Message</h3>
        <form action="${pageContext.request.contextPath}/messages/send" method="post">
            <div class="form-row">
                <div class="form-group">
                    <label for="recipient">Recipient Username:</label>
                    <input type="text" id="recipient" name="recipient" required placeholder="Enter username" value="${param.friendUsername}" />
                </div>
                <div class="form-group">
                    <label for="messageTypeSelect">Message Type:</label>
                    <select id="messageTypeSelect" onchange="showMessageForm(this.value)" required>
                        <option value="note">Note</option>
                        <option value="friend_request">Friend Request</option>
                        <option value="challenge">Quiz Challenge (only available for friends)</option>
                    </select>
                    <input type="hidden" id="messageType" name="messageType" value="note" />
                    <div style="font-size:0.9em;color:#888;margin-top:4px;">* You can only send quiz challenges to your friends.</div>
                </div>
            </div>
            <div class="form-group" id="quizIdField" style="display: none;">
                <label for="quizName">Quiz Name (for challenges):</label>
                <input type="text" id="quizName" name="quizName" placeholder="Enter quiz name" />
            </div>
            <div class="form-group">
                <label for="content">Message Content:</label>
                <textarea id="content" name="content" required placeholder="Enter your message here..."></textarea>
            </div>
            <button type="submit" id="submitBtn" class="primary-btn">Send Note</button>
        </form>
    </div>
    <div class="section">
        <h3>Your Messages</h3>
        <div class="message-filter" style="margin-bottom: 1em;">
            <button class="btn filter-btn active" onclick="filterMessages('all')">All</button>
            <button class="btn filter-btn" onclick="filterMessages('note')">Notes</button>
            <button class="btn filter-btn" onclick="filterMessages('friend_request')">Friend Requests</button>
            <button class="btn filter-btn" onclick="filterMessages('challenge')">Challenges</button>
        </div>
        <c:choose>
            <c:when test="${empty messages}">
                <div class="empty-state">No messages yet. Connect with friends to start messaging!</div>
            </c:when>
            <c:otherwise>
                <c:forEach var="message" items="${messages}">
                    <div class="message-item ${!message.read ? 'unread' : ''}" data-type="${message.messageType}">
                        <div class="message-header">
                            <span class="message-type ${message.messageType}">
                                <c:choose>
                                    <c:when test="${message.messageType == 'note'}">📝 Note</c:when>
                                    <c:when test="${message.messageType == 'friend_request'}">👥 Friend Request</c:when>
                                    <c:when test="${message.messageType == 'challenge'}">🎯 Quiz Challenge</c:when>
                                </c:choose>
                            </span>
                            <span class="message-date">
                                <fmt:formatDate value="${message.dateSent}" pattern="yyyy-MM-dd HH:mm" />
                            </span>
                        </div>
                        <div class="message-sender">
                            From: <c:out value="${message.senderUsername}" />
                        </div>
                        <div class="message-content">
                            <c:choose>
                                <c:when test="${message.messageType == 'challenge'}">
                                    <div class="challenge-info">
                                        <c:out value="${message.content}" />
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div>${fn:escapeXml(message.content)}</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <c:if test="${message.messageType == 'challenge' && message.quizId != null}">
                            <div class="challenge-info">
                                🎯 Quiz Challenge: Quiz <b>${message.quizName}</b>
                                <a href="${pageContext.request.contextPath}/quiz?id=${message.quizId}" class="btn btn-small btn-warning">Take Challenge</a>
                            </div>
                        </c:if>
                        <c:if test="${message.messageType == 'friend_request'}">
                            <div class="message-actions">
                                <a href="${pageContext.request.contextPath}/friends" class="btn btn-small btn-success">Manage Friends</a>
                            </div>
                        </c:if>
                        <div class="message-actions">
                            <a href="${pageContext.request.contextPath}/messages?friendUsername=${message.senderUsername}" class="btn btn-small btn-primary">Reply</a>
                            <c:if test="${!message.read}">
                                <form action="${pageContext.request.contextPath}/messages/markRead" method="post" style="display:inline;">
                                    <input type="hidden" name="messageId" value="${message.messageId}" />
                                    <button type="submit" class="btn btn-small btn-success">Mark as Read</button>
                                </form>
                                <span class="btn btn-small" style="background: #17a2b8; color: white;">New</span>
                            </c:if>
                            <form action="${pageContext.request.contextPath}/messages/delete" method="post" style="display:inline;">
                                <input type="hidden" name="messageId" value="${message.messageId}" />
                                <button type="submit" class="btn btn-small btn-danger" onclick="return confirm('Are you sure you want to delete this message?');">Delete</button>
                            </form>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html> 