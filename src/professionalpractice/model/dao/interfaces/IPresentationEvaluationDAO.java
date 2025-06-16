package professionalpractice.model.dao.interfaces;

import professionalpractice.model.pojo.PresentationEvaluation;
import java.sql.SQLException;

public interface IPresentationEvaluationDAO {
    int saveEvaluation(PresentationEvaluation evaluation) throws SQLException;
}