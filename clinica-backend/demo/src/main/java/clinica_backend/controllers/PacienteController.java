package clinica_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import clinica_backend.models.Paciente;
import clinica_backend.repositories.PacienteRepository;
import jakarta.servlet.http.HttpSession;

/**
 * Controlador para gestionar las acciones relacionadas con los pacientes.
 */
@Controller
@RequestMapping("/paciente")
public class PacienteController {

    @Autowired
    private PacienteRepository pacienteRepository;

    /**
     * Registra un nuevo paciente y guarda su información en la sesión.
     *
     * @param nombres   Nombres del paciente.
     * @param apellidos Apellidos del paciente.
     * @param correo    Correo electrónico del paciente.
     * @param contraseña Contraseña del paciente.
     * @param celular   Número de celular del paciente.
     * @param dni       DNI del paciente.
     * @param session   Objeto HttpSession para gestionar la sesión del usuario.
     * @return Redirige a la página principal del paciente si el registro es exitoso,
     *         o al formulario de registro con un mensaje de error en caso de fallo.
     */
    @PostMapping(consumes = "application/x-www-form-urlencoded")
    public String registrarPaciente(@RequestParam("nombres") String nombres,
                                    @RequestParam("apellidos") String apellidos,
                                    @RequestParam("correo") String correo,
                                    @RequestParam("contraseña") String contraseña,
                                    @RequestParam("celular") String celular,
                                    @RequestParam("dni") String dni,
                                    HttpSession session) {
        try {
            // Crear una nueva instancia de Paciente y establecer sus propiedades
            Paciente paciente = new Paciente();
            paciente.setNombres(nombres);
            paciente.setApellidos(apellidos);
            paciente.setCorreo(correo);
            paciente.setContraseña(contraseña);
            paciente.setCelular(celular);
            paciente.setDni(dni);

            // Guardar el paciente en la base de datos
            pacienteRepository.save(paciente);

            // Almacenar el paciente en la sesión
            session.setAttribute("paciente", paciente);

            // Redirigir a la página principal del paciente
            return "redirect:/paginaprincipalPaciente.html";
        } catch (Exception e) {
            // Redirigir al formulario de registro con un mensaje de error
            return "redirect:/registroPaciente.html?error=true";
        }
    }

    /**
     * Obtiene los datos del paciente desde la sesión actual.
     *
     * @param session Objeto HttpSession para acceder a los datos del paciente en la sesión.
     * @return ResponseEntity con el objeto Paciente si existe en la sesión, o un estado 404 si no se encuentra.
     */
    @GetMapping("/sesion")
    public ResponseEntity<Paciente> obtenerPacienteSesion(HttpSession session) {
        // Recuperar el paciente de la sesión
        Paciente paciente = (Paciente) session.getAttribute("paciente");
        
        // Verificar si el paciente existe en la sesión
        if (paciente != null) {
            // Retornar la información del paciente
            return ResponseEntity.ok(paciente);
        } else {
            // Retornar un estado 404 si no se encuentra el paciente
            return ResponseEntity.status(404).build();
        }
    }
}
