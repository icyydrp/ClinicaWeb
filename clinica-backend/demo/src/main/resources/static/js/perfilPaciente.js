document.addEventListener('DOMContentLoaded', function () {
    cargarDatosPaciente();

    // Asociar los eventos a los botones
    document.getElementById('cambiar-password').addEventListener('click', cambiarPassword);
    document.getElementById('guardar-cambios').addEventListener('click', guardarCambios);

    // Subir foto al seleccionar una nueva
    document.getElementById('uploadPic').addEventListener('change', subirFoto);
});

// Función para cargar los datos del paciente
async function cargarDatosPaciente() {
    const token = localStorage.getItem('authToken');

    if (!token) {
        alert('No tienes autorización. Por favor, inicia sesión.');
        window.location.href = '/login.html';
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/paciente/sesion', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Sesión expirada o token inválido. Redirigiendo al login...');
        }

        const data = await response.json();

        document.getElementById('nombres').value = data.nombres || '';
        document.getElementById('apellidos').value = data.apellidos || '';
        document.getElementById('correo').value = data.correo || '';
        document.getElementById('dni').value = data.dni || '';
        document.getElementById('celular').value = data.celular || '';

        if (data.foto) {
            document.getElementById('profilePic').src = data.foto;
        }
    } catch (error) {
        console.error('Error al cargar los datos del paciente:', error);
        alert('No se pudo cargar la información del paciente.');
        window.location.href = '/login.html';
    }
}

// Función para subir la foto del paciente
async function subirFoto(event) {
    const token = localStorage.getItem('authToken');

    if (!token) {
        alert('No tienes autorización. Por favor, inicia sesión.');
        window.location.href = '/login.html';
        return;
    }

    const formData = new FormData();
    formData.append('foto', event.target.files[0]);

    try {
        const response = await fetch('http://localhost:8080/api/perfil/paciente/subirFoto', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token
            },
            body: formData
        });

        if (!response.ok) {
            throw new Error('Error al subir la foto.');
        }

        alert('Foto subida con éxito');
        window.location.reload(); // Recargar para mostrar la nueva foto
    } catch (error) {
        console.error('Error al subir la foto:', error);
        alert('Error al subir la foto.');
    }
}

// Función para guardar los cambios en los datos del paciente
async function guardarCambios() {
    const token = localStorage.getItem('authToken');

    if (!token) {
        alert('No tienes autorización. Por favor, inicia sesión.');
        window.location.href = '/login.html';
        return;
    }

    const datosPaciente = {
        nombres: document.getElementById('nombres').value,
        apellidos: document.getElementById('apellidos').value,
        correo: document.getElementById('correo').value,
        dni: document.getElementById('dni').value,
        celular: document.getElementById('celular').value
    };

    try {
        const response = await fetch('http://localhost:8080/api/perfil/paciente/actualizar', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datosPaciente)
        });

        if (!response.ok) {
            throw new Error('Error al guardar los datos.');
        }

        alert('Datos guardados exitosamente.');
    } catch (error) {
        console.error('Error al guardar los datos:', error);
        alert('No se pudo guardar los datos.');
    }
}

// Función para cambiar la contraseña del paciente
async function cambiarPassword() {
    const token = localStorage.getItem('authToken');

    if (!token) {
        alert('No tienes autorización. Por favor, inicia sesión.');
        window.location.href = '/login.html';
        return;
    }

    const currentPassword = document.getElementById('current_password').value;
    const newPassword = document.getElementById('new_password').value;

    if (!currentPassword || !newPassword) {
        alert('Por favor, complete ambos campos de contraseña.');
        return;
    }

    const passwords = {
        current_password: currentPassword,
        new_password: newPassword
    };

    try {
        const response = await fetch('http://localhost:8080/api/perfil/paciente/cambiarPassword', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(passwords)
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || 'Error desconocido.');
        }

        alert(data.message);  // Mostrar el mensaje desde la respuesta
        // Limpiar los campos de contraseña
        document.getElementById('current_password').value = '';
        document.getElementById('new_password').value = '';
    } catch (error) {
        console.error('Error al cambiar la contraseña:', error);
        alert(error.message || 'No se pudo cambiar la contraseña.');
    }
}
