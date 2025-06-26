package professionalpractice.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List; // Importar List

import professionalpractice.model.ConectionBD;
import professionalpractice.model.pojo.DeliveryDefinition;
import professionalpractice.model.pojo.Group;
import professionalpractice.model.pojo.OperationResult;
import professionalpractice.model.pojo.Term;
import professionalpractice.model.pojo.Record;

public class ScheduleDeliveryDAO {

    public static OperationResult scheduleDeliveryForMultipleGroups(
            String definitionName,
            String definitionDescription,
            Timestamp definitionStartDate,
            Timestamp definitionEndDate,
            String deliveryTypeUI,
            List<Integer> idSelectedGroups) throws SQLException {

        OperationResult result = new OperationResult();
        result.setIsError(true);
        Connection connectionBD = null;

        try {
            connectionBD = ConectionBD.getConnection();
            if (connectionBD == null) {
                throw new SQLException("Error: Sin conexión con la Base de Datos");
            }

            Term currentTerm = TermDAO.getCurrentPeriod(connectionBD);
            if (currentTerm == null) {
                result.setMensaje("No se encontró un periodo académico activo para programar la entrega.");
                return result;
            }

            if (idSelectedGroups == null || idSelectedGroups.isEmpty()) {
                result.setMensaje("No se seleccionaron grupos para programar la entrega.");
                connectionBD.rollback();
                return result;
            }

            String deliveryTypeEnum;
            String documentTableName;
            String documentIdColumnNameInDefinition;
            String documentStatusDefault = "EN_REVISION";

            switch(deliveryTypeUI){
                case "DOCUMENTOS INICIALES":
                    deliveryTypeEnum = "INITIAL DOCUMENT";
                    documentTableName = "initialdocument";
                    documentIdColumnNameInDefinition = "idInitialDocumentTemplate";
                    break;
                case "REPORTES":
                    deliveryTypeEnum = "REPORT";
                    documentTableName = "reportdocument";
                    documentIdColumnNameInDefinition = "idReportDocumentTemplate";
                    break;
                case "DOCUMENTOS FINALES":
                    deliveryTypeEnum = "FINAL DOCUMENT";
                    documentTableName = "finaldocument";
                    documentIdColumnNameInDefinition = "idFinalDocumentTemplate";
                    break;
                default:
                    result.setMensaje("Tipo de entrega desconocido.");
                    return result;
            }

            connectionBD.setAutoCommit(false);

            int generatedDocumentId = -1;
            try {
                LocalDate dateForDocument = definitionStartDate.toLocalDateTime().toLocalDate();

                if ("initialdocument".equals(documentTableName)) {
                    generatedDocumentId = insertInitialDocument(connectionBD, definitionName, dateForDocument, documentStatusDefault);
                } else if ("reportdocument".equals(documentTableName)) {
                    generatedDocumentId = insertReportDocument(connectionBD, definitionName, dateForDocument, 0, 0.00f, documentStatusDefault);
                } else if ("finaldocument".equals(documentTableName)) {
                    generatedDocumentId = insertFinalDocument(connectionBD, definitionName, dateForDocument, documentStatusDefault);
                }
            } catch (SQLException e) {
                result.setMensaje("Error al preparar el documento base: " + e.getMessage());
                connectionBD.rollback();
                return result;
            }

            if (generatedDocumentId == -1) {
                result.setMensaje("No se pudo generar el ID del documento base para la entrega.");
                connectionBD.rollback();
                return result;
            }

            String sqlInsertDefinition = "INSERT INTO DeliveryDefinition (name, description, startDate, endDate, deliveryType, idInitialDocumentTemplate, idFinalDocumentTemplate, idReportDocumentTemplate, idTerm) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psDefinition = connectionBD.prepareStatement(sqlInsertDefinition, Statement.RETURN_GENERATED_KEYS);

            psDefinition.setString(1, definitionName);
            psDefinition.setString(2, definitionDescription);
            psDefinition.setTimestamp(3, definitionStartDate);
            psDefinition.setTimestamp(4, definitionEndDate);
            psDefinition.setString(5, deliveryTypeEnum);

            if ("idInitialDocumentTemplate".equals(documentIdColumnNameInDefinition)) {
                psDefinition.setInt(6, generatedDocumentId);
                psDefinition.setNull(7, java.sql.Types.INTEGER);
                psDefinition.setNull(8, java.sql.Types.INTEGER);
            } else if ("idFinalDocumentTemplate".equals(documentIdColumnNameInDefinition)) {
                psDefinition.setNull(6, java.sql.Types.INTEGER);
                psDefinition.setInt(7, generatedDocumentId);
                psDefinition.setNull(8, java.sql.Types.INTEGER);
            } else if ("idReportDocumentTemplate".equals(documentIdColumnNameInDefinition)) {
                psDefinition.setNull(6, java.sql.Types.INTEGER);
                psDefinition.setNull(7, java.sql.Types.INTEGER);
                psDefinition.setInt(8, generatedDocumentId);
            }
            psDefinition.setInt(9, currentTerm.getIdTerm());

            psDefinition.executeUpdate();
            ResultSet rsDefinition = psDefinition.getGeneratedKeys();
            int generatedDeliveryDefinitionId = -1;
            if (rsDefinition.next()) {
                generatedDeliveryDefinitionId = rsDefinition.getInt(1);
            }
            rsDefinition.close();
            psDefinition.close();

            if (generatedDeliveryDefinitionId == -1) {
                result.setMensaje("No se pudo generar el ID para la definición de la entrega.");
                connectionBD.rollback();
                return result;
            }

            String sqlInsertInstance = "INSERT INTO delivery (idRecord, idDeliveryDefinition, delivered, status, dateDelivered, filePath, observations, grade, reportedHours) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psInstance = connectionBD.prepareStatement(sqlInsertInstance);

            int totalDeliveriesScheduled = 0;

            for (Integer idGroup : idSelectedGroups) {
                ArrayList<Record> recordsForThisGroup = RecordDAO.getRecordsForAssignedStudentsInGroupAndPeriod(idGroup, currentTerm.getIdTerm(), connectionBD);

                if (recordsForThisGroup.isEmpty()) {
                    continue;
                }

                for (Record record : recordsForThisGroup) {
                    psInstance.setInt(1, record.getIdRecord());
                    psInstance.setInt(2, generatedDeliveryDefinitionId);
                    psInstance.setBoolean(3, false);
                    psInstance.setString(4, "PENDIENTE");
                    psInstance.setNull(5, java.sql.Types.TIMESTAMP);
                    psInstance.setNull(6, java.sql.Types.VARCHAR);
                    psInstance.setNull(7, java.sql.Types.VARCHAR);
                    psInstance.setNull(8, java.sql.Types.DECIMAL);
                    psInstance.setNull(9, java.sql.Types.INTEGER);

                    psInstance.executeUpdate();
                    totalDeliveriesScheduled++;
                }
            }
            psInstance.close();

            if (totalDeliveriesScheduled == 0) {
                result.setMensaje("No se programaron entregas ya que no se encontraron estudiantes elegibles en los grupos seleccionados.");
                connectionBD.rollback();
                return result;
            }

            connectionBD.commit();
            result.setIsError(false);
            result.setMensaje("Se programaron " + totalDeliveriesScheduled + " entregas para los estudiantes de los grupos seleccionados.");

        } catch (SQLException e) {
            result.setMensaje("Ocurrió un error y no se pudo completar la operación: " + e.getMessage());
            try {
                if (connectionBD != null && !connectionBD.getAutoCommit()) {
                    connectionBD.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            if (connectionBD != null) {
                try {
                    connectionBD.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return result;
    }

    private static boolean isDeliveryAlreadyScheduled(int idRecord, int idDeliveryDefinition, Connection connectionBD) throws SQLException {
        String sql = "SELECT COUNT(*) FROM delivery WHERE idRecord = ? AND idDeliveryDefinition = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connectionBD.prepareStatement(sql);
            ps.setInt(1, idRecord);
            ps.setInt(2, idDeliveryDefinition);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
        }
        return false;
    }

    private static int insertInitialDocument(Connection connectionBD, String name, LocalDate date, String status) throws SQLException {
        String sql = "INSERT INTO initialdocument (name, date) VALUES (?, ?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        int generatedId = -1;

        try {
            ps = connectionBD.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setDate(2, java.sql.Date.valueOf(date));

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException ex) { /* Log error */ } }
            if (ps != null) { try { ps.close(); } catch (SQLException ex) { /* Log error */ } }
        }
        return generatedId;
    }

    private static int insertFinalDocument(Connection connectionBD, String name, LocalDate date, String status) throws SQLException {
        String sql = "INSERT INTO finaldocument (name, date) VALUES (?, ?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        int generatedId = -1;

        try {
            ps = connectionBD.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setDate(2, java.sql.Date.valueOf(date));

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException ex) { /* Log error */ } }
            if (ps != null) { try { ps.close(); } catch (SQLException ex) { /* Log error */ } }
        }
        return generatedId;
    }

    private static int insertReportDocument(Connection connectionBD, String name, LocalDate date, int reportedHours, float grade, String status) throws SQLException {
        String sql = "INSERT INTO reportdocument (name, date) VALUES (?, ?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        int generatedId = -1;

        try {
            ps = connectionBD.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setDate(2, java.sql.Date.valueOf(date));

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException ex) { /* Log error */ } }
            if (ps != null) { try { ps.close(); } catch (SQLException ex) { /* Log error */ } }
        }
        return generatedId;
    }
}