package professionalpractice.model.dao.interfaces;

import professionalpractice.model.pojo.ProjectManager;
import java.sql.SQLException;
import java.util.List;

public interface IProjectManagerDAO {
    List<ProjectManager> getAllProjectManagers() throws SQLException;
    int registerProjectManager(ProjectManager manager) throws SQLException;
    int updateProjectManager(ProjectManager manager) throws SQLException;
    List<ProjectManager> getAllProjectManagersByProjectId(int projectId) throws SQLException;
    List<ProjectManager> getProjectManagersByOrganizationId(int organizationId) throws SQLException;
}