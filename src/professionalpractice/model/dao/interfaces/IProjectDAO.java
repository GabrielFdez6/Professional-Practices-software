package professionalpractice.model.dao.interfaces;

import professionalpractice.model.pojo.Project;
import java.sql.SQLException;
import java.util.List; // Importar List

public interface IProjectDAO {
    public List<Project> getAllProjects() throws SQLException;

    public int assignProjectToStudent(int idProject, int idStudent, String projectName) throws SQLException;

    public int saveProject(Project project) throws SQLException;

    public int updateProject(Project project) throws SQLException;

    public List<Project> getProjectsByProjectManagerId(int projectManagerId) throws SQLException;

    public Project getProjectById(int projectId) throws SQLException;

    public List<Project> getAvailableProjects() throws SQLException;

    public int deleteProject(int projectId) throws SQLException;
}