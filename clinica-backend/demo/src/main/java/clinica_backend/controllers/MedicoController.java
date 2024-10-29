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

/**
 * Controlador para gestionar las acciones relacionadas con los médicos.
 */
@Controller // Permitir redirecciones y controladores
@RequestMapping("/medico")
public class MedicoController {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private CitaRepository citaRepository;

    /**
     * Registra un nuevo médico y lo guarda en la sesión.
     *
     * @param nombres            Nombres del médico.
     * @param apellidos          Apellidos del médico.
     * @param correo             Correo electrónico del médico.
     * @param contraseña         Contraseña del médico.
     * @param numeroColegiatura  Número de colegiatura del médico.
     * @param especialidad       Especialidad médica del médico.
     * @param dni                DNI del médico.
     * @param session            Objeto HttpSession para gestionar la sesión del usuario.
     * @return Redirige a la página principal del médico o al registro en caso de error.
     */
    @PostMapping(value = "/registrar", consumes = "application/x-www-form-urlencoded")
    public String registrarMedico(
            @RequestParam("nombres") String nombres,
            @RequestParam("apellidos") String apellidos,
            @RequestParam("correo") String correo,
            @RequestParam("contraseña") String contraseña,
            @RequestParam("numero_colegiatura") String numeroColegiatura,
            @RequestParam("especialidad") String especialidad,
            @RequestParam("dni") String dni,
            HttpSession session) {

        try {
            // Crear una instancia de Medico y establecer sus propiedades
            Medico medico = new Medico();
            medico.setNombres(nombres);
            medico.setApellidos(apellidos);
            medico.setCorreo(correo);
            medico.setContraseña(contraseña);
            medico.setNumeroColegiatura(numeroColegiatura);
            medico.setEspecialidad(especialidad);
            medico.setDni(dni);

            // Guardar en la base de datos
            medicoRepository.save(medico);

            // Guardar en la sesión del usuario
            session.setAttribute("medico", medico);

            // Redirigir a la página principal del médico
            return "redirect:/paginaprincipalMedico.html";
        } catch (Exception e) {
            // Redirigir al formulario de registro con un mensaje de error
            return "redirect:/registroMedico.html?error=true";
        }
    }

    /**
     * Obtiene los datos del médico desde la sesión del usuario.
     *
     * @param session Objeto HttpSession para acceder a la sesión del usuario.
     * @return ResponseEntity con los datos del médico o un mensaje de error si no se encuentra.
     */
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

    /**
     * Obtiene las citas asignadas a un médico específico.
     *
     * @param medicoId ID del médico cuyas citas se desean obtener.
     * @return Lista de citas del médico.
     */
    @GetMapping("/citas/{medicoId}")
    public ResponseEntity<List<Cita>> obtenerCitasPorMedico(@PathVariable Long medicoId) {
        List<Cita> citas = citaRepository.findByMedico_Id(medicoId);
        return ResponseEntity.ok(citas);
    }

    /**
     * Obtiene los datos del médico desde la sesión del usuario.
     *
     * @param session Objeto HttpSession para acceder a la sesión del usuario.
     * @return ResponseEntity con los datos del médico o un mensaje de error si no se encuentra.
     */
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

    /**
     * Método auxiliar para convertir una cadena de texto en una fecha.
     *
     * @param fecha Cadena de texto que representa la fecha.
     * @return Objeto Date correspondiente a la fecha o null si el formato es incorrecto.
     */
    private Date convertirFecha(String fecha) {
        try {
            return (fecha != null && !fecha.isEmpty()) ? Date.valueOf(fecha) : null;
        } catch (IllegalArgumentException e) {
            System.err.println("Formato de fecha incorrecto: " + fecha);
            return null;
        }
    }

    /**
     * Obtiene la información del médico en sesión desde el objeto HttpSession.
     *
     * @param session Objeto HttpSession para acceder a la sesión del usuario.
     * @return Optional con el objeto Medico si se encuentra en la sesión.
     */
    @RequestMapping("/sesion")
    public Optional<Medico> obtenerMedicoSesion(HttpSession session) {
        return Optional.ofNullable((Medico) session.getAttribute("medico"));
    }
}
