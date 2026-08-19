package Controlador;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class CorreoUtil {

    public static boolean enviarCorreo(String destinatario, String codigo) {
        // IMPRESIÓN DE RESPALDO EN CONSOLA (Para que lo veas en los logs de Railway)
        System.out.println("==================================================");
        System.out.println(" [MODO SIMULACIÓN CORREO] Destinatario: " + destinatario);
        System.out.println(" 🔑 CÓDIGO DE VERIFICACIÓN: " + codigo);
        System.out.println("==================================================");

        final String remitente = "barstocks.a.s@gmail.com";
        final String password = "tnaccwnefdzehmme"; 

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Código de Verificación - BarStock");
            message.setText("Hola,\n\nTu código de verificación para completar el registro en BarStock es: " + codigo);

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            System.out.println("⚠️ Aviso de red: No se pudo conectar a Gmail por bloqueo de puertos en la nube (Normal en Railway). Se usará el código en consola.");
            return false;
        }
    }
}