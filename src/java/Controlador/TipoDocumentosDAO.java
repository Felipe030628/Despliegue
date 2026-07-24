package Controlador;

import Controlador.Conexion;
import Modelo.TiposDocumentos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoDocumentosDAO {

    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    public List<TiposDocumentos> listar() {
        List<TiposDocumentos> lista = new ArrayList<>();
        // Consulta alineada al nuevo script de la tabla 'tipos_documentos'
        String sql = "SELECT * FROM tipos_documentos"; 
        
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                TiposDocumentos td = new TiposDocumentos();
                
                // Mapeo con los nuevos nombres de columna del script
                td.setIdTipoDocumento(rs.getInt("idTipoDoc")); 
                td.setNombre_documento(rs.getString("nombreDoc"));
                
                lista.add(td);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar tipos de documentos: " + e);
        } finally {
            // Cerramos recursos para evitar fugas de memoria en GlassFish
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e);
            }
        }
        return lista;
    }
}