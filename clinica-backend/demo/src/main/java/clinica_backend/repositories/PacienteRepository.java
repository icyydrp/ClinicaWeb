package clinica_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import clinica_backend.models.Paciente;

/**
 * Repositorio para gestionar las operaciones de persistencia de la entidad {@link Paciente}.
 * Provee métodos específicos para la autenticación del paciente.
 */
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    /**
     * Buscar pacientes por correo y contraseña.
     * 
     * @param correo Correo del paciente.
     * @param contraseña Contraseña del paciente.
     * @return Paciente encontrado o vacío si no existe.
     */
    Optional<Paciente> findByCorreoAndContraseña(String correo, String contraseña);

    /**
     * Buscar pacientes por nombres, correo o DNI.
     * 
     * @param nombres Nombre parcial del paciente.
     * @param correo Correo parcial del paciente.
     * @param dni DNI parcial del paciente.
     * @return Lista de pacientes coincidentes.
     */
    List<Paciente> findByNombresContainingIgnoreCaseOrCorreoContainingIgnoreCaseOrDniContaining(String nombres, String correo, String dni);

    /**
     * Buscar un paciente por correo.
     * 
     * @param correo Correo del paciente.
     * @return Paciente encontrado o vacío si no existe.
     */
    Optional<Paciente> findByCorreo(String correo);

    /**
     * Buscar un paciente por DNI.
     * 
     * @param dni DNI del paciente.
     * @return Paciente encontrado o vacío si no existe.
     */
    Optional<Paciente> findByDni(String dni);
}
