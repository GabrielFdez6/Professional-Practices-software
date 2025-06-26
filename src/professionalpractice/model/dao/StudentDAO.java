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
import professionalpractice.model.pojo.DeliveryInfo;
import professionalpractice.model.pojo.Term;
import professionalpractice.utils.Constants;
import java.time.LocalDateTime;

public class StudentDAO implements IStudentDAO {

  @Override
  public StudentProgress getStudentProgress(int idStudent) throws SQLException {
    StudentProgress studentProgress = null;
    Connection connection = ConectionBD.getConnection();

    if (connection != null) {
      try {
        String query = "SELECT " +
            "s.idStudent, s.enrollment, s.semester, s.email, s.firstName, s.lastNameMother, s.lastNameFather, "
            +
            "p.name AS nombreProyecto, r.hoursCount AS horasAcumuladas, " +
            "COALESCE(AVG(all_grades.grade), 0.0) AS averageGrade " +
            "FROM student s " +
            "INNER JOIN record r ON s.idStudent = r.idStudent " +
            "LEFT JOIN projectassignment pa ON r.idRecord = pa.idRecord " +
            "LEFT JOIN project p ON pa.idProject = p.idProject " +
            "LEFT JOIN ( " +
            "    SELECT d.idRecord, d.grade FROM delivery d WHERE d.grade IS NOT NULL AND d.status IN ('APROBADO') "
            +
            "    UNION ALL " +
            "    SELECT pe.idRecord, pe.grade FROM presentationevaluation pe WHERE pe.grade IS NOT NULL " +
            ") AS all_grades ON r.idRecord = all_grades.idRecord " +
            "WHERE s.idStudent = ? " +
            "GROUP BY s.idStudent, s.enrollment, s.semester, s.email, s.firstName, s.lastNameMother, s.lastNameFather, p.name, r.hoursCount;";

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
          student.setGrade(resultSet.getDouble("averageGrade"));

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
        String query = "SELECT s.idStudent, s.enrollment, s.semester, s.email, s.firstName, s.lastNameMother, s.lastNameFather, s.idUser, s.isAssignedToProject, "
            +
            "COALESCE(AVG(all_grades.grade), 0.0) as calculatedGrade " +
            "FROM student s " +
            "LEFT JOIN record r ON s.idStudent = r.idStudent " +
            "LEFT JOIN ( " +
            "    SELECT d.idRecord, d.grade FROM delivery d WHERE d.grade IS NOT NULL AND d.status IN ('ENTREGADO', 'APROBADO') "
            +
            "    UNION ALL " +
            "    SELECT pe.idRecord, pe.grade FROM presentationevaluation pe WHERE pe.grade IS NOT NULL " +
            ") AS all_grades ON r.idRecord = all_grades.idRecord " +
            "WHERE s.idUser = ? " +
            "GROUP BY s.idStudent, s.enrollment, s.semester, s.email, s.firstName, s.lastNameMother, s.lastNameFather, s.idUser, s.isAssignedToProject";
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
          student.setAssignedToProject(rs.getBoolean("isAssignedToProject"));
          student.setGrade(rs.getDouble("calculatedGrade"));
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

    String query = "SELECT s.idStudent, s.enrollment, " +
        "CONCAT(s.firstName, ' ', s.lastNameFather, ' ', s.lastNameMother) AS studentFullName, " +
        "s.semester, p.name AS projectName " +
        "FROM student s " +
        "JOIN record r ON s.idStudent = r.idStudent " +
        "JOIN projectassignment pa ON r.idRecord = pa.idRecord " +
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
        String query = "SELECT s.idStudent, s.enrollment, s.semester, s.email, s.firstName, s.lastNameMother, s.lastNameFather, "
            +
            "COALESCE(AVG(all_grades.grade), 0.0) as calculatedGrade " +
            "FROM student s " +
            "LEFT JOIN record r ON s.idStudent = r.idStudent " +
            "LEFT JOIN ( " +
            "    SELECT d.idRecord, d.grade FROM delivery d WHERE d.grade IS NOT NULL AND d.status IN ('ENTREGADO', 'APROBADO') "
            +
            "    UNION ALL " +
            "    SELECT pe.idRecord, pe.grade FROM presentationevaluation pe WHERE pe.grade IS NOT NULL " +
            ") AS all_grades ON r.idRecord = all_grades.idRecord " +
            "GROUP BY s.idStudent, s.enrollment, s.semester, s.email, s.firstName, s.lastNameMother, s.lastNameFather";
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
          student.setGrade(rs.getDouble("calculatedGrade"));
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

  /**
   * Verifica si un estudiante está registrado en el período escolar actual
   * 
   * @param idStudent ID del estudiante
   * @return true si el estudiante está en el período actual, false en caso
   *         contrario
   * @throws SQLException si hay error en la consulta
   */
  public static boolean isStudentInCurrentPeriod(int idStudent) throws SQLException {
    String query = "SELECT COUNT(*) FROM record r " +
        "JOIN term t ON r.idTerm = t.idTerm " +
        "WHERE r.idStudent = ? AND CURDATE() BETWEEN t.startDate AND t.endDate";

    try (Connection connection = ConectionBD.getConnection();
        PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setInt(1, idStudent);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getInt(1) > 0;
        }
      }
    }
    return false;
  }

  /**
   * Obtiene el período escolar actual para un estudiante específico
   * 
   * @param idStudent ID del estudiante
   * @return Term del período actual o null si no está en ningún período activo
   * @throws SQLException si hay error en la consulta
   */
  public static Term getCurrentPeriodForStudent(int idStudent) throws SQLException {
    String query = "SELECT t.idTerm, t.name, t.startDate, t.endDate " +
        "FROM record r " +
        "JOIN term t ON r.idTerm = t.idTerm " +
        "WHERE r.idStudent = ? AND CURDATE() BETWEEN t.startDate AND t.endDate " +
        "LIMIT 1";

    try (Connection connection = ConectionBD.getConnection();
        PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setInt(1, idStudent);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          Term term = new Term();
          term.setIdTerm(rs.getInt("idTerm"));
          term.setName(rs.getString("name"));
          term.setStartDate(String.valueOf(rs.getDate("startDate").toLocalDate()));
          term.setEndDate(String.valueOf(rs.getDate("endDate").toLocalDate()));
          return term;
        }
      }
    }
    return null;
  }

  public ArrayList<Student> getUnassignedStudents() throws SQLException {
    ArrayList<Student> students = new ArrayList<>();
    // Modificada la consulta para incluir solo estudiantes del período actual
    String query = "SELECT DISTINCT s.idStudent, s.enrollment, s.semester, s.email, s.firstName, s.lastNameMother, s.lastNameFather "
        +
        "FROM student s " +
        "JOIN record r ON s.idStudent = r.idStudent " +
        "JOIN term t ON r.idTerm = t.idTerm " +
        "WHERE (s.isAssignedToProject = 0 OR s.isAssignedToProject IS NULL) " +
        "AND CURDATE() BETWEEN t.startDate AND t.endDate";

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
      System.err.println("Error al obtener los estudiantes no asignados del período actual: " + e.getMessage());
      throw e;
    }
    return students;
  }

  public static ArrayList<Student> getStudentsWithoutEvaluation() throws SQLException {
    ArrayList<Student> students = new ArrayList<>();
    String query = "SELECT s.idStudent, s.enrollment, s.semester, s.email, s.firstName, s.lastNameMother, s.lastNameFather "
        +
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

  public ArrayList<DeliveryInfo> getStudentDeliveries(int idStudent) throws SQLException {
    ArrayList<DeliveryInfo> deliveries = new ArrayList<>();

    String query = "SELECT " +
        "d.idDelivery, dd.name as deliveryName, dd.deliveryType, " +
        "d.dateDelivered, d.delivered, d.status, d.filePath, d.observations, d.grade, " +
        "dd.startDate, dd.endDate " +
        "FROM delivery d " +
        "INNER JOIN deliverydefinition dd ON d.idDeliveryDefinition = dd.idDeliveryDefinition " +
        "INNER JOIN record r ON d.idRecord = r.idRecord " +
        "WHERE r.idStudent = ? AND d.status IN ('ENTREGADO', 'EN_REVISION', 'RECHAZADO', 'APROBADO') " +
        "ORDER BY dd.endDate ASC";

    try (Connection connection = ConectionBD.getConnection();
        PreparedStatement ps = connection.prepareStatement(query)) {

      ps.setInt(1, idStudent);

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          DeliveryInfo delivery = new DeliveryInfo();
          delivery.setIdDelivery(rs.getInt("idDelivery"));
          delivery.setDeliveryName(rs.getString("deliveryName"));
          delivery.setDeliveryType(rs.getString("deliveryType"));

          // Manejar fechas que pueden ser null
          if (rs.getTimestamp("dateDelivered") != null) {
            delivery.setDateDelivered(rs.getTimestamp("dateDelivered").toLocalDateTime());
          }

          delivery.setDelivered(rs.getBoolean("delivered"));
          delivery.setStatus(rs.getString("status"));
          delivery.setFilePath(rs.getString("filePath"));
          delivery.setObservations(rs.getString("observations"));

          // Manejar calificación que puede ser null
          if (rs.getObject("grade") != null) {
            delivery.setGrade(rs.getDouble("grade"));
          }

          if (rs.getTimestamp("startDate") != null) {
            delivery.setStartDate(rs.getTimestamp("startDate").toLocalDateTime());
          }

          if (rs.getTimestamp("endDate") != null) {
            delivery.setEndDate(rs.getTimestamp("endDate").toLocalDateTime());
          }

          deliveries.add(delivery);
        }
      }
    } catch (SQLException e) {
      System.err.println("Error al obtener las entregas del estudiante: " + e.getMessage());
      throw e;
    }

    return deliveries;
  }

  /**
   * Calcula el promedio de calificaciones del estudiante basado en sus entregas y
   * evaluaciones de presentación
   * 
   * @param idStudent ID del estudiante
   * @return Promedio de calificaciones o 0.0 si no tiene calificaciones
   * @throws SQLException si hay error en la consulta
   */
  public static double calculateStudentAverage(int idStudent) throws SQLException {
    double average = 0.0;

    String query = "SELECT AVG(all_grades.grade) as averageGrade " +
        "FROM record r " +
        "INNER JOIN ( " +
        "    SELECT d.idRecord, d.grade FROM delivery d WHERE d.grade IS NOT NULL AND d.status IN ('ENTREGADO', 'APROBADO') "
        +
        "    UNION ALL " +
        "    SELECT pe.idRecord, pe.grade FROM presentationevaluation pe WHERE pe.grade IS NOT NULL " +
        ") AS all_grades ON r.idRecord = all_grades.idRecord " +
        "WHERE r.idStudent = ?";

    try (Connection connection = ConectionBD.getConnection();
        PreparedStatement ps = connection.prepareStatement(query)) {

      ps.setInt(1, idStudent);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next() && rs.getObject("averageGrade") != null) {
          average = rs.getDouble("averageGrade");
        }
      }
    } catch (SQLException e) {
      System.err.println("Error al calcular el promedio del estudiante: " + e.getMessage());
      throw e;
    }

    return average;
  }

  /**
   * Obtiene el estudiante con su promedio calculado dinámicamente basado en
   * entregas y evaluaciones de presentación
   * 
   * @param idStudent ID del estudiante
   * @return Student con el promedio calculado
   * @throws SQLException si hay error en la consulta
   */
  public static Student getStudentWithCalculatedGrade(int idStudent) throws SQLException {
    Student student = null;

    String query = "SELECT s.idStudent, s.enrollment, s.semester, s.email, s.firstName, " +
        "s.lastNameMother, s.lastNameFather, s.phone, s.credits, s.isAssignedToProject, " +
        "s.idUser, " +
        "COALESCE(AVG(all_grades.grade), 0.0) as calculatedGrade " +
        "FROM student s " +
        "LEFT JOIN record r ON s.idStudent = r.idStudent " +
        "LEFT JOIN ( " +
        "    SELECT d.idRecord, d.grade FROM delivery d WHERE d.grade IS NOT NULL AND d.status IN ('ENTREGADO', 'APROBADO') "
        +
        "    UNION ALL " +
        "    SELECT pe.idRecord, pe.grade FROM presentationevaluation pe WHERE pe.grade IS NOT NULL " +
        ") AS all_grades ON r.idRecord = all_grades.idRecord " +
        "WHERE s.idStudent = ? " +
        "GROUP BY s.idStudent, s.enrollment, s.semester, s.email, s.firstName, s.lastNameMother, s.lastNameFather, " +
        "s.phone, s.credits, s.isAssignedToProject, s.idUser";

    try (Connection connection = ConectionBD.getConnection();
        PreparedStatement ps = connection.prepareStatement(query)) {

      ps.setInt(1, idStudent);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          student = new Student();
          student.setIdStudent(rs.getInt("idStudent"));
          student.setEnrollment(rs.getString("enrollment"));
          student.setSemester(rs.getString("semester"));
          student.setEmail(rs.getString("email"));
          student.setFirstName(rs.getString("firstName"));
          student.setLastNameMother(rs.getString("lastNameMother"));
          student.setLastNameFather(rs.getString("lastNameFather"));
          student.setPhone(rs.getString("phone"));
          student.setCredits(rs.getInt("credits"));
          student.setAssignedToProject(rs.getBoolean("isAssignedToProject"));
          student.setIdUser(rs.getInt("idUser"));
          student.setGrade(rs.getDouble("calculatedGrade"));
        }
      }
    } catch (SQLException e) {
      System.err.println("Error al obtener el estudiante con promedio calculado: " + e.getMessage());
      throw e;
    }

    return student;
  }

}