package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {
    
    public Connection Conexion() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Usamos la URL externa fija del proxy de Railway
            String url = "jdbc:mysql://sakura.proxy.rlwy.net:24908/railway?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            String user = "railway"; // <--- CAMBIADO DE "root" A "railway"
            String pass = "aSZLfvKfJVnchDkGkHFWxnycCmAIcYJU";
            
            System.out.println("Conectando a traves del proxy externo de Railway...");
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("¡CONEXIÓN EXITOSA A LA BASE DE DATOS!");
        } catch (Exception e) {
            System.err.println("--- ERROR FATAL EN LA CONEXION ---");
            e.printStackTrace();
        }
        return con;
    }
}