package professionalpractice.model.dao;

import professionalpractice.model.ConectionBD;
import professionalpractice.model.dao.interfaces.IProjectDAO;
import professionalpractice.model.pojo.Project;
import professionalpractice.utils.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO implements IProjectDAO {
    @Override
    public List<Project> getAllProjects() throws SQLException {
        List<Project> projects = new ArrayList<>();
        String query = "SELECT idProject, name, department, description FROM project";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Project project = new Project();
                project.setIdProject(rs.getInt("idProject"));
                project.setName(rs.getString("name"));
                project.setDepartment(rs.getString("department"));
                project.setDescription(rs.getString("description"));
                projects.add(project);
            }
        }
        return projects;
    }

    public ArrayList<Project> getAvailableProjects() throws SQLException {
        ArrayList<Project> projects = new ArrayList<>();
        String query = "SELECT idProject, name, department, description, availability FROM project WHERE availability > 0";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Project project = new Project();
                project.setIdProject(rs.getInt("idProject"));
                project.setName(rs.getString("name"));
                project.setDepartment(rs.getString("department"));
                project.setDescription(rs.getString("description"));
                project.setAvailability(rs.getInt("availability"));
                projects.add(project);
            }
        }
        return projects;
    }

    @Override
    public int assignProjectToStudent(int idProject, int idStudent, String projectName) throws SQLException {
        Connection conn = null;
        int responseCode = Constants.CONNECTION_FAILED;

        String getRecordQuery = "SELECT idRecord FROM record WHERE idStudent = ? LIMIT 1";
        String updateProjectQuery = "UPDATE project SET idRecord = ?, availability = availability - 1 WHERE idProject = ?";
        String updateStudentStatusQuery = "UPDATE student SET isAssignedToProject = 1 WHERE idStudent = ?";
        String updateStudentSelectionQuery = "UPDATE student SET projectSelection = ? WHERE idStudent = ?";

        try {
            conn = ConectionBD.getConnection();
            conn.setAutoCommit(false);

            int recordId = -1;
            try (PreparedStatement psGetRecord = conn.prepareStatement(getRecordQuery)) {
                psGetRecord.setInt(1, idStudent);
                try (ResultSet rs = psGetRecord.executeQuery()) {
                    if (rs.next()) {
                        recordId = rs.getInt("idRecord");
                    }
                }
            }

            if (recordId <= 0) {
                throw new SQLException("No se encontró un expediente para el estudiante. La asignación no puede proceder.");
            }

            int projectRowsAffected;
            try (PreparedStatement psUpdateProject = conn.prepareStatement(updateProjectQuery)) {
                psUpdateProject.setInt(1, recordId);
                psUpdateProject.setInt(2, idProject);
                projectRowsAffected = psUpdateProject.executeUpdate();
            }

            int studentStatusRowsAffected;
            try (PreparedStatement psUpdateStudent = conn.prepareStatement(updateStudentStatusQuery)) {
                psUpdateStudent.setInt(1, idStudent);
                studentStatusRowsAffected = psUpdateStudent.executeUpdate();
            }

            int studentSelectionRowsAffected;
            try (PreparedStatement psUpdateSelection = conn.prepareStatement(updateStudentSelectionQuery)) {
                psUpdateSelection.setString(1, projectName);
                psUpdateSelection.setInt(2, idStudent);
                studentSelectionRowsAffected = psUpdateSelection.executeUpdate();
            }

            if (projectRowsAffected > 0 && studentStatusRowsAffected > 0 && studentSelectionRowsAffected > 0) {
                conn.commit();
                responseCode = Constants.OPERATION_SUCCESFUL;
            } else {
                conn.rollback();
                throw new SQLException("La asignación falló, una de las operaciones no afectó a ninguna fila.");
            }

        } catch (SQLException e) {
            System.err.println("Error en la transacción de asignación: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error al revertir la transacción: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseCode;
    }
}