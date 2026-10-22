package Controlador;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Modelo.SeguimientoPedido;

public class SeguimientoPedidoDAO {

    private final Conexion conexion = new Conexion();

    /**
     * Inserta un nuevo registro de seguimiento de pedido mediante procedimiento almacenado.
     * 
     * @param s Objeto SeguimientoPedido con los datos a registrar.
     */
    public void insertar(SeguimientoPedido s) {
        String sql = "{call sp_insertar_seguimiento_pedido(?, ?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, s.seguimientoPedidoCol);
            cs.setInt(2, s.estadoPedidoId);
            cs.setInt(3, s.pedidoId);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al insertar el seguimiento de pedido: " + e.getMessage());
        }
    }

    /**
     * Obtiene un registro de seguimiento específico por su ID.
     * 
     * @param id Identificador único del seguimiento.
     * @return Objeto SeguimientoPedido poblado o vacío si no se encuentra.
     */
    public SeguimientoPedido consultar(int id) {
        SeguimientoPedido s = new SeguimientoPedido();
        String sql = "{call sp_consultar_seguimiento_pedido(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    s.idSeguimientoPedido = rs.getInt("idSeguimientoPedido");
                    s.seguimientoPedidoCol = rs.getString("seguimientoPedidoCol");
                    s.estadoPedidoId = rs.getInt("estadoPedidoId");
                    s.pedidoId = rs.getInt("pedidoId");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar el seguimiento de pedido por ID (" + id + "): " + e.getMessage());
        }

        return s;
    }

    /**
     * Obtiene la lista completa de registros de seguimiento.
     * 
     * @return List con los objetos SeguimientoPedido.
     */
    public List<SeguimientoPedido> listar() {
        List<SeguimientoPedido> lista = new ArrayList<>();
        String sql = "{call sp_listar_seguimientos_pedido()}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                SeguimientoPedido s = new SeguimientoPedido();
                s.idSeguimientoPedido = rs.getInt("idSeguimientoPedido");
                s.seguimientoPedidoCol = rs.getString("seguimientoPedidoCol");
                s.estadoPedidoId = rs.getInt("estadoPedidoId");
                s.pedidoId = rs.getInt("pedidoId");
                lista.add(s);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el listado de seguimientos de pedido: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Actualiza la información de un seguimiento de pedido existente.
     * 
     * @param s Objeto SeguimientoPedido con los datos actualizados.
     */
    public void actualizar(SeguimientoPedido s) {
        String sql = "{call sp_actualizar_seguimiento_pedido(?, ?, ?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, s.idSeguimientoPedido);
            cs.setString(2, s.seguimientoPedidoCol);
            cs.setInt(3, s.estadoPedidoId);
            cs.setInt(4, s.pedidoId);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar el seguimiento de pedido (" + s.idSeguimientoPedido + "): " + e.getMessage());
        }
    }

    /**
     * Elimina un registro de seguimiento de pedido por su ID.
     * 
     * @param id Identificador del seguimiento a eliminar.
     */
    public void eliminar(int id) {
        String sql = "{call sp_eliminar_seguimiento_pedido(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al eliminar el seguimiento de pedido (" + id + "): " + e.getMessage());
        }
    }
}