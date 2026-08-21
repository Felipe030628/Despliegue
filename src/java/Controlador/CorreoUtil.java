package Controlador;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

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
            message.setFrom(new InternetAddress(remitente, "BarStock"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Tu código de verificación - BarStock");

            // Parte de texto plano (fallback para clientes que no leen HTML)
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(
                "Hola,\n\n" +
                "Tu codigo de verificacion para completar el registro en BarStock es: " + codigo + "\n\n" +
                "Este codigo expira en 15 minutos. Si tu no solicitaste este registro, ignora este mensaje.\n\n" +
                "-- BarStock"
            );

            // Parte HTML con el diseño de marca
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(construirHtmlCorreo(codigo), "text/html; charset=UTF-8");

            MimeMultipart multipart = new MimeMultipart("alternative");
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(htmlPart);

            message.setContent(multipart);

            Transport.send(message);
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Aviso de red: No se pudo conectar a Gmail por bloqueo de puertos en la nube (Normal en Railway). Se usará el código en consola.");
            return false;
        }
    }

    public static boolean enviarCorreoRecuperacion(String destinatario, String codigo) {
        System.out.println("==================================================");
        System.out.println(" [MODO SIMULACIÓN CORREO] Destinatario: " + destinatario);
        System.out.println(" 🔑 CÓDIGO DE RECUPERACIÓN: " + codigo);
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
            message.setFrom(new InternetAddress(remitente, "BarStock"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Recupera tu contraseña - BarStock");

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(
                "Hola,\n\n" +
                "Recibimos una solicitud para restablecer tu contraseña en BarStock. Tu codigo de verificacion es: " + codigo + "\n\n" +
                "Este codigo expira en 15 minutos. Si tu no solicitaste este cambio, ignora este mensaje: tu contraseña actual seguira funcionando.\n\n" +
                "-- BarStock"
            );

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(construirHtmlRecuperacion(codigo), "text/html; charset=UTF-8");

            MimeMultipart multipart = new MimeMultipart("alternative");
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(htmlPart);

            message.setContent(multipart);

            Transport.send(message);
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Aviso de red: No se pudo conectar a Gmail por bloqueo de puertos en la nube (Normal en Railway). Se usará el código en consola.");
            return false;
        }
    }

    /**
     * Plantilla HTML del correo de recuperación de contraseña. Misma identidad visual que el
     * correo de verificación de registro, pero con texto e ícono propios de "restablecer".
     */
    private static String construirHtmlRecuperacion(String codigo) {
        return "<!DOCTYPE html>"
            + "<html lang=\"es\">"
            + "<head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"></head>"
            + "<body style=\"margin:0; padding:0; background-color:#0d0d0d; font-family:'Segoe UI', Arial, sans-serif;\">"
            + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#0d0d0d; padding:40px 0;\">"
            + "<tr><td align=\"center\">"
            + "<table role=\"presentation\" width=\"420\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#1a1a1a; border-radius:16px; border:1px solid rgba(197,160,89,0.25); border-top:1px solid rgba(197,160,89,0.6); box-shadow:0 25px 50px rgba(0,0,0,0.6); overflow:hidden;\">"
            + "<tr><td align=\"center\" style=\"padding:40px 30px 10px 30px;\">"
            + "<div style=\"font-family:'Brush Script MT', cursive; color:#c5a059; font-size:42px; line-height:1;\">BarStock</div>"
            + "<div style=\"color:#a0a0a0; font-size:11px; letter-spacing:3px; text-transform:uppercase; margin-top:6px;\">Inventory Management</div>"
            + "</td></tr>"
            + "<tr><td align=\"center\" style=\"padding:20px 30px 0 30px;\">"
            + "<div style=\"width:60px; height:60px; border-radius:50%; background-color:rgba(197,160,89,0.1); border:1px solid rgba(197,160,89,0.4); text-align:center; line-height:60px; font-size:26px;\">&#128274;</div>"
            + "</td></tr>"
            + "<tr><td align=\"center\" style=\"padding:20px 40px 10px 40px;\">"
            + "<p style=\"color:#ffffff; font-size:16px; margin:0 0 8px 0;\">Restablece tu contraseña</p>"
            + "<p style=\"color:#a0a0a0; font-size:13px; line-height:1.6; margin:0;\">Usa el siguiente código para crear una nueva contraseña en BarStock. Expira en 15 minutos.</p>"
            + "</td></tr>"
            + "<tr><td align=\"center\" style=\"padding:25px 30px;\">"
            + "<div style=\"background-color:#202020; border:1px solid rgba(197,160,89,0.4); border-radius:10px; padding:18px; display:inline-block; min-width:220px;\">"
            + "<span style=\"color:#c5a059; font-size:32px; font-weight:bold; letter-spacing:10px;\">" + codigo + "</span>"
            + "</div>"
            + "</td></tr>"
            + "<tr><td align=\"center\" style=\"padding:0 40px 35px 40px;\">"
            + "<p style=\"color:#6b6b6b; font-size:11px; line-height:1.6; margin:0;\">Si tú no solicitaste este cambio, tu contraseña actual seguirá funcionando y puedes ignorar este mensaje.</p>"
            + "</td></tr>"
            + "<tr><td align=\"center\" style=\"background-color:#141414; padding:18px; border-top:1px solid rgba(197,160,89,0.15);\">"
            + "<p style=\"color:#5a5a5a; font-size:10px; letter-spacing:1px; margin:0;\">&copy; BarStock &mdash; Todos los derechos reservados</p>"
            + "</td></tr>"
            + "</table>"
            + "</td></tr>"
            + "</table>"
            + "</body></html>";
    }

    /**
     * Plantilla HTML del correo de verificación, con la identidad visual de BarStock
     * (dorado #c5a059 sobre fondo oscuro). Usa estilos en línea porque la mayoría
     * de los clientes de correo (Gmail, Outlook) ignoran las hojas de estilo externas.
     */
    private static String construirHtmlCorreo(String codigo) {
        return "<!DOCTYPE html>"
            + "<html lang=\"es\">"
            + "<head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"></head>"
            + "<body style=\"margin:0; padding:0; background-color:#0d0d0d; font-family:'Segoe UI', Arial, sans-serif;\">"
            + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#0d0d0d; padding:40px 0;\">"
            + "<tr><td align=\"center\">"

            // Tarjeta principal
            + "<table role=\"presentation\" width=\"420\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#1a1a1a; border-radius:16px; border:1px solid rgba(197,160,89,0.25); border-top:1px solid rgba(197,160,89,0.6); box-shadow:0 25px 50px rgba(0,0,0,0.6); overflow:hidden;\">"

            // Cabecera / marca
            + "<tr><td align=\"center\" style=\"padding:40px 30px 10px 30px;\">"
            + "<div style=\"font-family:'Brush Script MT', cursive; color:#c5a059; font-size:42px; line-height:1;\">BarStock</div>"
            + "<div style=\"color:#a0a0a0; font-size:11px; letter-spacing:3px; text-transform:uppercase; margin-top:6px;\">Inventory Management</div>"
            + "</td></tr>"

            // Icono
            + "<tr><td align=\"center\" style=\"padding:20px 30px 0 30px;\">"
            + "<div style=\"width:60px; height:60px; border-radius:50%; background-color:rgba(197,160,89,0.1); border:1px solid rgba(197,160,89,0.4); text-align:center; line-height:60px; font-size:26px;\">&#9993;</div>"
            + "</td></tr>"

            // Texto
            + "<tr><td align=\"center\" style=\"padding:20px 40px 10px 40px;\">"
            + "<p style=\"color:#ffffff; font-size:16px; margin:0 0 8px 0;\">Verifica tu cuenta</p>"
            + "<p style=\"color:#a0a0a0; font-size:13px; line-height:1.6; margin:0;\">Usa el siguiente código para completar tu registro en BarStock. Expira en 15 minutos.</p>"
            + "</td></tr>"

            // Código
            + "<tr><td align=\"center\" style=\"padding:25px 30px;\">"
            + "<div style=\"background-color:#202020; border:1px solid rgba(197,160,89,0.4); border-radius:10px; padding:18px; display:inline-block; min-width:220px;\">"
            + "<span style=\"color:#c5a059; font-size:32px; font-weight:bold; letter-spacing:10px;\">" + codigo + "</span>"
            + "</div>"
            + "</td></tr>"

            // Nota de seguridad
            + "<tr><td align=\"center\" style=\"padding:0 40px 35px 40px;\">"
            + "<p style=\"color:#6b6b6b; font-size:11px; line-height:1.6; margin:0;\">Si tú no solicitaste este código, puedes ignorar este mensaje de forma segura.</p>"
            + "</td></tr>"

            // Footer
            + "<tr><td align=\"center\" style=\"background-color:#141414; padding:18px; border-top:1px solid rgba(197,160,89,0.15);\">"
            + "<p style=\"color:#5a5a5a; font-size:10px; letter-spacing:1px; margin:0;\">&copy; BarStock &mdash; Todos los derechos reservados</p>"
            + "</td></tr>"

            + "</table>"
            + "</td></tr>"
            + "</table>"
            + "</body></html>";
    }
}