package Controlador;

import Modelo.PedidoCabecera;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoCabeceraDAO {

    private String url = "jdbc:mysql://localhost:3307/barstock";
    private String user = "root";
    private String pass = "";

    // INSERTAR
    public void insertar(PedidoCabecera p) {
        String sql = "INSERT INTO pedido_cabecera (fecha_hora, usuarios_idUsuarios, mesa_idMesa) VALUES (?, ?, ?)";
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Usamos Timestamp para guardar fecha y hora exacta
            ps.setTimestamp(1, new java.sql.Timestamp(p.fecha_hora.getTime()));
            ps.setInt(2, p.usuarios_idUsuarios);
            ps.setString(3, p.mesa_idMesa);

            ps.executeUpdate();
            System.out.println("Cabecera de pedido insertada correctamente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CONSULTAR (Individual)
    public PedidoCabecera consultar(int id) {
        PedidoCabecera p = new PedidoCabecera();
        String sql = "SELECT * FROM pedido_cabecera WHERE idPedido = ?";
        
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p.idPedido = rs.getInt("idPedido");
                    p.fecha_hora = rs.getTimestamp("fecha_hora");
                    p.usuarios_idUsuarios = rs.getInt("usuarios_idUsuarios");
                    p.mesa_idMesa = rs.getString("mesa_idMesa");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    // LISTAR (Todos los pedidos)
    public List<PedidoCabecera> listar() {
        List<PedidoCabecera> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedido_cabecera ORDER BY fecha_hora DESC";

        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PedidoCabecera p = new PedidoCabecera();
                p.idPedido = rs.getInt("idPedido");
                p.fecha_hora = rs.getTimestamp("fecha_hora");
                p.usuarios_idUsuarios = rs.getInt("usuarios_idUsuarios");
                p.mesa_idMesa = rs.getString("mesa_idMesa");
                lista.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ACTUALIZAR
    public void actualizar(PedidoCabecera p) {
        String sql = "UPDATE pedido_cabecera SET fecha_hora=?, usuarios_idUsuarios=?, mesa_idMesa=? WHERE idPedido=?";
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, new java.sql.Timestamp(p.fecha_hora.getTime()));
            ps.setInt(2, p.usuarios_idUsuarios);
            ps.setString(3, p.mesa_idMesa);
            ps.setInt(4, p.idPedido);

            ps.executeUpdate();
            System.out.println("Cabecera de pedido actualizada correctamente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ELIMINAR
    public void eliminar(int id) {
        String sql = "DELETE FROM pedido_cabecera WHERE idPedido = ?";
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Cabecera de pedido eliminada correctamente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}