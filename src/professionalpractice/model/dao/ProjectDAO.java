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
        String query = "SELECT idProject, name, description, methodology, availability, idLinkedOrganization, idProjectManager FROM project";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Project project = new Project();
                project.setIdProject(rs.getInt("idProject"));
                project.setName(rs.getString("name"));
                project.setDescription(rs.getString("description"));
                project.setMethodology(rs.getString("methodology"));
                project.setAvailability(rs.getInt("availability"));
                project.setIdLinkedOrganization(rs.getInt("idLinkedOrganization"));
                project.setIdProjectManager(rs.getInt("idProjectManager"));

                projects.add(project);
            }
        }
        return projects;
    }

    public ArrayList<Project> getAvailableProjects() throws SQLException {
        ArrayList<Project> projects = new ArrayList<>();
        String query = "SELECT p.idProject, p.name, p.description, p.methodology, p.availability, p.idLinkedOrganization, p.idProjectManager, lo.name AS linkedOrganizationName " +
                "FROM project p " +
                "JOIN linkedorganization lo ON p.idLinkedOrganization = lo.idLinkedOrganization " +
                "WHERE p.availability > 0";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Project project = new Project();
                project.setIdProject(rs.getInt("idProject"));
                project.setName(rs.getString("name"));
                project.setDescription(rs.getString("description"));
                project.setAvailability(rs.getInt("availability"));
                project.setMethodology(rs.getString("methodology"));
                project.setIdLinkedOrganization(rs.getInt("idLinkedOrganization"));
                project.setIdProjectManager(rs.getInt("idProjectManager"));
                project.setLinkedOrganizationName(rs.getString("linkedOrganizationName"));
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
        String insertAssignmentQuery = "INSERT INTO projectassignment (idProject, idRecord) VALUES (?, ?)";
        String updateAvailabilityQuery = "UPDATE project SET availability = availability - 1 WHERE idProject = ?";
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
                conn.rollback();
                throw new SQLException("No se encontró un expediente para el estudiante. La asignación no puede proceder.");
            }

            try (PreparedStatement psInsertAssignment = conn.prepareStatement(insertAssignmentQuery)) {
                psInsertAssignment.setInt(1, idProject);
                psInsertAssignment.setInt(2, recordId);
                psInsertAssignment.executeUpdate();
            }

            try (PreparedStatement psUpdateProject = conn.prepareStatement(updateAvailabilityQuery)) {
                psUpdateProject.setInt(1, idProject);
                psUpdateProject.executeUpdate();
            }

            try (PreparedStatement psUpdateStudent = conn.prepareStatement(updateStudentStatusQuery)) {
                psUpdateStudent.setInt(1, idStudent);
                psUpdateStudent.executeUpdate();
            }

            try (PreparedStatement psUpdateSelection = conn.prepareStatement(updateStudentSelectionQuery)) {
                psUpdateSelection.setString(1, projectName);
                psUpdateSelection.setInt(2, idStudent);
                psUpdateSelection.executeUpdate();
            }

            conn.commit();
            responseCode = Constants.OPERATION_SUCCESFUL;

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

    @Override
    public int saveProject(Project project) throws SQLException {
        String sql = "INSERT INTO project (name, description, methodology, availability, idLinkedOrganization, idProjectManager, department) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getDescription());
            pstmt.setString(3, project.getMethodology());
            pstmt.setInt(4, project.getAvailability());
            pstmt.setInt(5, project.getIdLinkedOrganization());
            pstmt.setInt(6, project.getIdProjectManager());
            pstmt.setString(7, project.getDepartment());
            int rowsAffected = pstmt.executeUpdate();
            return (rowsAffected > 0) ? Constants.OPERATION_SUCCESFUL : Constants.OPERATION_FAILED;
        }
    }

    @Override
    public int updateProject(Project project) throws SQLException {
        String sql = "UPDATE project SET name = ?, description = ?, methodology = ?, availability = ?, idLinkedOrganization = ?, idProjectManager = ?, department = ? WHERE idProject = ?";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, project.getName());
            pstmt.setString(2, project.getDescription());
            pstmt.setString(3, project.getMethodology());
            pstmt.setInt(4, project.getAvailability());
            pstmt.setInt(5, project.getIdLinkedOrganization());
            pstmt.setInt(6, project.getIdProjectManager());
            pstmt.setString(7, project.getDepartment());
            pstmt.setInt(8, project.getIdProject());
            int rowsAffected = pstmt.executeUpdate();
            return (rowsAffected > 0) ? Constants.OPERATION_SUCCESFUL : Constants.OPERATION_FAILED;
        }
    }

    @Override
    public List<Project> getProjectsByProjectManagerId(int projectManagerId) throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT idProject, name, description, methodology, availability, idLinkedOrganization, idProjectManager, department " +
                "FROM project " +
                "WHERE idProjectManager = ?";

        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectManagerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Project project = new Project();
                    project.setIdProject(rs.getInt("idProject"));
                    project.setName(rs.getString("name"));
                    project.setDescription(rs.getString("description"));
                    project.setMethodology(rs.getString("methodology"));
                    project.setAvailability(rs.getInt("availability"));
                    project.setIdLinkedOrganization(rs.getInt("idLinkedOrganization"));
                    project.setIdProjectManager(rs.getInt("idProjectManager"));
                    project.setDepartment(rs.getString("department"));
                    projects.add(project);
                }
            }
        }
        return projects;
    }

    @Override
    public Project getProjectById(int projectId) throws SQLException {
        Project project = null;
        String sql = "SELECT idProject, name, description, methodology, availability, idLinkedOrganization, idProjectManager " +
                "FROM project WHERE idProject = ?";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    project = new Project();
                    project.setIdProject(rs.getInt("idProject"));
                    project.setName(rs.getString("name"));
                    project.setDescription(rs.getString("description"));
                    project.setMethodology(rs.getString("methodology"));
                    project.setAvailability(rs.getInt("availability"));
                    project.setIdLinkedOrganization(rs.getInt("idLinkedOrganization"));
                    project.setIdProjectManager(rs.getInt("idProjectManager"));
                }
            }
        }
        return project;
    }

    @Override
    public int deleteProject(int projectId) throws SQLException {
        String sql = "DELETE FROM project WHERE idProject = ?";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            int rowsAffected = pstmt.executeUpdate();
            return (rowsAffected > 0) ? Constants.OPERATION_SUCCESFUL : Constants.OPERATION_FAILED;
        }
    }
}