package professionalpractice.model.dao.interfaces;

import professionalpractice.model.pojo.UserAccount;

import java.sql.SQLException;

public interface IUserAccountDAO {
    public UserAccount getUserByUsername(String username) throws SQLException;
}