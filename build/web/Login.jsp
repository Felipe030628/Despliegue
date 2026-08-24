<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login | BarStock Premium</title>
    
    <link href="https://fonts.googleapis.com/css2?family=Great+Vibes&family=Montserrat:wght@300;400;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="Vista/Css/Login.css?v=2">
</head>
<body>

    <div class="login-card">
        <h1 class="brand-title">BarStock</h1>
        <span class="subtitle">Inventory Management</span>
        
        <%-- Bloque de errores --%>
        <% 
            String errorMsg = (String) request.getAttribute("mensaje");
            if (errorMsg == null) { 
                errorMsg = (String) request.getAttribute("error"); 
            }
            if (errorMsg != null) { 
        %>
            <div class="error-toast" style="color: red; margin-bottom: 10px;">
                <%= errorMsg %>
            </div>
        <% } %>

        <%-- Mensaje de éxito tras restablecer la contraseña --%>
        <%
            String status = request.getParameter("status");
            if ("pass_actualizada".equals(status)) {
        %>
            <div class="success-toast" style="background: rgba(40,167,69,0.1); border: 1px solid #28a745; color: #7ce495; padding: 12px; border-radius: 6px; font-size: 0.85rem; margin-bottom: 1.5rem;">
                Tu contraseña se actualizó correctamente. Ya puedes iniciar sesión.
            </div>
        <%
            }
        %>

        <%-- FORMULARIO CORREGIDO: Nombres de input coinciden con el Servlet --%>
        <form action="${pageContext.request.contextPath}/ServletLogin" method="POST">
            <input type="hidden" name="accion" value="ingresar">

            <div class="input-group-custom">
                <label>Correo Electrónico</label>
                <input type="email" name="txtCorreo" placeholder="usuario@barstock.com" required>
            </div>
            
            <div class="input-group-custom">
                <label>Contraseña</label>
                <div class="password-wrapper">
                    <input type="password" name="txtContrasena" id="txtContrasena" placeholder="••••••••" required>
                    <button type="button" class="toggle-password" tabindex="-1" aria-label="Mostrar contraseña" onclick="togglePassword(this, 'txtContrasena')">
                        <svg class="icon-eye-open" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                        <svg class="icon-eye-closed" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" style="display:none;"><path d="M17.94 17.94A10.94 10.94 0 0 1 12 20c-7 0-11-8-11-8a21.8 21.8 0 0 1 5.06-6.06M9.9 4.24A10.4 10.4 0 0 1 12 4c7 0 11 8 11 8a21.8 21.8 0 0 1-2.16 3.19M14.12 14.12a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
                    </button>
                </div>
            </div>

            <button type="submit" class="btn-gold">Entrar al Sistema</button>
        </form>

        <div class="footer-links" style="margin-top: 1rem;">
            <a href="Vista/RecuperarContrasena.jsp">¿Olvidaste tu <span class="gold-span">contraseña?</span></a>
        </div>
        <div class="footer-links">
            <a href="Registro.jsp">¿No tienes cuenta? <span class="gold-span">Regístrate aquí</span></a>
        </div>
    </div>

    <script>
        function togglePassword(btn, inputId) {
            var input = document.getElementById(inputId);
            var open = btn.querySelector('.icon-eye-open');
            var closed = btn.querySelector('.icon-eye-closed');
            if (input.type === 'password') {
                input.type = 'text';
                open.style.display = 'none';
                closed.style.display = 'block';
                btn.style.color = '#c5a059';
                btn.setAttribute('aria-label', 'Ocultar contraseña');
            } else {
                input.type = 'password';
                open.style.display = 'block';
                closed.style.display = 'none';
                btn.style.color = '#a0a0a0';
                btn.setAttribute('aria-label', 'Mostrar contraseña');
            }
        }
        document.querySelectorAll('.toggle-password').forEach(function (btn) {
            btn.addEventListener('mouseenter', function () { btn.style.background = 'rgba(197,160,89,0.12)'; });
            btn.addEventListener('mouseleave', function () { btn.style.background = 'transparent'; });
        });
    </script>

</body>
</html>