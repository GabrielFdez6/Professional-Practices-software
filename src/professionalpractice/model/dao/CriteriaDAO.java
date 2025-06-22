package professionalpractice.model.dao;

import professionalpractice.model.ConectionBD;
import professionalpractice.model.dao.interfaces.ICriteriaDAO;
import professionalpractice.model.pojo.Criteria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CriteriaDAO implements ICriteriaDAO {
    @Override
    public List<Criteria> getAllCriteria() throws SQLException {
        List<Criteria> criteriaList = new ArrayList<>();
        String query = "SELECT idCriteria, criteriaName, competent, independent, advancedBasic, thresholdBasic, notCompetent FROM evaluationcriteria";
        try (Connection conn = ConectionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Criteria criteria = new Criteria();
                criteria.setIdCriteria(rs.getInt("idCriteria"));
                criteria.setCriteriaName(rs.getString("criteriaName"));
                criteria.setCompetent(rs.getString("competent"));
                criteria.setIndependent(rs.getString("independent"));
                criteria.setAdvancedBasic(rs.getString("advancedBasic"));
                criteria.setThresholdBasic(rs.getString("thresholdBasic"));
                criteria.setNotCompetent(rs.getString("notCompetent"));
                criteriaList.add(criteria);
            }
        }
        return criteriaList;
    }
}