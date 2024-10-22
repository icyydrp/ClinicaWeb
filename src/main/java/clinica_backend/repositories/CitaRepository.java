package clinica_backend.repositories;

import clinica_backend.models.Cita;
import clinica_backend.models.Medico;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;


@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    // Obtener citas por paciente usando la relación con paciente_id
List<Cita> findByPaciente_Id(Integer pacienteId);

    // Obtener citas por paciente en un rango de fechas
    List<Cita> findByPaciente_IdAndFechaBetween(
            Integer pacienteId, Date fechaInicio, Date fechaFin);

    // Obtener citas por médico y estado
    List<Cita> findByMedico_IdAndEstado(Integer medicoId, String estado);

    // Obtener citas por médico en un rango de fechas
    List<Cita> findByMedico_IdAndFechaBetween(Integer medicoId, Date fechaInicio, Date fechaFin);

    @Query("SELECT c FROM Cita c JOIN FETCH c.paciente WHERE c.medico.id = :medicoId")
List<Cita> findByMedico_Id(@Param("medicoId") Integer medicoId);






@Query("SELECT c FROM Cita c JOIN FETCH c.paciente p WHERE c.medico.id = :medicoId AND c.fecha BETWEEN :fechaInicio AND :fechaFin AND c.estado = :estado")
List<Cita> findByMedico_IdAndFechaBetweenAndEstado(
        @Param("medicoId") Integer medicoId,
        @Param("fechaInicio") Date fechaInicio,
        @Param("fechaFin") Date fechaFin,
        @Param("estado") String estado);

        @Query("SELECT DISTINCT c.medico FROM Cita c WHERE c.paciente.id = :pacienteId")
List<Medico> findDistinctMedicosByPacienteId(@Param("pacienteId") Integer pacienteId);

@Query("SELECT c FROM Cita c WHERE c.medico.id = :medicoId AND c.paciente.id = :pacienteId")
List<Cita> findByMedicoAndPaciente(@Param("medicoId") Integer medicoId, @Param("pacienteId") Integer pacienteId);


    
}
