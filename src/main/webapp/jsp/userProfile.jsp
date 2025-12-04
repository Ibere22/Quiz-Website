<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User, model.Achievement, java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${userInfo == null || quizCount == null || avgScore == null || achievements == null}">
    <div style="background: linear-gradient(135deg, #ff6b6b, #ee5a24); color: white; padding: 18px; border-radius: 12px; margin: 30px auto; max-width: 600px; text-align: center; font-weight: bold;">
        Error: Required profile data is missing. Please access this page via the correct link or try again later.
    </div>
    <c:remove var="userInfo" />
    <c:remove var="quizCount" />
    <c:remove var="avgScore" />
    <c:remove var="achievements" />
    <c:remove var="isOwnProfile" />
</c:if>
<%
    String[] allAchievements = {
            "amateur_author",
            "prolific_author",
            "prodigious_author",
            "practice_makes_perfect",
            "i_am_the_greatest",
            "quiz_machine"
    };
    String[] achievementNames = {
            "Amateur Author",
            "Prolific Author",
            "Prodigious Author",
            "Practice Makes Perfect",
            "I am the Greatest",
            "Quiz Machine"
    };
    String[] achievementDescs = {
            "Created your first quiz!",
            "Created 5 quizzes!",
            "Created 10 quizzes!",
            "Took a quiz in practice mode!",
            "Achieved the highest score on a quiz!",
            "Took 10 quizzes!"
    };
    pageContext.setAttribute("allAchievements", allAchievements);
    pageContext.setAttribute("achievementNames", achievementNames);
    pageContext.setAttribute("achievementDescs", achievementDescs);
%>
<!DOCTYPE html>
<html>
<head>
    <title>User Profile - Quiz Website</title>
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
            line-height: 1.6;
            color: #2c3e50;
        }

        /* =========================== NAVIGATION =========================== */
        .nav-back {
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

        .nav-back:hover {
            background: rgba(255, 255, 255, 0.25);
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
            text-decoration: none;
            color: white;
        }

        /* =========================== MAIN CONTAINER =========================== */
        .main-container {
            max-width: 900px;
            margin: 80px auto 40px;
            padding: 0 40px;
            position: relative;
            z-index: 3;
        }

        /* =========================== PROFILE CARD =========================== */
        .profile-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 25px 80px rgba(0, 0, 0, 0.15);
            overflow: hidden;
            margin-bottom: 30px;
            position: relative;
        }

        /* Decorative corner for profile card */
        .profile-card::before {
            content: '';
            position: absolute;
            top: 0;
            right: 0;
            width: 100px;
            height: 100px;
            background: linear-gradient(135deg, transparent 50%, rgba(102, 126, 234, 0.1) 50%);
            border-radius: 0 20px 0 100px;
            pointer-events: none;
        }

        .profile-header {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            padding: 40px;
            text-align: center;
        }

        .profile-avatar {
            font-size: 4rem;
            margin-bottom: 20px;
        }

        .profile-title {
            font-size: 2rem;
            font-weight: 700;
            margin-bottom: 10px;
        }

        .profile-subtitle {
            font-size: 1.1rem;
            opacity: 0.9;
        }

        .profile-body {
            padding: 40px;
        }

        /* =========================== FRIEND REQUEST SECTION =========================== */
        .friend-request-section {
            background: white;
            border-radius: 20px;
            box-shadow: 0 25px 80px rgba(0, 0, 0, 0.15);
            overflow: hidden;
            margin-bottom: 30px;
        }

        .friend-request-header {
            background: linear-gradient(135deg, #28a745, #20c997);
            color: white;
            padding: 30px 40px;
            display: flex;
            align-items: center;
            gap: 20px;
        }

        .friend-request-icon {
            font-size: 2rem;
        }

        .friend-request-title {
            font-size: 1.5rem;
            font-weight: 600;
        }

        .friend-request-body {
            padding: 40px;
        }

        .friend-request-btn {
            width: 100%;
            padding: 16px;
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            border: none;
            border-radius: 12px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .friend-request-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 35px rgba(40, 167, 69, 0.4);
        }

        .friend-request-btn:active {
            transform: translateY(0);
        }

        /* =========================== PROFILE SECTIONS =========================== */
        .profile-section {
            margin-bottom: 35px;
            padding: 25px;
            background: #f8f9fa;
            border-radius: 15px;
            border-left: 4px solid #667eea;
            transition: all 0.3s ease;
        }

        .profile-section:hover {
            transform: translateX(5px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
        }

        .profile-section-title {
            font-size: 1.3rem;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .profile-section-icon {
            font-size: 1.5rem;
        }

        .profile-info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
        }

        .profile-info-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 15px 20px;
            background: white;
            border-radius: 10px;
            border: 1px solid #e9ecef;
            transition: all 0.3s ease;
        }

        .profile-info-item:hover {
            border-color: #667eea;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.1);
        }

        .profile-label {
            font-weight: 600;
            color: #34495e;
        }

        .profile-value {
            color: #2c3e50;
            font-weight: 500;
        }

        /* =========================== ACHIEVEMENTS SECTION =========================== */
        .achievements-section {
            margin-bottom: 35px;
        }

        .achievements-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .achievements-count {
            background: linear-gradient(135deg, #667eea, #764ba2);
            color: white;
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 0.9rem;
            font-weight: 600;
        }

        .achievements-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 15px;
        }

        .achievement-item {
            display: flex;
            align-items: center;
            gap: 15px;
            padding: 20px;
            background: white;
            border-radius: 12px;
            border: 2px solid #e9ecef;
            transition: all 0.3s ease;
        }

        .achievement-item:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
        }

        .achievement-item.unlocked {
            border-color: #28a745;
            background: linear-gradient(135deg, #f8fff9, #f0fff4);
        }

        .achievement-item.locked {
            border-color: #e9ecef;
            background: #f8f9fa;
            opacity: 0.7;
        }

        .achievement-icon {
            font-size: 2rem;
            min-width: 40px;
            text-align: center;
        }

        .achievement-content {
            flex: 1;
        }

        .achievement-name {
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 5px;
        }

        .achievement-description {
            color: #7f8c8d;
            font-size: 0.9rem;
        }

        /* =========================== ALERT STYLES =========================== */
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

        .alert-error {
            background: linear-gradient(135deg, #ff6b6b, #ee5a24);
            color: white;
            box-shadow: 0 6px 20px rgba(255, 107, 107, 0.3);
        }

        /* =========================== RESPONSIVE DESIGN =========================== */
        @media (max-width: 768px) {
            .main-container {
                margin: 60px auto 20px;
                padding: 0 20px;
            }

            .nav-back {
                position: relative;
                top: auto;
                left: auto;
                margin-bottom: 20px;
                display: inline-block;
            }

            .profile-header {
                padding: 30px 20px;
            }

            .profile-title {
                font-size: 1.5rem;
            }

            .profile-body {
                padding: 30px 20px;
            }

            .profile-info-grid {
                grid-template-columns: 1fr;
            }

            .achievements-grid {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 480px) {
            .main-container {
                margin: 40px auto 20px;
                padding: 0 15px;
            }

            .profile-header {
                padding: 25px 15px;
            }

            .profile-body {
                padding: 25px 15px;
            }

            .profile-section {
                padding: 20px 15px;
            }
        }
    </style>
</head>
<body>
<c:if test="${not empty quizId}">
    <a href="${pageContext.request.contextPath}/quiz-summery?quizId=${quizId}" class="nav-back">← Back to Rankings</a>
</c:if>

<div class="main-container">
    <!-- Friend Request Section (if not own profile) -->
    <c:if test="${not isOwnProfile}">
        <div class="friend-request-section">
            <div class="friend-request-header">
                <div class="friend-request-icon">👥</div>
                <div class="friend-request-title">Send Friend Request</div>
            </div>

            <div class="friend-request-body">
                <c:if test="${not empty success}">
                    <div class="alert alert-success">${success}</div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-error">${error}</div>
                </c:if>

                <form action="${pageContext.request.contextPath}/friends/request" method="post">
                    <input type="hidden" name="friendUsername" value="${friendUsername != null ? friendUsername : userInfo.username}" />
                    <input type="hidden" name="returnToProfile" value="true" />
                    <c:if test="${not empty quizId}">
                        <input type="hidden" name="quizId" value="${quizId}" />
                    </c:if>
                    <button type="submit" class="friend-request-btn">Send Friend Request</button>
                </form>
            </div>
        </div>
    </c:if>

    <!-- Profile Card -->
    <div class="profile-card">
        <div class="profile-header">
            <div class="profile-avatar">👤</div>
            <h1 class="profile-title">User Profile</h1>
            <p class="profile-subtitle">${userInfo.username}'s profile and achievements</p>
        </div>

        <div class="profile-body">
            <!-- User Information Section -->
            <div class="profile-section">
                <div class="profile-section-title">
                    <span class="profile-section-icon">📋</span>
                    Account Information
                </div>
                <div class="profile-info-grid">
                    <div class="profile-info-item">
                        <span class="profile-label">Username:</span>
                        <span class="profile-value">${userInfo.username}</span>
                    </div>
                    <div class="profile-info-item">
                        <span class="profile-label">Registered on:</span>
                        <span class="profile-value">${userInfo.createdDate}</span>
                    </div>
                </div>
            </div>

            <!-- Quiz Statistics Section -->
            <div class="profile-section">
                <div class="profile-section-title">
                    <span class="profile-section-icon">📊</span>
                    Quiz Statistics
                </div>
                <div class="profile-info-grid">
                    <div class="profile-info-item">
                        <span class="profile-label">Quizzes Taken:</span>
                        <span class="profile-value">${quizCount}</span>
                    </div>
                    <div class="profile-info-item">
                        <span class="profile-label">Average Score:</span>
                        <span class="profile-value">${avgScore}</span>
                    </div>
                </div>
            </div>

            <!-- Achievements Section -->
            <div class="profile-section achievements-section">
                <div class="profile-section-title">
                    <span class="profile-section-icon">🏆</span>
                    Achievements
                </div>

                <div class="achievements-header">
                    <div class="achievements-count">
                        <c:set var="unlocked" value="${fn:length(achievements)}" />
                        ${unlocked}/6 unlocked
                    </div>
                </div>

                <div class="achievements-grid">
                    <c:forEach var="achType" items="${allAchievements}" varStatus="status">
                        <c:set var="hasIt" value="false" />
                        <c:forEach var="ach" items="${achievements}">
                            <c:if test="${ach.achievementType == achType}">
                                <c:set var="hasIt" value="true" />
                            </c:if>
                        </c:forEach>
                        <div class="achievement-item ${hasIt ? 'unlocked' : 'locked'}">
                            <div class="achievement-icon">
                                <c:choose>
                                    <c:when test="${hasIt}">✅</c:when>
                                    <c:otherwise>🔒</c:otherwise>
                                </c:choose>
                            </div>
                            <div class="achievement-content">
                                <div class="achievement-name">${achievementNames[status.index]}</div>
                                <div class="achievement-description">${achievementDescs[status.index]}</div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html> 