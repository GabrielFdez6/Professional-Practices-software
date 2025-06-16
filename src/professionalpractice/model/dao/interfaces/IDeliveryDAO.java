package professionalpractice.model.dao.interfaces;

import professionalpractice.model.pojo.Delivery;
import java.sql.SQLException;
import java.util.List;

public interface IDeliveryDAO {

    int scheduleDelivery(Delivery delivery) throws SQLException;

    List<Delivery> getDeliveriesByRecord(int idRecord) throws SQLException;

    int scheduleDeliveryForAllRecords(Delivery delivery) throws SQLException;
}