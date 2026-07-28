package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    
    public Connection Conexion() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Leemos las variables de entorno de Railway; si no existen, usa los valores por defecto
            String host = System.getenv("MYSQLHOST") != null ? System.getenv("MYSQLHOST") : "sakura.proxy.rlwy.net";
            String port = System.getenv("MYSQLPORT") != null ? System.getenv("MYSQLPORT") : "24908";
            String db = System.getenv("MYSQL_DATABASE") != null ? System.getenv("MYSQL_DATABASE") : "railway";
            String user = System.getenv("MYSQLUSER") != null ? System.getenv("MYSQLUSER") : "root";
            String pass = System.getenv("MYSQLPASSWORD") != null ? System.getenv("MYSQLPASSWORD") : "aSZLfvKfJVnchDkGkHFWxnycCmAICyJU";
            
            // Construimos la URL de conexion dinamica
            String url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false&serverTimezone=UTC";
            
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("¡Conexión exitosa a la base de datos de Railway!");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
        return con;
    }
}