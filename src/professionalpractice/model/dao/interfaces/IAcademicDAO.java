package professionalpractice.model.dao.interfaces;

import java.sql.SQLException;
import professionalpractice.model.pojo.Academic;

/**
 * Define las operaciones de base de datos para la entidad Academic.
 */
public interface IAcademicDAO {

    /**
     * Busca y devuelve un objeto Academic basado en el ID de su cuenta de usuario.
     * @param userId El ID de la tabla useraccount.
     * @return El objeto Academic correspondiente o null si no se encuentra.
     * @throws SQLException Si ocurre un error de conexión o consulta.
     */
    public Academic getAcademicByUserId(int userId) throws SQLException;

}