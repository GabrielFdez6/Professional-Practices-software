package professionalpractice.model.dao.interfaces;

import java.sql.SQLException;
import professionalpractice.model.pojo.Coordinator;

public interface ICoordinatorDAO {
    public Coordinator getCoordinatorByIdUser(int idUser) throws SQLException;
}