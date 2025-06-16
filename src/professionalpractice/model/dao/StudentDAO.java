package professionalpractice.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import professionalpractice.model.ConectionBD;
import professionalpractice.model.dao.interfaces.IStudentDAO;
import professionalpractice.model.pojo.Student;
import professionalpractice.model.pojo.StudentProgress;

public class StudentDAO implements IStudentDAO {

    /**
     * Obtiene el progreso de un estudiante para el CU-09.
     * La consulta ha sido corregida para usar las tablas 'student', 'record' y 'project'
     * y sus respectivas columnas del archivo 'proyectobebidafinal-dump.sql'.
     * @param idStudent El ID del estudiante.
     * @return Un objeto StudentProgress con la información, o null si ocurre un error.
     */
    @Override
    public StudentProgress getStudentProgress(int idStudent) throws SQLException {
        StudentProgress studentProgress = null;
        Connection connection = ConectionBD.getConnection();

        if (connection != null) {
            try {
                // Consulta corregida para coincidir con la nueva estructura de la BD
                String query = "SELECT " +
                        "s.idStudent, s.enrollment, s.semester, s.email, s.firstName, s.lastNameMother, s.lastNameFather, s.grade, " +
                        "p.name AS nombreProyecto, r.hoursCount AS horasAcumuladas " +
                        "FROM student s " +
                        "INNER JOIN record r ON s.idStudent = r.idStudent " +
                        "INNER JOIN project p ON r.idRecord = p.idRecord " +
                        "WHERE s.idStudent = ?;";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, idStudent);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    Student student = new Student();
                    student.setStudentId(resultSet.getInt("idStudent"));
                    student.setStudentNumber(resultSet.getString("enrollment")); // Corregido: enrollment
                    student.setSemester(resultSet.getString("semester"));
                    student.setEmail(resultSet.getString("email"));
                    student.setFirstName(resultSet.getString("firstName")); // Corregido: firstName
                    student.setMaternalLastName(resultSet.getString("lastNameMother")); // Corregido: lastNameMother
                    student.setPaternalLastName(resultSet.getString("lastNameFather")); // Corregido: lastNameFather
                    student.setFinalGrade(resultSet.getDouble("grade")); // Corregido: grade

                    studentProgress = new StudentProgress();
                    studentProgress.setProjectName(resultSet.getString("nombreProyecto"));
                    studentProgress.setAccumulatedHours(resultSet.getInt("horasAcumuladas")); // Corregido: r.hoursCount
                    studentProgress.setStudent(student);
                }
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error al obtener el progreso del estudiante: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return studentProgress;
    }

    /**
     * Obtiene un estudiante basado en su id de usuario.
     * La consulta ha sido corregida para usar la tabla 'student' y sus columnas.
     * @param userId El ID del usuario asociado al estudiante.
     * @return Un objeto Student, o null si no se encuentra o hay un error.
     */
    @Override
    public Student getStudentByUserId(int userId) throws SQLException {
        Student student = null;
        Connection connection = ConectionBD.getConnection();

        if (connection != null) {
            try {
                // Consulta corregida para usar la tabla 'student'
                String query = "SELECT idStudent, enrollment, semester, email, firstName, lastNameMother, lastNameFather, idUser FROM student WHERE idUser = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    student = new Student();
                    student.setStudentId(rs.getInt("idStudent"));
                    student.setStudentNumber(rs.getString("enrollment")); // Corregido: enrollment
                    student.setSemester(rs.getString("semester"));
                    student.setEmail(rs.getString("email"));
                    student.setFirstName(rs.getString("firstName")); // Corregido: firstName
                    student.setMaternalLastName(rs.getString("lastNameMother")); // Corregido: lastNameMother
                    student.setPaternalLastName(rs.getString("lastNameFather")); // Corregido: lastNameFather
                    student.setUserId(rs.getInt("idUser"));
                }
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error en getStudentByUserId: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return student;
    }
}