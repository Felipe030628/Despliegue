package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {
    public Connection Conexion() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Leemos los datos desde las variables de entorno configuradas en Azure App Service
            String host = System.getenv("DB_HOST");     // barstock-bd.mysql.database.azure.com
            String port = System.getenv("DB_PORT");     // 3306
            String dbName = System.getenv("DB_NAME");   // nombre de tu base de datos
            String user = System.getenv("DB_USER");     // barstockadmin
            String pass = System.getenv("DB_PASSWORD"); // tu contraseña

            String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                    + "?useSSL=true&requireSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true";

            System.out.println("Conectando a Azure Database for MySQL...");
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("¡CONEXIÓN EXITOSA A LA BASE DE DATOS!");
        } catch (Exception e) {
            System.err.println("--- ERROR FATAL EN LA CONEXION ---");
            e.printStackTrace();
        }
        return con;
    }
}