package professionalpractice.model.dao.interfaces;

import professionalpractice.model.pojo.Record;
import java.sql.SQLException;
import java.util.List;

public interface IRecordDAO {
    Record getRecordByStudentId(int idStudent) throws SQLException;
    List<Integer> getAllActiveRecordIds() throws SQLException;
}