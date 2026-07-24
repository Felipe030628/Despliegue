<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BarStock | Gestión Premium</title>

    <link href="https://fonts.googleapis.com/css2?family=Great+Vibes&family=Montserrat:wght@300;400;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <link rel="stylesheet" href="Vista/Css/Home.css">
</head>
<body>

    <!-- NAVBAR -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark-premium sticky-top">
        <div class="container">

            <a class="navbar-brand logo-mini" href="#inicio">
                BarStock
            </a>

            <button class="navbar-toggler"
                    type="button"
                    data-bs-toggle="collapse"
                    data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarNav">

                <ul class="navbar-nav me-auto">

                    <li class="nav-item">
                        <a class="nav-link" href="#inicio">Inicio</a>
                    </li>

                    <li class="nav-item">
                        <a class="nav-link" href="#historia">Historia</a>
                    </li>

                    <li class="nav-item">
                        <a class="nav-link" href="#ficha">Ficha Técnica</a>
                    </li>

                    <li class="nav-item">
                        <a class="nav-link" href="#modulos">Módulos</a>
                    </li>

                </ul>

                <div class="d-flex gap-2">

                    <a href="Login.jsp" class="btn-login-nav">
                        INICIAR SESIÓN
                    </a>

                    <a href="Registro.jsp"
                       class="btn-login-nav"
                       style="background: transparent; border: 1px solid #d4af37; color: #d4af37;">
                        REGISTRARSE
                    </a>

                </div>

            </div>
        </div>
    </nav>

    <!-- CARRUSEL -->
    <div id="inicio"
         class="carousel slide carousel-fade"
         data-bs-ride="carousel">

        <div class="carousel-inner">

            <!-- ITEM 1 -->
            <div class="carousel-item active">

                <img src="https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?q=80&w=1470"
                     class="d-block w-100"
                     alt="Cocktail">

                <div class="carousel-overlay"></div>

                <div class="carousel-caption">

                    <h1 class="logo-text">BarStock</h1>

                    <p class="fancy-font">
                        Excelencia en control, pasión por el servicio
                    </p>

                    <h2 class="welcome-msg">
                        BIENVENIDO AL FUTURO DE TU BAR
                    </h2>

                </div>
            </div>

            <!-- ITEM 2 -->
            <div class="carousel-item">

                <img src="https://images.unsplash.com/photo-1470337458703-46ad1756a187?q=80&w=1469"
                     class="d-block w-100"
                     alt="Bar">

                <div class="carousel-overlay"></div>

                <div class="carousel-caption">

                    <h1 class="logo-text">BarStock</h1>

                    <p class="fancy-font">
                        Inventario Inteligente
                    </p>

                    <h2 class="welcome-msg">
                        CONTROL EN TIEMPO REAL
                    </h2>

                </div>
            </div>

        </div>
    </div>

    <!-- FICHA TECNICA -->
    <section id="ficha" class="container my-5 py-5">

        <div class="row align-items-center">

            <div class="col-md-6">

                <h3 class="gold-text">FICHA TÉCNICA</h3>

                <div class="gold-line"></div>

                <ul class="tech-list text-light">

                    <li>
                        <strong>Propósito:</strong>
                        Gestión de stock y ventas para bares.
                    </li>

                    <li>
                        <strong>Tecnología:</strong>
                        Arquitectura MVC con Java EE y Servlets.
                    </li>

                    <li>
                        <strong>Base de Datos:</strong>
                        MySQL Workbench (Usuarios y Roles).
                    </li>

                    <li>
                        <strong>Interfaz:</strong>
                        Diseño Premium Lounge con Bootstrap 5.
                    </li>

                </ul>

            </div>

            <div class="col-md-6">

                <div class="tech-card text-center">

                    <h4 class="gold-text">BARSTOCK v1.0</h4>

                    <hr class="border-gold">

                    <p class="small">
                        Sincronización de Inventarios
                    </p>

                </div>

            </div>

        </div>

    </section>

    <!-- HISTORIA -->
    <section id="historia" class="container my-5 py-5">

        <div class="row align-items-center">

            <div class="col-md-7">

                <h3 class="gold-text">NUESTRA HISTORIA</h3>

                <div class="gold-line"></div>

                <p class="text-light lead">
                    BarStock nació de la observación directa en el sector nocturno de la ciudad.
                    Notamos que muchos bares aún dependen de procesos manuales y registros en papel.
                </p>

                <div class="problem-box p-4 mb-4">

                    <h5 class="text-danger">La Problemática:</h5>

                    <p class="small text-light">
                        La falta de un control digital genera "fugas" de inventario,
                        errores en el flujo de caja y una desconexión total entre lo que hay
                        en bodega y lo que se vende en la barra.
                        Esto resulta en pérdidas económicas significativas y una mala experiencia
                        para el cliente.
                    </p>

                </div>

                <p class="text-light">
                    <strong>Nuestra base:</strong>
                    Nos inspiramos en la eficiencia de los grandes sistemas logísticos
                    para traer esa misma precisión a los bares locales,
                    facilitando una herramienta robusta pero intuitiva.
                </p>

            </div>

            <div class="col-md-5 text-center">

                <div class="history-decoration">

                    <span class="fancy-font" style="font-size: 3rem;">
                        Desde la Barra
                    </span>

                    <p class="gold-text">
                        Hacia la Digitalización
                    </p>

                </div>

            </div>

        </div>

    </section>

    <!-- MODULOS -->
    <section id="modulos" class="container my-5 py-5">

        <h3 class="gold-text text-center">
            MÓDULOS DEL SISTEMA
        </h3>

        <div class="gold-line mx-auto"></div>

        <div class="row mt-5 text-center">

            <div class="col-md-4">

                <div class="modulo-box">

                    <h5>📦 Inventario</h5>

                    <p class="small">
                        Control de botellas y suministros.
                    </p>

                </div>

            </div>

            <div class="col-md-4">

                <div class="modulo-box">

                    <h5>👥 Usuarios</h5>

                    <p class="small">
                        Gestión de roles y permisos.
                    </p>

                </div>

            </div>

            <div class="col-md-4">

                <div class="modulo-box">

                    <h5>💰 Ventas</h5>

                    <p class="small">
                        Registro de consumo en tiempo real.
                    </p>

                </div>

            </div>

        </div>

    </section>

    <!-- CTA -->
    <section class="join-team-section text-center py-5 my-5">

        <div class="container">

            <h3 class="fancy-font CTA-title">
                ¿Quieres ser parte del equipo de BarStock?
            </h3>

            <p class="text-light text-muted-premium mb-4 mx-auto"
               style="max-width: 600px;">

                Regístrate hoy mismo y descubre la manera más ágil,
                elegante y profesional de administrar el inventario
                y las ventas de tu negocio nocturno.

            </p>

            <a href="Registro.jsp" class="btn-gold-cta">
                CREAR MI CUENTA
            </a>

        </div>

    </section>

    <!-- FOOTER -->
    <footer class="text-center py-4 border-top border-secondary mt-5">

        <p class="small text-muted">
            &copy; 2026 BarStock - Proyecto Académico SENA
        </p>

    </footer>

    <!-- SCRIPTS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="Vista/JavaScript/Home.js"></script>

</body>
</html>