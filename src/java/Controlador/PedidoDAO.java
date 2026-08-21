package Controlador;

import Modelo.DetallePedido;
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

    // Registra el pedido, guarda cada producto pedido en detalle_pedido y genera
    // automáticamente el movimiento de stock (salida) correspondiente a cada línea.
    // Todo ocurre en una sola transacción: si algo falla no se descuenta nada.
    // Devuelve el idPedido generado, o -1 si falló.
    public int registrarPedidoConDetalle(Pedidos p, List<DetallePedido> detalles) {
        int idPedidoGenerado = -1;
        String sqlPedido = "INSERT INTO pedidos (cliente, mesa, fecha, estado, total) VALUES (?,?,?,?,?)";
        String sqlDetalle = "INSERT INTO detalle_pedido (idPedido, idProducto, cantidad, precio_unitario, subtotal) VALUES (?,?,?,?,?)";
        String sqlMovimiento = "INSERT INTO movimientos_stock (fecha, cantidad, motivo, idProducto) VALUES (?,?,?,?)";

        Connection conTx = null;
        PreparedStatement psPedido = null;
        PreparedStatement psDetalle = null;
        PreparedStatement psMov = null;
        ResultSet keys = null;

        try {
            conTx = cn.Conexion();
            conTx.setAutoCommit(false);

            psPedido = conTx.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            psPedido.setString(1, p.getCliente());
            psPedido.setString(2, p.getMesa());
            psPedido.setString(3, p.getFecha());
            psPedido.setString(4, p.getEstado());
            psPedido.setDouble(5, p.getTotal());
            psPedido.executeUpdate();

            keys = psPedido.getGeneratedKeys();
            if (keys.next()) {
                idPedidoGenerado = keys.getInt(1);
            }

            if (idPedidoGenerado > 0 && detalles != null && !detalles.isEmpty()) {
                psDetalle = conTx.prepareStatement(sqlDetalle);
                psMov = conTx.prepareStatement(sqlMovimiento);

                for (DetallePedido d : detalles) {
                    psDetalle.setInt(1, idPedidoGenerado);
                    psDetalle.setInt(2, d.getIdProducto());
                    psDetalle.setInt(3, d.getCantidad());
                    psDetalle.setDouble(4, d.getPrecioUnitario());
                    psDetalle.setDouble(5, d.getSubtotal());
                    psDetalle.addBatch();

                    psMov.setString(1, p.getFecha());
                    psMov.setInt(2, d.getCantidad());
                    psMov.setString(3, "Salida - Pedido #" + idPedidoGenerado);
                    psMov.setInt(4, d.getIdProducto());
                    psMov.addBatch();
                }
                psDetalle.executeBatch();
                psMov.executeBatch();
            }

            conTx.commit();
        } catch (SQLException e) {
            System.out.println("Error registrar pedido con detalle: " + e);
            idPedidoGenerado = -1;
            try {
                if (conTx != null) conTx.rollback();
            } catch (SQLException ex) {
                System.out.println("Error en rollback: " + ex.getMessage());
            }
        } finally {
            try {
                if (keys != null) keys.close();
                if (psDetalle != null) psDetalle.close();
                if (psMov != null) psMov.close();
                if (psPedido != null) psPedido.close();
                if (conTx != null) {
                    conTx.setAutoCommit(true);
                    conTx.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos de transacción: " + e.getMessage());
            }
        }
        return idPedidoGenerado;
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
        // Antes de borrar el pedido, se repone el stock de cada producto que tenía
        // (se genera un movimiento de "Entrada" por anulación) y se borra su detalle.
        Connection conTx = null;
        try {
            conTx = cn.Conexion();
            conTx.setAutoCommit(false);

            String sqlDetalle = "SELECT idProducto, cantidad FROM detalle_pedido WHERE idPedido = ?";
            PreparedStatement psSel = conTx.prepareStatement(sqlDetalle);
            psSel.setInt(1, idPedido);
            ResultSet rsDet = psSel.executeQuery();

            String sqlMov = "INSERT INTO movimientos_stock (fecha, cantidad, motivo, idProducto) VALUES (NOW(), ?, ?, ?)";
            PreparedStatement psMov = conTx.prepareStatement(sqlMov);
            while (rsDet.next()) {
                psMov.setInt(1, rsDet.getInt("cantidad"));
                psMov.setString(2, "Entrada - Anulación pedido #" + idPedido);
                psMov.setInt(3, rsDet.getInt("idProducto"));
                psMov.addBatch();
            }
            psMov.executeBatch();
            rsDet.close();
            psSel.close();
            psMov.close();

            PreparedStatement psDelDetalle = conTx.prepareStatement("DELETE FROM detalle_pedido WHERE idPedido = ?");
            psDelDetalle.setInt(1, idPedido);
            psDelDetalle.executeUpdate();
            psDelDetalle.close();

            PreparedStatement psDelPedido = conTx.prepareStatement("DELETE FROM pedidos WHERE idPedido = ?");
            psDelPedido.setInt(1, idPedido);
            psDelPedido.executeUpdate();
            psDelPedido.close();

            conTx.commit();
        } catch (Exception e) {
            System.out.println("❌ Error al eliminar pedido: " + e.getMessage());
            try {
                if (conTx != null) conTx.rollback();
            } catch (SQLException ex) {
                System.out.println("Error en rollback: " + ex.getMessage());
            }
        } finally {
            try {
                if (conTx != null) {
                    conTx.setAutoCommit(true);
                    conTx.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }
}