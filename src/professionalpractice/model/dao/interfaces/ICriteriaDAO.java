package professionalpractice.model.dao.interfaces;

import professionalpractice.model.pojo.Criteria;
import java.sql.SQLException;
import java.util.List;

public interface ICriteriaDAO {
    List<Criteria> getAllCriteria() throws SQLException;
}