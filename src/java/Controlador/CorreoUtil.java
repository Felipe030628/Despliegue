package Controlador;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class CorreoUtil {

    private static final String SMTP_HOST = getEnvOrDefault("MAIL_HOST", "smtp.gmail.com");
    private static final String SMTP_PORT = getEnvOrDefault("MAIL_PORT", "587");
    private static final String REMITENTE = getEnvOrDefault("MAIL_USERNAME", "");
    private static final String PASSWORD = getEnvOrDefault("MAIL_PASSWORD", "");
    private static final String APP_NAME = "BarStock";

    /**
     * Envía un correo electrónico con el código de verificación para registro.
     */
    public static boolean enviarCorreo(String destinatario, String codigo) {
        String asunto = "Tu código de verificación - " + APP_NAME;
        String mensajeTexto = "Hola,\n\nTu código de verificación para completar el registro es: " 
                            + codigo + "\n\nEste código expira en 15 minutos.\n\n-- " + APP_NAME;
        String mensajeHtml = construirHtmlGenerico(
            "Verifica tu cuenta",
            "Usa el siguiente código para completar tu registro. Expira en 15 minutos.",
            codigo,
            "Si no solicitaste este código, puedes ignorar este mensaje de forma segura.",
            "&#9993;"
        );

        return procesarEnvio(destinatario, asunto, mensajeTexto, mensajeHtml);
    }

    /**
     * Envía un correo electrónico con el código de recuperación de contraseña.
     */
    public static boolean enviarCorreoRecuperacion(String destinatario, String codigo) {
        String asunto = "Restablecer contraseña - " + APP_NAME;
        String mensajeTexto = "Hola,\n\nTu código para restablecer tu contraseña es: " 
                            + codigo + "\n\nEste código expira en 15 minutos.\n\n-- " + APP_NAME;
        String mensajeHtml = construirHtmlGenerico(
            "Restablece tu contraseña",
            "Usa el siguiente código para crear una nueva contraseña. Expira en 15 minutos.",
            codigo,
            "Si no solicitaste este cambio, tu contraseña actual seguirá funcionando de forma segura.",
            "&#128274;"
        );

        return procesarEnvio(destinatario, asunto, mensajeTexto, mensajeHtml);
    }

    /**
     * Método centralizado para la autenticación, configuración SMTP y despacho de correos.
     */
    private static boolean procesarEnvio(String destinatario, String asunto, String textoPlano, String contenidoHtml) {
        if (REMITENTE.isEmpty() || PASSWORD.isEmpty()) {
            System.err.println("Error: Credenciales de correo no configuradas en el entorno.");
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMITENTE, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(REMITENTE, APP_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(textoPlano, "UTF-8");

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(contenidoHtml, "text/html; charset=UTF-8");

            MimeMultipart multipart = new MimeMultipart("alternative");
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(htmlPart);

            message.setContent(multipart);

            Transport.send(message);
            return true;

        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("Error al enviar correo a " + destinatario + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Plantilla HTML unificada con la identidad visual corporativa.
     */
    private static String construirHtmlGenerico(String titulo, String descripcion, String codigo, String notaSeguridad, String iconoHtml) {
        return "<!DOCTYPE html><html lang=\"es\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"></head>"
            + "<body style=\"margin:0; padding:0; background-color:#0d0d0d; font-family:'Segoe UI', Arial, sans-serif;\">"
            + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#0d0d0d; padding:40px 0;\">"
            + "<tr><td align=\"center\">"
            + "<table role=\"presentation\" width=\"420\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#1a1a1a; border-radius:16px; border:1px solid rgba(197,160,89,0.25); border-top:1px solid rgba(197,160,89,0.6); box-shadow:0 25px 50px rgba(0,0,0,0.6); overflow:hidden;\">"
            + "<tr><td align=\"center\" style=\"padding:40px 30px 10px 30px;\">"
            + "<div style=\"font-family:'Brush Script MT', cursive; color:#c5a059; font-size:42px; line-height:1;\">" + APP_NAME + "</div>"
            + "<div style=\"color:#a0a0a0; font-size:11px; letter-spacing:3px; text-transform:uppercase; margin-top:6px;\">Inventory Management</div>"
            + "</td></tr>"
            + "<tr><td align=\"center\" style=\"padding:20px 30px 0 30px;\">"
            + "<div style=\"width:60px; height:60px; border-radius:50%; background-color:rgba(197,160,89,0.1); border:1px solid rgba(197,160,89,0.4); text-align:center; line-height:60px; font-size:26px;\">" + iconoHtml + "</div>"
            + "</td></tr>"
            + "<tr><td align=\"center\" style=\"padding:20px 40px 10px 40px;\">"
            + "<p style=\"color:#ffffff; font-size:16px; margin:0 0 8px 0;\">" + titulo + "</p>"
            + "<p style=\"color:#a0a0a0; font-size:13px; line-height:1.6; margin:0;\">" + descripcion + "</p>"
            + "</td></tr>"
            + "<tr><td align=\"center\" style=\"padding:25px 30px;\">"
            + "<div style=\"background-color:#202020; border:1px solid rgba(197,160,89,0.4); border-radius:10px; padding:18px; display:inline-block; min-width:220px;\">"
            + "<span style=\"color:#c5a059; font-size:32px; font-weight:bold; letter-spacing:10px;\">" + codigo + "</span>"
            + "</div>"
            + "</td></tr>"
            + "<tr><td align=\"center\" style=\"padding:0 40px 35px 40px;\">"
            + "<p style=\"color:#6b6b6b; font-size:11px; line-height:1.6; margin:0;\">" + notaSeguridad + "</p>"
            + "</td></tr>"
            + "<tr><td align=\"center\" style=\"background-color:#141414; padding:18px; border-top:1px solid rgba(197,160,89,0.15);\">"
            + "<p style=\"color:#5a5a5a; font-size:10px; letter-spacing:1px; margin:0;\">&copy; " + APP_NAME + " &mdash; Todos los derechos reservados</p>"
            + "</td></tr>"
            + "</table>"
            + "</td></tr>"
            + "</table>"
            + "</body></html>";
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }
}