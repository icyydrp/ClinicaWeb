document.addEventListener('DOMContentLoaded', () => {
    const pacientesTable = document.getElementById('pacientesTable');
    const medicosTable = document.getElementById('medicosTable');
    const searchPacienteBtn = document.getElementById('searchPacienteBtn');
    const searchMedicoBtn = document.getElementById('searchMedicoBtn');
    const addPacienteBtn = document.getElementById('addPacienteBtn');
    const addMedicoBtn = document.getElementById('addMedicoBtn');

    // Cargar pacientes y médicos al iniciar
    cargarPacientes();
    cargarMedicos();

    // Buscar pacientes
    searchPacienteBtn.addEventListener('click', () => {
        const query = document.getElementById('searchPaciente').value.trim();
        cargarPacientes(query);
    });

    // Buscar médicos
    searchMedicoBtn.addEventListener('click', () => {
        const query = document.getElementById('searchMedico').value.trim();
        cargarMedicos(query);
    });

    // Función para cargar pacientes
    async function cargarPacientes(query = '') {
        try {
            const response = await fetch(`/pacientes?search=${query}`);
            if (!response.ok) {
                throw new Error('Error al obtener pacientes');
            }
            const pacientes = await response.json();

            pacientesTable.innerHTML = ''; // Limpiar tabla

            if (pacientes.length === 0) {
                pacientesTable.innerHTML = `<tr><td colspan="5" class="text-center">No se encontraron pacientes</td></tr>`;
                return;
            }

            pacientes.forEach(paciente => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${paciente.id}</td>
                    <td>${paciente.nombres} ${paciente.apellidos}</td>
                    <td>${paciente.correo}</td>
                    <td>${paciente.dni}</td>
                    <td class="text-center">
                        <button class="btn btn-warning btn-sm" onclick="editarPaciente(${paciente.id})">
                            <i class="fas fa-edit"></i> Editar
                        </button>
                        <button class="btn btn-danger btn-sm" onclick="eliminarPaciente(${paciente.id})">
                            <i class="fas fa-trash"></i> Eliminar
                        </button>
                    </td>
                `;
                pacientesTable.appendChild(row);
            });
        } catch (error) {
            console.error('Error al cargar pacientes:', error);
            pacientesTable.innerHTML = `<tr><td colspan="5" class="text-center text-danger">Error al cargar pacientes</td></tr>`;
        }
    }

    // Función para cargar médicos
    async function cargarMedicos(query = '') {
        try {
            const response = await fetch(`/medicos?search=${query}`);
            if (!response.ok) {
                throw new Error('Error al obtener médicos');
            }
            const medicos = await response.json();

            medicosTable.innerHTML = ''; // Limpiar tabla

            if (medicos.length === 0) {
                medicosTable.innerHTML = `<tr><td colspan="6" class="text-center">No se encontraron médicos</td></tr>`;
                return;
            }

            medicos.forEach(medico => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${medico.id}</td>
                    <td>${medico.nombres} ${medico.apellidos}</td>
                    <td>${medico.correo}</td>
                    <td>${medico.dni}</td>
                    <td>${medico.especialidad}</td>
                    <td class="text-center">
                        <button class="btn btn-warning btn-sm" onclick="editarMedico(${medico.id})">
                            <i class="fas fa-edit"></i> Editar
                        </button>
                        <button class="btn btn-danger btn-sm" onclick="eliminarMedico(${medico.id})">
                            <i class="fas fa-trash"></i> Eliminar
                        </button>
                    </td>
                `;
                medicosTable.appendChild(row);
            });
        } catch (error) {
            console.error('Error al cargar médicos:', error);
            medicosTable.innerHTML = `<tr><td colspan="6" class="text-center text-danger">Error al cargar médicos</td></tr>`;
        }
    }

    // Funciones para redirigir a agregar/editar
    addPacienteBtn.addEventListener('click', () => window.location.href = '/registroPacienteAdmin.html');
    addMedicoBtn.addEventListener('click', () => window.location.href = '/registroMedicoAdmin.html');
});

function editarPaciente(id) {
    window.location.href = `/editarPaciente.html?id=${id}`;
}

function editarMedico(id) {
    window.location.href = `/editarMedico.html?id=${id}`;
}

function eliminarPaciente(id) {
    if (confirm('¿Estás seguro de eliminar este paciente?')) {
        fetch(`/pacientes/${id}`, { method: 'DELETE' })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al eliminar paciente');
                }
                location.reload();
            })
            .catch(error => console.error('Error al eliminar paciente:', error));
    }
}

function eliminarMedico(id) {
    if (confirm('¿Estás seguro de eliminar este médico?')) {
        fetch(`/medicos/${id}`, { method: 'DELETE' })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al eliminar médico');
                }
                location.reload();
            })
            .catch(error => console.error('Error al eliminar médico:', error));
    }
}
