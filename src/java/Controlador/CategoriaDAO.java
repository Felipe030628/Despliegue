package Controlador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Modelo.Categorias;

public class CategoriaDAO {

    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // LISTAR (Todos los registros)
    public List<Categorias> listar() {
        List<Categorias> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias";

        try {
            con = cn.Conexion(); // Usando la conexión centralizada de Railway
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Categorias c = new Categorias();
                c.setIdCategorias(rs.getInt("idCategorias"));
                c.setNombre_categoria(rs.getString("nombre_categoria"));
                lista.add(c);
            }
        } catch (Exception e) {
            System.out.println("❌ Error al listar categorías: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (Exception ex) {
                System.out.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }
        return lista;
    }
}