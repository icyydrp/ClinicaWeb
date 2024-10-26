package clinica_backend.repositories;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import clinica_backend.models.Cita;
import clinica_backend.models.Medico;
import clinica_backend.models.Receta;


@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    // Obtener citas por paciente usando la relación con paciente_id
List<Cita> findByPaciente_Id(Long pacienteId);
List<Cita> findByEstado(String estado);


    // Obtener citas por paciente en un rango de fechas
    List<Cita> findByPaciente_IdAndFechaBetween(
            Long pacienteId, Date fechaInicio, Date fechaFin);

    // Obtener citas por médico y estado
    List<Cita> findByMedico_IdAndEstado(Long medicoId, String estado);

    // Obtener citas por médico en un rango de fechas
    List<Cita> findByMedico_IdAndFechaBetween(Long medicoId, Date fechaInicio, Date fechaFin);

    @Query("SELECT c FROM Cita c JOIN FETCH c.paciente WHERE c.medico.id = :medicoId")
List<Cita> findByMedico_Id(@Param("medicoId") Long medicoId);

@Query("SELECT DISTINCT c.medico FROM Cita c WHERE c.paciente.id = :pacienteId")
List<Medico> findMedicosByPacienteId(@Param("pacienteId") Long pacienteId);

 // Obtener citas por médico y fecha con un estado específico
List<Cita> findByMedico_IdAndFechaAndEstado(Long medicoId, Date fecha, String estado);


List<Cita> findByPaciente_IdAndEstado(Long pacienteId, String estado);


@Query("SELECT c FROM Cita c JOIN FETCH c.paciente p WHERE c.medico.id = :medicoId AND c.fecha BETWEEN :fechaInicio AND :fechaFin AND c.estado = :estado")
List<Cita> findByMedico_IdAndFechaBetweenAndEstado(
        @Param("medicoId") Long medicoId,
        @Param("fechaInicio") Date fechaInicio,
        @Param("fechaFin") Date fechaFin,
        @Param("estado") String estado);

        @Query("SELECT DISTINCT c.medico FROM Cita c WHERE c.paciente.id = :pacienteId")
List<Medico> findDistinctMedicosByPacienteId(@Param("pacienteId") Long pacienteId);

@Query("SELECT c FROM Cita c WHERE c.medico.id = :medicoId AND c.paciente.id = :pacienteId")
List<Cita> findByMedicoAndPaciente(@Param("medicoId") Long medicoId, @Param("pacienteId") Long pacienteId);

  @Query("SELECT c FROM Cita c WHERE c.paciente.id = :pacienteId")
    List<Cita> findCitasConPacientePorEstado(@Param("pacienteId") Long pacienteId);
    

    @Query("SELECT r FROM Receta r WHERE r.cita.paciente.id = :pacienteId")
    List<Receta> findByCita_Paciente_Id(@Param("pacienteId") Long pacienteId);
}
