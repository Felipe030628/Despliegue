package Util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Utilidad para el hashing seguro de contraseñas.
 *
 * Usa PBKDF2WithHmacSHA256 (incluido en el propio JDK, sin necesidad de
 * añadir ninguna librería externa al proyecto) con una sal (salt) aleatoria
 * por usuario y un número alto de iteraciones, siguiendo las recomendaciones
 * de OWASP.
 *
 * Formato en el que se guarda la contraseña en la BD (columna `contrasena`):
 *
 *      PBKDF2$<iteraciones>$<salt en Base64>$<hash en Base64>
 *
 * Ese formato permite además detectar de forma segura las cuentas antiguas
 * que aún tengan la contraseña guardada en texto plano (no empiezan por
 * "PBKDF2$"), para poder migrarlas de forma transparente la próxima vez que
 * el usuario inicie sesión correctamente (ver UsuariosDAO.validarLogin).
 */
public final class PasswordUtil {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final String PREFIJO = "PBKDF2";
    private static final int ITERACIONES = 210_000; // recomendación OWASP 2023+
    private static final int LONGITUD_SALT_BYTES = 16;
    private static final int LONGITUD_HASH_BITS = 256;

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
        // Clase de utilidades: no se instancia
    }

    /**
     * Genera el hash seguro de una contraseña en texto plano, listo para
     * guardar en la base de datos.
     */
    public static String hash(String contrasenaPlano) {
        if (contrasenaPlano == null) {
            throw new IllegalArgumentException("La contraseña no puede ser nula");
        }

        byte[] salt = new byte[LONGITUD_SALT_BYTES];
        RANDOM.nextBytes(salt);

        byte[] hashBytes = pbkdf2(contrasenaPlano.toCharArray(), salt, ITERACIONES, LONGITUD_HASH_BITS);

        return PREFIJO + "$" + ITERACIONES + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hashBytes);
    }

    /**
     * Verifica que una contraseña en texto plano corresponde al hash
     * almacenado (con el formato generado por {@link #hash(String)}).
     *
     * Si el valor almacenado NO tiene el formato de hash (por ejemplo,
     * cuentas antiguas creadas antes de este cambio, con la contraseña en
     * texto plano), devuelve false: ese caso especial de migración se maneja
     * aparte en UsuariosDAO, nunca aquí.
     */
    public static boolean verificar(String contrasenaPlano, String hashAlmacenado) {
        if (contrasenaPlano == null || hashAlmacenado == null || !esHashValido(hashAlmacenado)) {
            return false;
        }

        try {
            String[] partes = hashAlmacenado.split("\\$");
            // partes[0] = "PBKDF2", partes[1] = iteraciones, partes[2] = salt, partes[3] = hash
            int iteraciones = Integer.parseInt(partes[1]);
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[3]);

            byte[] hashCalculado = pbkdf2(contrasenaPlano.toCharArray(), salt, iteraciones, hashEsperado.length * 8);

            return comparacionSegura(hashEsperado, hashCalculado);
        } catch (Exception e) {
            // Cualquier problema al parsear/decodificar se trata como "no coincide"
            return false;
        }
    }

    /**
     * Indica si el valor guardado ya tiene el formato de hash de esta clase
     * (true) o si es una contraseña antigua en texto plano (false).
     */
    public static boolean esHashValido(String valorAlmacenado) {
        return valorAlmacenado != null
                && valorAlmacenado.startsWith(PREFIJO + "$")
                && valorAlmacenado.split("\\$").length == 4;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iteraciones, int longitudBits) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iteraciones, longitudBits);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITMO);
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // ALGORITMO es estándar del JDK; esto no debería ocurrir nunca en tiempo de ejecución
            throw new RuntimeException("Error al calcular el hash de la contraseña", e);
        }
    }

    /**
     * Comparación en tiempo constante para evitar ataques de temporización
     * (timing attacks) al comparar hashes.
     */
    private static boolean comparacionSegura(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        int resultado = 0;
        for (int i = 0; i < a.length; i++) {
            resultado |= a[i] ^ b[i];
        }
        return resultado == 0;
    }
}
