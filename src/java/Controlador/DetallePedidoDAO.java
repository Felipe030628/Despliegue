package Controlador;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Modelo.DetallePedido;

public class DetallePedidoDAO {

    private final Conexion conexion = new Conexion();

    /**
     * Obtiene el listado de ítems pertenecientes a un pedido mediante procedimiento almacenado.
     * 
     * @param idPedido Identificador único del pedido.
     * @return List con los objetos DetallePedido correspondientes.
     */
    public List<DetallePedido> listarPorPedido(int idPedido) {
        List<DetallePedido> lista = new ArrayList<>();
        String sql = "{call sp_listar_detalle_pedido(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, idPedido);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    DetallePedido detalle = new DetallePedido();
                    detalle.setIdDetalle(rs.getInt("idDetalle"));
                    detalle.setIdPedido(rs.getInt("idPedido"));
                    detalle.setIdProducto(rs.getInt("idProducto"));
                    detalle.setNombreProducto(rs.getString("nombreProducto"));
                    detalle.setCantidad(rs.getInt("cantidad"));
                    detalle.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    detalle.setSubtotal(rs.getDouble("subtotal"));
                    lista.add(detalle);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar el detalle del pedido " + idPedido + ": " + e.getMessage());
        }

        return lista;
    }
}