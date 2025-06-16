// Ubicación: src/professionalpractice/model/ConectionBD.java
package professionalpractice.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConectionBD {

    private static final String HOST = "localhost:3306";
    private static final String DB = "professionalpractices";
    private static final String USER = "root";
    private static final String PASSWORD = "Monte1324.";

    private static Connection conn;

    // El constructor privado previene la instanciación desde otras clases (Singleton)
    private ConectionBD() {}

    /**
     * Devuelve la instancia única de la conexión. Si no existe o está cerrada,
     * crea una nueva.
     * @return La conexión a la base de datos.
     * @throws SQLException si ocurre un error al conectar.
     */
    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            try {
                // La carga del driver con Class.forName ya no es necesaria en JDBC 4.0+
                String url = "jdbc:mysql://" + HOST + "/" + DB+"?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
                conn = DriverManager.getConnection(url, USER, PASSWORD);
                System.out.println("Conexion a BD establecida");
            } catch (SQLException e) {
                // Relanzamos la excepción para que la capa superior la maneje.
                System.err.println("Error de conexión a la BD: " + e.getMessage());
                throw e;
            }
        }
        return conn;
    }

    /**
     * Cierra la conexión a la base de datos al final de la vida de la aplicación.
     */
    public static void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Conexión a BD cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}