document.addEventListener('DOMContentLoaded', () => {
    const formPaciente = document.getElementById('formPaciente');
    const mensajeDiv = document.getElementById('mensaje');

    // Cargar datos si es edición
    const params = new URLSearchParams(window.location.search);
    const pacienteId = params.get('id');
    if (pacienteId) {
        cargarPaciente(pacienteId);
    }

    formPaciente.addEventListener('submit', async (e) => {
        e.preventDefault();

        const paciente = {
            nombres: document.getElementById('nombres').value.trim(),
            apellidos: document.getElementById('apellidos').value.trim(),
            correo: document.getElementById('correo').value.trim(),
            contraseña: document.getElementById('contraseña').value.trim(),
            dni: document.getElementById('dni').value.trim(),
            celular: document.getElementById('celular').value.trim(),
        };

        const method = pacienteId ? 'PUT' : 'POST';
        const url = pacienteId ? `/pacientes/${pacienteId}` : '/pacientes';

        try {
            const response = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(paciente),
            });

            const result = await response.json();

            if (response.ok) {
                mensajeDiv.innerHTML = `<div class="alert alert-success">${result.message || 'Operación exitosa'}</div>`;
                if (!pacienteId) formPaciente.reset();
            } else {
                mensajeDiv.innerHTML = `<div class="alert alert-danger">${result.message || 'Error al guardar'}</div>`;
            }
        } catch (error) {
            mensajeDiv.innerHTML = `<div class="alert alert-danger">Error inesperado</div>`;
            console.error(error);
        }
    });

    async function cargarPaciente(id) {
        try {
            const response = await fetch(`/pacientes/${id}`);
            if (!response.ok) {
                throw new Error('Paciente no encontrado');
            }
            const paciente = await response.json();
            
            // Prellenar los campos con los datos del paciente
            document.getElementById('nombres').value = paciente.nombres || '';
            document.getElementById('apellidos').value = paciente.apellidos || '';
            document.getElementById('correo').value = paciente.correo || '';
            document.getElementById('contraseña').value = paciente.contraseña || '';
            document.getElementById('dni').value = paciente.dni || '';
            document.getElementById('celular').value = paciente.celular || '';
        } catch (error) {
            console.error('Error al cargar paciente:', error);
            document.getElementById('mensaje').innerHTML = `
                <div class="alert alert-danger">Error al cargar los datos del paciente.</div>`;
        }
    }
    
});
