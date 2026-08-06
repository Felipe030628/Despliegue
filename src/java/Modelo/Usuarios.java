package Modelo;

public class Usuarios {
    // Atributos privados que coinciden con las columnas de tu BD
    private int idUsuarios;
    private String nombre;
    private String apellido;
    private String correo;
    private String fecha_nacimiento;
    private int idTipoDocumento;
    private String nombre_documento;
    private String telefono;
    private String direccion;
    private String contrasena;
    private int idRol;
    private int activo; // 1 = Activo, 0 = Inactivo (Baja lógica para cumplir con el profesor)

    // Constructor vacío
    public Usuarios() {}

    // Getters y Setters (Necesarios para el DAO y los JSP)
    public int getIdUsuarios() { return idUsuarios; }
    public void setIdUsuarios(int idUsuarios) { this.idUsuarios = idUsuarios; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getFecha_nacimiento() { return fecha_nacimiento; }
    public void setFecha_nacimiento(String fecha_nacimiento) { this.fecha_nacimiento = fecha_nacimiento; }

    public int getIdTipoDocumento() { return idTipoDocumento; }
    public void setIdTipoDocumento(int idTipoDocumento) { this.idTipoDocumento = idTipoDocumento; }

    public String getNombre_documento() { return nombre_documento; }
    public void setNombre_documento(String nombre_documento) { this.nombre_documento = nombre_documento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }
}