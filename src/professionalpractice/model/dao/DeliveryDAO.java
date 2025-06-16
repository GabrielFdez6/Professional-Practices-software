package professionalpractice.model.dao;

import professionalpractice.model.ConectionBD;
import professionalpractice.model.dao.interfaces.IDeliveryDAO;
import professionalpractice.model.dao.interfaces.IRecordDAO;
import professionalpractice.model.pojo.Delivery;
import professionalpractice.utils.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeliveryDAO implements IDeliveryDAO {

    @Override
    public int scheduleDelivery(Delivery delivery) throws SQLException {
        String query = "INSERT INTO delivery (name, deliveryType, startDate, endDate, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, delivery.getName());
            pstmt.setString(2, delivery.getDeliveryType());
            pstmt.setTimestamp(3, delivery.getStartDate());
            pstmt.setTimestamp(4, delivery.getEndDate());
            pstmt.setString(5, delivery.getDescription());

            int rowsAffected = pstmt.executeUpdate();
            return (rowsAffected > 0) ? Constants.OPERATION_SUCCESFUL : 0;
        }
    }

    @Override
    public List<Delivery> getDeliveriesByRecord(int idRecord) throws SQLException {
        List<Delivery> deliveries = new ArrayList<>();
        String query = "SELECT idDelivery, name, deliveryType, startDate, endDate, description FROM delivery WHERE idRecord = ?";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idRecord);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Delivery delivery = new Delivery();
                    delivery.setIdDelivery(rs.getInt("idDelivery"));
                    delivery.setName(rs.getString("name"));
                    delivery.setDeliveryType(rs.getString("deliveryType"));
                    delivery.setStartDate(rs.getTimestamp("startDate"));
                    delivery.setEndDate(rs.getTimestamp("endDate"));
                    delivery.setDescription(rs.getString("description"));
                    deliveries.add(delivery);
                }
            }
        }
        return deliveries;
    }

    public int linkDocumentToDelivery(int idDelivery, int documentId, String deliveryType) throws SQLException {
        String columnToUpdate;
        switch (deliveryType) {
            case "INITIAL DOCUMENT":
                columnToUpdate = "idInitialDocument";
                break;
            case "FINAL DOCUMENT":
                columnToUpdate = "idFinalDocument";
                break;
            case "REPORT":
                columnToUpdate = "idReportDocument";
                break;
            default:
                throw new SQLException("Invalid delivery type provided: " + deliveryType);
        }

        String query = String.format("UPDATE delivery SET %s = ? WHERE idDelivery = ?", columnToUpdate);
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, documentId);
            pstmt.setInt(2, idDelivery);
            int rowsAffected = pstmt.executeUpdate();
            return (rowsAffected > 0) ? Constants.OPERATION_SUCCESFUL : 0;
        }
    }

    @Override
    public int scheduleDeliveryForAllRecords(Delivery delivery) throws SQLException {
        int rowsAffected = 0;
        String query = "INSERT INTO delivery (name, deliveryType, startDate, endDate, description, idRecord) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null; // Declare connection outside the try block

        try {
            conn = ConectionBD.getConnection();
            conn.setAutoCommit(false); // Start transaction

            IRecordDAO recordDAO = new RecordDAO();
            List<Integer> recordIds = recordDAO.getAllActiveRecordIds();

            if (recordIds.isEmpty()) {
                return Constants.OPERATION_SUCCESFUL; // Nothing to do
            }

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                for (Integer recordId : recordIds) {
                    pstmt.setString(1, delivery.getName());
                    pstmt.setString(2, delivery.getDeliveryType());
                    pstmt.setTimestamp(3, delivery.getStartDate());
                    pstmt.setTimestamp(4, delivery.getEndDate());
                    pstmt.setString(5, delivery.getDescription());
                    pstmt.setInt(6, recordId);
                    pstmt.addBatch();
                }

                int[] batchResults = pstmt.executeBatch();
                for (int result : batchResults) {
                    rowsAffected += (result > 0) ? result : 0;
                }
            }

            conn.commit(); // Commit transaction if everything was successful

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Roll back all changes if an error occurred
            }
            throw e; // Re-throw the exception to be handled by the controller
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // Always restore default behavior
                conn.close(); // Finally, close the connection
            }
        }

        return (rowsAffected > 0) ? Constants.OPERATION_SUCCESFUL : 0;
    }
}