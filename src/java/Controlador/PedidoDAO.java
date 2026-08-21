package Controlador;

import Modelo.Pedidos;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PedidoDAO {
    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    public List<Pedidos> listarPedidos() {
        List<Pedidos> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedidos";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Pedidos p = new Pedidos();
                p.setIdPedido(rs.getInt("idPedido"));
                p.setCliente(rs.getString("cliente"));
                p.setMesa(rs.getString("mesa"));
                p.setFecha(rs.getString("fecha"));
                p.setEstado(rs.getString("estado"));
                p.setTotal(rs.getDouble("total"));
                lista.add(p);
            }
        } catch (SQLException e) { 
            System.out.println("Error listar: " + e); 
        } finally { 
            cerrarRecursos(); 
        }
        return lista;
    }

    public void registrarPedido(Pedidos p) {
        String sql = "INSERT INTO pedidos (cliente, mesa, fecha, estado, total) VALUES (?,?,?,?,?)";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getCliente());
            ps.setString(2, p.getMesa());
            ps.setString(3, p.getFecha());
            ps.setString(4, p.getEstado());
            ps.setDouble(5, p.getTotal());
            ps.executeUpdate();
        } catch (SQLException e) { 
            System.out.println("Error registrar: " + e); 
        } finally { 
            cerrarRecursos(); 
        }
    }

    public double obtenerTotalCajaHoy() {
        double total = 0;
        String sql = "SELECT SUM(total) FROM pedidos";  
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
                System.out.println("DEBUG: El total sin filtrar es: " + total);
            }
        } catch (Exception e) {
            System.out.println("Error en obtenerTotalCajaHoy: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return total;
    }

    // Suma real de ventas (total de pedidos) agrupada por día, para los últimos 7 días.
    // La columna "fecha" se guarda como texto en formato datetime-local ("yyyy-MM-ddTHH:mm"),
    // por eso se convierte explícitamente con STR_TO_DATE antes de agrupar/filtrar.
    // La clave del mapa es la fecha en formato "yyyy-MM-dd".
    public Map<String, Double> obtenerVentasUltimos7Dias() {
        Map<String, Double> ventasPorDia = new LinkedHashMap<>();
        String sql = "SELECT DATE(STR_TO_DATE(fecha, '%Y-%m-%dT%H:%i')) AS dia, SUM(total) AS totalDia "
                + "FROM pedidos "
                + "WHERE STR_TO_DATE(fecha, '%Y-%m-%dT%H:%i') >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) "
                + "GROUP BY dia "
                + "ORDER BY dia ASC";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                String dia = rs.getString("dia");
                if (dia != null) {
                    ventasPorDia.put(dia, rs.getDouble("totalDia"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en obtenerVentasUltimos7Dias: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return ventasPorDia;
    }

    public Pedidos listarPorId(int id) {
        Pedidos p = null;
        String sql = "SELECT * FROM pedidos WHERE idPedido = ?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                p = new Pedidos();
                p.setIdPedido(rs.getInt("idPedido"));
                p.setCliente(rs.getString("cliente"));
                p.setMesa(rs.getString("mesa"));
                p.setFecha(rs.getString("fecha"));
                p.setEstado(rs.getString("estado"));
                p.setTotal(rs.getDouble("total"));
            }
        } catch (SQLException e) {
            System.out.println("Error listarPorId: " + e);
        } finally {
            cerrarRecursos();
        }
        return p;
    }

    public void actualizarPedido(Pedidos p) {
        String sql = "UPDATE pedidos SET cliente = ?, mesa = ?, fecha = ?, estado = ?, total = ? WHERE idPedido = ?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getCliente());
            ps.setString(2, p.getMesa());
            ps.setString(3, p.getFecha());
            ps.setString(4, p.getEstado());
            ps.setDouble(5, p.getTotal());
            ps.setInt(6, p.getIdPedido());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar pedido: " + e);
        } finally {
            cerrarRecursos();
        }
    }

    private void cerrarRecursos() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (Exception e) { 
            System.out.println("Error al cerrar: " + e.getMessage()); 
        }
    }
    
    public void eliminarPedido(int idPedido) {
    String sql = "DELETE FROM pedidos WHERE idPedido = ?";
    try {
        con = cn.Conexion();
        ps = con.prepareStatement(sql);
        ps.setInt(1, idPedido);
        ps.executeUpdate();
    } catch (Exception e) {
        System.out.println("❌ Error al eliminar pedido: " + e.getMessage());
    } finally {
        cerrarRecursos();
    }
}
}