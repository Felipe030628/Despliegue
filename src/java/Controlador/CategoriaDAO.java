package Controlador;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Modelo.Categorias;

public class CategoriaDAO {

    private final Conexion conexion = new Conexion();

    /**
     * Obtiene la lista completa de categorías invocando un procedimiento almacenado.
     * 
     * @return List con los objetos Categorias encontrados.
     */
    public List<Categorias> listar() {
        List<Categorias> lista = new ArrayList<>();
        String sql = "{call sp_listar_categorias()}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Categorias categoria = new Categorias();
                categoria.setIdCategorias(rs.getInt("idCategorias"));
                categoria.setNombre_categoria(rs.getString("nombre_categoria"));
                lista.add(categoria);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el listado de categorías: " + e.getMessage());
        }

        return lista;
    }
}