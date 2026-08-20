/* ==========================================================================
   Efectos de Feedback Visual Premium (Estilo Login) - BarStock
   ========================================================================== */

document.addEventListener("DOMContentLoaded", () => {
    const btnSubmit = document.querySelector(".btn-gold-premium");
    const inputs = document.querySelectorAll(".form-control-premium");

    // Animaciones de escala física e interactividad del botón dorado mate
    if (btnSubmit) {
        btnSubmit.style.transition = "all 0.25s cubic-bezier(0.4, 0, 0.2, 1)";

        btnSubmit.addEventListener("mouseenter", () => {
            // Aumento leve de brillo y microescala idéntica a interfaces móviles modernas
            btnSubmit.style.backgroundColor = "#dfb875";
            btnSubmit.style.transform = "scale(1.015)";
            btnSubmit.style.boxShadow = "0 6px 20px rgba(205, 166, 97, 0.12)";
        });

        btnSubmit.addEventListener("mouseleave", () => {
            btnSubmit.style.backgroundColor = "#cda661";
            btnSubmit.style.transform = "scale(1)";
            btnSubmit.style.boxShadow = "none";
        });

        btnSubmit.addEventListener("mousedown", () => {
            btnSubmit.style.transform = "scale(0.99)";
        });
    }

    // Halo de enfoque perimetral para todos los inputs activos
    inputs.forEach(input => {
        input.addEventListener("focus", () => {
            input.style.boxShadow = "0 0 0 2px rgba(205, 166, 97, 0.2)";
        });

        input.addEventListener("blur", () => {
            input.style.boxShadow = "none";
        });
    });

    /* ======================================================================
       Mostrar / Ocultar contraseña
       ====================================================================== */
    document.querySelectorAll(".btn-toggle-pass").forEach(btn => {
        btn.addEventListener("click", () => {
            const targetId = btn.getAttribute("data-target");
            const input = document.getElementById(targetId);
            const icon = btn.querySelector("i");
            if (!input) return;

            if (input.type === "password") {
                input.type = "text";
                icon.classList.remove("bi-eye");
                icon.classList.add("bi-eye-slash");
            } else {
                input.type = "password";
                icon.classList.remove("bi-eye-slash");
                icon.classList.add("bi-eye");
            }
        });
    });

    /* ======================================================================
       Medidor de seguridad de la contraseña
       ====================================================================== */
    const pass = document.getElementById("txtpass");
    const passConfirm = document.getElementById("txtpassConfirm");
    const strengthBar = document.getElementById("strengthBar");
    const strengthText = document.getElementById("strengthText");
    const matchText = document.getElementById("matchText");
    const form = document.querySelector("form");
    const terminos = document.getElementById("txtterminos");

    function evaluarSeguridad(valor) {
        let puntaje = 0;
        if (valor.length >= 8) puntaje++;
        if (valor.length >= 12) puntaje++;
        if (/[a-z]/.test(valor) && /[A-Z]/.test(valor)) puntaje++;
        if (/\d/.test(valor)) puntaje++;
        if (/[^A-Za-z0-9]/.test(valor)) puntaje++;
        return puntaje; // 0 a 5
    }

    function actualizarMedidor() {
        if (!pass || !strengthBar || !strengthText) return;
        const valor = pass.value;

        if (valor.length === 0) {
            strengthBar.style.width = "0%";
            strengthBar.style.backgroundColor = "#2e2e2e";
            strengthText.textContent = "Introduce una contraseña";
            strengthText.style.color = "#8c8c8c";
            return;
        }

        const puntaje = evaluarSeguridad(valor);
        let porcentaje, color, etiqueta;

        if (puntaje <= 1) {
            porcentaje = 20; color = "#dc3545"; etiqueta = "Muy débil";
        } else if (puntaje === 2) {
            porcentaje = 40; color = "#fd7e14"; etiqueta = "Débil";
        } else if (puntaje === 3) {
            porcentaje = 60; color = "#ffc107"; etiqueta = "Aceptable";
        } else if (puntaje === 4) {
            porcentaje = 80; color = "#9acd32"; etiqueta = "Fuerte";
        } else {
            porcentaje = 100; color = "#198754"; etiqueta = "Muy fuerte";
        }

        strengthBar.style.width = porcentaje + "%";
        strengthBar.style.backgroundColor = color;
        strengthText.textContent = etiqueta;
        strengthText.style.color = color;
    }

    function verificarCoincidencia() {
        if (!pass || !passConfirm || !matchText) return true;
        if (passConfirm.value.length === 0) {
            matchText.textContent = "";
            passConfirm.style.borderColor = "";
            return false;
        }
        if (pass.value === passConfirm.value) {
            matchText.textContent = "Las contraseñas coinciden";
            matchText.style.color = "#4df3a5";
            passConfirm.classList.remove("input-error");
            return true;
        } else {
            matchText.textContent = "Las contraseñas no coinciden";
            matchText.style.color = "#ff7685";
            passConfirm.classList.add("input-error");
            return false;
        }
    }

    if (pass) {
        pass.addEventListener("input", () => {
            actualizarMedidor();
            verificarCoincidencia();
        });
    }

    if (passConfirm) {
        passConfirm.addEventListener("input", verificarCoincidencia);
    }

    /* ======================================================================
       Validación al enviar el formulario
       ====================================================================== */
    if (form) {
        form.addEventListener("submit", (e) => {
            let valido = true;

            if (pass && pass.value.length < 8) {
                valido = false;
                strengthText.textContent = "La contraseña debe tener al menos 8 caracteres";
                strengthText.style.color = "#ff7685";
            }

            if (pass && passConfirm && pass.value !== passConfirm.value) {
                valido = false;
                verificarCoincidencia();
            }

            if (terminos && !terminos.checked) {
                valido = false;
                terminos.classList.add("input-error");
                terminos.focus();
            }

            if (!valido) {
                e.preventDefault();
            }
        });
    }

    if (terminos) {
        terminos.addEventListener("change", () => {
            terminos.classList.remove("input-error");
        });
    }
});
