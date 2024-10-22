document.addEventListener('DOMContentLoaded', function() {
    // Cargar datos del paciente cuando la página se carga
    fetch('/api/paciente/perfil', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Mostrar la foto si existe
            document.getElementById('profilePic').src = data.foto || 'default.jpg';

            // Rellenar los campos del perfil con los datos del paciente
            document.getElementById('nombres').value = data.nombres || '';
            document.getElementById('apellidos').value = data.apellidos || '';
            document.getElementById('correo').value = data.correo || '';
            document.getElementById('dni').value = data.dni || '';
            document.getElementById('celular').value = data.celular || '';
        } else {
            alert('No se pudieron cargar los datos del paciente.');
        }
    })
    .catch(error => {
        console.error('Error al cargar los datos del paciente:', error);
    });

    // Mostrar la foto seleccionada y subirla al servidor
    document.getElementById('uploadPic').addEventListener('change', function(event) {
        const reader = new FileReader();
        reader.onload = function() {
            const output = document.getElementById('profilePic');
            output.src = reader.result;  // Mostrar la imagen seleccionada
        };
        reader.readAsDataURL(event.target.files[0]);

        // Subir la imagen al servidor
        const formData = new FormData();
        formData.append('foto', event.target.files[0]);

        fetch('/api/paciente/cambiar-foto', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('Foto cambiada exitosamente.');
            } else {
                alert('Error al cambiar la foto.');
            }
        })
        .catch(error => {
            console.error('Error al subir la foto:', error);
            alert('Error al subir la foto.');
        });
    });

    // Guardar cambios en los datos del paciente
    document.getElementById('guardar-cambios').addEventListener('click', function() {
        const datosPaciente = {
            nombres: document.getElementById('nombres').value,
            apellidos: document.getElementById('apellidos').value,
            correo: document.getElementById('correo').value,
            dni: document.getElementById('dni').value,
            celular: document.getElementById('celular').value
        };

        fetch('/api/paciente/actualizar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datosPaciente)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('Datos guardados exitosamente.');
            } else {
                alert('Error al guardar los datos.');
            }
        })
        .catch(error => {
            console.error('Error al guardar los cambios:', error);
            alert('Error al guardar los cambios.');
        });
    });

    // Cambiar contraseña del paciente
    document.getElementById('cambiar-password').addEventListener('click', function() {
        const passwords = {
            current_password: document.getElementById('current_password').value,
            new_password: document.getElementById('new_password').value
        };

        fetch('/api/paciente/cambiar-contrasena', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(passwords)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('Contraseña cambiada exitosamente.');
            } else {
                alert(data.message || 'Error al cambiar la contraseña.');
            }
        })
        .catch(error => {
            console.error('Error al cambiar la contraseña:', error);
            alert('Error al cambiar la contraseña.');
        });
    });
});
