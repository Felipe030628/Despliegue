document.addEventListener("DOMContentLoaded", function () {

    const selectProducto = document.getElementById("selectProducto");
    const inputCantidad = document.getElementById("inputCantidad");
    const btnAgregar = document.getElementById("btnAgregarProducto");
    const cuerpoCarrito = document.getElementById("cuerpoCarrito");
    const totalTexto = document.getElementById("totalCarritoTexto");
    const inputTotalOculto = document.getElementById("inputTotalOculto");
    const contenedorOcultos = document.getElementById("contenedorInputsOcultos");
    const formPedido = document.getElementById("formPedido");
    const precioPreview = document.getElementById("precioPreview");
    const stockPreview = document.getElementById("stockPreview");

    // Carrito en memoria: [{ idProducto, nombre, precio, cantidad, stockDisponible }]
    let carrito = [];

    if (!selectProducto || !btnAgregar) {
        // Esta página no tiene el formulario de pedidos (p.ej. EditarPedido.jsp
        // sólo muestra el detalle de sólo lectura), así que no hay nada que enlazar.
        return;
    }

    function productoSeleccionado() {
        const opt = selectProducto.options[selectProducto.selectedIndex];
        if (!opt || !opt.value) return null;
        return {
            id: parseInt(opt.value, 10),
            nombre: opt.textContent.trim().split(" — ")[0].trim(),
            precio: parseFloat(opt.dataset.precio || "0"),
            stock: parseInt(opt.dataset.stock || "0", 10)
        };
    }

    function actualizarPreview() {
        const p = productoSeleccionado();
        if (!p) {
            precioPreview.textContent = "";
            stockPreview.textContent = "";
            return;
        }
        precioPreview.innerHTML = "Precio unitario: <strong>$" + p.precio.toFixed(2) + "</strong>";
        stockPreview.textContent = "Stock disponible: " + p.stock;
        stockPreview.dataset.bajo = p.stock <= 0 ? "1" : "0";
        if (inputCantidad) {
            inputCantidad.max = p.stock > 0 ? p.stock : 0;
        }
    }

    selectProducto.addEventListener("change", actualizarPreview);
    actualizarPreview();

    function formatoMoneda(valor) {
        return "$" + valor.toFixed(2);
    }

    function calcularTotal() {
        return carrito.reduce((acc, linea) => acc + linea.precio * linea.cantidad, 0);
    }

    function renderCarrito() {
        cuerpoCarrito.innerHTML = "";

        if (carrito.length === 0) {
            cuerpoCarrito.innerHTML = '<tr class="carrito-vacio"><td colspan="5">Todavía no agregaste productos a este pedido.</td></tr>';
        } else {
            carrito.forEach((linea, index) => {
                const tr = document.createElement("tr");
                tr.innerHTML =
                    '<td>' + linea.nombre + '</td>' +
                    '<td>' + formatoMoneda(linea.precio) + '</td>' +
                    '<td>' + linea.cantidad + '</td>' +
                    '<td>' + formatoMoneda(linea.precio * linea.cantidad) + '</td>' +
                    '<td class="text-center"><button type="button" class="btn-quitar-linea" data-index="' + index + '" title="Quitar"><i class="bi bi-x-lg"></i></button></td>';
                cuerpoCarrito.appendChild(tr);
            });
        }

        const total = calcularTotal();
        totalTexto.textContent = formatoMoneda(total);
        inputTotalOculto.value = total.toFixed(2);

        cuerpoCarrito.querySelectorAll(".btn-quitar-linea").forEach(btn => {
            btn.addEventListener("click", function () {
                const idx = parseInt(this.dataset.index, 10);
                carrito.splice(idx, 1);
                renderCarrito();
            });
        });
    }

    btnAgregar.addEventListener("click", function () {
        const p = productoSeleccionado();
        const cantidad = parseInt(inputCantidad.value, 10);

        if (!p) {
            alert("Seleccioná un producto.");
            return;
        }
        if (!cantidad || cantidad <= 0) {
            alert("Ingresá una cantidad válida.");
            return;
        }

        // Si el producto ya está en el carrito, se suma la cantidad (respetando el stock)
        const existente = carrito.find(l => l.idProducto === p.id);
        const cantidadYaEnCarrito = existente ? existente.cantidad : 0;

        if (cantidadYaEnCarrito + cantidad > p.stock) {
            alert("No hay stock suficiente de \"" + p.nombre + "\". Disponible: " + p.stock + (cantidadYaEnCarrito > 0 ? " (ya tenés " + cantidadYaEnCarrito + " en el pedido)" : ""));
            return;
        }

        if (existente) {
            existente.cantidad += cantidad;
        } else {
            carrito.push({
                idProducto: p.id,
                nombre: p.nombre,
                precio: p.precio,
                cantidad: cantidad
            });
        }

        inputCantidad.value = 1;
        renderCarrito();
    });

    formPedido.addEventListener("submit", function (event) {
        if (carrito.length === 0) {
            event.preventDefault();
            alert("Agregá al menos un producto antes de registrar el pedido.");
            return;
        }

        contenedorOcultos.innerHTML = "";
        carrito.forEach(linea => {
            const inputId = document.createElement("input");
            inputId.type = "hidden";
            inputId.name = "productoId[]";
            inputId.value = linea.idProducto;
            contenedorOcultos.appendChild(inputId);

            const inputCant = document.createElement("input");
            inputCant.type = "hidden";
            inputCant.name = "cantidad[]";
            inputCant.value = linea.cantidad;
            contenedorOcultos.appendChild(inputCant);
        });
    });

    renderCarrito();
});
