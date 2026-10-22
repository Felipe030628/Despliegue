package Controlador;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Modelo.TiposDocumentos;

public class TipoDocumentosDAO {

    private final Conexion conexion = new Conexion();

    /**
     * Obtiene el listado de tipos de documentos mediante un procedimiento almacenado.
     * 
     * @return List con los objetos TiposDocumentos registrados.
     */
    public List<TiposDocumentos> listar() {
        List<TiposDocumentos> lista = new ArrayList<>();
        String sql = "{call sp_listar_tipos_documentos()}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                TiposDocumentos td = new TiposDocumentos();
                td.setIdTipoDocumento(rs.getInt("idTipoDoc"));
                td.setNombre_documento(rs.getString("nombreDoc"));
                lista.add(td);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar tipos de documentos: " + e.getMessage());
        }

        return lista;
    }
}