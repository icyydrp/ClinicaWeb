// Obtener referencias a elementos del chatbot
const chatbotContainer = document.getElementById('chatbot-container');
const chatbotToggleBtn = document.getElementById('chatbot-toggle-btn');
const chatbotCloseBtn = document.getElementById('chatbot-close-btn');
const chatbotMessages = document.getElementById('chatbot-messages');
const chatbotInput = document.getElementById('chatbot-input');
const chatbotSendBtn = document.getElementById('chatbot-send-btn');

// Abrir el chatbot
chatbotToggleBtn.addEventListener('click', () => {
    chatbotContainer.style.display = 'flex';
    chatbotToggleBtn.classList.add('hidden');
});

// Cerrar el chatbot
chatbotCloseBtn.addEventListener('click', () => {
    chatbotContainer.style.display = 'none';
    chatbotToggleBtn.classList.remove('hidden');
});

// Enviar mensaje del usuario con el botón
chatbotSendBtn.addEventListener('click', enviarMensaje);

// Enviar mensaje del usuario con 'Enter'
chatbotInput.addEventListener('keypress', (event) => {
    if (event.key === 'Enter') {
        enviarMensaje();
    }
});

// Función para enviar el mensaje
function enviarMensaje() {
    const userMessage = chatbotInput.value.trim();
    if (userMessage) {
        addMessage('Usuario', userMessage, 'user-message');
        chatbotInput.value = '';
        generateResponse(userMessage);
    }
}

// Agregar mensaje al chat
function addMessage(sender, message, className) {
    const messageElement = document.createElement('div');
    messageElement.classList.add(className);
    messageElement.innerHTML = `<strong>${sender}:</strong> ${message}`;
    chatbotMessages.appendChild(messageElement);

    // Desplazar el scroll hacia abajo
    chatbotMessages.scrollTop = chatbotMessages.scrollHeight;
}

// Generar respuesta del chatbot
function generateResponse(message) {
    let response = '';

    if (message.includes('cita')) {
        response = '¿Te gustaría reservar una cita? Puedes hacerlo en la sección de reservas o llamando al 987654321.';
    } else if (message.includes('horario')) {
        response = 'Nuestro horario es de 8:00 AM a 6:00 PM de lunes a viernes. ¿Hay algo más en lo que pueda ayudarte?';
    } else if (message.includes('servicio') || message.includes('especialidad')) {
        response = 'Ofrecemos servicios de cardiología, pediatría, rayos X, y más. ¿Quieres saber más detalles sobre alguno de ellos?';
    } else if (message.includes('emergencia')) {
        response = 'En caso de emergencia, llama al 911 o acude directamente a la clínica.';
    } else if (message.includes('contacto')) {
        response = 'Puedes contactarnos llamando al 987654321 o enviando un correo a contacto@clinica.com.';
    } else if (message.includes('farmacia')) {
        response = 'Nuestra farmacia está abierta durante los horarios de atención. ¿Quieres consultar la disponibilidad de algún medicamento?';
    } else if (message.includes('seguros')) {
        response = 'Aceptamos seguros de Rimac, Pacífico y Mapfre. ¿Tienes alguna otra pregunta?';
    } else {
        response = 'No entendí tu mensaje. ¿Podrías reformularlo o preguntarme algo más específico?';
    }

    // Simulación de respuesta después de 500ms
    setTimeout(() => addMessage('Chatbot', response, 'bot-message'), 500);
}

// Modificación para incluir el token JWT en todas las solicitudes fetch
(function () {
    const originalFetch = fetch;
    window.fetch = function () {
        const args = arguments;

        // Obtener el token del localStorage
        const token = localStorage.getItem('authToken');
        if (token) {
            if (!args[1]) {
                args[1] = {};
            }
            if (!args[1].headers) {
                args[1].headers = {};
            }
            args[1].headers['Authorization'] = 'Bearer ' + token;
        }

        return originalFetch.apply(this, arguments);
    };
})();

// Verificar la validez del token al cargar la página principal del paciente
document.addEventListener('DOMContentLoaded', function () {
    const token = localStorage.getItem('authToken');
    if (!token) {
        // Redirigir al login si no hay un token
        alert('No tienes autorización. Por favor, inicia sesión.');
        window.location.href = '/login.html';
    } else {
        console.log('Token presente, acceso permitido a la página principal del paciente.');
    }
});
