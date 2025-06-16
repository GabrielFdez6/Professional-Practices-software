package professionalpractice.model.dao.interfaces;

import professionalpractice.model.pojo.Record;
import java.sql.SQLException;

public interface IRecordDAO {
    Record getRecordByStudentId(int idStudent) throws SQLException;
}