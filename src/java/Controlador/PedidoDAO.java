package Controlador;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import Modelo.DetallePedido;
import Modelo.Pedidos;

public class PedidoDAO {

    private static final ZoneId ZONA_NEGOCIO = ZoneId.of("America/Bogota");
    private final Conexion conexion = new Conexion();

    /**
     * Obtiene el listado completo de pedidos.
     */
    public List<Pedidos> listarPedidos() {
        List<Pedidos> lista = new ArrayList<>();
        String sql = "{call sp_listar_pedidos()}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

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
            System.err.println("Error al listar los pedidos: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Registra un pedido individual.
     */
    public void registrarPedido(Pedidos p) {
        String sql = "{call sp_registrar_pedido(?, ?, ?, ?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, p.getCliente());
            cs.setString(2, p.getMesa());
            cs.setString(3, p.getFecha());
            cs.setString(4, p.getEstado());
            cs.setDouble(5, p.getTotal());
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al registrar el pedido: " + e.getMessage());
        }
    }

    /**
     * Registra el pedido, sus detalles y los movimientos de stock asociados dentro de una sola transacción.
     */
    public int registrarPedidoConDetalle(Pedidos p, List<DetallePedido> detalles) {
        int idPedidoGenerado = -1;
        String sqlPedido = "{call sp_registrar_pedido_transaccion(?, ?, ?, ?, ?)}";
        String sqlDetalle = "{call sp_registrar_detalle_pedido(?, ?, ?, ?, ?)}";
        String sqlMovimiento = "{call sp_registrar_movimiento(?, ?, ?, ?)}";

        Connection conTx = null;

        try {
            conTx = conexion.Conexion();
            conTx.setAutoCommit(false);

            try (CallableStatement csPedido = conTx.prepareCall(sqlPedido)) {
                csPedido.setString(1, p.getCliente());
                csPedido.setString(2, p.getMesa());
                csPedido.setString(3, p.getFecha());
                csPedido.setString(4, p.getEstado());
                csPedido.setDouble(5, p.getTotal());

                try (ResultSet rsKey = csPedido.executeQuery()) {
                    if (rsKey.next()) {
                        idPedidoGenerado = rsKey.getInt("idPedido");
                    }
                }
            }

            if (idPedidoGenerado > 0 && detalles != null && !detalles.isEmpty()) {
                try (CallableStatement csDetalle = conTx.prepareCall(sqlDetalle);
                     CallableStatement csMov = conTx.prepareCall(sqlMovimiento)) {

                    for (DetallePedido d : detalles) {
                        csDetalle.setInt(1, idPedidoGenerado);
                        csDetalle.setInt(2, d.getIdProducto());
                        csDetalle.setInt(3, d.getCantidad());
                        csDetalle.setDouble(4, d.getPrecioUnitario());
                        csDetalle.setDouble(5, d.getSubtotal());
                        csDetalle.addBatch();

                        csMov.setString(1, p.getFecha());
                        csMov.setInt(2, d.getCantidad());
                        csMov.setString(3, "Salida - Pedido #" + idPedidoGenerado);
                        csMov.setInt(4, d.getIdProducto());
                        csMov.addBatch();
                    }

                    csDetalle.executeBatch();
                    csMov.executeBatch();
                }
            }

            conTx.commit();

        } catch (SQLException e) {
            System.err.println("Error al registrar el pedido con detalle: " + e.getMessage());
            idPedidoGenerado = -1;
            if (conTx != null) {
                try {
                    conTx.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error en rollback: " + ex.getMessage());
                }
            }
        } finally {
            if (conTx != null) {
                try {
                    conTx.setAutoCommit(true);
                    conTx.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexión de la transacción: " + e.getMessage());
                }
            }
        }

        return idPedidoGenerado;
    }

    /**
     * Obtiene el total recaudado en caja durante el día actual.
     */
    public double obtenerTotalCajaHoy() {
        double total = 0;
        String hoy = LocalDate.now(ZONA_NEGOCIO).toString();
        String sql = "{call sp_obtener_total_caja_hoy(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, hoy);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("total");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener total caja de hoy: " + e.getMessage());
        }

        return total;
    }

    /**
     * Obtiene las ventas acumuladas de los últimos 7 días.
     */
    public Map<String, Double> obtenerVentasUltimos7Dias() {
        Map<String, Double> ventasPorDia = new LinkedHashMap<>();
        String desde = LocalDate.now(ZONA_NEGOCIO).minusDays(6).toString();
        String sql = "{call sp_obtener_ventas_ultimos_7_dias(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, desde);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    String dia = rs.getString("dia");
                    if (dia != null) {
                        ventasPorDia.put(dia, rs.getDouble("totalDia"));
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener ventas de los últimos 7 días: " + e.getMessage());
        }

        return ventasPorDia;
    }

    /**
     * Consulta un pedido por su ID.
     */
    public Pedidos listarPorId(int id) {
        Pedidos p = null;
        String sql = "{call sp_buscar_pedido_por_id(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    p = new Pedidos();
                    p.setIdPedido(rs.getInt("idPedido"));
                    p.setCliente(rs.getString("cliente"));
                    p.setMesa(rs.getString("mesa"));
                    p.setFecha(rs.getString("fecha"));
                    p.setEstado(rs.getString("estado"));
                    p.setTotal(rs.getDouble("total"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar el pedido por ID (" + id + "): " + e.getMessage());
        }

        return p;
    }

    /**
     * Actualiza la información de un pedido existente.
     */
    public void actualizarPedido(Pedidos p) {
        String sql = "{call sp_actualizar_pedido(?, ?, ?, ?, ?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, p.getIdPedido());
            cs.setString(2, p.getCliente());
            cs.setString(3, p.getMesa());
            cs.setString(4, p.getFecha());
            cs.setString(5, p.getEstado());
            cs.setDouble(6, p.getTotal());
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar el pedido (" + p.getIdPedido() + "): " + e.getMessage());
        }
    }

    /**
     * Elimina un pedido y revierte el stock mediante movimientos de entrada.
     */
    public void eliminarPedido(int idPedido) {
        String sqlConsultarDetalle = "{call sp_listar_detalle_pedido(?)}";
        String sqlMovimiento = "{call sp_registrar_movimiento(?, ?, ?, ?)}";
        String sqlEliminarDetalle = "{call sp_eliminar_detalle_por_pedido(?)}";
        String sqlEliminarPedido = "{call sp_eliminar_pedido(?)}";

        Connection conTx = null;

        try {
            conTx = conexion.Conexion();
            conTx.setAutoCommit(false);

            try (CallableStatement csSel = conTx.prepareCall(sqlConsultarDetalle);
                 CallableStatement csMov = conTx.prepareCall(sqlMovimiento)) {

                csSel.setInt(1, idPedido);

                try (ResultSet rsDet = csSel.executeQuery()) {
                    while (rsDet.next()) {
                        csMov.setString(1, LocalDate.now(ZONA_NEGOCIO).toString());
                        csMov.setInt(2, rsDet.getInt("cantidad"));
                        csMov.setString(3, "Entrada - Anulación pedido #" + idPedido);
                        csMov.setInt(4, rsDet.getInt("idProducto"));
                        csMov.addBatch();
                    }
                }
                csMov.executeBatch();
            }

            try (CallableStatement csDelDet = conTx.prepareCall(sqlEliminarDetalle)) {
                csDelDet.setInt(1, idPedido);
                csDelDet.executeUpdate();
            }

            try (CallableStatement csDelPed = conTx.prepareCall(sqlEliminarPedido)) {
                csDelPed.setInt(1, idPedido);
                csDelPed.executeUpdate();
            }

            conTx.commit();

        } catch (SQLException e) {
            System.err.println("Error al eliminar el pedido (" + idPedido + "): " + e.getMessage());
            if (conTx != null) {
                try {
                    conTx.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error en rollback: " + ex.getMessage());
                }
            }
        } finally {
            if (conTx != null) {
                try {
                    conTx.setAutoCommit(true);
                    conTx.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexión de la transacción: " + e.getMessage());
                }
            }
        }
    }
}