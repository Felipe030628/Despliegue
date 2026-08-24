<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Pedidos | BarStock</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Vista/Css/Global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Vista/Css/Pedidos.css">
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
                    <a href="Vista/Panel.jsp"><i class="bi bi-sliders me-2"></i> Dashboard</a>
                </li>

                <!-- Menú Desplegable: Inventario -->
                <li class="nav-item dropdown">
                    <a class="dropdown-toggle" href="#" role="button" data-bs-toggle="collapse" data-bs-target="#menuInventario">
                        <i class="bi bi-box-seam me-2"></i> Inventario
                    </a>
                    <ul class="collapse list-unstyled ps-3" id="menuInventario">
                        <li><a href="${pageContext.request.contextPath}/Producto?accion=listar">Productos</a></li>
                        <li><a href="${pageContext.request.contextPath}/Movimiento?accion=listar"><i class="bi bi-arrow-left-right me-3"></i> Movimientos Stock</a></li>
                    </ul>
                </li>

                <!-- Menú Desplegable: Gestión de Pedidos -->
                <li class="nav-item dropdown">
                    <a class="dropdown-toggle" href="#" role="button" data-bs-toggle="collapse" data-bs-target="#menuPedidos">
                        <i class="bi bi-cart-check me-2"></i> Pedidos
                    </a>
                    <ul class="collapse list-unstyled ps-3 show" id="menuPedidos">
                        <li><a href="${pageContext.request.contextPath}/Pedido?accion=listar"><i class="bi bi-cart me-3"></i> Pedidos</a></li>
                        <li><a href="${pageContext.request.contextPath}/Mesas?accion=listar"><i class="bi bi-ui-checks-grid me-3"></i> Mesas</a></li>
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

            <c:if test="${not empty sessionScope.errorPedido}">
                <div class="alert-stock">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>${sessionScope.errorPedido}
                </div>
                <c:remove var="errorPedido" scope="session"/>
            </c:if>

            <div class="content-card">
                <h5>Nuevo Pedido</h5>
                <form id="formPedido" action="${pageContext.request.contextPath}/Pedido?accion=guardar" method="POST">

                    <div class="row g-3 mb-4">
                        <div class="col-md-4"><input type="text" name="cliente" class="form-control" placeholder="Nombre Cliente" required></div>
                        <div class="col-md-3">
                            <select name="mesa" class="form-select" required>
                                <option value="" disabled selected>Seleccione Mesa</option>
                                <c:forEach var="m" items="${listaMesas}">
                                    <option value="${m.numero_mesa}">${m.numero_mesa} (Cap: ${m.capacidad})</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-5"><input type="datetime-local" name="fecha" class="form-control" required></div>
                    </div>

                    <label class="form-label">Agregar productos al pedido</label>
                    <div class="selector-producto mb-2">
                        <div>
                            <select id="selectProducto" class="form-select">
                                <option value="">Seleccione producto</option>
                                <c:forEach var="p" items="${listaProductos}">
                                    <option value="${p.id}" data-precio="${p.precio}" data-stock="${p.stock}">
                                        ${p.nombre} — $${p.precio} (Stock: ${p.stock})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div>
                            <input type="number" id="inputCantidad" class="form-control" min="1" value="1" placeholder="Cantidad">
                        </div>
                        <div>
                            <!-- reservado para futuras columnas (p.ej. observaciones) -->
                        </div>
                        <div>
                            <button type="button" id="btnAgregarProducto" class="btn btn-gold w-100">
                                <i class="bi bi-plus-lg me-1"></i> Agregar
                            </button>
                        </div>
                    </div>
                    <div id="precioPreview" class="precio-preview"></div>
                    <div id="stockPreview" class="precio-preview"></div>

                    <table class="table table-striped align-middle mt-3" id="tablaCarrito">
                        <thead>
                            <tr>
                                <th>Producto</th>
                                <th>Precio Unit.</th>
                                <th>Cantidad</th>
                                <th>Subtotal</th>
                                <th class="text-center">Quitar</th>
                            </tr>
                        </thead>
                        <tbody id="cuerpoCarrito">
                            <tr class="carrito-vacio"><td colspan="5">Todavía no agregaste productos a este pedido.</td></tr>
                        </tbody>
                    </table>

                    <div class="total-carrito">
                        <span>Total del pedido</span>
                        <strong id="totalCarritoTexto">$0.00</strong>
                    </div>

                    <input type="hidden" id="inputTotalOculto" name="total" value="0">
                    <div id="contenedorInputsOcultos"></div>

                    <div class="d-flex justify-content-end mt-3">
                        <button type="submit" class="btn btn-gold px-4">
                            <i class="bi bi-check2-circle me-1"></i> Registrar Pedido
                        </button>
                    </div>
                </form>
            </div>

            <div class="content-card">
                <h5>Pedidos Registrados</h5>
                <table class="table table-striped align-middle">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Cliente</th>
                            <th>Mesa</th>
                            <th>Fecha</th>
                            <th>Estado</th>
                            <th>Total</th>
                            <th class="text-center">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${lista}">
                            <tr>
                                <td>${p.idPedido}</td>
                                <td>${p.cliente}</td>
                                <td>${p.mesa}</td>
                                <td>${p.fecha}</td>
                                <td>${p.estado}</td>
                                <td>$${p.total}</td>
                                <td>
                                    <button type="button" class="btn btn-icon" title="Ver detalle" onclick="location.href='${pageContext.request.contextPath}/Pedido?accion=ver&id=${p.idPedido}'">
                                        <i class="bi bi-eye-fill"></i>
                                    </button>
                                    <button type="button" class="btn btn-icon" title="Factura PDF" onclick="window.open('${pageContext.request.contextPath}/Pedido?accion=factura&id=${p.idPedido}', '_blank')">
                                        <i class="bi bi-file-earmark-pdf-fill"></i>
                                    </button>
                                    <button type="button" class="btn btn-warning btn-sm" title="Editar" onclick="location.href='${pageContext.request.contextPath}/Pedido?accion=cargar&id=${p.idPedido}'">
                                        <i class="bi bi-pencil-fill"></i>
                                    </button>
                                    <a href="${pageContext.request.contextPath}/Pedido?accion=eliminar&id=${p.idPedido}"
                                       class="btn btn-danger btn-sm"
                                       title="Eliminar (repone el stock)"
                                       onclick="return confirm('¿Estás seguro de que deseas eliminar este pedido? El stock de sus productos será repuesto.');">
                                        <i class="bi bi-trash-fill"></i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </main>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/Vista/JavaScript/Pedidos.js"></script>
</body>
</html>
