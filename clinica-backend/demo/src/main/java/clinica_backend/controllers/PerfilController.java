package clinica_backend.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import clinica_backend.models.Medico;
import clinica_backend.models.Paciente;
import clinica_backend.repositories.MedicoRepository;
import clinica_backend.repositories.PacienteRepository;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/perfil")  // Ruta base para evitar conflictos
public class PerfilController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    private static final String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads/";

    // Obtener los datos del paciente
     @GetMapping("/paciente")
public ResponseEntity<Paciente> obtenerDatosPaciente(HttpSession session) {
    Paciente paciente = (Paciente) session.getAttribute("paciente");
    if (paciente == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
    return ResponseEntity.ok(paciente);
}

 @GetMapping("/medico")
public ResponseEntity<Medico> obtenerDatosMedico(HttpSession session) {
    Medico medico = (Medico) session.getAttribute("medico");

    if (medico == null) {
        System.out.println("Error: Sesión del médico no encontrada o expirada.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    System.out.println("Médico encontrado: " + medico.getNombres());
    return ResponseEntity.ok(medico);
}


@PostMapping("/paciente/subirFoto")
public ResponseEntity<String> subirFotoPaciente(
        @RequestParam("foto") MultipartFile foto, HttpSession session) {
    try {
        Paciente paciente = (Paciente) session.getAttribute("paciente");

        if (paciente == null) {
            System.out.println("Sesión de paciente no encontrada.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sesión no válida");
        }

        if (foto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No se seleccionó ninguna foto");
        }

        String nombreFoto = "foto-" + UUID.randomUUID() + getExtension(foto.getOriginalFilename());
        Path rutaArchivo = Paths.get(UPLOAD_DIRECTORY + nombreFoto);

        // Crear los directorios si no existen
        Files.createDirectories(rutaArchivo.getParent());
        foto.transferTo(rutaArchivo.toFile());

        // Guardar la ruta en la base de datos
        paciente.setFoto("/uploads/" + nombreFoto);
        pacienteRepository.save(paciente);

        return ResponseEntity.ok("Foto subida exitosamente");
    } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir la foto");
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado");
    }
}


@PostMapping("/medico/subirFoto")
public ResponseEntity<Map<String, String>> subirFotoMedico(
        @RequestParam("foto") MultipartFile foto, HttpSession session) throws IOException {
    Medico medico = (Medico) session.getAttribute("medico");
    if (medico != null && !foto.isEmpty()) {
        String nombreFoto = "foto-" + UUID.randomUUID() + getExtension(foto.getOriginalFilename());
        Path rutaArchivo = Paths.get(UPLOAD_DIRECTORY + nombreFoto);

        Files.createDirectories(rutaArchivo.getParent());
        foto.transferTo(rutaArchivo.toFile());

        String rutaFoto = "/uploads/" + nombreFoto;
        medico.setFoto(rutaFoto);
        medicoRepository.save(medico);

        return ResponseEntity.ok(Map.of("foto", rutaFoto));
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Error al subir la foto."));
}


    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }

@PostMapping("/paciente/actualizar")
public ResponseEntity<String> actualizarDatosPaciente(
        @RequestBody Paciente nuevosDatos, HttpSession session) {
    Paciente paciente = (Paciente) session.getAttribute("paciente");

    if (paciente == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sesión no válida");
    }

    paciente.setNombres(nuevosDatos.getNombres());
    paciente.setApellidos(nuevosDatos.getApellidos());
    paciente.setCorreo(nuevosDatos.getCorreo());
    paciente.setDni(nuevosDatos.getDni());
    paciente.setCelular(nuevosDatos.getCelular());

    pacienteRepository.save(paciente);
    return ResponseEntity.ok("Datos actualizados con éxito");
}


@PostMapping("/paciente/cambiarPassword")
public ResponseEntity<Map<String, String>> cambiarPassword(
        @RequestBody Map<String, String> passwords, HttpSession session) {

    Paciente paciente = (Paciente) session.getAttribute("paciente");
    if (paciente == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Sesión no válida."));
    }

    String currentPassword = passwords.get("current_password");
    String newPassword = passwords.get("new_password");

    if (!paciente.getContraseña().equals(currentPassword)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Contraseña actual incorrecta."));
    }

    paciente.setContraseña(newPassword);
    pacienteRepository.save(paciente);

    return ResponseEntity.ok(Map.of("message", "Contraseña cambiada exitosamente."));
}

  // Actualizar datos del médico
    @PostMapping("/medico/actualizar")
    public ResponseEntity<String> actualizarDatosMedico(
            @RequestBody Medico nuevosDatos, HttpSession session) {
        Medico medico = (Medico) session.getAttribute("medico");
        if (medico == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sesión no válida.");
        }

        medico.setNombres(nuevosDatos.getNombres());
        medico.setApellidos(nuevosDatos.getApellidos());
        medico.setCorreo(nuevosDatos.getCorreo());
        medico.setDni(nuevosDatos.getDni());
        medico.setEspecialidad(nuevosDatos.getEspecialidad());
        medico.setNumeroColegiatura(nuevosDatos.getNumeroColegiatura());

        medicoRepository.save(medico);
        return ResponseEntity.ok("Datos actualizados con éxito.");
    }

      // Cambiar contraseña del médico
    @PostMapping("/medico/cambiarPassword")
    public ResponseEntity<String> cambiarPasswordMedico(
            @RequestBody Map<String, String> passwords, HttpSession session) {
        Medico medico = (Medico) session.getAttribute("medico");
        if (medico == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sesión no válida.");
        }

        String currentPassword = passwords.get("current_password");
        String newPassword = passwords.get("new_password");

        if (!medico.getContraseña().equals(currentPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Contraseña actual incorrecta.");
        }

        medico.setContraseña(newPassword);
        medicoRepository.save(medico);

        return ResponseEntity.ok("Contraseña cambiada exitosamente.");
    }
}

