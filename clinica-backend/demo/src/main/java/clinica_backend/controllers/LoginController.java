package clinica_backend.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * Método para manejar la solicitud de inicio de sesión.
     *
     * @param correo El correo electrónico ingresado por el usuario.
     * @param contraseña La contraseña ingresada por el usuario.
     * @param session La sesión HTTP donde se almacenan los datos del usuario autenticado.
     * @param redirectAttributes Atributos de redirección para enviar mensajes entre solicitudes.
     * @return La redirección a la página principal correspondiente si las credenciales son válidas,
     *         de lo contrario, redirige a la página de login con un mensaje de error.
     */
    @PostMapping("/login")
    public String iniciarSesion(
            @RequestParam String correo,
            @RequestParam String contraseña,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        System.out.println("Correo recibido: " + correo);
        System.out.println("Contraseña recibida: " + contraseña);

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
            session.setAttribute("medico", medico);
            session.setAttribute("medicoId", medico.getId());
            session.setMaxInactiveInterval(30 * 60); // 30 minutos

            System.out.println("Médico autenticado con ID: " + medico.getId());
            return "redirect:/paginaprincipalMedico.html";
        }

        // Credenciales inválidas
        System.out.println("Credenciales inválidas");
        redirectAttributes.addFlashAttribute("error", "Credenciales inválidas");
        return "redirect:/login.html?error=invalid";
    }
}
