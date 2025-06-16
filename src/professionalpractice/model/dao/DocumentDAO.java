package professionalpractice.model.dao;

import professionalpractice.model.ConectionBD;
import professionalpractice.model.dao.interfaces.IDocumentDAO;
import professionalpractice.model.pojo.FinalDocument;
import professionalpractice.model.pojo.InitialDocument;
import professionalpractice.model.pojo.ReportDocument;

import java.sql.*;

public class DocumentDAO implements IDocumentDAO {

    @Override
    public int saveInitialDocument(InitialDocument document) throws SQLException {
        String query = "INSERT INTO initialdocument (name, date, delivered, status, filePath, observations, grade) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return saveDocument(query, document.getName(), document.getFilePath());
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

            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    @Override
    public int saveFinalDocument(FinalDocument document) throws SQLException {
        String query = "INSERT INTO finaldocument (name, date, delivered, status, filePath, observations, grade) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return saveDocument(query, document.getName(), document.getFilePath());
    }

    private int saveDocument(String query, String name, String filePath) throws SQLException {
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setDate(2, new java.sql.Date(System.currentTimeMillis())); // Current date
            pstmt.setBoolean(3, true); // Delivered
            pstmt.setString(4, "EN_REVISION"); // Initial status
            pstmt.setString(5, filePath);
            pstmt.setString(6, null); // No observations initially
            pstmt.setBigDecimal(7, null); // No grade initially

            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }
}