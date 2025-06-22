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

    public static ArrayList<Group> obtenerGruposPorPeriodo(int idPeriodo, Connection conexionBD) throws SQLException { // <--- ACEPTA LA CONEXIÓN
        ArrayList<Group> grupos = new ArrayList<>();
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        // ¡NO abrimos ni cerramos la conexión aquí! Solo la usamos.
        if (conexionBD == null) { // Una validación extra por si se pasa null
            throw new SQLException("Error: La conexión a la base de datos es nula.");
        }

        try {
            String sql = "SELECT " +
                    "sg.idSubjectGroup, " +
                    "sg.schedule AS scheduleInfo, " +
                    "a.idAcademic, " +
                    "a.firstName AS academicFirstName, " +
                    "a.lastNameFather AS academicLastNameFather, " +
                    "a.lastNameMother AS academicLastNameMother, " +
                    "s.idSubject, " +
                    "s.name AS subjectName, " +
                    "s.credits AS subjectCredits, " +
                    "t.idTerm, " +
                    "t.name AS termName, " +
                    "t.startDate AS termStartDate, " +
                    "t.endDate AS termEndDate " +
                    "FROM " +
                    "subjectgroup sg " +
                    "JOIN " +
                    "teachingassignment ta ON sg.idSubjectGroup = ta.idSubjectGroup " +
                    "JOIN " +
                    "academic a ON ta.idAcademic = a.idAcademic " +
                    "JOIN " +
                    "subject s ON sg.idSubject = s.idSubject " +
                    "JOIN " +
                    "term t ON sg.idTerm = t.idTerm " +
                    "WHERE " +
                    "sg.idTerm = ?";

            sentencia = conexionBD.prepareStatement(sql);
            sentencia.setInt(1, idPeriodo);
            resultado = sentencia.executeQuery();

            while (resultado.next()) {
                grupos.add(convertirRegistroGrupo(resultado));
            }
        } finally {
            // Solo cerramos Statement y ResultSet, NO la Connection
            if (resultado != null) { try { resultado.close(); } catch (SQLException ex) { /* Manejo de error al cerrar */ } }
            if (sentencia != null) { try { sentencia.close(); } catch (SQLException ex) { /* Manejo de error al cerrar */ } }
            // NO conexionBD.close();
        }
        return grupos;
    }

    private static Group convertirRegistroGrupo(ResultSet rs) throws SQLException {
        Group grupo = new Group();
        grupo.setIdGrupo(rs.getInt("idSubjectGroup")); // Mapea idGrupo a idSubjectGroup

        // Asumiendo que 'seccion' y 'bloque' en tu POJO Group pueden ser llenados con el 'scheduleInfo'
        // Si necesitas un parseo más complejo de 'scheduleInfo' para 'seccion' y 'bloque', hazlo aquí.
        // Por ahora, simplemente tomamos la cadena completa de 'scheduleInfo'
        // Si tu POJO tiene campos específicos para 'seccion' y 'bloque',
        // considera si 'scheduleInfo' te da esa información o si necesitas ajustar el POJO.
        // Por ejemplo, podrías tener un campo 'schedule' en Group y setearlo así:
        // grupo.setSchedule(rs.getString("scheduleInfo"));

        // Mapear el Académico
        Academic academico = new Academic();
        academico.setIdAcademic(rs.getInt("idAcademic"));
        // Concatenar el nombre completo del académico
        academico.setFirstName(rs.getString("academicFirstName") + " " +
                rs.getString("academicLastNameFather") + " " +
                (rs.getString("academicLastNameMother") != null ? rs.getString("academicLastNameMother") : ""));
        grupo.setAcademico(academico);

        // Mapear la Experiencia Educativa (tabla 'subject')
        Subject ee = new Subject();
        ee.setIdSubject(rs.getInt("idSubject"));
        ee.setName(rs.getString("subjectName"));
        // NOTA: La tabla 'subject' en tu dump NO tiene una columna 'nrc'.
        // Si 'nrc' es obligatorio en tu POJO EducationalExperience,
        // deberás revisar de dónde lo obtienes o si debe ser nullable.
        // Si no es necesario, puedes comentar o eliminar la siguiente línea:
        // ee.setNrc(rs.getString("nrc")); // Esto podría lanzar SQLException si la columna no existe

        grupo.setExperienciaEducativa(ee);

        // Mapear el Periodo (tabla 'term')
        Term periodo = new Term();
        periodo.setIdTerm(rs.getInt("idTerm"));
        periodo.setName(rs.getString("termName"));
        periodo.setStartDate(String.valueOf(rs.getDate("termStartDate").toLocalDate()));
        periodo.setEndDate(String.valueOf(rs.getDate("termEndDate").toLocalDate()));
        grupo.setPeriodo(periodo);

        return grupo;
    }
}