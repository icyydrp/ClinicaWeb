document.addEventListener('DOMContentLoaded', async () => {
    await cargarPacientesConCitasAceptadas();
});

// Cargar pacientes con citas aceptadas
async function cargarPacientesConCitasAceptadas() {
    try {
        const response = await fetch('/api/pacientes/con-citas-aceptadas');
        if (!response.ok) throw new Error('Error al cargar los pacientes');

        const pacientes = await response.json();
        mostrarPacientesUnicos(pacientes);
    } catch (error) {
        console.error('Error al cargar los pacientes:', error);
    }
}

// Mostrar la lista de pacientes sin duplicados
function mostrarPacientesUnicos(pacientes) {
    const listaPacientes = document.getElementById('listaPacientes');
    listaPacientes.innerHTML = '';

    const pacientesUnicos = new Map();
    pacientes.forEach(paciente => {
        if (!pacientesUnicos.has(paciente.id)) {
            pacientesUnicos.set(paciente.id, paciente);
        }
    });

    pacientesUnicos.forEach(paciente => {
        const pacienteItem = document.createElement('button');
        pacienteItem.classList.add('list-group-item', 'list-group-item-action');
        pacienteItem.textContent = `${paciente.nombres} ${paciente.apellidos}`;
        pacienteItem.addEventListener('click', () => {
            cargarRecetaPaciente(paciente.id);
        });
        listaPacientes.appendChild(pacienteItem);
    });
}

// Cargar la receta del paciente seleccionado
async function cargarRecetaPaciente(pacienteId) {
    try {
        const response = await fetch(`/api/paciente/${pacienteId}/recetas`);
        if (!response.ok) throw new Error('Error al cargar la receta');

        const recetas = await response.json();
        if (recetas.length > 0) {
            mostrarReceta(recetas[0]);
        } else {
            alert('No se encontraron recetas para este paciente.');
        }
    } catch (error) {
        console.error('Error al cargar la receta:', error);
    }
}

// Mostrar la receta en el formato solicitado
function mostrarReceta(receta) {
    document.getElementById('medicoNombre').textContent = receta.medicoNombre || 'N/A';
    document.getElementById('medicoCorreo').textContent = receta.medicoCorreo || 'N/A';

    document.getElementById('pacienteNombre').textContent = receta.pacienteNombre || 'N/A';
    document.getElementById('pacienteCelular').textContent = receta.pacienteCelular || 'N/A';
    document.getElementById('pacienteCorreo').textContent = receta.pacienteCorreo || 'N/A';

    document.getElementById('recetaMedicamentos').textContent = receta.medicamentos || 'N/A';
    document.getElementById('recetaDosis').textContent = receta.dosis || 'N/A';
    document.getElementById('recetaFrecuencia').textContent = receta.frecuencia || 'N/A';
    document.getElementById('recetaInstrucciones').textContent = receta.instrucciones || 'N/A';
    document.getElementById('notaAdicional').textContent = receta.notaAdicional || 'No hay notas adicionales';

    document.getElementById('recetaDetalle').style.display = 'block';
}
function volverPaginaAnterior() {
    window.history.back();
}
document.getElementById('btnDescargar').addEventListener('click', () => {
    const recetaContent = document.getElementById('recetaContent');
    const opt = {
        margin: 0.5,
        filename: 'Receta_Medica.pdf',
        image: { type: 'jpeg', quality: 0.98 },
        html2canvas: { scale: 2 },
        jsPDF: { unit: 'in', format: 'letter', orientation: 'portrait' }
    };
    html2pdf().set(opt).from(recetaContent).save();
});