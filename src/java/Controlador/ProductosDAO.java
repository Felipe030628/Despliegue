package Controlador;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    // Distribución real de productos por categoría (para la dona y las barras del Panel)
    // Devuelve una lista ordenada de mayor a menor con el nombre de la categoría y
    // cuántos productos tiene registrados esa categoría en este momento.
    public List<Map<String, Object>> obtenerDistribucionCategorias() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT c.nombre_categoria AS categoria, COUNT(p.idProductos) AS total "
                + "FROM categorias c "
                + "LEFT JOIN productos p ON p.categorias_idCategorias = c.idCategorias "
                + "GROUP BY c.idCategorias, c.nombre_categoria "
                + "ORDER BY total DESC";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> fila = new LinkedHashMap<>();
                fila.put("categoria", rs.getString("categoria"));
                fila.put("total", rs.getInt("total"));
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerDistribucionCategorias: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return lista;
    }

    // 1b. LISTAR PRODUCTOS CON SU STOCK ACTUAL (para el selector de Pedidos)
    // El stock no se guarda en una columna: se calcula sumando entradas y restando
    // salidas registradas en movimientos_stock, igual que en contarProductosCriticos().
    public List<Productos> listarProductosConStock() {
        List<Productos> lista = new ArrayList<>();
        String sql = "SELECT p.idProductos, p.nombre, p.precio, p.categorias_idCategorias, "
                + "COALESCE(SUM(CASE "
                + "    WHEN LOWER(m.motivo) LIKE 'entrada%' THEN m.cantidad "
                + "    WHEN LOWER(m.motivo) LIKE 'salida%' THEN -m.cantidad "
                + "    ELSE 0 END), 0) AS stock "
                + "FROM productos p "
                + "LEFT JOIN movimientos_stock m ON m.idProducto = p.idProductos "
                + "GROUP BY p.idProductos, p.nombre, p.precio, p.categorias_idCategorias "
                + "ORDER BY p.nombre";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Productos p = new Productos();
                p.setId(rs.getInt("idProductos"));
                p.setNombre(rs.getString("nombre"));
                p.setPrecio(rs.getDouble("precio"));
                p.setIdCategoria(rs.getInt("categorias_idCategorias"));
                p.setStock(rs.getInt("stock"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error en listarProductosConStock: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return lista;
    }

    // Stock actual de un solo producto (entradas - salidas)
    public int obtenerStock(int idProducto) {
        int stock = 0;
        String sql = "SELECT COALESCE(SUM(CASE "
                + "    WHEN LOWER(motivo) LIKE 'entrada%' THEN cantidad "
                + "    WHEN LOWER(motivo) LIKE 'salida%' THEN -cantidad "
                + "    ELSE 0 END), 0) AS stock "
                + "FROM movimientos_stock WHERE idProducto = ?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idProducto);
            rs = ps.executeQuery();
            if (rs.next()) stock = rs.getInt("stock");
        } catch (SQLException e) {
            System.err.println("Error en obtenerStock: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return stock;
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

    // 13. Buscar productos por nombre (barra de búsqueda global del Panel)
    public List<Productos> buscarPorNombre(String termino, int limite) {
        List<Productos> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE nombre LIKE ? ORDER BY nombre LIMIT ?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + termino + "%");
            ps.setInt(2, limite);
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
            System.err.println("Error en buscarPorNombre: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return lista;
    }
}