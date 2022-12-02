package pl.coderslab.entity;

import org.mindrot.jbcrypt.BCrypt;
import pl.coderslab.DbUtil;

import java.sql.*;
import java.util.ArrayList;

public class UserDao {
    private static final String CREATE_USER_QUERY =
            "INSERT INTO users(username, email, password) VALUES (?, ?, ?);";

    private static final String UPDATE_USER_QUERY =
            "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?;";

    private static final String SELECT_USER_BY_ID_QUERY =
            "SELECT id AS id, username AS u, email AS e, password AS p FROM users WHERE id = ?;";

    private static final String DELETE_USER_BY_ID_QUERY =
            "DELETE FROM users WHERE id = ?;";

    private static final String SELECT_ALL_USERS_QUERY =
            "SELECT * FROM users;";

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public User create(User user) {
        try (Connection conn = DbUtil.connect()) {
            PreparedStatement prepInsertStatement =
                    conn.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
//            INSERT INTO users(username, email, password) VALUES (?, ?, ?);
            prepInsertStatement.setString(1, user.getUserName());
            prepInsertStatement.setString(2, user.getEmail());
            prepInsertStatement.setString(3, hashPassword(user.getPassword()));
            prepInsertStatement.executeUpdate();

            ResultSet resultSet = prepInsertStatement.getGeneratedKeys();
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
            PreparedStatement prepSelectStatement = conn.prepareStatement(SELECT_USER_BY_ID_QUERY);
//            SELECT id AS id, username AS u, email AS e, password AS p FROM users WHERE id = ?;
            prepSelectStatement.setString(1, String.valueOf(userId));
            prepSelectStatement.executeQuery();
            ResultSet resultSet = prepSelectStatement.getResultSet();

            String userName = "";
            String userEmail = "";
            String userPass = "";
            while (resultSet.next()) {
                if (resultSet.getString("id").isEmpty()) {
                    break;
                } else {
                    userName = resultSet.getString("u");
                    userEmail = resultSet.getString("e");
                    userPass = resultSet.getString("p");
                    User user = new User(userName, userEmail, userPass);
                    user.setId(userId);
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public void update(User user) {
        try (Connection conn = DbUtil.connect()) {
            PreparedStatement prepUpdateStatement = conn.prepareStatement(UPDATE_USER_QUERY);
//            UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?;
            prepUpdateStatement.setString(1, user.getUserName());
            prepUpdateStatement.setString(2, user.getEmail());
            prepUpdateStatement.setString(3, hashPassword(user.getPassword()));
            prepUpdateStatement.setString(4, String.valueOf(user.getId()));
            prepUpdateStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(int userId) {
        int sumIdBeforeDelete = idSumVerification();
        try (Connection conn = DbUtil.connect()) {
            PreparedStatement prepDeleteStatement = conn.prepareStatement(DELETE_USER_BY_ID_QUERY);
//            DELETE FROM users WHERE id = ?;
            prepDeleteStatement.setString(1, String.valueOf(userId));
            prepDeleteStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        int sumIdAfterDelete = idSumVerification();
        System.out.println(
                sumIdBeforeDelete == sumIdAfterDelete ? "No user with such ID" : "User deleted"
        );
    }

    private int idSumVerification() {
        int sumId = 0;
        try (Connection conn = DbUtil.connect()) {
            PreparedStatement prepIdSumStatement =
                    conn.prepareStatement("select sum(id) from users;");
            prepIdSumStatement.executeQuery();
            while (prepIdSumStatement.getResultSet().next()) {
                sumId += prepIdSumStatement.getResultSet().getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sumId;
    }

    public ArrayList<User> findAll() {
        ArrayList<User> users = new ArrayList<>();
        try (Connection conn = DbUtil.connect()) {
            PreparedStatement prepSelectAllStatement = conn.prepareStatement(SELECT_ALL_USERS_QUERY);
//            SELECT * FROM users;
            prepSelectAllStatement.executeQuery();
            ResultSet allUsers = prepSelectAllStatement.getResultSet();

            while (allUsers.next()) {
                User user = new User();
                user.setId(allUsers.getInt(1));
                user.setEmail(allUsers.getString(2));
                user.setUserName(allUsers.getString(3));
                user.setPassword(allUsers.getString(4));
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return users;
    }
}