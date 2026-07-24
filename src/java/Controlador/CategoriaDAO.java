package Controlador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Modelo.Categorias;

public class CategoriaDAO {

    private String url = "jdbc:mysql://localhost:3307/barstock";
    private String user = "root";
    private String pass = "";

    // LISTAR (Todos los registros)
    public List<Categorias> listar() {
        List<Categorias> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias";

        try (Connection con = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Categorias c = new Categorias();
                c.setIdCategorias(rs.getInt("idCategorias"));
                c.setNombre_categoria(rs.getString("nombre_categoria"));
                lista.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}