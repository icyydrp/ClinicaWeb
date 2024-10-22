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
        events: function (fetchInfo, successCallback, failureCallback) {
            fetch('/api/sesion/medico')
                .then(response => response.json())
                .then(data => {
                    const medicoId = data.medicoId;
        
                    return fetch(`/api/medico/citas/${medicoId}`);
                })
                .then(response => response.json())
                .then(citas => {
                    console.log('Citas recibidas:', citas); // Verificar la estructura de datos
        
                    const eventos = citas.map(cita => ({
                        title: `Paciente: ${cita.paciente.nombres} ${cita.paciente.apellidos}`,
                        start: `${cita.fecha}T${cita.hora}`,
                        description: cita.motivo || 'Sin motivo',
                        extendedProps: {
                            estado: cita.estado,
                            comentario: cita.comentario || 'Sin comentarios'
                        }
                    }));
        
                    successCallback(eventos);
                })
                .catch(error => {
                    console.error('Error al cargar citas:', error);
                    failureCallback(error);
                });
        }
        
        ,
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
