/**
 * Sidebar.js
 * ---------------------------------------------------------------
 * Controla dos comportamientos de la barra lateral de BarStock:
 *
 *   1) Los submenús desplegables (Inventario, Pedidos, etc.), de
 *      forma independiente a Bootstrap Collapse, para evitar los
 *      problemas de las flechas que no reaccionaban al clic.
 *
 *   2) El plegado/colapso de toda la barra lateral, mediante el
 *      botón circular ubicado en su borde derecho. El estado
 *      (abierta/colapsada) se guarda en localStorage para que se
 *      mantenga al navegar entre las distintas páginas del panel,
 *      ya que cada una es una carga completa de página (JSP).
 *
 * Marcado esperado en el JSP para los submenús:
 *
 *   <a class="dropdown-toggle" href="#" role="button"
 *      data-toggle="submenu" data-target="#menuInventario">
 *      ...
 *   </a>
 *   <ul class="submenu list-unstyled ps-3" id="menuInventario">
 *      ...
 *   </ul>
 *
 * El botón puede además traer "show" ya puesto en el <ul> por el
 * propio JSP (para que el submenú de la sección activa aparezca
 * abierto al cargar la página). Este script respeta ese estado
 * inicial y calcula la altura real para animarlo correctamente.
 *
 * Marcado esperado para el colapso de toda la barra:
 *
 *   <div class="dashboard-container">
 *       <button class="sidebar-toggle-btn" id="sidebarCollapseBtn"
 *               type="button" aria-label="Mostrar u ocultar menú">
 *           <i class="bi bi-chevron-left"></i>
 *       </button>
 *       <aside class="sidebar">...</aside>
 *       <main class="main-content">...</main>
 *   </div>
 * ---------------------------------------------------------------
 */
(function () {
    'use strict';

    /* ============================================================
       1. SUBMENÚS DESPLEGABLES (Inventario, Pedidos, etc.)
       ============================================================ */

    function alturaReal(el) {
        // Mide la altura real del contenido (scrollHeight) para animar
        // con precisión, sin depender de un valor fijo en el CSS.
        return el.scrollHeight;
    }

    function abrirSubmenu(toggle, submenu) {
        submenu.classList.add('show');
        submenu.style.maxHeight = alturaReal(submenu) + 'px';
        toggle.setAttribute('aria-expanded', 'true');
    }

    function cerrarSubmenu(toggle, submenu) {
        // Fija la altura actual antes de animar a 0 (si no, no hay transición).
        submenu.style.maxHeight = alturaReal(submenu) + 'px';
        // Forzar reflow para que el navegador registre el valor anterior
        // antes de aplicar el nuevo (si no, salta directo a 0 sin animar).
        // eslint-disable-next-line no-unused-expressions
        submenu.offsetHeight;
        submenu.style.maxHeight = '0px';
        submenu.classList.remove('show');
        toggle.setAttribute('aria-expanded', 'false');
    }

    function alternarSubmenu(toggle, submenu) {
        var estaAbierto = submenu.classList.contains('show');
        if (estaAbierto) {
            cerrarSubmenu(toggle, submenu);
        } else {
            abrirSubmenu(toggle, submenu);
        }
    }

    function inicializarSubmenus() {
        var toggles = document.querySelectorAll('.sidebar-menu [data-toggle="submenu"]');

        toggles.forEach(function (toggle) {
            var selector = toggle.getAttribute('data-target');
            if (!selector || selector === '#') return;

            var submenu = document.querySelector(selector);
            if (!submenu) return;

            // Estado inicial: si el JSP ya marcó "show" (sección activa),
            // dejamos el submenú abierto con su altura real calculada.
            if (submenu.classList.contains('show')) {
                toggle.setAttribute('aria-expanded', 'true');
                submenu.style.maxHeight = alturaReal(submenu) + 'px';
            } else {
                toggle.setAttribute('aria-expanded', 'false');
                submenu.style.maxHeight = '0px';
            }

            toggle.addEventListener('click', function (e) {
                e.preventDefault();
                alternarSubmenu(toggle, submenu);
            });

            // Cuando termina la transición de apertura, se deja "auto" / "none"
            // para que el contenido pueda crecer sin quedar recortado
            // (por ejemplo si se agregan más ítems al menú).
            submenu.addEventListener('transitionend', function (e) {
                if (e.propertyName === 'max-height' && submenu.classList.contains('show')) {
                    submenu.style.maxHeight = 'none';
                }
            });

            // Si la ventana cambia de tamaño y el submenú está abierto con
            // "none", lo recalculamos por si el contenido cambió de alto.
            window.addEventListener('resize', function () {
                if (submenu.classList.contains('show')) {
                    submenu.style.maxHeight = 'none';
                }
            });
        });
    }

    /* ============================================================
       2. COLAPSO / EXPANSIÓN DE TODA LA BARRA LATERAL
       ============================================================ */

    var STORAGE_KEY = 'barstock_sidebar_collapsed';

    function inicializarColapsoBarra() {
        var contenedor = document.querySelector('.dashboard-container');
        var boton = document.getElementById('sidebarCollapseBtn');
        if (!contenedor || !boton) return;

        function aplicarEstado(colapsada) {
            contenedor.classList.toggle('sidebar-collapsed', colapsada);
            boton.setAttribute('aria-expanded', String(!colapsada));
            boton.setAttribute('title', colapsada ? 'Expandir menú' : 'Colapsar menú');
        }

        // Recupera el último estado elegido por el usuario (persiste entre páginas).
        var guardado = localStorage.getItem(STORAGE_KEY) === '1';
        aplicarEstado(guardado);

        boton.addEventListener('click', function () {
            var colapsada = !contenedor.classList.contains('sidebar-collapsed');
            aplicarEstado(colapsada);
            try {
                localStorage.setItem(STORAGE_KEY, colapsada ? '1' : '0');
            } catch (err) {
                // Si localStorage no está disponible, simplemente no persiste.
            }
        });
    }

    /* ============================================================
       INICIALIZACIÓN
       ============================================================ */

    function inicializar() {
        inicializarSubmenus();
        inicializarColapsoBarra();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', inicializar);
    } else {
        inicializar();
    }
})();
