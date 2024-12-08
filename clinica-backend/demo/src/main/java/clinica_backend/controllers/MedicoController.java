package clinica_backend.controllers;

import java.sql.Date;
import java.util.HashMap;
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

import clinica_backend.models.Cita;
import clinica_backend.models.Medico;
import clinica_backend.repositories.CitaRepository;
import clinica_backend.repositories.MedicoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private CitaRepository citaRepository;
    
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registro de médico desde formulario individual.
     */
    @PostMapping(value = "/registrar", consumes = "application/x-www-form-urlencoded")
    public String registrarMedicoConFormulario(
            @RequestParam("nombres") String nombres,
            @RequestParam("apellidos") String apellidos,
            @RequestParam("correo") String correo,
            @RequestParam("contraseña") String contraseña,
            @RequestParam("numero_colegiatura") String numeroColegiatura,
            @RequestParam("especialidad") String especialidad,
            @RequestParam("dni") String dni,
            HttpSession session) {
        try {
            // Verificar si el correo, número de colegiatura o DNI ya existen
            if (medicoRepository.findByCorreo(correo).isPresent()) {
                return "redirect:/registroMedico.html?error=correoExistente";
            }
            if (medicoRepository.findByNumeroColegiatura(numeroColegiatura).isPresent()) {
                return "redirect:/registroMedico.html?error=colegiaturaExistente";
            }
            if (medicoRepository.findByDni(dni).isPresent()) {
                return "redirect:/registroMedico.html?error=dniExistente";
            }
    
            // Crear y guardar al médico en la base de datos
            Medico medico = new Medico();
            medico.setNombres(nombres);
            medico.setApellidos(apellidos);
            medico.setCorreo(correo);
            
            String contraseñaEncriptada = passwordEncoder.encode(contraseña);
            medico.setContraseña(contraseñaEncriptada);

            
            medico.setNumeroColegiatura(numeroColegiatura);
            medico.setEspecialidad(especialidad);
            medico.setDni(dni);
    
            medicoRepository.save(medico);
    
            // Configurar la sesión del médico
            session.setAttribute("medico", medico);
            session.setAttribute("medicoId", medico.getId());
            session.setMaxInactiveInterval(30 * 60); // Sesión válida por 30 minutos
    
            return "redirect:/paginaprincipalMedico.html";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/registroMedico.html?error=true";
        }
    }
    

    /**
     * Lista de médicos con opción de búsqueda.
     */
    @GetMapping
    @ResponseBody
    public List<Medico> obtenerMedicos(@RequestParam(required = false) String search) {
        if (search == null || search.isEmpty()) {
            return medicoRepository.findAll();
        }
        return medicoRepository.findByNombresContainingIgnoreCaseOrCorreoContainingIgnoreCaseOrDniContainingIgnoreCase(
                search, search, search);
    }

    /**
     * Registro de médico desde el administrador.
     */
/**
 * Registro de médico desde el administrador.
 */
@PostMapping("/admin/registrar")
@ResponseBody
public ResponseEntity<Map<String, Object>> registrarMedicoDesdeAdmin(@RequestBody Medico medico) {
    try {
        // Verificar si el correo ya existe
        if (medicoRepository.findByCorreo(medico.getCorreo()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("success", false, "message", "El correo ya está registrado."));
        }

        // Verificar si el número de colegiatura ya existe
        if (medicoRepository.findByNumeroColegiatura(medico.getNumeroColegiatura()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("success", false, "message", "El número de colegiatura ya está registrado."));
        }

        // Verificar si el DNI ya existe
        if (medicoRepository.findByDni(medico.getDni()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("success", false, "message", "El DNI ya está registrado."));
        }

        // Guardar el médico en la base de datos
        medicoRepository.save(medico);

        // Respuesta exitosa
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Médico registrado exitosamente."
        ));
    } catch (Exception e) {
        e.printStackTrace();
        // Respuesta de error interno
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "success", false,
                        "message", "Error interno del servidor."
                ));
    }
}


    /**
     * Obtiene un médico por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Medico> obtenerMedico(@PathVariable Long id) {
        return medicoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Actualiza datos del médico.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Medico> actualizarMedico(@PathVariable Long id, @RequestBody Medico medicoActualizado) {
        return medicoRepository.findById(id)
                .map(medico -> {
                    medico.setNombres(medicoActualizado.getNombres());
                    medico.setApellidos(medicoActualizado.getApellidos());
                    medico.setCorreo(medicoActualizado.getCorreo());
                    medico.setNumeroColegiatura(medicoActualizado.getNumeroColegiatura());
                    medico.setEspecialidad(medicoActualizado.getEspecialidad());
                    medico.setDni(medicoActualizado.getDni());
                    return ResponseEntity.ok(medicoRepository.save(medico));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Elimina un médico por ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMedico(@PathVariable Long id) {
        if (medicoRepository.existsById(id)) {
            medicoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Citas asignadas a un médico.
     */
    @GetMapping("/citas/{medicoId}")
    public ResponseEntity<List<Cita>> obtenerCitasPorMedico(@PathVariable Long medicoId) {
        List<Cita> citas = citaRepository.findByMedico_Id(medicoId);
        return ResponseEntity.ok(citas);
    }

    /**
     * Sesión del médico.
     */
    @GetMapping("/sesion")
    public ResponseEntity<Map<String, Object>> obtenerSesionMedico(HttpSession session) {
        Long medicoId = (Long) session.getAttribute("medicoId");

        if (medicoId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Sesión no encontrada"));
        }

        Optional<Medico> medicoOpt = medicoRepository.findById(medicoId);

        if (medicoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Médico no encontrado"));
        }

        Medico medico = medicoOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("medicoId", medico.getId());
        response.put("nombres", medico.getNombres());
        response.put("apellidos", medico.getApellidos());

        return ResponseEntity.ok(response);
    }

    /**
     * Método auxiliar para convertir fechas.
     */
    private Date convertirFecha(String fecha) {
        try {
            return (fecha != null && !fecha.isEmpty()) ? Date.valueOf(fecha) : null;
        } catch (IllegalArgumentException e) {
            System.err.println("Formato de fecha incorrecto: " + fecha);
            return null;
        }
    }

    @GetMapping("/medico")
    public ResponseEntity<Map<String, Object>> obtenerDatosMedico(HttpSession session) {
        Long id = (Long) session.getAttribute("medicoId");

        if (id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Sesión no encontrada"));
        }

        Optional<Medico> medicoOpt = medicoRepository.findById(id);
        if (medicoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Médico no encontrado"));
        }

        Medico medico = medicoOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("nombres", medico.getNombres());
        response.put("apellidos", medico.getApellidos());
        response.put("correo", medico.getCorreo());
        response.put("dni", medico.getDni());
        response.put("especialidad", medico.getEspecialidad());
        response.put("numero_colegiatura", medico.getNumeroColegiatura());

        return ResponseEntity.ok(response);
    } 
}
