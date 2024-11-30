document.addEventListener('DOMContentLoaded', async () => {
    try {
        // Llamar al backend para obtener las recetas
        const response = await fetch('/recetas');
        if (!response.ok) {
            throw new Error('Error al cargar las recetas');
        }

        const recetas = await response.json();
        cargarTablaRecetas(recetas);

        // Configurar botón para descargar todas las recetas
        const descargarTodasBtn = document.getElementById('descargarTodasBtn');
        descargarTodasBtn.addEventListener('click', () => descargarTodasRecetasPDF(recetas));
    } catch (error) {
        console.error('Error al cargar recetas:', error);
    }
});

function cargarTablaRecetas(recetas) {
    const tableBody = document.querySelector('#recetasTable');
    tableBody.innerHTML = '';

    recetas.forEach(receta => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${receta.id}</td>
            <td>${receta.fechaEmision || 'No especificada'}</td>
            <td>${receta.cita?.paciente?.nombres || 'Sin datos'} ${receta.cita?.paciente?.apellidos || ''}</td>
            <td>${receta.cita?.medico?.nombres || 'Sin datos'} ${receta.cita?.medico?.apellidos || ''}</td>
            <td>${receta.medicamentos || 'Sin datos'}</td>
            <td>${receta.dosis || 'Sin datos'}</td>
            <td>${receta.frecuencia || 'Sin datos'}</td>
            <td>${receta.instrucciones || 'Sin datos'}</td>
            <td>
                <button class="btn btn-primary btn-sm btn-descargar" data-receta='${JSON.stringify(receta)}'>
                    <i class="fas fa-download"></i> Descargar PDF
                </button>
            </td>
        `;
        tableBody.appendChild(row);
    });

    // Configurar eventos para los botones de descarga individual
    document.querySelectorAll('.btn-descargar').forEach(button => {
        button.addEventListener('click', (event) => {
            const receta = JSON.parse(event.target.closest('button').getAttribute('data-receta'));
            descargarRecetaPDF(receta);
        });
    });
}


function descargarRecetaPDF(receta) {
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF();

    doc.setFontSize(16);
    doc.text(`Receta Médica - ID: ${receta.id}`, 14, 10);

    doc.autoTable({
        startY: 20,
        head: [['ID', 'Fecha Emisión', 'Paciente', 'Médico', 'Medicamentos', 'Dosis', 'Frecuencia', 'Instrucciones']],
        body: [
            [
                receta.id,
                receta.fechaEmision || 'No especificada',
                `${receta.cita?.paciente?.nombres || ''} ${receta.cita?.paciente?.apellidos || ''}`,
                `${receta.cita?.medico?.nombres || ''} ${receta.cita?.medico?.apellidos || ''}`,
                receta.medicamentos || '',
                receta.dosis || '',
                receta.frecuencia || '',
                receta.instrucciones || ''
            ]
        ],
    });

    doc.save(`receta_${receta.id}.pdf`);
}

function descargarTodasRecetasPDF(recetas) {
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF();

    doc.setFontSize(16);
    doc.text('Recetas Médicas', 14, 10);

    doc.autoTable({
        startY: 20,
        head: [['ID', 'Fecha Emisión', 'Paciente', 'Médico', 'Medicamentos', 'Dosis', 'Frecuencia', 'Instrucciones']],
        body: recetas.map(receta => [
            receta.id,
            receta.fechaEmision || 'No especificada',
            `${receta.cita?.paciente?.nombres || ''} ${receta.cita?.paciente?.apellidos || ''}`,
            `${receta.cita?.medico?.nombres || ''} ${receta.cita?.medico?.apellidos || ''}`,
            receta.medicamentos || '',
            receta.dosis || '',
            receta.frecuencia || '',
            receta.instrucciones || ''
        ]),
    });

    doc.save('recetas_medicas.pdf');
}
document.getElementById('backButton').addEventListener('click', () => {
    history.back(); // Retrocede a la página anterior
});
