<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel de Control | BarStock</title>

    <link href="https://fonts.googleapis.com/css2?family=Great+Vibes&family=Fraunces:opsz,wght@9..144,300;9..144,500;9..144,600;9..144,700&family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/Vista/Css/Panel.css">
</head>
<body>

    <!-- PANTALLA DE CARGA -->
    <div id="loadingScreen" class="loading-screen">
        <div class="loading-content">
            <h1 class="loading-logo">BarStock</h1>
            <div class="loading-glass">
                <div class="loading-bubble"></div>
                <div class="loading-bubble"></div>
                <div class="loading-bubble"></div>
                <div class="loading-liquid"></div>
            </div>
            <p class="loading-text">Sirviendo tu inventario<span class="dot">.</span><span class="dot">.</span><span class="dot">.</span></p>
        </div>
    </div>

    <div class="dashboard-container">

        <aside class="sidebar">
            <div class="sidebar-header">
                <h3>BarStock</h3>
                <p>Inventario Inteligente</p>
            </div>

            <ul class="sidebar-menu">
                <li>
                    <a href="Panel.jsp" class="active"><i class="bi bi-sliders me-2"></i> Dashboard</a>
                </li>

                <li class="nav-item dropdown">
                    <a class="dropdown-toggle" href="#" role="button" data-bs-toggle="collapse" data-bs-target="#menuInventario">
                        <i class="bi bi-box-seam me-2"></i> Inventario
                    </a>
                    <ul class="collapse list-unstyled ps-3" id="menuInventario">
                        <li><a href="${pageContext.request.contextPath}/Producto?accion=listar">Productos</a></li>
                        <li><a href="${pageContext.request.contextPath}/Movimiento?accion=listar"><i class="bi bi-arrow-left-right me-3"></i> Movimientos Stock</a></li>
                    </ul>
                </li>

                <li class="nav-item dropdown">
                    <a class="dropdown-toggle" href="#" role="button" data-bs-toggle="collapse" data-bs-target="#menuPedidos">
                        <i class="bi bi-cart-check me-2"></i> Pedidos
                    </a>
                    <ul class="collapse list-unstyled ps-3" id="menuPedidos">
                        <li><a href="${pageContext.request.contextPath}/Pedido?accion=listar">
                        <i class="bi bi-cart me-3"></i> Pedidos
                    </a></li>
                    <li>
                    <a href="${pageContext.request.contextPath}/Mesas?accion=listar">
                        <i class="bi bi-ui-checks-grid me-3"></i> Mesas
                    </a>
                    </li>
                    </ul>
                </li>

                <li>
                    <a href="${pageContext.request.contextPath}/UsuariosCont?accion=listar">
                    <i class="bi bi-people me-3"></i> Empleados
                    </a>
                </li>

                <li class="mt-5">
                    <a href="${pageContext.request.contextPath}/index.jsp" class="logout-link">
                        <i class="bi bi-power me-2"></i> Cerrar Sesión
                    </a>
                </li>
            </ul>
        </aside>

        <main class="main-content">

            <header class="top-navbar">
                <div class="search-box">
                    <i class="bi bi-search"></i>
                    <input type="text" id="dashboardSearchInput" placeholder="Buscar licores, transacciones, empleados..." autocomplete="off">
                    <div class="search-results-panel" id="searchResultsPanel">
                        <div class="search-results-section">
                            <div class="search-results-title">Productos</div>
                            <ul class="search-results-list" id="searchProductosList"></ul>
                        </div>
                        <div class="search-results-section">
                            <div class="search-results-title">Empleados</div>
                            <ul class="search-results-list" id="searchEmpleadosList"></ul>
                        </div>
                    </div>
                </div>
                <div class="top-navbar-actions">

                    <div class="nav-popover-wrapper">
                        <button class="icon-nav-btn" id="btnBuzon" type="button"><i class="bi bi-envelope"></i><span class="badge-dot" id="buzonBadge"></span></button>
                        <div class="nav-popover" id="buzonPopover">
                            <div class="nav-popover-header">
                                <span>Buzón de mensajes</span>
                            </div>
                            <ul class="nav-popover-list" id="buzonList">
                                <li>
                                    <div class="nav-popover-icon"><i class="bi bi-person-circle"></i></div>
                                    <div>
                                        <p class="m-0 item-main-text">Sin mensajes nuevos</p>
                                        <small class="text-muted">Aquí verás tus mensajes internos</small>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>

                    <div class="nav-popover-wrapper">
                        <button class="icon-nav-btn" id="btnCampana" type="button"><i class="bi bi-bell"></i><span class="badge-dot" id="campanaBadge"></span></button>
                        <div class="nav-popover" id="campanaPopover">
                            <div class="nav-popover-header">
                                <span>Notificaciones</span>
                            </div>
                            <ul class="nav-popover-list" id="campanaList">
                                <li>
                                    <div class="nav-popover-icon text-danger"><i class="bi bi-exclamation-triangle"></i></div>
                                    <div>
                                        <p class="m-0 item-main-text">Cargando notificaciones...</p>
                                        <small class="text-muted">Se generan según el stock crítico</small>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>

                    <div class="user-badge" id="btnUserBadge" role="button" tabindex="0">
                        <span class="user-role">
                            <i class="bi bi-person me-1"></i>
                            ${sessionScope.usuarioLogueado != null ? sessionScope.usuarioLogueado.nombre : "Administrador"}
                        </span>
                    </div>
                </div>
            </header>

            <!-- ===== MINI CARNET DE USUARIO ===== -->
            <div class="carnet-overlay" id="carnetOverlay">
                <div class="carnet-card" id="carnetCard">
                    <button class="carnet-close" id="carnetClose" type="button" aria-label="Cerrar">
                        <i class="bi bi-x-lg"></i>
                    </button>

                    <div class="carnet-brand">
                        <span>BarStock</span>
                        <small>Credencial de Empleado</small>
                    </div>

                    <div class="carnet-photo">
                        <i class="bi bi-person-fill"></i>
                    </div>

                    <h3 class="carnet-nombre">
                        ${sessionScope.usuarioLogueado != null ?
                            sessionScope.usuarioLogueado.nombre.concat(' ').concat(sessionScope.usuarioLogueado.apellido != null ? sessionScope.usuarioLogueado.apellido : '')
                            : 'Administrador'}
                    </h3>
                    <p class="carnet-rol" id="carnetRol">
                        <c:choose>
                            <c:when test="${sessionScope.usuarioLogueado != null and sessionScope.usuarioLogueado.idRol == 1}">Administrador</c:when>
                            <c:when test="${sessionScope.usuarioLogueado != null and sessionScope.usuarioLogueado.idRol == 2}">Empleado</c:when>
                            <c:otherwise>Administrador</c:otherwise>
                        </c:choose>
                    </p>

                    <div class="carnet-divider"></div>

                    <div class="carnet-detalle">
                        <div class="carnet-detalle-item">
                            <i class="bi bi-envelope"></i>
                            <span>${sessionScope.usuarioLogueado != null ? sessionScope.usuarioLogueado.correo : 'No disponible'}</span>
                        </div>
                        <div class="carnet-detalle-item">
                            <i class="bi bi-telephone"></i>
                            <span>${sessionScope.usuarioLogueado != null ? sessionScope.usuarioLogueado.telefono : 'No disponible'}</span>
                        </div>
                        <div class="carnet-detalle-item">
                            <i class="bi bi-card-text"></i>
                            <span>ID Empleado: #${sessionScope.usuarioLogueado != null ? sessionScope.usuarioLogueado.idUsuarios : '000'}</span>
                        </div>
                    </div>

                    <div class="carnet-footer">
                        <span class="carnet-estado"><i class="bi bi-check-circle-fill"></i> Sesión activa</span>
                        <span class="carnet-fecha" id="carnetFecha"></span>
                    </div>
                </div>
            </div>

            <div class="row g-3 mb-4">
                <div class="col-md-4">
                    <div class="kpi-mini-card">
                        <div class="kpi-mini-icon"><i class="bi bi-vessel-front"></i></div>
                        <div>
                            <div class="kpi-mini-title">Variedad Stock</div>
                            <div class="kpi-mini-value" id="variedad-stock">Cargando...</div>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="kpi-mini-card">
                        <div class="kpi-mini-icon text-danger"><i class="bi bi-exclamation-triangle"></i></div>
                        <div>
                            <div class="kpi-mini-title">Stock Crítico</div>
                            <div class="kpi-mini-value text-danger" id="stock-critico">Cargando...</div>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="kpi-mini-card">
                        <div class="kpi-mini-icon text-success"><i class="bi bi-cash-stack"></i></div>
                        <div>
                            <div class="kpi-mini-title">Caja del Día</div>
                            <div class="kpi-mini-value" id="caja-dia">Cargando...</div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row g-3 mb-4">
                <div class="col-lg-8">
                    <div class="dashboard-widget">
                        <div class="widget-header">
                            <h5>Flujo de Ventas Semanal</h5>
                        </div>
                        <div class="chart-container">
                            <canvas id="mainSalesChart"></canvas>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4">
                    <div class="dashboard-widget premium-gradient-widget text-center d-flex flex-column justify-content-center align-items-center">
                        <div class="widget-weather-status">
                            <i class="bi bi-brightness-high gold-text fs-1"></i>
                            <h3 class="mt-2 mb-0 fw-bold">Bogot&aacute;</h3>
                        </div>
                        <div class="widget-date-display my-3">
                            <h1 id="liveTime" class="fw-bold m-0 text-white">00:00:00</h1>
                            <p id="liveDate" class="gold-text uppercase tracking-wide fs-7 m-0">Cargando fecha...</p>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row g-3">
                <div class="col-md-4">
                    <div class="dashboard-widget">
                        <div class="widget-header">
                            <h5>Rendimiento de Categorías</h5>
                        </div>
                        <!-- Se llena dinámicamente desde DashboardData (distribución real por categoría) -->
                        <div id="categoriasProgress">
                            <p class="text-muted small mb-0">Cargando categorías...</p>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="dashboard-widget d-flex flex-column align-items-center justify-content-center">
                        <div class="widget-header w-100 text-start">
                            <h5>Participación de Mercado</h5>
                        </div>
                        <div class="chart-container-donut">
                            <canvas id="donutChart"></canvas>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="dashboard-widget">
                        <div class="widget-header d-flex justify-content-between align-items-center">
                            <h5>Más Solicitados (Top)</h5>
                            <a href="${pageContext.request.contextPath}/Vista/Inventario.jsp" class="btn btn-sm btn-outline-warning" style="font-size: 0.65rem;">
                                Ver Todo
                            </a>
                        </div>
                        <!-- Se llena dinámicamente desde DashboardData (top real por salidas de stock) -->
                        <ul class="premium-list-items" id="topProductosList">
                            <li><small class="text-muted">Cargando productos...</small></li>
                        </ul>
                    </div>
                </div>
            </div>

        </main>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // Context path del proyecto, expuesto para que Panel.js (archivo externo)
        // pueda armar la URL del servlet sin necesitar EL/JSP.
        window.APP_CONTEXT_PATH = '${pageContext.request.contextPath}';
    </script>
    <script src="${pageContext.request.contextPath}/Vista/JavaScript/Panel.js"></script>

    <script>
        // ---- Pantalla de carga: se oculta cuando el panel ya está listo ----
        (function () {
            var loader = document.getElementById('loadingScreen');
            var TIEMPO_MINIMO = 2600; // ms — deja ver la copa llenarse completa
            var inicio = Date.now();

            function ocultarLoader() {
                var transcurrido = Date.now() - inicio;
                var espera = Math.max(TIEMPO_MINIMO - transcurrido, 0);
                setTimeout(function () {
                    loader.classList.add('is-hidden');
                    setTimeout(function () { loader.remove(); }, 750);
                }, espera);
            }

            if (document.readyState === 'complete') {
                ocultarLoader();
            } else {
                window.addEventListener('load', ocultarLoader);
            }
        })();
    </script>
</body>
</html>
