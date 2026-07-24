/* ==========================================================================
   BARSTOCK - COMPORTAMIENTO DINÁMICO DE BÚSQUEDAS
   ========================================================================== */

document.addEventListener("DOMContentLoaded", () => {
    const btnToggleForm = document.getElementById('btnToggleForm');
    const formSection = document.getElementById('formSection');
    const tableSearch = document.getElementById('tableSearch');
    const searchCodigo = document.getElementById('searchCodigo');
    const tableRows = document.querySelectorAll('#inventoryTableBody tr');

    // Despliegue del cajón de formularios
    if (btnToggleForm && formSection) {
        btnToggleForm.addEventListener('click', () => {
            formSection.style.display = (formSection.style.display === 'none') ? 'block' : 'none';
        });
    }

    // Filtrado inteligente combinado
    function aplicarFiltros() {
        const valNombre = tableSearch ? tableSearch.value.toLowerCase() : '';
        const valCodigo = searchCodigo ? searchCodigo.value.toLowerCase() : '';

        tableRows.forEach(row => {
            const cells = row.getElementsByTagName('td');
            if (cells.length > 0) {
                const txtCodigo = cells[0].textContent.toLowerCase();
                const txtNombre = cells[2].textContent.toLowerCase();

                const coincideCodigo = txtCodigo.includes(valCodigo);
                const coincideNombre = txtNombre.includes(valNombre);

                if (coincideCodigo && coincideNombre) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            }
        });
    }

    if (tableSearch) tableSearch.addEventListener('keyup', aplicarFiltros);
    if (searchCodigo) searchCodigo.addEventListener('keyup', aplicarFiltros);
});