// Ubicación: src/professionalpractice/model/ConectionBD.java
package professionalpractice.model;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConectionBD {

    private static Connection conn;

    private ConectionBD() {}

    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            try (InputStream input = ConectionBD.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    throw new SQLException("No se encontró el archivo config.properties.");
                }
                Properties prop = new Properties();
                prop.load(input);

                String url = prop.getProperty("db.url");
                String rol = SesionUsuario.getInstancia().getRolUsuario();

                if (rol == null || rol.isEmpty()) {
                    throw new SQLException("No se puede abrir una conexión principal sin un rol de usuario válido.");
                }

                String userKey = "db.user." + rol;
                String passKey = "db.password." + rol;

                String usuarioDB = prop.getProperty(userKey);
                String passwordDB = prop.getProperty(passKey);

                if (usuarioDB == null || passwordDB == null) {
                    throw new SQLException("No se encontraron las credenciales para el rol: " + rol);
                }

                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(url, usuarioDB, passwordDB);
                System.out.println("Conexión principal establecida para el rol: " + rol);

            } catch (ClassNotFoundException | IOException e) {
                throw new SQLException("Error en la configuración de la conexión: " + e.getMessage());
            }
        }
        return conn;
    }

    public static Connection getLoginConnection() throws SQLException {
        try (InputStream input = ConectionBD.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new SQLException("No se encontró el archivo config.properties.");
            }
            Properties prop = new Properties();
            prop.load(input);

            String url = prop.getProperty("db.url");
            String loginUser = prop.getProperty("db.user.login_checker");
            String loginPass = prop.getProperty("db.password.login_checker");

            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, loginUser, loginPass);
        } catch (ClassNotFoundException | IOException e) {
            throw new SQLException("Error en la conexión del login checker: " + e.getMessage());
        }
    }

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