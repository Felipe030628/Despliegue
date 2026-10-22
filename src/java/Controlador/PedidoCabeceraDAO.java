package Controlador;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import Modelo.PedidoCabecera;

public class PedidoCabeceraDAO {

    private final Conexion conexion = new Conexion();

    /**
     * Inserta un nuevo registro de cabecera de pedido mediante procedimiento almacenado.
     * 
     * @param p Objeto PedidoCabecera con la información del pedido.
     */
    public void insertar(PedidoCabecera p) {
        String sql = "{call sp_insertar_pedido_cabecera(?, ?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setTimestamp(1, new Timestamp(p.fecha_hora.getTime()));
            cs.setInt(2, p.usuarios_idUsuarios);
            cs.setString(3, p.mesa_idMesa);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al insertar la cabecera de pedido: " + e.getMessage());
        }
    }

    /**
     * Obtiene una cabecera de pedido por su ID.
     * 
     * @param id Identificador único del pedido.
     * @return Objeto PedidoCabecera poblado o vacío si no se encuentra.
     */
    public PedidoCabecera consultar(int id) {
        PedidoCabecera p = new PedidoCabecera();
        String sql = "{call sp_consultar_pedido_cabecera(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    p.idPedido = rs.getInt("idPedido");
                    p.fecha_hora = rs.getTimestamp("fecha_hora");
                    p.usuarios_idUsuarios = rs.getInt("usuarios_idUsuarios");
                    p.mesa_idMesa = rs.getString("mesa_idMesa");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar la cabecera de pedido por ID (" + id + "): " + e.getMessage());
        }

        return p;
    }

    /**
     * Obtiene el listado de cabeceras de pedido ordenadas por fecha de manera descendente.
     * 
     * @return List con las cabeceras de pedido.
     */
    public List<PedidoCabecera> listar() {
        List<PedidoCabecera> lista = new ArrayList<>();
        String sql = "{call sp_listar_pedido_cabeceras()}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                PedidoCabecera p = new PedidoCabecera();
                p.idPedido = rs.getInt("idPedido");
                p.fecha_hora = rs.getTimestamp("fecha_hora");
                p.usuarios_idUsuarios = rs.getInt("usuarios_idUsuarios");
                p.mesa_idMesa = rs.getString("mesa_idMesa");
                lista.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el listado de cabeceras de pedido: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Actualiza la información de una cabecera de pedido existente.
     * 
     * @param p Objeto PedidoCabecera con la información actualizada.
     */
    public void actualizar(PedidoCabecera p) {
        String sql = "{call sp_actualizar_pedido_cabecera(?, ?, ?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, p.idPedido);
            cs.setTimestamp(2, new Timestamp(p.fecha_hora.getTime()));
            cs.setInt(3, p.usuarios_idUsuarios);
            cs.setString(4, p.mesa_idMesa);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar la cabecera de pedido (" + p.idPedido + "): " + e.getMessage());
        }
    }

    /**
     * Elimina un registro de cabecera de pedido por su ID.
     * 
     * @param id Identificador del pedido a eliminar.
     */
    public void eliminar(int id) {
        String sql = "{call sp_eliminar_pedido_cabecera(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al eliminar la cabecera de pedido (" + id + "): " + e.getMessage());
        }
    }
}