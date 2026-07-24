const btnCarrito = document.getElementById('btnCarrito');
const cartSidebar = document.getElementById('cartSidebar');
const closeCart = document.getElementById('closeCart');
let items = [];

btnCarrito.onclick = () => cartSidebar.classList.add('active');
closeCart.onclick = () => cartSidebar.classList.remove('active');

document.querySelectorAll('.add-to-cart').forEach(btn => {
    btn.onclick = () => {
        const item = {
            nombre: btn.dataset.nombre,
            precio: parseInt(btn.dataset.precio)
        };
        items.push(item);
        actualizarCarrito();
    };
});

function actualizarCarrito() {
    const body = document.getElementById('cartBody');
    const totalElement = document.getElementById('cartTotal');
    document.getElementById('cartCount').innerText = items.length;
    
    let total = 0;
    body.innerHTML = items.map((i, index) => {
        total += i.precio;
        return `
            <div class="d-flex justify-content-between text-white mb-3 small border-bottom border-secondary pb-2">
                <span>${i.nombre}</span>
                <span class="text-gold">$${i.precio.toLocaleString()}</span>
            </div>
        `;
    }).join('');
    
    totalElement.innerText = `$${total.toLocaleString()}`;
}