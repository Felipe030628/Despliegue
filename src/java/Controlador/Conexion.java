package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {
    
    public Connection Conexion() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Capturamos las variables de entorno oficiales de Railway
            String host = System.getenv("MYSQLHOST");
            String port = System.getenv("MYSQLPORT");
            String db   = System.getenv("MYSQLDATABASE");
            String user = System.getenv("MYSQLUSER");
            String pass = System.getenv("MYSQLPASSWORD");
            
            // Si por alguna razón alguna es null (corriendo local), ponemos respaldos
            if (host == null) host = "localhost";
            if (port == null) port = "3306";
            if (db == null) db = "barstock";
            if (user == null) user = "root";
            if (pass == null) pass = "";
            
            String url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            
            System.out.println("Intentando conectar a: " + host + ":" + port + " con usuario: " + user);
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("¡CONEXIÓN EXITOSA A LA BASE DE DATOS!");
        } catch (Exception e) {
            System.err.println("--- ERROR FATAL EN LA CONEXION ---");
            e.printStackTrace();
        }
        return con;
    }
}