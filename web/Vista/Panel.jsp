<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel de Control | BarStock</title>
    
    <link href="https://fonts.googleapis.com/css2?family=Great+Vibes&family=Montserrat:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Vista/Css/Panel.css">
</head>
<body>

    <div class="dashboard-container">
        
        <aside class="sidebar">
            <div class="sidebar-header">
                <h3>BarStock</h3>
                <p>Inventario Inteligente</p>
            </div>
            
            <ul class="sidebar-menu">
                <li>
                    <a href="Panel.jsp"><i class="bi bi-sliders me-2"></i> Dashboard</a>
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
                    <input type="text" placeholder="Buscar licores, transacciones, empleados...">
                </div>
                <div class="top-navbar-actions">
                    <button class="icon-nav-btn"><i class="bi bi-envelope"></i><span class="badge-dot"></span></button>
                    <button class="icon-nav-btn"><i class="bi bi-bell"></i><span class="badge-dot"></span></button>
                    <div class="user-badge">
                        <span class="user-role">
                            <i class="bi bi-person me-1"></i>
                            ${sessionScope.usuarioLogueado != null ? sessionScope.usuarioLogueado.nombre : "Administrador"}
                        </span>
                    </div>
                </div>
            </header>

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
                        <div class="progress-group mb-3">
                            <div class="d-flex justify-content-between font-size-sm mb-1">
                                <span>Aguardientes</span>
                                <span class="gold-text">75%</span>
                            </div>
                            <div class="progress-bar-bg"><div class="progress-bar-fill" style="width: 75%;"></div></div>
                        </div>
                        <div class="progress-group mb-3">
                            <div class="d-flex justify-content-between font-size-sm mb-1">
                                <span>Rones</span>
                                <span class="gold-text">55%</span>
                            </div>
                            <div class="progress-bar-bg"><div class="progress-bar-fill" style="width: 55%;"></div></div>
                        </div>
                        <div class="progress-group">
                            <div class="d-flex justify-content-between font-size-sm mb-1">
                                <span>Cervezas Premium</span>
                                <span class="gold-text">30%</span>
                            </div>
                            <div class="progress-bar-bg"><div class="progress-bar-fill" style="width: 30%;"></div></div>
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
                            <a href="${pageContext.request.contextPath}/Vista/Inventario.jsp" class="btn btn-sm btn-outline-warning" style="font-size: 0.65rem; border-color: var(--gold); color: var(--gold);">
                                Ver Todo
                            </a>
                        </div>
                        <ul class="premium-list-items">
                            <li>
                                <div class="d-flex align-items-center gap-3">
                                    <span class="rank-number">1</span>
                                    <div>
                                        <p class="m-0 item-main-text">Antioque&ntilde;o 750ml</p>
                                        <small class="text-muted">34 Ventas hoy</small>
                                    </div>
                                </div>
                                <span class="badge-price">$45k</span>
                            </li>
                            <li>
                                <div class="d-flex align-items-center gap-3">
                                    <span class="rank-number">2</span>
                                    <div>
                                        <p class="m-0 item-main-text">Ron Medell&iacute;n 3 A&ntilde;os</p>
                                        <small class="text-muted">19 Ventas hoy</small>
                                    </div>
                                </div>
                                <span class="badge-price">$52k</span>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>

        </main>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Vista/JavaScript/Panel.js"></script>
    
    <script>
        document.addEventListener("DOMContentLoaded", function() {
        // La línea del alert ya no está aquí
        
        function cargarDatos() {
            fetch('${pageContext.request.contextPath}/DashboardData?t=' + Date.now())
                .then(r => r.json())
                .then(d => {
                    document.getElementById('variedad-stock').innerText = d.variedad + ' Marcas';
                    document.getElementById('stock-critico').innerText = d.critico + ' Por Agotar';
                    document.getElementById('caja-dia').innerText = '$ ' + d.caja.toLocaleString('es-CO');
                })
                .catch(e => console.error('Error:', e));
        }
        
        cargarDatos();
        setInterval(cargarDatos, 5000);
    });
    </script>
</body>
</html>