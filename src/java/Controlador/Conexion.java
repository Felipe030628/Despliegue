package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {
    
    public Connection Conexion() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Datos fijos obtenidos directamente de tu panel de Railway (Servicio MySQL)
            String host = "sakura.proxy.rlwy.net";
            String port = "24908";
            String db   = "railway";
            String user = "root";
            String pass = "aSZLfvKfJVnchDkGkHFWxnycCmAIcYJU";
            
            String url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            
            System.out.println("Intentando conectar a la base de datos...");
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("¡CONEXIÓN EXITOSA A LA BASE DE DATOS!");
        } catch (Exception e) {
            System.err.println("--- ERROR FATAL EN LA CONEXION ---");
            e.printStackTrace();
        }
        return con;
    }
}