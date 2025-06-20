package professionalpractice.model.dao;

import java.sql.*;

import professionalpractice.model.ConectionBD;
import professionalpractice.model.dao.interfaces.IPresentationEvaluationDAO;
import professionalpractice.model.pojo.PresentationEvaluation;
import professionalpractice.utils.Constants;

public class PresentationEvaluationDAO implements IPresentationEvaluationDAO {

    @Override
    public int saveEvaluation(PresentationEvaluation evaluation) throws SQLException {
        String query = "INSERT INTO presentationevaluation (date, grade, observations, idRecord) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, evaluation.getDate());
            ps.setBigDecimal(2, evaluation.getGrade());
            ps.setString(3, evaluation.getObservations());
            ps.setInt(4, evaluation.getIdRecord());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }
}
