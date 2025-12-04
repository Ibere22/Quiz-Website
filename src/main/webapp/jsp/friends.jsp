<%--
  Friends management page for Quiz Website
  Handles viewing friends, pending requests, and sending friend requests
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Friends - Quiz Website</title>
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
            max-width: 800px;
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
            gap: 2em;
            margin-bottom: 1em;
            justify-content: center;
        }
        .stat-item {
            background: #e9ecef;
            padding: 1em;
            border-radius: 12px;
            text-align: center;
            flex: 1;
            min-width: 120px;
        }
        .stat-number {
            font-size: 1.5em;
            font-weight: bold;
            color: #667eea;
        }
        .stat-label {
            color: #666;
            font-size: 0.9em;
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
        .form-container {
            width: 100%;
            text-align: left;
        }
        input[type="text"] {
            width: 200px;
            padding: 0.5em;
            margin-right: 0.5em;
            border: 1px solid #aaa;
            border-radius: 8px;
            font-size: 1em;
            background: #f8f9fa;
        }
        .primary-btn, .btn-send, .btn-accept, .btn-decline {
            padding: 0.7em 2em;
            border: none;
            border-radius: 12px;
            font-size: 1em;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        .primary-btn, .btn-send {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            margin-bottom: 10px;
        }
        .primary-btn:hover, .btn-send:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
        }
        .btn-accept {
            background: #28a745;
            color: white;
        }
        .btn-accept:hover { background: #218838; }
        .btn-decline {
            background: #dc3545;
            color: white;
        }
        .btn-decline:hover { background: #c82333; }
        .friend-item, .request-item {
            padding: 1em;
            margin: 0.5em 0;
            border: 1px solid #eee;
            border-radius: 12px;
            background: #fafafa;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 8px rgba(102,126,234,0.04);
        }
        .friend-info { flex-grow: 1; }
        .friend-name { font-weight: bold; color: #333; }
        .friend-date { color: #666; font-size: 0.9em; margin-top: 0.3em; }
        .action-buttons { display: flex; gap: 0.5em; }
        .message {
            padding: 16px;
            border-radius: 12px;
            margin-bottom: 25px;
            font-weight: 500;
            text-align: center;
        }
        .success {
            background: linear-gradient(135deg, #00b894, #00a085);
            color: white;
            box-shadow: 0 6px 20px rgba(0, 184, 148, 0.3);
        }
        .error {
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
        @media (max-width: 900px) {
            .main-container { max-width: 98vw; }
        }
        @media (max-width: 700px) {
            .main-container { margin: 20px; padding: 35px 10px; }
            .nav-home { position: relative; top: auto; left: auto; margin-bottom: 20px; display: inline-block; }
            .stats { flex-direction: column; gap: 0.5em; }
            .friend-item, .request-item { flex-direction: column; align-items: flex-start; }
            .action-buttons { flex-direction: column; gap: 0.5em; }
        }
    </style>
</head>
<body>
<a href="${pageContext.request.contextPath}/" class="nav-home">&larr; Home</a>
<div class="main-container">
    <div class="page-title">Friends</div>
    <c:if test="${not empty success}">
        <div class="message success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="message error">${error}</div>
    </c:if>
    <div class="stats">
        <div class="stat-item">
            <div class="stat-number">${friends.size()}</div>
            <div class="stat-label">Friends</div>
        </div>
        <div class="stat-item">
            <div class="stat-number">${pendingRequests.size()}</div>
            <div class="stat-label">Pending Requests</div>
        </div>
    </div>
    <div class="section">
        <h3>Send Friend Request</h3>
        <form action="${pageContext.request.contextPath}/friends/request" method="post" class="form-container">
            <input type="text" name="friendUsername" placeholder="Enter username" required />
            <button type="submit" class="btn-send">Send Request</button>
        </form>
    </div>
    <div class="section">
        <h3>Pending Friend Requests</h3>
        <c:choose>
            <c:when test="${empty pendingRequests}">
                <div class="empty-state">No pending friend requests</div>
            </c:when>
            <c:otherwise>
                <c:forEach var="request" items="${pendingRequests}">
                    <div class="request-item">
                        <div class="friend-info">
                            <div class="friend-name">Request from: ${usernames[request.requesterId]}</div>
                            <div class="friend-date">
                                <fmt:formatDate value="${request.dateRequested}" pattern="MMM dd, yyyy HH:mm" />
                            </div>
                        </div>
                        <div class="action-buttons">
                            <form action="${pageContext.request.contextPath}/friends/accept" method="post" style="display: inline;">
                                <input type="hidden" name="friendshipId" value="${request.friendshipId}" />
                                <button type="submit" class="btn-accept">Accept</button>
                            </form>
                            <form action="${pageContext.request.contextPath}/friends/decline" method="post" style="display: inline;">
                                <input type="hidden" name="friendshipId" value="${request.friendshipId}" />
                                <button type="submit" class="btn-decline">Decline</button>
                            </form>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="section">
        <h3>My Friends</h3>
        <c:choose>
            <c:when test="${empty friends}">
                <div class="empty-state">No friends yet. Send some friend requests to get started!</div>
            </c:when>
            <c:otherwise>
                <c:forEach var="friend" items="${friends}">
                    <div class="friend-item">
                        <div class="friend-info">
                            <div class="friend-name">
                                <c:choose>
                                    <c:when test="${friend.requesterId == sessionScope.user.userId}">
                                        ${usernames[friend.receiverId]}
                                    </c:when>
                                    <c:otherwise>
                                        ${usernames[friend.requesterId]}
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="friend-date">
                                Friends since: <fmt:formatDate value="${friend.dateAccepted}" pattern="MMM dd, yyyy" />
                            </div>
                        </div>
                        <div class="action-buttons">
                            <c:set var="friendId" value="${friend.requesterId == sessionScope.user.userId ? friend.receiverId : friend.requesterId}" />
                            <a href="${pageContext.request.contextPath}/messages?friendUsername=${usernames[friendId]}" class="btn-send">Send Message</a>
                            <form action="${pageContext.request.contextPath}/friends/remove" method="post" style="display:inline; margin-left:8px;">
                                <input type="hidden" name="friendId" value="${friendId}" />
                                <button type="submit" class="btn-decline" onclick="return confirm('Are you sure you want to remove this friend?');">Remove Friend</button>
                            </form>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="section">
        <h3>Quick Links</h3>
        <p>
            <a href="${pageContext.request.contextPath}/messages">View Messages</a> |
            <a href="${pageContext.request.contextPath}/quiz/create">Create Quiz</a> |
            <a href="${pageContext.request.contextPath}/">Home</a>
        </p>
    </div>
</div>
</body>
</html> 