package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {
    
    public Connection Conexion() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Usamos directamente los datos fijos de la red interna de Railway
            String host = "mysql.railway.internal";
            String port = "3306";
            String db = "railway";
            String user = "root";
            String pass = "aSZLfvKfJVnchDkGkHFWxnycCmAICyJU";
            
            String url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            
            System.out.println("Intentando conectar a MySQL interno...");
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("¡CONEXIÓN EXITOSA A LA BASE DE DATOS DE RAILWAY!");
        } catch (Exception e) {
            System.err.println("--- ERROR FATAL EN LA CONEXION ---");
            e.printStackTrace();
        }
        return con;
    }
}