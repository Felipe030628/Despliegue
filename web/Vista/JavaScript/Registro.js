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
});