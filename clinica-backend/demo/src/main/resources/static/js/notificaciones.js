document.addEventListener('DOMContentLoaded', function() {
    // Cargar número de citas médicas del paciente y notificaciones al cargar la página
    cargarNumeroDeCitas();
    cargarNotificaciones();

    // Alternar el panel de notificaciones al hacer clic en la campanita
    const notificationIcon = document.getElementById('notificationIcon');
    notificationIcon.addEventListener('click', function() {
        const panel = document.getElementById('notification-panel');
        panel.classList.toggle('hidden');  // Mostrar u ocultar el panel
        console.log('Campanita clickeada, mostrando/ocultando notificaciones');
    });

    // Función para cargar las notificaciones desde el servidor
    function cargarNotificaciones() {
        fetch('/api/notificaciones')  // Ruta para obtener las notificaciones del servidor
            .then(response => response.json())
            .then(data => {
                const notificationList = document.getElementById('notification-list');
                notificationList.innerHTML = ''; // Limpiar contenido previo

                if (data.length === 0) {
                    notificationList.innerHTML = '<p>No tienes notificaciones.</p>';
                    return;
                }

                data.forEach(notificacion => {
                    const notificacionDiv = document.createElement('div');
                    notificacionDiv.classList.add('notificacion');
                    notificacionDiv.innerHTML = `
                        <p><strong>Cita:</strong> Te quedan ${notificacion.dias_restantes} días para tu cita médica.</p>
                        <p><strong>Doctor:</strong> ${notificacion.medico}</p>
                        <small>Fecha de la cita: ${new Date(notificacion.fecha_cita).toLocaleDateString()}</small>
                    `;
                    notificationList.appendChild(notificacionDiv);
                });

                // Actualiza el contador de notificaciones
                const notificationCount = document.getElementById('notification-count');
                notificationCount.textContent = data.length;
            })
            .catch(error => console.error('Error al cargar notificaciones:', error));
    }

    // Función para cargar el número de notificaciones
    function cargarNumeroDeCitas() {
        fetch('/api/notificaciones')
            .then(response => response.json())
            .then(data => {
                const countElement = document.getElementById('notification-count');
                countElement.textContent = data.length; // Actualiza el número de notificaciones
            })
            .catch(error => console.error('Error al cargar el número de citas:', error));
    }
});
