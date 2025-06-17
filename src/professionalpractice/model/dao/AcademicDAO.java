package professionalpractice.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import professionalpractice.model.ConectionBD;
import professionalpractice.model.dao.interfaces.IAcademicDAO;
import professionalpractice.model.pojo.Academic;

public class AcademicDAO implements IAcademicDAO {

    @Override
    public Academic getAcademicByUserId(int userId) throws SQLException {
        Academic academic = null;
        String query = "SELECT idAcademic, idSubjectGroup, firstName, lastNameFather, lastNameMother, email, status, idUser " +
                "FROM academic WHERE idUser = ?";

        try (Connection connection = ConectionBD.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    academic = new Academic();
                    academic.setIdAcademic(resultSet.getInt("idAcademic"));
                    academic.setIdSubjectGroup(resultSet.getInt("idSubjectGroup"));
                    academic.setFirstName(resultSet.getString("firstName"));
                    academic.setLastNameFather(resultSet.getString("lastNameFather"));
                    academic.setLastNameMother(resultSet.getString("lastNameMother"));
                    academic.setEmail(resultSet.getString("email"));
                    academic.setStatus(resultSet.getBoolean("status"));
                    academic.setIdUser(resultSet.getInt("idUser"));
                }
            }
        } catch (SQLException e) {
            System.err.println("DAO Error - getAcademicByUserId: " + e.getMessage());
            throw e;
        }
        return academic;
    }
}