package professionalpractice.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import professionalpractice.model.ConectionBD;
import professionalpractice.model.pojo.Term;

public class TermDAO {
    public static ArrayList<Term> getAllTerms() throws SQLException {
        ArrayList<Term> terms = new ArrayList<>();
        Connection connectionBD = ConectionBD.getConnection();
        if (connectionBD != null) {
            String sql = "SELECT idTerm, name, startDate, endDate FROM term ORDER BY startDate DESC";
            PreparedStatement statement = connectionBD.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Term term = new Term();
                term.setIdTerm(resultSet.getInt("idTerm"));
                term.setName(resultSet.getString("name"));
                term.setStartDate(resultSet.getString("startDate"));
                term.setEndDate(resultSet.getString("endDate"));
                terms.add(term);
            }
            connectionBD.close();
            statement.close();
            resultSet.close();

        } else {
            throw new SQLException("Error: Sin conexión a la Base de Datos.");
        }
        return terms;
    }

    public static Term getCurrentPeriod(Connection connectionBD) throws SQLException {
        Term currentTerm = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        if (connectionBD == null) {
            throw new SQLException("Error: La conexión a la base de datos es nula.");
        }

        try {
            String sql = "SELECT idTerm, name, startDate, endDate FROM term " +
                    "WHERE ? BETWEEN startDate AND endDate LIMIT 1";

            statement = connectionBD.prepareStatement(sql);
            statement.setDate(1, java.sql.Date.valueOf(LocalDate.now()));

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                currentTerm = new Term();
                currentTerm.setIdTerm(resultSet.getInt("idTerm"));
                currentTerm.setName(resultSet.getString("name"));
                currentTerm.setStartDate(String.valueOf(resultSet.getDate("startDate").toLocalDate()));
                currentTerm.setEndDate(String.valueOf(resultSet.getDate("endDate").toLocalDate()));
            }
        } finally {
            if (resultSet != null) { try { resultSet.close(); } catch (SQLException ex) { /* Manejo de error al cerrar */ } }
            if (statement != null) { try { statement.close(); } catch (SQLException ex) { /* Manejo de error al cerrar */ } }
        }
        return currentTerm;
    }
}