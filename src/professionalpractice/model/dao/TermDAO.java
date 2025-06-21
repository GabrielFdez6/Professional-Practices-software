package professionalpractice.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import professionalpractice.model.ConectionBD;
import professionalpractice.model.pojo.Term;

public class TermDAO {
    public static ArrayList<Term> obtenerTodosLosPeriodos() throws SQLException {
        ArrayList<Term> periodos = new ArrayList<>();
        Connection conexionBD = ConectionBD.getConnection();
        if (conexionBD != null) {
            String sql = "SELECT idPeriodo, nombrePeriodo, fechaInicio, fechaFin FROM periodo ORDER BY fechaInicio DESC";
            PreparedStatement sentencia = conexionBD.prepareStatement(sql);
            ResultSet resultado = sentencia.executeQuery();
            while (resultado.next()) {
                Term periodo = new Term();
                periodo.setIdTerm(resultado.getInt("idPeriodo"));
                periodo.setName(resultado.getString("nombrePeriodo"));
                periodo.setStartDate(resultado.getString("fechaInicio"));
                periodo.setEndDate(resultado.getString("fechaFin"));
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

    public static Term obtenerPeriodoActual() throws SQLException {
        Term periodo = null;
        Connection conexionBD = ConectionBD.getConnection();
        if (conexionBD != null) {
            String sql = "SELECT idPeriodo, nombrePeriodo, fechaInicio, fechaFin FROM periodo ORDER BY fechaInicio DESC LIMIT 1";
            PreparedStatement sentencia = conexionBD.prepareStatement(sql);
            ResultSet resultado = sentencia.executeQuery();
            if (resultado.next()) {
                periodo = new Term();
                periodo.setIdTerm(resultado.getInt("idPeriodo"));
                periodo.setName(resultado.getString("nombrePeriodo"));
                periodo.setStartDate(resultado.getString("fechaInicio"));
                periodo.setEndDate(resultado.getString("fechaFin"));
            }
            conexionBD.close();
            sentencia.close();
            resultado.close();
        } else {
            throw new SQLException("Error: Sin conexión a la Base de Datos");
        }
        return periodo;
    }
}