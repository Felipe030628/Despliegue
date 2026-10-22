package Controlador;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import Modelo.MovimientosStock;

public class MovimientoStockDAO {

    private final Conexion conexion = new Conexion();

    /**
     * Obtiene el listado completo de movimientos de stock con el nombre del producto asociado.
     */
    public List<MovimientosStock> listarMovimientos() {
        List<MovimientosStock> lista = new ArrayList<>();
        String sql = "{call sp_listar_movimientos()}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

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

        } catch (SQLException e) {
            System.err.println("Error al listar los movimientos de stock: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Registra un nuevo movimiento de stock.
     */
    public void registrarMovimiento(MovimientosStock m) {
        String sql = "{call sp_registrar_movimiento(?, ?, ?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, m.getFecha());
            cs.setInt(2, m.getCantidad());
            cs.setString(3, m.getMotivo());
            cs.setInt(4, m.getIdProducto());
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al registrar el movimiento: " + e.getMessage());
        }
    }

    /**
     * Busca un movimiento por su ID.
     */
    public MovimientosStock listarPorId(int id) {
        MovimientosStock m = new MovimientosStock();
        String sql = "{call sp_buscar_movimiento_por_id(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    m.setIdMovimiento(rs.getInt("idMovimiento"));
                    m.setFecha(rs.getString("fecha"));
                    m.setCantidad(rs.getInt("cantidad"));
                    m.setMotivo(rs.getString("motivo"));
                    m.setIdProducto(rs.getInt("idProducto"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar el movimiento por ID (" + id + "): " + e.getMessage());
        }

        return m;
    }

    /**
     * Actualiza la información de un movimiento de stock existente.
     */
    public void actualizarMovimiento(MovimientosStock m) {
        String sql = "{call sp_actualizar_movimiento(?, ?, ?, ?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, m.getIdMovimiento());
            cs.setString(2, m.getFecha());
            cs.setInt(3, m.getCantidad());
            cs.setString(4, m.getMotivo());
            cs.setInt(5, m.getIdProducto());
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar el movimiento (" + m.getIdMovimiento() + "): " + e.getMessage());
        }
    }

    /**
     * Calcula la cantidad de productos cuyo stock actual está en o por debajo del umbral mínimo.
     */
    public int contarProductosCriticos(int umbralMinimo) {
        int total = 0;
        String sql = "{call sp_contar_productos_criticos(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, umbralMinimo);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt("total");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al contar productos críticos: " + e.getMessage());
        }

        return total;
    }

    /**
     * Obtiene el ranking de productos con mayor número de salidas.
     */
    public List<Map<String, Object>> obtenerTopProductos(int limite) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "{call sp_obtener_top_productos(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, limite);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    fila.put("nombre", rs.getString("nombre"));
                    fila.put("cantidad", rs.getInt("totalSalidas"));
                    fila.put("monto", rs.getInt("totalSalidas") * rs.getDouble("precio"));
                    lista.add(fila);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener top productos: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Elimina un movimiento de stock por su ID.
     */
    public void eliminarMovimiento(int idMovimiento) {
        String sql = "{call sp_eliminar_movimiento(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, idMovimiento);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al eliminar el movimiento (" + idMovimiento + "): " + e.getMessage());
        }
    }
}