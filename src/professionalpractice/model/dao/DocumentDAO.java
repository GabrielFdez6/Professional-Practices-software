package professionalpractice.model.dao;

import professionalpractice.model.ConectionBD;
import professionalpractice.model.dao.interfaces.IDocumentDAO;
import professionalpractice.model.pojo.FinalDocument;
import professionalpractice.model.pojo.InitialDocument;
import professionalpractice.model.pojo.ReportDocument;

import java.sql.*;
import java.math.BigDecimal;

public class DocumentDAO implements IDocumentDAO {

    @Override
    public int saveInitialDocument(InitialDocument document) throws SQLException {
        String query = "INSERT INTO initialdocument (name, date, delivered, status, filePath, observations, grade) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, document.getName());
            pstmt.setDate(2, new java.sql.Date(document.getDate().getTime()));
            pstmt.setBoolean(3, document.isDelivered());
            pstmt.setString(4, document.getStatus());
            pstmt.setString(5, document.getFilePath());
            pstmt.setString(6, document.getObservations());

            if (document.getGrade() != null) {
                pstmt.setBigDecimal(7, document.getGrade());
            } else {
                pstmt.setNull(7, Types.DECIMAL);
            }

            return executeAndGetId(pstmt);
        }
    }

    @Override
    public int saveReportDocument(ReportDocument document) throws SQLException {
        String query = "INSERT INTO reportdocument (reportedHours, date, grade, name, delivered, status, filePath) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, document.getReportedHours());
            pstmt.setDate(2, new java.sql.Date(document.getDate().getTime()));
            pstmt.setBigDecimal(3, document.getGrade());
            pstmt.setString(4, document.getName());
            pstmt.setBoolean(5, document.isDelivered());
            pstmt.setString(6, document.getStatus());
            pstmt.setString(7, document.getFilePath());

            return executeAndGetId(pstmt);
        }
    }

    @Override
    public int saveFinalDocument(FinalDocument document) throws SQLException {
        String query = "INSERT INTO finaldocument (name, date, delivered, status, filePath, observations, grade) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, document.getName());
            pstmt.setDate(2, new java.sql.Date(document.getDate().getTime()));
            pstmt.setBoolean(3, document.isDelivered());
            pstmt.setString(4, document.getStatus());
            pstmt.setString(5, document.getFilePath());
            pstmt.setString(6, document.getObservations());

            if (document.getGrade() != null) {
                pstmt.setBigDecimal(7, document.getGrade());
            } else {
                pstmt.setNull(7, Types.DECIMAL);
            }

            return executeAndGetId(pstmt);
        }
    }

    private int executeAndGetId(PreparedStatement pstmt) throws SQLException {
        pstmt.executeUpdate();
        try (ResultSet rs = pstmt.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }
}