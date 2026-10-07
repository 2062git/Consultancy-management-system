
        package np.edu.cms.dao;

import np.edu.cms.config.DatabaseConnection;
import np.edu.cms.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {

    public User authenticate(String username, String password)
            throws SQLException {

        String sql = """
            SELECT id, username, full_name, password, role
            FROM users
            WHERE username = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    String storedPassword = result.getString("password");

                    if (BCrypt.checkpw(password, storedPassword)) {

                        return new User(
                                result.getInt("id"),
                                result.getString("username"),
                                result.getString("full_name"),
                                result.getString("role")
                        );
                    }
                }
            }
        }

        return null;
    }
}
