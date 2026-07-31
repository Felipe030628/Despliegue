package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {
    
    public Connection Conexion() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Leemos las variables que Railway le pasa internamente al contenedor
            String host = System.getenv("MYSQLHOST") != null ? System.getenv("MYSQLHOST") : "mysql.railway.internal";
            String port = System.getenv("MYSQLPORT") != null ? System.getenv("MYSQLPORT") : "3306";
            String db   = System.getenv("MYSQLDATABASE") != null ? System.getenv("MYSQLDATABASE") : "railway";
            String user = System.getenv("MYSQLUSER") != null ? System.getenv("MYSQLUSER") : "root";
            String pass = System.getenv("MYSQLPASSWORD") != null ? System.getenv("MYSQLPASSWORD") : "aSZLfvKfJVnchDkGkHFWxnycCmAIcYJU";
            
            String url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            
            System.out.println("Conectando a la base de datos en: " + host);
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("¡CONEXIÓN EXITOSA A LA BASE DE DATOS!");
        } catch (Exception e) {
            System.err.println("--- ERROR FATAL EN LA CONEXION ---");
            e.printStackTrace();
        }
        return con;
    }
}