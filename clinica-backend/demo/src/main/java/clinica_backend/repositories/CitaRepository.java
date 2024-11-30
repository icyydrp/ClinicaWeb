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

/**
 * Repositorio para gestionar las operaciones de persistencia de la entidad {@link Cita}.
 * Provee métodos específicos para obtener citas, médicos y recetas basados en distintos criterios.
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Obtiene todas las citas de un paciente específico.
     *
     * @param pacienteId ID del paciente.
     * @return Lista de citas asociadas al paciente.
     */
    List<Cita> findByPaciente_Id(Long pacienteId);

    /**
     * Obtiene todas las citas con un estado específico.
     *
     * @param estado Estado de la cita.
     * @return Lista de citas con el estado especificado.
     */
    List<Cita> findByEstado(String estado);

    /**
     * Obtiene las citas de un paciente dentro de un rango de fechas.
     *
     * @param pacienteId ID del paciente.
     * @param fechaInicio Fecha de inicio del rango.
     * @param fechaFin Fecha de fin del rango.
     * @return Lista de citas dentro del rango de fechas.
     */
    List<Cita> findByPaciente_IdAndFechaBetween(Long pacienteId, Date fechaInicio, Date fechaFin);

    /**
     * Obtiene las citas de un médico con un estado específico.
     *
     * @param medicoId ID del médico.
     * @param estado Estado de la cita.
     * @return Lista de citas del médico con el estado especificado.
     */
    List<Cita> findByMedico_IdAndEstado(Long medicoId, String estado);

    /**
     * Obtiene las citas de un médico dentro de un rango de fechas.
     *
     * @param medicoId ID del médico.
     * @param fechaInicio Fecha de inicio del rango.
     * @param fechaFin Fecha de fin del rango.
     * @return Lista de citas del médico dentro del rango de fechas.
     */
    List<Cita> findByMedico_IdAndFechaBetween(Long medicoId, Date fechaInicio, Date fechaFin);

    /**
     * Obtiene todas las citas de un médico utilizando una consulta personalizada.
     *
     * @param medicoId ID del médico.
     * @return Lista de citas asociadas al médico.
     */
    @Query("SELECT c FROM Cita c JOIN FETCH c.paciente WHERE c.medico.id = :medicoId")
    List<Cita> findByMedico_Id(@Param("medicoId") Long medicoId);

    /**
     * Obtiene todos los médicos distintos que han atendido a un paciente.
     *
     * @param pacienteId ID del paciente.
     * @return Lista de médicos que han atendido al paciente.
     */
    @Query("SELECT DISTINCT c.medico FROM Cita c WHERE c.paciente.id = :pacienteId")
    List<Medico> findMedicosByPacienteId(@Param("pacienteId") Long pacienteId);

    /**
     * Obtiene citas de un médico en una fecha específica con un estado dado.
     *
     * @param medicoId ID del médico.
     * @param fecha Fecha de la cita.
     * @param estado Estado de la cita.
     * @return Lista de citas en la fecha y estado especificados.
     */
    List<Cita> findByMedico_IdAndFechaAndEstado(Long medicoId, Date fecha, String estado);

    /**
     * Obtiene citas de un paciente con un estado específico.
     *
     * @param pacienteId ID del paciente.
     * @param estado Estado de la cita.
     * @return Lista de citas del paciente con el estado especificado.
     */
    List<Cita> findByPaciente_IdAndEstado(Long pacienteId, String estado);

    /**
     * Obtiene citas de un médico dentro de un rango de fechas y con un estado específico.
     *
     * @param medicoId ID del médico.
     * @param fechaInicio Fecha de inicio del rango.
     * @param fechaFin Fecha de fin del rango.
     * @param estado Estado de la cita.
     * @return Lista de citas dentro del rango de fechas y con el estado especificado.
     */
    @Query("SELECT c FROM Cita c JOIN FETCH c.paciente p WHERE c.medico.id = :medicoId AND c.fecha BETWEEN :fechaInicio AND :fechaFin AND c.estado = :estado")
    List<Cita> findByMedico_IdAndFechaBetweenAndEstado(
        @Param("medicoId") Long medicoId,
        @Param("fechaInicio") Date fechaInicio,
        @Param("fechaFin") Date fechaFin,
        @Param("estado") String estado
    );

    /**
     * Obtiene todos los médicos únicos que han atendido a un paciente.
     *
     * @param pacienteId ID del paciente.
     * @return Lista de médicos únicos.
     */
    @Query("SELECT DISTINCT c.medico FROM Cita c WHERE c.paciente.id = :pacienteId")
    List<Medico> findDistinctMedicosByPacienteId(@Param("pacienteId") Long pacienteId);

    /**
     * Obtiene todas las citas entre un médico y un paciente.
     *
     * @param medicoId ID del médico.
     * @param pacienteId ID del paciente.
     * @return Lista de citas entre el médico y el paciente.
     */
    @Query("SELECT c FROM Cita c WHERE c.medico.id = :medicoId AND c.paciente.id = :pacienteId")
    List<Cita> findByMedicoAndPaciente(@Param("medicoId") Long medicoId, @Param("pacienteId") Long pacienteId);

    /**
     * Obtiene todas las citas con un paciente dado.
     *
     * @param pacienteId ID del paciente.
     * @return Lista de citas del paciente.
     */
    @Query("SELECT c FROM Cita c WHERE c.paciente.id = :pacienteId")
    List<Cita> findCitasConPacientePorEstado(@Param("pacienteId") Long pacienteId);

    /**
     * Obtiene todas las recetas de un paciente específico.
     *
     * @param pacienteId ID del paciente.
     * @return Lista de recetas del paciente.
     */
    @Query("SELECT r FROM Receta r WHERE r.cita.paciente.id = :pacienteId")
    List<Receta> findByCita_Paciente_Id(@Param("pacienteId") Long pacienteId);

    
}
