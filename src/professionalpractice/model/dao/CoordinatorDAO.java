package professionalpractice.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import professionalpractice.model.ConectionBD;
import professionalpractice.model.dao.interfaces.ICoordinatorDAO;
import professionalpractice.model.pojo.Coordinator;

public class CoordinatorDAO implements ICoordinatorDAO {

    @Override
    public Coordinator getCoordinatorByIdUser(int idUser) throws SQLException {
        Coordinator coordinator = null;
        String query = "SELECT p.idCoordinator, p.firstName, p.lastNameFather, p.lastNameMother, p.email, p.idUser " +
                "FROM coordinator p INNER JOIN useraccount u ON p.idUser = u.idUser " +
                "WHERE u.idUser = ? AND u.role = 'COORDINATOR'";

        try (Connection connection = ConectionBD.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, idUser);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    coordinator = new Coordinator();
                    coordinator.setIdCoordinator(resultSet.getInt("idCoordinator"));
                    coordinator.setFirstName(resultSet.getString("firstName"));
                    coordinator.setLastNameFather(resultSet.getString("lastNameFather"));
                    coordinator.setLastNameMother(resultSet.getString("lastNameMother"));
                    coordinator.setEmail(resultSet.getString("email"));
                    coordinator.setIdUser(resultSet.getInt("idUser"));
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error - getCoordinatorByIdUser: " + e.getMessage());
            throw e;
        }

        return coordinator;
    }
}