package professionalpractice.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import professionalpractice.model.ConectionBD;
import professionalpractice.model.dao.interfaces.IStudentDAO;
import professionalpractice.model.pojo.Student;
import professionalpractice.model.pojo.StudentProgress;
import professionalpractice.model.pojo.StudentProject;
import professionalpractice.utils.Constants;

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
                    student.setIdStudent(resultSet.getInt("idStudent"));
                    student.setEnrollment(resultSet.getString("enrollment")); // Corregido: enrollment
                    student.setSemester(resultSet.getString("semester"));
                    student.setEmail(resultSet.getString("email"));
                    student.setFirstName(resultSet.getString("firstName")); // Corregido: firstName
                    student.setLastNameMother(resultSet.getString("lastNameMother")); // Corregido: lastNameMother
                    student.setLastNameFather(resultSet.getString("lastNameFather")); // Corregido: lastNameFather
                    student.setGrade(resultSet.getDouble("grade")); // Corregido: grade

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
                    student.setIdStudent(rs.getInt("idStudent"));
                    student.setEnrollment(rs.getString("enrollment")); // Corregido: enrollment
                    student.setSemester(rs.getString("semester"));
                    student.setEmail(rs.getString("email"));
                    student.setFirstName(rs.getString("firstName")); // Corregido: firstName
                    student.setLastNameMother(rs.getString("lastNameMother")); // Corregido: lastNameMother
                    student.setLastNameFather(rs.getString("lastNameFather")); // Corregido: lastNameFather
                    student.setIdUser(rs.getInt("idUser"));
                }
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error en getStudentByUserId: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return student;
    }

    /**
     * Recupera una lista de estudiantes con sus proyectos asignados, filtrando por el profesor (academic) y el periodo (term).
     * Este método se alinea con el Flujo Normal, paso 1 del CU-10.
     *
     * @param idAcademic El ID del profesor que imparte la materia.
     * @param idTerm El ID del periodo escolar actual.
     * @return Un HashMap que contiene un código de respuesta y, si la operación es exitosa,
     * una lista de objetos StudentProject con la información de los estudiantes.
     */
    public static HashMap<String, Object> getStudentsWithProjectByProfessor(int idAcademic, int idTerm) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("responseCode", Constants.OPERATION_SUCCESFUL);


        // Consulta SQL que une las tablas necesarias para obtener los datos requeridos por la vista.
        String query =
                "SELECT s.idStudent, s.enrollment, " +
                        "CONCAT(s.firstName, ' ', s.lastNameFather, ' ', s.lastNameMother) AS studentFullName, " +
                        "s.semester, p.name AS projectName " +
                        "FROM student s " +
                        "JOIN record r ON s.idStudent = r.idStudent " +
                        "JOIN project p ON r.idRecord = p.idRecord " +
                        "JOIN subjectgroup sg ON r.idSubjectGroup = sg.idSubjectGroup " +
                        "JOIN teachingassignment ta ON sg.idSubjectGroup = ta.idSubjectGroup " +
                        "WHERE ta.idAcademic = ? AND sg.idTerm = ? AND s.isAssignedToProject = 1;";

        try (Connection connection = ConectionBD.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            // Se establecen los parámetros para el PreparedStatement para filtrar la búsqueda.
            pstmt.setInt(1, idAcademic);
            pstmt.setInt(2, idTerm);

            ArrayList<StudentProject> studentsList = new ArrayList<>();
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Se crea un objeto StudentProject por cada fila del resultado.
                    StudentProject studentProject = new StudentProject();
                    studentProject.setIdStudent(rs.getInt("idStudent"));
                    studentProject.setEnrollment(rs.getString("enrollment"));
                    studentProject.setStudentFullName(rs.getString("studentFullName"));
                    studentProject.setSemester(rs.getString("semester"));
                    studentProject.setProjectName(rs.getString("projectName"));
                    studentsList.add(studentProject);
                }
            }
            response.put("students", studentsList);

        } catch (SQLException ex) {
            // En caso de una excepción SQL, se cambia el código de respuesta.
            response.put("responseCode", Constants.CONNECTION_FAILED);
            ex.printStackTrace();
        }
        return response;
    }

    public static ArrayList<Student> getStudents() throws SQLException {
        ArrayList<Student> students = new ArrayList<>();
        Connection connection = ConectionBD.getConnection();

        if (connection != null) {
            try {
                String query = "SELECT idStudent, enrollment, semester, email, firstName, lastNameMother, lastNameFather FROM student";
                PreparedStatement ps = connection.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Student student = new Student();
                    student.setIdStudent(rs.getInt("idStudent"));
                    student.setEnrollment(rs.getString("enrollment"));
                    student.setSemester(rs.getString("semester"));
                    student.setEmail(rs.getString("email"));
                    student.setFirstName(rs.getString("firstName"));
                    student.setLastNameMother(rs.getString("lastNameMother"));
                    student.setLastNameFather(rs.getString("lastNameFather"));
                    students.add(student);
                }
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error al obtener los estudiantes: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return students;
    }
}