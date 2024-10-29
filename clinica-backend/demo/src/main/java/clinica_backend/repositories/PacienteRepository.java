package clinica_backend.repositories;

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
     * Busca un paciente por su correo y contraseña.
     * Este método se utiliza para autenticar pacientes en el sistema.
     *
     * @param correo Correo del paciente.
     * @param contraseña Contraseña del paciente.
     * @return Un {@link Optional} que contiene el paciente si se encuentra, o vacío si no.
     */
    Optional<Paciente> findByCorreoAndContraseña(String correo, String contraseña);
}
