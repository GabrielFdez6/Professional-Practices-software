package professionalpractice.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import professionalpractice.model.ConectionBD;
import professionalpractice.model.pojo.Delivery;
import professionalpractice.model.pojo.Group;
import professionalpractice.model.pojo.OperationResult;
import professionalpractice.model.pojo.Term;

public class ScheduleDeliveryDAO {
    public static OperationResult programarEntrega(String nombre, String descripcion, String fechaInicio, String fechaFin, int idGrupoEE, String tabla) throws SQLException {
        OperationResult resultado = new OperationResult();
        resultado.setIsError(true);
        Connection conexionBD = ConectionBD.getConnection();
        if (conexionBD != null) {
            String sql = String.format("INSERT INTO %s (nombre, descripcion, fechaInicio, fechaFin, grupoEE_idgrupoEE) VALUES (?, ?, ?, ?, ?)", tabla);

            PreparedStatement sentencia = conexionBD.prepareStatement(sql);
            sentencia.setString(1, nombre);
            sentencia.setString(2, descripcion);
            sentencia.setString(3, fechaInicio);
            sentencia.setString(4, fechaFin);
            sentencia.setInt(5, idGrupoEE);

            int filasAfectadas = sentencia.executeUpdate();

            if (filasAfectadas > 0) {
                resultado.setIsError(false);
                resultado.setMensaje("La entrega ha sido programada correctamente.");
            } else {
                resultado.setMensaje("No se pudo programar la entrega.");
            }

            conexionBD.close();
            sentencia.close();
        } else {
            throw new SQLException("Error: Sin conexión a la Base de Datos");
        }
        return resultado;
    }

    public static OperationResult programarEntregaPeriodoActual(Delivery nuevaEntrega, String tabla) throws SQLException {
        OperationResult resultado = new OperationResult();
        resultado.setIsError(true);
        Connection conexionBD = ConectionBD.getConnection();
        if (conexionBD != null) {
            try {
                // 1. Obtener el periodo actual
                Term periodoActual = TermDAO.obtenerPeriodoActual();
                if (periodoActual == null) {
                    resultado.setMensaje("No se encontró un periodo escolar activo para programar la entrega.");
                    return resultado;
                }

                // 2. Obtener todos los grupos de ese periodo
                ArrayList<Group> grupos = GroupDAO.obtenerGruposPorPeriodo(periodoActual.getIdTerm());
                if (grupos.isEmpty()) {
                    resultado.setMensaje("No se encontraron grupos en el periodo actual para asignar la entrega.");
                    return resultado;
                }

                // 3. Iniciar Transacción
                conexionBD.setAutoCommit(false);

                String sql = String.format("INSERT INTO %s (nombre, descripcion, fechaInicio, fechaFin, grupoEE_idgrupoEE) VALUES (?, ?, ?, ?, ?)", tabla);
                PreparedStatement sentencia = conexionBD.prepareStatement(sql);

                // 4. Registrar la entrega para cada grupo
                for (Group grupo : grupos) {
                    sentencia.setString(1, nuevaEntrega.getName());
                    sentencia.setString(2, nuevaEntrega.getDescription());

                    // --- CORRECCIÓN AQUÍ ---
                    // Se convierten las fechas de String a java.sql.Date
                    LocalDate fechaInicio = LocalDate.parse(nuevaEntrega.getStartDate());
                    LocalDate fechaFin = LocalDate.parse(nuevaEntrega.getEndDate());
                    sentencia.setDate(3, java.sql.Date.valueOf(fechaInicio));
                    sentencia.setDate(4, java.sql.Date.valueOf(fechaFin));
                    // --- FIN DE LA CORRECCIÓN ---

                    sentencia.setInt(5, grupo.getIdGrupo());
                    sentencia.executeUpdate();
                }

                // 5. Confirmar Transacción
                conexionBD.commit();
                resultado.setIsError(false);
                resultado.setMensaje("La entrega ha sido programada correctamente para todos los grupos del periodo actual.");

            } catch (SQLException e) {
                // 6. Revertir en caso de error
                // --- MEJORA SUGERIDA ---
                // Se añade el mensaje de la excepción original para facilitar futuras depuraciones.
                resultado.setMensaje("Ocurrió un error y no se pudo completar la operación: " + e.getMessage());
                try {
                    // Solo intenta hacer rollback si la transacción fue iniciada (autocommit es false).
                    if (conexionBD != null && !conexionBD.getAutoCommit()) {
                        conexionBD.rollback();
                    }
                } catch (SQLException ex) {
                    // Opcional: Registrar que el rollback falló, aunque la excepción original es más importante.
                    ex.printStackTrace();
                }
            } finally {
                if (conexionBD != null) {
                    conexionBD.close();
                }
            }
        } else {
            throw new SQLException("Error: Sin conexión a la Base de Datos");
        }
        return resultado;
    }
}
