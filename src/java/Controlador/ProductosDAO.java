package Controlador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Modelo.Productos;

public class ProductosDAO {
    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // 1. LISTAR PRODUCTOS
    public List<Productos> listarProductos() {
        List<Productos> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Productos p = new Productos();
                p.setId(rs.getInt("idProductos"));
                p.setNombre(rs.getString("nombre"));
                p.setPrecio(rs.getDouble("precio"));
                p.setFecha_vencimiento(rs.getString("fecha_vencimiento"));
                p.setIdCategoria(rs.getInt("categorias_idCategorias"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return lista;
    }

    // 2. REGISTRAR PRODUCTO
    public void registrarProducto(Productos p) {
        String sql = "INSERT INTO productos (nombre, precio, fecha_vencimiento, categorias_idCategorias) VALUES (?,?,?,?)";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setString(3, p.getFecha_vencimiento());
            ps.setInt(4, p.getIdCategoria());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al registrar: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // 3. ACTUALIZAR PRODUCTO
    public void actualizarProducto(Productos p) {
        String sql = "UPDATE productos SET nombre=?, precio=?, fecha_vencimiento=?, categorias_idCategorias=? WHERE idProductos=?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setString(3, p.getFecha_vencimiento());
            ps.setInt(4, p.getIdCategoria());
            ps.setInt(5, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // 4. ELIMINAR PRODUCTO (Corregido a idProductos para que coincida con tu BD)
    public void eliminarProducto(int id) {
        String sql = "DELETE FROM productos WHERE idProductos = ?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("❌ Error al eliminar producto: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // Método para contar productos que están en stock crítico
    public int contarProductosCriticos() {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM productos"; 
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) total = rs.getInt(1);
            System.out.println("DEBUG: Total productos encontrados: " + total);
        } catch (Exception e) { 
            System.out.println("Error: " + e.getMessage()); 
        } finally { 
            cerrarRecursos(); 
        }
        return total;
    }

    // Método para contar la variedad de marcas distintas en tu inventario
    public int contarTotalMarcas() {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM productos"; 
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error en contarTotalMarcas: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return total;
    }

    // Método para cerrar recursos de forma segura
    private void cerrarRecursos() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (Exception e) {
            System.out.println("Error al cerrar recursos: " + e.getMessage());
        }
    }
    
    public Productos listarPorId(int id) {
        Productos p = new Productos();
        String sql = "SELECT * FROM productos WHERE idProductos = ?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                p.setId(rs.getInt("idProductos"));
                p.setNombre(rs.getString("nombre"));
                p.setPrecio(rs.getDouble("precio"));
                p.setFecha_vencimiento(rs.getString("fecha_vencimiento"));
                p.setIdCategoria(rs.getInt("categorias_idCategorias"));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar por ID: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return p;
    }
}