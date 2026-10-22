package Controlador;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Modelo.Mesa;

public class MesaDAO {

    private final Conexion conexion = new Conexion();

    /**
     * Obtiene la lista completa de mesas.
     */
    public List<Mesa> listarMesas() {
        List<Mesa> lista = new ArrayList<>();
        String sql = "{call sp_listar_mesas()}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Mesa m = new Mesa();
                m.setIdMesa(rs.getInt("idMesa"));
                m.setNumero_mesa(rs.getString("numero_mesa"));
                m.setEstado(rs.getString("estado"));
                m.setCapacidad(rs.getInt("capacidad"));
                lista.add(m);
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar el listado de mesas: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Registra una nueva mesa en la base de datos.
     */
    public void registrarMesa(Mesa m) {
        String sql = "{call sp_registrar_mesa(?, ?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, m.getNumero_mesa());
            cs.setString(2, m.getEstado());
            cs.setInt(3, m.getCapacidad());
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al registrar la mesa: " + e.getMessage());
        }
    }

    /**
     * Obtiene una mesa por su ID.
     */
    public Mesa listarPorId(int id) {
        Mesa m = null;
        String sql = "{call sp_buscar_mesa_por_id(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    m = new Mesa();
                    m.setIdMesa(rs.getInt("idMesa"));
                    m.setNumero_mesa(rs.getString("numero_mesa"));
                    m.setEstado(rs.getString("estado"));
                    m.setCapacidad(rs.getInt("capacidad"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar la mesa por ID (" + id + "): " + e.getMessage());
        }

        return m;
    }

    /**
     * Actualiza los datos de una mesa existente.
     */
    public void actualizarMesa(Mesa m) {
        String sql = "{call sp_actualizar_mesa(?, ?, ?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, m.getIdMesa());
            cs.setString(2, m.getNumero_mesa());
            cs.setInt(3, m.getCapacidad());
            cs.setString(4, m.getEstado());
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar la mesa (" + m.getIdMesa() + "): " + e.getMessage());
        }
    }

    /**
     * Actualiza el estado de una mesa buscando por su número identificador.
     */
    public void actualizarEstadoPorNumero(String numeroMesa, String nuevoEstado) {
        String sql = "{call sp_actualizar_estado_mesa_por_numero(?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, numeroMesa);
            cs.setString(2, nuevoEstado);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al cambiar estado de la mesa " + numeroMesa + ": " + e.getMessage());
        }
    }

    /**
     * Elimina una mesa de la base de datos por su ID.
     */
    public void eliminarMesa(int idMesa) {
        String sql = "{call sp_eliminar_mesa(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, idMesa);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al eliminar la mesa (" + idMesa + "): " + e.getMessage());
        }
    }
}