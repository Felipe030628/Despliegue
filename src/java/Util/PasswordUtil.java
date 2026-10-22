package Util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final String PREFIJO = "PBKDF2";
    private static final int ITERACIONES = 210_000;
    private static final int LONGITUD_SALT_BYTES = 16;
    private static final int LONGITUD_HASH_BITS = 256;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordUtil() {

    }

    public static String hash(String contrasenaPlano) {
        if (contrasenaPlano == null) {
            throw new IllegalArgumentException("La contraseña no puede ser nula");
        }

        byte[] salt = new byte[LONGITUD_SALT_BYTES];
        SECURE_RANDOM.nextBytes(salt);

        char[] chars = contrasenaPlano.toCharArray();
        try {
            byte[] hashBytes = pbkdf2(chars, salt, ITERACIONES, LONGITUD_HASH_BITS);

            return PREFIJO + "$" + ITERACIONES + "$"
                    + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(hashBytes);
        } finally {
            Arrays.fill(chars, '\0');
        }
    }

    /**
     * Verifica la validez de una contraseña en texto plano contra un hash almacenado.
     *
     * @param contrasenaPlano Contraseña recibida durante el proceso de autenticación.
     * @param hashAlmacenado  Hash recuperado del repositorio de datos.
     * @return true si la contraseña coincide con el hash; false de lo contrario.
     */
    public static boolean verificar(String contrasenaPlano, String hashAlmacenado) {
        if (contrasenaPlano == null || hashAlmacenado == null || !esHashValido(hashAlmacenado)) {
            return false;
        }

        char[] chars = contrasenaPlano.toCharArray();
        try {
            String[] partes = hashAlmacenado.split("\\$");
            int iteraciones = Integer.parseInt(partes[1]);
            byte[] salt = Base64.getDecoder().decode(partes[2]);
            byte[] hashEsperado = Base64.getDecoder().decode(partes[3]);

            byte[] hashCalculado = pbkdf2(chars, salt, iteraciones, hashEsperado.length * 8);

            return comparacionSegura(hashEsperado, hashCalculado);
        } catch (Exception e) {
            return false;
        } finally {
            Arrays.fill(chars, '\0');
        }
    }

    /**
     * Evalúa si un valor textual posee el formato de hash PBKDF2 válido.
     *
     * @param valorAlmacenado Cadena a evaluar.
     * @return true si cumple con el prefijo y estructura esperada; false de lo contrario.
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
            throw new RuntimeException("Error en el cálculo del hash PBKDF2: " + e.getMessage(), e);
        }
    }

    /**
     * Comparación en tiempo constante para mitigar vulnerabilidades por ataques de temporización (timing attacks).
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