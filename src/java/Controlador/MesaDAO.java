package Controlador;

import Modelo.Mesa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MesaDAO {
    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    public List<Mesa> listarMesas() {
        List<Mesa> lista = new ArrayList<>();
        String sql = "SELECT * FROM mesa";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Mesa m = new Mesa();
                m.setIdMesa(rs.getInt("idMesa"));
                m.setNumero_mesa(rs.getString("numero_mesa"));
                m.setEstado(rs.getString("estado"));
                m.setCapacidad(rs.getInt("capacidad"));
                lista.add(m);
            }
        } catch (SQLException e) { 
            System.out.println("Error listar mesas: " + e); 
        } finally { 
            cerrarRecursos(); 
        }
        return lista;
    }
    
    public void registrarMesa(Mesa m) {
        String sql = "INSERT INTO mesa (numero_mesa, estado, capacidad) VALUES (?, ?, ?)";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, m.getNumero_mesa());
            ps.setString(2, m.getEstado());
            ps.setInt(3, m.getCapacidad());
            ps.executeUpdate();
        } catch (Exception e) { 
            System.out.println("Error al registrar mesa: " + e); 
        } finally { 
            cerrarRecursos(); 
        }
    }

    // --- NUEVO: Buscar mesa por ID para cargarla en la vista de edición ---
    public Mesa listarPorId(int id) {
        Mesa m = null;
        String sql = "SELECT * FROM mesa WHERE idMesa = ?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                m = new Mesa();
                m.setIdMesa(rs.getInt("idMesa"));
                m.setNumero_mesa(rs.getString("numero_mesa"));
                m.setEstado(rs.getString("estado"));
                m.setCapacidad(rs.getInt("capacidad"));
            }
        } catch (SQLException e) {
            System.out.println("Error listarPorId mesa: " + e);
        } finally {
            cerrarRecursos();
        }
        return m;
    }

    // --- NUEVO: Actualizar los datos de la mesa en la base de datos ---
    public void actualizarMesa(Mesa m) {
    String sql = "UPDATE mesa SET numero_mesa = ?, capacidad = ?, estado = ? WHERE idMesa = ?";
    try {
        con = cn.Conexion();
        ps = con.prepareStatement(sql);
        ps.setString(1, m.getNumero_mesa());
        ps.setInt(2, m.getCapacidad());
        ps.setString(3, m.getEstado());
        ps.setInt(4, m.getIdMesa());
        ps.executeUpdate();
    } catch (SQLException e) {
        System.out.println("Error actualizar mesa: " + e);
    } finally {
        cerrarRecursos();
    }
}

    // Método auxiliar para liberar recursos de BD de forma segura
    private void cerrarRecursos() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (Exception e) { 
            System.out.println("Error al cerrar recursos: " + e.getMessage()); 
        }
    }
    
    public void actualizarEstadoPorNumero(String numeroMesa, String nuevoEstado) {
        String sql = "UPDATE mesa SET estado = ? WHERE numero_mesa = ?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, nuevoEstado);
            ps.setString(2, numeroMesa);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al cambiar estado de mesa por número: " + e);
        } finally {
            cerrarRecursos();
        }
    }
    
    public void eliminarMesa(int idMesa) {
    String sql = "DELETE FROM mesa WHERE idMesa = ?"; // Ajusta el nombre de la tabla o columna según tu base de datos
    try {
        con = cn.Conexion();
        ps = con.prepareStatement(sql);
        ps.setInt(1, idMesa);
        ps.executeUpdate();
    } catch (Exception e) {
        System.out.println("❌ Error al eliminar mesa: " + e.getMessage());
    } finally {
        cerrarRecursos();
    }
}
}