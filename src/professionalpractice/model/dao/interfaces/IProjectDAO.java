package professionalpractice.model.dao.interfaces;

import professionalpractice.model.pojo.Project;
import java.sql.SQLException;
import java.util.List;

public interface IProjectDAO {
    public List<Project> getAllProjects() throws SQLException;
    public int assignProjectToStudent(int idProject, int idStudent, String projectName) throws SQLException;
}