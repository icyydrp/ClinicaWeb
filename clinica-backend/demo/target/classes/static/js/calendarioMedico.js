document.addEventListener('DOMContentLoaded', function () {
    const calendarEl = document.getElementById('calendar');

    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'es',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        events: async function (fetchInfo, successCallback, failureCallback) {
            try {
                const medicoId = await obtenerMedicoSesion();
                console.log(`ID del médico: ${medicoId}`);

                const response = await fetch(`/api/medico/citas/${medicoId}`);
                if (!response.ok) throw new Error('No se pudieron cargar las citas.');

                const citas = await response.json();
                console.log('Citas recibidas:', citas);

                const eventos = citas.map(cita => ({
                    title: `Paciente: ${cita.paciente.nombres} ${cita.paciente.apellidos}`,
                    start: `${cita.fecha}T${cita.hora}`,
                    className: estadoClase(cita.estado), // Clase dinámica basada en el estado
                    description: cita.motivo || 'Sin motivo',
                    extendedProps: {
                        estado: cita.estado,
                        comentario: cita.comentario || 'Sin comentarios'
                    }
                }));

                successCallback(eventos);
            } catch (error) {
                console.error('Error al cargar los eventos:', error);
                failureCallback(error);
            }
        },
        eventClick: function (info) {
            const { title, start, extendedProps } = info.event;
            alert(`
                ${title}
                Fecha: ${start.toLocaleDateString()}
                Hora: ${start.toLocaleTimeString()}
                Estado: ${extendedProps.estado}
                Comentario: ${extendedProps.comentario}
            `);
        }
    });

    calendar.render();
});

async function obtenerMedicoSesion() {
    try {
        const response = await fetch('/api/sesion/medico');  // Verificar ruta correcta
        if (!response.ok) throw new Error('No se encontró la sesión del médico.');

        const data = await response.json();
        console.log('Datos de la sesión del médico:', data);

        return data.medicoId;
    } catch (error) {
        alert('Error de sesión. Por favor, inicia sesión nuevamente.');
        window.location.href = "/login.html";
        throw error;
    }
}

function estadoClase(estado) {
    switch (estado.toLowerCase()) {
        case 'aceptada':
            return 'evento-aceptada';
        case 'pendiente':
            return 'evento-pendiente';
        case 'cancelada':
            return 'evento-cancelado';
        default:
            return 'evento-otro';
    }
}
