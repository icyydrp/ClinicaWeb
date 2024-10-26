package clinica_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import clinica_backend.models.Receta;


@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {

    @Query("SELECT r FROM Receta r WHERE r.cita.paciente.id = :pacienteId")
    List<Receta> findByCita_Paciente_Id(@Param("pacienteId") Long pacienteId);
}
