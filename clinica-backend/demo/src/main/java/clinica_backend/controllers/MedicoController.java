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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import clinica_backend.models.Cita;
import clinica_backend.models.Medico;
import clinica_backend.repositories.CitaRepository;
import clinica_backend.repositories.MedicoRepository;
import jakarta.servlet.http.HttpSession;

@Controller // Permitir redirecciones y controladores
@RequestMapping("/medico")
public class MedicoController {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private CitaRepository citaRepository;

    @PostMapping("/medico/registrar")
    public String registrarMedico(
            @RequestParam("nombres") String nombres,
            @RequestParam("apellidos") String apellidos,
            @RequestParam("correo") String correo,
            @RequestParam("contraseña") String contraseña,
            @RequestParam("numero_colegiatura") String numeroColegiatura,
            @RequestParam("especialidad") String especialidad,
            @RequestParam("dni") String dni) {
        try {
            Medico medico = new Medico();
            medico.setNombres(nombres);
            medico.setApellidos(apellidos);
            medico.setCorreo(correo);
            medico.setContraseña(contraseña);
            medico.setNumeroColegiatura(numeroColegiatura);
            medico.setEspecialidad(especialidad);
            medico.setDni(dni);

            medicoRepository.save(medico);
            return "redirect:/paginaprincipalMedico.html";
        } catch (Exception e) {
            return "redirect:/registroMedico.html?error=true";
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


    @GetMapping("/citas/{medicoId}")
    public ResponseEntity<List<Cita>> obtenerCitasPorMedico(@PathVariable Long medicoId) {
        List<Cita> citas = citaRepository.findByMedico_Id(medicoId);
        return ResponseEntity.ok(citas);
    }

     @GetMapping("/sesion/medico")
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

 // Método auxiliar para convertir la fecha
    private Date convertirFecha(String fecha) {
        try {
            return (fecha != null && !fecha.isEmpty()) ? Date.valueOf(fecha) : null;
        } catch (IllegalArgumentException e) {
            System.err.println("Formato de fecha incorrecto: " + fecha);
            return null;
        }
    }

}
