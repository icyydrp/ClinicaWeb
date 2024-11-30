document.addEventListener('DOMContentLoaded', () => {
    const formMedico = document.getElementById('formMedico');
    const mensajeDiv = document.getElementById('mensaje');

    // Cargar datos si es edición
    const params = new URLSearchParams(window.location.search);
    const medicoId = params.get('id');
    if (medicoId) {
        cargarMedico(medicoId);
    }

    formMedico.addEventListener('submit', async (e) => {
        e.preventDefault();

        const medico = {
            nombres: document.getElementById('nombres').value.trim(),
            apellidos: document.getElementById('apellidos').value.trim(),
            correo: document.getElementById('correo').value.trim(),
            contraseña: document.getElementById('contraseña').value.trim(),
            dni: document.getElementById('dni').value.trim(),
            especialidad: document.getElementById('especialidad').value.trim(),
            numeroColegiatura: document.getElementById('numeroColegiatura').value.trim(),
        };

        const method = medicoId ? 'PUT' : 'POST';
        const url = medicoId ? `/medicos/${medicoId}` : '/medicos';

        try {
            const response = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(medico),
            });

            const result = await response.json();

            if (response.ok) {
                mensajeDiv.innerHTML = `<div class="alert alert-success">${result.message || 'Operación exitosa'}</div>`;
                if (!medicoId) formMedico.reset();
            } else {
                mensajeDiv.innerHTML = `<div class="alert alert-danger">${result.message || 'Error al guardar'}</div>`;
            }
        } catch (error) {
            mensajeDiv.innerHTML = `<div class="alert alert-danger">Error inesperado</div>`;
            console.error(error);
        }
    });

    async function cargarMedico(id) {
        try {
            const response = await fetch(`/medicos/${id}`);
            const medico = await response.json();

            if (response.ok) {
                document.getElementById('nombres').value = medico.nombres;
                document.getElementById('apellidos').value = medico.apellidos;
                document.getElementById('correo').value = medico.correo;
                document.getElementById('contraseña').value = medico.contraseña;
                document.getElementById('dni').value = medico.dni;
                document.getElementById('especialidad').value = medico.especialidad;
                document.getElementById('numeroColegiatura').value = medico.numeroColegiatura;
            } else {
                mensajeDiv.innerHTML = `<div class="alert alert-danger">Médico no encontrado</div>`;
            }
        } catch (error) {
            mensajeDiv.innerHTML = `<div class="alert alert-danger">Error al cargar médico</div>`;
            console.error(error);
        }
    }
});
