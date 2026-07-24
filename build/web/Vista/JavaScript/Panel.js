/* ==========================================================================
   BARSTOCK - CORE ENGINE DEL DASHBOARD INTERACTIVO
   ========================================================================== */

document.addEventListener("DOMContentLoaded", () => {
    
    // 1. RELOJ EN TIEMPO REAL (WIDGET LATERAL)
    const updateLiveTime = () => {
        const timeDisplay = document.getElementById("liveTime");
        const dateDisplay = document.getElementById("liveDate");
        
        if (!timeDisplay || !dateDisplay) return;

        const now = new Date();
        
        // Conversión a cadena de hora legible
        timeDisplay.textContent = now.toLocaleTimeString('es-CO', { hour12: false });
        
        // Conversión a formato de fecha larga
        const opcionesFecha = { weekday: 'long', day: 'numeric', month: 'short' };
        dateDisplay.textContent = now.toLocaleDateString('es-CO', opcionesFecha).toUpperCase();
    };
    
    setInterval(updateLiveTime, 1000);
    updateLiveTime();

    // COLOR PALETTE DE CONTROL PARA CHART.JS
    const goldColor = '#c5a059';
    const darkBorder = '#1a1a1a';
    const textMuted = 'rgba(255, 255, 255, 0.5)';

    // 2. GRÁFICO 1: FLUJO DE VENTAS SEMANAL (LÍNEAS SUAVES SUAVIZADAS)
    const ctxSales = document.getElementById('mainSalesChart');
    if (ctxSales) {
        new Chart(ctxSales, {
            type: 'line',
            data: {
                labels: ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'],
                datasets: [{
                    label: 'Ventas ($)',
                    data: [210000, 340000, 180000, 490000, 645000, 890000, 420000],
                    borderColor: goldColor,
                    backgroundColor: 'rgba(197, 160, 89, 0.05)',
                    fill: true,
                    tension: 0.4, // Curvatura elegante
                    borderWidth: 2,
                    pointBackgroundColor: goldColor
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: {
                    x: { grid: { color: darkBorder }, ticks: { color: textMuted, font: { family: 'Montserrat' } } },
                    y: { grid: { color: darkBorder }, ticks: { color: textMuted, font: { family: 'Montserrat' } } }
                }
            }
        });
    }

    // 3. GRÁFICO 2: PARTICIPACIÓN DE MERCADO (DONA GEOMÉTRICA)
    const ctxDonut = document.getElementById('donutChart');
    if (ctxDonut) {
        new Chart(ctxDonut, {
            type: 'doughnut',
            data: {
                labels: ['Aguardientes', 'Rones', 'Cervezas', 'Otros'],
                datasets: [{
                    data: [45, 25, 20, 10],
                    backgroundColor: [
                        '#c5a059', // Oro Principal
                        'rgba(197, 160, 89, 0.7)', 
                        'rgba(197, 160, 89, 0.4)',
                        '#222222'  // Oscuro neutro para consistencia
                    ],
                    borderWidth: 0
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: { color: textMuted, font: { family: 'Montserrat', size: 10 } }
                    }
                }
            }
        });
    }
});