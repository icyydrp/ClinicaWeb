document.addEventListener("DOMContentLoaded", cargarCitas);

function cargarCitas() {
    fetch('/api/sesion/medico')
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al obtener la sesión del médico.');
            }
            return response.json();
        })
        .then(data => {
            const medicoId = data.medicoId;
            return fetch(`/api/medico/citas/${medicoId}`);
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al cargar las citas del médico.');
            }
            return response.json();
        })
        .then(citas => {
            console.log('Citas recibidas:', citas); // Verificar datos recibidos
            mostrarCitas(citas);
        })
        .catch(error => console.error('Error al cargar citas:', error));
}

function mostrarCitas(citas) {
    const listaCitas = document.getElementById('citas-lista');
    listaCitas.innerHTML = '';

    if (citas.length === 0) {
        listaCitas.innerHTML = `
            <div class="alert alert-warning text-center" role="alert">
                No se encontraron citas con los filtros aplicados.
            </div>`;
        return;
    }

    citas.forEach(cita => {
        const paciente = cita.paciente || {};
        const nombres = paciente.nombres || 'N/A';
        const apellidos = paciente.apellidos || 'N/A';
        const nombresCompletos = `${nombres} ${apellidos}`;
        const motivo = cita.motivo || 'Motivo no especificado';

        console.log(`Paciente: ${nombresCompletos}`); // Verificar en consola

        listaCitas.innerHTML += `
            <div class="card shadow-sm mb-3">
                <div class="card-header bg-primary text-white">
                    <h5 class="mb-0">Paciente: ${nombresCompletos}</h5>
                </div>
                <div class="card-body">
                    <p><strong>Fecha:</strong> ${new Date(cita.fecha).toLocaleDateString()}</p>
                    <p><strong>Hora:</strong> ${cita.hora}</p>
                    <p><strong>Estado:</strong> ${cita.estado}</p>
                    <p><strong>Motivo:</strong> ${motivo}</p>
                    <p><strong>Comentario:</strong> ${cita.comentario || 'Sin comentarios'}</p>

                    <textarea class="form-control mb-2" id="comentario-${cita.id}" 
                              placeholder="Escribe un comentario..."></textarea>
                    <button class="btn btn-outline-primary me-2" 
                            onclick="guardarComentario(${cita.id})">Guardar Comentario</button>
                    <button class="btn btn-outline-secondary" 
                            onclick="abrirChat(${paciente.id})">Chat con Paciente</button>
                </div>
                ${cita.estado === 'Pendiente' ? `
                    <div class="card-footer text-end">
                        <button class="btn btn-success me-2" 
                                onclick="aceptarCita(${cita.id})">Aceptar</button>
                        <button class="btn btn-danger" 
                                onclick="cancelarCita(${cita.id})">Cancelar</button>
                    </div>` 
                : ''}
            </div>`;
    });
}

function abrirChat(pacienteId) {
    if (!pacienteId) {
        alert('El paciente no está disponible.');
        return;
    }
    // Redirige a la página del chat con el ID del paciente en la URL
    window.location.href = `ChatconPaciente.html?pacienteId=${pacienteId}`;
}

function aceptarCita(citaId) {
    fetch(`/api/citas/${citaId}/aceptar`, { method: 'PUT' })
        .then(response => response.text())
        .then(message => {
            alert(message);
            cargarCitas();
        })
        .catch(error => console.error('Error al aceptar cita:', error));
}

function cancelarCita(citaId) {
    if (confirm('¿Está seguro de cancelar esta cita?')) {
        fetch(`/api/citas/${citaId}/cancelar`, { method: 'PUT' })
            .then(response => response.text())
            .then(message => {
                alert(message);
                cargarCitas();
            })
            .catch(error => console.error('Error al cancelar cita:', error));
    }
}

function guardarComentario(citaId) {
    const comentario = document.getElementById(`comentario-${citaId}`).value;
    fetch(`/api/citas/${citaId}/comentar`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ comentario })
    })
        .then(response => response.text())
        .then(message => {
            alert(message);
            cargarCitas();
        })
        .catch(error => console.error('Error al agregar comentario:', error));
}

function aplicarFiltro() {
    const fechaInicio = document.getElementById('fechaInicio').value;
    const fechaFin = document.getElementById('fechaFin').value;
    const estado = document.getElementById('estado').value;

    const url = new URL(`/api/medico/citas/1`, window.location.origin);
    if (fechaInicio) url.searchParams.append('fechaInicio', fechaInicio);
    if (fechaFin) url.searchParams.append('fechaFin', fechaFin);
    if (estado) url.searchParams.append('estado', estado);

    fetch(url)
        .then(response => response.ok ? response.json() : Promise.reject())
        .then(citas => mostrarCitas(citas))
        .catch(error => console.error('Error al filtrar citas:', error));
}
