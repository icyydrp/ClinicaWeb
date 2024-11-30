package clinica_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import clinica_backend.models.Medico;

/**
 * Repositorio para gestionar las operaciones de persistencia de la entidad {@link Medico}.
 * Provee métodos específicos para autenticación y búsqueda por especialidad.
 */
@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    /**
     * Busca un médico por su correo y contraseña.
     * Este método es útil para la autenticación.
     *
     * @param correo Correo del médico.
     * @param contraseña Contraseña del médico.
     * @return Un {@link Optional} que contiene el médico si se encuentra, o vacío si no.
     */
    Optional<Medico> findByCorreoAndContraseña(String correo, String contraseña);

    /**
     * Obtiene todos los médicos que tienen una especialidad específica.
     *
     * @param especialidad Especialidad médica.
     * @return Lista de médicos que coinciden con la especialidad proporcionada.
     */
    List<Medico> findByEspecialidad(String especialidad);

    /**
     * Busca un médico por su correo.
     *
     * @param correo Correo del médico.
     * @return Un {@link Optional} que contiene el médico si se encuentra, o vacío si no.
     */
    Optional<Medico> findByCorreo(String correo);

    /**
     * Busca un médico por su número de colegiatura.
     *
     * @param numeroColegiatura Número de colegiatura del médico.
     * @return Un {@link Optional} que contiene el médico si se encuentra, o vacío si no.
     */
    Optional<Medico> findByNumeroColegiatura(String numeroColegiatura);

    /**
     * Busca un médico por su DNI.
     *
     * @param dni DNI del médico.
     * @return Un {@link Optional} que contiene el médico si se encuentra, o vacío si no.
     */
    Optional<Medico> findByDni(String dni);

    
      /**
     * Busca médicos que coincidan parcialmente con los nombres, correo o DNI.
     *
     * @param nombres Nombre parcial o completo del médico.
     * @param correo Correo parcial o completo del médico.
     * @param dni DNI parcial o completo del médico.
     * @return Lista de médicos que coincidan con los criterios de búsqueda.
     */
    List<Medico> findByNombresContainingIgnoreCaseOrCorreoContainingIgnoreCaseOrDniContainingIgnoreCase(
            String nombres, String correo, String dni);
    
}
