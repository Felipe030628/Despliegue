package Util;

/**
 * Clase utilitaria para la construcción y sanitización manual de cadenas en formato JSON.
 * Proporciona métodos de escape para asegurar la validez estructural de la sintaxis JSON
 * sin depender de librerías externas.
 */
public final class JsonUtil {

    private JsonUtil() {
        // Constructor privado para prevenir la instanciación de la clase utilitaria
    }

    /**
     * Escapa caracteres especiales en un texto para ser embebido de forma segura en un valor JSON.
     *
     * @param texto Cadena original a sanitizar.
     * @return Cadena con caracteres especiales escapados, o cadena vacía si el parámetro es null.
     */
    public static String esc(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    /**
     * Formatea un valor textual como una cadena JSON delimitada por comillas dobles.
     *
     * @param valor Texto a formatear.
     * @return Cadena encerrada en comillas dobles con caracteres escapados.
     */
    public static String str(String valor) {
        return "\"" + esc(valor) + "\"";
    }
}