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
    public static ArrayList<Term> obtenerTodosLosPeriodos() throws SQLException {
        ArrayList<Term> periodos = new ArrayList<>();
        Connection conexionBD = ConectionBD.getConnection();
        if (conexionBD != null) {
            String sql = "SELECT idTerm, name, startDate, endDate FROM term ORDER BY startDate DESC";
            PreparedStatement sentencia = conexionBD.prepareStatement(sql);
            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {
                Term periodo = new Term();
                periodo.setIdTerm(resultado.getInt("idTerm"));
                periodo.setName(resultado.getString("name"));
                periodo.setStartDate(resultado.getString("startDate"));
                periodo.setEndDate(resultado.getString("endDate"));
                periodos.add(periodo);
            }
            conexionBD.close();
            sentencia.close();
            resultado.close();

        } else {
            throw new SQLException("Error: Sin conexión a la Base de Datos.");
        }
        return periodos;
    }

    public static Term obtenerPeriodoActual(Connection conexionBD) throws SQLException { // <--- ACEPTA LA CONEXIÓN
        Term periodoActual = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        // ¡NO abrimos ni cerramos la conexión aquí! Solo la usamos.
        if (conexionBD == null) { // Una validación extra por si se pasa null
            throw new SQLException("Error: La conexión a la base de datos es nula.");
        }

        try {
            String sql = "SELECT idTerm, name, startDate, endDate FROM term " +
                    "WHERE ? BETWEEN startDate AND endDate LIMIT 1";

            sentencia = conexionBD.prepareStatement(sql);
            sentencia.setDate(1, java.sql.Date.valueOf(LocalDate.now()));

            resultado = sentencia.executeQuery();

            if (resultado.next()) {
                periodoActual = new Term();
                periodoActual.setIdTerm(resultado.getInt("idTerm"));
                periodoActual.setName(resultado.getString("name"));
                periodoActual.setStartDate(String.valueOf(resultado.getDate("startDate").toLocalDate()));
                periodoActual.setEndDate(String.valueOf(resultado.getDate("endDate").toLocalDate()));
            }
        } finally {
            // Solo cerramos Statement y ResultSet, NO la Connection
            if (resultado != null) { try { resultado.close(); } catch (SQLException ex) { /* Manejo de error al cerrar */ } }
            if (sentencia != null) { try { sentencia.close(); } catch (SQLException ex) { /* Manejo de error al cerrar */ } }
            // NO conexionBD.close();
        }
        return periodoActual;
    }
}