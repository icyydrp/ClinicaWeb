package clinica_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import clinica_backend.models.Receta;

/**
 * Repositorio para gestionar las operaciones de persistencia de la entidad {@link Receta}.
 * Provee métodos personalizados para la búsqueda de recetas asociadas a un paciente.
 */
@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {

    /**
     * Busca todas las recetas asociadas a un paciente en función de su ID.
     *
     * @param pacienteId ID del paciente cuyas recetas se desean obtener.
     * @return Lista de recetas asociadas al paciente especificado.
     */
    @Query("SELECT r FROM Receta r WHERE r.cita.paciente.id = :pacienteId")
    List<Receta> findByCita_Paciente_Id(@Param("pacienteId") Long pacienteId);

    @Query("SELECT r FROM Receta r WHERE r.cita.medico.id = :medicoId")
List<Receta> findByCita_Medico_Id(@Param("medicoId") Long medicoId);

}
