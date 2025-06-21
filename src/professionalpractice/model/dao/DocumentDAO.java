package professionalpractice.model.dao;

import professionalpractice.model.ConectionBD;
import professionalpractice.model.dao.interfaces.IDocumentDAO;
import professionalpractice.model.pojo.FinalDocument;
import professionalpractice.model.pojo.InitialDocument;
import professionalpractice.model.pojo.ReportDocument;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DocumentDAO implements IDocumentDAO {

    private static final Logger LOGGER = Logger.getLogger(DocumentDAO.class.getName());

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

    @Override
    public List<InitialDocument> getAllInitialDocuments() throws SQLException {
        List<InitialDocument> documents = new ArrayList<>();
        String query = "SELECT idInitialDocument, name, date, delivered, status, filePath, observations, grade FROM initialdocument";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                InitialDocument doc = new InitialDocument();
                doc.setIdInitialDocument(rs.getInt("idInitialDocument"));
                doc.setName(rs.getString("name"));
                doc.setDate(rs.getDate("date"));
                doc.setDelivered(rs.getBoolean("delivered"));
                doc.setStatus(rs.getString("status"));
                doc.setFilePath(rs.getString("filePath"));
                doc.setObservations(rs.getString("observations"));
                doc.setGrade(rs.getBigDecimal("grade"));
                documents.add(doc);
                System.out.println("Documento Inicial leído: " + doc.getName() + ", Fecha: " + doc.getDate() + ", Estado: " + doc.getStatus());
            }
            System.out.println("Total de Documentos Iniciales encontrados en DB: " + documents.size());
        }
        return documents;
    }

    @Override
    public List<ReportDocument> getAllReportDocuments() throws SQLException {
        List<ReportDocument> documents = new ArrayList<>();
        String query = "SELECT idReportDocument, reportedHours, date, grade, name, delivered, status, filePath FROM reportdocument";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ReportDocument doc = new ReportDocument();
                doc.setIdReportDocument(rs.getInt("idReportDocument"));
                doc.setReportedHours(rs.getInt("reportedHours"));
                doc.setDate(rs.getDate("date"));
                doc.setGrade(rs.getBigDecimal("grade"));
                doc.setName(rs.getString("name"));
                doc.setDelivered(rs.getBoolean("delivered"));
                doc.setStatus(rs.getString("status"));
                doc.setFilePath(rs.getString("filePath"));
                documents.add(doc);
                System.out.println("Reporte leído: " + doc.getName() + ", Horas: " + doc.getReportedHours() + ", Estado: " + doc.getStatus());
            }
            System.out.println("Total de Reportes encontrados en DB: " + documents.size());
        }
        return documents;
    }

    @Override
    public List<FinalDocument> getAllFinalDocuments() throws SQLException {
        List<FinalDocument> documents = new ArrayList<>();
        String query = "SELECT idFinalDocument, name, date, delivered, status, filePath, observations, grade FROM finaldocument";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                FinalDocument doc = new FinalDocument();
                doc.setIdFinalDocument(rs.getInt("idFinalDocument"));
                doc.setName(rs.getString("name"));
                doc.setDate(rs.getDate("date"));
                doc.setDelivered(rs.getBoolean("delivered"));
                doc.setStatus(rs.getString("status"));
                doc.setFilePath(rs.getString("filePath"));
                doc.setObservations(rs.getString("observations"));
                doc.setGrade(rs.getBigDecimal("grade"));
                documents.add(doc);
                System.out.println("Documento Final leído: " + doc.getName() + ", Fecha: " + doc.getDate() + ", Estado: " + doc.getStatus());
            }
            System.out.println("Total de Documentos Finales encontrados en DB: " + documents.size());
        }
        return documents;
    }

    @Override
    public List<String> getDistinctInitialDocumentNames() throws SQLException {
        List<String> names = new ArrayList<>();
        String query = "SELECT DISTINCT name FROM initialdocument";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching distinct initial document names", e);
            throw e; // Propaga la excepción para que el controlador la maneje
        }
        return names;
    }

    @Override
    public List<String> getDistinctReportDocumentNames() throws SQLException {
        List<String> names = new ArrayList<>();
        String query = "SELECT DISTINCT name FROM reportdocument";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching distinct report document names", e);
            throw e; // Propaga la excepción para que el controlador la maneje
        }
        return names;
    }

    @Override
    public List<String> getDistinctFinalDocumentNames() throws SQLException {
        List<String> names = new ArrayList<>();
        String query = "SELECT DISTINCT name FROM finaldocument";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching distinct final document names", e);
            throw e; // Propaga la excepción para que el controlador la maneje
        }
        return names;
    }
}