package professionalpractice.model.dao;

import professionalpractice.model.ConectionBD;
import professionalpractice.model.dao.interfaces.IProjectManagerDAO;
import professionalpractice.model.pojo.ProjectManager;
import professionalpractice.utils.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectManagerDAO implements IProjectManagerDAO {

    @Override
    public List<ProjectManager> getAllProjectManagers() throws SQLException {
        List<ProjectManager> managers = new ArrayList<>();
        String sql = "SELECT idProjectManager, idLinkedOrganization, firstName, lastNameFather, lastNameMother, position, email, phone FROM projectmanager";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ProjectManager manager = new ProjectManager();
                manager.setIdProjectManager(rs.getInt("idProjectManager"));
                manager.setIdLinkedOrganization(rs.getInt("idLinkedOrganization"));
                manager.setFirstName(rs.getString("firstName"));
                manager.setLastNameFather(rs.getString("lastNameFather"));
                manager.setLastNameMother(rs.getString("lastNameMother"));
                manager.setPosition(rs.getString("position"));
                manager.setEmail(rs.getString("email"));
                manager.setPhone(rs.getString("phone"));
                managers.add(manager);
            }
        }
        return managers;
    }

    @Override
    public int registerProjectManager(ProjectManager manager) throws SQLException {
        String sql = "INSERT INTO projectmanager (idLinkedOrganization, firstName, lastNameFather, lastNameMother, position, email, phone) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, manager.getIdLinkedOrganization());
            pstmt.setString(2, manager.getFirstName());
            pstmt.setString(3, manager.getLastNameFather());
            pstmt.setString(4, manager.getLastNameMother());
            pstmt.setString(5, manager.getPosition());
            pstmt.setString(6, manager.getEmail());
            pstmt.setString(7, manager.getPhone());

            int rowsAffected = pstmt.executeUpdate();
            return (rowsAffected > 0) ? Constants.OPERATION_SUCCESFUL : 0;
        }
    }

    @Override
    public int updateProjectManager(ProjectManager manager) throws SQLException {
        System.out.println(manager.getIdProjectManager());
        String sql = "UPDATE projectmanager SET firstName = ?, lastNameFather = ?, lastNameMother = ?, position = ?, email = ?, phone = ? WHERE idProjectManager = ?";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, manager.getFirstName());
            pstmt.setString(2, manager.getLastNameFather());
            pstmt.setString(3, manager.getLastNameMother());
            pstmt.setString(4, manager.getPosition());
            pstmt.setString(5, manager.getEmail());
            pstmt.setString(6, manager.getPhone());
            pstmt.setInt(7, manager.getIdProjectManager());

            int rowsAffected = pstmt.executeUpdate();
            return (rowsAffected > 0) ? Constants.OPERATION_SUCCESFUL : 0;
        }
    }

    @Override
    public List<ProjectManager> getAllProjectManagersByProjectId(int projectId) throws SQLException {
        List<ProjectManager> managers = new ArrayList<>();
        String sql = "SELECT pm.idProjectManager, pm.idLinkedOrganization, pm.firstName, pm.lastNameFather, " +
                "pm.lastNameMother, pm.position, pm.email, pm.phone " +
                "FROM projectmanager pm " +
                "JOIN project p ON pm.idLinkedOrganization = p.idLinkedOrganization " +
                "WHERE p.idProject = ?";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ProjectManager manager = new ProjectManager();
                    manager.setIdProjectManager(rs.getInt("idProjectManager"));
                    manager.setIdLinkedOrganization(rs.getInt("idLinkedOrganization"));
                    manager.setFirstName(rs.getString("firstName"));
                    manager.setLastNameFather(rs.getString("lastNameFather"));
                    manager.setLastNameMother(rs.getString("lastNameMother"));
                    manager.setPosition(rs.getString("position"));
                    manager.setEmail(rs.getString("email"));
                    manager.setPhone(rs.getString("phone"));
                    managers.add(manager);
                }
                System.out.println("Project Managers for project ID " + projectId + ": " + managers.size());
            }
        }
        return managers;
    }

    @Override
    public List<ProjectManager> getProjectManagersByOrganizationId(int organizationId) throws SQLException {
        List<ProjectManager> managers = new ArrayList<>();
        String sql = "SELECT idProjectManager, idLinkedOrganization, firstName, lastNameFather, lastNameMother, position, email, phone " +
                "FROM projectmanager " +
                "WHERE idLinkedOrganization = ?";

        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, organizationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ProjectManager manager = new ProjectManager();
                    manager.setIdProjectManager(rs.getInt("idProjectManager"));
                    manager.setIdLinkedOrganization(rs.getInt("idLinkedOrganization"));
                    manager.setFirstName(rs.getString("firstName"));
                    manager.setLastNameFather(rs.getString("lastNameFather"));
                    manager.setLastNameMother(rs.getString("lastNameMother"));
                    manager.setPosition(rs.getString("position"));
                    manager.setEmail(rs.getString("email"));
                    manager.setPhone(rs.getString("phone"));
                    managers.add(manager);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener responsables de proyecto por ID de organización: " + e.getMessage());
            throw e;
        }
        return managers;
    }
}