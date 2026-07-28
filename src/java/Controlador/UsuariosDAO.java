package Controlador;

import Modelo.Usuarios;
import Modelo.TiposDocumentos; // Asegúrate de importar tu modelo de TipoDocumento correctamente
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuariosDAO {
    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // 1. Validar Login
    public boolean validarLogin(String correo, String password) {
    boolean encon = false;
    String sql = "SELECT * FROM usuarios WHERE correo = ? AND password = ?"; // (o el nombre de tu tabla y campos)
    
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://sakura.proxy.rlwy.net:24908/railway?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        
        // Conexión directa dentro del mismo método para evitar nulos
        java.sql.Connection conexionDirecta = java.sql.DriverManager.getConnection(url, "root", "aSZLfvKfJVnchDkGkHFWxnycCmAICyJU");
        
        java.sql.PreparedStatement ps = conexionDirecta.prepareStatement(sql);
        ps.setString(1, correo);
        ps.setString(2, password);
        java.sql.ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            encon = true;
        }
        
        conexionDirecta.close();
    } catch (Exception e) {
        System.err.println("--- ERROR DIRECTO EN EL DAO ---");
        e.printStackTrace();
    }
    
    return encon;
}

    // 2. Registrar Usuario
    public int registrar(Usuarios u) {
        String sql = "INSERT INTO usuarios (nombre, apellido, correo, fecha_nacimiento, idTipoDocumento, num_documento, telefono, direccion, contrasena, idRol) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int r = 0;
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getCorreo());
            ps.setString(4, u.getFecha_nacimiento());
            ps.setInt(5, u.getIdTipoDocumento());
            ps.setString(6, u.getNombre_documento()); // CORREGIDO: Usando num_documento
            ps.setString(7, u.getTelefono());
            ps.setString(8, u.getDireccion());
            ps.setString(9, u.getContrasena());
            ps.setInt(10, u.getIdRol());
            r = ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("❌ Error en registrar: " + e.getMessage());
        } finally { 
            cerrarRecursos(); 
        }
        return r;
    }

    // 3. Buscar por Correo
    public Usuarios buscarPorEmail(String correo) {
        String sql = "SELECT * FROM usuarios WHERE correo = ?";
        Usuarios user = null;
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            rs = ps.executeQuery();
            if (rs.next()) { 
                user = mapearUsuario(rs); 
            }
        } catch (Exception e) {
            System.out.println("❌ Error en buscarPorEmail: " + e.getMessage());
        } finally { 
            cerrarRecursos(); 
        }
        return user;
    }

    // Método privado para mapear datos (Evita duplicidad de código)
    private Usuarios mapearUsuario(ResultSet rs) throws Exception {
        Usuarios u = new Usuarios();
        u.setIdUsuarios(rs.getInt("idUsuarios"));
        u.setNombre(rs.getString("nombre"));
        u.setApellido(rs.getString("apellido"));
        u.setCorreo(rs.getString("correo"));
        u.setFecha_nacimiento(rs.getString("fecha_nacimiento"));
        u.setIdTipoDocumento(rs.getInt("idTipoDocumento"));
        u.setNombre_documento(rs.getString("num_documento")); // CORREGIDO: Usando setNum_documento
        u.setTelefono(rs.getString("telefono"));
        u.setDireccion(rs.getString("direccion"));
        u.setContrasena(rs.getString("contrasena"));
        u.setIdRol(rs.getInt("idRol"));
        return u;
    }

    private void cerrarRecursos() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (Exception e) { 
            System.out.println("Error al cerrar: " + e.getMessage()); 
        }
    }
    
    // 4. Listar Usuarios (Para la tabla de Empleados)
    public List<Usuarios> listarUsuarios() {
        List<Usuarios> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        } catch (Exception e) {
            System.out.println("❌ Error en listarUsuarios: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return lista;
    }
    
    // 5. Buscar usuario por ID para cargarlo en el formulario de edición
    public Usuarios listarPorId(int id) {
        Usuarios u = null;
        String sql = "SELECT * FROM usuarios WHERE idUsuarios = ?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                u = mapearUsuario(rs);
            }
        } catch (Exception e) {
            System.out.println("❌ Error al listar usuario por ID: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return u;
    }

    // 6. Actualizar los datos del usuario en la base de datos
    public void actualizarUsuario(Usuarios u) {
        String sql = "UPDATE usuarios SET nombre = ?, apellido = ?, correo = ?, contrasena = ?, idTipoDocumento = ?, num_documento = ?, telefono = ?, direccion = ?, idRol = ? WHERE idUsuarios = ?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getCorreo());
            ps.setString(4, u.getContrasena());
            ps.setInt(5, u.getIdTipoDocumento());
            ps.setString(6, u.getNombre_documento()); // CORREGIDO: Usando getNum_documento
            ps.setString(7, u.getTelefono());
            ps.setString(8, u.getDireccion());
            ps.setInt(9, u.getIdRol());
            ps.setInt(10, u.getIdUsuarios());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar usuario: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }
    
    // 7. Listar tipos de documento excluyendo la tarjeta de identidad (ID 2)
    // 7. Listar tipos de documento excluyendo la tarjeta de identidad (ID 2)
    public List<TiposDocumentos> listarTiposDocumentoMayores() {
        List<TiposDocumentos> lista = new ArrayList<>();
        String sql = "SELECT * FROM tipo_documento WHERE idTipoDocumento <> 2";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                TiposDocumentos td = new TiposDocumentos();
                td.setIdTipoDocumento(rs.getInt("idTipoDocumento"));
                td.setNombre_documento(rs.getString("nombre_documento"));
                lista.add(td);
            }
        } catch (Exception e) {
            System.out.println("Error al listar documentos: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return lista;
    }
    
    public void eliminar(int id) {
    String sql = "DELETE FROM usuarios WHERE idUsuarios = ?";
    try {
        con = cn.Conexion();
        ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    } catch (Exception e) {
        System.out.println("❌ Error al eliminar usuario: " + e.getMessage());
    } finally {
        cerrarRecursos();
    }
}
}