package Controlador;

import Modelo.Pagos;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagosDAO {

    private String url = "jdbc:mysql://localhost:3307/barstock";
    private String user = "root";
    private String pass = "";

    // INSERTAR
    public void insertar(Pagos p) {
        String sql = "INSERT INTO pagos (monto, pedido_Cabecera_idPedido, estados_Pag_idEstados_Pag, metodos_Pago_idMetodos_Pago) VALUES (?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, p.monto);
            ps.setInt(2, p.pedido_Cabecera_idPedido);
            ps.setInt(3, p.estados_Pag_idEstados_Pag);
            ps.setInt(4, p.metodos_Pago_idMetodos_Pago);

            ps.executeUpdate();
            System.out.println("Pago registrado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // CONSULTAR
    public Pagos consultar(int id) {
        Pagos p = new Pagos();
        String sql = "SELECT * FROM pagos WHERE idPagos = ?";
        
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p.idPagos = rs.getInt("idPagos");
                    p.monto = rs.getDouble("monto");
                    p.pedido_Cabecera_idPedido = rs.getInt("pedido_Cabecera_idPedido");
                    p.estados_Pag_idEstados_Pag = rs.getInt("estados_Pag_idEstados_Pag");
                    p.metodos_Pago_idMetodos_Pago = rs.getInt("metodos_Pago_idMetodos_Pago");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    // LISTAR
    public List<Pagos> listar() {
        List<Pagos> lista = new ArrayList<>();
        String sql = "SELECT * FROM pagos";

        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pagos p = new Pagos();
                p.idPagos = rs.getInt("idPagos");
                p.monto = rs.getDouble("monto");
                p.pedido_Cabecera_idPedido = rs.getInt("pedido_Cabecera_idPedido");
                p.estados_Pag_idEstados_Pag = rs.getInt("estados_Pag_idEstados_Pag");
                p.metodos_Pago_idMetodos_Pago = rs.getInt("metodos_Pago_idMetodos_Pago");
                lista.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ACTUALIZAR
    public void actualizar(Pagos p) {
        String sql = "UPDATE pagos SET monto=?, pedido_Cabecera_idPedido=?, estados_Pag_idEstados_Pag=?, metodos_Pago_idMetodos_Pago=? WHERE idPagos=?";
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, p.monto);
            ps.setInt(2, p.pedido_Cabecera_idPedido);
            ps.setInt(3, p.estados_Pag_idEstados_Pag);
            ps.setInt(4, p.metodos_Pago_idMetodos_Pago);
            ps.setInt(5, p.idPagos);

            ps.executeUpdate();
            System.out.println("Pago actualizado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ELIMINAR
    public void eliminar(int id) {
        String sql = "DELETE FROM pagos WHERE idPagos = ?";
        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Pago eliminado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}