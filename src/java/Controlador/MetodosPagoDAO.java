package Controlador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Modelo.MetodosPago;

public class MetodosPagoDAO {

    private String url = "jdbc:mysql://localhost:3307/barstock";
    private String user = "root";
    private String pass = "";

    // INSERTAR
    public void insertar(MetodosPago m) {
        String sql = "INSERT INTO metodos_pago (descripcion_Metodo) VALUES (?)";
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, m.descripcion_Metodo);
            ps.executeUpdate();
            
            System.out.println("Metodo de pago insertado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CONSULTAR (Individual)
    public MetodosPago consultar(int id) {
        MetodosPago m = new MetodosPago();
        String sql = "SELECT * FROM metodos_pago WHERE idMetodos_Pago = ?";
        
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    m.idMetodos_Pago = rs.getInt("idMetodos_Pago");
                    m.descripcion_Metodo = rs.getString("descripcion_Metodo");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    // LISTAR (Todos los métodos de pago)
    public List<MetodosPago> listar() {
        List<MetodosPago> lista = new ArrayList<>();
        String sql = "SELECT * FROM metodos_pago";

        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MetodosPago m = new MetodosPago();
                m.idMetodos_Pago = rs.getInt("idMetodos_Pago");
                m.descripcion_Metodo = rs.getString("descripcion_Metodo");
                lista.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ACTUALIZAR
    public void actualizar(MetodosPago m) {
        String sql = "UPDATE metodos_pago SET descripcion_Metodo = ? WHERE idMetodos_Pago = ?";
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, m.descripcion_Metodo);
            ps.setInt(2, m.idMetodos_Pago);

            ps.executeUpdate();
            System.out.println("Metodo de pago actualizado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ELIMINAR
    public void eliminar(int id) {
        String sql = "DELETE FROM metodos_pago WHERE idMetodos_Pago = ?";
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Metodo de pago eliminado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}