package Modelo;

public class Pedidos {
    private int idPedido;
    private String cliente;
    private String mesa;
    private String fecha;
    private String estado;
    private double total;

    // Constructores, Getters y Setters
    public Pedidos() {}
    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getMesa() { return mesa; }
    public void setMesa(String mesa) { this.mesa = mesa; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}