package Controlador;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Modelo.Pagos;

public class PagosDAO {

    private final Conexion conexion = new Conexion();

    /**
     * Inserta un nuevo registro de pago mediante procedimiento almacenado.
     * 
     * @param p Objeto Pagos con la información del pago.
     */
    public void insertar(Pagos p) {
        String sql = "{call sp_insertar_pago(?, ?, ?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setDouble(1, p.monto);
            cs.setInt(2, p.pedido_Cabecera_idPedido);
            cs.setInt(3, p.estados_Pag_idEstados_Pag);
            cs.setInt(4, p.metodos_Pago_idMetodos_Pago);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al insertar el pago: " + e.getMessage());
        }
    }

    /**
     * Obtiene un pago específico por su ID.
     * 
     * @param id Identificador único del pago.
     * @return Objeto Pagos poblado o vacío si no se encuentra.
     */
    public Pagos consultar(int id) {
        Pagos p = new Pagos();
        String sql = "{call sp_consultar_pago(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    p.idPagos = rs.getInt("idPagos");
                    p.monto = rs.getDouble("monto");
                    p.pedido_Cabecera_idPedido = rs.getInt("pedido_Cabecera_idPedido");
                    p.estados_Pag_idEstados_Pag = rs.getInt("estados_Pag_idEstados_Pag");
                    p.metodos_Pago_idMetodos_Pago = rs.getInt("metodos_Pago_idMetodos_Pago");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar el pago por ID (" + id + "): " + e.getMessage());
        }

        return p;
    }

    /**
     * Obtiene la lista completa de pagos registrados.
     * 
     * @return List con los objetos Pagos.
     */
    public List<Pagos> listar() {
        List<Pagos> lista = new ArrayList<>();
        String sql = "{call sp_listar_pagos()}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Pagos p = new Pagos();
                p.idPagos = rs.getInt("idPagos");
                p.monto = rs.getDouble("monto");
                p.pedido_Cabecera_idPedido = rs.getInt("pedido_Cabecera_idPedido");
                p.estados_Pag_idEstados_Pag = rs.getInt("estados_Pag_idEstados_Pag");
                p.metodos_Pago_idMetodos_Pago = rs.getInt("metodos_Pago_idMetodos_Pago");
                lista.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el listado de pagos: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Actualiza la información de un pago existente.
     * 
     * @param p Objeto Pagos con los datos actualizados.
     */
    public void actualizar(Pagos p) {
        String sql = "{call sp_actualizar_pago(?, ?, ?, ?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, p.idPagos);
            cs.setDouble(2, p.monto);
            cs.setInt(3, p.pedido_Cabecera_idPedido);
            cs.setInt(4, p.estados_Pag_idEstados_Pag);
            cs.setInt(5, p.metodos_Pago_idMetodos_Pago);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar el pago (" + p.idPagos + "): " + e.getMessage());
        }
    }

    /**
     * Elimina un registro de pago por su ID.
     * 
     * @param id Identificador del pago a eliminar.
     */
    public void eliminar(int id) {
        String sql = "{call sp_eliminar_pago(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al eliminar el pago (" + id + "): " + e.getMessage());
        }
    }
}