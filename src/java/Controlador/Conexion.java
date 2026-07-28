package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    
    public Connection Conexion() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Usamos la conexion interna de Railway si esta disponible, o la externa de respaldo
            String host = System.getenv("MYSQLHOST") != null ? System.getenv("MYSQLHOST") : "sakura.proxy.rlwy.net";
            String port = System.getenv("MYSQLPORT") != null ? System.getenv("MYSQLPORT") : "24908";
            String db = System.getenv("MYSQL_DATABASE") != null ? System.getenv("MYSQL_DATABASE") : "railway";
            String user = System.getenv("MYSQLUSER") != null ? System.getenv("MYSQLUSER") : "root";
            String pass = System.getenv("MYSQLPASSWORD") != null ? System.getenv("MYSQLPASSWORD") : "aSZLfvKfJVnchDkGkHFWxnycCmAICyJU";
            
            String url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false&serverTimezone=UTC";
            
            System.out.println("Intentando conectar a: " + host + ":" + port + " con usuario " + user);
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("¡Conexión exitosa a la base de datos de Railway!");
        } catch (Exception e) {
            // AQUÍ IMPRIMIMOS EL ERROR REAL EN LA CONSOLA DE RAILWAY
            System.err.println("--- ERROR CRITICO DE CONEXION ---");
            e.printStackTrace();
        }
        return con;
    }
}