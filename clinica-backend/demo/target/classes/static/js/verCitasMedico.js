document.addEventListener("DOMContentLoaded", async () => {
    try {
        const medicoId = await verificarSesionMedico();  // Verificar y obtener el ID del médico

        if (medicoId) {
            console.log("Sesión válida. Cargando citas...");
            sessionStorage.setItem('medicoId', medicoId);  // Guardar el ID del médico en sessionStorage
            await cargarCitas();  // Cargar citas sin pasar el ID explícitamente
        }
    } catch (error) {
        console.error('Error al verificar la sesión:', error);
        alert("Sesión expirada o no válida. Redirigiendo al login...");
        window.location.href = "/login.html";
    }
});

async function verificarSesionMedico() {
    const response = await fetch('/api/sesion/medico');  // Verificar que esta ruta coincida
    if (!response.ok) {
        throw new Error("Sesión no válida.");
    }
    const data = await response.json();
    console.log("Datos del médico en sesión:", data);
    return data.medicoId;
}

// Cargar las citas del médico autenticado
async function cargarCitas() {
    const medicoId = sessionStorage.getItem('medicoId');
    console.log("Cargando citas para el médico ID:", medicoId);

    try {
        const response = await fetch(`/api/medico/citas/${medicoId}`);
        if (!response.ok) throw new Error("Error al obtener las citas.");

        const citas = await response.json();
        console.log('Citas recibidas:', citas);

        if (citas.length === 0) {
            document.getElementById('citas-lista').innerHTML = '<p>No hay citas disponibles.</p>';
        } else {
            mostrarCitas(citas);
        }
    } catch (error) {
        console.error('Error al cargar las citas:', error);
    }
}

function mostrarCitas(citas) {
    const listaCitas = document.getElementById('citas-lista');
    listaCitas.innerHTML = '';

    const medicoId = sessionStorage.getItem('medicoId');

    citas.forEach(cita => {
        const paciente = cita.paciente || {};
        const nombresCompletos = `${paciente.nombres || 'N/A'} ${paciente.apellidos || 'N/A'}`;
        const fecha = new Date(cita.fecha).toLocaleDateString('es-PE');
        const hora = cita.hora || 'Hora no especificada';
        const motivo = cita.motivo || 'Motivo no especificado';
        const comentario = cita.comentario || 'Sin comentarios';
        const estado = cita.estado || 'pendiente';

        const card = document.createElement('div');
        card.className = 'card shadow-sm mb-3';

        card.innerHTML = `
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0">Paciente: ${nombresCompletos}</h5>
            </div>
            <div class="card-body">
                <p><strong>Fecha:</strong> ${fecha}</p>
                <p><strong>Hora:</strong> ${hora}</p>
                <p><strong>Estado:</strong> ${estado}</p>
                <p><strong>Motivo:</strong> ${motivo}</p>
                <p><strong>Comentario:</strong> 
                    <span id="comentario-texto-${cita.id}">${comentario}</span>
                </p>
                <textarea class="form-control mb-2" id="comentario-${cita.id}" 
                          placeholder="Escribe un comentario..."></textarea>
                <button class="btn btn-outline-primary me-2" 
                        onclick="guardarComentario(${cita.id})">Guardar Comentario</button>
                <button class="btn btn-outline-secondary" 
                        onclick="abrirChat(${paciente.id})">Chat con Paciente</button>
            </div>`;

        const footer = document.createElement('div');
        footer.className = 'card-footer text-end';

        if (estado.toLowerCase() === 'pendiente') {
            const botonAceptar = document.createElement('button');
            botonAceptar.className = 'btn btn-success me-2';
            botonAceptar.textContent = 'Aceptar Cita';
            botonAceptar.addEventListener('click', () => aceptarCita(cita.id));
            footer.appendChild(botonAceptar);

            const botonCancelar = document.createElement('button');
            botonCancelar.className = 'btn btn-danger me-2';
            botonCancelar.textContent = 'Cancelar Cita';
            botonCancelar.addEventListener('click', () => cancelarCita(cita.id));
            footer.appendChild(botonCancelar);
        }

        if (estado.toLowerCase() === 'aceptada') {
            const botonReceta = document.createElement('button');
            botonReceta.className = 'btn btn-primary me-2';
            botonReceta.textContent = 'Realizar Receta';
            botonReceta.addEventListener('click', () =>
                realizarReceta(cita.id, paciente.id, medicoId)
            );
            footer.appendChild(botonReceta);
        }

        card.appendChild(footer);
        listaCitas.appendChild(card);
    });
}


function realizarReceta(citaId, pacienteId, medicoId) {
    if (!citaId || !pacienteId || !medicoId) {
        console.error('Faltan parámetros para realizar la receta:', { citaId, pacienteId, medicoId });
        alert('No se puede realizar la receta. Faltan parámetros.');
        return;
    }

    const url = `Receta.html?citaId=${citaId}&pacienteId=${pacienteId}&medicoId=${medicoId}`;
    console.log('Redirigiendo a:', url);
    window.location.href = url;
}

function aceptarCita(citaId) {
    fetch(`/api/citas/${citaId}/aceptar`, { method: 'PUT' })
        .then(response => response.text())
        .then(message => {
            alert(message);
            location.reload();
        })
        .catch(error => console.error('Error al aceptar cita:', error));
}

function cancelarCita(citaId) {
    if (confirm('¿Está seguro de cancelar esta cita?')) {
        fetch(`/api/citas/${citaId}/cancelar`, { method: 'PUT' })
            .then(response => response.text())
            .then(message => {
                alert(message);
                location.reload();
            })
            .catch(error => console.error('Error al cancelar cita:', error));
    }
}

function guardarComentario(idCita) {
    const comentarioInput = document.getElementById(`comentario-${idCita}`);
    const comentario = comentarioInput.value;

    if (!comentario) {
        alert('Por favor, escribe un comentario.');
        return;
    }

    fetch(`/api/citas/${idCita}/comentar`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ comentario })
    })
        .then(response => response.json())
        .then(cita => {
            alert('Comentario guardado con éxito.');
            document.getElementById(`comentario-texto-${cita.id}`).textContent = cita.comentario;
            comentarioInput.value = '';
        })
        .catch(error => console.error('Error al guardar comentario:', error));
}

function abrirChat(pacienteId) {
    const medicoId = sessionStorage.getItem('medicoId');
    console.log(`Medico ID: ${medicoId}, Paciente ID: ${pacienteId}`);

    if (!medicoId) {
        alert('El ID del médico no se encontró.');
        return;
    }

    if (!pacienteId) {
        alert('El paciente no está disponible.');
        return;
    }

    window.location.href = `/ChatConPaciente.html?medicoId=${medicoId}&pacienteId=${pacienteId}`;
}

function aplicarFiltro() {
    verificarSesionMedico().then(medicoId => {
        const fechaInicio = document.getElementById('fechaInicio').value;
        const fechaFin = document.getElementById('fechaFin').value;
        const estado = document.getElementById('estado').value;

        const url = new URL(`/api/medico/citas/${medicoId}`, window.location.origin);
        if (fechaInicio) url.searchParams.append('fechaInicio', fechaInicio);
        if (fechaFin) url.searchParams.append('fechaFin', fechaFin);
        if (estado) url.searchParams.append('estado', estado);

        fetch(url)
            .then(response => response.json())
            .then(citas => mostrarCitas(citas))
            .catch(error => console.error('Error al aplicar filtro:', error));
    });
}
