document.addEventListener('DOMContentLoaded', function () {
    cargarDatosPaciente();

    // Asocia los eventos a los botones
    document.getElementById('cambiar-password').addEventListener('click', cambiarPassword);
    document.getElementById('guardar-cambios').addEventListener('click', guardarCambios);

    // Subir foto al seleccionar una nueva
    document.getElementById('uploadPic').addEventListener('change', subirFoto);
});

// Función para cargar los datos del paciente
function cargarDatosPaciente() {
    fetch('/api/perfil/paciente')
        .then(response => {
            if (!response.ok) {
                throw new Error('Sesión expirada. Redirigiendo al login...');
            }
            return response.json();
        })
        .then(data => {
            document.getElementById('nombres').value = data.nombres || '';
            document.getElementById('apellidos').value = data.apellidos || '';
            document.getElementById('correo').value = data.correo || '';
            document.getElementById('dni').value = data.dni || '';
            document.getElementById('celular').value = data.celular || '';

            if (data.foto) {
                document.getElementById('profilePic').src = data.foto;
            }
        })
        .catch(error => {
            console.error('Error al cargar los datos del paciente:', error);
            alert('No se pudo cargar la información del paciente.');
            window.location.href = '/login.html';
        });
}

// Función para subir la foto del paciente
function subirFoto(event) {
    const formData = new FormData();
    formData.append('foto', event.target.files[0]);

    fetch('/api/perfil/paciente/subirFoto', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al subir la foto');
            }
            return response.text();
        })
        .then(() => {
            alert('Foto subida con éxito');
            window.location.reload(); // Recargar para mostrar la nueva foto
        })
        .catch(error => {
            console.error('Error al subir la foto:', error);
            alert('Error al subir la foto.');
        });
}

// Función para guardar los cambios en los datos del paciente
function guardarCambios() {
    const datosPaciente = {
        nombres: document.getElementById('nombres').value,
        apellidos: document.getElementById('apellidos').value,
        correo: document.getElementById('correo').value,
        dni: document.getElementById('dni').value,
        celular: document.getElementById('celular').value
    };

    fetch('/api/perfil/paciente/actualizar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(datosPaciente)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al guardar los datos.');
            }
            alert('Datos guardados exitosamente.');
        })
        .catch(error => {
            console.error('Error al guardar los datos:', error);
            alert('No se pudo guardar los datos.');
        });
}

// Función para cambiar la contraseña del paciente
function cambiarPassword() {
    const currentPassword = document.getElementById('current_password').value;
    const newPassword = document.getElementById('new_password').value;

    if (!currentPassword || !newPassword) {
        alert('Por favor, complete ambos campos de cosntraseña.');
        return;
    }

    const passwords = {
        current_password: currentPassword,
        new_password: newPassword
    };

    fetch('/api/perfil/paciente/cambiarPassword', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(passwords)
    })
        .then(response => {
            return response.json().then(data => {
                if (!response.ok) {
                    throw new Error(data.message || 'Error desconocido.');
                }
                return data;
            });
        })
        .then(data => {
            alert(data.message);  // Mostrar el mensaje desde la respuesta
            // Limpiar los campos de contraseña
            document.getElementById('current_password').value = '';
            document.getElementById('new_password').value = '';
        })
        .catch(error => {
            console.error('Error al cambiar la contraseña:', error);
            alert(error.message || 'No se pudo cambiar la contraseña.');
        });
}
