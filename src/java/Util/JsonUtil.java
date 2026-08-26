package Util;

/**
 * Utilidades simples para construir JSON a mano, sin librerías externas
 * (siguiendo el mismo patrón ya usado en DashboardData.java y BuscarGlobal.java).
 */
public class JsonUtil {

    public static String esc(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static String str(String valor) {
        return "\"" + esc(valor) + "\"";
    }
}
