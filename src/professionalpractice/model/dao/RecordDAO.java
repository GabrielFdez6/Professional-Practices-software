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

    public static ArrayList<Record> getRecordsByGroupAndTerm(int idSubjectGroup, int idTerm, Connection connectionBD) throws SQLException {
        ArrayList<Record> records = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        if (connectionBD == null) {
            throw new SQLException("Error: La conexión a la base de datos es nula.");
        }

        try {
            String sql = "SELECT idRecord, idStudent, idSubjectGroup, hoursCount, reportPath, presentationPath, idTerm " +
                    "FROM record " +
                    "WHERE idSubjectGroup = ? AND idTerm = ?";

            statement = connectionBD.prepareStatement(sql);
            statement.setInt(1, idSubjectGroup);
            statement.setInt(2, idTerm);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Record record = new Record();
                record.setIdRecord(resultSet.getInt("idRecord"));
                record.setIdStudent(resultSet.getInt("idStudent"));
                record.setIdSubjectGroup(resultSet.getInt("idSubjectGroup"));
                record.setHoursCount(resultSet.getInt("hoursCount"));
                record.setReportPath(resultSet.getString("reportPath"));
                record.setPresentationPath(resultSet.getString("presentationPath"));
                record.setIdTerm(resultSet.getInt("idTerm"));
                records.add(record);
            }
        } finally {
            if (resultSet != null) { try { resultSet.close(); } catch (SQLException ex) { /* Log error */ } }
            if (statement != null) { try { statement.close(); } catch (SQLException ex) { /* Log error */ } }
        }
        return records;
    }

    public static ArrayList<Record> getRecordsForAssignedStudentsInGroupAndPeriod(int idSubjectGroup, int idTerm, Connection conexionBD) throws SQLException {
        ArrayList<Record> records = new ArrayList<>();
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        if (conexionBD == null) {
            throw new SQLException("Error: La conexión a la base de datos es nula.");
        }

        try {
            // Consulta para obtener los records donde el estudiante está asignado a un proyecto
            String sql = "SELECT r.idRecord, r.idStudent, r.idSubjectGroup, r.hoursCount, " +
                    "r.reportPath, r.presentationPath, r.idTerm " +
                    "FROM record r " +
                    "JOIN student s ON r.idStudent = s.idStudent " + // Unir con la tabla de estudiantes
                    "WHERE r.idSubjectGroup = ? AND r.idTerm = ? AND s.isAssignedToProject = 1"; // <--- CLAVE: s.isAssignedToProject = 1

            sentencia = conexionBD.prepareStatement(sql);
            sentencia.setInt(1, idSubjectGroup);
            sentencia.setInt(2, idTerm);
            resultado = sentencia.executeQuery();

            while (resultado.next()) {
                Record record = new Record();
                record.setIdRecord(resultado.getInt("idRecord"));
                record.setIdStudent(resultado.getInt("idStudent"));
                record.setIdSubjectGroup(resultado.getInt("idSubjectGroup"));
                record.setHoursCount(resultado.getInt("hoursCount"));
                record.setReportPath(resultado.getString("reportPath"));
                record.setPresentationPath(resultado.getString("presentationPath"));
                record.setIdTerm(resultado.getInt("idTerm"));
                records.add(record);
            }
        } finally {
            if (resultado != null) { try { resultado.close(); } catch (SQLException ex) { /* Log error */ } }
            if (sentencia != null) { try { sentencia.close(); } catch (SQLException ex) { /* Log error */ } }
            // NO CERRAR LA CONEXIÓN AQUÍ
        }
        return records;
    }
}