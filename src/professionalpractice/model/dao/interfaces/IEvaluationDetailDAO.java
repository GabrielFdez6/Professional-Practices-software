package professionalpractice.model.dao.interfaces;

import professionalpractice.model.pojo.EvaluationDetail;
import java.sql.SQLException;
import java.util.List;

public interface IEvaluationDetailDAO {
    boolean saveEvaluationDetails(List<EvaluationDetail> details) throws SQLException;
}