package Modelo;

public class TiposDocumentos {
    private int idTipoDocumento;
    private String nombre_documento;

    // Constructor vacío
    public TiposDocumentos() {
    }

    // Getters y Setters obligatorios
    public int getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(int idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public String getNombre_documento() {
        return nombre_documento;
    }

    public void setNombre_documento(String nombre_documento) {
        this.nombre_documento = nombre_documento;
    }
}