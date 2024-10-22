package clinica_backend.controllers;

import clinica_backend.models.Medico;
import clinica_backend.models.Paciente;
import clinica_backend.repositories.MedicoRepository;
import clinica_backend.repositories.PacienteRepository;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@SessionAttributes({"paciente", "medico"}) // Guardar los objetos en la sesión
public class LoginController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @PostMapping("/login")
    public String iniciarSesion(@RequestParam String correo, 
                                @RequestParam String contraseña, 
                                HttpSession session, 
                                RedirectAttributes redirectAttributes) {
        Optional<Paciente> paciente = pacienteRepository
                .findByCorreoAndContraseña(correo.trim(), contraseña.trim());
        if (paciente.isPresent()) {
            session.setAttribute("paciente", paciente.get());
            session.setMaxInactiveInterval(30 * 60); // Sesión válida por 30 minutos
            return "redirect:/paginaprincipalPaciente.html";
        }
    
        Optional<Medico> medico = medicoRepository
                .findByCorreoAndContraseña(correo.trim(), contraseña.trim());
        if (medico.isPresent()) {
            session.setAttribute("medico", medico.get());
            session.setMaxInactiveInterval(30 * 60);
            return "redirect:/paginaprincipalMedico.html";
        }
    
        redirectAttributes.addAttribute("error", "invalid");
        return "redirect:/login.html";
    }
    

    // Manejar cierre de sesión
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate(); // Invalidar la sesión actual
        return "redirect:/paginainicio.html"; // Redirigir a la página de inicio
    }
}
