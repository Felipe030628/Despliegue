/**
 * Sidebar.js
 * ---------------------------------------------------------------
 * Controla el desplegado/plegado de los submenús de la barra
 * lateral (Inventario, Pedidos, etc.) de forma independiente a
 * Bootstrap Collapse, para evitar los problemas de las flechas
 * que no reaccionaban al clic.
 *
 * Marcado esperado en el JSP:
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
 * ---------------------------------------------------------------
 */
(function () {
    'use strict';

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

    function inicializar() {
        var toggles = document.querySelectorAll('.sidebar-menu [data-toggle="submenu"]');

        toggles.forEach(function (toggle) {
            var selector = toggle.getAttribute('data-target') || toggle.getAttribute('href');
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

            // Cuando termina la transición de apertura, se deja "auto"
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

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', inicializar);
    } else {
        inicializar();
    }
})();
