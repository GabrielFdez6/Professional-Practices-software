package professionalpractice.model.dao.interfaces;

import professionalpractice.model.pojo.DeliveryInfo;
import professionalpractice.model.pojo.Student;
import professionalpractice.model.pojo.StudentProgress;

import java.sql.SQLException;
import java.util.ArrayList;

public interface IStudentDAO {

    StudentProgress getStudentProgress(int idStudent) throws SQLException;
    Student getStudentByUserId(int userId) throws SQLException;
    ArrayList<DeliveryInfo> getStudentDeliveries(int idStudent) throws SQLException;

}