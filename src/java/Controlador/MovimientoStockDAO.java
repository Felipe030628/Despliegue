package Controlador;

import Modelo.MovimientosStock;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimientoStockDAO {
    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // LISTAR TODOS LOS MOVIMIENTOS
    public List<MovimientosStock> listarMovimientos() {
        List<MovimientosStock> lista = new ArrayList<>();
        String sql = "SELECT * FROM movimientos_stock";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                MovimientosStock m = new MovimientosStock();
                m.setIdMovimiento(rs.getInt("idMovimiento"));
                m.setFecha(rs.getString("fecha"));
                m.setCantidad(rs.getInt("cantidad"));
                m.setMotivo(rs.getString("motivo"));
                m.setIdProducto(rs.getInt("idProducto"));
                lista.add(m);
            }
            System.out.println("DAO: Se cargaron " + lista.size() + " registros.");
        } catch (SQLException e) {
            System.err.println("DAO Error: " + e.getMessage());
        }
        return lista;
    }

    // REGISTRAR NUEVO MOVIMIENTO
    public void registrarMovimiento(MovimientosStock m) {
        String sql = "INSERT INTO movimientos_stock (fecha, cantidad, motivo, idProducto) VALUES (?,?,?,?)";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, m.getFecha());
            ps.setInt(2, m.getCantidad());
            ps.setString(3, m.getMotivo());
            ps.setInt(4, m.getIdProducto());
            ps.executeUpdate();
        } catch (SQLException e) { 
            System.err.println("Error registrar: " + e); 
        }
    }

    // BUSCAR POR ID (Para cargar los datos en el formulario de edición)
    public MovimientosStock listarPorId(int id) {
        MovimientosStock m = new MovimientosStock();
        String sql = "SELECT * FROM movimientos_stock WHERE idMovimiento = ?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                m.setIdMovimiento(rs.getInt("idMovimiento"));
                m.setFecha(rs.getString("fecha"));
                m.setCantidad(rs.getInt("cantidad"));
                m.setMotivo(rs.getString("motivo"));
                m.setIdProducto(rs.getInt("idProducto"));
            }
        } catch (SQLException e) {
            System.err.println("Error listarPorId: " + e);
        }
        return m;
    }

    // ACTUALIZAR MOVIMIENTO EXISTENTE
    public void actualizarMovimiento(MovimientosStock m) {
        String sql = "UPDATE movimientos_stock SET fecha = ?, cantidad = ?, motivo = ?, idProducto = ? WHERE idMovimiento = ?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, m.getFecha());
            ps.setInt(2, m.getCantidad());
            ps.setString(3, m.getMotivo());
            ps.setInt(4, m.getIdProducto());
            ps.setInt(5, m.getIdMovimiento());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error actualizar: " + e);
        }
    }
    
    public void eliminarMovimiento(int idMovimiento) {
    String sql = "DELETE FROM movimientos_stock WHERE idMovimiento = ?";
    try {
        con = cn.Conexion();
        ps = con.prepareStatement(sql);
        ps.setInt(1, idMovimiento);
        ps.executeUpdate();
    } catch (Exception e) {
        System.out.println("❌ Error al eliminar movimiento: " + e.getMessage());
    } finally {
        try {
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException ex) {
            System.out.println("Error al cerrar recursos: " + ex.getMessage());
        }
    }
}
}