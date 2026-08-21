/* ==========================================================================
   BARSTOCK - CORE ENGINE DEL DASHBOARD INTERACTIVO
   Todo el panel (KPIs, gráficas, categorías y top de productos) se alimenta
   en tiempo real desde /DashboardData mediante polling cada 5 segundos.
   ========================================================================== */

document.addEventListener("DOMContentLoaded", () => {

    // Context path expuesto por Panel.jsp (window.APP_CONTEXT_PATH)
    const BASE_URL = window.APP_CONTEXT_PATH || "";

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

    let mainSalesChart = null;
    let donutChart = null;

    // 2. GRÁFICO 1: FLUJO DE VENTAS SEMANAL (arranca vacío, se llena con datos reales)
    const ctxSales = document.getElementById('mainSalesChart');
    if (ctxSales) {
        mainSalesChart = new Chart(ctxSales, {
            type: 'line',
            data: {
                labels: [],
                datasets: [{
                    label: 'Ventas ($)',
                    data: [],
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

    // 3. GRÁFICO 2: PARTICIPACIÓN DE MERCADO (arranca vacío, se llena con categorías reales)
    const ctxDonut = document.getElementById('donutChart');
    if (ctxDonut) {
        donutChart = new Chart(ctxDonut, {
            type: 'doughnut',
            data: {
                labels: [],
                datasets: [{
                    data: [],
                    backgroundColor: [
                        '#c5a059', // Oro Principal
                        'rgba(197, 160, 89, 0.7)',
                        'rgba(197, 160, 89, 0.4)',
                        '#222222', // Oscuro neutro para consistencia
                        'rgba(255, 255, 255, 0.25)',
                        'rgba(197, 160, 89, 0.2)'
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

    // ---- Helpers de actualización (cada uno toca solo su parte del panel) ----

    function actualizarKPIs(d) {
        const variedadEl = document.getElementById('variedad-stock');
        const criticoEl = document.getElementById('stock-critico');
        const cajaEl = document.getElementById('caja-dia');

        if (variedadEl) variedadEl.innerText = d.variedad + ' Marcas';
        if (criticoEl) criticoEl.innerText = d.critico + ' Por Agotar';
        if (cajaEl) cajaEl.innerText = '$ ' + Number(d.caja || 0).toLocaleString('es-CO');
    }

    function actualizarGraficoVentas(d) {
        if (!mainSalesChart || !d.ventasSemana) return;
        mainSalesChart.data.labels = d.ventasSemana.labels || [];
        mainSalesChart.data.datasets[0].data = d.ventasSemana.data || [];
        mainSalesChart.update();
    }

    function actualizarCategorias(d) {
        if (!d.categorias) return;

        // Dona de participación por categoría
        if (donutChart) {
            donutChart.data.labels = d.categorias.labels || [];
            donutChart.data.datasets[0].data = d.categorias.data || [];
            donutChart.update();
        }

        // Barras de "Rendimiento de Categorías" (top 3, mismo dato real de la dona)
        const contenedor = document.getElementById('categoriasProgress');
        if (!contenedor) return;

        const labels = d.categorias.labels || [];
        const valores = d.categorias.data || [];

        if (labels.length === 0) {
            contenedor.innerHTML = '<p class="text-muted small mb-0">Sin categorías registradas.</p>';
            return;
        }

        const maxValor = Math.max.apply(null, valores.concat([1]));
        let html = '';
        labels.slice(0, 3).forEach((nombre, i) => {
            const valor = valores[i] || 0;
            const porcentaje = Math.round((valor / maxValor) * 100);
            html += `
                <div class="progress-group mb-3">
                    <div class="d-flex justify-content-between font-size-sm mb-1">
                        <span>${escaparHtml(nombre)}</span>
                        <span class="gold-text">${porcentaje}%</span>
                    </div>
                    <div class="progress-bar-bg"><div class="progress-bar-fill" style="width: ${porcentaje}%;"></div></div>
                </div>`;
        });
        contenedor.innerHTML = html;
    }

    function actualizarTopProductos(d) {
        const lista = document.getElementById('topProductosList');
        if (!lista) return;

        const productos = d.topProductos || [];
        if (productos.length === 0) {
            lista.innerHTML = '<li><small class="text-muted">Aún no hay salidas de stock registradas.</small></li>';
            return;
        }

        let html = '';
        productos.forEach((p, index) => {
            html += `
                <li>
                    <div class="d-flex align-items-center gap-3">
                        <span class="rank-number">${index + 1}</span>
                        <div>
                            <p class="m-0 item-main-text">${escaparHtml(p.nombre)}</p>
                            <small class="text-muted">${p.cantidad} salidas registradas</small>
                        </div>
                    </div>
                    <span class="badge-price">$${Number(p.monto || 0).toLocaleString('es-CO')}</span>
                </li>`;
        });
        lista.innerHTML = html;
    }

    function escaparHtml(texto) {
        const div = document.createElement('div');
        div.innerText = texto == null ? '' : String(texto);
        return div.innerHTML;
    }

    // ---- Ciclo de actualización en tiempo real ----
    function actualizarPanelCompleto() {
        fetch(BASE_URL + '/DashboardData?t=' + Date.now())
            .then(r => r.json())
            .then(d => {
                if (d.error) {
                    console.error('DashboardData respondió con error:', d.error);
                    return;
                }
                actualizarKPIs(d);
                actualizarGraficoVentas(d);
                actualizarCategorias(d);
                actualizarTopProductos(d);
            })
            .catch(e => console.error('Error al actualizar el panel:', e));
    }

    actualizarPanelCompleto();
    setInterval(actualizarPanelCompleto, 5000);
});
