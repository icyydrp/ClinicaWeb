package clinica_backend.controllers;

import clinica_backend.models.Chat;
import clinica_backend.models.Cita;
import clinica_backend.models.Medico;
import clinica_backend.models.Notificacion;
import clinica_backend.models.Paciente;
import clinica_backend.repositories.CitaRepository;
import clinica_backend.repositories.MedicoRepository;
import clinica_backend.repositories.NotificacionRepository;
import clinica_backend.repositories.PacienteRepository;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.Map;

import clinica_backend.repositories.ChatRepository;




@RestController
@RequestMapping("/api")
public class CitaController {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;
    @Autowired
private NotificacionRepository notificacionRepository;

@Autowired
private ChatRepository chatRepository;


    @GetMapping("/medicos")
    public List<Medico> obtenerMedicosPorEspecialidad(@RequestParam String especialidad) {
        return medicoRepository.findByEspecialidad(especialidad);
    }

    @PostMapping("/citas/agendar")
    public ResponseEntity<String> agendarCita(
            @RequestParam("fecha") String fecha,
            @RequestParam("hora") String hora,
            @RequestParam("especialidad") String especialidad,
            @RequestParam("medico_id") Integer medicoId,
            @RequestParam("motivo") String motivo,
            @RequestParam("pacienteId") Integer pacienteId) {
    
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(pacienteId);
        if (pacienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Paciente no encontrado");
        }
    
        Optional<Medico> medicoOpt = medicoRepository.findById(medicoId);
        if (medicoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Médico no encontrado");
        }
    
        Cita nuevaCita = new Cita();
        nuevaCita.setFecha(java.sql.Date.valueOf(fecha));
        nuevaCita.setHora(java.sql.Time.valueOf(hora + ":00"));
        nuevaCita.setEspecialidad(especialidad);
        nuevaCita.setMotivo(motivo);
        nuevaCita.setEstado("Pendiente");
        nuevaCita.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        nuevaCita.setPaciente(pacienteOpt.get());
        nuevaCita.setMedico(medicoOpt.get());
    
        citaRepository.save(nuevaCita);
        return ResponseEntity.ok("Cita agendada con éxito");
    }
    
    @GetMapping("/citas/paciente/{pacienteId}")
public List<Map<String, Object>> obtenerCitasPorPaciente(@PathVariable Integer pacienteId) {
    List<Cita> citas = citaRepository.findByPaciente_Id(pacienteId);

    // Transformamos las citas para incluir toda la información relevante.
    List<Map<String, Object>> citasConComentario = new ArrayList<>();
    for (Cita cita : citas) {
        Map<String, Object> citaInfo = new HashMap<>();
        citaInfo.put("id", cita.getId());
        citaInfo.put("fecha", cita.getFecha());
        citaInfo.put("hora", cita.getHora());
        citaInfo.put("especialidad", cita.getEspecialidad());
        citaInfo.put("motivo", cita.getMotivo());
        citaInfo.put("estado", cita.getEstado());
        citaInfo.put("comentario", cita.getComentario()); // Agregar comentario del médico

        citasConComentario.add(citaInfo);
    }
    return citasConComentario;
}





    @PutMapping("/citas/{citaId}/modificar")
    public ResponseEntity<String> modificarCita(@PathVariable Long citaId, @RequestBody Cita nuevaCita) {
        Optional<Cita> citaOpt = citaRepository.findById(citaId);
        if (citaOpt.isPresent()) {
            Cita cita = citaOpt.get();
            if ("Pendiente".equals(cita.getEstado())) {
                boolean sinCambios = cita.getMotivo().equals(nuevaCita.getMotivo()) &&
                        cita.getFecha().equals(nuevaCita.getFecha()) &&
                        cita.getHora().equals(nuevaCita.getHora());
    
                if (sinCambios) {
                    return ResponseEntity.ok("No se realizó ninguna modificación.");
                }
    
                cita.setMotivo(nuevaCita.getMotivo());
                cita.setFecha(nuevaCita.getFecha());
                cita.setHora(nuevaCita.getHora());
                citaRepository.save(cita);
                return ResponseEntity.ok("Cita modificada con éxito.");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La cita no puede ser modificada.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cita no encontrada.");
    }
    

@PutMapping("/citas/{citaId}/cancelar")
public ResponseEntity<String> cancelarCita(@PathVariable Long citaId) {
    Optional<Cita> citaOpt = citaRepository.findById(citaId);
    if (citaOpt.isPresent()) {
        Cita cita = citaOpt.get();
        if (!"Cancelado".equals(cita.getEstado())) {
            cita.setEstado("Cancelado");
            citaRepository.save(cita);
            return ResponseEntity.ok("Cita cancelada con éxito");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La cita ya está cancelada");
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cita no encontrada");
}


@GetMapping("/citas/filtrar")
public List<Cita> filtrarCitasPorFecha(
        @RequestParam Integer pacienteId,
        @RequestParam String fechaInicio,
        @RequestParam String fechaFin) {

    Date inicio = Date.valueOf(fechaInicio);
    Date fin = Date.valueOf(fechaFin);

    return citaRepository.findByPaciente_IdAndFechaBetween(pacienteId, inicio, fin);
}

    @GetMapping("/citas/historial/pdf")
    public ResponseEntity<byte[]> descargarHistorialPDF(
            @RequestParam("pacienteId") Integer pacienteId) throws IOException, DocumentException {
        List<Cita> citas = citaRepository.findByPaciente_Id(pacienteId);
    
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
    
        PdfWriter.getInstance(document, out);
        document.open();
    
        document.add(new Paragraph("Historial de Citas"));
        document.add(new Paragraph("\n"));
    
        for (Cita cita : citas) {
            document.add(new Paragraph("Fecha: " + cita.getFecha() + " Hora: " + cita.getHora()));
            document.add(new Paragraph("Especialidad: " + cita.getEspecialidad()));
            document.add(new Paragraph("Médico: " + cita.getMedico().getNombres()));
            document.add(new Paragraph("Motivo: " + cita.getMotivo()));
            document.add(new Paragraph("Estado: " + cita.getEstado()));
            document.add(new Paragraph("\n"));
        }
    
        document.close();
    
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "historial_citas.pdf");
    
        return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
    }


       // Obtener citas para un médico por su ID con filtros
       @GetMapping("/medico/citas/{medicoId}")
public List<Map<String, Object>> obtenerCitasPorMedico(
        @PathVariable Integer medicoId,
        @RequestParam(required = false) String fechaInicio,
        @RequestParam(required = false) String fechaFin,
        @RequestParam(required = false) String estado) {

    Date inicio = (fechaInicio != null && !fechaInicio.isEmpty()) ? Date.valueOf(fechaInicio) : null;
    Date fin = (fechaFin != null && !fechaFin.isEmpty()) ? Date.valueOf(fechaFin) : null;

    List<Cita> citas;

    if (inicio != null && fin != null && estado != null) {
        citas = citaRepository.findByMedico_IdAndFechaBetweenAndEstado(medicoId, inicio, fin, estado);
    } else if (estado != null) {
        citas = citaRepository.findByMedico_IdAndEstado(medicoId, estado);
    } else if (inicio != null && fin != null) {
        citas = citaRepository.findByMedico_IdAndFechaBetween(medicoId, inicio, fin);
    } else {
        citas = citaRepository.findByMedico_Id(medicoId);
    }

    // Transformar las citas para incluir los datos del paciente
    List<Map<String, Object>> citasMapeadas = new ArrayList<>();
    for (Cita cita : citas) {
        Map<String, Object> citaInfo = new HashMap<>();
        citaInfo.put("id", cita.getId());
        citaInfo.put("fecha", cita.getFecha());
        citaInfo.put("hora", cita.getHora());
        citaInfo.put("estado", cita.getEstado());
        citaInfo.put("motivo", cita.getMotivo());

        // Incluir los datos del paciente
        if (cita.getPaciente() != null) {
            Map<String, String> pacienteInfo = new HashMap<>();
            pacienteInfo.put("nombres", cita.getPaciente().getNombres());
            pacienteInfo.put("apellidos", cita.getPaciente().getApellidos());
            citaInfo.put("paciente", pacienteInfo);
        } else {
            citaInfo.put("paciente", "Paciente no disponible");
        }

        citasMapeadas.add(citaInfo);
    }

    return citasMapeadas;
}








// Aceptar una cita
@PutMapping("/citas/{citaId}/aceptar")
public ResponseEntity<String> aceptarCita(@PathVariable Long citaId) {
    Optional<Cita> citaOpt = citaRepository.findById(citaId);
    if (citaOpt.isPresent()) {
        Cita cita = citaOpt.get();
        cita.setEstado("Aceptada");
        citaRepository.save(cita);
        return ResponseEntity.ok("Cita aceptada con éxito");
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cita no encontrada");
}

// Comentar una cita
@PutMapping("/citas/{citaId}/comentar")
public ResponseEntity<String> comentarCita(@PathVariable Long citaId, @RequestBody Map<String, String> comentario) {
    Optional<Cita> citaOpt = citaRepository.findById(citaId);
    if (citaOpt.isPresent()) {
        Cita cita = citaOpt.get();
        cita.setComentario(comentario.get("comentario"));
        citaRepository.save(cita);
        return ResponseEntity.ok("Comentario agregado con éxito");
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cita no encontrada");
}


@PostMapping("/notificaciones/crear")
public ResponseEntity<String> crearNotificacion(
        @RequestParam String mensaje,
        @RequestParam Integer pacienteId,  // Cambiado a Integer
        @RequestParam Integer medicoId) {  // Cambiado a Integer

    Optional<Paciente> paciente = pacienteRepository.findById(pacienteId);
    Optional<Medico> medico = medicoRepository.findById(medicoId);

    if (paciente.isEmpty() || medico.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Paciente o Médico no encontrado.");
    }

    Notificacion notificacion = new Notificacion();
    notificacion.setMensaje(mensaje);
    notificacion.setPaciente(paciente.get());
    notificacion.setMedico(medico.get());
    notificacion.setLeido(false);
    notificacion.setCreatedAt(new Timestamp(System.currentTimeMillis()));

    notificacionRepository.save(notificacion);
    return ResponseEntity.ok("Notificación creada con éxito.");
}

@GetMapping("/sesion/medico")
public ResponseEntity<Map<String, Integer>> obtenerMedicoSesion(HttpSession session) {
    Medico medico = (Medico) session.getAttribute("medico");
    if (medico != null) {
        Map<String, Integer> response = new HashMap<>();
        response.put("medicoId", medico.getId());
        return ResponseEntity.ok(response);
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
}
@GetMapping("/sesion/paciente")
public ResponseEntity<Map<String, Integer>> obtenerPacienteSesion(HttpSession session) {
    Paciente paciente = (Paciente) session.getAttribute("paciente");
    if (paciente != null) {
        Map<String, Integer> response = new HashMap<>();
        response.put("pacienteId", paciente.getId());
        return ResponseEntity.ok(response);
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
}

// Obtener los mensajes del chat
@GetMapping("/chat/{citaId}")
public List<Chat> obtenerMensajes(@PathVariable Long citaId) {
    return chatRepository.findByCita_Id(citaId);
}

// Enviar un mensaje al chat
@PostMapping("/chat/{citaId}/enviar")
public ResponseEntity<String> enviarMensaje(
        @PathVariable Long citaId, 
        @RequestBody Map<String, String> mensaje) {

    Optional<Cita> citaOpt = citaRepository.findById(citaId);
    if (citaOpt.isPresent()) {
        Cita cita = citaOpt.get();
        Chat chat = new Chat();
        chat.setCita(cita);
        chat.setRemitente(mensaje.get("remitente"));
        chat.setMensaje(mensaje.get("mensaje"));
        chat.setFechaEnvio(new Timestamp(System.currentTimeMillis()));

        chatRepository.save(chat);
        return ResponseEntity.ok("Mensaje enviado con éxito");
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cita no encontrada");
}






@GetMapping("/medicos/paciente/{pacienteId}")
public List<Medico> obtenerMedicosPorPaciente(@PathVariable Integer pacienteId) {
    List<Cita> citas = citaRepository.findByPaciente_Id(pacienteId);
    return citas.stream()
                .map(Cita::getMedico)
                .distinct()
                .collect(Collectors.toList());
}

@GetMapping("/citas/medico/{medicoId}/paciente/{pacienteId}")
public List<Cita> obtenerCitasPorMedicoYPaciente(@PathVariable Integer medicoId, @PathVariable Integer pacienteId) {
    return citaRepository.findByMedicoAndPaciente(medicoId, pacienteId);
}


@GetMapping("/citas/{medicoId}")
public ResponseEntity<List<Cita>> obtenerCitasPorMedico(@PathVariable Integer medicoId) {
    List<Cita> citas = citaRepository.findByMedico_Id(medicoId);
    return ResponseEntity.ok(citas);
}





    
}
