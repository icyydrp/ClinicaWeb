document.addEventListener("DOMContentLoaded", function () {
    fetchReviews();
});

function fetchReviews() {
    fetch("http://localhost:8080/api/reviews")
        .then((response) => response.json())
        .then((reviews) => {
            const reviewsList = document.getElementById("reviews-list");
            reviewsList.innerHTML = ""; // Limpiar contenido previo
            reviews.forEach((review) => {
                const avatarInitial = review.nombrePaciente.charAt(0).toUpperCase();
                const stars = '★'.repeat(review.calificacion) + '☆'.repeat(5 - review.calificacion);

                const reviewCard = document.createElement("div");
                reviewCard.classList.add("review-card");
                reviewCard.innerHTML = `
                    <div class="review-avatar">${avatarInitial}</div>
                    <div class="review-content">
                        <div class="review-card-header">
                            <h3>${review.nombrePaciente}</h3>
                            <small>${review.calificacion} estrellas</small>
                        </div>
                        <div class="review-stars">${stars}</div>
                        <p>${review.comentario}</p>
                        <div class="review-actions">
                            <button class="edit" onclick="openEditModal(${review.id}, '${review.nombrePaciente}', ${review.calificacion}, '${review.comentario}')">Editar</button>
                        </div>
                    </div>
                `;
                reviewsList.appendChild(reviewCard);
            });
        })
        .catch((error) => console.error("Error al cargar reseñas:", error));
}

// Función para agregar una nueva reseña
document.getElementById("review-form").addEventListener("submit", function (event) {
    event.preventDefault();
    const reviewData = {
        nombrePaciente: document.getElementById("review-name").value.trim(),
        calificacion: parseInt(document.getElementById("review-rating").value),
        comentario: document.getElementById("review-text").value.trim(),
    };

    fetch("http://localhost:8080/api/reviews/add", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(reviewData),
    })
        .then((response) => {
            if (!response.ok) throw new Error("Error al agregar la reseña.");
            return response.json();
        })
        .then(() => {
            alert("Reseña agregada con éxito.");
            fetchReviews(); // Recargar las reseñas
            document.getElementById("review-form").reset(); // Limpiar formulario
        })
        .catch((error) => console.error("Error al enviar la reseña:", error));
});

// Función para abrir el modal de edición
function openEditModal(id, name, rating, comment) {
    document.getElementById("edit-review-id").value = id;
    document.getElementById("edit-review-name").value = name;
    document.getElementById("edit-review-rating").value = rating;
    document.getElementById("edit-review-comment").value = comment;
    document.getElementById("editModal").style.display = "flex";
}

// Función para cerrar el modal
function closeModal() {
    document.getElementById("editModal").style.display = "none";
}

// Función para guardar cambios en una reseña
document.getElementById("edit-review-form").addEventListener("submit", function (event) {
    event.preventDefault(); // Evitar envío del formulario

    const id = document.getElementById("edit-review-id").value;
    const updatedReview = {
        nombrePaciente: document.getElementById("edit-review-name").value.trim(),
        calificacion: parseInt(document.getElementById("edit-review-rating").value),
        comentario: document.getElementById("edit-review-comment").value.trim(),
    };

    fetch(`http://localhost:8080/api/reviews/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(updatedReview),
    })
        .then((response) => {
            if (!response.ok) throw new Error("Error al actualizar la reseña.");
            alert("Reseña actualizada correctamente.");
            fetchReviews(); // Recargar las reseñas
            closeModal(); // Cerrar el modal
        })
        .catch((error) => console.error("Error al actualizar la reseña:", error));
});

// Función para confirmar y eliminar una reseña
function confirmDelete() {
    const id = document.getElementById("edit-review-id").value;
    if (confirm("¿Estás seguro de que deseas eliminar esta reseña?")) {
        fetch(`http://localhost:8080/api/reviews/${id}`, { method: "DELETE" })
            .then((response) => {
                if (!response.ok) throw new Error("Error al eliminar la reseña.");
                alert("Reseña eliminada correctamente.");
                fetchReviews(); // Recargar las reseñas
                closeModal(); // Cerrar el modal
            })
            .catch((error) => console.error("Error al eliminar la reseña:", error));
    }
}
