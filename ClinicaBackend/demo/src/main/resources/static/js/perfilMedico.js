// Función para habilitar la edición de los campos
function habilitarEdicion(campoId) {
    document.getElementById(campoId).disabled = false;
}

// Función para cargar los datos del perfil automáticamente desde la sesión
// Función para cargar los datos del perfil automáticamente desde la sesión
function cargarPerfilMedico() {
    fetch('/obtener-datos-medico')
        .then(response => response.json())
        .then(medico => {
            if (medico.success === false) {
                console.error('Error al obtener los datos del médico:', medico.message);
            } else {
                document.getElementById('nombre').value = medico.nombres;
                document.getElementById('apellido').value = medico.apellidos;
                document.getElementById('correo').value = medico.correo;
                document.getElementById('dni').value = medico.dni;
                document.getElementById('especialidad').value = medico.especialidad;
                document.getElementById('numero_colegiatura').value = medico.numeroColegiatura; // Cambiado aquí
                if (medico.foto) {
                    document.getElementById('profilePic').src = medico.foto;
                }
            }
        })
        .catch(error => {
            console.error('Error al cargar el perfil del médico:', error);
        });
}


// Llama a la función cuando la página se carga
cargarPerfilMedico();





// Función para guardar los cambios del perfil
function guardarCambios() {
    const nombre = document.getElementById('nombre').value;
    const apellido = document.getElementById('apellido').value;
    const correo = document.getElementById('correo').value;
    const dni = document.getElementById('dni').value;
    const especialidad = document.getElementById('especialidad').value;
    const numero_colegiatura = document.getElementById('numero_colegiatura').value;

    const updatedData = {
        nombre: nombre,
        apellido: apellido,
        correo: correo,
        dni: dni,
        especialidad: especialidad,
        numero_colegiatura: numero_colegiatura
    };

    fetch('/actualizar-datos-medico', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(updatedData)
    })
    .then(response => {
        if (response.ok) {
            alert('Cambios guardados exitosamente');
        } else {
            alert('Error al guardar los cambios');
        }
    })
    .catch(error => {
        console.error('Error al guardar los cambios:', error);
    });
}

// Función para cambiar la contraseña
function cambiarPassword() {
    const currentPassword = document.getElementById('current_password').value;
    const newPassword = document.getElementById('new_password').value;

    fetch('/cambiar-password', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ current_password: currentPassword, new_password: newPassword })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('Contraseña cambiada exitosamente');
        } else {
            alert('Error al cambiar la contraseña');
        }
    })
    .catch(error => {
        console.error('Error al cambiar la contraseña:', error);
    });
}


// Función para cambiar la foto de perfil y guardarla automáticamente
// Función para cambiar la foto de perfil y guardarla automáticamente
function cambiarFoto() {
    const formData = new FormData();
    const file = document.getElementById('uploadPic').files[0];
    if (!file) {
        alert('Por favor selecciona una imagen.');
        return;
    }

    formData.append('foto', file);

    fetch('/subir-foto', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            document.getElementById('profilePic').src = data.foto_url; // Actualizar imagen en la página
        } else {
            alert('Error al subir la imagen');
        }
    })
    .catch(error => {
        console.error('Error al subir la imagen:', error);
    });
}

