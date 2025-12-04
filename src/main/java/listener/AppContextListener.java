package listener;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.sql.Connection;

import jakarta.servlet.annotation.WebListener;
import util.DbUtil;
import dao.UserDAO;
import dao.QuizDAO;
import dao.QuestionDAO;
import dao.QuizAttemptDAO;
import dao.FriendshipDAO;
import dao.MessageDAO;
import dao.AchievementDAO;
import dao.AnnouncementDAO;

@WebListener
public class AppContextListener implements ServletContextListener
{
    private Connection _connection;

    @Override
    public void contextInitialized(ServletContextEvent e) {
        try
        {
            System.out.println("Initializing DB connection...");
            _connection = DbUtil.getConnection();
            //Adding Connection in context
            e.getServletContext().setAttribute("DBConnection", _connection);

            //Adding DAO In context
            UserDAO userDAO = new UserDAO(_connection);
            QuizDAO quizDAO = new QuizDAO(_connection);
            QuestionDAO questionDAO = new QuestionDAO(_connection);
            QuizAttemptDAO quizAttemptDAO = new QuizAttemptDAO(_connection);
            FriendshipDAO friendshipDAO = new FriendshipDAO(_connection);
            MessageDAO messageDAO = new MessageDAO(_connection);
            AchievementDAO achievementDAO = new AchievementDAO(_connection);
            AnnouncementDAO announcementDAO = new AnnouncementDAO(_connection);

            e.getServletContext().setAttribute("userDAO", userDAO);
            e.getServletContext().setAttribute("quizDAO", quizDAO);
            e.getServletContext().setAttribute("questionDAO", questionDAO);
            e.getServletContext().setAttribute("quizAttemptDAO", quizAttemptDAO);
            e.getServletContext().setAttribute("friendshipDAO", friendshipDAO);
            e.getServletContext().setAttribute("messageDAO", messageDAO);
            e.getServletContext().setAttribute("achievementDAO", achievementDAO);
            e.getServletContext().setAttribute("announcementDAO", announcementDAO);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("Failed to connect to DB");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent e)
    {
        try
        {
            System.out.println("Closing DB connection...");
            _connection.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
