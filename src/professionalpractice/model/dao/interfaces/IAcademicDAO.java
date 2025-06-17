package professionalpractice.model.dao.interfaces;

import java.sql.SQLException;
import professionalpractice.model.pojo.Academic;

public interface IAcademicDAO {

    public Academic getAcademicByUserId(int userId) throws SQLException;

}