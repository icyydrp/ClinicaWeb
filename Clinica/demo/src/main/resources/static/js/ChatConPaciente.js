document.addEventListener("DOMContentLoaded", () => {
    const enviarBtn = document.getElementById('enviarMensajeBtn');
    const mensajeInput = document.getElementById('mensajeInput');
    const chatContainer = document.getElementById('chatContainer');
    const listaPacientes = document.getElementById('listaPacientes');

    cargarPacientes();

    enviarBtn.addEventListener('click', enviarMensaje);
    mensajeInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') enviarMensaje();
    });

    function cargarPacientes() {
        fetch('/api/sesion/medico')
            .then(response => response.json())
            .then(data => fetch(`/api/medico/citas/${data.medicoId}`))
            .then(response => response.json())
            .then(citas => mostrarPacientes(citas))
            .catch(error => console.error('Error al cargar pacientes:', error));
    }

    function mostrarPacientes(citas) {
        const pacientesUnicos = new Set();
        citas.forEach(cita => {
            if (cita.paciente) {
                pacientesUnicos.add(JSON.stringify({
                    id: cita.paciente.id,
                    nombres: cita.paciente.nombres,
                    apellidos: cita.paciente.apellidos
                }));
            }
        });

        listaPacientes.innerHTML = '';
        Array.from(pacientesUnicos).forEach(pacienteStr => {
            const paciente = JSON.parse(pacienteStr);
            const li = document.createElement('li');
            li.classList.add('list-group-item');
            li.textContent = `${paciente.nombres} ${paciente.apellidos}`;
            li.addEventListener('click', () => seleccionarPaciente(paciente.id, paciente.nombres, paciente.apellidos));
            listaPacientes.appendChild(li);
        });
    }

    function seleccionarPaciente(pacienteId, nombres, apellidos) {
        document.getElementById('tituloChat').textContent = `Chat con ${nombres} ${apellidos}`;
        chatContainer.innerHTML = '';
        cargarMensajes(pacienteId);
    }

    function cargarMensajes(pacienteId) {
        fetch(`/api/chat/paciente/${pacienteId}`)
            .then(response => response.json())
            .then(mensajes => {
                mensajes.forEach(mensaje => agregarMensaje(mensaje.remitente, mensaje.mensaje));
            })
            .catch(error => console.error('Error al cargar mensajes:', error));
    }

    function enviarMensaje() {
        const mensaje = mensajeInput.value.trim();
        if (!mensaje) {
            alert('No puedes enviar un mensaje vacío.');
            return;
        }

        const mensajeData = {
            remitente: 'medico',
            mensaje: mensaje
        };

        const pacienteId = new URLSearchParams(window.location.search).get('pacienteId');
        fetch(`/api/chat/${pacienteId}/enviar`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(mensajeData)
        })
            .then(response => {
                if (!response.ok) {
                    alert('Error al enviar el mensaje.');
                    return;
                }
                agregarMensaje('medico', mensaje);
                mensajeInput.value = '';
            })
            .catch(error => console.error('Error al enviar mensaje:', error));
    }

    function agregarMensaje(remitente, mensaje) {
        const messageDiv = document.createElement('div');
        messageDiv.classList.add('chat-message', remitente);

        const bubbleDiv = document.createElement('div');
        bubbleDiv.classList.add('chat-bubble', remitente);
        bubbleDiv.textContent = mensaje;

        messageDiv.appendChild(bubbleDiv);
        chatContainer.appendChild(messageDiv);

        chatContainer.scrollTop = chatContainer.scrollHeight;
    }
});
