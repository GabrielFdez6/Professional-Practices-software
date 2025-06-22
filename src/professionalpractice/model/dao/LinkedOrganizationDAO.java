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

  /**
   * Obtiene todas las organizaciones vinculadas activas, no solo las que tienen
   * proyectos
   * Este método es necesario para el flujo de actualización de responsables
   */
  public List<LinkedOrganization> getAllOrganizations() throws SQLException {
    List<LinkedOrganization> organizations = new ArrayList<>();
    String query = "SELECT idLinkedOrganization, name, address, phone " +
        "FROM linkedorganization " +
        "WHERE isActive = 1 " +
        "ORDER BY name";
    try (Connection connection = ConectionBD.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery()) {
      while (resultSet.next()) {
        LinkedOrganization org = new LinkedOrganization();
        org.setIdLinkedOrganization(resultSet.getInt("idLinkedOrganization"));
        org.setName(resultSet.getString("name"));
        org.setAddress(resultSet.getString("address"));
        org.setPhone(resultSet.getString("phone"));
        org.setActive(true); // Ya que filtramos por isActive = 1
        organizations.add(org);
      }
    }
    return organizations;
  }
}