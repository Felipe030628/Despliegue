package Controlador;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Modelo.TiposDocumentos;
import Modelo.Usuarios;
import Util.PasswordUtil;

public class UsuariosDAO {

    private final Conexion conexion = new Conexion();

    /**
     * Valida el inicio de sesión comparando la contraseña enviada con el hash de la BD.
     */
    public Usuarios validarLogin(String correo, String password) {
        if (correo == null || password == null) {
            return null;
        }

        Usuarios user = buscarPorEmail(correo);
        if (user == null) {
            return null;
        }

        String almacenada = user.getContrasena();
        if (almacenada == null) {
            return null;
        }

        boolean credencialesValidas;

        if (PasswordUtil.esHashValido(almacenada)) {
            credencialesValidas = PasswordUtil.verificar(password, almacenada);
        } else {
            credencialesValidas = almacenada.equals(password);
            if (credencialesValidas) {
                guardarHashDirecto(correo, PasswordUtil.hash(password));
            }
        }

        return credencialesValidas ? user : null;
    }

    private void guardarHashDirecto(String correo, String hash) {
        String sql = "{call sp_actualizar_contrasena_usuario(?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, correo);
            cs.setString(2, hash);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar contraseña a hash: " + e.getMessage());
        }
    }

    /**
     * Registra un nuevo usuario aplicando hash de seguridad a la contraseña.
     */
    public int registrar(Usuarios u) {
        String sql = "{call sp_registrar_usuario(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        int r = 0;

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, u.getNombre());
            cs.setString(2, u.getApellido());
            cs.setString(3, u.getCorreo());
            cs.setString(4, u.getFecha_nacimiento());
            cs.setInt(5, u.getIdTipoDocumento());
            cs.setString(6, u.getNombre_documento());
            cs.setString(7, u.getTelefono());
            cs.setString(8, u.getDireccion());
            cs.setString(9, PasswordUtil.hash(u.getContrasena()));
            cs.setInt(10, u.getIdRol());
            r = cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al registrar el usuario: " + e.getMessage());
        }

        return r;
    }

    /**
     * Busca un usuario por su dirección de correo electrónico.
     */
    public Usuarios buscarPorEmail(String correo) {
        String sql = "{call sp_buscar_usuario_por_email(?)}";
        Usuarios user = null;

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, correo);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    user = mapearUsuario(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por email (" + correo + "): " + e.getMessage());
        }

        return user;
    }

    private Usuarios mapearUsuario(ResultSet rs) throws SQLException {
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
        } catch (SQLException e) {
            u.setActivo(1);
        }

        return u;
    }

    /**
     * Obtiene el listado completo de usuarios registrados.
     */
    public List<Usuarios> listarUsuarios() {
        List<Usuarios> lista = new ArrayList<>();
        String sql = "{call sp_listar_usuarios()}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el listado de usuarios: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Busca un usuario por su ID.
     */
    public Usuarios listarPorId(int id) {
        Usuarios u = null;
        String sql = "{call sp_buscar_usuario_por_id(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    u = mapearUsuario(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por ID (" + id + "): " + e.getMessage());
        }

        return u;
    }

    /**
     * Actualiza la información del usuario en la base de datos.
     */
    public void actualizarUsuario(Usuarios u) {
        String sql = "{call sp_actualizar_usuario(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, u.getIdUsuarios());
            cs.setString(2, u.getNombre());
            cs.setString(3, u.getApellido());
            cs.setString(4, u.getCorreo());

            String contrasenaFinal = (u.getContrasena() == null || u.getContrasena().trim().isEmpty())
                    ? obtenerContrasenaActual(u.getIdUsuarios())
                    : PasswordUtil.hash(u.getContrasena());

            cs.setString(5, contrasenaFinal);
            cs.setInt(6, u.getIdTipoDocumento());
            cs.setString(7, u.getNombre_documento());
            cs.setString(8, u.getTelefono());
            cs.setString(9, u.getDireccion());
            cs.setInt(10, u.getIdRol());
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario (" + u.getIdUsuarios() + "): " + e.getMessage());
        }
    }

    private String obtenerContrasenaActual(int idUsuarios) {
        String sql = "{call sp_buscar_usuario_por_id(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, idUsuarios);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("contrasena");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener contraseña actual: " + e.getMessage());
        }

        return null;
    }

    /**
     * Obtiene los tipos de documentos aplicables excluyendo los de menores de edad.
     */
    public List<TiposDocumentos> listarTiposDocumentoMayores() {
        List<TiposDocumentos> lista = new ArrayList<>();
        String sql = "{call sp_listar_tipos_documento_mayores()}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                TiposDocumentos td = new TiposDocumentos();
                td.setIdTipoDocumento(rs.getInt("idTipoDocumento"));
                td.setNombre_documento(rs.getString("nombre_documento"));
                lista.add(td);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar tipos de documento: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Elimina físicamente un usuario por su ID.
     */
    public void eliminar(int id) {
        String sql = "{call sp_eliminar_usuario(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario (" + id + "): " + e.getMessage());
        }
    }

    /**
     * Cambia el estado (activo/inactivo) de un usuario.
     */
    public boolean cambiarEstado(int id, int activo) {
        boolean ok = false;
        String sql = "{call sp_cambiar_estado_usuario(?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.setInt(2, activo);
            cs.executeUpdate();
            ok = true;

        } catch (SQLException e) {
            System.err.println("Error al cambiar estado del usuario (" + id + "): " + e.getMessage());
        }

        return ok;
    }

    /**
     * Actualiza el código de verificación para un usuario determinado.
     */
    public boolean actualizarCodigoVerificacion(String correo, String codigo) {
        boolean actualizado = false;
        String sql = "{call sp_actualizar_codigo_verificacion(?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, correo);
            cs.setString(2, codigo);
            cs.executeUpdate();
            actualizado = true;

        } catch (SQLException e) {
            System.err.println("Error al actualizar código de verificación: " + e.getMessage());
        }

        return actualizado;
    }

    /**
     * Valida el código e ingresa la activación de la cuenta.
     */
    public boolean validarYActivarCuenta(String correo, String codigoIngresado) {
        boolean verificado = false;
        Usuarios user = buscarPorEmail(correo);

        if (user != null) {
            String sqlSelect = "{call sp_buscar_usuario_por_email(?)}";

            try (Connection con = conexion.Conexion();
                 CallableStatement cs = con.prepareCall(sqlSelect)) {

                cs.setString(1, correo);

                try (ResultSet rs = cs.executeQuery()) {
                    if (rs.next()) {
                        String codigoBD = rs.getString("codigo_verificacion");
                        if (codigoBD != null && codigoBD.equals(codigoIngresado)) {
                            
                            String sqlUpdate = "{call sp_validar_activar_cuenta(?)}";
                            try (CallableStatement csUpdate = con.prepareCall(sqlUpdate)) {
                                csUpdate.setString(1, correo);
                                csUpdate.executeUpdate();
                                verificado = true;
                            }
                        }
                    }
                }

            } catch (SQLException e) {
                System.err.println("Error al validar código de activación: " + e.getMessage());
            }
        }

        return verificado;
    }

    /**
     * Actualiza únicamente los datos básicos de contacto del usuario.
     */
    public void actualizarDatosBasicos(Usuarios u) {
        String sql = "{call sp_actualizar_datos_basicos_usuario(?, ?, ?, ?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, u.getIdUsuarios());
            cs.setString(2, u.getNombre());
            cs.setString(3, u.getApellido());
            cs.setString(4, u.getTelefono());
            cs.setString(5, u.getDireccion());
            cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar datos básicos del usuario: " + e.getMessage());
        }
    }

    /**
     * Guarda una nueva contraseña hasheada para el usuario indicado por correo.
     */
    public boolean actualizarContrasena(String correo, String nuevaContrasena) {
        boolean actualizado = false;
        String sql = "{call sp_actualizar_contrasena_usuario(?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, correo);
            cs.setString(2, PasswordUtil.hash(nuevaContrasena));
            cs.executeUpdate();
            actualizado = true;

        } catch (SQLException e) {
            System.err.println("Error al actualizar la contraseña: " + e.getMessage());
        }

        return actualizado;
    }

    /**
     * Valida el código asignado para la recuperación de contraseña.
     */
    public boolean validarCodigoRecuperacion(String correo, String codigoIngresado) {
        boolean verificado = false;
        String sqlSelect = "{call sp_buscar_usuario_por_email(?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sqlSelect)) {

            cs.setString(1, correo);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    String codigoBD = rs.getString("codigo_verificacion");
                    if (codigoBD != null && codigoIngresado != null && codigoBD.equals(codigoIngresado)) {
                        
                        String sqlUpdate = "{call sp_limpiar_codigo_verificacion(?)}";
                        try (CallableStatement csUpdate = con.prepareCall(sqlUpdate)) {
                            csUpdate.setString(1, correo);
                            csUpdate.executeUpdate();
                            verificado = true;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al validar el código de recuperación: " + e.getMessage());
        }

        return verificado;
    }

    /**
     * Busca usuarios cuyo nombre, apellido o correo coincidan con el término indicado.
     */
    public List<Usuarios> buscarPorNombreOCorreo(String termino, int limite) {
        List<Usuarios> lista = new ArrayList<>();
        String sql = "{call sp_buscar_usuarios_por_filtro(?, ?)}";

        try (Connection con = conexion.Conexion();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, termino);
            cs.setInt(2, limite);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearUsuario(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar usuarios por término: " + e.getMessage());
        }

        return lista;
    }
}