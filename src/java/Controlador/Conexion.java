package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Configuración por variables de entorno con valores por defecto
    private final String host = getEnvOrDefault("DB_HOST", "localhost");
    private final String port = getEnvOrDefault("DB_PORT", "3306");
    private final String dbName = getEnvOrDefault("DB_NAME", "");
    private final String user = getEnvOrDefault("DB_USER", "root");
    private final String pass = getEnvOrDefault("DB_PASSWORD", "");

    private final String url = String.format(
        "jdbc:mysql://%s:%s/%s?useSSL=true&requireSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true",
        host, port, dbName
    );

    /**
     * Establece y retorna una nueva conexión a la base de datos.
     * 
     * @return Connection objeto de conexión o null si ocurre un fallo.
     */
    public Connection Conexion() {
        Connection con = null;
        try {
            Class.forName(DRIVER);
            con = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al establecer la conexión JDBC: " + e.getMessage());
        }
        return con;
    }

    private String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }
}