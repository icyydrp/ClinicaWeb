package clinica_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import clinica_backend.models.Chat;
import clinica_backend.repositories.ChatRepository;


public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByCita_Id(Long citaId);
}
