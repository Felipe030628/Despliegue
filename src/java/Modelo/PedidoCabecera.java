package Modelo;

import java.sql.Timestamp;

public class PedidoCabecera {

    public int idPedido;
    public Timestamp fecha_hora;
    public int usuarios_idUsuarios;
    public String mesa_idMesa;

    public PedidoCabecera() {
    }

    public PedidoCabecera(int id, Timestamp fecha, int idUsuario, String idMesa) {
        idPedido = id;
        fecha_hora = fecha;
        usuarios_idUsuarios = idUsuario;
        mesa_idMesa = idMesa;
    }
}
