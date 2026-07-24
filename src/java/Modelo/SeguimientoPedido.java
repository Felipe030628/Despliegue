package Modelo;

public class SeguimientoPedido {

    public int getIdSeguimientoPedido() {
        return idSeguimientoPedido;
    }

    public void setIdSeguimientoPedido(int idSeguimientoPedido) {
        this.idSeguimientoPedido = idSeguimientoPedido;
    }

    public String getSeguimientoPedidoCol() {
        return seguimientoPedidoCol;
    }

    public void setSeguimientoPedidoCol(String seguimientoPedidoCol) {
        this.seguimientoPedidoCol = seguimientoPedidoCol;
    }

    public int getEstadoPedidoId() {
        return estadoPedidoId;
    }

    public void setEstadoPedidoId(int estadoPedidoId) {
        this.estadoPedidoId = estadoPedidoId;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(int pedidoId) {
        this.pedidoId = pedidoId;
    }

    public int idSeguimientoPedido;
    public String seguimientoPedidoCol;
    public int estadoPedidoId;
    public int pedidoId;
    
}

