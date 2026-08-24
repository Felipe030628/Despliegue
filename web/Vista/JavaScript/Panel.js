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

    // ==========================================================
    // 4. BOTONES DE LA BARRA SUPERIOR (buzón, campanita, carnet)
    // ==========================================================

    function cerrarTodosLosPopovers(excepto) {
        document.querySelectorAll('.nav-popover.is-open').forEach(pop => {
            if (pop !== excepto) pop.classList.remove('is-open');
        });
    }

    function togglePopover(botonId, popoverId) {
        const boton = document.getElementById(botonId);
        const popover = document.getElementById(popoverId);
        if (!boton || !popover) return;

        boton.addEventListener('click', (e) => {
            e.stopPropagation();
            const yaAbierto = popover.classList.contains('is-open');
            cerrarTodosLosPopovers();
            if (!yaAbierto) popover.classList.add('is-open');
        });
    }

    togglePopover('btnBuzon', 'buzonPopover');
    togglePopover('btnCampana', 'campanaPopover');

    // Cierra los popovers al hacer clic fuera de ellos
    document.addEventListener('click', (e) => {
        if (!e.target.closest('.nav-popover-wrapper')) cerrarTodosLosPopovers();
    });

    // ---- Notificaciones de la campanita, generadas a partir del stock crítico real ----
    function actualizarNotificaciones(d) {
        const lista = document.getElementById('campanaList');
        const badge = document.getElementById('campanaBadge');
        if (!lista) return;

        const critico = Number(d.critico || 0);
        if (critico <= 0) {
            lista.innerHTML = `
                <li>
                    <div class="nav-popover-icon"><i class="bi bi-check2-circle"></i></div>
                    <div>
                        <p class="m-0 item-main-text">Todo en orden</p>
                        <small class="text-muted">No hay productos con stock crítico</small>
                    </div>
                </li>`;
            if (badge) badge.style.display = 'none';
        } else {
            lista.innerHTML = `
                <li>
                    <div class="nav-popover-icon text-danger"><i class="bi bi-exclamation-triangle"></i></div>
                    <div>
                        <p class="m-0 item-main-text">${critico} producto(s) por agotarse</p>
                        <small class="text-muted">Revisa el módulo de Inventario</small>
                    </div>
                </li>`;
            if (badge) badge.style.display = 'block';
        }
    }

    // Engancha las notificaciones al mismo ciclo de polling del panel
    function actualizarPanelConNotificaciones() {
        fetch(BASE_URL + '/DashboardData?t=' + Date.now())
            .then(r => r.json())
            .then(d => {
                if (d.error) return;
                actualizarNotificaciones(d);
            })
            .catch(() => {});
    }
    setInterval(actualizarPanelConNotificaciones, 5000);
    actualizarPanelConNotificaciones();

    // ---- Mini carnet del usuario ----
    const carnetOverlay = document.getElementById('carnetOverlay');
    const btnUserBadge = document.getElementById('btnUserBadge');
    const carnetClose = document.getElementById('carnetClose');
    const carnetFecha = document.getElementById('carnetFecha');

    function abrirCarnet() {
        if (!carnetOverlay) return;
        if (carnetFecha) {
            const hoy = new Date();
            carnetFecha.textContent = hoy.toLocaleDateString('es-CO', { day: 'numeric', month: 'short', year: 'numeric' });
        }
        carnetOverlay.classList.add('is-open');
    }
    function cerrarCarnet() {
        if (carnetOverlay) carnetOverlay.classList.remove('is-open');
    }

    if (btnUserBadge) {
        btnUserBadge.addEventListener('click', abrirCarnet);
        btnUserBadge.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' || e.key === ' ') abrirCarnet();
        });
    }
    if (carnetClose) carnetClose.addEventListener('click', cerrarCarnet);
    if (carnetOverlay) {
        carnetOverlay.addEventListener('click', (e) => {
            if (e.target === carnetOverlay) cerrarCarnet();
        });
    }
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') cerrarCarnet();
    });

    // ==========================================================
    // 5. BUSCADOR GLOBAL DEL DASHBOARD (productos + empleados reales)
    //    Consulta /BuscarGlobal con debounce y muestra un desplegable
    //    con las coincidencias directamente desde la base de datos.
    // ==========================================================
    const searchInput = document.getElementById('dashboardSearchInput');
    const searchPanel = document.getElementById('searchResultsPanel');
    const searchProductosList = document.getElementById('searchProductosList');
    const searchEmpleadosList = document.getElementById('searchEmpleadosList');

    let searchDebounceTimer = null;
    let searchAbortController = null;

    function escaparHtmlBusqueda(texto) {
        const div = document.createElement('div');
        div.innerText = texto == null ? '' : String(texto);
        return div.innerHTML;
    }

    function cerrarPanelBusqueda() {
        if (searchPanel) searchPanel.classList.remove('is-open');
    }

    function renderizarResultadosBusqueda(data) {
        if (!searchProductosList || !searchEmpleadosList) return;

        const productos = data.productos || [];
        const empleados = data.empleados || [];

        // Productos → enlazan directo a su edición en el módulo de Inventario
        if (productos.length === 0) {
            searchProductosList.innerHTML = '<li class="search-results-empty">Sin coincidencias</li>';
        } else {
            searchProductosList.innerHTML = productos.map(p => `
                <li>
                    <a href="${BASE_URL}/Producto?accion=cargar&id=${p.id}">
                        <span>${escaparHtmlBusqueda(p.nombre)}</span>
                        <span class="result-sub">$${Number(p.precio || 0).toLocaleString('es-CO')}</span>
                    </a>
                </li>`).join('');
        }

        // Empleados → enlazan directo a su edición en el módulo de Empleados
        if (empleados.length === 0) {
            searchEmpleadosList.innerHTML = '<li class="search-results-empty">Sin coincidencias</li>';
        } else {
            searchEmpleadosList.innerHTML = empleados.map(u => `
                <li>
                    <a href="${BASE_URL}/UsuariosCont?accion=cargar&id=${u.id}">
                        <span>${escaparHtmlBusqueda(u.nombre)}</span>
                        <span class="result-sub">${escaparHtmlBusqueda(u.correo)}</span>
                    </a>
                </li>`).join('');
        }

        if (searchPanel) searchPanel.classList.add('is-open');
    }

    function ejecutarBusquedaGlobal(termino) {
        if (searchAbortController) searchAbortController.abort();
        searchAbortController = new AbortController();

        fetch(BASE_URL + '/BuscarGlobal?termino=' + encodeURIComponent(termino), {
            signal: searchAbortController.signal
        })
            .then(r => r.json())
            .then(data => {
                if (data.error) return;
                renderizarResultadosBusqueda(data);
            })
            .catch(e => {
                if (e.name !== 'AbortError') console.error('Error en la búsqueda global:', e);
            });
    }

    if (searchInput) {
        searchInput.addEventListener('input', () => {
            const termino = searchInput.value.trim();

            clearTimeout(searchDebounceTimer);

            if (termino.length < 2) {
                cerrarPanelBusqueda();
                return;
            }

            searchDebounceTimer = setTimeout(() => ejecutarBusquedaGlobal(termino), 300);
        });

        searchInput.addEventListener('focus', () => {
            if (searchInput.value.trim().length >= 2 &&
                (searchProductosList.innerHTML || searchEmpleadosList.innerHTML)) {
                searchPanel.classList.add('is-open');
            }
        });
    }

    // Cierra el desplegable al hacer clic fuera del buscador
    document.addEventListener('click', (e) => {
        if (!e.target.closest('.search-box')) cerrarPanelBusqueda();
    });
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') cerrarPanelBusqueda();
    });
});
