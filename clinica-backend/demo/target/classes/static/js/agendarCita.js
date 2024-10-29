document.addEventListener("DOMContentLoaded", function () {
    cargarIdPaciente(); // Cargar el ID del paciente desde la sesión
    document.getElementById('especialidad').addEventListener('change', cargarMedicos);
    document.getElementById("formAgendarCita").addEventListener("submit", agendarCita);
});

// Cargar el ID del paciente desde la sesión
async function cargarIdPaciente() {
    try {
        const response = await fetch('/api/sesion/paciente');
        if (!response.ok) {
            throw new Error("Sesión no válida. Redirigiendo al login.");
        }
        const data = await response.json();
        console.log("Paciente autenticado con ID:", data.pacienteId);
        document.getElementById('pacienteId').value = data.pacienteId;
    } catch (error) {
        console.error('Error al obtener ID del paciente:', error);
        window.location.href = "/login.html";
    }
}

// Cargar los médicos según la especialidad seleccionada
function cargarMedicos() {
    const especialidadSeleccionada = document.getElementById('especialidad').value;

    if (!especialidadSeleccionada) {
        document.getElementById('medico_id').innerHTML = '<option value="">Seleccione un médico</option>';
        return;
    }

    // Llamada al endpoint correcto para obtener los médicos por especialidad
    fetch(`/api/medicos/especialidad?especialidad=${especialidadSeleccionada}`)
        .then(response => response.json())
        .then(medicos => {
            const selectMedico = document.getElementById('medico_id');
            selectMedico.innerHTML = '<option value="">Seleccione un médico</option>';

            medicos.forEach(medico => {
                const option = document.createElement('option');
                option.value = medico.id;
                option.text = `${medico.nombres} ${medico.apellidos}`;
                selectMedico.add(option);
            });
        })
        .catch(error => console.error('Error al cargar los médicos:', error));
}



// Agendar una cita
async function agendarCita(event) {
    event.preventDefault();  // Evitar recarga de página

    const pacienteId = document.getElementById('pacienteId').value;
    const fecha = document.getElementById('fecha').value;
    const hora = document.getElementById('hora').value;
    const especialidad = document.getElementById('especialidad').value;
    const motivo = document.getElementById('motivo').value;
    const medicoId = document.getElementById('medico_id').value;

    // Verificar que todos los campos estén llenos
    if (!fecha || !hora || !especialidad || !motivo || !medicoId) {
        alert("Por favor, complete todos los campos.");
        return;
    }

    const data = new URLSearchParams({
        fecha,
        hora,
        especialidad,
        motivo,
        medico_id: medicoId,
        pacienteId: pacienteId  // Enviar el ID del paciente autenticado
    });

    try {
        const response = await fetch('/api/citas/agendar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: data
        });

        if (!response.ok) {
            throw new Error("Error al agendar la cita.");
        }

        const message = await response.text();
        alert(message);
        window.location.href = "/verCitasPaciente.html";  // Redirigir al historial de citas
    } catch (error) {
        console.error('Error al agendar la cita:', error);
        alert("Hubo un problema al agendar la cita. Inténtelo de nuevo.");
    }
}
