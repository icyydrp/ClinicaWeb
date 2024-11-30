document.addEventListener('DOMContentLoaded', async () => {
    await cargarReporte('/reportes/citas', 'citasTable', ['idCita', 'paciente', 'medico', 'especialidad', 'fecha', 'hora', 'estado']);
    await cargarReporte('/reportes/pacientes', 'pacientesTable', ['id', 'nombre', 'correo', 'celular', 'dni']);
    await cargarReporte('/reportes/medicos', 'medicosTable', ['id', 'nombre', 'especialidad', 'numeroColegiatura', 'correo']);
});

async function cargarReporte(endpoint, tableId, columnas) {
    try {
        const response = await fetch(endpoint);
        const data = await response.json();
        const tableBody = document.querySelector(`#${tableId} tbody`);
        tableBody.innerHTML = '';

        data.forEach(row => {
            const tr = document.createElement('tr');
            columnas.forEach(columna => {
                const td = document.createElement('td');
                td.textContent = row[columna] || 'No disponible';
                tr.appendChild(td);
            });
            tableBody.appendChild(tr);
        });
    } catch (error) {
        console.error(`Error al cargar el reporte de ${endpoint}:`, error);
    }
}

function exportarPDF(tableId, filename) {
    const { jsPDF } = window.jspdf; // Accede a jsPDF desde el espacio global
    const doc = new jsPDF(); // Inicializa jsPDF

    // Selecciona la tabla y genera el PDF
    const table = document.getElementById(tableId);
    doc.text(`Reporte de ${filename}`, 14, 16); // Título del PDF
    doc.autoTable({ html: `#${tableId}`, startY: 20 }); // Genera la tabla desde el HTML

    // Descarga el PDF
    doc.save(`${filename}.pdf`);
}
