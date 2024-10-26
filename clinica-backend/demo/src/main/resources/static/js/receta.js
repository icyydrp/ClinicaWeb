document.addEventListener('DOMContentLoaded', () => {
    try {
        const urlParams = new URLSearchParams(window.location.search);
        const citaId = urlParams.get('citaId');
        const pacienteId = urlParams.get('pacienteId');
        const medicoId = urlParams.get('medicoId');

        console.log('Parámetros recibidos:', { citaId, pacienteId, medicoId });

        if (citaId && pacienteId && medicoId) {
            document.getElementById('citaId').value = citaId;
            document.getElementById('medicoId').value = medicoId;
        } else {
            mostrarError('No se encontraron los parámetros necesarios.');
        }

        const recetaForm = document.getElementById('recetaForm');
        if (recetaForm) {
            recetaForm.addEventListener('submit', enviarReceta);
        } else {
            console.error('Formulario de receta no encontrado.');
        }
    } catch (error) {
        console.error('Error al procesar los parámetros de la URL:', error);
        mostrarError('Error al procesar la solicitud. Inténtalo de nuevo.');
    }
});

// Función para enviar la receta al backend
async function enviarReceta(event) {
    event.preventDefault();

    const citaId = document.getElementById('citaId').value;
    const medicoId = document.getElementById('medicoId').value;
    const medicamentos = document.getElementById('medicamentos').value;
    const dosis = document.getElementById('dosis').value;
    const frecuencia = document.getElementById('frecuencia').value;
    const instrucciones = document.getElementById('instrucciones').value;
    const notaAdicional = document.getElementById('notasadicional').value;

    const receta = {
        cita: { id: parseInt(citaId) }, // Asegurarse de que sea un número
        medicamentos,
        dosis,
        frecuencia,
        instrucciones,
        notaAdicional,
        fechaEmision: new Date().toISOString().split('T')[0], // "YYYY-MM-DD"
    };

    console.log('Receta enviada:', receta);

    try {
        const response = await fetch(`/api/medico/${medicoId}/recetas/enviar`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(receta),
        });

        if (!response.ok) {
            const errorData = await response.json();
            mostrarError(`Error: ${errorData.error}`);
            return;
        }

        alert('Receta enviada con éxito.');
        window.location.href = '/paginaprincipalMedico.html';
    } catch (error) {
        console.error('Error al enviar la receta:', error);
        mostrarError('No se pudo enviar la receta. Inténtalo más tarde.');
    }
}

// Mostrar mensaje de error en la interfaz
function mostrarError(mensaje) {
    const container = document.querySelector('.container');
    container.innerHTML = `
        <div class="alert alert-danger mt-4" role="alert">
            ${mensaje}
        </div>`;
}

function volverPaginaAnterior() {
    window.history.back();
}