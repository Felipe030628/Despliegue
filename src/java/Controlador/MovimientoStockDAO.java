package Controlador;

import Modelo.MovimientosStock;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MovimientoStockDAO {
    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // LISTAR TODOS LOS MOVIMIENTOS (incluye el nombre del producto vía JOIN)
    public List<MovimientosStock> listarMovimientos() {
        List<MovimientosStock> lista = new ArrayList<>();
        String sql = "SELECT m.*, p.nombre AS nombreProducto "
                + "FROM movimientos_stock m "
                + "LEFT JOIN productos p ON p.idProductos = m.idProducto "
                + "ORDER BY m.idMovimiento DESC";
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
                m.setNombreProducto(rs.getString("nombreProducto"));
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
    
    // Calcula el stock real de cada producto (entradas - salidas, según el texto
    // del campo "motivo") y cuenta cuántos productos están en o por debajo del
    // umbral crítico. Los productos sin movimientos registrados cuentan con
    // stock 0 (también críticos) gracias al LEFT JOIN.
    public int contarProductosCriticos(int umbralMinimo) {
        int total = 0;
        String sql = "SELECT COUNT(*) AS total FROM ( "
                + "  SELECT p.idProductos, "
                + "         COALESCE(SUM(CASE "
                + "             WHEN LOWER(m.motivo) LIKE 'entrada%' THEN m.cantidad "
                + "             WHEN LOWER(m.motivo) LIKE 'salida%' THEN -m.cantidad "
                + "             ELSE 0 END), 0) AS stockActual "
                + "  FROM productos p "
                + "  LEFT JOIN movimientos_stock m ON m.idProducto = p.idProductos "
                + "  GROUP BY p.idProductos "
                + ") t WHERE t.stockActual <= ?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, umbralMinimo);
            rs = ps.executeQuery();
            if (rs.next()) total = rs.getInt("total");
        } catch (SQLException e) {
            System.err.println("Error en contarProductosCriticos: " + e.getMessage());
        } finally {
            cerrarRecursosLocal();
        }
        return total;
    }

    // Top de productos más solicitados según las salidas de stock registradas.
    public List<Map<String, Object>> obtenerTopProductos(int limite) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT p.nombre AS nombre, p.precio AS precio, SUM(m.cantidad) AS totalSalidas "
                + "FROM movimientos_stock m "
                + "JOIN productos p ON p.idProductos = m.idProducto "
                + "WHERE LOWER(m.motivo) LIKE 'salida%' "
                + "GROUP BY p.idProductos, p.nombre, p.precio "
                + "ORDER BY totalSalidas DESC "
                + "LIMIT ?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, limite);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> fila = new LinkedHashMap<>();
                fila.put("nombre", rs.getString("nombre"));
                fila.put("cantidad", rs.getInt("totalSalidas"));
                fila.put("monto", rs.getInt("totalSalidas") * rs.getDouble("precio"));
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerTopProductos: " + e.getMessage());
        } finally {
            cerrarRecursosLocal();
        }
        return lista;
    }

    // Estos dos métodos nuevos abren su propia conexión con executeQuery de agregación
    // (no reutilizan listarMovimientos), por lo que cierran recursos igual que el resto del DAO.
    private void cerrarRecursosLocal() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (Exception e) {
            System.out.println("Error al cerrar recursos: " + e.getMessage());
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