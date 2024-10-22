document.addEventListener("DOMContentLoaded", () => {
    const enviarBtn = document.getElementById('enviarMensajeBtn');
    const mensajeInput = document.getElementById('mensajeInput');
    const chatContainer = document.getElementById('chatContainer');
    const listaMedicos = document.getElementById('listaMedicos');
    const tituloChat = document.getElementById('tituloChat');

    let citaIdSeleccionada = null;

    cargarMedicos();

    enviarBtn.addEventListener('click', enviarMensaje);
    mensajeInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') enviarMensaje();
    });

    function cargarMedicos() {
        const pacienteId = 1; // Cambia este ID según el paciente logueado
        fetch(`/api/medicos/paciente/${pacienteId}`)
            .then(response => response.json())
            .then(medicos => {
                listaMedicos.innerHTML = ''; // Limpiar lista de médicos
                medicos.forEach(medico => {
                    const li = document.createElement('li');
                    li.classList.add('list-group-item');
                    li.textContent = `${medico.nombres} ${medico.apellidos}`;
                    li.addEventListener('click', () => seleccionarMedico(medico, pacienteId));
                    listaMedicos.appendChild(li);
                });
            })
            .catch(error => console.error('Error al cargar los médicos:', error));
    }

    function seleccionarMedico(medico, pacienteId) {
        console.log(`Seleccionado médico con ID: ${medico.id}`);
        tituloChat.textContent = `Chat con ${medico.nombres} ${medico.apellidos}`;

        // Obtener las citas del médico y paciente seleccionados
        fetch(`/api/citas/medico/${medico.id}/paciente/${pacienteId}`)
            .then(response => response.json())
            .then(citas => {
                if (citas.length > 0) {
                    citaIdSeleccionada = citas[0].id; // Usar la primera cita encontrada
                    cargarMensajes();
                } else {
                    alert('No se encontraron citas con este médico.');
                }
            })
            .catch(error => console.error('Error al seleccionar al médico:', error));
    }

    function enviarMensaje() {
        const mensaje = mensajeInput.value.trim();
        if (!mensaje || !citaIdSeleccionada) {
            alert('Selecciona un médico y escribe un mensaje.');
            return;
        }

        const mensajeData = {
            remitente: 'paciente',
            mensaje: mensaje
        };

        fetch(`/api/chat/${citaIdSeleccionada}/enviar`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(mensajeData)
        })
            .then(response => {
                if (!response.ok) {
                    console.error('Error al enviar mensaje:', response);
                    alert('Error al enviar el mensaje. Revisa la consola.');
                    return;
                }
                agregarMensaje('paciente', mensaje);
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
});
