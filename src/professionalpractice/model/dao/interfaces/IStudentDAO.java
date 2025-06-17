package professionalpractice.model.dao.interfaces;

import professionalpractice.model.pojo.Student;
import professionalpractice.model.pojo.StudentProgress;

import java.sql.SQLException;

public interface IStudentDAO {

    public StudentProgress getStudentProgress(int idStudent) throws SQLException;
    Student getStudentByUserId(int userId) throws SQLException;

}