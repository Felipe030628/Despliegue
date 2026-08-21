package Controlador;

import Modelo.DetallePedido;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetallePedidoDAO {

    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // Lista los productos de un pedido, ya con el nombre del producto incluido
    public List<DetallePedido> listarPorPedido(int idPedido) {
        List<DetallePedido> lista = new ArrayList<>();
        String sql = "SELECT d.idDetalle, d.idPedido, d.idProducto, p.nombre AS nombreProducto, "
                + "d.cantidad, d.precio_unitario, d.subtotal "
                + "FROM detalle_pedido d "
                + "JOIN productos p ON p.idProductos = d.idProducto "
                + "WHERE d.idPedido = ? "
                + "ORDER BY d.idDetalle";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idPedido);
            rs = ps.executeQuery();
            while (rs.next()) {
                DetallePedido d = new DetallePedido();
                d.setIdDetalle(rs.getInt("idDetalle"));
                d.setIdPedido(rs.getInt("idPedido"));
                d.setIdProducto(rs.getInt("idProducto"));
                d.setNombreProducto(rs.getString("nombreProducto"));
                d.setCantidad(rs.getInt("cantidad"));
                d.setPrecioUnitario(rs.getDouble("precio_unitario"));
                d.setSubtotal(rs.getDouble("subtotal"));
                lista.add(d);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar detalle de pedido: " + e);
        } finally {
            cerrarRecursos();
        }
        return lista;
    }

    private void cerrarRecursos() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (Exception e) {
            System.out.println("Error al cerrar recursos: " + e.getMessage());
        }
    }
}
