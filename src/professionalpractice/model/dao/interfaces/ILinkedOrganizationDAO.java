package professionalpractice.model.dao.interfaces;

import professionalpractice.model.pojo.LinkedOrganization;
import java.sql.SQLException;
import java.util.List;

public interface ILinkedOrganizationDAO {
    List<LinkedOrganization> getAllActiveOrganizations() throws SQLException;
}