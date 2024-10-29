package clinica_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import clinica_backend.models.Chat;

/**
 * Repositorio para gestionar las operaciones de persistencia de la entidad {@link Chat}.
 * Extiende {@link JpaRepository} para proporcionar operaciones CRUD básicas.
 */
public interface ChatRepository extends JpaRepository<Chat, Long> {

    /**
     * Encuentra los mensajes de chat asociados a una cita específica.
     *
     * @param citaId El ID de la cita para la cual se desean obtener los mensajes.
     * @return Una lista de mensajes de chat asociados a la cita proporcionada.
     */
    List<Chat> findByCita_Id(Long citaId);
}
