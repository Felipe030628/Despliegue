package Modelo;

public class Pagos {

    public int idPagos;
    public double monto;
    public int pedido_Cabecera_idPedido;
    public int estados_Pag_idEstados_Pag;
    public int metodos_Pago_idMetodos_Pago;

    public Pagos() {
    }

    public Pagos(int id, double mon, int idPedido, int idEstado, int idMetodo) {
        idPagos = id;
        monto = mon;
        pedido_Cabecera_idPedido = idPedido;
        estados_Pag_idEstados_Pag = idEstado;
        metodos_Pago_idMetodos_Pago = idMetodo;
    }
}

