package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {
    Connection con;
    // Datos de tu base de datos (asegúrate de que el nombre sea bd_barstock o el tuyo)
    String url = "jdbc:mysql://localhost:3307/Barstock"; 
    String user = "root";
    String pass = ""; // Tu contraseña de MySQL

    public Connection Conexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            System.err.println("Error de conexión: " + e);
        }
        return con;
    }
}
