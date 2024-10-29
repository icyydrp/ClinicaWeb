package clinica_backend.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import clinica_backend.models.Chat;
import clinica_backend.models.Cita;
import clinica_backend.models.Medico;
import clinica_backend.models.Notificacion;
import clinica_backend.models.Paciente;
import clinica_backend.models.Receta;
import clinica_backend.repositories.ChatRepository;
import clinica_backend.repositories.CitaRepository;
import clinica_backend.repositories.MedicoRepository;
import clinica_backend.repositories.NotificacionRepository;
import clinica_backend.repositories.PacienteRepository;
import clinica_backend.repositories.RecetaRepository;
import jakarta.servlet.http.HttpSession;

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
        
        @Autowired
        private RecetaRepository recetaRepository;
        
    @Autowired
    @GetMapping("/medicos")
    public List<Medico> obtenerTodosLosMedicos() {
        return medicoRepository.findAll();
    }
    @GetMapping("/medicos/especialidad")
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
            @RequestParam("pacienteId") Long pacienteId) {

        // Verificar si el pacienteId recibido es correcto
        System.out.println("ID del Paciente recibido: " + pacienteId);

        Optional<Paciente> pacienteOpt = pacienteRepository.findById(pacienteId);
        if (pacienteOpt.isEmpty()) {
            System.err.println("Paciente no encontrado con ID: " + pacienteId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Paciente no encontrado");
        }

        Optional<Medico> medicoOpt = medicoRepository.findById(Long.valueOf(medicoId));
        if (medicoOpt.isEmpty()) {
            System.err.println("Médico no encontrado con ID: " + medicoId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Médico no encontrado");
        }

        Paciente paciente = pacienteOpt.get();  // Verifica el paciente correcto
        Medico medico = medicoOpt.get();

        // Crear la nueva cita
        Cita nuevaCita = new Cita();
        nuevaCita.setFecha(java.sql.Date.valueOf(fecha));
        nuevaCita.setHora(java.sql.Time.valueOf(hora + ":00"));
        nuevaCita.setEspecialidad(especialidad);
        nuevaCita.setMotivo(motivo);
        nuevaCita.setEstado("Pendiente");
        nuevaCita.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        nuevaCita.setPaciente(paciente);  // Asociar correctamente el paciente
        nuevaCita.setMedico(medico);      // Asociar el médico

        citaRepository.save(nuevaCita);  // Guardar la cita

        System.out.println("Cita agendada para el paciente con ID: " + pacienteId);

        return ResponseEntity.ok("Cita agendada con éxito");
    }



    @GetMapping("/citas/paciente/{pacienteId}")
    public ResponseEntity<?> obtenerCitasPorPaciente(@PathVariable Long pacienteId) {
        try {
            List<Cita> citas = citaRepository.findByPaciente_Id(pacienteId);
            System.out.println("Número de citas encontradas: " + citas.size());

            List<Map<String, Object>> citasConComentario = new ArrayList<>();
            for (Cita cita : citas) {
                Map<String, Object> citaInfo = new HashMap<>();
                citaInfo.put("id", cita.getId());
                citaInfo.put("fecha", cita.getFecha());
                citaInfo.put("hora", cita.getHora());
                citaInfo.put("especialidad", cita.getEspecialidad());
                citaInfo.put("motivo", cita.getMotivo());
                citaInfo.put("estado", cita.getEstado());
                citaInfo.put("comentario", cita.getComentario());

                if (cita.getMedico() != null) {
                    Map<String, Object> medicoInfo = new HashMap<>();
                    medicoInfo.put("nombres", cita.getMedico().getNombres());
                    medicoInfo.put("apellidos", cita.getMedico().getApellidos());
                    citaInfo.put("medico", medicoInfo);
                }

                citasConComentario.add(citaInfo);
            }
            return ResponseEntity.ok(citasConComentario);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener citas.");
        }
    }



        @PutMapping("/citas/{citaId}/modificar")
        public ResponseEntity<String> modificarCita(@PathVariable Long citaId, @RequestBody Cita nuevaCita) {
            Optional<Cita> citaOpt = citaRepository.findById(citaId);
            if (citaOpt.isPresent()) {
                Cita cita = citaOpt.get();
                if ("Pendiente".equals(cita.getEstado())) {
                    boolean sinCambios = cita.getMotivo().equals(nuevaCita.getMotivo())
                            && cita.getFecha().equals(nuevaCita.getFecha())
                            && cita.getHora().equals(nuevaCita.getHora());

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
            @RequestParam Long pacienteId,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {

        try {
            Date inicio = Date.valueOf(fechaInicio);
            Date fin = Date.valueOf(fechaFin);
            return citaRepository.findByPaciente_IdAndFechaBetween(pacienteId, inicio, fin);
        } catch (IllegalArgumentException e) {
            System.err.println("Formato de fecha incorrecto: " + fechaInicio + " o " + fechaFin);
            return Collections.emptyList(); // Devuelve una lista vacía en caso de error de formato
        }
    }


        @GetMapping("/citas/historial/pdf")
    public ResponseEntity<byte[]> descargarHistorialPDF(
            @RequestParam("pacienteId") Long pacienteId) throws IOException, DocumentException {

        List<Cita> citas = citaRepository.findByPaciente_Id(pacienteId);

        if (citas.isEmpty()) {
            System.out.println("No hay citas para este paciente.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        document.add(new Paragraph("Historial de Citas"));
        document.add(new Paragraph("\n"));

        for (Cita cita : citas) {
            document.add(new Paragraph("Fecha: " + cita.getFecha() + " Hora: " + cita.getHora()));
            document.add(new Paragraph("Especialidad: " + cita.getEspecialidad()));
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
    public ResponseEntity<List<Map<String, Object>>> obtenerCitasPorMedico(
            @PathVariable Long medicoId,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String estado) {

        try {
            List<Cita> citas;

            if (fechaInicio != null && fechaFin != null && estado != null) {
                citas = citaRepository.findByMedico_IdAndFechaBetweenAndEstado(
                        medicoId, Date.valueOf(fechaInicio), Date.valueOf(fechaFin), estado);
            } else if (estado != null) {
                citas = citaRepository.findByMedico_IdAndEstado(medicoId, estado);
            } else if (fechaInicio != null && fechaFin != null) {
                citas = citaRepository.findByMedico_IdAndFechaBetween(
                        medicoId, Date.valueOf(fechaInicio), Date.valueOf(fechaFin));
            } else {
                citas = citaRepository.findByMedico_Id(medicoId);
            }

            List<Map<String, Object>> citasMapeadas = new ArrayList<>();
            for (Cita cita : citas) {
                Map<String, Object> citaInfo = new HashMap<>();
                citaInfo.put("id", cita.getId());
                citaInfo.put("fecha", cita.getFecha().toString());
                citaInfo.put("hora", cita.getHora());
                citaInfo.put("estado", cita.getEstado());
                citaInfo.put("motivo", cita.getMotivo());

                if (cita.getPaciente() != null) {
                    Map<String, Object> pacienteInfo = new HashMap<>();
                    pacienteInfo.put("id", cita.getPaciente().getId());
                    pacienteInfo.put("nombres", cita.getPaciente().getNombres());
                    pacienteInfo.put("apellidos", cita.getPaciente().getApellidos());
                    citaInfo.put("paciente", pacienteInfo);
                }

                citasMapeadas.add(citaInfo);
            }

            return ResponseEntity.ok(citasMapeadas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Manejo de error en las fechas
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Otros errores
        }
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
        public ResponseEntity<Cita> comentarCita(
                @PathVariable Long citaId,
                @RequestBody Map<String, String> comentario) {

            Optional<Cita> citaOpt = citaRepository.findById(citaId);
            if (citaOpt.isPresent()) {
                Cita cita = citaOpt.get();
                cita.setComentario(comentario.get("comentario"));
                citaRepository.save(cita);
                return ResponseEntity.ok(cita); // Devolver la cita actualizada
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        @PostMapping("/notificaciones/crear")
        public ResponseEntity<String> crearNotificacion(
                @RequestParam String mensaje,
                @RequestParam Long pacienteId, // Cambiado a Integer
                @RequestParam Long medicoId) {  // Cambiado a Integer

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
    public ResponseEntity<Map<String, Long>> obtenerMedicoSesion(HttpSession session) {
        Medico medico = (Medico) session.getAttribute("medico");  // Recuperar el objeto médico de la sesión
        if (medico != null) {
            Map<String, Long> response = new HashMap<>();
            response.put("medicoId", medico.getId());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // Si no hay sesión
    }




    // Obtener la sesión del paciente
    @GetMapping("/sesion/paciente")
    public ResponseEntity<Map<String, Long>> obtenerPacienteSesion(HttpSession session) {
        Paciente paciente = (Paciente) session.getAttribute("paciente");

        if (paciente != null) {
            Map<String, Long> response = new HashMap<>();
            response.put("pacienteId", paciente.getId());
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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

        // Obtener los médicos de un paciente
        @GetMapping("/medicos/paciente/{pacienteId}")
        public List<Medico> obtenerMedicosPorPaciente(@PathVariable Long pacienteId) {
            List<Cita> citas = citaRepository.findByPaciente_Id(pacienteId);
            return citas.stream()
                        .map(Cita::getMedico)
                        .distinct()
                        .collect(Collectors.toList());
        }

        // Obtener las citas de un médico y paciente
        @GetMapping("/citas/medico/{medicoId}/paciente/{pacienteId}")
        public List<Cita> obtenerCitasPorMedicoYPaciente(
            @PathVariable Long medicoId, @PathVariable Long pacienteId) {
            return citaRepository.findByMedicoAndPaciente(medicoId, pacienteId);
        }

    
        // Obtener las citas de un paciente desde su sesión
        @GetMapping("/citas/paciente")
    public ResponseEntity<List<Map<String, Object>>> obtenerCitasPorPaciente(HttpSession session) {
        Long pacienteId = (Long) session.getAttribute("pacienteId");

        if (pacienteId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<Cita> citas = citaRepository.findByPaciente_Id(pacienteId);
        List<Map<String, Object>> citasConComentario = new ArrayList<>();
        for (Cita cita : citas) {
            Map<String, Object> citaInfo = new HashMap<>();
            citaInfo.put("id", cita.getId());
            citaInfo.put("fecha", cita.getFecha());
            citaInfo.put("hora", cita.getHora());
            citaInfo.put("especialidad", cita.getEspecialidad());
            citaInfo.put("motivo", cita.getMotivo());
            citaInfo.put("estado", cita.getEstado());
            citaInfo.put("comentario", cita.getComentario());
            citasConComentario.add(citaInfo);
        }
        return ResponseEntity.ok(citasConComentario);
    }

    @GetMapping("/medico/{medicoId}/notificaciones")
    public List<Map<String, Object>> obtenerNotificaciones(@PathVariable Long medicoId) {
        List<Cita> citasAceptadas = citaRepository.findByMedico_IdAndEstado(medicoId, "Aceptada");
        List<Cita> citasPendientes = citaRepository.findByMedico_IdAndEstado(medicoId, "Pendiente");

        List<Map<String, Object>> notificaciones = new ArrayList<>();

        // Crear notificaciones para citas aceptadas
        for (Cita cita : citasAceptadas) {
            Map<String, Object> notificacion = new HashMap<>();
            notificacion.put("paciente", Map.of(
                "nombres", cita.getPaciente().getNombres(),
                "apellidos", cita.getPaciente().getApellidos()
            ));
            notificacion.put("estado", "Aceptada");
            notificacion.put("mensaje", "Tienes una cita con el paciente " + cita.getPaciente().getNombres());
            notificaciones.add(notificacion);
        }

        // Crear notificaciones para citas pendientes
        for (Cita cita : citasPendientes) {
            Map<String, Object> notificacion = new HashMap<>();
            notificacion.put("paciente", Map.of(
                "nombres", cita.getPaciente().getNombres(),
                "apellidos", cita.getPaciente().getApellidos()
            ));
            notificacion.put("estado", "Pendiente");
            notificacion.put("mensaje", "Tienes una cita pendiente del paciente " + cita.getPaciente().getNombres());
            notificaciones.add(notificacion);
        }

        return notificaciones;
    }


    @GetMapping("/paciente/{pacienteId}/notificaciones")
    public ResponseEntity<?> listarNotificaciones(@PathVariable Long pacienteId) {
        try {
            boolean existePaciente = pacienteRepository.existsById(pacienteId);
            if (!existePaciente) {
                System.err.println("Paciente no encontrado: " + pacienteId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Paciente no encontrado"));
            }

            List<Cita> citas = citaRepository.findCitasConPacientePorEstado(pacienteId);
            List<Map<String, Object>> notificaciones = new ArrayList<>();

            citas.forEach(cita -> 
                notificaciones.add(crearNotificacion(cita.getEstado(), cita))
            );

            return ResponseEntity.ok(notificaciones);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    // Método para crear notificaciones
    private Map<String, Object> crearNotificacion(String estado, Cita cita) {
        Map<String, Object> notificacion = new HashMap<>();
        notificacion.put("estado", estado);

        String medicoNombre = (cita.getMedico() != null) ? 
            cita.getMedico().getNombres() + " " + cita.getMedico().getApellidos() :
            "Médico no asignado";

        notificacion.put("mensaje", "Tienes una cita con el médico " + medicoNombre);
        notificacion.put("fecha", cita.getFecha().toString());
        return notificacion;
    }


    // Agregar recordatorios para citas aceptadas
    private void agregarRecordatorios(List<Cita> citas, List<Map<String, Object>> notificaciones) {
        LocalDate hoy = LocalDate.now();

        for (Cita cita : citas) {
            if (!"Aceptada".equalsIgnoreCase(cita.getEstado())) {
                continue;  // Solo procesar citas aceptadas
            }

            LocalDate fechaCita = convertirFecha(cita.getFecha());
            long diasRestantes = ChronoUnit.DAYS.between(hoy, fechaCita);

            if (diasRestantes == 1 || diasRestantes == 2) {
                Map<String, Object> recordatorio = new HashMap<>();
                recordatorio.put("estado", "Recordatorio");
                recordatorio.put("mensaje", "Te falta " + diasRestantes + " día(s) para tu cita.");
                recordatorio.put("fecha", cita.getFecha().toString());

                notificaciones.add(recordatorio);
            }
        }
    }

    // Convertir Date a LocalDate
    private LocalDate convertirFecha(Date fecha) {
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }



    //receta

    @PostMapping("/medico/{medicoId}/recetas/enviar")
    public ResponseEntity<?> enviarReceta(
            @PathVariable Long medicoId,
            @RequestBody Receta receta) {
        try {
            // Depuración: imprimir receta recibida
            System.out.println("Receta recibida: " + receta);

            Optional<Cita> citaOpt = citaRepository.findById(receta.getCita().getId());
            if (citaOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Cita no encontrada."));
            }

            Cita cita = citaOpt.get();
            if (!cita.getEstado().equalsIgnoreCase("Aceptada")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "La cita no está en estado aceptado."));
            }

            if (!cita.getMedico().getId().equals(medicoId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El médico no coincide con la cita."));
            }

            receta.setCita(cita);
            recetaRepository.save(receta);  // Guardar receta

            // Crear y guardar notificación
            Notificacion notificacion = new Notificacion();
            notificacion.setMensaje("Se ha emitido una nueva receta para la cita.");
            notificacion.setPaciente(cita.getPaciente());
            notificacion.setMedico(cita.getMedico());
            notificacion.setLeido(false);
            notificacion.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            notificacionRepository.save(notificacion);

            System.out.println("Receta enviada con éxito.");
            return ResponseEntity.ok(Map.of("mensaje", "Receta enviada con éxito."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error interno en el servidor."));
        }
    }


    @GetMapping("/paciente/{pacienteId}/recetas")
    public ResponseEntity<List<Map<String, Object>>> obtenerRecetasPorPaciente(@PathVariable Long pacienteId) {
        System.out.println("Buscando recetas para paciente con ID: " + pacienteId); // Log para verificar

        List<Receta> recetas = recetaRepository.findByCita_Paciente_Id(pacienteId);

        if (recetas.isEmpty()) {
            System.out.println("No se encontraron recetas para el paciente con ID: " + pacienteId); // Log para verificar
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }

        List<Map<String, Object>> recetasInfo = recetas.stream().map(receta -> {
            Map<String, Object> recetaInfo = new HashMap<>();

            recetaInfo.put("id", receta.getId());
            recetaInfo.put("medicamentos", receta.getMedicamentos());
            recetaInfo.put("dosis", receta.getDosis());
            recetaInfo.put("frecuencia", receta.getFrecuencia());
            recetaInfo.put("instrucciones", receta.getInstrucciones());
            recetaInfo.put("notaAdicional", receta.getNotaAdicional());
            recetaInfo.put("fechaEmision", receta.getFechaEmision());

            Cita cita = receta.getCita();
            recetaInfo.put("medicoNombre", cita.getMedico().getNombres());
            recetaInfo.put("medicoCorreo", cita.getMedico().getCorreo());

            Paciente paciente = cita.getPaciente();
            recetaInfo.put("pacienteNombre", paciente.getNombres() + " " + paciente.getApellidos());
            recetaInfo.put("pacienteCorreo", paciente.getCorreo());
            recetaInfo.put("pacienteCelular", paciente.getCelular());

            return recetaInfo;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(recetasInfo);
    }

        @GetMapping("/pacientes/con-citas-aceptadas")
    public List<Map<String, Object>> obtenerPacientesConCitasAceptadas() {
        List<Cita> citasAceptadas = citaRepository.findByEstado("Aceptada");

        return citasAceptadas.stream()
            .map(cita -> {
                Paciente paciente = cita.getPaciente();
                Map<String, Object> pacienteInfo = new HashMap<>();
                pacienteInfo.put("id", paciente.getId());
                pacienteInfo.put("nombres", paciente.getNombres());
                pacienteInfo.put("apellidos", paciente.getApellidos());
                pacienteInfo.put("correo", paciente.getCorreo());
                pacienteInfo.put("celular", paciente.getCelular());
                return pacienteInfo;
            }).collect(Collectors.toList());
    }

        

    }