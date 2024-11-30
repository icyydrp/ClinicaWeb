document.getElementById('formRegistrarMedico').addEventListener('submit', async (e) => {
    e.preventDefault(); // Evitar el envío predeterminado del formulario

    // Obtener los valores de los campos del formulario
    const medico = {
        nombres: document.getElementById('nombres').value.trim(),
        apellidos: document.getElementById('apellidos').value.trim(),
        correo: document.getElementById('correo').value.trim(),
        contraseña: document.getElementById('contraseña').value.trim(),
        numeroColegiatura: document.getElementById('numeroColegiatura').value.trim(),
        especialidad: document.getElementById('especialidad').value.trim(),
        dni: document.getElementById('dni').value.trim(),
    };

    const mensajeDiv = document.getElementById('mensaje'); // Div para mensajes
    mensajeDiv.innerHTML = ''; // Limpiar mensajes previos

    try {
        // Validación básica de los campos del formulario
        if (!validarFormulario(medico)) {
            mensajeDiv.innerHTML = `
                <div class="alert alert-warning">Por favor, completa todos los campos correctamente.</div>`;
            return;
        }

        // Enviar los datos al backend
        const response = await fetch('/medicos/admin/registrar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(medico),
        });

        // Procesar la respuesta del servidor
        const result = await response.json();

        if (response.ok) {
            // Mostrar mensaje de éxito
            mensajeDiv.innerHTML = `
                <div class="alert alert-success">${result.message}</div>`;
            document.getElementById('formRegistrarMedico').reset();
        } else {
            // Mostrar mensaje de error del servidor
            mensajeDiv.innerHTML = `
                <div class="alert alert-danger">${result.message}</div>`;
        }
    } catch (error) {
        console.error('Error en la solicitud:', error);
        mensajeDiv.innerHTML = `
            <div class="alert alert-danger">Ocurrió un error inesperado. Intenta nuevamente.</div>`;
    }
});

// Función para validar los campos del formulario
function validarFormulario(medico) {
    const camposRequeridos = [
        'nombres',
        'apellidos',
        'correo',
        'contraseña',
        'numeroColegiatura',
        'especialidad',
        'dni',
    ];

    // Verificar que todos los campos estén llenos
    for (const campo of camposRequeridos) {
        if (!medico[campo]) {
            return false; // Si algún campo está vacío, la validación falla
        }
    }

    // Validar el formato del correo
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(medico.correo)) {
        alert('El correo no tiene un formato válido.');
        return false;
    }

    // Validar el DNI (por ejemplo, 8 dígitos en Perú)
    if (!/^\d{8}$/.test(medico.dni)) {
        alert('El DNI debe contener exactamente 8 dígitos.');
        return false;
    }

    // Validar la contraseña (mínimo 8 caracteres con una mayúscula, un número y un símbolo)
    const passwordRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    if (!passwordRegex.test(medico.contraseña)) {
        alert(
            'La contraseña debe tener al menos 8 caracteres, incluyendo una letra mayúscula, un número y un símbolo.'
        );
        return false;
    }

    return true; // Si todo está bien, la validación pasa
}
