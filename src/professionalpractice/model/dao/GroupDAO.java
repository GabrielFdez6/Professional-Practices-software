package professionalpractice.model.dao;

import professionalpractice.model.ConectionBD;
import professionalpractice.model.pojo.Academic;
import professionalpractice.model.pojo.Group;
import professionalpractice.model.pojo.Subject;
import professionalpractice.model.pojo.Term;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class GroupDAO {

    public static ArrayList<Group> obtenerGruposPorPeriodo(int idPeriodo) throws SQLException {
        ArrayList<Group> grupos = new ArrayList<>();
        Connection conexionBD = ConectionBD.getConnection();
        if (conexionBD != null) {
            String sql = "SELECT g.idgrupoEE, g.seccion, g.bloque, " +
                    "a.idAcademico, a.nombre AS nombreAcademico, " +
                    "ee.idExperienciaEducativa, ee.nombre AS nombreEE, ee.nrc, " +
                    "p.idPeriodo, p.nombrePeriodo " +
                    "FROM grupoee g " +
                    "JOIN academico a ON g.Academico_idAcademico = a.idAcademico " +
                    "JOIN experienciaeducativa ee ON g.ExperienciaEducativa_idExperienciaEducativa = ee.idExperienciaEducativa " +
                    "JOIN periodo p ON g.Periodo_idPeriodo = p.idPeriodo " +
                    "WHERE g.Periodo_idPeriodo = ?";

            PreparedStatement sentencia = conexionBD.prepareStatement(sql);
            sentencia.setInt(1, idPeriodo);
            ResultSet resultado = sentencia.executeQuery();

            while(resultado.next()){;
                grupos.add(convertirRegistroGrupo(resultado));
            }

            conexionBD.close();
            sentencia.close();
            resultado.close();
        } else {
            throw new SQLException("Error: Sin conexión a la Base de Datos");
        }
        return grupos;
    }

    private static Group convertirRegistroGrupo(ResultSet resultado) throws SQLException {
        Group grupo = new Group();
        grupo.setIdGrupo(resultado.getInt("idgrupoEE"));
        grupo.setSeccion(resultado.getString("seccion"));
        grupo.setBloque(resultado.getString("bloque"));

        Academic academico = new Academic();
        academico.setIdAcademic(resultado.getInt("idAcademico"));
        academico.setFirstName(resultado.getString("nombreAcademico"));
        grupo.setAcademico(academico);

        Subject ee = new Subject();
        ee.setIdSubject(resultado.getInt("idExperienciaEducativa"));
        ee.setName(resultado.getString("nombreEE"));
        ee.setNrc(resultado.getString("nrc"));
        grupo.setExperienciaEducativa(ee);

        Term periodo = new Term();
        periodo.setIdTerm(resultado.getInt("idPeriodo"));
        periodo.setName(resultado.getString("nombrePeriodo"));
        grupo.setPeriodo(periodo);

        return grupo;
    }
}