document.addEventListener("DOMContentLoaded", function() {
    console.log("Sistema de Inventario BarStock cargado correctamente.");

    // Ejemplo: Alerta de confirmación antes de enviar el formulario
    const form = document.getElementById('formProducto');
    if(form) {
        form.addEventListener('submit', function(event) {
            console.log("Enviando nuevo producto al servidor...");
        });
    }
});