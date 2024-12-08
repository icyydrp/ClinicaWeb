package clinica_backend.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import clinica_backend.models.Paciente;
import clinica_backend.repositories.PacienteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteRepository pacienteRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Listar todos los pacientes o buscar por nombre, correo o DNI.
     *
     * @param search Consulta de búsqueda opcional.
     * @return Lista de pacientes que coinciden con los criterios.
     */
    @GetMapping
    @ResponseBody
    public List<Paciente> obtenerPacientes(@RequestParam(required = false) String search) {
        if (search == null || search.isEmpty()) {
            return pacienteRepository.findAll();
        }
        return pacienteRepository.findByNombresContainingIgnoreCaseOrCorreoContainingIgnoreCaseOrDniContaining(
                search, search, search);
    }

    /**
     * Registro de paciente desde el formulario del paciente.
     *
     * @param nombres   Nombres del paciente.
     * @param apellidos Apellidos del paciente.
     * @param correo    Correo electrónico del paciente.
     * @param contraseña Contraseña del paciente.
     * @param celular   Número de celular del paciente.
     * @param dni       DNI del paciente.
     * @param session   Objeto HttpSession para gestionar la sesión del usuario.
     * @return Redirección a la página principal del paciente en caso de éxito.
     */
    @PostMapping(value = "/registrar", consumes = "application/x-www-form-urlencoded")
    public String registrarPacienteConFormulario(@RequestParam("nombres") String nombres,
                                                 @RequestParam("apellidos") String apellidos,
                                                 @RequestParam("correo") String correo,
                                                 @RequestParam("contraseña") String contraseña,
                                                 @RequestParam("celular") String celular,
                                                 @RequestParam("dni") String dni,
                                                 HttpSession session) {
        try {
            if (pacienteRepository.findByCorreo(correo).isPresent()) {
                return "redirect:/registroPaciente.html?error=correo";
            }
            if (pacienteRepository.findByDni(dni).isPresent()) {
                return "redirect:/registroPaciente.html?error=dni";
            }

            Paciente paciente = new Paciente();
            paciente.setNombres(nombres);
            paciente.setApellidos(apellidos);
            paciente.setCorreo(correo);
            
            String contraseñaEncriptada = passwordEncoder.encode(contraseña);
            paciente.setContraseña(contraseñaEncriptada);
            
            paciente.setCelular(celular);
            paciente.setDni(dni);

            pacienteRepository.save(paciente);
            session.setAttribute("paciente", paciente);
            return "redirect:/paginaprincipalPaciente.html";
        } catch (Exception e) {
            return "redirect:/registroPaciente.html?error=servidor";
        }
    }

    /**
     * Registro de paciente desde el administrador.
     *
     * @param paciente Datos del paciente en formato JSON.
     * @return Respuesta indicando el éxito o error del registro.
     */
    @PostMapping("/admin/registrar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registrarPacientePorAdministrador(@RequestBody Paciente paciente) {
        try {
            // Validar si el correo ya está registrado
            if (pacienteRepository.findByCorreo(paciente.getCorreo()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("success", false, "message", "El correo ya está registrado."));
            }
    
            // Validar si el DNI ya está registrado
            if (pacienteRepository.findByDni(paciente.getDni()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("success", false, "message", "El DNI ya está registrado."));
            }
    
            // Guardar el nuevo paciente
            Paciente nuevoPaciente = pacienteRepository.save(paciente);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("success", true, "message", "Paciente registrado exitosamente.", "paciente", nuevoPaciente));
        } catch (Exception e) {
            // Manejo de errores inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Error interno del servidor."));
        }
    }
    

    /**
     * Actualizar datos de un paciente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Paciente> actualizarPaciente(@PathVariable Long id, @RequestBody Paciente pacienteActualizado) {
        return pacienteRepository.findById(id)
                .map(paciente -> {
                    paciente.setNombres(pacienteActualizado.getNombres());
                    paciente.setApellidos(pacienteActualizado.getApellidos());
                    paciente.setCorreo(pacienteActualizado.getCorreo());
                    paciente.setDni(pacienteActualizado.getDni());
                    paciente.setCelular(pacienteActualizado.getCelular());
                    return ResponseEntity.ok(pacienteRepository.save(paciente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Eliminar un paciente por ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {
        if (pacienteRepository.existsById(id)) {
            pacienteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Obtener los datos del paciente autenticado en sesión.
     */
    @GetMapping("/sesion")
    public ResponseEntity<Paciente> obtenerPacienteSesion(HttpSession session) {
        Paciente paciente = (Paciente) session.getAttribute("paciente");
        if (paciente != null) {
            return ResponseEntity.ok(paciente);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> obtenerPacientePorId(@PathVariable Long id) {
        return pacienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
