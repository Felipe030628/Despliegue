package Controlador;

import Modelo.SeguimientoPedido;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeguimientoPedidoDAO {

    private String url = "jdbc:mysql://localhost:3307/barstock";
    private String user = "root";
    private String pass = "";

    // INSERTAR
    public void insertar(SeguimientoPedido s) {
        String sql = "INSERT INTO seguimiento_pedido (seguimientoPedidoCol, estadoPedidoId, pedidoId) VALUES (?, ?, ?)";
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.seguimientoPedidoCol);
            ps.setInt(2, s.estadoPedidoId);
            ps.setInt(3, s.pedidoId);

            ps.executeUpdate();
            System.out.println("Seguimiento de pedido insertado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CONSULTAR (Individual)
    public SeguimientoPedido consultar(int id) {
        SeguimientoPedido s = new SeguimientoPedido();
        String sql = "SELECT * FROM seguimiento_pedido WHERE idSeguimientoPedido = ?";
        
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    s.idSeguimientoPedido = rs.getInt("idSeguimientoPedido");
                    s.seguimientoPedidoCol = rs.getString("seguimientoPedidoCol");
                    s.estadoPedidoId = rs.getInt("estadoPedidoId");
                    s.pedidoId = rs.getInt("pedidoId");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    // LISTAR (Todos los seguimientos)
    public List<SeguimientoPedido> listar() {
        List<SeguimientoPedido> lista = new ArrayList<>();
        String sql = "SELECT * FROM seguimiento_pedido";

        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SeguimientoPedido s = new SeguimientoPedido();
                s.idSeguimientoPedido = rs.getInt("idSeguimientoPedido");
                s.seguimientoPedidoCol = rs.getString("seguimientoPedidoCol");
                s.estadoPedidoId = rs.getInt("estadoPedidoId");
                s.pedidoId = rs.getInt("pedidoId");
                lista.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ACTUALIZAR
    public void actualizar(SeguimientoPedido s) {
        String sql = "UPDATE seguimiento_pedido SET seguimientoPedidoCol=?, estadoPedidoId=?, pedidoId=? WHERE idSeguimientoPedido=?";
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, s.seguimientoPedidoCol);
            ps.setInt(2, s.estadoPedidoId);
            ps.setInt(3, s.pedidoId);
            ps.setInt(4, s.idSeguimientoPedido);

            ps.executeUpdate();
            System.out.println("Seguimiento de pedido actualizado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ELIMINAR
    public void eliminar(int id) {
        String sql = "DELETE FROM seguimiento_pedido WHERE idSeguimientoPedido = ?";
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Seguimiento de pedido eliminado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}