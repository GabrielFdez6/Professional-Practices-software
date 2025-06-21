/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package professionalpractice.model.dao;

import java.sql.*;
import java.util.ArrayList;
import professionalpractice.model.ConectionBD;
import professionalpractice.model.pojo.Delivery;

/**
 *
 * @author Dell
 */
public class DeliveryDAO {
    public static ArrayList<Delivery> obtenerEntregasPorGrupo(int idGrupo, String tabla) throws SQLException {
        ArrayList<Delivery> entregas = new ArrayList<>();
        Connection conexionBD = ConectionBD.getConnection();
        if (conexionBD != null) {
            String sql = String.format("SELECT * FROM %s WHERE grupoEE_idgrupoEE = ?", tabla);

            PreparedStatement sentencia = conexionBD.prepareStatement(sql);
            sentencia.setInt(1, idGrupo);
            ResultSet resultado = sentencia.executeQuery();

            while(resultado.next()){
                Delivery entrega = new Delivery();
                // El nombre del campo ID es diferente en cada tabla
                if (tabla.equals("entregadocumentoinicio")) {
                    entrega.setIdDelivery(resultado.getInt("idEntregaDocumentoInicio"));
                } else if (tabla.equals("entregareporte")) {
                    entrega.setIdDelivery(resultado.getInt("idEntregaReporte"));
                } else {
                    entrega.setIdDelivery(resultado.getInt("idEntregaDocumentoFinal"));
                }
                entrega.setName(resultado.getString("nombre"));
                entrega.setDescription(resultado.getString("descripcion"));
                entrega.setStartDate(Timestamp.valueOf(resultado.getString("fechaInicio")));
                entrega.setEndDate(Timestamp.valueOf(resultado.getString("fechaFin")));
                entregas.add(entrega);
            }

            conexionBD.close();
            sentencia.close();
            resultado.close();
        } else {
            throw new SQLException("Error: Sin conexión a la Base de Datos");
        }
        return entregas;
    }

    public static ArrayList<Delivery> obtenerTodasLasEntregas(String tabla) throws SQLException {
        ArrayList<Delivery> entregas = new ArrayList<>();
        Connection conexionBD = ConectionBD.getConnection();
        if (conexionBD != null) {
            String sql = String.format("SELECT * FROM %s", tabla);
            PreparedStatement sentencia = conexionBD.prepareStatement(sql);
            ResultSet resultado = sentencia.executeQuery();

            while(resultado.next()){
                Delivery entrega = new Delivery();
                if (tabla.equals("entregadocumentoinicio")) {
                    entrega.setIdDelivery(resultado.getInt("idEntregaDocumentoInicio"));
                } else if (tabla.equals("entregareporte")) {
                    entrega.setIdDelivery(resultado.getInt("idEntregaReporte"));
                } else {
                    entrega.setIdDelivery(resultado.getInt("idEntregaDocumentoFinal"));
                }
                entrega.setName(resultado.getString("nombre"));
                entrega.setDescription(resultado.getString("descripcion"));
                entrega.setStartDate(Timestamp.valueOf(resultado.getString("fechaInicio")));
                entrega.setEndDate(Timestamp.valueOf(resultado.getString("fechaFin")));
                entregas.add(entrega);
            }

            conexionBD.close();
            sentencia.close();
            resultado.close();
        } else {
            throw new SQLException("Error: Sin conexión a la Base de Datos");
        }
        return entregas;
    }

    public static ArrayList<Delivery> obtenerEntregasPendientesEstudiante(int idGrupo, int idExpediente, String tablaEntrega) throws SQLException {
        ArrayList<Delivery> entregas = new ArrayList<>();
        Connection conexionBD = ConectionBD.getConnection();
        if (conexionBD != null) {

            // --- LÓGICA PARA CONSTRUIR LA CONSULTA DINÁMICA ---
            String tablaDocumento;
            String campoIdEntregaFK;
            String campoIdEntregaPK;
            String campoIdDocumentoPK;

            if (tablaEntrega.equals("entregadocumentoinicio")) {
                tablaDocumento = "documentoinicio";
                campoIdEntregaPK = "idEntregaDocumentoInicio";
                campoIdDocumentoPK = "idDocumentoInicio";
                campoIdEntregaFK = "EntregaDocumentoInicio_idEntregaDocumentoInicio";
            } else if (tablaEntrega.equals("entregareporte")) {
                tablaDocumento = "reporte";
                campoIdEntregaPK = "idEntregaReporte";
                campoIdDocumentoPK = "idReporte";
                campoIdEntregaFK = "EntregaReporte_idEntregaReporte";
            } else { // entregadocumentofinal
                tablaDocumento = "documentofinal";
                campoIdEntregaPK = "idEntregaDocumentoFinal";
                campoIdDocumentoPK = "idDocumentoFinal";
                campoIdEntregaFK = "EntregaDocumentoFinal_idEntregaDocumentoFinal";
            }

            String sql = String.format(
                    "SELECT e.*, d.%s AS documento_id " +
                            "FROM %s e " +
                            "LEFT JOIN %s d ON e.%s = d.%s AND d.Expediente_idExpediente = ? " +
                            "WHERE e.grupoEE_idgrupoEE = ?",
                    campoIdDocumentoPK, tablaEntrega, tablaDocumento, campoIdEntregaPK, campoIdEntregaFK
            );
            // --- FIN DE LA LÓGICA DE CONSTRUCCIÓN ---

            PreparedStatement sentencia = conexionBD.prepareStatement(sql);
            sentencia.setInt(1, idExpediente); // El idExpediente para el JOIN
            sentencia.setInt(2, idGrupo);      // El idGrupo para el WHERE

            ResultSet resultado = sentencia.executeQuery();

            while(resultado.next()){
                Delivery entrega = new Delivery();
                entrega.setIdDelivery(resultado.getInt(campoIdEntregaPK));
                entrega.setName(resultado.getString("nombre"));
                entrega.setDescription(resultado.getString("descripcion"));
                entrega.setStartDate(Timestamp.valueOf(resultado.getString("fechaInicio")));
                entrega.setStartDate(Timestamp.valueOf(resultado.getString("fechaFin")));

                // Determinar el estado basado en si se encontró un documento
                if (resultado.getObject("documento_id") != null) {
                    entrega.setStatus("Entregado");
                } else {
                    entrega.setStatus("Sin Entregar");
                }

                entregas.add(entrega);
            }

            conexionBD.close();
        }
        return entregas;
    }

    public static ArrayList<Delivery> obtenerEntregasPorTipo(int idTipoDocumento, int idAcademico) throws SQLException {
        ArrayList<Delivery> entregas = new ArrayList<>();
        Connection conexion = ConectionBD.getConnection();

        if (conexion != null) {
            String nombreTabla = "";
            String nombreColumnaId = "";

            switch (idTipoDocumento) {
                case 1:
                    nombreTabla = "entregadocumentoinicio";
                    nombreColumnaId = "idEntregaDocumentoInicio";
                    break;
                case 2:
                    nombreTabla = "entregareporte";
                    nombreColumnaId = "idEntregaReporte";
                    break;
                case 3:
                    nombreTabla = "entregadocumentofinal";
                    nombreColumnaId = "idEntregaDocumentoFinal";
                    break;
                default:
                    conexion.close();
                    return entregas;
            }

            // Consulta modificada con JOIN para filtrar por el idAcademico
            String consulta = String.format("SELECT t.%s, t.nombre, t.descripcion, t.fechaInicio, t.fechaFin " +
                            "FROM %s t " +
                            "JOIN grupoee g ON t.grupoEE_idgrupoEE = g.idgrupoEE " +
                            "WHERE g.Academico_idAcademico = ?",
                    nombreColumnaId, nombreTabla);

            try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
                // Se establece el parámetro del idAcademico en la consulta
                sentencia.setInt(1, idAcademico);

                ResultSet resultado = sentencia.executeQuery();
                while (resultado.next()) {
                    Delivery entrega = new Delivery();
                    entrega.setIdDelivery(resultado.getInt(nombreColumnaId));
                    entrega.setName(resultado.getString("nombre"));
                    entrega.setDescription(resultado.getString("descripcion"));
                    // La corrección del tipo de dato que hicimos antes se mantiene
                    entrega.setStartDate(Timestamp.valueOf(resultado.getTimestamp("fechaInicio").toLocalDateTime().toLocalDate().toString()));
                    entrega.setEndDate(Timestamp.valueOf(resultado.getTimestamp("fechaFin").toLocalDateTime().toLocalDate().toString()));
                    entregas.add(entrega);
                }
            } finally {
                conexion.close();
            }
        }
        return entregas;
    }

    public int linkDocumentToDelivery(int idDelivery, int documentId, String deliveryType) {
        return 0;
    }
}
