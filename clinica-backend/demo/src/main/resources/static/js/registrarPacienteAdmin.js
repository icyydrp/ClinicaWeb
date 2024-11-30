document.addEventListener('DOMContentLoaded', () => {
    const formPaciente = document.getElementById('formPaciente');
    const mensajeDiv = document.getElementById('mensaje');

    // Cargar datos si es edición (opcional)
    const params = new URLSearchParams(window.location.search);
    const pacienteId = params.get('id');
    if (pacienteId) {
        cargarPaciente(pacienteId);
    }

    // Manejo del envío del formulario
    formPaciente.addEventListener('submit', async (e) => {
        e.preventDefault(); // Prevenir el comportamiento predeterminado del formulario

        // Crear objeto paciente desde los campos del formulario
        const paciente = {
            nombres: document.getElementById('nombres').value.trim(),
            apellidos: document.getElementById('apellidos').value.trim(),
            correo: document.getElementById('correo').value.trim(),
            contraseña: document.getElementById('contraseña').value.trim(),
            dni: document.getElementById('dni').value.trim(),
            celular: document.getElementById('celular').value.trim(),
        };

        // Determinar método y URL según si es creación o edición
        const method = pacienteId ? 'PUT' : 'POST';
        const url = pacienteId ? `/pacientes/${pacienteId}` : '/pacientes/admin/registrar';

        try {
            // Enviar datos al servidor
            const response = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(paciente),
            });

            const result = await response.json();

            if (response.ok) {
                // Mostrar mensaje de éxito
                mensajeDiv.innerHTML = `<div class="alert alert-success">${result.message || 'Operación exitosa'}</div>`;
                if (!pacienteId) formPaciente.reset();
            } else {
                // Mostrar mensaje de error del servidor
                mensajeDiv.innerHTML = `<div class="alert alert-danger">${result.message || 'Error al guardar'}</div>`;
            }
        } catch (error) {
            // Manejo de errores inesperados
            mensajeDiv.innerHTML = `<div class="alert alert-danger">Error inesperado. Intenta nuevamente.</div>`;
            console.error('Error al registrar el paciente:', error);
        }
    });

    // Función para cargar datos del paciente si se edita
    async function cargarPaciente(id) {
        try {
            const response = await fetch(`/pacientes/${id}`);
            const paciente = await response.json();

            if (response.ok) {
                document.getElementById('nombres').value = paciente.nombres || '';
                document.getElementById('apellidos').value = paciente.apellidos || '';
                document.getElementById('correo').value = paciente.correo || '';
                document.getElementById('contraseña').value = paciente.contraseña || '';
                document.getElementById('dni').value = paciente.dni || '';
                document.getElementById('celular').value = paciente.celular || '';
            } else {
                mensajeDiv.innerHTML = `<div class="alert alert-danger">Paciente no encontrado.</div>`;
            }
        } catch (error) {
            mensajeDiv.innerHTML = `<div class="alert alert-danger">Error al cargar los datos del paciente.</div>`;
            console.error('Error al cargar el paciente:', error);
        }
    }
});
