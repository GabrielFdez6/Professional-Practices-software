package professionalpractice.model.dao;

import professionalpractice.model.ConectionBD;
import professionalpractice.model.dao.interfaces.IEvaluationDetailDAO;
import professionalpractice.model.pojo.EvaluationDetail;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class EvaluationDetailDAO implements IEvaluationDetailDAO {
    @Override
    public boolean saveEvaluationDetails(List<EvaluationDetail> details) throws SQLException {
        String query = "INSERT INTO evaluationdetail (idEvaluation, idCriteria, grade) VALUES (?, ?, ?)";
        try (Connection conn = ConectionBD.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                for (EvaluationDetail detail : details) {
                    pstmt.setInt(1, detail.getIdEvaluation());
                    pstmt.setInt(2, detail.getIdCriteria());
                    pstmt.setFloat(3, detail.getGrade());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}