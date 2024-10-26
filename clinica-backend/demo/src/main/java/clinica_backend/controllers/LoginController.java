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

@Controller
public class LoginController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

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
            session.setMaxInactiveInterval(30 * 60);  // 30 minutos

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
            session.setMaxInactiveInterval(30 * 60);  // 30 minutos

            System.out.println("Médico autenticado con ID: " + medico.getId());
            return "redirect:/paginaprincipalMedico.html";
        }

        // Credenciales inválidas
        System.out.println("Credenciales inválidas");
        redirectAttributes.addFlashAttribute("error", "Credenciales inválidas");
        return "redirect:/login.html?error=invalid";
    }
}
