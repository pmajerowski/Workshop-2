package pl.coderslab.entity;

import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.DbUtil;

import java.sql.*;

public class UserDao {
    private static final String CREATE_USER_QUERY =
            "INSERT INTO users(username, email, password) VALUES (?, ?, ?);";

    private static final String UPDATE_USERNAME_QUERY =
            "UPDATE users SET username = ? WHERE id = ?;";

    private static final String UPDATE_EMAIL_QUERY =
            "UPDATE users SET email = ? WHERE id = ?;";

    private static final String UPDATE_PASSWORD_QUERY =
            "UPDATE users SET password = ? WHERE id = ?;";

    private static final String SELECT_USER_BY_ID_QUERY =
            "SELECT id AS id, username AS u, email AS e, password AS p FROM users WHERE id = ?;";

    private static final String DELETE_USER_BY_ID_QUERY =
            "DELETE FROM users WHERE id = ?;";

    private static final String SELECT_ALL_USERS_QUERY =
            "SELECT * FROM users;";

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }


    public User create(User user) {
        try (Connection conn = DbUtil.connect()) {
            PreparedStatement prepStatement =
                    conn.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
//            INSERT INTO users(username, email, password) VALUES (?, ?, ?);
            prepStatement.setString(1, user.getUserName());
            prepStatement.setString(2, user.getEmail());
            prepStatement.setString(3, hashPassword(user.getPassword()));
            prepStatement.executeUpdate();

            ResultSet resultSet = prepStatement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User read(int userId) {
        try (Connection conn = DbUtil.connect()) {
            PreparedStatement prepStatement = conn.prepareStatement(SELECT_USER_BY_ID_QUERY);
//            SELECT id AS id, username AS u, email AS e, password AS p FROM users WHERE id = ?;
            prepStatement.setString(1, String.valueOf(userId));
            prepStatement.executeQuery();
            ResultSet resultSet = prepStatement.getResultSet();

            String userName = "";
            String userEmail = "";
            String userPass = "";
            while (resultSet.next()) {
                if(resultSet.getString("id").isEmpty()) {
                    return null;
                } else {
                    userName = resultSet.getString("u");
                    userEmail = resultSet.getString("e");
                    userPass = resultSet.getString("p");
                }
            }

            User user = new User(userName, userEmail, userPass);
            user.setId(userId);
            return user;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}