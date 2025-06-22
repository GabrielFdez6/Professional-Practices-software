package professionalpractice.model.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.scene.control.Alert; // Necesario para Utils.showSimpleAlert
import professionalpractice.model.ConectionBD;
import professionalpractice.model.pojo.Delivery;
import professionalpractice.model.pojo.DeliveryDefinition;
import professionalpractice.model.pojo.Record;
// import professionalpractice.utils.Constants; // Eliminar esta importación
import professionalpractice.utils.Utils; // Tu clase Utils

public class DeliveryDAO implements professionalpractice.model.dao.interfaces.IDeliveryDAO {

    // Método para obtener un Delivery por su ID y cargar su Definition
    public Delivery obtenerDeliveryPorId(int idDelivery) throws SQLException {
        Delivery delivery = null;
        Connection conexionBD = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexionBD = ConectionBD.getConnection();
            if (conexionBD == null) {
                throw new SQLException("Error: Sin conexión a la Base de Datos");
            }

            String sql = "SELECT " +
                    "d.idDelivery, d.idRecord, d.idDeliveryDefinition, " +
                    // --- Incluye las nuevas columnas de `delivery` aquí ---
                    "d.dateDelivered, d.delivered, d.status, d.filePath, d.observations, d.grade, d.reportedHours, " +
                    // --- Fin de las nuevas columnas ---
                    "dd.idDeliveryDefinition AS dd_id, dd.name AS dd_name, dd.description AS dd_description, " +
                    "dd.startDate AS dd_startDate, dd.endDate AS dd_endDate, dd.deliveryType AS dd_deliveryType, " +
                    "dd.idInitialDocumentTemplate AS dd_initialDocId, dd.idFinalDocumentTemplate AS dd_finalDocId, " +
                    "dd.idReportDocumentTemplate AS dd_reportDocId, " +
                    "dd.idTerm AS dd_termId " +
                    "FROM " +
                    "delivery d " +
                    "JOIN " +
                    "DeliveryDefinition dd ON d.idDeliveryDefinition = dd.idDeliveryDefinition " +
                    "WHERE d.idDelivery = ?";

            sentencia = conexionBD.prepareStatement(sql);
            sentencia.setInt(1, idDelivery);
            resultado = sentencia.executeQuery();

            if (resultado.next()) {
                delivery = new Delivery();
                delivery.setIdDelivery(resultado.getInt("idDelivery"));
                delivery.setIdRecord(resultado.getInt("idRecord"));
                delivery.setIdDeliveryDefinition(resultado.getInt("idDeliveryDefinition"));

                // --- Mapea las nuevas columnas de `delivery` aquí ---
                delivery.setDateDelivered(resultado.getTimestamp("dateDelivered"));
                delivery.setDelivered(resultado.getBoolean("delivered"));
                delivery.setStatus(resultado.getString("status"));
                delivery.setFilePath(resultado.getString("filePath"));
                delivery.setObservations(resultado.getString("observations"));
                // Uso de getBigDecimal para manejar valores DECIMAL de la BD
                delivery.setGrade(resultado.getBigDecimal("grade"));
                // Uso de getObject(column, Class) para tipos primitivos que pueden ser NULL
                delivery.setReportedHours(resultado.getObject("reportedHours", Integer.class));
                // --- Fin del mapeo de nuevas columnas ---

                DeliveryDefinition definition = new DeliveryDefinition();
                definition.setIdDeliveryDefinition(resultado.getInt("dd_id"));
                definition.setName(resultado.getString("dd_name"));
                definition.setDescription(resultado.getString("dd_description"));
                definition.setStartDate(resultado.getTimestamp("dd_startDate"));
                definition.setEndDate(resultado.getTimestamp("dd_endDate"));
                definition.setDeliveryType(resultado.getString("dd_deliveryType"));
                definition.setIdInitialDocumentTemplate(resultado.getObject("dd_initialDocId", Integer.class));
                definition.setIdFinalDocumentTemplate(resultado.getObject("dd_finalDocId", Integer.class));
                definition.setIdReportDocumentTemplate(resultado.getObject("dd_reportDocId", Integer.class));
                definition.setIdTerm(resultado.getObject("dd_termId", Integer.class));

                delivery.setDeliveryDefinition(definition);
            }
        } finally {
            if (resultado != null) { try { resultado.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
            if (sentencia != null) { try { sentencia.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
            if (conexionBD != null) { try { conexionBD.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
        }
        return delivery;
    }

    public int updateStudentDeliveryStatus(
            int idDelivery,
            String filePath,
            java.util.Date deliveryDate,
            String status,
            String observations,
            BigDecimal grade,
            Integer reportedHours
    ) throws SQLException {
        Connection conexionBD = null;
        PreparedStatement sentencia = null;
        int filasAfectadas = 0; // <--- INICIALIZAR A 0 (indica fallo por defecto)

        try {
            conexionBD = ConectionBD.getConnection();
            if (conexionBD == null) {
                throw new SQLException("Error: Sin conexión a la Base de Datos");
            }

            String sql = "UPDATE delivery SET " +
                    "dateDelivered = ?, " +
                    "delivered = ?, " +
                    "status = ?, " +
                    "filePath = ?, " +
                    "observations = ?, " +
                    "grade = ?, " +
                    "reportedHours = ? " +
                    "WHERE idDelivery = ?";

            sentencia = conexionBD.prepareStatement(sql);

            sentencia.setTimestamp(1, new Timestamp(deliveryDate.getTime()));
            sentencia.setBoolean(2, true);
            sentencia.setString(3, status);
            sentencia.setString(4, filePath);
            sentencia.setString(5, observations);

            if (grade != null) {
                sentencia.setBigDecimal(6, grade);
            } else {
                sentencia.setNull(6, java.sql.Types.DECIMAL);
            }
            if (reportedHours != null) {
                sentencia.setInt(7, reportedHours);
            } else {
                sentencia.setNull(7, java.sql.Types.INTEGER);
            }
            sentencia.setInt(8, idDelivery);

            filasAfectadas = sentencia.executeUpdate();

            // Aquí, simplemente devolvemos el número de filas afectadas
            // Un valor mayor que 0 indica éxito.
            return filasAfectadas;

        } finally {
            if (sentencia != null) { try { sentencia.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
            if (conexionBD != null) { try { conexionBD.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
        }
    }

    @Override
    public int scheduleDelivery(Delivery delivery) throws SQLException {
        // Implementación real o lanza UnsupportedOperationException si no se usa
        return 0; // Valor de retorno por defecto
    }

    @Override
    public List<Delivery> getDeliveriesByRecord(int idRecord) throws SQLException {
        List<Delivery> deliveries = new ArrayList<>();
        Connection conexionBD = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexionBD = ConectionBD.getConnection();
            if (conexionBD == null) {
                throw new SQLException("Error: Sin conexión a la Base de Datos");
            }

            // Consulta que une `delivery` con `DeliveryDefinition`
            // para obtener todos los detalles necesarios para la lista.
            String sql = "SELECT " +
                    "d.idDelivery, d.idRecord, d.idDeliveryDefinition, " +
                    // Incluye las columnas de la instancia de `delivery` si las quieres en la lista o en `Delivery` POJO
                    "d.dateDelivered, d.delivered, d.status, d.filePath, d.observations, d.grade, d.reportedHours, " +
                    // Incluye todas las columnas de `DeliveryDefinition` con alias
                    "dd.idDeliveryDefinition AS dd_id, dd.name AS dd_name, dd.description AS dd_description, " +
                    "dd.startDate AS dd_startDate, dd.endDate AS dd_endDate, dd.deliveryType AS dd_deliveryType, " +
                    "dd.idInitialDocumentTemplate AS dd_initialDocId, dd.idFinalDocumentTemplate AS dd_finalDocId, " +
                    "dd.idReportDocumentTemplate AS dd_reportDocId, " +
                    "dd.idTerm AS dd_termId " +
                    "FROM " +
                    "delivery d " +
                    "JOIN " +
                    "DeliveryDefinition dd ON d.idDeliveryDefinition = dd.idDeliveryDefinition " +
                    "WHERE d.idRecord = ?"; // Filtrar por el idRecord del estudiante

            sentencia = conexionBD.prepareStatement(sql);
            sentencia.setInt(1, idRecord);
            resultado = sentencia.executeQuery();

            while (resultado.next()) {
                Delivery delivery = new Delivery();
                delivery.setIdDelivery(resultado.getInt("idDelivery"));
                delivery.setIdRecord(resultado.getInt("idRecord"));
                delivery.setIdDeliveryDefinition(resultado.getInt("idDeliveryDefinition"));

                // Mapear los campos de la instancia de entrega (si existen en `delivery` tabla)
                delivery.setDateDelivered(resultado.getTimestamp("dateDelivered"));
                delivery.setDelivered(resultado.getBoolean("delivered"));
                delivery.setStatus(resultado.getString("status"));
                delivery.setFilePath(resultado.getString("filePath"));
                delivery.setObservations(resultado.getString("observations"));
                delivery.setGrade(resultado.getBigDecimal("grade"));
                delivery.setReportedHours(resultado.getObject("reportedHours", Integer.class));


                // Mapear y anidar el objeto DeliveryDefinition
                DeliveryDefinition definition = new DeliveryDefinition();
                definition.setIdDeliveryDefinition(resultado.getInt("dd_id"));
                definition.setName(resultado.getString("dd_name"));
                definition.setDescription(resultado.getString("dd_description"));
                definition.setStartDate(resultado.getTimestamp("dd_startDate"));
                definition.setEndDate(resultado.getTimestamp("dd_endDate"));
                definition.setDeliveryType(resultado.getString("dd_deliveryType"));
                definition.setIdInitialDocumentTemplate(resultado.getObject("dd_initialDocId", Integer.class));
                definition.setIdFinalDocumentTemplate(resultado.getObject("dd_finalDocId", Integer.class));
                definition.setIdReportDocumentTemplate(resultado.getObject("dd_reportDocId", Integer.class));
                definition.setIdTerm(resultado.getObject("dd_termId", Integer.class));
                // Si agregaste dd.idSubjectGroup, mapearlo aquí:
                // definition.setIdSubjectGroup(resultado.getObject("dd_subjectGroupId", Integer.class));

                delivery.setDeliveryDefinition(definition); // ANIDAR LA DEFINICIÓN

                deliveries.add(delivery);
            }
        } finally {
            if (resultado != null) { try { resultado.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
            if (sentencia != null) { try { sentencia.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
            if (conexionBD != null) { try { conexionBD.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
        }
        return deliveries;
    }

    @Override
    public int scheduleDeliveryForAllRecords(Delivery delivery) throws SQLException {
        // Implementación real o lanza UnsupportedOperationException
        return 0; // Valor de retorno por defecto
    }
}