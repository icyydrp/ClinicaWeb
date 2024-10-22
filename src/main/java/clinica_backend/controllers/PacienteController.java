package clinica_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import clinica_backend.models.Paciente;
import clinica_backend.repositories.PacienteRepository;

@Controller // Cambiado a @Controller para permitir redirecciones
@RequestMapping("/paciente")
public class PacienteController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @PostMapping(consumes = "application/x-www-form-urlencoded")
    public String registrarPaciente(@RequestParam("nombres") String nombres,
                                    @RequestParam("apellidos") String apellidos,
                                    @RequestParam("correo") String correo,
                                    @RequestParam("contraseña") String contraseña,
                                    @RequestParam("celular") String celular,
                                    @RequestParam("dni") String dni) {
        try {
            Paciente paciente = new Paciente();
            paciente.setNombres(nombres);
            paciente.setApellidos(apellidos);
            paciente.setCorreo(correo);
            paciente.setContraseña(contraseña);
            paciente.setCelular(celular);
            paciente.setDni(dni);
    
            pacienteRepository.save(paciente);
            return "redirect:/paginaprincipalPaciente.html"; // Redirigir tras el registro exitoso
        } catch (Exception e) {
            return "redirect:/registroPaciente.html?error=true"; // Redirigir en caso de error
        }
    }
    
}
