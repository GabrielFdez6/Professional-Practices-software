package professionalpractice.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;

import professionalpractice.model.ConectionBD;
import professionalpractice.model.pojo.Delivery; // POJO para las instancias de entrega
import professionalpractice.model.pojo.DeliveryDefinition; // Nuevo POJO para las definiciones
import professionalpractice.model.pojo.Group;
import professionalpractice.model.pojo.OperationResult;
import professionalpractice.model.pojo.Term;
import professionalpractice.model.pojo.Record;

public class ScheduleDeliveryDAO {

    // (El método programarEntrega si aún lo usas, se mantiene sin cambios si es para un caso diferente)

    // Método modificado para aceptar los datos de la definición de entrega directamente
    public static OperationResult programarEntregaPeriodoActual(
            String nombreDefinicion,
            String descripcionDefinicion,
            Timestamp fechaInicioDefinicion,
            Timestamp fechaFinDefinicion,
            String tipoEntregaUI) throws SQLException { // 'tipoEntregaUI' es el String de la UI ("DOCUMENTOS INICIALES", etc.)

        OperationResult resultado = new OperationResult();
        resultado.setIsError(true);
        Connection conexionBD = null;

        try {
            conexionBD = ConectionBD.getConnection();
            if (conexionBD == null) {
                throw new SQLException("Error: Sin conexión a la Base de Datos");
            }

            Term periodoActual = TermDAO.obtenerPeriodoActual(conexionBD);
            if (periodoActual == null) {
                resultado.setMensaje("No se encontró un periodo escolar activo para programar la entrega.");
                return resultado;
            }

            ArrayList<Group> grupos = GroupDAO.obtenerGruposPorPeriodo(periodoActual.getIdTerm(), conexionBD);
            if (grupos.isEmpty()) {
                resultado.setMensaje("No se encontraron grupos en el periodo actual para asignar la entrega.");
                return resultado;
            }

            String deliveryTypeEnum; // Valor ENUM para la BD ('INITIAL DOCUMENT', 'REPORT', etc.)
            String documentTableName; // Nombre de la tabla de documento específico (e.g., 'initialdocument')
            String documentIdColumnNameInDefinition; // Columna FK en DeliveryDefinition (e.g., 'idInitialDocumentTemplate')
            String documentStatusDefault = "EN_REVISION"; // Estado por defecto al programar

            // Mapear el tipo de entrega de la UI al ENUM de la BD y nombres de tabla/columna
            switch(tipoEntregaUI){
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
                    resultado.setMensaje("Tipo de entrega desconocido.");
                    return resultado;
            }

            conexionBD.setAutoCommit(false); // Inicia la transacción

            // --- PASO 1: Insertar en la tabla específica del documento y obtener su ID ---
            int idDocumentoGenerado = -1;
            try {
                // Los métodos insertInitialDocument, insertReportDocument, insertFinalDocument
                // usan LocalDate para la fecha, así que convertimos el Timestamp
                LocalDate dateForDocument = fechaInicioDefinicion.toLocalDateTime().toLocalDate();

                if ("initialdocument".equals(documentTableName)) {
                    idDocumentoGenerado = insertInitialDocument(conexionBD, nombreDefinicion, dateForDocument, documentStatusDefault);
                } else if ("reportdocument".equals(documentTableName)) {
                    idDocumentoGenerado = insertReportDocument(conexionBD, nombreDefinicion, dateForDocument, 0, 0.00f, documentStatusDefault);
                } else if ("finaldocument".equals(documentTableName)) {
                    idDocumentoGenerado = insertFinalDocument(conexionBD, nombreDefinicion, dateForDocument, documentStatusDefault);
                }
            } catch (SQLException e) {
                resultado.setMensaje("Error al preparar el documento base: " + e.getMessage());
                conexionBD.rollback();
                return resultado;
            }

            if (idDocumentoGenerado == -1) {
                resultado.setMensaje("No se pudo generar el ID del documento base para la entrega.");
                conexionBD.rollback();
                return resultado;
            }

            // --- PASO 2: Insertar en DeliveryDefinition (LA PLANTILLA DE LA ENTREGA) ---
            String sqlInsertDefinition = "INSERT INTO DeliveryDefinition (name, description, startDate, endDate, deliveryType, idInitialDocumentTemplate, idFinalDocumentTemplate, idReportDocumentTemplate, idTerm) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psDefinition = conexionBD.prepareStatement(sqlInsertDefinition, Statement.RETURN_GENERATED_KEYS);

            psDefinition.setString(1, nombreDefinicion);
            psDefinition.setString(2, descripcionDefinicion);
            psDefinition.setTimestamp(3, fechaInicioDefinicion);
            psDefinition.setTimestamp(4, fechaFinDefinicion);
            psDefinition.setString(5, deliveryTypeEnum);

            // Setear el ID de la plantilla de documento en la columna correcta, los otros a NULL
            if ("idInitialDocumentTemplate".equals(documentIdColumnNameInDefinition)) {
                psDefinition.setInt(6, idDocumentoGenerado);
                psDefinition.setNull(7, java.sql.Types.INTEGER);
                psDefinition.setNull(8, java.sql.Types.INTEGER);
            } else if ("idFinalDocumentTemplate".equals(documentIdColumnNameInDefinition)) {
                psDefinition.setNull(6, java.sql.Types.INTEGER);
                psDefinition.setInt(7, idDocumentoGenerado);
                psDefinition.setNull(8, java.sql.Types.INTEGER);
            } else if ("idReportDocumentTemplate".equals(documentIdColumnNameInDefinition)) {
                psDefinition.setNull(6, java.sql.Types.INTEGER);
                psDefinition.setNull(7, java.sql.Types.INTEGER);
                psDefinition.setInt(8, idDocumentoGenerado);
            }
            psDefinition.setInt(9, periodoActual.getIdTerm()); // Asocia la definición al periodo actual

            psDefinition.executeUpdate();
            ResultSet rsDefinition = psDefinition.getGeneratedKeys();
            int idDeliveryDefinitionGenerado = -1;
            if (rsDefinition.next()) {
                idDeliveryDefinitionGenerado = rsDefinition.getInt(1);
            }
            rsDefinition.close();
            psDefinition.close();

            if (idDeliveryDefinitionGenerado == -1) {
                resultado.setMensaje("No se pudo generar el ID para la definición de la entrega.");
                conexionBD.rollback();
                return resultado;
            }

            // --- PASO 3: Insertar en la tabla 'delivery' (LAS INSTANCIAS POR CADA RECORD) ---
            // Solo las columnas idRecord y idDeliveryDefinition son obligatorias aquí,
            // más cualquier campo que hayas añadido para la instancia individual (e.g., status, dateDelivered).
            String sqlInsertInstance = "INSERT INTO delivery (idRecord, idDeliveryDefinition, delivered, status, dateDelivered, filePath, observations, grade, reportedHours) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psInstance = conexionBD.prepareStatement(sqlInsertInstance);

            int totalEntregasProgramadas = 0;
            for (Group grupo : grupos) {
                ArrayList<Record> recordsDelGrupo = RecordDAO.obtenerRecordsPorGrupoYPeriodo(grupo.getIdGrupo(), periodoActual.getIdTerm(), conexionBD);

                for (Record record : recordsDelGrupo) {
                    psInstance.setInt(1, record.getIdRecord());
                    psInstance.setInt(2, idDeliveryDefinitionGenerado);
                    psInstance.setBoolean(3, false); // delivered = false al programar
                    psInstance.setString(4, "PENDIENTE"); // status = "PENDIENTE" al programar
                    psInstance.setNull(5, java.sql.Types.TIMESTAMP); // dateDelivered = NULL
                    psInstance.setNull(6, java.sql.Types.VARCHAR);   // filePath = NULL
                    psInstance.setNull(7, java.sql.Types.VARCHAR);   // observations = NULL
                    psInstance.setNull(8, java.sql.Types.DECIMAL);   // grade = NULL
                    psInstance.setNull(9, java.sql.Types.INTEGER);   // reportedHours = NULL

                    psInstance.executeUpdate();
                    totalEntregasProgramadas++;
                }
            }
            psInstance.close();

            conexionBD.commit();
            resultado.setIsError(false);
            resultado.setMensaje("Se programaron " + totalEntregasProgramadas + " instancias de entrega y se creó la definición de entrega.");

        } catch (SQLException e) {
            resultado.setMensaje("Ocurrió un error y no se pudo completar la operación: " + e.getMessage());
            try {
                if (conexionBD != null && !conexionBD.getAutoCommit()) {
                    conexionBD.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            if (conexionBD != null) {
                try {
                    conexionBD.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return resultado;
    }

    // --- Métodos Auxiliares para insertar en tablas de documentos (se mantienen igual) ---
    private static int insertInitialDocument(Connection conexionBD, String name, LocalDate date, String status) throws SQLException {
        String sql = "INSERT INTO initialdocument (name, date, delivered, status) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        int idGenerado = -1;

        try {
            ps = conexionBD.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setBoolean(3, false); // delivered = 0 (false) por defecto al programar
            ps.setString(4, status); // status = 'EN_REVISION' o 'NO_ENTREGADO'

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException ex) { /* Log error */ } }
            if (ps != null) { try { ps.close(); } catch (SQLException ex) { /* Log error */ } }
        }
        return idGenerado;
    }

    private static int insertFinalDocument(Connection conexionBD, String name, LocalDate date, String status) throws SQLException {
        String sql = "INSERT INTO finaldocument (name, date, delivered, status) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        int idGenerado = -1;

        try {
            ps = conexionBD.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setBoolean(3, false); // delivered = 0 (false) por defecto
            ps.setString(4, status); // status = 'EN_REVISION' o 'NO_ENTREGADO'

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException ex) { /* Log error */ } }
            if (ps != null) { try { ps.close(); } catch (SQLException ex) { /* Log error */ } }
        }
        return idGenerado;
    }

    private static int insertReportDocument(Connection conexionBD, String name, LocalDate date, int reportedHours, float grade, String status) throws SQLException {
        String sql = "INSERT INTO reportdocument (name, date, reportedHours, grade, delivered, status) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        int idGenerado = -1;

        try {
            ps = conexionBD.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setInt(3, reportedHours); // 0 horas reportadas al programar
            ps.setFloat(4, grade);       // 0.00 de calificación al programar
            ps.setBoolean(5, false);     // delivered = 0 (false) por defecto
            ps.setString(6, status);     // status = 'EN_REVISION' o 'NO_ENTREGADO'

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException ex) { /* Log error */ } }
            if (ps != null) { try { ps.close(); } catch (SQLException ex) { /* Log error */ } }
        }
        return idGenerado;
    }
}