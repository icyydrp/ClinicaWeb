document.getElementById("downloadBackupBtn").addEventListener("click", () => {
    window.location.href = "/configuracion/descargar-backup";
});

document.getElementById("restoreBackupForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const formData = new FormData();
    const fileInput = document.getElementById("backupFile");

    if (fileInput.files.length === 0) {
        alert("Por favor, selecciona un archivo.");
        return;
    }

    formData.append("backupFile", fileInput.files[0]);

    try {
        const response = await fetch("/configuracion/restaurar-backup", {
            method: "POST",
            body: formData
        });

        if (response.ok) {
            alert("Backup restaurado con éxito");
            document.getElementById("restoreBackupModal").classList.remove("show");
            document.body.classList.remove("modal-open");
            document.querySelector(".modal-backdrop").remove();
        } else {
            alert("Error al restaurar el backup");
        }
    } catch (error) {
        console.error("Error al restaurar backup:", error);
        alert("Error al restaurar el backup");
    }
});
document.getElementById('cerrarSesionBtn').addEventListener('click', () => {
    if (confirm('¿Estás seguro de que deseas cerrar sesión?')) {
        fetch('/api/sesion/cerrar', { method: 'POST' })
            .then(response => {
                if (response.ok) {
                    alert('Sesión cerrada correctamente.');
                    // Redirigir al usuario a la página de inicio de sesión
                    window.location.href = 'login.html';
                } else {
                    alert('Hubo un problema al cerrar la sesión. Intenta nuevamente.');
                }
            })
            .catch(error => {
                console.error('Error al cerrar sesión:', error);
                alert('Error al cerrar sesión. Por favor, intenta nuevamente.');
            });
    }
});
