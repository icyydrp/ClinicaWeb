package clinica_backend.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import clinica_backend.models.Medico;
import clinica_backend.models.Paciente;
import clinica_backend.repositories.MedicoRepository;
import clinica_backend.repositories.PacienteRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Primero, buscar en el repositorio de médicos
        Optional<Medico> medicoOpt = medicoRepository.findByCorreo(username);
        if (medicoOpt.isPresent()) {
            Medico medico = medicoOpt.get();
            return User.builder()
                    .username(medico.getCorreo())
                    .password(medico.getContraseña())
                    .roles("MEDICO") // Configura roles aquí. Verifica que coincidan con SecurityConfig
                    .build();
        }

        // Luego, buscar en el repositorio de pacientes si no es un médico
        Optional<Paciente> pacienteOpt = pacienteRepository.findByCorreo(username);
        if (pacienteOpt.isPresent()) {
            Paciente paciente = pacienteOpt.get();
            return User.builder()
                    .username(paciente.getCorreo())
                    .password(paciente.getContraseña())
                    .roles("PACIENTE") // Configura roles aquí. Verifica que coincidan con SecurityConfig
                    .build();
        }

        // Si no se encuentra ni como paciente ni como médico, lanzar excepción
        throw new UsernameNotFoundException("Usuario no encontrado con el correo: " + username);
    }
}