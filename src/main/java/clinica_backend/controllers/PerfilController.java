package clinica_backend.controllers;

import clinica_backend.models.Medico;
import clinica_backend.models.Paciente;
import clinica_backend.repositories.MedicoRepository;
import clinica_backend.repositories.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PerfilController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    // Ruta absoluta para el directorio uploads
    private static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads/";

    @GetMapping("/paciente")
public ResponseEntity<Paciente> obtenerDatosPaciente(HttpSession session) {
    Paciente paciente = (Paciente) session.getAttribute("paciente");
    if (paciente == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
    return ResponseEntity.ok(paciente);
}


    @GetMapping("/medico")
    public Medico obtenerDatosMedico(HttpSession session) {
        return (Medico) session.getAttribute("medico");
    }

    @PostMapping("/paciente/actualizar")
    public Paciente actualizarPaciente(@RequestBody Paciente paciente, HttpSession session) {
        Paciente pacienteSesion = (Paciente) session.getAttribute("paciente");
        if (pacienteSesion != null) {
            pacienteSesion.setNombres(paciente.getNombres());
            pacienteSesion.setApellidos(paciente.getApellidos());
            pacienteSesion.setCorreo(paciente.getCorreo());
            pacienteSesion.setDni(paciente.getDni());
            pacienteSesion.setCelular(paciente.getCelular());
            pacienteRepository.save(pacienteSesion);
        }
        return pacienteSesion;
    }

    @PostMapping("/medico/actualizar")
    public Medico actualizarMedico(@RequestBody Medico medico, HttpSession session) {
        Medico medicoSesion = (Medico) session.getAttribute("medico");
        if (medicoSesion != null) {
            medicoSesion.setNombres(medico.getNombres());
            medicoSesion.setApellidos(medico.getApellidos());
            medicoSesion.setCorreo(medico.getCorreo());
            medicoSesion.setDni(medico.getDni());
            medicoSesion.setEspecialidad(medico.getEspecialidad());
            medicoSesion.setNumeroColegiatura(medico.getNumeroColegiatura());  // Corregido
            medicoRepository.save(medicoSesion);
        }
        return medicoSesion;
    }
    

    @PostMapping("/paciente/cambiarPassword")
    public String cambiarPasswordPaciente(HttpSession session, @RequestParam String nuevaPassword) {
        Paciente paciente = (Paciente) session.getAttribute("paciente");
        if (paciente != null) {
            paciente.setContraseña(nuevaPassword);
            pacienteRepository.save(paciente);
            return "Contraseña cambiada correctamente";
        }
        return "Error al cambiar la contraseña";
    }

    @PostMapping("/medico/cambiarPassword")
    public String cambiarPasswordMedico(HttpSession session, @RequestParam String nuevaPassword) {
        Medico medico = (Medico) session.getAttribute("medico");
        if (medico != null) {
            medico.setContraseña(nuevaPassword);
            medicoRepository.save(medico);
            return "Contraseña cambiada correctamente";
        }
        return "Error al cambiar la contraseña";
    }

    @PostMapping("/paciente/subirFoto")
    public String subirFotoPaciente(@RequestParam("foto") MultipartFile foto, HttpSession session) throws IOException {
        Paciente paciente = (Paciente) session.getAttribute("paciente");
        if (paciente != null && !foto.isEmpty()) {
            String nombreFoto = "foto-" + UUID.randomUUID() + getExtension(foto.getOriginalFilename());
            Path rutaArchivo = Paths.get(UPLOAD_DIRECTORY + nombreFoto);

            Files.createDirectories(rutaArchivo.getParent());
            foto.transferTo(rutaArchivo.toFile());

            paciente.setFoto("/uploads/" + nombreFoto);
            pacienteRepository.save(paciente);

            return "Foto subida exitosamente";
        }
        return "Error al subir la foto";
    }

    @PostMapping("/medico/subirFoto")
    public String subirFotoMedico(@RequestParam("foto") MultipartFile foto, HttpSession session) throws IOException {
        Medico medico = (Medico) session.getAttribute("medico");
        if (medico != null && !foto.isEmpty()) {
            String nombreFoto = "foto-" + UUID.randomUUID() + getExtension(foto.getOriginalFilename());
            Path rutaArchivo = Paths.get(UPLOAD_DIRECTORY + nombreFoto);

            Files.createDirectories(rutaArchivo.getParent());
            foto.transferTo(rutaArchivo.toFile());

            medico.setFoto("/uploads/" + nombreFoto);
            medicoRepository.save(medico);

            return "Foto subida exitosamente";
        }
        return "Error al subir la foto";
    }

    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }
}
