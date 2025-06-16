// Ubicación: src/professionalpractice/model/dao/UserAccountDAO.java
package professionalpractice.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import professionalpractice.model.ConectionBD;
import professionalpractice.model.dao.interfaces.IUserAccountDAO;
import professionalpractice.model.pojo.UserAccount;

public class UserAccountDAO implements IUserAccountDAO {

    public UserAccount getUserByUsername(String username) throws SQLException {
        UserAccount user = null;
        String query = "SELECT idUser, username, password, role FROM UserAccount WHERE username = ?";

        // Usar la conexión especial de login
        try (Connection connection = ConectionBD.getLoginConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = new UserAccount();
                    user.setUserId(resultSet.getInt("idUser"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
                    user.setRole(resultSet.getString("role"));
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error - getUserByUsername: " + e.getMessage());
            throw e;
        }
        return user;
    }
}