document.addEventListener("DOMContentLoaded", () => {
    // Elementos del DOM
    const notificationIcon = document.querySelector('.notification-icon');
    const notificationList = document.getElementById('notification-list');
    const notificationCount = document.getElementById('notification-count');
    const ul = document.getElementById('notifications'); // Lista de notificaciones

    // Inicialización
    cargarDatosMedico();
    validarSesionMedico().then(() => {
        cargarNotificaciones();
    }).catch((error) => {
        console.error('Sesión no válida:', error);
        alert("Sesión no válida. Por favor, inicia sesión.");
        window.location.href = "/login.html";
    });

    // Mostrar/ocultar lista de notificaciones al hacer clic en el ícono
    notificationIcon.addEventListener('click', (event) => {
        event.stopPropagation();
        const isHidden = notificationList.style.display === 'none';
        notificationList.style.display = isHidden ? 'block' : 'none';
    });

    // Cerrar notificaciones si se hace clic fuera del ícono o la lista
    document.addEventListener('click', (event) => {
        if (!notificationIcon.contains(event.target) && !notificationList.contains(event.target)) {
            notificationList.style.display = 'none';
        }
    });


    document.getElementById('uploadPic').addEventListener('change', subirFoto);
    document.getElementById('cambiar-password').addEventListener('click', cambiarPassword);
    document.getElementById('guardar-cambios').addEventListener('click', guardarCambios);

    // Función para cargar los datos del médico desde el backend
    function cargarDatosMedico() {
        fetch('/api/perfil/medico')
            .then(response => {
                if (!response.ok) throw new Error('Error al cargar los datos del médico.');
                return response.json();
            })
            .then(data => {
                if (data) {
                    document.getElementById('nombre').value = data.nombres || '';
                    document.getElementById('apellido').value = data.apellidos || '';
                    document.getElementById('correo').value = data.correo || '';
                    document.getElementById('dni').value = data.dni || '';
                    document.getElementById('especialidad').value = data.especialidad || '';
                    document.getElementById('numero_colegiatura').value = data.numeroColegiatura || '';
                    document.getElementById('profilePic').src = data.foto || '/uploads/default.jpg';
                }
            })
            .catch(error => {
                console.error('Error al cargar los datos del médico:', error);
                alert('No se pudieron cargar los datos del médico.');
            });
    }
    

    // Función para subir la foto de perfil
    function subirFoto(event) {
        const formData = new FormData();
        formData.append('foto', event.target.files[0]);

        fetch('/api/perfil/medico/subirFoto', { method: 'POST', body: formData })
            .then(response => {
                if (!response.ok) throw new Error('Error al subir la foto');
                return response.text();
            })
            .then(() => {
                alert('Foto subida con éxito.');
                cargarDatosMedico();
            })
            .catch(error => {
                console.error('Error al subir la foto:', error);
                alert('Error al subir la foto. Verifica el tamaño permitido.');
            });
    }

    // Función para cambiar la contraseña
    function cambiarPassword() {
        const currentPassword = document.getElementById('current_password').value;
        const newPassword = document.getElementById('new_password').value;

        if (!currentPassword || !newPassword) {
            alert('Completa ambos campos de contraseña.');
            return;
        }

        const passwords = { current_password: currentPassword, new_password: newPassword };

        fetch('/api/perfil/medico/cambiarPassword', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(passwords)
        })
            .then(response => {
                if (!response.ok) throw new Error('Contraseña incorrecta.');
                return response.text();
            })
            .then(() => {
                alert('Contraseña cambiada exitosamente.');
                document.getElementById('current_password').value = newPassword;
                document.getElementById('new_password').value = '';
            })
            .catch(error => {
                console.error('Error al cambiar la contraseña:', error);
                alert('Error al cambiar la contraseña.');
            });
    }

    // Función para guardar cambios en los datos personales
    function guardarCambios() {
        const data = {
            nombres: document.getElementById('nombre').value,
            apellidos: document.getElementById('apellido').value,
            correo: document.getElementById('correo').value,
            dni: document.getElementById('dni').value,
            especialidad: document.getElementById('especialidad').value,
            numeroColegiatura: document.getElementById('numero_colegiatura').value
        };

        fetch('/api/perfil/medico/actualizar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (!response.ok) throw new Error('Error al guardar cambios.');
                return response.text();
            })
            .then(() => {
                alert('Datos actualizados con éxito.');
                cargarDatosMedico();
            })
            .catch(error => {
                console.error('Error al guardar cambios:', error);
                alert('No se pudieron guardar los cambios.');
            });
    }

    // Función para cargar las notificaciones desde el backend
    function cargarNotificaciones() {
        const medicoId = sessionStorage.getItem('medicoId'); // Obtener ID del médico

        fetch(`/api/medico/${medicoId}/notificaciones`)
            .then(response => {
                if (!response.ok) throw new Error("Error al obtener notificaciones.");
                return response.json();
            })
            .then(notificaciones => {
                console.log("Notificaciones recibidas:", notificaciones);

                notificationCount.textContent = notificaciones.length;
                ul.innerHTML = ''; // Limpiar notificaciones previas

                if (notificaciones.length === 0) {
                    const li = document.createElement('li');
                    li.textContent = "No tienes nuevas notificaciones.";
                    ul.appendChild(li);
                    return;
                }

                notificaciones.forEach(notificacion => {
                    const paciente = notificacion.paciente || {};
                    const nombreCompleto = `${paciente.nombres || 'Desconocido'} ${paciente.apellidos || ''}`;
                    const fechaCita = notificacion.fecha ? 
                        new Date(notificacion.fecha).toLocaleDateString('es-ES') : 'Fecha no disponible';
                    const diasRestantes = notificacion.fecha ? calcularDiasRestantes(notificacion.fecha) : 'N/A';

                    const div = document.createElement('div');
                    div.classList.add('notification-card');
                    div.innerHTML = `
                        <h3 class="${notificacion.estado === 'Aceptada' ? 'aceptada' : 'pendiente'}">
                            ${notificacion.estado === 'Aceptada' ? 'CITA ACEPTADA' : 'CITA PENDIENTE'}
                        </h3>
                        <p><strong>Nombre del paciente:</strong> ${nombreCompleto}</p>
                        ${notificacion.estado === 'Aceptada' ? `
                            <p><strong>Fecha de la cita:</strong> ${fechaCita}</p>
                            <p><strong>Días restantes para la cita:</strong> ${diasRestantes}</p>
                        ` : `
                            <p>Tienes una cita pendiente con el paciente ${nombreCompleto}</p>
                        `}
                    `;
                    ul.appendChild(div);
                });
            })
            .catch(error => {
                console.error('Error al cargar notificaciones:', error);
                alert("Ocurrió un error al cargar las notificaciones.");
            });
    }
    


    // Función para calcular los días restantes
    function calcularDiasRestantes(fechaCita) {
        const fecha = new Date(fechaCita);
        const hoy = new Date();
        // Normalizar fechas para comparar solo la parte de la fecha, sin horas
        fecha.setHours(0, 0, 0, 0);
        hoy.setHours(0, 0, 0, 0);
        const diferencia = fecha - hoy;
        return Math.max(0, Math.ceil(diferencia / (1000 * 60 * 60 * 24)));
    }

    // Función para validar sesión del médico
    async function validarSesionMedico() {
        try {
            const response = await fetch('/api/sesion/medico'); // Cambia según la ruta correcta
            if (!response.ok) {
                throw new Error("Sesión no válida");
            }
            const data = await response.json();
            console.log("Sesión activa para el médico:", data);
    
            // Guarda los datos del médico en sessionStorage
            sessionStorage.setItem('medicoId', data.medicoId || '');
            sessionStorage.setItem('nombres', data.nombres || '');
            sessionStorage.setItem('apellidos', data.apellidos || '');
    
            // Muestra el nombre del médico en la interfaz
            const medicoNombre = data.nombres ? `Bienvenido Dr. ${data.nombres}` : 'Bienvenido Doctor';
            document.getElementById('medicoNombre').textContent = medicoNombre;
        } catch (error) {
            console.error('Sesión no válida:', error);
            alert("Sesión no válida. Por favor, inicia sesión.");
            window.location.href = "/login.html";
        }
    }
    
    

    
});

function cerrarSesion() {
    fetch('/logout', { method: 'GET' })
        .then(() => window.location.href = "/login.html")
        .catch(error => console.error('Error al cerrar sesión:', error));
}
