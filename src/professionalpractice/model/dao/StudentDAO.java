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

    @Override
    public StudentProgress getStudentProgress(int idStudent) throws SQLException {
        StudentProgress studentProgress = null;
        Connection connection = ConectionBD.getConnection();

        if (connection != null) {
            try {
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
                    student.setEnrollment(resultSet.getString("enrollment"));
                    student.setSemester(resultSet.getString("semester"));
                    student.setEmail(resultSet.getString("email"));
                    student.setFirstName(resultSet.getString("firstName"));
                    student.setLastNameMother(resultSet.getString("lastNameMother"));
                    student.setLastNameFather(resultSet.getString("lastNameFather"));
                    student.setGrade(resultSet.getDouble("grade"));

                    studentProgress = new StudentProgress();
                    studentProgress.setProjectName(resultSet.getString("nombreProyecto"));
                    studentProgress.setAccumulatedHours(resultSet.getInt("horasAcumuladas"));
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

    @Override
    public Student getStudentByUserId(int userId) throws SQLException {
        Student student = null;
        Connection connection = ConectionBD.getConnection();

        if (connection != null) {
            try {
                String query = "SELECT idStudent, enrollment, semester, email, firstName, lastNameMother, lastNameFather, idUser FROM student WHERE idUser = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    student = new Student();
                    student.setIdStudent(rs.getInt("idStudent"));
                    student.setEnrollment(rs.getString("enrollment"));
                    student.setSemester(rs.getString("semester"));
                    student.setEmail(rs.getString("email"));
                    student.setFirstName(rs.getString("firstName"));
                    student.setLastNameMother(rs.getString("lastNameMother"));
                    student.setLastNameFather(rs.getString("lastNameFather"));
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

    public static HashMap<String, Object> getStudentsWithProjectByProfessor(int idAcademic, int idTerm) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("responseCode", Constants.OPERATION_SUCCESFUL);

        String query =
                "SELECT s.idStudent, s.enrollment, " +
                        "CONCAT(s.firstName, ' ', s.lastNameFather, ' ', s.lastNameMother) AS studentFullName, " +
                        "s.semester, p.name AS projectName " +
                        "FROM student s " +
                        "JOIN record r ON s.idStudent = r.idStudent " +
                        "JOIN project_assignment pa ON r.idRecord = pa.idRecord " +
                        "JOIN project p ON pa.idProject = p.idProject " +
                        "JOIN subjectgroup sg ON r.idSubjectGroup = sg.idSubjectGroup " +
                        "JOIN teachingassignment ta ON sg.idSubjectGroup = ta.idSubjectGroup " +
                        "WHERE ta.idAcademic = ? AND sg.idTerm = ? AND s.isAssignedToProject = 1;";

        try (Connection connection = ConectionBD.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, idAcademic);
            pstmt.setInt(2, idTerm);

            ArrayList<StudentProject> studentsList = new ArrayList<>();
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    StudentProject studentProject = new StudentProject();
                    studentProject.setIdStudent(rs.getInt("idStudent"));
                    studentProject.setEnrollment(rs.getString("enrollment"));
                    studentProject.setStudentFullName(rs.getString("studentFullName"));
                    studentProject.setSemester(rs.getString("semester"));
                    studentProject.setProjectName(rs.getString("projectName"));
                    studentsList.add(studentProject);
                }
            }
            System.out.println(studentsList);
            response.put("students", studentsList);

        } catch (SQLException ex) {
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

    public static int getRecordIdByStudentId(int idStudent) throws SQLException {
        int idRecord = -1;
        String query = "SELECT idRecord FROM record WHERE idStudent = ? LIMIT 1";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idStudent);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idRecord = rs.getInt("idRecord");
                }
            }
        }
        return idRecord;
    }

    public ArrayList<Student> getUnassignedStudents() throws SQLException {
        ArrayList<Student> students = new ArrayList<>();
        String query = "SELECT idStudent, enrollment, semester, email, firstName, lastNameMother, lastNameFather FROM student WHERE isAssignedToProject = 0 OR isAssignedToProject IS NULL";
        try (Connection connection = ConectionBD.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
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
        } catch (SQLException e) {
            System.err.println("Error al obtener los estudiantes no asignados: " + e.getMessage());
            throw e;
        }
        return students;
    }

    public static ArrayList<Student> getStudentsWithoutEvaluation() throws SQLException {
        ArrayList<Student> students = new ArrayList<>();
        String query = "SELECT s.idStudent, s.enrollment, s.semester, s.email, s.firstName, s.lastNameMother, s.lastNameFather " +
                "FROM student s " +
                "JOIN record r ON s.idStudent = r.idStudent " +
                "JOIN term t ON r.idTerm = t.idTerm " +
                "WHERE CURDATE() BETWEEN t.startDate AND t.endDate AND s.isAssignedToProject = 1";

        try (Connection connection = ConectionBD.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

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
        } catch (SQLException e) {
            System.err.println("Error al obtener los estudiantes no evaluados: " + e.getMessage());
            throw e;
        }
        return students;
    }
}