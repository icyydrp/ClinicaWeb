document.addEventListener("DOMContentLoaded", async () => {
    const enviarBtn = document.getElementById('enviarMensajeBtn');
    const mensajeInput = document.getElementById('mensajeInput');
    const chatContainer = document.getElementById('chatContainer');
    const listaMedicos = document.getElementById('listaMedicos');
    const tituloChat = document.getElementById('tituloChat');

    let citaIdSeleccionada = null;

    try {
        const pacienteId = await verificarSesionPaciente();
        console.log("Sesión válida para el paciente:", pacienteId);
        cargarMedicos(pacienteId);
    } catch (error) {
        console.error("Error al verificar la sesión:", error);
        alert("Sesión expirada o no válida. Redirigiendo al login...");
        window.location.href = "/login.html";
    }

    enviarBtn.addEventListener('click', enviarMensaje);
    mensajeInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') enviarMensaje();
    });

    async function verificarSesionPaciente() {
        const response = await fetch('/api/sesion/paciente'); // Nueva ruta corregida
        if (!response.ok) {
            alert("ID del paciente no encontrado. Por favor, inicia sesión nuevamente.");
            window.location.href = "/login.html";
            throw new Error("Sesión no válida.");
        }
        const data = await response.json();
        console.log("Datos del paciente en sesión:", data);
        return data.pacienteId;
    }
    
    

    function cargarMedicos(pacienteId) {
        fetch(`/api/medicos/paciente/${pacienteId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error("No se encontraron médicos para este paciente.");
                }
                return response.json();
            })
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
            .catch(error => console.error("Error al cargar los médicos:", error));
    }

    function seleccionarMedico(medico, pacienteId) {
        console.log(`Seleccionado médico con ID: ${medico.id}`);
        tituloChat.textContent = `Chat con ${medico.nombres} ${medico.apellidos}`;

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
            .catch(error => console.error("Error al seleccionar al médico:", error));
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
                    throw new Error('Error al enviar el mensaje.');
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
