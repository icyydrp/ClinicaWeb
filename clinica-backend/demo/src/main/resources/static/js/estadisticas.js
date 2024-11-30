document.addEventListener('DOMContentLoaded', async () => {
    try {
        // Llamar al backend para obtener los datos de estadísticas
        const response = await fetch('/estadisticas/citas');
        if (!response.ok) {
            throw new Error('Error al cargar estadísticas');
        }

        const data = await response.json();
        console.log('Datos recibidos:', data); // Para depuración

        // Generar los gráficos con los datos obtenidos
        cargarGraficoCitasPorMes(data.meses);
        cargarGraficoEstados(data.estados);
        cargarGraficoEspecialidades(data.especialidades);
    } catch (error) {
        console.error('Error al cargar las estadísticas:', error);
        document.body.innerHTML = '<p class="text-danger text-center">Error al cargar las estadísticas.</p>';
    }
});

// Gráfico de Citas por Mes
function cargarGraficoCitasPorMes(meses) {
    const ctx = document.getElementById('citasPorMesChart').getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: Object.keys(meses),
            datasets: [{
                label: 'Citas por Mes',
                data: Object.values(meses),
                backgroundColor: 'rgba(54, 162, 235, 0.6)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: true },
                tooltip: { enabled: true }
            },
            scales: {
                y: { beginAtZero: true }
            }
        }
    });
}

// Gráfico de Estados de las Citas
function cargarGraficoEstados(estados) {
    const ctx = document.getElementById('estadosCitasChart').getContext('2d');
    new Chart(ctx, {
        type: 'pie',
        data: {
            labels: Object.keys(estados),
            datasets: [{
                data: Object.values(estados),
                backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0'],
                hoverBackgroundColor: ['#FF4384', '#36C2EB', '#FFDE56', '#4BD0C0']
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: true },
                tooltip: { enabled: true }
            }
        }
    });
}

// Gráfico de Especialidades Más Demandadas
function cargarGraficoEspecialidades(especialidades) {
    const ctx = document.getElementById('especialidadesChart').getContext('2d');
    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: Object.keys(especialidades),
            datasets: [{
                data: Object.values(especialidades),
                backgroundColor: ['#FF9F40', '#FF6384', '#36A2EB', '#4BC0C0', '#9966FF'],
                hoverBackgroundColor: ['#FFAF40', '#FF7384', '#46B2EB', '#5BD0C0', '#A076FF']
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: true },
                tooltip: { enabled: true }
            }
        }
    });
}
