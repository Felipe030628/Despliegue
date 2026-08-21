package Modelo;

public class Productos {
    private int id;
    private String nombre;
    private double precio;
    private String fecha_vencimiento; // Cambiado para coincidir con el JSP
    private int idCategoria;
    private int idCategorias;
    private String nom_categoria;
    private int stock; // calculado a partir de movimientos_stock (entradas - salidas)

    public Productos() {}

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    // Getters y Setters
    public int getIdCategorias() {
        return idCategorias;
    }
    public String getNom_categoria() {
    return nom_categoria;
}

public void setNom_categoria(String nom_categoria) {
    this.nom_categoria = nom_categoria;
}

    public void setIdCategorias(int idCategorias) {
        this.idCategorias = idCategorias;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    // Este es el método que causaba el error
    public String getFecha_vencimiento() { return fecha_vencimiento; }
    public void setFecha_vencimiento(String fecha_vencimiento) { this.fecha_vencimiento = fecha_vencimiento; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
}