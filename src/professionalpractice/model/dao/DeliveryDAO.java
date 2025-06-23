package professionalpractice.model.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import professionalpractice.model.ConectionBD;
import professionalpractice.model.pojo.Delivery;
import professionalpractice.model.pojo.DeliveryDefinition;

public class DeliveryDAO implements professionalpractice.model.dao.interfaces.IDeliveryDAO {

    public Delivery getDeliveryById(int idDelivery) throws SQLException {
        Delivery delivery = null;
        Connection connectionBD = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connectionBD = ConectionBD.getConnection();
            if (connectionBD == null) {
                throw new SQLException("Error: Sin conexión a la Base de Datos");
            }

            String sql = "SELECT " +
                    "d.idDelivery, d.idRecord, d.idDeliveryDefinition, " +
                    "d.dateDelivered, d.delivered, d.status, d.filePath, d.observations, d.grade, d.reportedHours, " +
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

            statement = connectionBD.prepareStatement(sql);
            statement.setInt(1, idDelivery);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                delivery = new Delivery();
                delivery.setIdDelivery(resultSet.getInt("idDelivery"));
                delivery.setIdRecord(resultSet.getInt("idRecord"));
                delivery.setIdDeliveryDefinition(resultSet.getInt("idDeliveryDefinition"));

                delivery.setDateDelivered(resultSet.getTimestamp("dateDelivered"));
                delivery.setDelivered(resultSet.getBoolean("delivered"));
                delivery.setStatus(resultSet.getString("status"));
                delivery.setFilePath(resultSet.getString("filePath"));
                delivery.setObservations(resultSet.getString("observations"));
                delivery.setGrade(resultSet.getBigDecimal("grade"));
                delivery.setReportedHours(resultSet.getObject("reportedHours", Integer.class));

                DeliveryDefinition definition = new DeliveryDefinition();
                definition.setIdDeliveryDefinition(resultSet.getInt("dd_id"));
                definition.setName(resultSet.getString("dd_name"));
                definition.setDescription(resultSet.getString("dd_description"));
                definition.setStartDate(resultSet.getTimestamp("dd_startDate"));
                definition.setEndDate(resultSet.getTimestamp("dd_endDate"));
                definition.setDeliveryType(resultSet.getString("dd_deliveryType"));
                definition.setIdInitialDocumentTemplate(resultSet.getObject("dd_initialDocId", Integer.class));
                definition.setIdFinalDocumentTemplate(resultSet.getObject("dd_finalDocId", Integer.class));
                definition.setIdReportDocumentTemplate(resultSet.getObject("dd_reportDocId", Integer.class));
                definition.setIdTerm(resultSet.getObject("dd_termId", Integer.class));

                delivery.setDeliveryDefinition(definition);
            }
        } finally {
            if (resultSet != null) { try { resultSet.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
            if (statement != null) { try { statement.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
            if (connectionBD != null) { try { connectionBD.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
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
        Connection connectionBD = null;
        PreparedStatement statement = null;
        int rowsAffected = 0;

        try {
            connectionBD = ConectionBD.getConnection();
            if (connectionBD == null) {
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

            statement = connectionBD.prepareStatement(sql);

            statement.setTimestamp(1, new Timestamp(deliveryDate.getTime()));
            statement.setBoolean(2, true);
            statement.setString(3, status);
            statement.setString(4, filePath);
            statement.setString(5, observations);

            if (grade != null) {
                statement.setBigDecimal(6, grade);
            } else {
                statement.setNull(6, java.sql.Types.DECIMAL);
            }
            if (reportedHours != null) {
                statement.setInt(7, reportedHours);
            } else {
                statement.setNull(7, java.sql.Types.INTEGER);
            }
            statement.setInt(8, idDelivery);

            rowsAffected = statement.executeUpdate();

            return rowsAffected;

        } finally {
            if (statement != null) { try { statement.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
            if (connectionBD != null) { try { connectionBD.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
        }
    }

    @Override
    public int scheduleDelivery(Delivery delivery) throws SQLException {
        return 0;
    }

    @Override
    public List<Delivery> getDeliveriesByRecord(int idRecord) throws SQLException {
        List<Delivery> deliveries = new ArrayList<>();
        Connection connectionBD = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connectionBD = ConectionBD.getConnection();
            if (connectionBD == null) {
                throw new SQLException("Error: Sin conexión a la Base de Datos");
            }

            String sql = "SELECT " +
                    "d.idDelivery, d.idRecord, d.idDeliveryDefinition, " +
                    "d.dateDelivered, d.delivered, d.status, d.filePath, d.observations, d.grade, d.reportedHours, " +
                    "dd.idDeliveryDefinition AS dd_id, dd.name AS dd_name, dd.description AS dd_description, " +
                    "dd.startDate AS dd_startDate, dd.endDate AS dd_endDate, dd.deliveryType AS dd_deliveryType, " +
                    "dd.idInitialDocumentTemplate AS dd_initialDocId, dd.idFinalDocumentTemplate AS dd_finalDocId, " +
                    "dd.idReportDocumentTemplate AS dd_reportDocId, " +
                    "dd.idTerm AS dd_termId " +
                    "FROM " +
                    "delivery d " +
                    "JOIN " +
                    "DeliveryDefinition dd ON d.idDeliveryDefinition = dd.idDeliveryDefinition " +
                    "WHERE d.idRecord = ?";

            statement = connectionBD.prepareStatement(sql);
            statement.setInt(1, idRecord);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Delivery delivery = new Delivery();
                delivery.setIdDelivery(resultSet.getInt("idDelivery"));
                delivery.setIdRecord(resultSet.getInt("idRecord"));
                delivery.setIdDeliveryDefinition(resultSet.getInt("idDeliveryDefinition"));

                delivery.setDateDelivered(resultSet.getTimestamp("dateDelivered"));
                delivery.setDelivered(resultSet.getBoolean("delivered"));
                delivery.setStatus(resultSet.getString("status"));
                delivery.setFilePath(resultSet.getString("filePath"));
                delivery.setObservations(resultSet.getString("observations"));
                delivery.setGrade(resultSet.getBigDecimal("grade"));
                delivery.setReportedHours(resultSet.getObject("reportedHours", Integer.class));

                DeliveryDefinition definition = new DeliveryDefinition();
                definition.setIdDeliveryDefinition(resultSet.getInt("dd_id"));
                definition.setName(resultSet.getString("dd_name"));
                definition.setDescription(resultSet.getString("dd_description"));
                definition.setStartDate(resultSet.getTimestamp("dd_startDate"));
                definition.setEndDate(resultSet.getTimestamp("dd_endDate"));
                definition.setDeliveryType(resultSet.getString("dd_deliveryType"));
                definition.setIdInitialDocumentTemplate(resultSet.getObject("dd_initialDocId", Integer.class));
                definition.setIdFinalDocumentTemplate(resultSet.getObject("dd_finalDocId", Integer.class));
                definition.setIdReportDocumentTemplate(resultSet.getObject("dd_reportDocId", Integer.class));
                definition.setIdTerm(resultSet.getObject("dd_termId", Integer.class));

                delivery.setDeliveryDefinition(definition);

                deliveries.add(delivery);
            }
        } finally {
            if (resultSet != null) { try { resultSet.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
            if (statement != null) { try { statement.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
            if (connectionBD != null) { try { connectionBD.close(); } catch (SQLException ex) { ex.printStackTrace(); } }
        }
        return deliveries;
    }

    @Override
    public int scheduleDeliveryForAllRecords(Delivery delivery) throws SQLException {
        return 0;
    }
}