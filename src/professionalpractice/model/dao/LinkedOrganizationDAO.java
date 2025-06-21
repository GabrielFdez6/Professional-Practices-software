package professionalpractice.model.dao;

import professionalpractice.model.ConectionBD;
import professionalpractice.model.dao.interfaces.ILinkedOrganizationDAO;
import professionalpractice.model.pojo.LinkedOrganization;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LinkedOrganizationDAO implements ILinkedOrganizationDAO {
    @Override
    public List<LinkedOrganization> getAllActiveOrganizations() throws SQLException {
        List<LinkedOrganization> organizations = new ArrayList<>();
        String query = "SELECT DISTINCT lo.idLinkedOrganization, lo.name, lo.address " +
                "FROM linkedorganization lo " +
                "INNER JOIN projectmanager pm ON lo.idLinkedOrganization = pm.idLinkedOrganization " +
                "WHERE lo.isActive = 1";
        try (Connection connection = ConectionBD.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                LinkedOrganization org = new LinkedOrganization();
                org.setIdLinkedOrganization(resultSet.getInt("idLinkedOrganization"));
                org.setName(resultSet.getString("name"));
                org.setAddress(resultSet.getString("address"));
                organizations.add(org);
            }
        }
        return organizations;
    }
}