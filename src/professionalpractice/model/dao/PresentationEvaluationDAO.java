package professionalpractice.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import professionalpractice.model.ConectionBD;
import professionalpractice.model.dao.interfaces.IPresentationEvaluationDAO;
import professionalpractice.model.pojo.PresentationEvaluation;
import professionalpractice.utils.Constants;

public class PresentationEvaluationDAO implements IPresentationEvaluationDAO {

    @Override
    public int saveEvaluation(PresentationEvaluation evaluation) throws SQLException {
        String query = "INSERT INTO presentationevaluation (title, date, grade, observations, idRecord) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, evaluation.getTitle());
            ps.setDate(2, evaluation.getDate());
            ps.setBigDecimal(3, evaluation.getGrade());
            ps.setString(4, evaluation.getObservations());
            ps.setInt(5, evaluation.getIdRecord());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return Constants.OPERATION_SUCCESFUL;
            }
        }
        return Constants.CONNECTION_FAILED;
    }
}