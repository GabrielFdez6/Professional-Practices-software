package professionalpractice.model.dao;

import professionalpractice.model.ConectionBD;
import professionalpractice.model.dao.interfaces.IRecordDAO;
import professionalpractice.model.pojo.Record;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecordDAO implements IRecordDAO {

    @Override
    public Record getRecordByStudentId(int idStudent) throws SQLException {
        Record record = null;
        String query = "SELECT idRecord, idStudent, idSubjectGroup, hoursCount, idTerm FROM record WHERE idStudent = ?";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idStudent);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    record = new Record();
                    record.setIdRecord(rs.getInt("idRecord"));
                    record.setIdStudent(rs.getInt("idStudent"));
                    record.setIdSubjectGroup(rs.getInt("idSubjectGroup"));
                    record.setHoursCount(rs.getInt("hoursCount"));
                    record.setIdTerm(rs.getInt("idTerm"));
                }
            }
        }
        return record;
    }
    @Override
    public List<Integer> getAllActiveRecordIds() throws SQLException {
        List<Integer> recordIds = new ArrayList<>();
        String query = "SELECT idRecord FROM record";
        Connection conn = ConectionBD.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                recordIds.add(rs.getInt("idRecord"));
            }
        }
        return recordIds;
    }
}