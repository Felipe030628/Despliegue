package Controlador;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Modelo.MetodosPago;

public class MetodosPagoDAO {

    private final Conexion conexion = new Conexion();

    /**
     * Inserta un nuevo método de pago en la base de datos mediante procedimiento almacenado.
     * 
     * @param m Objeto MetodosPago con los datos a registrar.
     */
    public void insertar(MetodosPago m) {
        String sql = "{call sp_insertar_metodo_pago(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, m.descripcion_Metodo);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al insertar el método de pago: " + e.getMessage());
        }
    }

    /**
     * Obtiene un método de pago por su ID.
     * 
     * @param id Identificador del método de pago.
     * @return Objeto MetodosPago correspondiente o un objeto vacío si no se encuentra.
     */
    public MetodosPago consultar(int id) {
        MetodosPago m = new MetodosPago();
        String sql = "{call sp_consultar_metodo_pago(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    m.idMetodos_Pago = rs.getInt("idMetodos_Pago");
                    m.descripcion_Metodo = rs.getString("descripcion_Metodo");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar el método de pago por ID (" + id + "): " + e.getMessage());
        }

        return m;
    }

    /**
     * Obtiene la lista completa de métodos de pago registrados.
     * 
     * @return List con los métodos de pago.
     */
    public List<MetodosPago> listar() {
        List<MetodosPago> lista = new ArrayList<>();
        String sql = "{call sp_listar_metodos_pago()}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                MetodosPago m = new MetodosPago();
                m.idMetodos_Pago = rs.getInt("idMetodos_Pago");
                m.descripcion_Metodo = rs.getString("descripcion_Metodo");
                lista.add(m);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el listado de métodos de pago: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Actualiza la descripción de un método de pago existente.
     * 
     * @param m Objeto MetodosPago con la información actualizada.
     */
    public void actualizar(MetodosPago m) {
        String sql = "{call sp_actualizar_metodo_pago(?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, m.idMetodos_Pago);
            cs.setString(2, m.descripcion_Metodo);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar el método de pago (" + m.idMetodos_Pago + "): " + e.getMessage());
        }
    }

    /**
     * Elimina un método de pago de la base de datos por su ID.
     * 
     * @param id Identificador del método de pago a eliminar.
     */
    public void eliminar(int id) {
        String sql = "{call sp_eliminar_metodo_pago(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al eliminar el método de pago (" + id + "): " + e.getMessage());
        }
    }
}