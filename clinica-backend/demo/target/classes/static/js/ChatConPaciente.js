document.addEventListener("DOMContentLoaded", () => {
    const enviarBtn = document.getElementById('enviarMensajeBtn');
    const mensajeInput = document.getElementById('mensajeInput');
    const chatContainer = document.getElementById('chatContainer');
    const listaPacientes = document.getElementById('listaPacientes');
    const tituloChat = document.getElementById('tituloChat');

    let citaIdSeleccionada = null;

    // Intentar obtener el ID del médico desde la URL o sessionStorage
    const medicoId = new URLSearchParams(window.location.search).get('medicoId') || sessionStorage.getItem('medicoId');
    console.log('Medico ID:', medicoId); // Log para depuración

    if (!medicoId) {
        alert('ID del médico no encontrado. Asegúrate de que la URL incluya el parámetro medicoId.');
        return;
    }

    sessionStorage.setItem('medicoId', medicoId); // Guardar el ID en sessionStorage
    cargarPacientes(medicoId);

    enviarBtn.addEventListener('click', enviarMensaje);
    mensajeInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') enviarMensaje();
    });

    function cargarPacientes(medicoId) {
        fetch(`/api/chat/medico/${medicoId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('No se encontraron citas para este médico.');
                }
                return response.json();
            })
            .then(citas => {
                listaPacientes.innerHTML = ''; // Limpiar lista de pacientes
                const pacientesUnicos = new Map();

                // Filtrar citas para obtener pacientes únicos
                citas.forEach(cita => {
                    const paciente = cita.paciente;
                    if (paciente && !pacientesUnicos.has(paciente.id)) {
                        pacientesUnicos.set(paciente.id, paciente);
                    }
                });

                // Mostrar los pacientes en la lista
                pacientesUnicos.forEach((paciente) => {
                    const li = document.createElement('li');
                    li.classList.add('list-group-item');
                    li.textContent = `${paciente.nombres} ${paciente.apellidos}`;
                    li.addEventListener('click', () => seleccionarPaciente(paciente, medicoId));
                    listaPacientes.appendChild(li);
                });
            })
            .catch(error => console.error('Error al cargar los pacientes:', error));
    }

    function seleccionarPaciente(paciente, medicoId) {
        console.log(`Seleccionado paciente con ID: ${paciente.id}`);
        tituloChat.textContent = `Chat con ${paciente.nombres} ${paciente.apellidos}`;

        fetch(`/api/chat/medico/${medicoId}/paciente/${paciente.id}`)
            .then(response => response.json())
            .then(citas => {
                if (citas.length > 0) {
                    citaIdSeleccionada = citas[0].id; // Usar la primera cita encontrada
                    cargarMensajes();
                } else {
                    alert('No se encontraron citas con este paciente.');
                }
            })
            .catch(error => console.error('Error al seleccionar al paciente:', error));
    }

    function enviarMensaje() {
        const mensaje = mensajeInput.value.trim();
        if (!mensaje || !citaIdSeleccionada) {
            alert('Selecciona un paciente y escribe un mensaje.');
            return;
        }

        const mensajeData = {
            remitente: 'medico',
            mensaje: mensaje
        };

        fetch(`/api/chat/${citaIdSeleccionada}/enviar`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(mensajeData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al enviar el mensaje.');
                }
                agregarMensaje('medico', mensaje);
                mensajeInput.value = ''; // Limpiar input después de enviar
            })
            .catch(error => console.error('Error de red:', error));
    }

    function cargarMensajes() {
        if (!citaIdSeleccionada) return;

        fetch(`/api/chat/${citaIdSeleccionada}`)
            .then(response => response.json())
            .then(mensajes => {
                chatContainer.innerHTML = ''; // Limpiar mensajes anteriores
                mensajes.forEach(mensaje => {
                    agregarMensaje(mensaje.remitente, mensaje.mensaje);
                });
            })
            .catch(error => console.error('Error al cargar mensajes:', error));
    }

    function agregarMensaje(autor, mensaje) {
        const messageDiv = document.createElement('div');
        messageDiv.classList.add('chat-message', autor);

        const bubbleDiv = document.createElement('div');
        bubbleDiv.classList.add('chat-bubble', autor);
        bubbleDiv.textContent = mensaje;

        messageDiv.appendChild(bubbleDiv);
        chatContainer.appendChild(messageDiv);

        chatContainer.scrollTop = chatContainer.scrollHeight;
    }

 

    function seleccionarPaciente(paciente, medicoId) {
        console.log(`Seleccionado paciente con ID: ${paciente.id}`);
        tituloChat.textContent = `Chat con ${paciente.nombres} ${paciente.apellidos}`;
    
        // Hacer fetch para obtener todas las citas del médico
        fetch(`/api/medico/citas/${medicoId}`)
            .then(response => response.json())
            .then(citas => {
                // Buscar la cita específica del paciente seleccionado
                const cita = citas.find(c => c.paciente.id === paciente.id);
    
                if (cita) {
                    citaIdSeleccionada = cita.id;  // Guardar el ID de la cita seleccionada
                    cargarMensajes();  // Cargar los mensajes de la cita seleccionada
                } else {
                    alert('No se encontraron citas con este paciente.');
                }
            })
            .catch(error => console.error('Error al seleccionar al paciente:', error));
    }


 

    function redirigirAlChat(medicoId) {
        if (!medicoId) {
            console.error('ID del médico no encontrado.');
            return;
        }
        sessionStorage.setItem('medicoId', medicoId);
        window.location.href = `/ChatConPaciente.html?medicoId=${medicoId}`;
    }
    
    
});
