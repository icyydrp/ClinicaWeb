document.addEventListener("DOMContentLoaded", async () => {
    try {
        await validarSesionPaciente();
        const pacienteId = sessionStorage.getItem('pacienteId');

        if (!pacienteId) throw new Error("No se pudo obtener el ID del paciente.");
        console.log("ID del paciente obtenido:", pacienteId);

        await cargarCitas();
        await cargarNotificaciones(pacienteId);

        asignarEventos();
    } catch (error) {
        console.error('Error en la inicialización:', error);
        alert("Error al cargar las notificaciones. Por favor, inicia sesión.");
        window.location.href = "/login.html";
    }
});

// **Asignar eventos a botones**
function asignarEventos() {
    try {
        const guardarBtn = document.getElementById('guardarCambiosBtn');
        const filtrarBtn = document.getElementById('filtrarBtn');
        const descargarPdfBtn = document.getElementById('descargarPdfBtn');

        if (guardarBtn) guardarBtn.addEventListener('click', guardarModificacion);
        if (filtrarBtn) filtrarBtn.addEventListener('click', filtrarCitas);
        if (descargarPdfBtn) descargarPdfBtn.addEventListener('click', descargarHistorialPDF);
    } catch (error) {
        console.error("Error al asignar eventos:", error);
    }
}

// **Obtener ID del paciente**
async function obtenerIdPaciente() {
    try {
        const response = await fetch('/api/sesion/paciente');
        if (!response.ok) throw new Error("Sesión no válida.");
        const data = await response.json();
        console.log("ID del paciente autenticado:", data.pacienteId);
        return data.pacienteId;
    } catch (error) {
        console.error('Error al obtener ID del paciente:', error);
        window.location.href = "/login.html";
    }
}

// **Cargar citas del paciente**
async function cargarCitas() {
    try {
        const pacienteId = await obtenerIdPaciente();
        const response = await fetch(`/api/citas/paciente/${pacienteId}`);
        if (!response.ok) throw new Error('Error al obtener las citas.');
        const citas = await response.json();
        console.log('Citas recibidas:', citas);
        mostrarCitas(citas);
    } catch (error) {
        console.error('Error al cargar las citas:', error);
        alert('No se pudieron cargar las citas.');
    }
}

// **Mostrar citas**
function mostrarCitas(citas) {
    const listaCitas = document.getElementById('listaCitas');
    listaCitas.innerHTML = '';
    citas.forEach(cita => {
        const medico = cita.medico ? `${cita.medico.nombres} ${cita.medico.apellidos}` : 'Médico no asignado';
        const fecha = new Date(cita.fecha).toLocaleDateString('es-PE');
        const hora = cita.hora || 'Hora no especificada';
        const motivo = cita.motivo || 'Sin motivo especificado';
        const comentario = cita.comentario || 'Sin comentario disponible';
        const estado = cita.estado || 'Pendiente';

        let botones = '';
        if (estado.toLowerCase() === 'pendiente') {
            botones = `
                <button class="btn btn-warning me-2" onclick="abrirModalModificar(${cita.id}, '${motivo}', '${cita.fecha}', '${cita.hora}')">
                    Modificar
                </button>
                <button class="btn btn-danger" onclick="cancelarCita(${cita.id})">Cancelar</button>`;
        }

        listaCitas.innerHTML += `
            <div class="list-group-item mb-3">
                <p><strong>Fecha:</strong> ${fecha}</p>
                <p><strong>Hora:</strong> ${hora}</p>
                <p><strong>Especialidad:</strong> ${cita.especialidad}</p>
                <p><strong>Motivo:</strong> ${motivo}</p>
                <p><strong>Médico:</strong> ${medico}</p>
                <p><strong>Comentario:</strong> ${comentario}</p>
                <p><strong>Estado:</strong> ${estado}</p>
                ${botones}
                <button class="btn btn-primary mt-2" onclick="abrirChat(${cita.id})">Abrir Chat</button>
            </div>`;
    });
}

// **Abrir chat**
function abrirChat(citaId) {
    window.location.href = `/chat.html?citaId=${citaId}`;
}

// **Abrir modal para modificar cita**
function abrirModalModificar(id, motivo, fecha, hora) {
    citaIdSeleccionada = id;
    document.getElementById('motivoNuevo').value = motivo || '';
    document.getElementById('fechaNueva').value = fecha || '';
    document.getElementById('horaNueva').value = hora || '';
    new bootstrap.Modal(document.getElementById('modalModificar')).show();
}

// **Guardar modificación**
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
        if (!response.ok) throw new Error('Error al modificar la cita.');
        alert('Cita modificada con éxito.');
        location.reload();
    })
    .catch(error => console.error('Error al modificar la cita:', error));
}

// **Cancelar cita**
function cancelarCita(id) {
    if (confirm('¿Está seguro de cancelar esta cita?')) {
        fetch(`/api/citas/${id}/cancelar`, { method: 'PUT' })
        .then(response => {
            if (!response.ok) throw new Error('Error al cancelar la cita.');
            alert('Cita cancelada con éxito.');
            location.reload();
        })
        .catch(error => console.error('Error al cancelar la cita:', error));
    }
}

// **Filtrar citas por fecha**
async function filtrarCitas() {
    const pacienteId = await obtenerIdPaciente();
    const fechaInicio = document.getElementById('fechaInicio').value;
    const fechaFin = document.getElementById('fechaFin').value;

    fetch(`/api/citas/filtrar?pacienteId=${pacienteId}&fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`)
        .then(response => response.json())
        .then(citas => mostrarCitas(citas))
        .catch(error => console.error('Error al filtrar citas:', error));
}

// **Descargar historial en PDF**
async function descargarHistorialPDF() {
    const pacienteId = await obtenerIdPaciente();

    fetch(`/api/citas/historial/pdf?pacienteId=${pacienteId}`)
        .then(response => response.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'historial_citas.pdf';
            a.click();
            a.remove();
        })
        .catch(error => console.error('Error al descargar historial:', error));
}

// Cargar notificaciones del paciente
async function cargarNotificaciones(pacienteId) {
    console.log(`Cargando notificaciones para paciente ID: ${pacienteId}`);
    try {
        const response = await fetch(`/api/paciente/${pacienteId}/notificaciones`);
        if (!response.ok) {
            const errorText = await response.text();
            console.error("Error en la respuesta:", errorText);
            throw new Error(`Error al obtener notificaciones: ${errorText}`);
        }
        const notificaciones = await response.json();
        console.log("Notificaciones recibidas:", notificaciones);

        mostrarNotificaciones(notificaciones);
        actualizarContadorNotificaciones(notificaciones.length);
    } catch (error) {
        console.error('Error al cargar notificaciones:', error);
        document.getElementById('listaNotificaciones').innerHTML = `
            <li class="list-group-item">Error al cargar las notificaciones. Inténtalo más tarde.</li>`;
    }
}

// Mostrar notificaciones en el modal
function mostrarNotificaciones(notificaciones) {
    const listaNotificaciones = document.getElementById('listaNotificaciones');
    listaNotificaciones.innerHTML = ''; // Limpiar lista anterior

    if (notificaciones.length === 0) {
        listaNotificaciones.innerHTML = `
            <li class="list-group-item">No tienes nuevas notificaciones.</li>`;
        return;
    }

    notificaciones.forEach(notificacion => {
        const { estado, mensaje, fecha } = notificacion;
        const fechaFormateada = new Date(fecha).toLocaleDateString('es-ES');

        const li = document.createElement('li');
        li.classList.add('list-group-item', 'mb-2', 'shadow-sm');
        li.innerHTML = `
            <h6 class="${estado === 'Aceptada' ? 'text-success' 
                        : estado === 'Cancelada' ? 'text-danger' 
                        : 'text-warning'}">
                ${estado}
            </h6>
            <p><strong>Mensaje:</strong> ${mensaje}</p>
            <p><strong>Fecha:</strong> ${fechaFormateada}</p>`;
        listaNotificaciones.appendChild(li);
    });
}

// Actualizar el contador de notificaciones
function actualizarContadorNotificaciones(cantidad) {
    const badge = document.getElementById('notificationCount');
    badge.textContent = cantidad;
    badge.style.display = cantidad > 0 ? 'inline' : 'none';
}




    
    // **Validar sesión del paciente**
    async function validarSesionPaciente() {
        try {
            const response = await fetch('/api/sesion/paciente');
            if (!response.ok) throw new Error("Sesión no válida");
    
            const data = await response.json();
            console.log("Datos de la sesión:", data);
    
            sessionStorage.setItem('pacienteId', data.pacienteId);
        } catch (error) {
            console.error("Error al validar la sesión:", error);
            window.location.href = "/login.html";
        }
    }
    
    


