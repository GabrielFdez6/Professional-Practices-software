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

    public static ArrayList<Group> getGroupsByTerm(int idTerm, Connection connectionBD) throws SQLException {
        ArrayList<Group> groups = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        if (connectionBD == null) {
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

            statement = connectionBD.prepareStatement(sql);
            statement.setInt(1, idTerm);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                groups.add(convertGroupRecord(resultSet));
            }
        } finally {
            if (resultSet != null) { try { resultSet.close(); } catch (SQLException ex) { /* Manejo de error al cerrar */ } }
            if (statement != null) { try { statement.close(); } catch (SQLException ex) { /* Manejo de error al cerrar */ } }
        }
        return groups;
    }

    private static Group convertGroupRecord(ResultSet rs) throws SQLException {
        Group group = new Group();
        group.setIdGroup(rs.getInt("idSubjectGroup")); // Changed from setIdGrupo to setIdGroup

        Academic academic = new Academic();
        academic.setIdAcademic(rs.getInt("idAcademic"));
        academic.setFirstName(rs.getString("academicFirstName") + " " +
                rs.getString("academicLastNameFather") + " " +
                (rs.getString("academicLastNameMother") != null ? rs.getString("academicLastNameMother") : ""));
        group.setAcademic(academic); // Changed from setAcademico to setAcademic

        Subject subject = new Subject();
        subject.setIdSubject(rs.getInt("idSubject"));
        subject.setName(rs.getString("subjectName"));
        group.setEducationalExperience(subject); // Changed from setExperienciaEducativa to setEducationalExperience

        Term term = new Term();
        term.setIdTerm(rs.getInt("idTerm"));
        term.setName(rs.getString("termName"));
        term.setStartDate(String.valueOf(rs.getDate("termStartDate").toLocalDate()));
        term.setEndDate(String.valueOf(rs.getDate("termEndDate").toLocalDate()));
        group.setTerm(term); // Changed from setPeriodo to setTerm

        return group;
    }
}