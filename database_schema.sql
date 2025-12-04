-- Quiz Website Database Schema
-- Execute this script to create the database structure

-- Create database
CREATE DATABASE IF NOT EXISTS quiz_website;
USE quiz_website;

-- Users table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_admin BOOLEAN DEFAULT FALSE,
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- Quizzes table
CREATE TABLE quizzes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    creator_id INT NOT NULL,
    random_order BOOLEAN DEFAULT FALSE,
    one_page BOOLEAN DEFAULT TRUE,
    immediate_correction BOOLEAN DEFAULT FALSE,
    practice_mode BOOLEAN DEFAULT FALSE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_creator_id (creator_id),
    INDEX idx_created_date (created_date)
);

-- Questions table
CREATE TABLE questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id INT NOT NULL,
    question_type ENUM('question-response', 'fill-in-blank', 'multiple-choice', 'picture-response') NOT NULL,
    question_text TEXT NOT NULL,
    correct_answer TEXT NOT NULL,
    choices_json JSON,
    image_url VARCHAR(500),
    order_num INT NOT NULL,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    INDEX idx_quiz_id (quiz_id),
    INDEX idx_order_num (order_num)
);

-- Quiz attempts table
CREATE TABLE quiz_attempts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    quiz_id INT NOT NULL,
    score DECIMAL(5,2) NOT NULL,
    total_questions INT NOT NULL,
    time_taken BIGINT NOT NULL, -- Time in seconds
    date_taken TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_practice BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_quiz_id (quiz_id),
    INDEX idx_date_taken (date_taken),
    INDEX idx_score (score)
);

-- Friendships table
CREATE TABLE friendships (
    id INT AUTO_INCREMENT PRIMARY KEY,
    requester_id INT NOT NULL,
    receiver_id INT NOT NULL,
    status ENUM('pending', 'accepted', 'declined', 'blocked') DEFAULT 'pending',
    date_requested TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_accepted TIMESTAMP NULL,
    FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_friendship (requester_id, receiver_id),
    INDEX idx_requester_id (requester_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_status (status)
);

-- Messages table
CREATE TABLE messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    message_type ENUM('friend_request', 'challenge', 'note') NOT NULL,
    content TEXT NOT NULL,
    quiz_id INT NULL, -- Only for challenge messages
    date_sent TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE SET NULL,
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_date_sent (date_sent),
    INDEX idx_is_read (is_read)
);

-- Achievements table
CREATE TABLE achievements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    achievement_type ENUM('amateur_author', 'prolific_author', 'prodigious_author', 'quiz_machine', 'i_am_the_greatest', 'practice_makes_perfect') NOT NULL,
    date_earned TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_achievement (user_id, achievement_type),
    INDEX idx_user_id (user_id),
    INDEX idx_achievement_type (achievement_type)
);

-- Announcements table
CREATE TABLE announcements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_by INT NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    priority ENUM('low', 'medium', 'high') DEFAULT 'medium',
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_created_date (created_date),
    INDEX idx_is_active (is_active),
    INDEX idx_priority (priority),
    INDEX idx_created_by (created_by)
);

-- Insert sample admin user
-- Password: shasum gives d033e22ae348aeb5660fc2140aec35850c4da997
INSERT INTO users (username, password_hash, email, is_admin) VALUES
('admin', 'd033e22ae348aeb5660fc2140aec35850c4da997', 'admin@quiz.com', TRUE);


COMMIT; 