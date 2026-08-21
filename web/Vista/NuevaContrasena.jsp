<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nueva Contraseña | BarStock</title>

    <link href="https://fonts.googleapis.com/css2?family=Great+Vibes&family=Montserrat:wght@300;400;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="Css/RecuperarContrasena.css">
</head>
<body>

    <%-- Si el código todavía no ha sido validado en esta sesión, no debe poder llegar aquí --%>
    <%
        if (session.getAttribute("correoResetOk") == null) {
            response.sendRedirect("RecuperarContrasena.jsp");
            return;
        }
    %>

    <div class="verify-card">
        <h1 class="brand-title">BarStock</h1>
        <span class="subtitle">Recuperar Contraseña</span>

        <div class="verify-icon">🔒</div>
        <p class="verify-text">
            Crea tu nueva contraseña. Debe tener al menos 8 caracteres.
        </p>

        <%-- Bloque de mensajes (error) según el parámetro ?status= --%>
        <%
            String status = request.getParameter("status");
            if ("error_pass_corta".equals(status)) {
        %>
            <div class="error-toast">
                La contraseña debe tener al menos 8 caracteres.
            </div>
        <%
            } else if ("error_pass_no_coincide".equals(status)) {
        %>
            <div class="error-toast">
                Las contraseñas no coinciden.
            </div>
        <%
            }
        %>

        <form action="../RecuperarContrasenaCont" method="POST">
            <input type="hidden" name="accion" value="cambiarContrasena">
            <div class="input-group-custom">
                <label>Nueva Contraseña</label>
                <div class="password-wrapper">
                    <input type="password" name="txtpass" id="txtpass" placeholder="••••••••" required minlength="8">
                    <button type="button" class="toggle-password" tabindex="-1" aria-label="Mostrar contraseña" onclick="togglePassword(this, 'txtpass')" style="position:absolute; top:50%; right:6px; transform:translateY(-50%); width:36px; height:36px; display:flex; align-items:center; justify-content:center; background:transparent; border:none; padding:0; margin:0; cursor:pointer; color:#a0a0a0; border-radius:8px;">
                        <svg class="icon-eye-open" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                        <svg class="icon-eye-closed" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" style="display:none;"><path d="M17.94 17.94A10.94 10.94 0 0 1 12 20c-7 0-11-8-11-8a21.8 21.8 0 0 1 5.06-6.06M9.9 4.24A10.4 10.4 0 0 1 12 4c7 0 11 8 11 8a21.8 21.8 0 0 1-2.16 3.19M14.12 14.12a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
                    </button>
                </div>
            </div>
            <div class="input-group-custom">
                <label>Confirmar Contraseña</label>
                <div class="password-wrapper">
                    <input type="password" name="txtpassConfirm" id="txtpassConfirm" placeholder="••••••••" required minlength="8">
                    <button type="button" class="toggle-password" tabindex="-1" aria-label="Mostrar contraseña" onclick="togglePassword(this, 'txtpassConfirm')" style="position:absolute; top:50%; right:6px; transform:translateY(-50%); width:36px; height:36px; display:flex; align-items:center; justify-content:center; background:transparent; border:none; padding:0; margin:0; cursor:pointer; color:#a0a0a0; border-radius:8px;">
                        <svg class="icon-eye-open" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                        <svg class="icon-eye-closed" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" style="display:none;"><path d="M17.94 17.94A10.94 10.94 0 0 1 12 20c-7 0-11-8-11-8a21.8 21.8 0 0 1 5.06-6.06M9.9 4.24A10.4 10.4 0 0 1 12 4c7 0 11 8 11 8a21.8 21.8 0 0 1-2.16 3.19M14.12 14.12a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
                    </button>
                </div>
            </div>
            <button type="submit" class="btn-gold">Guardar Contraseña</button>
        </form>

        <div class="footer-links">
            <a href="../Login.jsp">Volver a <span class="gold-span">Iniciar Sesión</span></a>
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
