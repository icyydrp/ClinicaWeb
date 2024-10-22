package clinica_backend.repositories;

import clinica_backend.models.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Integer> {
    Optional<Medico> findByCorreoAndContraseña(String correo, String contraseña);

    // Método para obtener médicos por especialidad
    List<Medico> findByEspecialidad(String especialidad);
}
