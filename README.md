# Quiz Website

A full-stack, multi-functional web application for creating, taking, and sharing quizzes. Built with Java Servlets, JSP, and MySQL as a team project.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies](#technologies)
- [Installation](#installation)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Testing](#testing)
- [Team](#team)

## 🎯 Overview

Quiz Website is a comprehensive quiz platform that allows users to create interactive quizzes with various question types, take quizzes in different modes, track achievements, connect with friends, and compete on leaderboards. The platform includes a full admin panel for content and user management.

## ✨ Features

### User Features

#### 🔐 Authentication & Security
- **User Registration & Login**: Secure authentication system with password hashing
- **Password Security**: SHA-1 hashing with optional salt support
- **Session Management**: Secure session handling for user authentication

#### 📝 Quiz Creation
- **Multiple Question Types**:
  - Question-Response
  - Fill-in-the-Blank
  - Multiple-Choice (with custom choices)
  - Picture-Response (with image URLs)
- **Quiz Configuration Options**:
  - Random question order
  - One-page display mode (all questions on single page)
  - Immediate correction mode
  - Practice mode for learning

#### 🎮 Quiz Taking
- **Standard Mode**: Timed quiz taking with score tracking
- **Practice Mode**: Learn without pressure
- **Real-time Feedback**: Immediate correction option
- **Quiz History**: View all past attempts with detailed results

#### 🏆 Achievements System
- Automatic achievement awards for various milestones:
  - First quiz creation
  - Multiple quiz completions
  - Quiz creation milestones (1, 5, 10 quizzes)
  - And more...
- Achievement tracking and display on user profiles

#### 👥 Social Features
- **Friends System**:
  - Send and receive friend requests
  - Accept or decline friend requests
  - View friend list and statistics
  - Remove friends
- **Messaging System**:
  - Internal messaging (notes)
  - Friend request notifications
  - Quiz challenge messages
- **User Profiles**:
  - View your own profile with statistics
  - Browse other users' profiles
  - See achievements and quiz history

#### 📊 Leaderboards
- Quiz-specific leaderboards
- View top performers for each quiz
- Track your ranking

### Admin Features

#### 🔧 Admin Panel
- **Dashboard**: Site-wide statistics and metrics
  - Total users and admins
  - Total quizzes and attempts
  - Recent activity tracking
  - Announcement management overview

#### 📢 Announcement System
- Create, edit, and delete announcements
- Set announcement priorities (high, medium, low)
- Display announcements on homepage

#### 👤 User Management
- View all registered users
- Promote users to admin status
- Delete user accounts
- View user statistics

#### 📚 Quiz Management
- View all quizzes in the system
- Delete quizzes
- View quiz statistics (questions, attempts, creator)

#### 🧹 Data Cleanup
- Bulk delete quiz attempts
- Bulk delete announcements
- Database maintenance tools

## 🛠 Technologies

### Backend
- **Java 24**: Core programming language
- **Jakarta Servlet 6.1.0**: Server-side request handling
- **JSP (JavaServer Pages)**: Dynamic web page generation
- **JSTL 2.0**: Java Standard Tag Library for JSP
- **Maven**: Build automation and dependency management

### Frontend
- **JSP**: Server-side rendering
- **JSTL**: Template engine
- **CSS**: Styling

### Database
- **MySQL 8+**: Relational database management
- **JDBC**: Database connectivity

### Testing
- **JUnit 5.11.0**: Unit testing framework
- Comprehensive test coverage for DAOs, models, and utilities

### Additional Libraries
- **Gson 2.10.1**: JSON processing for question choices
- **Apache Commons Codec 1.16.0**: Password hashing utilities

## 🚀 Installation

### Prerequisites

- **Java 24+** (JDK)
- **Maven 3.6+**
- **MySQL 8+**
- **Tomcat 10+** (or another Servlet 6-compatible container)
- **IDE** (IntelliJ IDEA recommended)

### Step 1: Database Setup

1. Create the database and tables:

```bash
mysql -u root -p < database_schema.sql
```

2. Update database credentials in:
   - `src/main/java/util/DbUtil.java`
   - `src/main/java/dao/DatabaseConnection.java`
   
   Default credentials: `root`/`root` (change as needed)

### Step 2: Build the Project

**Linux/Mac:**
```bash
./mvnw clean package
```

**Windows:**
```bash
mvnw.cmd clean package
```

This will create a WAR file in the `target/` directory.

### Step 3: Deploy

#### Option A: Using IntelliJ IDEA
1. Configure Tomcat server in IntelliJ IDEA
2. Add the project as an artifact
3. Run the application

#### Option B: Manual Deployment
1. Copy the generated WAR file to Tomcat's `webapps/` directory
2. Start Tomcat server
3. Access the application at `http://localhost:8080/`

### Step 4: Access the Application

Open your browser and navigate to:
```
http://localhost:8080/
```

## 📖 Usage

### Getting Started

1. **Register**: Create a new account
2. **Login**: Sign in with your credentials
3. **Explore**: Browse available quizzes or create your own

### Creating a Quiz

1. Navigate to "Create Quiz"
2. Enter quiz title and description
3. Configure quiz settings (random order, one-page, immediate correction)
4. Add questions one by one:
   - Select question type
   - Enter question text
   - Provide correct answer
   - For multiple-choice: add choices
   - For picture-response: add image URL
5. Finish and save the quiz

### Taking a Quiz

1. Browse quizzes from the main page
2. Select a quiz to take
3. Answer questions (format depends on quiz settings)
4. Submit and view results
5. Check your score and see correct answers

### Social Features

- **Add Friends**: Search for users and send friend requests
- **Messages**: Send messages to friends or respond to requests
- **View Profiles**: Check out other users' achievements and statistics
- **Leaderboards**: See top performers for each quiz

### Admin Access

1. Login with an admin account
2. Navigate to `/admin` (not publicly linked for security)
3. Access admin dashboard for:
   - User management
   - Quiz management
   - Announcement creation
   - Data cleanup

## 📁 Project Structure

```
quiz-website/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── controller/      # Servlet controllers (MVC)
│   │   │   ├── dao/             # Data Access Objects
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── model/            # Domain models
│   │   │   ├── listener/         # Servlet context listeners
│   │   │   └── util/             # Utility classes
│   │   └── webapp/
│   │       ├── jsp/              # JSP view files
│   │       │   ├── admin/        # Admin panel pages
│   │       │   └── ...           # User pages
│   │       ├── WEB-INF/
│   │       │   └── web.xml       # Web application configuration
│   │       └── index.jsp         # Homepage
│   └── test/
│       └── java/                 # JUnit test files
│           ├── dao/               # DAO tests
│           ├── dto/               # DTO tests
│           ├── model/             # Model tests
│           └── util/              # Utility tests
├── database_schema.sql           # Database schema
├── pom.xml                       # Maven configuration
├── mvnw                          # Maven wrapper (Unix)
├── mvnw.cmd                      # Maven wrapper (Windows)
└── README.md                     # This file
```

### Key Components

- **Controllers**: Handle HTTP requests and route to appropriate views
- **DAOs**: Database operations and queries
- **DTOs**: Data transfer between layers
- **Models**: Business logic and domain entities
- **Listeners**: Application initialization (database connection, DAO setup)
- **Utils**: Helper classes (password hashing, database utilities)

## 🗄 Database Schema

The application uses the following main tables:

- **users**: User accounts and authentication
- **quizzes**: Quiz metadata and settings
- **questions**: Quiz questions with various types
- **quiz_attempts**: User quiz attempts and scores
- **friendships**: Friend relationships and requests
- **messages**: Internal messaging system
- **achievements**: User achievement tracking
- **announcements**: Admin announcements

For the complete schema, see `database_schema.sql`.

## 🧪 Testing

Run all unit tests:

```bash
./mvnw test
```

or on Windows:

```bash
mvnw.cmd test
```

### Test Coverage

- **DAO Tests**: All database operations
- **Model Tests**: Business logic validation
- **DTO Tests**: Data transfer validation
- **Utility Tests**: Helper function verification

Tests use JUnit 5 and follow best practices with proper setup/teardown.

## 👨‍💻 Team

This project was developed as a team effort for the FreeUni OOP Final Project.

**Team Members:**
- ნიკა სადღობელაშვილი (Nika Sadgobelashvili)
- ირაკლი ბერელიძე (Irakli Berelidze)
- გიორგი სულაქველიძე (Giorgi Sulakvelidze)
- კონსტანტინე ბახუტაშვილი (Konstantin Bakhtutashvili)

**Team Name:** hmm

## 📝 License

This project was created as an academic project for FreeUni.

## 🔗 Additional Resources

- See `README.txt` for detailed Georgian documentation
- See `Example-Quiz-Website.pdf` for project specification

---

**Note**: This is a full-stack web application demonstrating Java EE technologies, MVC architecture, and database integration. Suitable for learning and portfolio purposes.
