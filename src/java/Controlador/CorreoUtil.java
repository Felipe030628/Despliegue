package Controlador;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class CorreoUtil {

    public static boolean enviarCorreo(String destinatario, String codigo) {
        // Tu correo de Gmail y la contraseña de aplicación que generes en Google
        final String remitente = "tucorreo@gmail.com";
        final String password = "tu_contraseña_de_aplicacion"; 

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
            message.setText("Hola,\n\nTu código de verificación para completar el registro en BarStock es: " + codigo + "\n\nPor favor, ingrésalo en la página web.");

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}