package clinica_backend.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import clinica_backend.models.Medico;
import clinica_backend.models.Paciente;
import clinica_backend.repositories.MedicoRepository;
import clinica_backend.repositories.PacienteRepository;
import jakarta.servlet.http.HttpSession;

/**
 * Controlador encargado de gestionar el proceso de inicio de sesión de pacientes y médicos.
 */
@Controller
public class LoginController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    // Credenciales predefinidas del administrador
    private static final String ADMIN_CORREO = "administradorclinica@hotmail.com";
    private static final String ADMIN_CONTRASEÑA = "Administrador123@";

    @PostMapping("/login")
    public String iniciarSesion(
            @RequestParam String correo,
            @RequestParam String contraseña,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        System.out.println("Correo recibido: " + correo);
        System.out.println("Contraseña recibida: " + contraseña);

        // Validar inicio de sesión como Administrador
        if (ADMIN_CORREO.equals(correo.trim()) && ADMIN_CONTRASEÑA.equals(contraseña.trim())) {
            session.setAttribute("administrador", true);
            session.setMaxInactiveInterval(30 * 60); // 30 minutos
            System.out.println("Administrador autenticado");
            return "redirect:/dashboardAdministrador.html";
        }

        // Autenticación del Paciente
        Optional<Paciente> pacienteOpt = pacienteRepository
                .findByCorreoAndContraseña(correo.trim(), contraseña.trim());

        if (pacienteOpt.isPresent()) {
            Paciente paciente = pacienteOpt.get();
            session.setAttribute("paciente", paciente);
            session.setAttribute("pacienteId", paciente.getId());
            session.setMaxInactiveInterval(30 * 60); // 30 minutos

            System.out.println("Paciente autenticado con ID: " + paciente.getId());
            return "redirect:/paginaprincipalPaciente.html";
        }

        // Autenticación del Médico
        Optional<Medico> medicoOpt = medicoRepository
                .findByCorreoAndContraseña(correo.trim(), contraseña.trim());

                if (medicoOpt.isPresent()) {
                    Medico medico = medicoOpt.get();
                    session.setAttribute("medico", medico); // Objeto completo
                    session.setAttribute("medicoId", medico.getId()); // Solo ID
                    session.setMaxInactiveInterval(30 * 60); // Sesión de 30 minutos
                    return "redirect:/paginaprincipalMedico.html";
                }
                
                

        // Credenciales inválidas
        System.out.println("Credenciales inválidas");
        redirectAttributes.addFlashAttribute("error", "Credenciales inválidas");
        return "redirect:/login.html?error=invalid";
    }

    @PostMapping("/api/sesion/cerrar")
    public ResponseEntity<Map<String, Object>> cerrarSesion(HttpSession session) {
        try {
            // Invalida la sesión del usuario
            session.invalidate();
            return ResponseEntity.ok(Map.of("success", true, "message", "Sesión cerrada correctamente."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error al cerrar sesión."));
        }
    }
    

}
