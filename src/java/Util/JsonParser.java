package Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser JSON mínimo, de solo lectura, sin dependencias externas.
 *
 * El proyecto no tiene ninguna librería JSON (Gson/Jackson/org.json) en
 * WEB-INF/lib, y todo el JSON de salida se arma a mano con StringBuilder
 * (ver JsonUtil). Para leer el body JSON que manda la app Flutter (por
 * ejemplo en Pedido?accion=crearJson) hace falta lo mismo pero al revés.
 *
 * Soporta objetos, arreglos, strings (con escapes básicos), números,
 * true/false/null. Es suficiente para los payloads sencillos y planos que
 * manda la app; no pretende ser un parser JSON completo de propósito general.
 */
public final class JsonParser {

    private final String s;
    private int i;

    private JsonParser(String s) {
        this.s = s;
        this.i = 0;
    }

    /** Parsea un texto JSON y devuelve Map, List, String, Double, Boolean o null. */
    public static Object parse(String json) {
        JsonParser p = new JsonParser(json == null ? "" : json);
        p.skipWhitespace();
        Object valor = p.parseValue();
        return valor;
    }

    /** Azúcar: parsea y castea directo a Map (caso típico: objeto raíz). */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseObject(String json) {
        Object v = parse(json);
        if (v instanceof Map) {
            return (Map<String, Object>) v;
        }
        return new LinkedHashMap<>();
    }

    private Object parseValue() {
        skipWhitespace();
        if (i >= s.length()) return null;
        char c = s.charAt(i);
        switch (c) {
            case '{': return parseObjectInternal();
            case '[': return parseArrayInternal();
            case '"': return parseString();
            case 't':
            case 'f': return parseBoolean();
            case 'n': parseNull(); return null;
            default: return parseNumber();
        }
    }

    private Map<String, Object> parseObjectInternal() {
        Map<String, Object> map = new LinkedHashMap<>();
        expect('{');
        skipWhitespace();
        if (peek() == '}') { i++; return map; }
        while (true) {
            skipWhitespace();
            String key = parseString();
            skipWhitespace();
            expect(':');
            Object value = parseValue();
            map.put(key, value);
            skipWhitespace();
            char c = peek();
            if (c == ',') { i++; continue; }
            if (c == '}') { i++; break; }
            throw new RuntimeException("JSON inválido cerca de la posición " + i);
        }
        return map;
    }

    private List<Object> parseArrayInternal() {
        List<Object> lista = new ArrayList<>();
        expect('[');
        skipWhitespace();
        if (peek() == ']') { i++; return lista; }
        while (true) {
            Object value = parseValue();
            lista.add(value);
            skipWhitespace();
            char c = peek();
            if (c == ',') { i++; continue; }
            if (c == ']') { i++; break; }
            throw new RuntimeException("JSON inválido cerca de la posición " + i);
        }
        return lista;
    }

    private String parseString() {
        skipWhitespace();
        expect('"');
        StringBuilder sb = new StringBuilder();
        while (true) {
            if (i >= s.length()) throw new RuntimeException("String JSON sin cerrar");
            char c = s.charAt(i++);
            if (c == '"') break;
            if (c == '\\') {
                if (i >= s.length()) throw new RuntimeException("Escape JSON incompleto");
                char esc = s.charAt(i++);
                switch (esc) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'u':
                        String hex = s.substring(i, i + 4);
                        sb.append((char) Integer.parseInt(hex, 16));
                        i += 4;
                        break;
                    default: sb.append(esc);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private Double parseNumber() {
        int start = i;
        while (i < s.length() && "-+0123456789.eE".indexOf(s.charAt(i)) >= 0) {
            i++;
        }
        String num = s.substring(start, i);
        if (num.isEmpty()) throw new RuntimeException("Número JSON inválido cerca de la posición " + i);
        return Double.parseDouble(num);
    }

    private Boolean parseBoolean() {
        if (s.startsWith("true", i)) { i += 4; return Boolean.TRUE; }
        if (s.startsWith("false", i)) { i += 5; return Boolean.FALSE; }
        throw new RuntimeException("Valor booleano JSON inválido cerca de la posición " + i);
    }

    private void parseNull() {
        if (s.startsWith("null", i)) { i += 4; return; }
        throw new RuntimeException("Valor JSON inválido cerca de la posición " + i);
    }

    private void skipWhitespace() {
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
    }

    private char peek() {
        return i < s.length() ? s.charAt(i) : '\0';
    }

    private void expect(char c) {
        skipWhitespace();
        if (i >= s.length() || s.charAt(i) != c) {
            throw new RuntimeException("Se esperaba '" + c + "' cerca de la posición " + i);
        }
        i++;
    }

    /** Lee todo el body de la petición como texto (asumiendo JSON en UTF-8). */
    public static String leerBody(jakarta.servlet.http.HttpServletRequest request) throws java.io.IOException {
        StringBuilder sb = new StringBuilder();
        try (java.io.BufferedReader reader = request.getReader()) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                sb.append(linea);
            }
        }
        return sb.toString();
    }
}