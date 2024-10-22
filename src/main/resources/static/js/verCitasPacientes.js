document.addEventListener("DOMContentLoaded", () => {
    cargarCitas();
    document.getElementById('guardarCambiosBtn').addEventListener('click', guardarModificacion);
    document.getElementById('filtrarBtn').addEventListener('click', filtrarCitas);
});

let citaIdSeleccionada;

function cargarCitas() {
    fetch('/api/sesion/paciente')
        .then(response => response.json())
        .then(data => fetch(`/api/citas/paciente/${data.pacienteId}`))
        .then(response => response.json())
        .then(citas => mostrarCitas(citas))
        .catch(error => console.error('Error:', error));
}

function mostrarCitas(citas) {
    const listaCitas = document.getElementById('listaCitas');
    listaCitas.innerHTML = ''; // Limpiar lista antes de mostrar

    citas.forEach(cita => {
        const medico = cita.medico 
            ? `${cita.medico.nombres || ''} ${cita.medico.apellidos || ''}` 
            : 'Médico no asignado';

        const fecha = new Date(cita.fecha).toLocaleDateString();
        const motivo = cita.motivo || 'Sin motivo especificado';
        const comentario = cita.comentario || 'Sin comentario disponible'; // Asegurar comentario

        listaCitas.innerHTML += `
            <div class="list-group-item">
                <p><strong>Fecha:</strong> ${fecha}</p>
                <p><strong>Hora:</strong> ${cita.hora}</p>
                <p><strong>Especialidad:</strong> ${cita.especialidad}</p>
                <p><strong>Motivo:</strong> ${motivo}</p>
                <p><strong>Médico:</strong> ${medico}</p>
                <p><strong>Comentario del Médico:</strong> ${comentario}</p>
                <button class="btn btn-primary" onclick="abrirChat(${cita.id})">
                    Abrir Chat
                </button>
            </div>`;
    });
}



function abrirChat(citaId) {
    window.location.href = `/chat.html?citaId=${citaId}`;
}





function abrirModalModificar(id, motivo, fecha, hora) {
    citaIdSeleccionada = id;
    document.getElementById('motivoNuevo').value = motivo || '';
    document.getElementById('fechaNueva').value = fecha || '';
    document.getElementById('horaNueva').value = hora || '';
    new bootstrap.Modal(document.getElementById('modalModificar')).show();
}

function guardarModificacion() {
    const motivo = document.getElementById('motivoNuevo').value;
    const fecha = document.getElementById('fechaNueva').value;
    const hora = document.getElementById('horaNueva').value;

    if (!motivo || !fecha || !hora) {
        alert('Por favor, completa todos los campos.');
        return;
    }

    const citaModificada = { motivo, fecha, hora };

    fetch(`/api/citas/${citaIdSeleccionada}/modificar`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(citaModificada)
    })
        .then(response => {
            if (!response.ok) return response.json().then(err => { throw new Error(err.message); });
            alert('Cita modificada con éxito.');
            cargarCitas();
            cerrarModal();
        })
        .catch(error => console.error('Error:', error));
}

function cancelarCita(id) {
    if (confirm('¿Está seguro de cancelar esta cita?')) {
        fetch(`/api/citas/${id}/cancelar`, { method: 'PUT' })
            .then(() => cargarCitas())
            .catch(error => console.error('Error:', error));
    }
}

function cerrarModal() {
    bootstrap.Modal.getInstance(document.getElementById('modalModificar')).hide();
}

function filtrarCitas() {
    const fechaInicio = document.getElementById('fechaInicio').value;
    const fechaFin = document.getElementById('fechaFin').value;

    fetch(`/api/citas/filtrar?pacienteId=1&fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`)
        .then(response => response.json())
        .then(citas => mostrarCitas(citas))
        .catch(error => console.error('Error al filtrar:', error));
}

function descargarHistorialPDF() {
    fetch('/api/citas/historial/pdf?pacienteId=1')
        .then(response => response.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'historial_citas.pdf';
            document.body.appendChild(a);
            a.click();
            a.remove();
        })
        .catch(error => console.error('Error al descargar el historial:', error));
}
