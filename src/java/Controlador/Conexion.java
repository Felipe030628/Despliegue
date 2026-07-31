package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {
    
    public Connection Conexion() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Railway te inyecta la URL completa en esta variable
            String url = System.getenv("DATABASE_URL");
            
            if (url == null || url.isEmpty()) {
                // Respaldo por si lo corres local en tu PC
                url = "jdbc:mysql://localhost:3306/barstock?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            }
            
            System.out.println("Conectando a la base de datos...");
            con = DriverManager.getConnection(url); // La URL de Railway ya trae usuario y contraseña incluidos
            System.out.println("¡CONEXIÓN EXITOSA A LA BASE DE DATOS!");
        } catch (Exception e) {
            System.err.println("--- ERROR FATAL EN LA CONEXION ---");
            e.printStackTrace();
        }
        return con;
    }
}