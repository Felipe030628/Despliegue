package Controlador;

import Modelo.Usuarios;
import Modelo.TiposDocumentos;
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

    // 1. Validar Login (Actualizado para validar contra el campo activo si lo requieres, o sin columnas inexistentes)
    public Usuarios validarLogin(String correo, String password) {
        Usuarios user = null;
        String sql = "SELECT * FROM usuarios WHERE correo = ? AND contrasena = ?";
    
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            ps.setString(2, password);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                user = mapearUsuario(rs);
            }
        } catch (Exception e) {
            System.err.println("--- ERROR DIRECTO EN EL DAO --- " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos();
        }
    
        return user;
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
            ps.setString(6, u.getNombre_documento());
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

    // Método privado para mapear datos
    private Usuarios mapearUsuario(ResultSet rs) throws Exception {
        Usuarios u = new Usuarios();
        u.setIdUsuarios(rs.getInt("idUsuarios"));
        u.setNombre(rs.getString("nombre"));
        u.setApellido(rs.getString("apellido"));
        u.setCorreo(rs.getString("correo"));
        u.setFecha_nacimiento(rs.getString("fecha_nacimiento"));
        u.setIdTipoDocumento(rs.getInt("idTipoDocumento"));
        u.setNombre_documento(rs.getString("num_documento"));
        u.setTelefono(rs.getString("telefono"));
        u.setDireccion(rs.getString("direccion"));
        u.setContrasena(rs.getString("contrasena"));
        u.setIdRol(rs.getInt("idRol"));
        
        try {
            u.setActivo(rs.getInt("activo"));
        } catch (Exception e) {
            u.setActivo(1); 
        }
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
            ps.setString(6, u.getNombre_documento());
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
    
    // 8. Método para eliminación física
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
    
    // 9. Cambiar Estado (Baja Lógica: Activo / Inactivo)
    public boolean cambiarEstado(int id, int activo) {
        boolean ok = false;
        String sql = "UPDATE usuarios SET activo = ? WHERE idUsuarios = ?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, activo);
            ps.setInt(2, id);
            ps.executeUpdate();
            ok = true;
        } catch (Exception e) {
            System.out.println("❌ Error al cambiar estado: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos();
        }
        return ok;
    }

    public boolean actualizarCodigoVerificacion(String correo, String codigo) {
        boolean actualizado = false;
        String sql = "UPDATE usuarios SET codigo_verificacion = ? WHERE correo = ?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, codigo);
            ps.setString(2, correo);
            ps.executeUpdate();
            actualizado = true;
        } catch (Exception e) {
            System.out.println("❌ Error al actualizar código de verificación: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return actualizado;
    }
    
    public boolean validarYActivarCuenta(String correo, String codigoIngresado) {
        boolean verificado = false;
        String sqlSelect = "SELECT codigo_verificacion FROM usuarios WHERE correo = ?";
        // Corregido: Se actualiza el campo 'activo = 1' en lugar de la columna eliminada 'estado_verificacion'
        String sqlUpdate = "UPDATE usuarios SET activo = 1, codigo_verificacion = NULL WHERE correo = ?";
        
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sqlSelect);
            ps.setString(1, correo);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                String codigoBD = rs.getString("codigo_verificacion");
                if (codigoBD != null && codigoBD.equals(codigoIngresado)) {
                    PreparedStatement psUpdate = con.prepareStatement(sqlUpdate);
                    psUpdate.setString(1, correo);
                    psUpdate.executeUpdate();
                    psUpdate.close();
                    verificado = true;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error al validar código: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return verificado;
    }

    // 10. Actualizar SOLO los datos de contacto del empleado (nombre, apellido, teléfono, dirección).
    // A propósito NO toca correo, contraseña ni documento: son datos esenciales/sensibles que
    // no deben poder cambiarse desde el formulario de edición de empleados.
    public void actualizarDatosBasicos(Usuarios u) {
        String sql = "UPDATE usuarios SET nombre = ?, apellido = ?, telefono = ?, direccion = ? WHERE idUsuarios = ?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getTelefono());
            ps.setString(4, u.getDireccion());
            ps.setInt(5, u.getIdUsuarios());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar datos básicos: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
    }

    // 11. Guardar la nueva contraseña una vez validado el código de recuperación
    public boolean actualizarContrasena(String correo, String nuevaContrasena) {
        boolean actualizado = false;
        String sql = "UPDATE usuarios SET contrasena = ? WHERE correo = ?";
        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, nuevaContrasena);
            ps.setString(2, correo);
            ps.executeUpdate();
            actualizado = true;
        } catch (Exception e) {
            System.out.println("❌ Error al actualizar contraseña: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return actualizado;
    }

    // 12. Validar el código de recuperación de contraseña. A diferencia de validarYActivarCuenta(),
    // NO toca la columna 'activo': solo confirma el código y lo invalida para que no se reutilice.
    public boolean validarCodigoRecuperacion(String correo, String codigoIngresado) {
        boolean verificado = false;
        String sqlSelect = "SELECT codigo_verificacion FROM usuarios WHERE correo = ?";
        String sqlUpdate = "UPDATE usuarios SET codigo_verificacion = NULL WHERE correo = ?";

        try {
            con = cn.Conexion();
            ps = con.prepareStatement(sqlSelect);
            ps.setString(1, correo);
            rs = ps.executeQuery();

            if (rs.next()) {
                String codigoBD = rs.getString("codigo_verificacion");
                if (codigoBD != null && codigoIngresado != null && codigoBD.equals(codigoIngresado)) {
                    PreparedStatement psUpdate = con.prepareStatement(sqlUpdate);
                    psUpdate.setString(1, correo);
                    psUpdate.executeUpdate();
                    psUpdate.close();
                    verificado = true;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error al validar código de recuperación: " + e.getMessage());
        } finally {
            cerrarRecursos();
        }
        return verificado;
    }
}